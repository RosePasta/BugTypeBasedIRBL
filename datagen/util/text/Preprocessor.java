package util.text;

public class Preprocessor {
	static Splitter split = new Splitter();
	static StopWordManager stopRemove = new StopWordManager();
	static Stem stemmer = new Stem();

	public static String preprocessForCamel(String content) {

		return stemmer.stemByList(stopRemove.getRefinedList(split.splitSourceCode(removeBlank(content)))).toLowerCase();
	}

	private static String removeBlank(String content) {
		
		while(content.contains("  "))
			content = content.replace("  ", " ");
		if(content.startsWith(" "))
			content = content.substring(1);
		if(content.endsWith(" "))
			content = content.substring(0, content.length()-2);
		return content;
	}

}
