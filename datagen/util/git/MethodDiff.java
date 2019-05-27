package util.git;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.CallableDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.comments.Comment;
import com.github.javaparser.printer.PrettyPrinterConfiguration;

/**
 * Created by Loren Klingman on 10/19/17.
 * Finds Changes Between Methods of Two Java Source Files
 */
public class MethodDiff {

    private static PrettyPrinterConfiguration ppc = null;

    class ClassPair {
        final ClassOrInterfaceDeclaration clazz;
        final String name;
        ClassPair(ClassOrInterfaceDeclaration c, String n) {
            clazz = c;
            name = n;
        }
    }

    public static PrettyPrinterConfiguration getPPC() {
        if (ppc != null) {
            return ppc;
        }
        PrettyPrinterConfiguration localPpc = new PrettyPrinterConfiguration();
        localPpc.setColumnAlignFirstMethodChain(false);
        localPpc.setColumnAlignParameters(false);
        localPpc.setEndOfLineCharacter("");
        localPpc.setIndent("");
        localPpc.setPrintComments(false);
        localPpc.setPrintJavaDoc(false);

        ppc = localPpc;
        return ppc;
    }

    public static <N extends Node> List<N> getChildNodesNotInClass(Node n, Class<N> clazz) {
        List<N> nodes = new ArrayList<>();
        for (Node child : n.getChildNodes()) {
            if (child instanceof ClassOrInterfaceDeclaration) {
                // Don't go into a nested class
                continue;
            }
            if (clazz.isInstance(child)) {
                nodes.add(clazz.cast(child));
            }
            nodes.addAll(getChildNodesNotInClass(child, clazz));
        }
        return nodes;
    }

    private List<ClassPair> getClasses(Node n, String parents, boolean inMethod) {
        List<ClassPair> pairList = new ArrayList<>();
        for (Node child : n.getChildNodes()) {
            if (child instanceof ClassOrInterfaceDeclaration) {
                ClassOrInterfaceDeclaration c = (ClassOrInterfaceDeclaration)child;
                String cName = parents+c.getNameAsString();
                if (inMethod) {
                    System.out.println(
                            "WARNING: Class "+cName+" is located inside a method. We cannot predict its name at"
                            + " compile time so it will not be diffed."
                    );
                } else {
                    pairList.add(new ClassPair(c, cName));
                    pairList.addAll(getClasses(c, cName + "$", inMethod));
                }
            } else if (child instanceof MethodDeclaration || child instanceof ConstructorDeclaration) {
                pairList.addAll(getClasses(child, parents, true));
            } else {
                pairList.addAll(getClasses(child, parents, inMethod));
            }
        }
        return pairList;
    }

    private List<ClassPair> getClasses(String file) {
    	try {
    		CompilationUnit cu = JavaParser.parse(file);
    		return getClasses(cu, "", false);
    	}catch(Exception e) {
    		return null;
    	}
    }

    public static String getSignature(String className, CallableDeclaration m) {
        return m.getSignature().asString();
    }

    public static HashSet<String> methodDiffInClass(String file1, String file2) {
        HashSet<String> changedMethods = new HashSet<>();
        HashMap<String, String> methods = new HashMap<>();

        MethodDiff md = new MethodDiff();

        // Load all the method and constructor values into a Hashmap from File1
        List<ClassPair> cList = md.getClasses(file1);
        if(cList == null)
        	return changedMethods;
        for (ClassPair c : cList) {
            List<ConstructorDeclaration> conList = getChildNodesNotInClass(c.clazz, ConstructorDeclaration.class);
            List<MethodDeclaration> mList = getChildNodesNotInClass(c.clazz, MethodDeclaration.class);
            for (MethodDeclaration m : mList) {
                String methodSignature = getSignature(c.name, m);

                if (m.getBody().isPresent()) {
                    methods.put(methodSignature, m.getBody().get().toString(getPPC()));
                } else {
                	return changedMethods;
//                    System.out.println("Warning: No Body for "+file1+" "+methodSignature);
                }
            }
            for (ConstructorDeclaration con : conList) {
                String methodSignature = getSignature(c.name, con);
                methods.put(methodSignature, con.getBody().toString(getPPC()));
            }
        }

        // Compare everything in file2 to what is in file1 and log any differences
        cList = md.getClasses(file2);
        if(cList == null)
        	return changedMethods;
        for (ClassPair c : cList) {
            List<ConstructorDeclaration> conList = getChildNodesNotInClass(c.clazz, ConstructorDeclaration.class);
            List<MethodDeclaration> mList = getChildNodesNotInClass(c.clazz, MethodDeclaration.class);
            for (MethodDeclaration m : mList) {
                String methodSignature = getSignature(c.name, m);

                if (m.getBody().isPresent()) {
                    String body1 = methods.remove(methodSignature);
                    if(body1 == null)
                    	continue;
                    String body2 = m.getBody().get().toString(getPPC());
                    if (!body1.equals(body2)) {
                        // Javassist doesn't add spaces for methods with 2+ parameters...
                        changedMethods.add(methodSignature.replace(" ", ""));
                    }
                } else {
                	return changedMethods;
//                    System.out.println("Warning: No Body for "+file2+" "+methodSignature);
                }
            }
            for (ConstructorDeclaration con : conList) {
                String methodSignature = getSignature(c.name, con);
                String body1 = methods.remove(methodSignature);
                if(body1 == null)
                	continue;
                String body2 = con.getBody().toString(getPPC());
                if (!body1.equals(body2)) {
                    // Javassist doesn't add spaces for methods with 2+ parameters...
                    changedMethods.add(methodSignature.replace(" ", ""));
                }
            }
            // Anything left in methods was only in the first set and so is "changed"
            for (String method : methods.keySet()) {
                // Javassist doesn't add spaces for methods with 2+ parameters...
                changedMethods.add(method.replace(" ", ""));
            }
        }
        return changedMethods;
    }

    private static void removeComments(Node node) {
        for (Comment child : node.getAllContainedComments()) {
            child.remove();
        }
    }
}