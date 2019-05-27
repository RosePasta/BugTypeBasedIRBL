package util.git;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import locus.util.GitHelp;
import util.FileUtil;

public class BugCommitExtractor {
	public static HashMap<Date, ArrayList<String>> getCommitMap(String project, String gitPath) {
		HashMap<Date, ArrayList<String>> bugCommitMap = new HashMap<Date, ArrayList<String>>();
		
		String pattern = "(?i)(.*fix.*)|(?i)(.*bug.*)|(?i)(.*issue.*)|(?i)(.*fail.*)|(?i)(.*error.*)|(?i)(.*problem.*)|(?i)(.*crash.*)";	        
		SimpleDateFormat dt = new SimpleDateFormat("yyyy-MMM-dd",
                Locale.US);
		Pattern p = Pattern.compile(pattern);
		String[] allCommit;
		try {
			GitHelp.getCheckoutAfterCommit("master", gitPath);
			GitHelp.initGit(gitPath);
			allCommit = GitHelp.getAllCommitOneLine(gitPath).split("\n");
			for (int i = 0; i < allCommit.length; i++) {
				String[] data = allCommit[i].split("\t");
				String[] dateData = data[2].split(" ");
				String dateString = dateData[4]+"-"+dateData[1]+"-"+dateData[2];
				Date date = dt.parse(dateString);
				String hashKey = data[0];
				String message = "";
				if(data.length > 3)
					message = data[3];
				Matcher m = p.matcher(message);
				if(m.find()) {
					if(bugCommitMap.containsKey(date)) {
						ArrayList<String> keyList = bugCommitMap.get(date);
						keyList.add(hashKey);
						bugCommitMap.replace(date, keyList);	
					}else {
						ArrayList<String> keyList = new ArrayList<String>();
						keyList.add(hashKey);
						bugCommitMap.put(date, keyList);
					}
				}else {
					continue;
				}
					
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return bugCommitMap;
	}
	
	public static HashMap<String, ArrayList<String>> getBugCommitMap(String project, String gitPath) {
		HashMap<String, ArrayList<String>> bugCommitMap = new HashMap<String, ArrayList<String>>();
		Pattern p = Pattern.compile(project + "-[0-9]+");
		String[] allCommit;
		ArrayList<String> neglectBug = new ArrayList<String>();
		try {
			GitHelp.getCheckoutAfterCommit("master", gitPath);
			GitHelp.initGit(gitPath);
			allCommit = GitHelp.getAllCommitOneLine(gitPath).split("\n");
			for (int i = 0; i < allCommit.length; i++) {
				String hashKey = allCommit[i].split("\t", 2)[0];
				String commitMsg = "";
				if(allCommit[i].split("\t").length>3)
					commitMsg = allCommit[i].split("\t")[3];
				else
					continue;
				if (!commitMsg.contains(project))
					continue;
				Matcher m = p.matcher(commitMsg);
				String prevBug = "";
				String flag = "";
//				System.out.println(allCommit[i]);
				while (m.find()) {
					String bugID = m.group();
					if (!prevBug.equals(bugID) && prevBug != "") {							
						String[] lines = GitHelp.gitShow(hashKey, gitPath).split("\n");
						String fileName = "";
						int javaNum = 0;
						for (int j = 0; j < lines.length; j++) {
							String line = lines[j];
//							System.out.println(hashKey+" "+line);
							if (line.startsWith("diff ")) {
								fileName = line;
								if(line.contains("\"a"))
									fileName = "";
								else if(line.contains("diff --cc "))
									fileName = "";
								else if (line.contains(" a/") && line.contains(" b/"))
									fileName = line.split(" a/")[1].split(" b/")[0];
								else
									fileName = line.split(" src/")[1].split(" src/")[0];						
							}else if(!line.startsWith("new file mode") && !fileName.equals("")) {
//								System.out.println(fileName);
								String name = fileName;
								name = name.replace("/", ".");
								int idx = name.indexOf(".org.");
								if (idx > 0)
									name = name.substring(idx);
								name = name.substring(1);
								if (name.endsWith(".java")) {
									javaNum++;
									fileName = "";
								}
							}else if(line.startsWith("new file mode")) {
								fileName = "";
							}
						}
						if(javaNum > 0) {
							System.err.println(hashKey + " " + bugID + " " + prevBug + " Multi Mentioned");		
							neglectBug.add(bugID.split("-")[1]);
							neglectBug.add(prevBug.split("-")[1]);
							flag = "NO";
							if (bugCommitMap.containsKey(bugID)) {
								bugCommitMap.remove(bugID);
								break;
							} else {
								break;
							}
						}else {
							System.err.println(hashKey + " " + bugID + " " + prevBug + " Multi Mentioned but no javafile");
						}
					}
					prevBug = bugID;
					if (bugCommitMap.containsKey(bugID)) {
						ArrayList<String> commits = bugCommitMap.get(bugID);
						commits.add(hashKey + flag);
						bugCommitMap.replace(bugID, commits);
					} else {
						ArrayList<String> commits = new ArrayList<String>();
						commits.add(hashKey + flag);
						bugCommitMap.put(bugID, commits);
					}
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		bugCommitMap.put("MULTIBUG", neglectBug);
		return bugCommitMap;
	}
	 

	
	public static HashMap<String, HashSet<String>> getFileMethodMap(ArrayList<String> commitList, String gitPath) {
		HashMap<String, HashSet<String>> fileMethodMap = new HashMap<String, HashSet<String>>();
		for (int i = 0; i < commitList.size(); i++) {
			String hashKey = commitList.get(i);
//			System.out.println(hashKey);
			try {
				String[] lines = GitHelp.gitShow(hashKey, gitPath).split("\n");
				String fileName = "!!";
				for (int j = 0; j < lines.length; j++) {
					String line = lines[j];
					if (line.startsWith("diff ")) {
						fileName = line;
//						System.out.println(fileName);
						try {
							if(line.contains("\"a"))
								fileName = "";
							else if(line.contains("diff --cc "))
								fileName = "";
							else if (line.contains(" a/") && line.contains(" b/"))
								fileName = line.split(" a/")[1].split(" b/")[0];
							else
								fileName = line.split(" src/")[1].split(" src/")[0];		
//							System.out.println(fileName);				
						}catch(Exception e) {
							System.err.println(hashKey+" "+line);
						}
					}else if(!line.startsWith("new file mode") && !fileName.equals("")) {
						String name = fileName;
						name = name.replace("/", ".");
						int idx = name.indexOf(".org.");
						if (idx > 0) {
							name = name.substring(idx);
							name = name.substring(1);
						}
						if (name.endsWith(".java")) {
//							System.out.println(name);
							if(!fileMethodMap.containsKey(name)) {
								fileMethodMap.put(name, new HashSet<String>());
								HashSet<String> methods = getMethod(hashKey, fileName, gitPath);
//								System.out.println(methods);
								if(methods.size() > 0)
									fileMethodMap.replace(name, methods);
							}else {
								HashSet<String> methods = getMethod(hashKey, fileName, gitPath);
								methods.addAll(fileMethodMap.get(name));
								if(methods.size() > 0)
									fileMethodMap.replace(name, methods);
							}
//							if (getEffectChange(hashKey, fileName, gitPath)) {
//								HashSet<String> methods = getMethod(hashKey, fileName, gitPath);
//								if(methods.size() > 0)
//									fileMethodMap.replace(name, methods);
//							} else {
//								fileMethodMap.remove(name);
//							}
							fileName = "";
						}
					}else if(line.startsWith("new file mode")) {
						fileName = "";
					}					

				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
//		System.out.println(fileMethodMap);
		return fileMethodMap;
	}

	private static File writeTempFile(File file, String tempName) {
		FileUtil.fileCopy(file.getAbsolutePath(), "./temp/" + tempName);
		return new File("./temp/" + tempName);
	}

	private static HashSet<String> getMethod(String hashKey, String fileName, String gitPath) {
		HashSet<String> fixedMethod = new HashSet<String>();
		try {
			GitHelp.getCheckout(hashKey, gitPath);
			File file = new File(gitPath + fileName);
			if (!file.exists())
				return fixedMethod;
			BufferedReader br = new BufferedReader(new FileReader(file));
			String prev = "";
			String str;
			while ((str = br.readLine()) != null) {
				prev = prev + str + "\n";
			}
			br.close();
			GitHelp.getCheckoutAfterCommit(hashKey, gitPath);
			file = new File(gitPath + fileName);
			if (!file.exists())
				return fixedMethod;
			else {
				br = new BufferedReader(new FileReader(file));
				String next = "";
				while ((str = br.readLine()) != null) {
					next = next + str + "\n";
				}
//				System.out.println(fileName+" "+prev.length()+" "+next.length());
				fixedMethod = MethodDiff.methodDiffInClass(prev, next);
				br.close();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return fixedMethod;
	}

//	private static boolean getEffectChange(String hashKey, String fileName, String gitPath) {
//		File file1 = null;
//		File file2 = null;
//		GitHelp.getCheckout(hashKey, gitPath);
//		File file = new File(gitPath + fileName);
//		if (!file.exists())
//			return false;
//		file1 = writeTempFile(file, "prev.txt");
//
//		GitHelp.getCheckoutAfterCommit(hashKey, gitPath);
//		file = new File(gitPath + fileName);
//		if (!file.exists())
//			return false;
//		else {
//			file2 = writeTempFile(file, "after.txt");
//		}
//		boolean effect = ChangeDistillerTest.getSignificanceChange(file1, file2);
//		if (effect)
//			return true;
//		System.err.println(fileName + " had only trivial changes");
//		return false;
//	}

	public static String containsMethodName(String line) {
		line = line.trim();

		// check that a line is comment.
		if (line.startsWith("//") || line.startsWith("/*") || line.startsWith("*/") || line.startsWith("*"))
			return null;

		// split code and comment if the line has comment.
		String methodCandidate = line;
		if (methodCandidate.indexOf("//") > 0) {
			methodCandidate = methodCandidate.substring(0, methodCandidate.indexOf("//"));
		}

		int index = methodCandidate.indexOf('(');
		if (index == -1) {
			return null;
		} else {
			methodCandidate = methodCandidate.substring(0, index + 1);
		}

		if (methodCandidate.contains("=") || methodCandidate.contains(" new ") || methodCandidate.contains(" class ")
				|| methodCandidate.contains(" extends "))
			return null;

//		String regExp = "(public|private|protected)*\\s+"
//				+ "(abstract|static|final|native|strictfp|synchronized)*\\s*"
//				+ "([A-z0-9_,.<>\\[\\]]*\\s*)*" + "\\(";
//		Pattern pattern = Pattern.compile(regExp);
		Pattern pattern = Pattern.compile("(public|private|protected).*");
		Matcher matcher = pattern.matcher(methodCandidate);
		if (matcher.find()) {
			// debug code
//			System.out.printf(">> [Method]: %s, %s\n", methodCandidate, matcher.group());

			String foundResult = matcher.group();
			String wordArray[] = foundResult.split("[ \\(]");
			String foundMethod = wordArray[wordArray.length - 1];
			return foundMethod;
		} else {
			return null;
		}
	}

}
