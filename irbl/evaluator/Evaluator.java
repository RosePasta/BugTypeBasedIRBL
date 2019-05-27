package evaluator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import util.CalculatorUtil;


public class Evaluator {
	private static String refinedSPR(String text) {
		String result = "";
		String[] data = text.split("\\.");
		int orgIndex = 0;
		int orgNum = 0;
		for (int i = 0; i < data.length; i++) {
			if (data[i].equals("org")) {
				orgNum++;
				orgIndex = i;
			}
		}
		if (orgNum > 1) {
			for (int i = orgIndex; i < data.length; i++) {
				result = result + data[i] + ".";
			}
		}
		if (result.length() > 0)
			return result.substring(0, result.length() - 1);
		else
			return text;
	}

	private static String modifyVer(String text) {
		HashMap<String, String> correctKeyMap = new HashMap<String, String>();
		correctKeyMap.put("commons.math3.", "commons.math.");
		correctKeyMap.put("commons.math4.", "commons.math.");
		correctKeyMap.put("commons.lang3.", "commons.lang.");
		correctKeyMap.put("commons.configuration2.", "commons.configuration.");
		correctKeyMap.put("commons.collections4.", "commons.collections.");
		correctKeyMap.put("org.springframework.jms.src.main.java.", "");
		Iterator<String> keyIter = correctKeyMap.keySet().iterator();
		while (keyIter.hasNext()) {
			String key = keyIter.next();
			if (text.contains(key)) {
				text = text.replace(key, correctKeyMap.get(key));
				break;
			}
		}
		text = refinedSPR(text);
		return text;
	}

	private static ArrayList<String> modifyVer(ArrayList<String> textList) {
		HashMap<String, String> correctKeyMap = new HashMap<String, String>();
		correctKeyMap.put("commons.math3.", "commons.math.");
		correctKeyMap.put("commons.math4.", "commons.math.");
		correctKeyMap.put("commons.lang3.", "commons.lang.");
		correctKeyMap.put("commons.configuration2.", "commons.configuration.");
		correctKeyMap.put("commons.collections4.", "commons.collections.");
		correctKeyMap.put("org.springframework.jms.src.main.java.", "");
		for (int i = 0; i < textList.size(); i++) {
			String text = textList.get(i);
			Iterator<String> keyIter = correctKeyMap.keySet().iterator();
			while (keyIter.hasNext()) {
				String key = keyIter.next();
				if (text.contains(key)) {
					text = text.replace(key, correctKeyMap.get(key));
					break;
				}
			}
			text = refinedSPR(text);
			textList.set(i, text);
		}
		return textList;
	}

	private static HashMap<String, Double> modifyVerMap(HashMap<String, Double> textList) {
		HashMap<String, String> correctKeyMap = new HashMap<String, String>();
		correctKeyMap.put("commons.math3.", "commons.math.");
		correctKeyMap.put("commons.math4.", "commons.math.");
		correctKeyMap.put("commons.lang3.", "commons.lang.");
		correctKeyMap.put("commons.configuration2.", "commons.configuration.");
		correctKeyMap.put("commons.collections4.", "commons.collections.");
		correctKeyMap.put("org.springframework.jms.src.main.java.", "");
		Iterator<String> iter = textList.keySet().iterator();
		HashMap<String, Double> returnResult = new HashMap<String, Double>();
		while (iter.hasNext()) {
			String text = iter.next();
			double value = textList.get(text);
			String newText = text;
			Iterator<String> keyIter = correctKeyMap.keySet().iterator();
			while (keyIter.hasNext()) {
				String key = keyIter.next();
				if (text.contains(key)) {
					newText = text.replace(key, correctKeyMap.get(key));
					break;
				}
			}
			newText = refinedSPR(newText);
			returnResult.put(newText, value);
		}
		return returnResult;
	}

	private static HashMap<String, HashSet<String>> modifyVer(HashMap<String, HashSet<String>> fileMethodMap) {
		HashMap<String, String> correctKeyMap = new HashMap<String, String>();
		correctKeyMap.put("commons.math3.", "commons.math.");
		correctKeyMap.put("commons.math4.", "commons.math.");
		correctKeyMap.put("commons.lang3.", "commons.lang.");
		correctKeyMap.put("commons.configuration2.", "commons.configuration.");
		correctKeyMap.put("commons.collections4.", "commons.collections.");
		correctKeyMap.put("org.springframework.jms.src.main.java.", "");
		HashMap<String, HashSet<String>> returnMap = new HashMap<String, HashSet<String>>();
		Iterator<String> fileIter = fileMethodMap.keySet().iterator();
		while (fileIter.hasNext()) {
			String text = fileIter.next();
			Iterator<String> keyIter = correctKeyMap.keySet().iterator();
			String newText = text;
			while (keyIter.hasNext()) {
				String key = keyIter.next();
				if (newText.contains(key)) {
					newText = newText.replace(key, correctKeyMap.get(key));
					break;
				}
			}
			newText = refinedSPR(newText);
			returnMap.put(newText, fileMethodMap.get(text));
		}
		return returnMap;
	}

	public static List sortByValue(final Map map) {
		List<String> list = new ArrayList();
		list.addAll(map.keySet());
		Collections.sort(list, new Comparator() {
			public int compare(Object o1, Object o2) {
				Object v1 = map.get(o1);
				Object v2 = map.get(o2);
				return ((Comparable) v2).compareTo(v1);
			}
		});
//		     Collections.reverse(list); // 주석시 오름차
		return list;
	}

		
	public static HashMap<String, Double> evaluateFileLocBLU(ArrayList<String> fileList,
			HashMap<String, Double> searchResult, int topN, HashMap<String, Double> simBugScore) {
		String[] fields = { "pp-classes", "pp-methods", "pp-variables", "pp-comments" };
		HashMap<String, Double> evalResult = new HashMap<String, Double>();
		fileList = modifyVer(fileList);
		double rr = 0;
		double ap = 0;
		double top = 0;
		double found = 0;
		double topRank = 0;
		String firstFile = "";
		int i = 0;
		searchResult = norm(searchResult);
		Iterator<String> fileIter = searchResult.keySet().iterator();
		int simNum = 0;
		int comNum = 0;
		while (fileIter.hasNext()) {
			i++;
			String fileName = fileIter.next();
			double score = searchResult.get(fileName);
			double simiScore = 0;
			double stScore = 0;
			double comScore = 0;
			if (simBugScore.containsKey(fileName)) {
				simiScore = simBugScore.get(fileName);
				simNum++;
			}
			double finalScore = ((1 - 0.3) * score + 0.3 * simiScore);
			
			searchResult.replace(fileName, finalScore);
		}
//		System.out.println(simNum + " " + stNum + " " + comNum);
//		System.out.println(fileList);
//		System.out.println(searchResult);
		i = 0;
		Iterator<String> it = sortByValue(searchResult).iterator();
		while (it.hasNext()) {
			String searchFileName = it.next();
//			System.out.println(searchFileName+" "+searchResult.get(searchFileName));
			searchFileName = modifyVer(searchFileName);
			if (firstFile.equals(""))
				firstFile = searchFileName;

			double rank = i + 1.0;
			if (fileList.contains(searchFileName)) {
				found++;
				if (topRank == 0) {
					topRank = rank;
					rr = 1.0 / rank;
				}
				if (topN == 100000) {
					if (top == 0 && i < 5)
						top++;
				} else {
					if (top == 0 && i < topN)
						top++;
				}
				ap = ap + (found / rank);
			}
			i++;
		}
		ap = ap / fileList.size();
		if (!Double.isFinite(ap))
			ap = 0;
//		if (found == 0) {
//			System.out.println("SEARCHM: " + searchResult.keySet().iterator().next());
//			System.out.println("BUGGYM: " + fileList.get(0));
//		}
		if (topRank == 0)
			evalResult.put("topRank", 0.0);
		else
			evalResult.put("topRank", topRank);

		evalResult.put("rr" + topN, rr);
		evalResult.put("ap" + topN, ap);
		if (topN == 100000)
			evalResult.put("top5", top);
		else
			evalResult.put("top" + topN, top);
//		System.out.println(evalResult);
		return evalResult;
	}

	public static HashMap<String, Double> evaluateFileLocStructuredInfoForBLIA(ArrayList<String> fileList,
			HashMap<String, Double> searchResult, int topN, HashMap<String, Double> stackScore,
			HashMap<String, Double> simBugScore, HashMap<String, Double> commitScore, double alpha, double beta) {
		String[] fields = { "pp-classes", "pp-methods", "pp-variables", "pp-comments" };
		HashMap<String, Double> evalResult = new HashMap<String, Double>();
		fileList = modifyVer(fileList);
		double rr = 0;
		double ap = 0;
		double top = 0;
		double found = 0;
		double topRank = 0;
		String firstFile = "";
		int i = 0;
		searchResult = norm(searchResult);
		Iterator<String> fileIter = searchResult.keySet().iterator();
		int simNum = 0;
		int stNum = 0;
		int comNum = 0;
		while (fileIter.hasNext()) {
			i++;
			String fileName = fileIter.next();
			double score = searchResult.get(fileName);
			double simiScore = 0;
			double stScore = 0;
			double comScore = 0;
			if (simBugScore.containsKey(fileName)) {
				simiScore = simBugScore.get(fileName);
				simNum++;
			}
			if (stackScore.containsKey(fileName)) {
				stScore = stackScore.get(fileName);
				stNum++;
			}
			if (commitScore.containsKey(fileName)) {
				comScore = commitScore.get(fileName);
				comNum++;
			}
			double finalScore = (((1 - alpha) * score + alpha * simiScore) + stScore);
			if (comScore > 0)
				finalScore = (1 - beta) * finalScore + beta * comScore;
			searchResult.replace(fileName, finalScore);
		}
//		System.out.println(simNum + " " + stNum + " " + comNum);
//		System.out.println(fileList);
//		System.out.println(searchResult);
		i = 0;
		Iterator<String> it = sortByValue(searchResult).iterator();
		while (it.hasNext()) {
			String searchFileName = it.next();
//			System.out.println(searchFileName+" "+searchResult.get(searchFileName));
			searchFileName = modifyVer(searchFileName);
			if (firstFile.equals(""))
				firstFile = searchFileName;

			double rank = i + 1.0;
			if (fileList.contains(searchFileName)) {
				found++;
				if (topRank == 0) {
					topRank = rank;
					rr = 1.0 / rank;
				}
				if (topN == 100000) {
					if (top == 0 && i < 5)
						top++;
				} else {
					if (top == 0 && i < topN)
						top++;
				}
				ap = ap + (found / rank);
			}
			i++;
		}
		ap = ap / fileList.size();
		if (!Double.isFinite(ap))
			ap = 0;
//		if (found == 0) {
//			System.out.println("SEARCHM: " + searchResult.keySet().iterator().next());
//			System.out.println("BUGGYM: " + fileList.get(0));
//		}
		if (topRank == 0)
			evalResult.put("topRank", 0.0);
		else
			evalResult.put("topRank", topRank);

		evalResult.put("rr" + topN, rr);
		evalResult.put("ap" + topN, ap);
		if (topN == 100000)
			evalResult.put("top5", top);
		else
			evalResult.put("top" + topN, top);
//		System.out.println(evalResult);
		return evalResult;
	}

	private static HashMap<String, Double> norm(HashMap<String, Double> searchResult) {
		// TODO Auto-generated method stub
		HashMap<String, Double> data = (HashMap<String, Double>) searchResult.clone();
		Iterator<String> iter = searchResult.keySet().iterator();
		double max = 0;
		double min = 9999999;
		while (iter.hasNext()) {
			String key = iter.next();
			double value = searchResult.get(key);
			if (max < value)
				max = value;
			if (min > value)
				min = value;
		}
		iter = data.keySet().iterator();
		while (iter.hasNext()) {
			String key = iter.next();
			double value = searchResult.get(key);
			double score = (value - min) / (max - min);
			if (!Double.isFinite(value))
				score = 0;
			data.replace(key, score);
//			System.out.println(key+" "+score+" "+value+" "+max+" "+min);
		}
		return data;
	}

	public static HashMap<String, Double> evaluateFileLoc(ArrayList<String> fileList, ArrayList<String> searchResult,
			int topN) {
		HashMap<String, Double> evalResult = new HashMap<String, Double>();
		fileList = modifyVer(fileList);
		double rr = 0;
		double ap = 0;
		double top = 0;
		double found = 0;
		double topRank = 0;
		String firstFile = "";
		for (int i = 0; i < searchResult.size(); i++) {
			String searchFileName = searchResult.get(i);
			searchFileName = modifyVer(searchFileName);
			if (firstFile.equals(""))
				firstFile = searchFileName;

			double rank = i + 1.0;
			if (fileList.contains(searchFileName)) {
//				System.out.println(rank+" "+searchFileName);
				found++;
				if (topRank == 0) {
					topRank = rank;
					rr = 1.0 / rank;
				}
				if (topN == 100000) {
					if (top == 0 && i < 5)
						top++;
				} else {
					if (top == 0 && i < topN)
						top++;
				}
				ap = ap + (found / rank);
			}
		}
		ap = ap / fileList.size();
//		if (found == 0) {
//			System.out.println("SEARCH: " + firstFile);
//			if (fileList.size() > 0)
//				System.out.println("BUGGY: " + fileList.get(0));
//			else
//				System.out.println("NO BUGGY");
//		}
		if (!Double.isFinite(ap))
			ap = 0;
		if (topRank == 0)
			evalResult.put("topRank", 0.0);
		else
			evalResult.put("topRank", topRank);
		evalResult.put("rr" + topN, rr);
		evalResult.put("ap" + topN, ap);
		if (topN == 100000)
			evalResult.put("top5", top);
		else
			evalResult.put("top" + topN, top);
//		System.out.println(evalResult);
		return evalResult;
	}

	public static HashMap<String, Double> evaluateMethodLoc(HashMap<String, HashSet<String>> fileMethodMap,
			ArrayList<String> searchResult, int topN) {
		HashMap<String, Double> evalResult = new HashMap<String, Double>();
		fileMethodMap = modifyVer(fileMethodMap);
		int mthNum = 0;
		Iterator<String> mapIter = fileMethodMap.keySet().iterator();
		while(mapIter.hasNext()) {
			String fileName = mapIter.next();
			mthNum = mthNum + fileMethodMap.get(fileName).size();
		}
				
		double rr = 0;
		double ap = 0;
		double top = 0;
		double found = 0;
		double topRank = 0;
		String temp = "";
		for (int i = 0; i < searchResult.size(); i++) {
			String searchFileName = modifyVer(searchResult.get(i).split(":")[0]);
			String searchMethodName = searchResult.get(i).split(":")[1];
			if (temp.equals(""))
				temp = searchFileName + ":" + searchMethodName;
			double rank = i + 1.0;
			if (fileMethodMap.containsKey(searchFileName)) {
				HashSet<String> methods = fileMethodMap.get(searchFileName);
				if (methods.contains(searchMethodName)) {
					found++;
					if (topRank == 0) {
						topRank = rank;
						rr = 1.0 / rank;
					}
					if (topN == 100000) {
						if (top == 0 && i < 5)
							top++;
					} else {
						if (top == 0 && i < topN)
							top++;
					}
					ap = ap + (found / rank);
				}
			}
		}
		ap = ap / mthNum;
//		if(found == 0) {
//			System.out.println("SEARCHM: "+temp);
//			String key = fileMethodMap.keySet().iterator().next();
//			System.out.println("BUGGYM: "+fileMethodMap);
//		}

		if (!Double.isFinite(ap))
			ap = 0;
		if (topRank == 0)
			evalResult.put("topRank", 0.0);
		else
			evalResult.put("topRank", topRank);
		evalResult.put("rr" + topN, rr);
		evalResult.put("ap" + topN, ap);
		if (topN == 100000)
			evalResult.put("top5", top);
		else
			evalResult.put("top" + topN, top);
		return evalResult;
	}

	public static HashMap<String, Double> meanEval(HashMap<String, ArrayList<Double>> evalResultList) {
		HashMap<String, Double> result = new HashMap<String, Double>();
		Iterator<String> keyIter = evalResultList.keySet().iterator();
		while (keyIter.hasNext()) {
			String key = keyIter.next();
			ArrayList<Double> values = evalResultList.get(key);
			double mean = CalculatorUtil.average(values);
			result.put(key, mean);
			result.put(key + "-NUM", values.size() * 1.0);
		}

		return result;
	}
	public static HashMap<String, Double> evaluateFileLocBL(ArrayList<String> fileList, HashMap<String, Double> searchResult,
			int topN, HashMap<String, Double> simBugScore ) {
		HashMap<String, Double> evalResult = new HashMap<String, Double>();
		fileList = modifyVer(fileList);
		double rr = 0;
		double ap = 0;
		double top = 0;
		double found = 0;
		double topRank = 0;
		String firstFile = "";
		int i = 0;
		searchResult = norm(searchResult);
		Iterator<String> fileIter = searchResult.keySet().iterator();
		while (fileIter.hasNext()) {
			i++;
			String fileName = fileIter.next();
			double score = searchResult.get(fileName);
			double simiScore = 0;
			if (simBugScore.containsKey(fileName)) {
				simiScore = simBugScore.get(fileName);
			}
			
			double finalScore = (1 - 0.3)*score+0.3*simiScore;			
			searchResult.replace(fileName, finalScore);
		}
		
		i = 0;
		Iterator<String> it = sortByValue(searchResult).iterator();
		while (it.hasNext()) {
			String searchFileName = it.next();
//			System.out.println(searchFileName+" "+searchResult.get(searchFileName));
			searchFileName = modifyVer(searchFileName);
			if (firstFile.equals(""))
				firstFile = searchFileName;

			double rank = i + 1.0;
			if (fileList.contains(searchFileName)) {
				found++;
				if (topRank == 0) {
					topRank = rank;
					rr = 1.0 / rank;
				}
				if (topN == 100000) {
					if (top == 0 && i < 5)
						top++;
				} else {
					if (top == 0 && i < topN)
						top++;
				}
				ap = ap + (found / rank);
			}
			i++;
		}
		ap = ap / fileList.size();
		if (!Double.isFinite(ap))
			ap = 0;
//		if (found == 0) {
//			System.out.println("SEARCHM: " + searchResult.keySet().iterator().next());
//			System.out.println("BUGGYM: " + fileList.get(0));
//		}
		if (topRank == 0)
			evalResult.put("topRank", 0.0);
		else
			evalResult.put("topRank", topRank);

		evalResult.put("rr" + topN, rr);
		evalResult.put("ap" + topN, ap);
		if (topN == 100000)
			evalResult.put("top5", top);
		else
			evalResult.put("top" + topN, top);
//		System.out.println(evalResult);
		return evalResult;
	}

	public static HashMap<String, Double> evaluateFileLocBR(ArrayList<String> fileList, HashMap<String, Double> searchResult,
			int topN, HashMap<String, Double> simBugScore, HashMap<String, Double> stackScore) {
		HashMap<String, Double> evalResult = new HashMap<String, Double>();
		fileList = modifyVer(fileList);
		double rr = 0;
		double ap = 0;
		double top = 0;
		double found = 0;
		double topRank = 0;
		String firstFile = "";
		int i = 0;
		searchResult = norm(searchResult);
		Iterator<String> fileIter = searchResult.keySet().iterator();
		while (fileIter.hasNext()) {
			i++;
			String fileName = fileIter.next();
			double score = searchResult.get(fileName);
			double simiScore = 0;
			double stScore = 0;
			if (simBugScore.containsKey(fileName)) {
				simiScore = simBugScore.get(fileName);
			}
			if (stackScore.containsKey(fileName)) {
				stScore = stackScore.get(fileName);
			}
			double finalScore = (1 - 0.3)*score+0.3*simiScore + stScore;			
			searchResult.replace(fileName, finalScore);
		}
		
		i = 0;
		Iterator<String> it = sortByValue(searchResult).iterator();
		while (it.hasNext()) {
			String searchFileName = it.next();
//			System.out.println(searchFileName+" "+searchResult.get(searchFileName));
			searchFileName = modifyVer(searchFileName);
			if (firstFile.equals(""))
				firstFile = searchFileName;

			double rank = i + 1.0;
			if (fileList.contains(searchFileName)) {
				found++;
				if (topRank == 0) {
					topRank = rank;
					rr = 1.0 / rank;
				}
				if (topN == 100000) {
					if (top == 0 && i < 5)
						top++;
				} else {
					if (top == 0 && i < topN)
						top++;
				}
				ap = ap + (found / rank);
			}
			i++;
		}
		ap = ap / fileList.size();
		if (!Double.isFinite(ap))
			ap = 0;
//		if (found == 0) {
//			System.out.println("SEARCHM: " + searchResult.keySet().iterator().next());
//			System.out.println("BUGGYM: " + fileList.get(0));
//		}
		if (topRank == 0)
			evalResult.put("topRank", 0.0);
		else
			evalResult.put("topRank", topRank);

		evalResult.put("rr" + topN, rr);
		evalResult.put("ap" + topN, ap);
		if (topN == 100000)
			evalResult.put("top5", top);
		else
			evalResult.put("top" + topN, top);
//		System.out.println(evalResult);
		return evalResult;
	}

	public static HashMap<String, Double> evaluateFileLocAM(ArrayList<String> fileList,
			HashMap<String, Double> searchResult, int topN, HashMap<String, Double> simBugScore,
			HashMap<String, Double> commitScore) {
		String[] fields = { "pp-classes", "pp-methods", "pp-variables", "pp-comments" };
		HashMap<String, Double> evalResult = new HashMap<String, Double>();
		fileList = modifyVer(fileList);
		double rr = 0;
		double ap = 0;
		double top = 0;
		double found = 0;
		double topRank = 0;
		String firstFile = "";
		int i = 0;
		searchResult = norm(searchResult);
		Iterator<String> fileIter = searchResult.keySet().iterator();
		int simNum = 0;
		int stNum = 0;
		int comNum = 0;
		while (fileIter.hasNext()) {
			i++;
			String fileName = fileIter.next();
			double score = searchResult.get(fileName);
			double simiScore = 0;
			double comScore = 0;
			if (simBugScore.containsKey(fileName)) {
				simiScore = simBugScore.get(fileName);
				simNum++;
			}
			if (commitScore.containsKey(fileName)) {
				comScore = commitScore.get(fileName);
				comNum++;
			}
			double finalScore = (((1 - 0.3) * score + 0.3 * simiScore));
			if (comScore > 0)
				finalScore = (1 - 0.3) * finalScore + 0.3 * comScore;
			searchResult.replace(fileName, finalScore);
		}
//		System.out.println(simNum + " " + stNum + " " + comNum);
//		System.out.println(fileList);
//		System.out.println(searchResult);
		i = 0;
		Iterator<String> it = sortByValue(searchResult).iterator();
		while (it.hasNext()) {
			String searchFileName = it.next();
//			System.out.println(searchFileName+" "+searchResult.get(searchFileName));
			searchFileName = modifyVer(searchFileName);
			if (firstFile.equals(""))
				firstFile = searchFileName;

			double rank = i + 1.0;
			if (fileList.contains(searchFileName)) {
				found++;
				if (topRank == 0) {
					topRank = rank;
					rr = 1.0 / rank;
				}
				if (topN == 100000) {
					if (top == 0 && i < 5)
						top++;
				} else {
					if (top == 0 && i < topN)
						top++;
				}
				ap = ap + (found / rank);
			}
			i++;
		}
		ap = ap / fileList.size();
		if (!Double.isFinite(ap))
			ap = 0;
//		if (found == 0) {
//			System.out.println("SEARCHM: " + searchResult.keySet().iterator().next());
//			System.out.println("BUGGYM: " + fileList.get(0));
//		}
		if (topRank == 0)
			evalResult.put("topRank", 0.0);
		else
			evalResult.put("topRank", topRank);

		evalResult.put("rr" + topN, rr);
		evalResult.put("ap" + topN, ap);
		if (topN == 100000)
			evalResult.put("top5", top);
		else
			evalResult.put("top" + topN, top);
//		System.out.println(evalResult);
		return evalResult;
	}

}
