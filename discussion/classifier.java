import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.StringTokenizer;

public class classifier {

	public static void main(String[] args) throws IOException {
		String project = "jdt";
		String type = "pos";
		String trainFile = "./classification_data/"+project+"-train_"+type+".data", 
				testFile = "./classification_data/"+project+"-test_"+type+".data",
				vocabFile = "./classification_data/"+project+"-vocab.data";
		
		System.out.println("NON BINARY NB");
		NBClassifier(false, trainFile, testFile, vocabFile);
		
		System.out.println("BINARY NB");
		NBClassifier(true, trainFile, testFile, vocabFile);
	}
	

	public static void NBClassifier(boolean binaryNB, String trainFile, String testFile, String vocabFile) throws IOException{
		long time = System.currentTimeMillis();
		String s;
		BufferedReader br;
		HashSet<Integer> stopwords = new HashSet<>();
		int distinctWords = 0;				
		
		br = new BufferedReader(new FileReader(vocabFile));
		while((s = br.readLine())!=null) {
			distinctWords++;
		}
		br.close();
		
		int[] countPos = new int[distinctWords];
		int[] countNeg = new int[distinctWords];
		int posValue = 0, negValue = 0, totalWordsInPos = 0, totalWordsInNeg = 0;
		
		br = new BufferedReader(new FileReader(trainFile));
		while((s = br.readLine())!=null) {
			StringTokenizer st = new StringTokenizer(s," :");
			if(st.countTokens()==0)	continue;
			int rating = Integer.parseInt(st.nextToken());
			if(rating > 5) { 
				posValue++;
				while(st.hasMoreTokens()) {
					int word = Integer.parseInt(st.nextToken());
					int freq = Integer.parseInt(st.nextToken());
					freq = binaryNB ? 1 : freq;
					if(stopwords.contains(word))	continue;
					countPos[word]+=freq;
					totalWordsInPos+=freq;
				}
			}else { 
				negValue++;
				while(st.hasMoreTokens()) {
					int word = Integer.parseInt(st.nextToken());
					int freq = Integer.parseInt(st.nextToken());
					freq = binaryNB ? 1 : freq;
					if(stopwords.contains(word))	continue;
					countNeg[word]+=freq;
					totalWordsInNeg+=freq;
				}
			}
		}
		br.close();
		
		br = new BufferedReader(new FileReader(testFile));
		int truePositive = 0, falsePositive = 0, falseNegative = 0, correctClassification = 0, incorrectClassification = 0;
		while((s = br.readLine())!=null) {
			StringTokenizer st = new StringTokenizer(s, " :");
			int rating = Integer.parseInt(st.nextToken());
			int actual = rating>5 ? 1 : 0;//1-->yes, 0-->no
			double probOfPos = Math.log(posValue/(posValue+negValue+0.0));
			double probOfNeg = Math.log(negValue/(posValue+negValue+0.0));
			
			while(st.hasMoreTokens()) {
				int word = Integer.parseInt(st.nextToken());
				int freq = Integer.parseInt(st.nextToken());
				freq = binaryNB ? 1 : freq;
				if(stopwords.contains(word))	continue;
				probOfPos+=freq*Math.log((countPos[word]+1)/(totalWordsInPos+distinctWords+0.0));
				probOfNeg+=freq*Math.log((countNeg[word]+1)/(totalWordsInNeg+distinctWords+0.0));
			}
			
			int predicted = (probOfPos>probOfNeg ? 1 : 0);
			if(predicted  == actual )	correctClassification++;
			else 						incorrectClassification++;
		
			if(predicted==1 && actual==1)			truePositive++;
			else if(predicted==1 && actual==0)		falseNegative++;
			else if(predicted==0 && actual==1)		falsePositive++;
		}
		br.close();
		double accuracy = correctClassification/(correctClassification + incorrectClassification + 0.0);
		double precision = truePositive/(truePositive+falsePositive+0.0);
		double recall = truePositive/(truePositive+falseNegative+0.0);
		double fscore = 2*precision*recall/(precision+recall); 
		System.out.println("Accuracy="+accuracy+"\nPrecision="+precision+" Recall="+recall+" F-Score="+fscore);
		time = System.currentTimeMillis()-time; 
		System.out.println("Time:"+time/1000d+"s");
	}
}
