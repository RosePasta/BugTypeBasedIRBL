package core.bench4bl;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import classifier.Classifier;
import common.Bug;
import indexer.FileIndexer;
import util.FileUtil;
import util.XMLParser;
import util.git.BugCommitExtractor;
import util.text.Splitter;

public class DataGen {
	public static HashMap<String, String> configMap = null;
	public static void main(String[] args) {
		configMap = FileUtil.readConfig();
		String group = configMap.get("GROUP");
		String project = configMap.get("PROJECT");
		String targetPath = configMap.get("BENCH4BL")
				+group+File.separator+project+File.separator; 
		
		String bugPath = targetPath+"bugrepo"+File.separator+"repository"+File.separator;
		String gitPath = targetPath+"gitrepo"+File.separator;
		String sourcePath = targetPath+"sources"+File.separator;
		String outDir = configMap.get("OUTPUT")+group+File.separator+project+File.separator;
		File sourceOut = new File(outDir+"sources"+File.separator);
		File bugOut = new File(outDir+"bug"+File.separator);
		if(!bugOut.exists())
			bugOut.mkdirs();
		if(!sourceOut.exists())
			sourceOut.mkdirs();
		
		
		// Bug Report Extender and Source File Indexer based on Bench4BL
		// Get Bug related Commits
		HashMap<String, ArrayList<String>> bugCommitMap 
					= BugCommitExtractor.getBugCommitMap(project,gitPath);
		ArrayList<String> neglect = bugCommitMap.get("MULTIBUG");
		
		ArrayList<File> bugRepoList = FileUtil.getAllFile(new File(bugPath));
		int bugNum = 0;
		int tbNum = 0;
		int pbNum = 0;
		for(int i = 0 ; i<bugRepoList.size(); i++) {
			ArrayList<Bug> newBugList = new ArrayList<Bug>();
			File bugFile = bugRepoList.get(i);
			String version = bugFile.getName().replace(".xml", "");
			// 1. Get Source File and Method Indexer
			HashMap<String, HashSet<String>> fileSet = FileIndexer.originFileIndexer(sourcePath+File.separator+version+File.separator, 
											sourceOut+File.separator+version+File.separator);
			FileIndexer.fileIndexerByType(sourcePath+File.separator+version+File.separator, 
					sourceOut+File.separator+version+File.separator, sourcePath, version+".");
//			System.out.println(fileSet);
			ArrayList<Bug> bugList = XMLParser.getFixedBugsFromXML(bugFile);
			for(int j = 0 ; j<bugList.size(); j++) {
				Bug bug = bugList.get(j);
				String bugID = bug.getBugID();
				String key = project+"-"+bugID;
				// 2.2. No Bug
//				System.out.println(bug.getType());
				if(!bug.getType().equals("Bug")) {
					System.err.println(key+" PASS - NO BUG");
					continue;
				}
				// 2.2. Passed the bugs (Multi mentioned, No commmit)
				if(neglect.contains(bug.getBugID())) {
					System.err.println(key+" PASS - Multiple Mentioned1");
					continue;
				}
				if(!bugCommitMap.containsKey(key)) {
					System.err.println(key+" PASS - Multiple Mentioned2");
					continue;
				}
				ArrayList<String> commits = bugCommitMap.get(key);
				// 2.2. Passed the bugs (Dup Report)
				HashMap<String, String> relations = bug.getRelationList();
				Iterator<String> relIter = relations.keySet().iterator();
				while(relIter.hasNext()) {
					String relID = relIter.next();
					String type = relations.get(relID);
					if(type.contains("Duplicate")) {
						System.err.println(key+" PASS - Duplicated");
						continue;
					}
				}
				
				
				// 2.3. Get Changed Source Files and Methods
				HashMap<String, HashSet<String>> fileMethodMap = BugCommitExtractor.getFileMethodMap(commits, gitPath);
				
				// 2.4. Get Bug Type (Production and Test)
				String bugType = Classifier.getBugType(fileMethodMap); 
				bug.setType(bugType);
				
				// 2.5 Refined Ground Truth based on Bug Types
				fileMethodMap = Classifier.refinedGroundTruth(fileMethodMap, fileSet, bugType);
				if(fileMethodMap == null || fileMethodMap.size() == 0) {
					System.err.println(bug.getBugID()+" PASS - SOME GT NOT INTO CORPUS");
					continue;
				}
				bug.addFileMethodMap(fileMethodMap);
				
//				System.out.println(bug.getBugID()+" "+bugType+" " +bug.getFileMethodMap());
				newBugList.add(bug);
				if(bugType.startsWith("PB"))
					pbNum++;
				else
					tbNum++;
				bugNum++;
			}
			System.out.println(bugList.size()+" "+newBugList.size()+" "+bugOut+File.separator+bugFile.getName());
			XMLParser.writeBugXMLWithMethod(newBugList, bugOut+File.separator+bugFile.getName(), project);
		}
		System.out.println(bugNum+" "+pbNum+" "+tbNum);
	}	

}
