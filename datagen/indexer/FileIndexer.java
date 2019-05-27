package indexer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.FSDirectory;

import classifier.Classifier;
import common.Method;
import util.FileUtil;

public class FileIndexer {

	public static HashMap<String, HashSet<String>> originFileIndexer(String inputDir, String outputDir) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		HashMap<String, HashSet<String>> fileMethodSet = new HashMap<String, HashSet<String>>(); 
		File verDir = new File(inputDir);
		String version = verDir.getName();

		// For Source Files
		ArrayList<File> allSourceFile = FileUtil.getAllJavaFile(verDir);
		ArrayList<String> allFileName = new ArrayList<String>();
		ArrayList<Document> allFileDocument = new ArrayList<Document>();
		File mk = new File(outputDir + "/raw/");
		if (!mk.exists())
			mk.mkdirs();
		mk = new File(outputDir + "/fileIndex/");
		if (!mk.exists())
			mk.mkdirs();
		mk = new File(outputDir + "/methodIndex/");
		if (!mk.exists())
			mk.mkdirs();

		// For Methods
		ArrayList<Document> allMethodDocument = new ArrayList<Document>();
		int sfNum = 0;
		int mthNum = 0;
		for (int k = 0; k < allSourceFile.size(); k++) {
			File file = allSourceFile.get(k);
			String fileName = file.getPath().replace(verDir.getAbsolutePath(), "");
			fileName = fileName.replace("\\", ".");
			int idx = fileName.indexOf(".org.");
			if (idx > 0)
				fileName = fileName.substring(idx);
			fileName = fileName.substring(1);
			allFileName.add(fileName);
			FileUtil.fileCopy(file.getAbsolutePath(), outputDir + "/raw/" + k + ".java");
			Document doc = SourceFileIndexer.createDocumentWithStructuredInfo(file, fileName, k);
			allFileDocument.add(doc);
			sfNum++;
			HashSet<String> methodSet = new HashSet<String>();
			ArrayList<Method> methods = MethodIndexer.getMethods(doc.get("raw"));
			for (int l = 0; l < methods.size(); l++) {
				Method method = methods.get(l);
				String name = fileName + ":" + method.getName() + "(" + method.getParams() + ")";
				name = name.replace(" ", "");
				Document mthDoc = MethodIndexer.createDocument(method, name, mthNum);
				allMethodDocument.add(mthDoc);
				mthNum++;
				methodSet.add(name.split(":")[1]);
			}
			fileMethodSet.put(fileName, methodSet);
		}
		FSDirectory dir;
		try {
			dir = FSDirectory.open(Paths.get(outputDir + "/fileIndex/"));
			IndexWriterConfig config = new IndexWriterConfig(new StandardAnalyzer());
			IndexWriter writer = new IndexWriter(dir, config);
			writer.deleteAll();
			writer.addDocuments(allFileDocument);
			writer.commit();
			writer.close();

			dir = FSDirectory.open(Paths.get(outputDir + "/methodIndex/"));
			config = new IndexWriterConfig(new StandardAnalyzer());
			writer = new IndexWriter(dir, config);
			writer.deleteAll();
			writer.addDocuments(allMethodDocument);
			writer.commit();
			writer.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(outputDir+" SF NUM: "+sfNum+" MTH NUM: "+mthNum);
		return fileMethodSet;

	}

	public static void fileIndexerByType(String inputDir, String outputDir, String absolutePath, String ver) {

		ArrayList<File> allSourceFile = FileUtil.getAllJavaFile(new File(inputDir));
		String sfOutput = outputDir;
		File mk = new File(sfOutput + "/pf-raw/");
		if (!mk.exists())
			mk.mkdirs();
		mk = new File(sfOutput + "/pfileIndex/");
		if (!mk.exists())
			mk.mkdirs();
		mk = new File(sfOutput + "/pmethodIndex/");
		if (!mk.exists())
			mk.mkdirs();
		mk = new File(sfOutput + "/tf-raw/");
		if (!mk.exists())
			mk.mkdirs();
		mk = new File(sfOutput + "/tfileIndex/");
		if (!mk.exists())
			mk.mkdirs();
		mk = new File(sfOutput + "/tmethodIndex/");
		if (!mk.exists())
			mk.mkdirs();

		File chk = new File(sfOutput + "/tMethodKeyMap.txt");
		if (chk.length() > 0) {
			System.out.println(sfOutput + " Already Existing");
//			return;
		}

		// For Methods
		ArrayList<String> allPFileName = new ArrayList<String>();
		ArrayList<Document> allPFileDocument = new ArrayList<Document>();
		ArrayList<Document> allPMethodDocument = new ArrayList<Document>();
		ArrayList<String> allTFileName = new ArrayList<String>();
		ArrayList<Document> allTFileDocument = new ArrayList<Document>();
		ArrayList<Document> allTMethodDocument = new ArrayList<Document>();
		int pfNum = 0;
		int tfNum = 0;
		int pmthNum = 0;
		int tmthNum = 0;
		int sfNum = 0;
		int mthNum = 0;
		for (int k = 0; k < allSourceFile.size(); k++) {
			File file = allSourceFile.get(k);
			String fileName = file.getPath().replace(absolutePath, "");
			fileName = fileName.replace("\\", ".");
			int idx = fileName.indexOf(".org.");
			if (idx > 0)
				fileName = fileName.substring(idx);
			fileName = fileName.substring(1);
			idx = fileName.indexOf(ver);
			if(idx > 0) {				
				fileName = fileName.substring(idx+ver.length());
			}
			boolean tFlag = Classifier.isTestRelated(fileName);

			sfNum++;
			if (tFlag) {
				FileUtil.fileCopy(file.getAbsolutePath(), sfOutput + "/tf-raw/" + tfNum + ".java");
				allTFileName.add(fileName);
				Document doc = SourceFileIndexer.createDocumentWithStructuredInfo(file, fileName, tfNum);
				allTFileDocument.add(doc);
				ArrayList<Method> methods = MethodIndexer.getMethods(doc.get("raw"));
				for (int l = 0; l < methods.size(); l++) {
					Method method = methods.get(l);
					String name = fileName + ":" + method.getName() + "(" + method.getParams() + ")";
					name = name.replace(" ", "");
					Document mthDoc = MethodIndexer.createDocument(method, name, tmthNum);
					allTMethodDocument.add(mthDoc);
					tmthNum++;
					mthNum++;
				}
				tfNum++;
			} else {
				FileUtil.fileCopy(file.getAbsolutePath(), sfOutput + "/pf-raw/" + pfNum + ".java");
				allPFileName.add(fileName);
				Document doc = SourceFileIndexer.createDocumentWithStructuredInfo(file, fileName, pfNum);
				allPFileDocument.add(doc);
				ArrayList<Method> methods = MethodIndexer.getMethods(doc.get("raw"));
				for (int l = 0; l < methods.size(); l++) {
					Method method = methods.get(l);
					String name = fileName + ":" + method.getName() + "(" + method.getParams() + ")";
					name = name.replace(" ", "");
					Document mthDoc = MethodIndexer.createDocument(method, name, pmthNum);
					if (method.getName().contains("test") || method.getName().contains("Test")
							|| method.getName().contains("junit"))
						allTMethodDocument.add(mthDoc);
					else
						allPMethodDocument.add(mthDoc);
					pmthNum++;
					mthNum++;
				}
				pfNum++;
			}
		}
		FSDirectory dir;
		try {
			dir = FSDirectory.open(Paths.get(sfOutput + "/pfileIndex/"));
			IndexWriterConfig config = new IndexWriterConfig(new StandardAnalyzer());
			IndexWriter writer = new IndexWriter(dir, config);
			writer.deleteAll();
			writer.addDocuments(allPFileDocument);
			writer.commit();
			writer.close();

			dir = FSDirectory.open(Paths.get(sfOutput + "/pmethodIndex/"));
			config = new IndexWriterConfig(new StandardAnalyzer());
			writer = new IndexWriter(dir, config);
			writer.deleteAll();
			writer.addDocuments(allPMethodDocument);
			writer.commit();
			writer.close();

			dir = FSDirectory.open(Paths.get(sfOutput + "/tfileIndex/"));
			config = new IndexWriterConfig(new StandardAnalyzer());
			writer = new IndexWriter(dir, config);
			writer.deleteAll();
			writer.addDocuments(allTFileDocument);
			writer.commit();
			writer.close();

			dir = FSDirectory.open(Paths.get(sfOutput + "/tmethodIndex/"));
			config = new IndexWriterConfig(new StandardAnalyzer());
			writer = new IndexWriter(dir, config);
			writer.deleteAll();
			writer.addDocuments(allTMethodDocument);
			writer.commit();
			writer.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(outputDir+" SF: "+sfNum+" PSF: "+pfNum+" TSF: "+tfNum+" MTH: "+mthNum+" PMTH: "+pmthNum+" TMTH: "+tmthNum);
	}
}