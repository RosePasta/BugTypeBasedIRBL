package indexer;

import java.io.File;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;

import util.FileParser;
import util.FileUtil;
import util.text.Preprocessor;


public class SourceFileIndexer {

	public static Document createDocument(File file, String fileName, int id) {
		Document doc = new Document();	
		doc.add(new TextField("id", id + "", Field.Store.YES));
		doc.add(new TextField("fileName", fileName, Field.Store.YES));
		
		String content = FileUtil.readFileContent(file);
		doc.add(new TextField("raw", content, Field.Store.YES));
		doc.add(new TextField("pp-raw", Preprocessor.preprocessForCamel(content), Field.Store.YES));
		return doc;
	}
	
	public static Document createDocumentWithStructuredInfo(File file, String fileName, int id) {
		Document doc = new Document();
		doc.add(new TextField("id", id + "", Field.Store.YES));
		doc.add(new TextField("fileName", fileName, Field.Store.YES));
		
		String content = FileUtil.readFileContent(file);
		doc.add(new TextField("raw", content, Field.Store.YES));
		doc.add(new TextField("pp-raw", Preprocessor.preprocessForCamel(content), Field.Store.YES));
		
		FileParser parser = new FileParser(content);
		String classes = parser.getAllClassNames();
		doc.add(new TextField("pp-classes", Preprocessor.preprocessForCamel(classes), Field.Store.YES));
		
		String methods = parser.getAllMethodNames();
		doc.add(new TextField("pp-methods", Preprocessor.preprocessForCamel(methods), Field.Store.YES));
		
		String variables = parser.getAllVariableNames();
		doc.add(new TextField("pp-variables", Preprocessor.preprocessForCamel(variables), Field.Store.YES));
		
		String comments = parser.getAllComments();
		doc.add(new TextField("pp-comments", Preprocessor.preprocessForCamel(comments), Field.Store.YES));
		
		String imports = parser.getImportedClasses().toString();
		doc.add(new TextField("imports", imports, Field.Store.YES));
		doc.add(new TextField("pp-imports", Preprocessor.preprocessForCamel(imports), Field.Store.YES));
		return doc;
	}

}
