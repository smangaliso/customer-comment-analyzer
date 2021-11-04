package com.ikhokha.techcheck;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;

public class Main {
	static final Map<String, Integer> totalResults = new HashMap<>();

	public static void main(String[] args) {
		

				
		File docPath = new File("docs");
		File[] commentFiles = docPath.listFiles((d, n) -> n.endsWith(".txt"));

		ExecutorService executorService = Executors.newFixedThreadPool(5);
		ArrayList<Future<Map<String, Integer>>>resultList = new ArrayList<Future<Map<String, Integer>>>();
		
		for (File commentFile : commentFiles) {
			
//			CommentAnalyzer commentAnalyzer = new CommentAnalyzer(commentFile);
//			Map<String, Integer> fileResults = commentAnalyzer.analyze();

			ZCommentAnalyzer zCommentAnalyzer = new ZCommentAnalyzer(commentFile);
			Future<Map<String, Integer>> result = executorService.submit(zCommentAnalyzer);
			resultList.add(result);
		}

		executorService.shutdown();

		resultList.forEach(result->{
			try {
				addReportResults(result.get(), totalResults);
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
		});

		System.out.println("RESULTS\n=======");
		totalResults.forEach((k,v) -> System.out.println(k + " : " + v));
	}
	
	/**
	 * This method adds the result counts from a source map to the target map 
	 * @param source the source map
	 * @param target the target map
	 */
	private static void addReportResults(Map<String, Integer> source, Map<String, Integer> target) {

		for (Map.Entry<String, Integer> entry : source.entrySet()) {
			if(!target.containsKey(entry.getKey())){
				target.put(entry.getKey(), entry.getValue());
				continue;
			}
			target.put(entry.getKey(), target.get(entry.getKey()) + entry.getValue());
		}
		
	}

}

class ZCommentAnalyzer implements Callable<Map<String, Integer>>{
	File filepath;

	public ZCommentAnalyzer(final File filepath){
		this.filepath = filepath;
	}

	@Override
	public Map<String, Integer> call() throws Exception {
		CommentAnalyzer commentAnalyzer = new CommentAnalyzer(filepath);
		return commentAnalyzer.analyze();
	}
}
