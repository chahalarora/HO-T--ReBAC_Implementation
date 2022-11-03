package ca.ucalgary.ispia.graphpatterns.tests;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.time.temporal.ValueRange;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang3.tuple.Triple;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ca.ucalgary.ispia.graphpatterns.Driver;
import ca.ucalgary.ispia.graphpatterns.gpchecker.opt.impl.DiscoverablePeriodFinderCBJ;
import ca.ucalgary.ispia.graphpatterns.gpchecker.opt.impl.DiscoverablePeriodFinderWithDbAccess;
import ca.ucalgary.ispia.graphpatterns.gpchecker.opt.impl.OfficialPeriodFinder;
import ca.ucalgary.ispia.graphpatterns.graph.AlgorithmType;
import ca.ucalgary.ispia.graphpatterns.graph.ErdosRenyiGraphCreator;
import ca.ucalgary.ispia.graphpatterns.graph.GraphPattern;
import ca.ucalgary.ispia.graphpatterns.graph.MyEdge;
import ca.ucalgary.ispia.graphpatterns.graph.MyNode;
import ca.ucalgary.ispia.graphpatterns.graph.RelType;
import ca.ucalgary.ispia.graphpatterns.util.Pair;
import ca.ucalgary.ispia.policy.opt.PolicyExecute;
import edu.stanford.nlp.util.Interval;
import edu.stanford.nlp.util.IntervalTree;

public class EvaluateDiscoverablePeriodFinder {
	
	public static GraphDatabaseService graphDb = null;
	
	static String propFileLocation= "./properties/Evaluate.properties";
	
	public Set<Triple<Long, Long, Interval>> sqlUpdateTripleSet =  new HashSet<>();	
	
	public static AlgorithmType algorithm = null;
	
	public static long timeoutlimit = 60001l;
	
	
	/**
	 * The main control for specifying tasks.
	 * @param args
	 */
	public static void main(String[] args){	
		
		Driver d = new Driver();
		graphDb = d.getGraphDb("graph.db");
		
		try {
			
			
			File file = new File(propFileLocation);
			InputStream in = new FileInputStream(file);
		    Properties properties = new Properties();
		    properties.load(in);
		    
		    int evaluationType = Integer.parseInt(properties.getProperty("evaluationType"));
		    String algorithmType = properties.getProperty("algorithm").toString();
		    int numberofIteration = Integer.parseInt(properties.getProperty("numberofIteration"));
		    int numberofNodesInGP = Integer.parseInt(properties.getProperty("numberofNodesInGP"));
		    int snapshotTimePoint =	Integer.parseInt(properties.getProperty("snapshotTimePoint"));
		    int numberofNodeInNeo4jGraphDatabse = Integer.parseInt(properties.getProperty("numberofNodeInNeo4jGraphDatabse"));
		    int historyEndTime = Integer.parseInt(properties.getProperty("historyEndTime")); 
		    
		    if(algorithmType.equalsIgnoreCase("FC")) {
		    	algorithm = AlgorithmType.FC;
		    
		    }else if(algorithmType.equalsIgnoreCase("FC_CBJ")) {
		    	algorithm = AlgorithmType.FC_CBJ;
		    	
		    } else if(algorithmType.equalsIgnoreCase("FC_LBJ")){
		    	algorithm = AlgorithmType.FC_LBJ;
		    
		    } else if(algorithmType.equalsIgnoreCase("FC_LBJ_Improved")){
		    	algorithm = AlgorithmType.FC_LBJ_Improved;
		    }
		    
		    EvaluateDiscoverablePeriodFinder evaluateDiscoverablePeriodFinder = new EvaluateDiscoverablePeriodFinder();		    
		    	
		    if(evaluationType == 0) {
		    	
		    	evaluateDiscoverablePeriodFinder.createFilesForGPInfoPair(numberofIteration, numberofNodeInNeo4jGraphDatabse, numberofNodesInGP, snapshotTimePoint, historyEndTime);
		    
		    } else if(evaluationType == 1) {
		    	
		    	EvaluateDiscoverablePeriodFinder evaluateDiscoverablePeriodFinderFileRead = new EvaluateDiscoverablePeriodFinder();
		    	Set<Pair<GraphPattern, Map<MyNode, Node>>> gpInfoPairSet = evaluateDiscoverablePeriodFinderFileRead.readGPInfoPairFromFile(numberofIteration, numberofNodeInNeo4jGraphDatabse, historyEndTime, numberofNodesInGP);
		    	evaluateDiscoverablePeriodFinder.evaluateDiscoverblePeriodFinderTimePointPresent(gpInfoPairSet, numberofIteration, snapshotTimePoint);
		    	
		    } else if(evaluationType == 2) {
		    	
		    	evaluateDiscoverablePeriodFinder.createFilesForRandomGPInfoPair(numberofIteration, numberofNodeInNeo4jGraphDatabse, numberofNodesInGP, snapshotTimePoint, historyEndTime);
		    
		    } else if(evaluationType == 3) {
		    	
		    	EvaluateDiscoverablePeriodFinder evaluateDiscoverablePeriodFinderFileRead = new EvaluateDiscoverablePeriodFinder();
		    	Set<Pair<GraphPattern, Map<MyNode, Node>>> gpInfoPairSet = evaluateDiscoverablePeriodFinderFileRead.readGPInfoPairFromFile(numberofIteration, numberofNodeInNeo4jGraphDatabse, historyEndTime, numberofNodesInGP);
		    	evaluateDiscoverablePeriodFinder.evaluateDiscoverblePeriodFinder(gpInfoPairSet, numberofIteration);
		    	
		    }
		    
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		graphDb.shutdown();
	}
	
	
	public void evaluateDiscoverblePeriodFinderTimePointPresent(Set<Pair<GraphPattern, Map<MyNode, Node>>> gpInfoPairSet, int numberofIteration, int snapshotTimePoint) throws IOException {	    
	    
		List<Long> totalTimeArray = new ArrayList<Long>();
		List<Long> totalTimeArrayPerDiscoverablePeriod = new ArrayList<Long>();
		List<Integer> totalDiscoverablePeriods = new ArrayList<Integer>();
		long totalTimeForRuns = 0;
		long totalTimeForRunsTimeoutInclude = 0;
		double averegeTimeForRun = 0;
		double averegeTimeForRunTimeoutInclude = 0;
		
		Iterator gpInfoPairSetIterator = gpInfoPairSet.iterator();
		
    	for (int i= 0; i< numberofIteration; i++) {
    			
    		//reading graph pattern info pair from file and running experiments.		
			Pair<GraphPattern, Map<MyNode, Node>> gpInfoPairOuput = (Pair<GraphPattern, Map<MyNode, Node>>) gpInfoPairSetIterator.next(); 
//    		EvaluateDiscoverablePeriodFinder evaluateDiscoverablePeriodFinder = new EvaluateDiscoverablePeriodFinder();	
//    		evaluateDiscoverablePeriodFinder.
    		
    		discoverblePeriodTimePointPresentTest(gpInfoPairOuput, snapshotTimePoint, totalTimeArray, totalTimeArrayPerDiscoverablePeriod, totalDiscoverablePeriods);
    	}

		
		for(int i=0; i< totalTimeArray.size(); i++) {
			totalTimeForRuns = totalTimeForRuns + totalTimeArray.get(i);
		}
		averegeTimeForRun = totalTimeForRuns/ (float) totalTimeArray.size();
		
		long totalTimeForRunsPerDiscoverablePeriod = 0;
		for(int i=0; i< totalTimeArrayPerDiscoverablePeriod.size(); i++) {
			totalTimeForRunsPerDiscoverablePeriod = totalTimeForRunsPerDiscoverablePeriod + totalTimeArrayPerDiscoverablePeriod.get(i);
		}
//		double averegeTimeForRunPerDiscoverablePeriod = totalTimeForRunsPerDiscoverablePeriod/ (float) totalTimeArrayPerDiscoverablePeriod.size();
		
		long numberOfDiscoverablePeriods = 0;
		for(int i=0; i< totalDiscoverablePeriods.size(); i++) {
			numberOfDiscoverablePeriods = numberOfDiscoverablePeriods + totalDiscoverablePeriods.get(i);
		}
		
		totalTimeForRunsTimeoutInclude = totalTimeForRuns + (numberofIteration - totalTimeArray.size())*this.timeoutlimit;
		averegeTimeForRunTimeoutInclude = totalTimeForRunsTimeoutInclude/ (float) numberofIteration;
		
		double averegeDiscoverablePeriods = numberOfDiscoverablePeriods/ (float) totalDiscoverablePeriods.size();
		
		
		double confidenceLevel95Percent = 1.960;
		double confidenceLevel90Percent = 1.645;
//			double confidenceLevel80Percent = 1.282;
//		double[] confidence95Percent = confidenceIntervalCalculator(totalTimeArray, confidenceLevel95Percent);
		double[] confidence90Percent = confidenceIntervalCalculator(totalTimeArray, confidenceLevel90Percent);
//		double[] confidence95PercentPerDiscoverablePeriod = confidenceIntervalCalculator(totalTimeArrayPerDiscoverablePeriod, confidenceLevel95Percent);
//		double[] confidence90PercentPerDiscoverablePeriod = confidenceIntervalCalculator(totalTimeArrayPerDiscoverablePeriod, confidenceLevel90Percent);
//			double[] confidence80Percent = confidenceIntervalCalculator(totalTimeArray, confidenceLevel80Percent);
		
		System.out.println("Total time array size: " + totalTimeArray.size());
		System.out.println("Algorithm: " + algorithm);
//		System.out.println("Number of nodes in graph databse: " + numberofNodeInNeo4jGraphDatabse);
//		System.out.println("History graph time interval: " + historyEndTime);
//		System.out.println("Number of nodes in GP: " + numberofNodesinGP);
		System.out.println("Total time: " + averegeTimeForRun);
		System.out.println("Total time timeout include: " + averegeTimeForRunTimeoutInclude);
		

		double averegeTrimmedTimeForRun = trimRunTime(totalTimeArray, 10);
		System.out.println("Total trimmed time: " + averegeTrimmedTimeForRun);
		
//		System.out.println("Average time: " + averegeTimeForRunPerDiscoverablePeriod);
		System.out.println("Number of discoverable periods: " + averegeDiscoverablePeriods);
//		System.out.println("Confidence95Percent: " + "[" + confidence95Percent[0] + ", " + confidence95Percent[1]+ "]");
		System.out.println("Confidence90Percent: " + "[" + confidence90Percent[0] + ", " + confidence90Percent[1]+ "]");
//		System.out.println("Confidence95Percent per discoverable period: " + "[" + confidence95PercentPerDiscoverablePeriod[0] + ", " + confidence95PercentPerDiscoverablePeriod[1]+ "]");
//		System.out.println("Confidence90Percent per discoverable period: " + "[" + confidence90PercentPerDiscoverablePeriod[0] + ", " + confidence90PercentPerDiscoverablePeriod[1]+ "]");
//			System.out.println("Confidence80Percent: " + "[" + confidence80Percent[0] + ", " + confidence80Percent[1]+ "]");
		System.out.println("\n");
		System.exit(0);
	}
	
	public static double trimRunTime( List<Long> totalTimeArray, int percent) {
	    
		List<Long> resultTimeArray = new ArrayList<>();
		
		if ( percent < 0 || 100 < percent ) {
	        throw new IllegalArgumentException("Unexpected value: " + percent);
	    }

	    if ( 0 == totalTimeArray.size() ) {
	        return Double.NaN;
	    }

	    final int n = totalTimeArray.size();
	    final int k = (int) Math.round(n * ( percent / 100.0 ) / 2.0); // Check overflow
	    
	    Collections.sort(totalTimeArray);
	    
	    for(int i=0; i<totalTimeArray.size(); i++) {
	    	
	    	if(!(i<k || i> n-k)) {
	    		resultTimeArray.add(totalTimeArray.get(i));
	    	}
	    	
	    	
	    }
	    
	    
	    long totalTrimmedTimeForRuns = 0;
		for(int i=0; i< resultTimeArray.size(); i++) {
			totalTrimmedTimeForRuns = totalTrimmedTimeForRuns + resultTimeArray.get(i);
		}
		
		double averegeTrimmedTimeForRun = totalTrimmedTimeForRuns/ (float) resultTimeArray.size();

		return averegeTrimmedTimeForRun;

	}
	
	public void evaluateDiscoverblePeriodFinder(Set<Pair<GraphPattern, Map<MyNode, Node>>> gpInfoPairSet, int numberofIteration) throws IOException {
		
		List<Long> totalTimeArray = new ArrayList<Long>();
		List<Integer> totalDiscoverablePeriods = new ArrayList<Integer>();
		
		Iterator gpInfoPairSetIterator = gpInfoPairSet.iterator();
		
    	for (int i= 0; i< numberofIteration; i++) {
    			
    		//reading graph pattern info pair from file and running experiments.		
			Pair<GraphPattern, Map<MyNode, Node>> gpInfoPairOuput = (Pair<GraphPattern, Map<MyNode, Node>>) gpInfoPairSetIterator.next(); 
    		EvaluateDiscoverablePeriodFinder evaluateDiscoverablePeriodFinder = new EvaluateDiscoverablePeriodFinder();	
    		evaluateDiscoverablePeriodFinder.discoverblePeriodTest(gpInfoPairOuput, totalTimeArray, totalDiscoverablePeriods);
    	}

		
		long totalTimeForRuns = 0;
		for(int i=0; i< totalTimeArray.size(); i++) {
			totalTimeForRuns = totalTimeForRuns + totalTimeArray.get(i);
		}
		double averegeTimeForRun = totalTimeForRuns/ (float) totalTimeArray.size();
		
		long numberOfDiscoverablePeriods = 0;
		for(int i=0; i< totalDiscoverablePeriods.size(); i++) {
			numberOfDiscoverablePeriods = numberOfDiscoverablePeriods + totalDiscoverablePeriods.get(i);
		}
		double averegeDiscoverablePeriods = numberOfDiscoverablePeriods/ (float) totalDiscoverablePeriods.size();
		
		
//		double confidenceLevel95Percent = 1.960;
//		double confidenceLevel90Percent = 1.645;
//			double confidenceLevel80Percent = 1.282;
//		double[] confidence95Percent = confidenceIntervalCalculator(totalTimeArray, confidenceLevel95Percent);
//		double[] confidence90Percent = confidenceIntervalCalculator(totalTimeArray, confidenceLevel90Percent);
//		double[] confidence95PercentPerDiscoverablePeriod = confidenceIntervalCalculator(totalTimeArrayPerDiscoverablePeriod, confidenceLevel95Percent);
//		double[] confidence90PercentPerDiscoverablePeriod = confidenceIntervalCalculator(totalTimeArrayPerDiscoverablePeriod, confidenceLevel90Percent);
//			double[] confidence80Percent = confidenceIntervalCalculator(totalTimeArray, confidenceLevel80Percent);
		
		System.out.println("Total time array size: " + totalTimeArray.size());
		System.out.println("Algorithm: " + algorithm);
//		System.out.println("Number of nodes in graph databse: " + numberofNodeInNeo4jGraphDatabse);
//		System.out.println("History graph time interval: " + historyEndTime);
//		System.out.println("Number of nodes in GP: " + numberofNodesinGP);
		System.out.println("Total time: " + averegeTimeForRun);
		System.out.println("Number of discoverable periods: " + averegeDiscoverablePeriods);
//		System.out.println("Confidence95Percent: " + "[" + confidence95Percent[0] + ", " + confidence95Percent[1]+ "]");
//		System.out.println("Confidence90Percent: " + "[" + confidence90Percent[0] + ", " + confidence90Percent[1]+ "]");
//		System.out.println("Confidence95Percent per discoverable period: " + "[" + confidence95PercentPerDiscoverablePeriod[0] + ", " + confidence95PercentPerDiscoverablePeriod[1]+ "]");
//		System.out.println("Confidence90Percent per discoverable period: " + "[" + confidence90PercentPerDiscoverablePeriod[0] + ", " + confidence90PercentPerDiscoverablePeriod[1]+ "]");
//			System.out.println("Confidence80Percent: " + "[" + confidence80Percent[0] + ", " + confidence80Percent[1]+ "]");
		System.out.println("\n");
	}
	
	public void createFilesForGPInfoPair(int numberofIteration, int numberofNodeInNeo4jGraphDatabse, int numberofNodesInGP, int snapshotTimePoint, int historyEndTime) throws IOException {
				
	    // running evaluation for range of number of nodes in graph pattern.
		File gpInfoFile = new File("GPInforPair/"+ numberofNodeInNeo4jGraphDatabse+"Nodes"+historyEndTime+"EndTime"+"/GPInfoPairN"+numberofNodesInGP+".txt");		
		FileOutputStream gpInfoPairfile = new FileOutputStream(gpInfoFile);
		ObjectOutputStream gpInfoPairObjecStream = new ObjectOutputStream(gpInfoPairfile);
				    	
		for(int i =0 ; i< numberofIteration; i++) {	
			EvaluateDiscoverablePeriodFinder discoverablePeriodGPInfoPairFile = new EvaluateDiscoverablePeriodFinder();
			Pair<GraphPattern, Map<MyNode, Long>> gpInfoPairIn = discoverablePeriodGPInfoPairFile.writeGPInfoPairToFile(numberofNodesInGP, snapshotTimePoint);
			gpInfoPairObjecStream.writeObject(gpInfoPairIn);
			System.out.println("Number of GP created: " + i);
		}
		
		gpInfoPairObjecStream.close();
		gpInfoPairfile.close();
		
		System.out.println("File created for GP size: " + numberofNodesInGP);

	}
	
	private void createFilesForRandomGPInfoPair(int numberofIteration, int numberofNodeInNeo4jGraphDatabse, int numberofNodesInGP, int snapshotTimePoint, int historyEndTime) throws IOException {
		
	    // running evaluation for range of number of nodes in graph pattern.
    	FileOutputStream gpInfoPairfile = new FileOutputStream(new File("GPInforPair/"+ numberofNodeInNeo4jGraphDatabse+"Nodes"+historyEndTime+"EndTime"+"/GPInfoPairN"+numberofNodesInGP+".txt"));
		ObjectOutputStream gpInfoPairObjecStream = new ObjectOutputStream(gpInfoPairfile);
				    	
		for(int i =0 ; i< numberofIteration; i++) {	
			EvaluateDiscoverablePeriodFinder discoverablePeriodGPInfoPairFile = new EvaluateDiscoverablePeriodFinder();
			Pair<GraphPattern, Map<MyNode, Long>> gpInfoPairIn = discoverablePeriodGPInfoPairFile.writeRandomGPInfoPairToFile(numberofNodesInGP);
			gpInfoPairObjecStream.writeObject(gpInfoPairIn);
			System.out.println("Number of GP created: " + i);
		}
		
		gpInfoPairObjecStream.close();
		gpInfoPairfile.close();
		
		System.out.println("File created for GP size: " + numberofNodesInGP);

	}
	
	private Pair<GraphPattern, Map<MyNode, Node>> generateRandomGraphPatternForTimePointPresent(int numberofNodeInGP, int snapshotTimePoint) {
		
		ErdosRenyiGraphCreator randomHistoryGraphCreator = new ErdosRenyiGraphCreator(graphDb);
		
		Pair<GraphPattern, Map<MyNode, Node>> gpInfoPair = null;
		
		Pair<Set<Node>, Set<Relationship>> historyGraphSnapshot = null;
		
		try (Transaction tx = graphDb.beginTx()){
			
			historyGraphSnapshot = randomHistoryGraphCreator.getHistoryGraphSnapshot(snapshotTimePoint);
			
			RandomGPCreator randomGPCreator = new RandomGPCreator(historyGraphSnapshot, new Random(), numberofNodeInGP, 1);
			
			gpInfoPair = randomGPCreator.getRandomGP();
			
			tx.success();
		}

		return gpInfoPair;
	}
	
	
	public void discoverblePeriodTimePointPresentTest(Pair<GraphPattern, Map<MyNode, Node>> gpInfoPair, int snapshotTimePoint, List<Long> totalTimeArray, List<Long> totalTimeArrayPerDiscoverablePeriod, List<Integer> totalDiscoverablePeriod ) {
			
		long endTime   = 0;
		long timeForRun = 0;
//		int failedCases = 0;
		
			
		DiscoverablePeriodFinderWithDbAccess discoverablePeriodFinder = new DiscoverablePeriodFinderWithDbAccess(graphDb, algorithm);		
		
		Terminator terminate = new Terminator(discoverablePeriodFinder);
		
//		long timeoutlimit = 60001l;
		
//		Terminator term = new Terminator();
		terminate.terminateAfter(timeoutlimit);
		
		long startTime = System.currentTimeMillis();
		IntervalTree discoverablePeriods = discoverablePeriodFinder.FCLBJTemporalInit(graphDb, gpInfoPair.first, gpInfoPair.second);
		
		if(!algorithm.equals(AlgorithmType.FC_LBJ_Improved)) {	
			OfficialPeriodFinder officialPeriodFinder = new OfficialPeriodFinder();
			discoverablePeriods = officialPeriodFinder.findOfficialPeriods(discoverablePeriods);
		}
		
		endTime   = System.currentTimeMillis();
		timeForRun = endTime - startTime;
		
		List<Long> nodeIdList = new ArrayList();
		
		for(Node node: gpInfoPair.second.values()) {
			nodeIdList.add(node.getId());
		}
		
			
		Iterator discoverablePeriodIterator = discoverablePeriods.iterator();
		while(discoverablePeriodIterator.hasNext()) {
			Interval discoverablePeriod = (Interval) discoverablePeriodIterator.next();
			Triple<Long, Long, Interval> sqlUpdateTriple = Triple.of(nodeIdList.get(0), nodeIdList.get(1), discoverablePeriod);
    		this.sqlUpdateTripleSet.add(sqlUpdateTriple);
		}
		
		if(discoverablePeriodFinder.killed) {
			
			System.out.print("KILLED ");
//			totalTimeArray.add(timeoutlimit);
//			failedCases++;
			
		} else {
			
			totalTimeArray.add(timeForRun);
			totalDiscoverablePeriod.add(discoverablePeriods.size());
		}
		
		
//		if(timeForRun < 1000) {
//		if(timeForRun < 2400) {
		
//			totalTimeArrayPerDiscoverablePeriod.add(timeForRun/discoverablePeriods.size());
//			totalTimeArray.add(timeForRun);
//			totalDiscoverablePeriod.add(discoverablePeriods.size());
		
//		}
		
		System.out.println("Pass, Time to run: "+ timeForRun);
//		Sy
//		System.out.println("Time to run: "+);

//		if(snapshotTimePointExists) {
//				
//			System.out.println("Pass");
//			totalTimeArrayPerDiscoverablePeriod.add(timeForRun/discoverablePeriods.size());
//			totalTimeArray.add(timeForRun);
//			totalDiscoverablePeriod.add(discoverablePeriods.size());
//			
//		}else {
//			
//			System.out.println("Fail");			
//			System.out.println("DP: " + discoverablePeriods);
//			System.out.println("DP: " + gpInfoPair);
//			
//		}	
	}
	
	public class Terminator {

		private Killable obj;
		private ExecutorService service;

		/**
		 * Constructor. 
		 * @param obj The object to kill
		 */
		Terminator( Killable obj ) {
			this.obj = obj;
			service = null;
		}

		/**
		 * Removes the reference to the object
		 */
		public void nullifyObj(){
			this.obj = null;
		}

		/**
		 * Terminates the process after the specified time.
		 * @param millis The specified time to terminate the process.
		 */
		public void terminateAfter( final long millis ) {
			service = Executors.newSingleThreadExecutor();
			service.submit( new Runnable() {
				@Override
				public void run() {

					//Make the threat sleep for the specified time
					long startTime = System.currentTimeMillis();
					do {
						try {
							Thread.sleep( millis );
						} catch ( InterruptedException ignored ){
							return;
						}
					}
					while ((System.currentTimeMillis() - startTime) < millis );

					//Kill the process if we still have reference to the object
					if (obj != null) {
						obj.kill();
					}

				}
			}
					);
		}

		//Stops this service.
		public void stop(){
			service.shutdownNow();
		}
	}
	
	private void discoverblePeriodTest(Pair<GraphPattern, Map<MyNode, Node>> gpInfoPair, List<Long> totalTimeArray, List<Integer> totalDiscoverablePeriod ) {
		
		long endTime   = 0;
		long timeForRun = 0;
			
		DiscoverablePeriodFinderWithDbAccess discoverablePeriodFinder = new DiscoverablePeriodFinderWithDbAccess(graphDb, algorithm);		
		
		long startTime = System.currentTimeMillis();
		IntervalTree discoverablePeriods = discoverablePeriodFinder.FCLBJTemporalInit(graphDb, gpInfoPair.first, gpInfoPair.second);
		
		if(!algorithm.equals(AlgorithmType.FC_LBJ_Improved)) {	
			OfficialPeriodFinder officialPeriodFinder = new OfficialPeriodFinder();
			discoverablePeriods = officialPeriodFinder.findOfficialPeriods(discoverablePeriods);
		}
		
		endTime   = System.currentTimeMillis();
		timeForRun = endTime - startTime;
		
		System.out.println("Pass");
		
		if(discoverablePeriods.size() == 0) {
		
			totalTimeArray.add(timeForRun);
		}
		totalDiscoverablePeriod.add(discoverablePeriods.size());
	
	}
	
	
	private double[] confidenceIntervalCalculator(List<Long> givenNumbers, double confidenceLevel) {

	    // calculate the mean value (= average)
	    double sum = 0.0;
	    for (double num : givenNumbers) {
	        sum += num;
	    }
	    double mean = sum / givenNumbers.size();

	    // calculate standard deviation
	    double squaredDifferenceSum = 0.0;
	    for (double num : givenNumbers) {
	        squaredDifferenceSum += (num - mean) * (num - mean);
	    }
	    double variance = squaredDifferenceSum / givenNumbers.size();
	    double standardDeviation = Math.sqrt(variance);

	    double temp = confidenceLevel * standardDeviation / Math.sqrt(givenNumbers.size());
	    
	    double confidenceIntervalLow = mean - temp;
	    double confidenceIntervalHigh = mean + temp;
	    if(confidenceIntervalLow < 0) {
	    	confidenceIntervalLow = 0;
	    }
	    
	    return new double[]{confidenceIntervalLow, confidenceIntervalHigh};
	}
	
	
	public Pair<GraphPattern, Map<MyNode, Long>> writeGPInfoPairToFile(int numberofNodeInGraphPattern, int snapshotTimePoint) throws IOException {
	    	    
	    Pair<GraphPattern, Map<MyNode, Node>> gpInfoPair = generateRandomGraphPatternForTimePointPresent(numberofNodeInGraphPattern, snapshotTimePoint);
	    
	    Map<MyNode, Long> vertexNodeMap = new HashMap<MyNode, Long>();
	    
	    for(MyNode myNode: gpInfoPair.second.keySet()) {
	    	vertexNodeMap.put(myNode, gpInfoPair.second.get(myNode).getId());
	    }
	    
		Pair<GraphPattern, Map<MyNode, Long>> gpInfoPairIn = new Pair(gpInfoPair.first, vertexNodeMap);

	    return gpInfoPairIn;
	}
	
	public Set<Pair<GraphPattern, Map<MyNode, Node>>> readGPInfoPairFromFile(int numberofIteration, int numberofNodeInNeo4jGraphDatabse, int historyEndTime, int numberofNodesInGP) throws IOException {
		
    	FileInputStream fileInput = new FileInputStream(new File("GPInforPair/"+ numberofNodeInNeo4jGraphDatabse+"Nodes"+historyEndTime+"EndTime"+"/GPInfoPairN"+numberofNodesInGP+".txt"));
		ObjectInputStream objectStreamInput = new ObjectInputStream(fileInput);
		
		Set<Pair<GraphPattern, Map<MyNode, Node>>> gpInfoPairSet = new HashSet<>();
		
    	for (int i= 0; i< numberofIteration; i++) {

	//		 reading graph pattern info pair from file and running experiments.
			Pair<GraphPattern, Map<MyNode, Long>> gpInfoPairOut = null;
			try {
				gpInfoPairOut = (Pair<GraphPattern, Map<MyNode, Long>>) objectStreamInput.readObject();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
				
			Map outMap = new HashMap();
			try (Transaction tx = graphDb.beginTx()){
										
				for(MyNode outMyNode: gpInfoPairOut.second.keySet()) {
					Node outNode = graphDb.getNodeById(gpInfoPairOut.second.get(outMyNode));
					outMap.put(outMyNode, outNode);
				}

				tx.success();
			}
			
			Pair<GraphPattern, Map<MyNode, Node>> gpInfoPairOuput = new Pair (gpInfoPairOut.first, outMap);
			
			gpInfoPairSet.add(gpInfoPairOuput);
			
		}
    	objectStreamInput.close();
		fileInput.close();
		
		return gpInfoPairSet;
	}
	
	
	public Pair<GraphPattern, Map<MyNode, Long>>  writeRandomGPInfoPairToFile(int numberofNodeInGraphPattern) {
		
		Node startNode = null;
		Node endNode = null;
		
		try (Transaction tx = graphDb.beginTx()){
			
			Random random = new Random();
			
			int startNodeRandomIndex = random.nextInt(5881);
			Iterator<Node> startNodeIterator = graphDb.getAllNodes().iterator();		
			for (int i = 0; i < startNodeRandomIndex; i++) {
				startNodeIterator.next();
			}
			
			int endNodeRandomIndex = random.nextInt(5881);
			Iterator<Node> endNodeIterator = graphDb.getAllNodes().iterator();		
			for (int i = 0; i < endNodeRandomIndex; i++) {
				endNodeIterator.next();
			}
			
			startNode = startNodeIterator.next();
			endNode = endNodeIterator.next();
		
			tx.success();
		}
		
		
		GraphPattern graphPattern = generateRandomGP(numberofNodeInGraphPattern);
		
		Map<MyNode, Long> info = new HashMap<MyNode, Long>();
		
		info.put(graphPattern.getNodes().get(0), startNode.getId());
		info.put(graphPattern.getNodes().get(1), endNode.getId());
		
		
		Pair<GraphPattern, Map<MyNode, Long>> gpInfoPairIn = new Pair(graphPattern, info);
			
	    return gpInfoPairIn;
	}
	
	
	
	
	public GraphPattern generateRandomGP(int numberOfNodes) {
		
//		https://stackoverflow.com/questions/2041517/random-simple-connected-graph-generation-with-given-sparseness
		
		MyNode startNode = new MyNode(0, "testNode");
		
		List<MyNode> nodesSet = new ArrayList<>();
		List<MyEdge> edgeSet = new ArrayList<>();
		
		nodesSet.add(startNode);
		
		Random random = new Random();
		
		for (int i=1; i < numberOfNodes; i++) {
			
			MyNode nextNode = new MyNode(i, "testNode");
			
			int randomNode = random.nextInt(nodesSet.size());
			int randomRelationship = random.nextInt(6);
			
			MyEdge edge = new MyEdge(nextNode, (MyNode) nodesSet.get(randomNode), getRelationshipType(randomRelationship), i);
			
			nodesSet.add(nextNode);
			edgeSet.add(edge);
		}
		
		GraphPattern graphPattern = new GraphPattern();
		
		for(MyNode node: nodesSet) {
			graphPattern.addNode(node);
		}
		
		for(MyEdge edge: edgeSet) {
			graphPattern.addEdge(edge);
		}
		
		return graphPattern;
	}
	
	public RelType getRelationshipType(int i) {
		
		switch(i) {
		  case 0:
			  return RelType.R0;
		  case 1:
			  return RelType.R1;
		  case 2:
			  return RelType.R2;
		  case 3:
			  return RelType.R3;
		  case 4:
			  return RelType.R4;
		  case 5:
			  return RelType.R5;
		  case 6:
			  return RelType.R6;
		  default:
			  break;
		}
		
		return null;
	}
	
	
}
	
