package com.ikhokha.techcheck;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

public class CommentAnalyzer {
	private final File file;
	private final Map<String, Predicate<String>> metricMap = new HashMap<>();
	private String line;
	
	public CommentAnalyzer(File file) {
		this.file = file;

		this.initMetric();
	}

	private void initMetric(boolean isCaseSensitive){
		// "SHORTER_THAN_15"
		metricMap.put("SHORTER_THAN_15", (lineStr)-> lineStr.length() < 15);

		// "MOVER_MENTIONS"
		metricMap.put("MOVER_MENTIONS", (lineStr)->{
			lineStr = isCaseSensitive ? lineStr : lineStr.toLowerCase();
			return lineStr.contains("mover");
		});

		// SHAKER_MENTIONS
		metricMap.put("SHAKER_MENTIONS", (lineStr)->{
			lineStr = isCaseSensitive ? lineStr : lineStr.toLowerCase();
			return lineStr.contains("shaker");
		});

		// QUESTIONS
		metricMap.put("QUESTIONS", (lineStr)-> lineStr.contains("?"));

		// SPAM
		metricMap.put("SPAM", (lineStr)-> lineStr.matches(".*http(s)?://.*"));
	}

	private void initMetric(){
		initMetric(false);
	}
	
	public Map<String, Integer> analyze() {
		
		Map<String, Integer> resultsMap = new HashMap<>();

		try (BufferedReader reader = new BufferedReader(new FileReader(file))) {

			while ((line = reader.readLine()) != null) {

				// Test Each Metric
				this.metricMap.entrySet().parallelStream().forEach(entry->{
					String key = entry.getKey();
					Predicate<String> predicate = entry.getValue();

					if(predicate.test(line))
						incOccurrence(resultsMap, key);
					else
						resultsMap.putIfAbsent(key, 0);
				});

//				isLessThan15 = line.length() < 15;
//				hasMover = isCaseSensitive ? line.contains("Mover") : line.toLowerCase().contains("mover");
//				hasShaker= isCaseSensitive ? line.contains("Shaker") : line.toLowerCase().contains("shaker");
//				hasQuestion = line.contains("?");
//				isSpam = line.matches(".*http(s)?://.*");

//				if(isLessThan15)
//					incOccurrence(resultsMap, "SHORTER_THAN_15");
//
//				if(hasMover)
//					incOccurrence(resultsMap, "MOVER_MENTIONS");
//
//				if(hasShaker)
//					incOccurrence(resultsMap, "SHAKER_MENTIONS");
//
//				if(hasQuestion)
//					incOccurrence(resultsMap, "QUESTIONS");
//
//				if(isSpam)
//					incOccurrence(resultsMap, "SPAM");

			}
			
		} catch (FileNotFoundException e) {
			System.out.println("File not found: " + file.getAbsolutePath());
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("IO Error processing file: " + file.getAbsolutePath());
			e.printStackTrace();
		}
		
		return resultsMap;
		
	}
	
	/**
	 * This method increments a counter by 1 for a match type on the countMap. Uninitialized keys will be set to 1
	 * @param countMap the map that keeps track of counts
	 * @param key the key for the value to increment
	 */
	private void incOccurrence(Map<String, Integer> countMap, String key) {
		
		countMap.putIfAbsent(key, 0);
		countMap.put(key, countMap.get(key) + 1);
	}

}
