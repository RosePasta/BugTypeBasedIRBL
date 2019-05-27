package util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class CalculatorUtil {


	public static HashMap<String, Double> validate(HashMap<String, Double> results) {
		HashMap<String, Double> returnResult = results;
		Iterator<String> iter = returnResult.keySet().iterator();
		while (iter.hasNext()) {
			String key = iter.next();
			if (!Double.isFinite(returnResult.get(key)))
				returnResult.replace(key, 0.0);
		}

		return returnResult;
	}

	public static Double normMed(ArrayList<Double> list) {
		if (list.size() == 0)
			return 0.0;
		double med = med(list);
		double max = max(list);
		double min = min(list);
		return (med - min) / (max - min);
	}

	public static Double normAverage(ArrayList<Double> list) {
		if (list.size() == 0)
			return 0.0;
		double avg = sum(list) / (list.size() * 1.0);
		double max = max(list);
		double min = min(list);
		return (avg - min) / (max - min);
	}

	public static Double med(ArrayList<Double> list) {
		if (list.size() == 0)
			return 0.0;
		int index = list.size() / 2;
		return list.get(index);
	}

	public static Double sum(ArrayList<Double> list) {
		if (list.size() == 0)
			return 0.0;
		if(list == null)
			return 0.0;
		double sum = 0;
		for (int i = 0; i < list.size(); i++) {
			if (Double.isFinite(list.get(i)))
				sum = sum + list.get(i);
		}
		return sum;
	}

	public static Double dev(ArrayList<Double> list) {
		if (list.size() == 0)
			return 0.0;
		double avg = average(list);
		double dev = 0;
		for (int i = 0; i < list.size(); i++) {
			if (Double.isFinite(list.get(i)))
				dev = dev + (list.get(i) - avg) * (list.get(i) - avg);
		}
		return dev / (list.size() * 1.0);
	}

	public static Double min(ArrayList<Double> list) {
		if (list.size() == 0)
			return 0.0;
		double min = 99999999;
		for (int i = 0; i < list.size(); i++) {
			if (Double.isFinite(list.get(i)))
				if (list.get(i) < min)
					min = list.get(i);
		}
		return min;
	}

	public static Double max(ArrayList<Double> list) {
		if (list.size() == 0)
			return 0.0;
		double max = 0;
		for (int i = 0; i < list.size(); i++) {
			if (Double.isFinite(list.get(i)))
				if (list.get(i) > max)
					max = list.get(i);
		}
		return max;
	}

	public static Double average(ArrayList<Double> list) {
		if (list.size() == 0)
			return 0.0;
		double avg = sum(list) / (list.size() * 1.0);
		return avg;
	}

}
