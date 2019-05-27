package classifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import util.text.Splitter;

public class Classifier {
	public static boolean isTestRelatedSimple(String fullPath) {
		if(fullPath.toLowerCase().contains("test") || fullPath.toLowerCase().contains("junit")) {			
			return true;
		}
//		System.out.println(fullPath);
		return false;
		
	}
	
	public static boolean isTestRelated(String fullPath) {
		HashSet<String> testRelatedFolderKeywords = new HashSet<String>();
		testRelatedFolderKeywords.add("test");
		testRelatedFolderKeywords.add("junit");
		testRelatedFolderKeywords.add("tests");
		
		boolean flag = false;
		String[] terms = fullPath.split("\\.");
		for(int i = 0 ; i<terms.length-2; i++) {
			String term = terms[i].toLowerCase();
			if(testRelatedFolderKeywords.contains(term)) {
//				System.out.println(fullPath+" Test Folder");
				return true;
			}			
		}
		String fileName = terms[terms.length-2];
		terms = Splitter.splitSourceCode(fileName);
		for(int i = 0 ; i<terms.length; i++) {
			String term = terms[i].toLowerCase();
			if(term.startsWith("test")) {
//				System.out.println(fullPath+" Test File");
				return true;
			}
		}
		
		return flag;
	}
	


	
	public static HashMap<String, HashSet<String>> refinedGroundTruth(HashMap<String, HashSet<String>> fileMethodMap,
			HashMap<String, HashSet<String>> fileSet, String bugType) {
		
		Set<String> fileNameSet = fileSet.keySet();
		HashMap<String, HashSet<String>> newFileMethodMap = new HashMap<String, HashSet<String>>();
		
		Iterator<String> mapIter = fileMethodMap.keySet().iterator();			
		while(mapIter.hasNext()) {
			String filePath = mapIter.next();
			if(!fileNameSet.contains(filePath)) {
				System.err.println(filePath+" NOT INTO CORPUS");
				return null;
			}		
			HashSet<String> newMethod = new HashSet<String>();
			HashSet<String> methods = fileMethodMap.get(filePath);
			Iterator<String> mthIter = methods.iterator();
			while(mthIter.hasNext()) {
				String method = mthIter.next();
				if(fileSet.get(filePath).contains(method)) {
					newMethod.add(method);
				}
			}
			boolean testRelated = Classifier.isTestRelated(filePath);
			if(testRelated && bugType.equals("TB")) {
				newFileMethodMap.put(filePath, newMethod);
			}else if(!testRelated & bugType.startsWith("PB")) {
				newFileMethodMap.put(filePath, newMethod);
			}else if(testRelated & bugType.startsWith("PB")){
//				System.err.println(filePath+" TF!");
			}
		}
		return newFileMethodMap;
	}

	public static boolean hasMethodBug(HashMap<String, HashSet<String>> fileMethodMap) {
		int bugMthNum = 0;
		Iterator<String> fileIter = fileMethodMap.keySet().iterator();
		while (fileIter.hasNext()) {
			String fileName = fileIter.next();
			bugMthNum = bugMthNum + fileMethodMap.get(fileName).size();
		}
		if (bugMthNum > 0) {
			return true;
		} else {
			return false;
		}
	}

	public static String getBugType(HashMap<String, HashSet<String>> fileMethodMap) {
		int tfNum = 0;
		int fNum = 0;
		Iterator<String> iter = fileMethodMap.keySet().iterator();
		ArrayList<String> pfList = new ArrayList<String>();
		while (iter.hasNext()) {
			String fullPath = iter.next();
			fNum++;
			if (Classifier.isTestRelated(fullPath)) {
				tfNum++;
			} else {
				pfList.add(fullPath);
			}
		}
		if (fNum == tfNum) {
//			System.out.println("TB");
			return "TB";
		}else {
			if(tfNum > 0)
				return "PB-TF";
			else
				return "PB";
		}
	}
}
