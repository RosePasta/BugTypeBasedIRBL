package searcher;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.similarities.ClassicSimilarity;

import common.Bug;
import util.text.Preprocessor;

public class SearcherByLucene {
	public ArrayList<String> searchFile(IndexSearcher searcher, QueryParser parser, int top, String searchQuery) {
		ArrayList<String> searchResult = new ArrayList<String>();
		int queryLen = searchQuery.split(" ").length;
		if (queryLen > 1024)
			BooleanQuery.setMaxClauseCount(queryLen);

		try {
			searcher.setSimilarity(new ClassicSimilarity());
			Query myquery = parser.parse(searchQuery);
			TopDocs results = searcher.search(myquery, top);
			ScoreDoc[] hits = results.scoreDocs;
			String findBuggyFile = "";
			ArrayList<String> rankedList = new ArrayList<String>();
			for (int i = 0; i < hits.length; ++i) {
				ScoreDoc item = hits[i];
				Document doc;
				doc = searcher.doc(item.doc);
				searchResult.add(doc.get("fileName"));
//				if(i< 20)
//					System.out.println(i+" "+item.score+" "+doc.get("fileName"));
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return searchResult;
	}

	public static ArrayList<String> searchMethod(IndexSearcher searcher, QueryParser parser, int top,
			String searchQuery) {
		ArrayList<String> searchResult = new ArrayList<String>();
		int queryLen = searchQuery.split(" ").length;
		if (queryLen > 1024)
			BooleanQuery.setMaxClauseCount(queryLen);

		try {
			searcher.setSimilarity(new ClassicSimilarity());
			Query myquery = parser.parse(searchQuery);
			TopDocs results = searcher.search(myquery, top);
			ScoreDoc[] hits = results.scoreDocs;
			String findBuggyFile = "";
			ArrayList<String> rankedList = new ArrayList<String>();
			for (int i = 0; i < hits.length; ++i) {
				ScoreDoc item = hits[i];
				Document doc;
				doc = searcher.doc(item.doc);
				searchResult.add(doc.get("fileMthName"));
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return searchResult;
	}

}
