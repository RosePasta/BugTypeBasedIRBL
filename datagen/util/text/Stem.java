package util.text;

import java.util.ArrayList;

// Referenced classes of package utils:
//            PorterStemmer

public class Stem
{
    public Stem()
    {
    }

    public static String stem(String word)
    {
        PorterStemmer stemmer = new PorterStemmer();
        stemmer.reset();
        return word;
//        stemmer.stem(word);
//        return stemmer.toString();
    }
    
    public static String stemNL(String word)
    {
        PorterStemmer stemmer = new PorterStemmer();
        stemmer.reset();
//        return word;
        stemmer.stem(word);
        return stemmer.toString();
    }
    

    public static String stemByList(ArrayList<String> words)
    {
    	String resultText = "";
        PorterStemmer stemmer = new PorterStemmer();
        for(int i = 0 ; i<words.size(); i++) {
        	stemmer.reset();
        	stemmer.stem(words.get(i));
        	resultText = resultText+stemmer.toString()+" ";
        }
        return resultText;
    }
}
