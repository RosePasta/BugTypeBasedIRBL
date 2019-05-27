package core;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.FSDirectory;

import classifier.Classifier;
import common.Bug;
import evaluator.Evaluator;
import searcher.SearcherByLucene;
import util.FileUtil;
import util.XMLParser;
import util.text.Preprocessor;

public class IRBL {
	public static HashMap<String, String> configMap = null;

	public static void main(String[] args) throws IOException {
		int topN = 100000;
		configMap = FileUtil.readConfig();
		String group = configMap.get("GROUP");
		String project = configMap.get("PROJECT");
		String targetPath = configMap.get("OUTPUT") + group + File.separator + project + File.separator;
		

		String bench4BLBugPath = configMap.get("BENCH4BL")
				+group+File.separator+project+File.separator+"bugrepo"+File.separator+"repository"+File.separator;

		String bugPath = targetPath + "bug" + File.separator;
		String sourcePath = targetPath + "sources" + File.separator;
		File[] allVersion = new File(sourcePath).listFiles();
		double bugNum = 0; int pb = 0; int tb = 0; int pbmth = 0; int tbmth = 0; int mth = 0; int pbtf = 0;
		HashMap<String, Double> resultMap = new HashMap<String, Double>();
		resultMap.put("O-TOP5", 0.0);
		resultMap.put("O-MAP", 0.0);
		resultMap.put("O-MRR", 0.0);
		resultMap.put("G-TOP5", 0.0);
		resultMap.put("G-MAP", 0.0);
		resultMap.put("G-MRR", 0.0);
		resultMap.put("S-TOP5", 0.0);
		resultMap.put("S-MAP", 0.0);
		resultMap.put("S-MRR", 0.0);
		for (int i = 0; i < allVersion.length; i++) {
			File dir = allVersion[i];
			String version = dir.getName();
			String pFileIndexPath = dir + "/pfileIndex/";
			IndexReader pfileReader = DirectoryReader.open(FSDirectory.open((new File(pFileIndexPath)).toPath()));
			IndexSearcher pfileSearcher = new IndexSearcher(pfileReader);
			
			String tFileIndexPath = dir + "/tfileIndex/";
			IndexReader tfileReader = DirectoryReader.open(FSDirectory.open((new File(tFileIndexPath)).toPath()));
			IndexSearcher tfileSearcher = new IndexSearcher(tfileReader);
			
			String allfileIndexPath = dir + "/fileIndex/";
			IndexReader allfileReader = DirectoryReader.open(FSDirectory.open((new File(allfileIndexPath)).toPath()));
			IndexSearcher allfileSearcher = new IndexSearcher(allfileReader);
			
			String pMthIndexPath = dir + "/pmethodIndex/";
			IndexReader pmthReader = DirectoryReader.open(FSDirectory.open((new File(pMthIndexPath)).toPath()));
			IndexSearcher pmthSearcher = new IndexSearcher(pmthReader);
			
			String tMthIndexPath = dir + "/tmethodIndex/";
			IndexReader tmthReader = DirectoryReader.open(FSDirectory.open((new File(tMthIndexPath)).toPath()));
			IndexSearcher tmthSearcher = new IndexSearcher(tmthReader);
			
			String allmthIndexPath = dir + "/methodIndex/";
			IndexReader allmthReader = DirectoryReader.open(FSDirectory.open((new File(allmthIndexPath)).toPath()));
			IndexSearcher allmthSearcher = new IndexSearcher(allmthReader);
			
			Analyzer analyzer = new StandardAnalyzer();			
			ArrayList<Bug> bugList = XMLParser.getFixedBugsFromXMLWithMethod(new File(bugPath+File.separator+version+".xml"));
			HashMap<String, Bug> originBugList = XMLParser.getFixedBugsFromXMLToMap(new File(bench4BLBugPath+File.separator+version+".xml"));
			
			for (int k = 0; k < bugList.size(); k++) {
				Bug bug = bugList.get(k);
				if(!bug.getType().contains("PB"))
					continue;
				String content = bug.getSum() + "\n" + bug.getDesc();
				String ppContent = Preprocessor.preprocessForCamel(content);
				QueryParser parser = new QueryParser("pp-raw", analyzer);
				IndexSearcher searcher = allfileSearcher;
				
				Bug originBug = originBugList.get(bug.getBugID());
				ArrayList<String> searchResult = new SearcherByLucene().searchFile(searcher, parser, topN, ppContent);
				HashMap<String, Double> evalResult = Evaluator.evaluateFileLoc(originBug.getFileList(), searchResult, topN);
				System.out.println("ORIGIN-FILE\t"+bug.getBugID()+"\t"+bug.getType()+"\t"+evalResult);
				resultMap.replace("O-TOP5", resultMap.get("O-TOP5")+evalResult.get("top5"));
				resultMap.replace("O-MRR", resultMap.get("O-MRR")+evalResult.get("rr"+topN));
				resultMap.replace("O-MAP", resultMap.get("O-MAP")+evalResult.get("ap"+topN));
				// 1. Original Buggy File and Method
				
				searchResult = new SearcherByLucene().searchFile(searcher, parser, topN, ppContent);
				evalResult = Evaluator.evaluateFileLoc(bug.getFileList(), searchResult, topN);
				System.out.println("GTF-FILE\t"+bug.getBugID()+"\t"+bug.getType()+"\t"+evalResult);
				resultMap.replace("G-TOP5", resultMap.get("G-TOP5")+evalResult.get("top5"));
				resultMap.replace("G-MRR", resultMap.get("G-MRR")+evalResult.get("rr"+topN));
				resultMap.replace("G-MAP", resultMap.get("G-MAP")+evalResult.get("ap"+topN));
				
				if(bug.getType().startsWith("PB")) {
					pb++;
					searcher = pfileSearcher;
					searchResult = new SearcherByLucene().searchFile(searcher, parser, topN, ppContent);
					evalResult = Evaluator.evaluateFileLoc(bug.getFileList(), searchResult, topN);
					System.out.println("PBL-FILE\t"+bug.getBugID()+"\t"+bug.getType()+"\t"+evalResult);		
					resultMap.replace("S-TOP5", resultMap.get("S-TOP5")+evalResult.get("top5"));
					resultMap.replace("S-MRR", resultMap.get("S-MRR")+evalResult.get("rr"+topN));
					resultMap.replace("S-MAP", resultMap.get("S-MAP")+evalResult.get("ap"+topN));			
				}else {					
					tb++;
					searcher = tfileSearcher;
					searchResult = new SearcherByLucene().searchFile(searcher, parser, topN, ppContent);
					evalResult = Evaluator.evaluateFileLoc(bug.getFileList(), searchResult, topN);
					System.out.println("TBL-FILE\t"+bug.getBugID()+"\t"+bug.getType()+"\t"+evalResult);		
					resultMap.replace("S-TOP5", resultMap.get("S-TOP5")+evalResult.get("top5"));
					resultMap.replace("S-MRR", resultMap.get("S-MRR")+evalResult.get("rr"+topN));
					resultMap.replace("S-MAP", resultMap.get("S-MAP")+evalResult.get("ap"+topN));
					
				}

//				boolean methodFlag = Classifier.hasMethodBug(bug.getFileMethodMap());
//				if(methodFlag) {
//					mth++;
//					searcher = allmthSearcher;
//					searchResult = new SearcherByLucene().searchMethod(searcher, parser, topN, ppContent);
//					evalResult = Evaluator.evaluateMethodLoc(bug.getFileMethodMap(), searchResult, topN);
//					System.out.println("GTF-MTH\t"+bug.getBugID()+"\t"+bug.getType()+"\t"+evalResult);
//					if(bug.getType().startsWith("PB")) {
//						pbmth++;
//						searcher = pmthSearcher;
//						searchResult = new SearcherByLucene().searchMethod(searcher, parser, topN, ppContent);
//						evalResult = Evaluator.evaluateMethodLoc(bug.getFileMethodMap(), searchResult, topN);
//						System.out.println("PBL-MTH\t"+bug.getBugID()+"\t"+bug.getType()+"\t"+evalResult);
//						
//					}else {
//						tbmth++;
//						searcher = tmthSearcher;
//						searchResult = new SearcherByLucene().searchMethod(searcher, parser, topN, ppContent);
//						evalResult = Evaluator.evaluateMethodLoc(bug.getFileMethodMap(), searchResult, topN);
//						System.out.println("TBL-MTH\t"+bug.getBugID()+"\t"+bug.getType()+"\t"+evalResult);
//					}
//				}				
				
				bugNum++;				
			}
		}
		System.out.println(bugNum+" "+pb+" "+tb+" "+mth+" "+pbmth+" "+tbmth+" "+pbtf);
		String result = resultMap.get("O-TOP5")/bugNum+"\t"+resultMap.get("O-MRR")/bugNum+"\t"+resultMap.get("O-MAP")/bugNum+"\t"
				+resultMap.get("G-TOP5")/bugNum+"\t"+resultMap.get("G-MRR")/bugNum+"\t"+resultMap.get("G-MAP")/bugNum+"\t"
				+resultMap.get("S-TOP5")/bugNum+"\t"+resultMap.get("S-MRR")/bugNum+"\t"+resultMap.get("S-MAP")/bugNum;
		System.out.println(result);
	}

}
