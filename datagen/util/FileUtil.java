package util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class FileUtil {
	
	public static String readFileContent(File file) {
		String content = "";
		String str;
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(file));
			while((str = br.readLine())!=null) {
				content = content+str+"\n";
			}
			br.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return content;
		
	}


	public static ArrayList<File> getAllFile(File dir) {
		ArrayList<File> allJavaFile = new ArrayList<File>();
		visitAllFiles(allJavaFile, dir, "");
		return allJavaFile;
	}

	public static void visitAllFiles(ArrayList<File> files, File dir, String type) {
		if (dir.isDirectory()) {
			File[] children = dir.listFiles();
			for (File f : children) {
				visitAllFiles(files, f, type);
			}
		} else {
			if(type.length() > 0 && dir.getName().endsWith(type))
				files.add(dir);
			else if(type.length() == 0)
				files.add(dir);
		}
	}
	

	public static ArrayList<File> getAllJavaFile(File dir) {
		ArrayList<File> allJavaFile = new ArrayList<File>();
		visitAllFiles(allJavaFile, dir, ".java");
		return allJavaFile;
	}


	public static ArrayList<String> getAllLinesByList(String file) {
		ArrayList<String> content = new ArrayList<String>();
		String str;
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(file));
			while((str = br.readLine())!=null) {
				content.add(str);
			}
			br.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return content;
	}

	public static HashMap<String, String> readConfig() {
		HashMap<String, String> configMap = new HashMap<String, String>();
		String str;
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(new File("./config")));
			while((str = br.readLine())!=null) {
				str = str.replace(" ", "");
				configMap.put(str.split("=")[0].toUpperCase(), str.split("=")[1]);
			}
			br.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return configMap;
		
	}
	

	public static void fileCopy(String inFileName, String outFileName) {
		try {
			FileInputStream fis = new FileInputStream(inFileName);
			FileOutputStream fos = new FileOutputStream(outFileName);

			int data = 0;
			while ((data = fis.read()) != -1) {
				fos.write(data);
			}
			fis.close();
			fos.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}

