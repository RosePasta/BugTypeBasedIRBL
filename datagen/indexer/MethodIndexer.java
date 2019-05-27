package indexer;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;

import common.Method;
import util.FileParser;
import util.text.Preprocessor;

public class MethodIndexer {
	
	public static ArrayList<Method> getMethods(String content){
		ArrayList<Method> result = new ArrayList<Method>();
		FileParser parser = new FileParser(content);
		HashMap<String, ArrayList<String>> methodData = parser.getMethodStringList();
		Iterator<String> mthIter = methodData.keySet().iterator();
		while(mthIter.hasNext()) {
			String mthString =mthIter.next();
			String[] data =  mthString.split("\n");
			String returnType = data[0];
			String mthName = data[1];
			String params = data[2];
			ArrayList<String> texts = methodData.get(mthString);
			String javaDoc = "";
			if(texts.size() > 1)
				javaDoc = texts.get(1);
			String code = texts.get(0).replace(javaDoc,"");
			
			Method method = new Method();
			method.setReturnType(returnType);
			method.setName(mthName);
			method.setParams(params);
			method.setCode(code);
			method.setJavaDoc(javaDoc);
			result.add(method);
		}
		return result;
	}

	public static Document createDocument(Method method, String name, int id) {
		Document doc = new Document();
		doc.add(new TextField("id", id + "", Field.Store.YES));
		doc.add(new TextField("fileMthName", name, Field.Store.YES));
		
		String code = method.getCode();
		doc.add(new TextField("code", code, Field.Store.YES));
		doc.add(new TextField("pp-code", Preprocessor.preprocessForCamel(code), Field.Store.YES));
		
		String javaDoc = method.getJavaDoc();
		doc.add(new TextField("raw", javaDoc+"\n"+code, Field.Store.YES));
		doc.add(new TextField("pp-raw", Preprocessor.preprocessForCamel(javaDoc+"\n"+code), Field.Store.YES));
		
		return doc;
	}

}
