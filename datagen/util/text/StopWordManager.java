// BLIZZARD's stopwords.StopWordManager

package util.text;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;

import util.FileUtil;

public class StopWordManager {
	public static ArrayList<String> stopList;
	String stopDir = "./data/stop-words-english-total.txt";
	String javaKeywordFile = "./data/java-keywords.txt";
	String emotionKeywordFile = "./data/emotional-keywords.txt";
	String CppKeywordFile = "./data/cpp-keywords.txt";

	public StopWordManager() {
		this.stopList = new ArrayList();
		loadStopWords();
	}

	protected void loadStopWords() {
		try {
			Scanner scanner = new Scanner(new File(this.stopDir));
			while (scanner.hasNext()) {
				String word = scanner.nextLine().trim();
				this.stopList.add(word);
			}
			scanner.close();

			ArrayList<String> keywords = FileUtil.getAllLinesByList(this.javaKeywordFile);
			this.stopList.addAll(keywords);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected String removeSpecialChars(String sentence) {
		String regex = "\\p{Punct}+|\\d+|\\s+";
		String[] parts = sentence.split(regex);
		String refined = new String();
		String[] arrayOfString1;
		int j = (arrayOfString1 = parts).length;
		for (int i = 0; i < j; i++) {
			String str = arrayOfString1[i];
			refined = refined + str.trim() + " ";
		}
		return refined;
	}

	public String removeTinyTerms(String sentence) {
		String regex = "\\p{Punct}+|\\d+|\\s+";
		String[] parts = sentence.split(regex);
		String refined = new String();
		String[] arrayOfString1;
		int j = (arrayOfString1 = parts).length;
		for (int i = 0; i < j; i++) {
			String str = arrayOfString1[i];
			if (str.length() >= 3) {
				refined = refined + str.trim() + " ";
			}
		}
		return refined;
	}

	public String getRefinedSentence(String sentence) {
		String refined = new String();
		String temp = removeSpecialChars(sentence);
		String[] tokens = temp.split("\\s+");
		String[] arrayOfString1;
		int j = (arrayOfString1 = tokens).length;
		for (int i = 0; i < j; i++) {
			String token = arrayOfString1[i];
			if (!this.stopList.contains(token.toLowerCase())) {
				refined = refined + token + " ";
			}
		}
		return refined.trim();
	}

	public static ArrayList<String> getRefinedList(String[] words) {
		ArrayList<String> refined = new ArrayList();
		String[] arrayOfString;
		int j = (arrayOfString = words).length;
		for (int i = 0; i < j; i++) {
			String word = arrayOfString[i];
			if (!stopList.contains(word.toLowerCase())) {
				refined.add(word);
			}
		}
		return refined;
	}

	public ArrayList<String> getRefinedList(ArrayList<String> words) {
		ArrayList<String> refined = new ArrayList();
		for (String word : words) {
			if (!this.stopList.contains(word.toLowerCase())) {
				refined.add(word);
			}
		}
		return refined;
	}

	public HashSet<String> getRefinedList(HashSet<String> words) {
		HashSet<String> refined = new HashSet();
		for (String word : words) {
			if (!this.stopList.contains(word.toLowerCase())) {
				refined.add(word);
			}
		}
		return refined;
	}

}