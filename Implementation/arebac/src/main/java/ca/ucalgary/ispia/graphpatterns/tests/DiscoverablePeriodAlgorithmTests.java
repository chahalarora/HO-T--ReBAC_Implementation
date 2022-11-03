package ca.ucalgary.ispia.graphpatterns.tests;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.time.temporal.ValueRange;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;

import ca.ucalgary.ispia.graphpatterns.Driver;
import ca.ucalgary.ispia.graphpatterns.gpchecker.opt.impl.DiscoverablePeriodFinder;
import ca.ucalgary.ispia.graphpatterns.gpchecker.opt.impl.DiscoverablePeriodFinderCBJ;
import ca.ucalgary.ispia.graphpatterns.gpchecker.opt.impl.DiscoverablePeriodFinderWithDbAccess;
import ca.ucalgary.ispia.graphpatterns.gpchecker.opt.impl.OfficialPeriodFinder;
import ca.ucalgary.ispia.graphpatterns.graph.AlgorithmType;
import ca.ucalgary.ispia.graphpatterns.graph.ErdosRenyiGraphCreator;
import ca.ucalgary.ispia.graphpatterns.graph.GraphPattern;
import ca.ucalgary.ispia.graphpatterns.graph.MyNode;
import ca.ucalgary.ispia.graphpatterns.util.Pair;
import edu.stanford.nlp.util.Interval;
import edu.stanford.nlp.util.IntervalTree;

public class DiscoverablePeriodAlgorithmTests {

	static GraphDatabaseService graphDb = null;
	static String propFileLocation= "./properties/Test.properties";
	
	static FileWriter fileWriter = null;
	static PrintWriter printWriter = null;
	
	static List<Set<ValueRange>> discoverablePeriodsList = new ArrayList<Set<ValueRange>>();
	static List<Pair<GraphPattern, Map<MyNode, Node>>> gpinfoPairList = new ArrayList<Pair<GraphPattern, Map<MyNode, Node>>>();
	
	
	
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

		    // writing debug logs to file.
		    fileWriter = new FileWriter("./debugCBJ.txt");
		    printWriter = new PrintWriter(fileWriter);
		    
		    int testType = Integer.parseInt(properties.getProperty("testType"));
		    int numberofIteration = Integer.parseInt(properties.getProperty("numberofIteration"));
//		    int snapshotTimePoint =	Integer.parseInt(properties.getProperty("snapshotTimePoint"));
		    int timelineLength =	Integer.parseInt(properties.getProperty("timelineLength"));
		    String algorithmType = properties.getProperty("algorithm").toString();
		    
		    Random random = new Random();
		    int snapshotTimePoint = random.nextInt(timelineLength + 1);
		    
		    AlgorithmType algorithm = null;
		    if(algorithmType.equalsIgnoreCase("FC")) {
		    	algorithm = AlgorithmType.FC;
		    
		    }else if(algorithmType.equalsIgnoreCase("FC_CBJ")) {
		    	algorithm = AlgorithmType.FC_CBJ;
		    	
		    } else if(algorithmType.equalsIgnoreCase("FC_LBJ")){
		    	algorithm = AlgorithmType.FC_LBJ;
		    
		    } else if(algorithmType.equalsIgnoreCase("FC_LBJ_Improved")){
		    	algorithm = AlgorithmType.FC_LBJ_Improved;
		    }
		    
		    String gpInfoPairTimePresentFile = properties.getProperty("gpInfoPairTimePresentFile").toString();
		    String gpInfoPairTimeAbsentFile = properties.getProperty("gpInfoPairTimeAbsentFile").toString();
		    
		    if(testType == 0) {
		    	// time point absent test single algorithm test.
		    
		    	//input file and object stream.
			    FileInputStream gpInfoPairInputFile = new FileInputStream(new File(gpInfoPairTimeAbsentFile));
				ObjectInputStream gpInfoPairObjectInputStream = new ObjectInputStream(gpInfoPairInputFile);
		    	
		    	for(int i =0 ; i< numberofIteration; i++) {
		    		
		    		Pair<GraphPattern, Map<MyNode, Long>> gpInfoPairOut = null;
					try {
						gpInfoPairOut = (Pair<GraphPattern, Map<MyNode, Long>>) gpInfoPairObjectInputStream.readObject();
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
		    		
		    		DiscoverablePeriodAlgorithmTests discoverablePeriodAlgorithmAbsentTests = new DiscoverablePeriodAlgorithmTests();
		    		discoverablePeriodAlgorithmAbsentTests.discoverblePeriodTimePointAbsentTest(gpInfoPairOuput, snapshotTimePoint, algorithm);
		    	}
		    	
		    	 gpInfoPairInputFile.close();
				 gpInfoPairObjectInputStream.close();
		    
		    } else if(testType == 1) {
		    	// time point present test single algorithm test.
		    	
		    	//input file and object stream.
			    FileInputStream gpInfoPairInputFile = new FileInputStream(new File(gpInfoPairTimePresentFile));
				ObjectInputStream gpInfoPairObjectInputStream = new ObjectInputStream(gpInfoPairInputFile);
		    	
				
				long startTime = System.currentTimeMillis();
				
				
		    	for(int i = 0; i< numberofIteration; i++) {
		    		
		    		Pair<GraphPattern, Map<MyNode, Long>> gpInfoPairOut = null;
					try {
						gpInfoPairOut = (Pair<GraphPattern, Map<MyNode, Long>>) gpInfoPairObjectInputStream.readObject();
					} catch (ClassNotFoundException e) {
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
		    		
		    		DiscoverablePeriodAlgorithmTests discoverablePeriodAlgorithmPresentTests = new DiscoverablePeriodAlgorithmTests();
		    		discoverablePeriodAlgorithmPresentTests.discoverblePeriodTimePointPresentTest(gpInfoPairOuput, snapshotTimePoint, algorithm);
		    	}
		    	
		    	
		    	long endTime   = System.currentTimeMillis();
				long timeForRun = endTime - startTime;
		    	
				System.out.println("Run time: " + timeForRun/numberofIteration);
				
		    	gpInfoPairInputFile.close();
				gpInfoPairObjectInputStream.close();
		    	
		    } else if(testType == 2) {
		    	// time point present compare different algorithms.
		    	
		    	//input file and object stream.
			    FileInputStream gpInfoPairInputFile = new FileInputStream(new File(gpInfoPairTimePresentFile));
				ObjectInputStream gpInfoPairObjectInputStream = new ObjectInputStream(gpInfoPairInputFile);
		    	
		    	for(int i = 0 ; i< numberofIteration; i++) {
		    		
		    		Pair<GraphPattern, Map<MyNode, Long>> gpInfoPairOut = null;
					try {
						gpInfoPairOut = (Pair<GraphPattern, Map<MyNode, Long>>) gpInfoPairObjectInputStream.readObject();
					} catch (ClassNotFoundException e) {
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
		    		
		    		DiscoverablePeriodAlgorithmTests discoverablePeriodAlgorithmCompareTests = new DiscoverablePeriodAlgorithmTests();
		    		discoverablePeriodAlgorithmCompareTests.discoverblePeriodAlgorithmsCompare(gpInfoPairOuput);
		    	}
		    	
		    	 gpInfoPairInputFile.close();
				 gpInfoPairObjectInputStream.close();
		    
		    } else if(testType == 3) {
		    	// time point absent write test cases to file.
		    	
				//output file and object steam.
				FileOutputStream gpInfoPairOutputFile = new FileOutputStream(new File(gpInfoPairTimeAbsentFile));
		    	ObjectOutputStream gpInfoPairObjectOutputStream = new ObjectOutputStream(gpInfoPairOutputFile);
		    	
		    	for(int i = 0 ; i< numberofIteration; i++) {	
		    		DiscoverablePeriodAlgorithmTests discoverablePeriodGPInfoPairFile = new DiscoverablePeriodAlgorithmTests();
			    	Pair<GraphPattern, Map<MyNode, Long>> gpInfoPairIn = discoverablePeriodGPInfoPairFile.writeGPInfoPairTimeAbsentToFile();
			    	gpInfoPairObjectOutputStream.writeObject(gpInfoPairIn);
		    	}
		    	
		    	gpInfoPairObjectOutputStream.close();
		    	gpInfoPairOutputFile.close();
		    
		    } else if(testType == 4) {
		    	// time point present write test cases to file.
		    	
				//output file and object steam.
				FileOutputStream gpInfoPairOutputFile = new FileOutputStream(new File(gpInfoPairTimePresentFile));
		    	ObjectOutputStream gpInfoPairObjectOutputStream = new ObjectOutputStream(gpInfoPairOutputFile);
		    	
		    	for(int i = 0 ; i< numberofIteration; i++) {	
		    		DiscoverablePeriodAlgorithmTests discoverablePeriodGPInfoPairFile = new DiscoverablePeriodAlgorithmTests();
			    	Pair<GraphPattern, Map<MyNode, Long>> gpInfoPairIn = discoverablePeriodGPInfoPairFile.writeGPInfoPairTimePresentToFile();
			    	gpInfoPairObjectOutputStream.writeObject(gpInfoPairIn);
		    	}
		    	
		    	gpInfoPairObjectOutputStream.close();
		    	gpInfoPairOutputFile.close();
		    } 
 
		    
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		graphDb.shutdown();
		
		System.exit(0);
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
	
	private Pair<GraphPattern, Map<MyNode, Node>> generateRandomGraphPatternForTimePointAbsent(int numberofNodeInGP, int snapshotTimePoint, int totalNodesInGraph) {
		
		ErdosRenyiGraphCreator randomHistoryGraphCreator = new ErdosRenyiGraphCreator(graphDb);
		
		Pair<GraphPattern, Map<MyNode, Node>> gpInfoPair = null;
		
		try (Transaction tx = graphDb.beginTx()){
			
			RandomGPCreator randomGPCreator = new RandomGPCreator(graphDb, new Random(), numberofNodeInGP, 0, snapshotTimePoint, totalNodesInGraph);
			
			gpInfoPair = randomGPCreator.getRandomGP();
			
			tx.success();
		}

		return gpInfoPair;
	}
	
	
	private void discoverblePeriodTimePointPresentTest(Pair<GraphPattern, Map<MyNode, Node>> gpInfoPair, int snapshotTimePoint, AlgorithmType algorithm) {
		
		DiscoverablePeriodFinderWithDbAccess discoverablePeriodFinder = new DiscoverablePeriodFinderWithDbAccess(graphDb, algorithm);
		IntervalTree discoverablePeriods = discoverablePeriodFinder.FCLBJTemporalInit(graphDb, gpInfoPair.first, gpInfoPair.second);
			
		boolean snapshotTimePointExists = false;
		Iterator discoverablePeriodsIterator = discoverablePeriods.iterator();
		
		while(discoverablePeriodsIterator.hasNext()) {
			
			Interval period = (Interval) discoverablePeriodsIterator.next();
			
			if((int) period.first <= snapshotTimePoint && snapshotTimePoint <= (int) period.second) {
				snapshotTimePointExists = true;
			}
		}
		
		if(!snapshotTimePointExists) {
			System.out.println("fail");
			System.out.println(gpInfoPair);
		} else {
			System.out.println("Pass");
		}
	}
	
	private void discoverblePeriodTimePointAbsentTest(Pair<GraphPattern, Map<MyNode, Node>> gpInfoPair, int snapshotTimePoint, AlgorithmType algorithm) {
		
		DiscoverablePeriodFinderWithDbAccess discoverablePeriodFinder = new DiscoverablePeriodFinderWithDbAccess(graphDb, algorithm);
		IntervalTree discoverablePeriods = discoverablePeriodFinder.FCLBJTemporalInit(graphDb, gpInfoPair.first, gpInfoPair.second);
		
		Iterator discoverablePeriodIterator = discoverablePeriods.iterator();
		
		boolean snapshotTimePointExists = false;
		while(discoverablePeriodIterator.hasNext()) {
			Interval discoverablePeriod = (Interval) discoverablePeriodIterator.next();
			if((int)discoverablePeriod.first <= snapshotTimePoint && snapshotTimePoint <= (int) discoverablePeriod.second) {
				snapshotTimePointExists = true;
			}
		}
		
		if(snapshotTimePointExists) {
			System.out.println("Fail");
		
		} else {
			System.out.println("Pass");
		}
	}
	
	
	private void discoverblePeriodAlgorithmsCompare(Pair<GraphPattern, Map<MyNode, Node>> gpInfoPair) {
		
		Map<MyNode, Node> info = new HashMap<MyNode, Node>();
		
		int fixedNodesCount = 0;
		
		for(Map.Entry<MyNode, Node> entry : gpInfoPair.second.entrySet()) {
			
			if(fixedNodesCount < 4) {
				info.put(entry.getKey(), entry.getValue());
			}
			fixedNodesCount++;
		}
		
		
		DiscoverablePeriodFinderWithDbAccess discoverablePeriodFinderFC = new DiscoverablePeriodFinderWithDbAccess(graphDb, AlgorithmType.FC);
		IntervalTree discoverablePeriodsFC = discoverablePeriodFinderFC.FCLBJTemporalInit(graphDb, gpInfoPair.first, info);

		DiscoverablePeriodFinderWithDbAccess discoverablePeriodFinderCBJ = new DiscoverablePeriodFinderWithDbAccess(graphDb, AlgorithmType.FC_CBJ);
		IntervalTree discoverablePeriodsCBJ = discoverablePeriodFinderCBJ.FCLBJTemporalInit(graphDb, gpInfoPair.first, info);
		
		DiscoverablePeriodFinderWithDbAccess discoverablePeriodFinderLBJ = new DiscoverablePeriodFinderWithDbAccess(graphDb, AlgorithmType.FC_LBJ);
		IntervalTree discoverablePeriodsLBJ = discoverablePeriodFinderLBJ.FCLBJTemporalInit(graphDb, gpInfoPair.first, info);
		
		DiscoverablePeriodFinderWithDbAccess discoverablePeriodFinderLBJImproved = new DiscoverablePeriodFinderWithDbAccess(graphDb, AlgorithmType.FC_LBJ_Improved);
		IntervalTree discoverablePeriodsLBJImproved = discoverablePeriodFinderLBJImproved.FCLBJTemporalInit(graphDb, gpInfoPair.first, info);
		
		Iterator discoverablePeriodsFCIterator = discoverablePeriodsFC.iterator();
		Iterator discoverablePeriodsCBJIterator = discoverablePeriodsCBJ.iterator();
		Iterator discoverablePeriodsLBJIterator = discoverablePeriodsLBJ.iterator();
		Iterator discoverablePeriodsLBJImprovedIterator = discoverablePeriodsLBJImproved.iterator();
		
		Set<Interval> discoverablePeriodsFCSet = new HashSet<Interval>();
		Set<Interval> discoverablePeriodsCBJSet = new HashSet<Interval>();
		Set<Interval> discoverablePeriodsLBJSet = new HashSet<Interval>();
		Set<Interval> discoverablePeriodsLBJImprovedSet = new HashSet<Interval>();
		
		while(discoverablePeriodsFCIterator.hasNext()) {
			
			Interval discoverablePeriodFC = (Interval) discoverablePeriodsFCIterator.next();
			
			discoverablePeriodsFCSet.add(discoverablePeriodFC);
		}
		
		while(discoverablePeriodsCBJIterator.hasNext()) {
					
			Interval discoverablePeriodCBJ = (Interval) discoverablePeriodsCBJIterator.next();
			
			discoverablePeriodsCBJSet.add(discoverablePeriodCBJ);
		}
		
		while(discoverablePeriodsLBJIterator.hasNext()) {
			
			Interval discoverablePeriodLBJ = (Interval) discoverablePeriodsLBJIterator.next();
			
			discoverablePeriodsLBJSet.add(discoverablePeriodLBJ);
		}
		
		while(discoverablePeriodsLBJImprovedIterator.hasNext()) {
			
			Interval discoverablePeriodLBJImproved = (Interval) discoverablePeriodsLBJImprovedIterator.next();
			
			discoverablePeriodsLBJImprovedSet.add(discoverablePeriodLBJImproved);
		}
		
		OfficialPeriodFinder officialPeriodFinder = new OfficialPeriodFinder();
		discoverablePeriodsFC = officialPeriodFinder.findOfficialPeriods(discoverablePeriodsFC);
		discoverablePeriodsCBJ = officialPeriodFinder.findOfficialPeriods(discoverablePeriodsCBJ);
		discoverablePeriodsLBJ = officialPeriodFinder.findOfficialPeriods(discoverablePeriodsLBJ);
		
		boolean isCBJandFCEqual = discoverablePeriodsCBJ.containsAll(discoverablePeriodsFC);
		boolean isLBJandFCEqual = discoverablePeriodsLBJ.containsAll(discoverablePeriodsFC);
		boolean isLBJImprovedandFCEqual = discoverablePeriodsLBJImprovedSet.containsAll(discoverablePeriodsFC);

		if(isCBJandFCEqual && isLBJandFCEqual && isLBJImprovedandFCEqual) {
			System.out.println("Pass");
//			System.out.println("FC Rel accessed: "+ discoverablePeriodFinderFC.serachTreeRelationshipsAccessed.size());
//			System.out.println("CBJ Rel accessed: "+ discoverablePeriodFinderCBJ.serachTreeRelationshipsAccessed.size());
//			System.out.println("LBJ Rel accessed: "+ discoverablePeriodFinderLBJ.serachTreeRelationshipsAccessed.size());
//			System.out.println("LBJ Improved Rel accessed: "+ discoverablePeriodFinderLBJImproved.serachTreeRelationshipsAccessed.size());
//			
			System.out.println("FC Nodes accessed: "+ discoverablePeriodFinderFC.serachTreeNodesAccessed.containsAll(info.values()));
			System.out.println("CBJ Nodes accessed: "+ discoverablePeriodFinderCBJ.serachTreeNodesAccessed.containsAll(info.values()));
			System.out.println("LBJ Nodes accessed: "+ discoverablePeriodFinderLBJ.serachTreeNodesAccessed.containsAll(info.values()));
			System.out.println("LBJ Improved Nodes accessed: "+ discoverablePeriodFinderLBJImproved.serachTreeNodesAccessed.containsAll(info.values()));
			
		} else {
			System.out.println("Fail");
//			System.out.println("CBJ and FC equal: " + isCBJandFCEqual);
//			System.out.println("LBJ and FC equal: " + isLBJandFCEqual);
//			System.out.println("LBJ and FC equal: " + isLBJImprovedandFCEqual);
		}					
	}
	
	public Pair<GraphPattern, Map<MyNode, Long>> writeGPInfoPairTimeAbsentToFile() throws IOException {
		File file = new File(propFileLocation);
		InputStream in = new FileInputStream(file);
	    Properties properties = new Properties();
	    properties.load(in);
	    int snapshotTimePoint =	Integer.parseInt(properties.getProperty("snapshotTimePoint"));
	    int numberofNodeInGraphPattern = Integer.parseInt(properties.getProperty("numberofNodeInGraphPattern"));
	    int numberofNodeInGraph = Integer.parseInt(properties.getProperty("numberofNodeInNeo4jGraphDatabse"));

	    Pair<GraphPattern, Map<MyNode, Node>> gpInfoPair = generateRandomGraphPatternForTimePointPresent(numberofNodeInGraphPattern, snapshotTimePoint);
	    
	    Map<MyNode, Long> vertexNodeMap = new HashMap<MyNode, Long>();
	    
	    for(MyNode myNode: gpInfoPair.second.keySet()) {
	    	vertexNodeMap.put(myNode, gpInfoPair.second.get(myNode).getId());
	    }
	    
		Pair<GraphPattern, Map<MyNode, Long>> gpInfoPairIn = new Pair(gpInfoPair.first, vertexNodeMap);

	    return gpInfoPairIn;
	}
	
	
	public Pair<GraphPattern, Map<MyNode, Long>> writeGPInfoPairTimePresentToFile() throws IOException {
		File file = new File(propFileLocation);
		InputStream in = new FileInputStream(file);
	    Properties properties = new Properties();
	    properties.load(in);
	    int snapshotTimePoint =	Integer.parseInt(properties.getProperty("snapshotTimePoint"));
	    int numberofNodeInGraphPattern = Integer.parseInt(properties.getProperty("numberofNodeInGraphPattern"));

	    Pair<GraphPattern, Map<MyNode, Node>> gpInfoPair = generateRandomGraphPatternForTimePointPresent(numberofNodeInGraphPattern, snapshotTimePoint);
	    
	    Map<MyNode, Long> vertexNodeMap = new HashMap<MyNode, Long>();
	    
	    for(MyNode myNode: gpInfoPair.second.keySet()) {
	    	vertexNodeMap.put(myNode, gpInfoPair.second.get(myNode).getId());
	    }
	    
		Pair<GraphPattern, Map<MyNode, Long>> gpInfoPairIn = new Pair(gpInfoPair.first, vertexNodeMap);

	    return gpInfoPairIn;
	}
		
}
