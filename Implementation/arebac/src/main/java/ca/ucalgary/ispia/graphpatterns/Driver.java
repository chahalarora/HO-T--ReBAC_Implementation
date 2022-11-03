package ca.ucalgary.ispia.graphpatterns;

//import java.io.BufferedReader;
import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileNotFoundException;
//import java.io.FileOutputStream;
//import java.io.FileReader;
//import java.io.IOException;
//import java.io.ObjectInputStream;
//import java.io.ObjectOutputStream;
//import java.sql.Connection;
//import java.time.temporal.ValueRange;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//import java.util.HashMap;
//import java.util.HashSet;
//import java.util.Iterator;
//import java.util.Map;
//import java.util.Random;
//import java.util.Set;
//import java.util.TreeSet;

//import org.apache.commons.lang3.tuple.Triple;
//import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
//import org.neo4j.graphdb.Label;
//import org.neo4j.graphdb.Node;
//import org.neo4j.graphdb.Relationship;
//import org.neo4j.graphdb.RelationshipType;
//import org.neo4j.graphdb.ResourceIterable;
//import org.neo4j.graphdb.ResourceIterator;
//import org.neo4j.graphdb.Result;
//import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

//import ca.ucalgary.ispia.graphpatterns.gpchecker.opt.GPHGCheckerLBJFC;
//import ca.ucalgary.ispia.graphpatterns.gpchecker.opt.impl.DPFinder;
//import ca.ucalgary.ispia.graphpatterns.gpchecker.opt.impl.DiscoverablePeriodFinder;
//import ca.ucalgary.ispia.graphpatterns.gpchecker.opt.impl.DiscoverablePeriodFinderCBJ;
//import ca.ucalgary.ispia.graphpatterns.gpchecker.opt.impl.DiscoverablePeriodFinderWithDbAccess;
//import ca.ucalgary.ispia.graphpatterns.gpchecker.opt.impl.OfficialPeriodFinder;
//import ca.ucalgary.ispia.graphpatterns.graph.AlgorithmType;
//import ca.ucalgary.ispia.graphpatterns.graph.DiscoverablePeriod;
//import ca.ucalgary.ispia.graphpatterns.graph.ErdosRenyiGraphCreator;
//import ca.ucalgary.ispia.graphpatterns.graph.GPHolder;
//import ca.ucalgary.ispia.graphpatterns.graph.SocialGraphCreator;
//import ca.ucalgary.ispia.graphpatterns.graph.GraphPattern;
//import ca.ucalgary.ispia.graphpatterns.graph.MyEdge;
//import ca.ucalgary.ispia.graphpatterns.graph.MyNode;
//import ca.ucalgary.ispia.graphpatterns.graph.MyRelationship;
//import ca.ucalgary.ispia.graphpatterns.graph.RelType;
//import ca.ucalgary.ispia.graphpatterns.tests.EvalTestRunner;
//import ca.ucalgary.ispia.graphpatterns.tests.EvaluateDiscoverablePeriodFinder;
//import ca.ucalgary.ispia.graphpatterns.tests.RandomGPCreator;
//import ca.ucalgary.ispia.graphpatterns.tests.SubgraphGenerator;
//import ca.ucalgary.ispia.graphpatterns.tests.TestGeneration;
//import ca.ucalgary.ispia.graphpatterns.util.Pair;
//import ca.ucalgary.ispia.policy.impl.AtomicValueImpl;
//import ca.ucalgary.ispia.policy.impl.ConjunctionMatrixImpl;
//import ca.ucalgary.ispia.policy.impl.DisjunctionMatrixImpl;
//import ca.ucalgary.ispia.policy.impl.NegationMatrixImpl;
//import ca.ucalgary.ispia.policy.impl.PolicyImpl;
//import ca.ucalgary.ispia.policy.impl.PolicyPrefixImpl;
//import ca.ucalgary.ispia.policy.opt.AllenRelation;
//import ca.ucalgary.ispia.policy.opt.AtomicValue;
//import ca.ucalgary.ispia.policy.opt.ExistentialQuantifier;
//import ca.ucalgary.ispia.policy.opt.PolicyExecute;
//import ca.ucalgary.ispia.policy.opt.PolicyPrefix;
//import ca.ucalgary.ispia.policy.opt.PolicyToSqlTraslator;
//import ca.ucalgary.ispia.policy.opt.RebacRelationIdentifier;
//import edu.stanford.nlp.util.Interval;
//import edu.stanford.nlp.util.IntervalTree;
//import scala.Int;
/**
 * The driver.
 * @author szrrizvi
 *
 */
public class Driver {
	
	static GraphDatabaseService graphDb = null;
	static int countSuccess = 0;
	static int countFailure = 0;
	
//	public static List<Long> totalTimeArray= new ArrayList<>();
	
	/**
	 * The main control for specifying tasks.
	 * @param args
	 */
	public static void main(String[] args){	
		
		Driver d = new Driver();
		graphDb = d.getGraphDb("graph.db");
		
		
//		Result result = graphDb.execute("MATCH (a:Requestor) Return a");
//		int nodeCreated = result.getQueryStatistics().getNodesCreated();
		//Random rand = new Random(5927541);
//		EvalTestRunner etr = new EvalTestRunner(graphDb);
		//etr.warmup(250);
		//System.out.println("Done Warmup\n");
		//etr.runGPHTestsList("simulation-tests/slashdottests/testCase", 6);
		//etr.runSimTests("simulation-tests/slashdottests/testCase", rand);
//		etr.runSimTests("simulation-tests/soc-pokectests/testCase-1.ser");
		
		// checking run time
		long startTime = System.currentTimeMillis();
		
//		testDiscoverablePeriodAlgorithm();
		
//		testGetRelevantRelationships();
//		testPickNextEdge();
//		testValueOrdering();
//		testBothVerticesNotAssigned();
//		testAssignOtherVertex();
//		testGetRelevantEdges();
//		testGetOverlappingRelationships();
//		testGetOverlappingRelationship();
//		testFindCommonRelationships();
//		testFilterSource();
//		testFilterTarget();
//		testPartialDiscoverableTimeChanges();
		
//		testGraphPatternGenerator();
//		miscellaneousTesting();
//		miscellaneousExperiment();
		
		
		
//		deleteNodesAndRelationship();
//		randomGraphCreator();
		
//		Map<Integer, Integer> testMap1 = new HashMap<Integer, Integer>();
//		Map<Integer, Integer> testMap2 = new HashMap<Integer, Integer>();
	
			
		
		for (int i= 0; i < 10; i++) {
//			System.out.println("Iteration start.");
//			testRandomGraphPattern(50, 3);
//			testRandomGraphPatternSnapshotPeriodExcluded(50, 11);
//			testFCForFailedCases();
//			System.out.println("Iteration end.");
//			System.out.println("\n");
			
//			testRandomGraphPattern(25, 4);
//			testFCForFailedCases();
			
//			try ( Session session = driver.session() )
//		    {
//		        session.run("CREATE (a:Person {name: $name})", parameters( "name", name ) );
//		    }
			
			
//			Driver driver = new Driver();
//			driver.testCBJLBJForFailedCases();
			
//			driver.createMiniHistoryGraphNodes(5);
			
//			driver.createMiniHistoryGraphRelationships();
			
//			driver.writingToFile();
			
//			driver.testOfficialPeriods();
			
//			driver.checkUpdateSqlTable();
			
//			driver.testPolicyTranslation();
			
//			driver.testIntervalTree();
			
//			driver.testEvaluation();
			
//			driver.testGenerateRandomGPInfo();
			
//			driver.testGraphFromCSV();
			
		}
//		System.out.println("Count Success: " + countSuccess);
//		System.out.println("Count Failure: " + countFailure);
		
		
		System.out.println("Run complete");
		
//		long totalTimeForRuns = 0;
//		for(int i=0; i< totalTimeArray.size(); i++) {
//			totalTimeForRuns = totalTimeForRuns + totalTimeArray.get(i);
//		}
//		double averegeTimeForRun = totalTimeForRuns/ (float) totalTimeArray.size();
		
//		System.out.println("Average run time: "+ averegeTimeForRun);
		
//		creatingSnapshotDiffGraph(2);
		
//		testFCForFailedCases();

//		historyGraphSnapshot(25, 2);
//		testRandomGraphPattern(20, 5);
		
		
//		testRandomGraphPatternSnapshotPeriodExcluded(10, 2);
		
//		checkingGrapDb();
//		historyGraphSnapshot();
//		long endTime   = System.currentTimeMillis();
//		long totalTime = endTime - startTime;
//		System.out.println(totalTime);
		
		
//		TestThread mt1 = new TestThread();
//        Thread pt = new Thread(mt1);
//        pt.setName("Mailthread");
//        pt.start();
		
		graphDb.shutdown();
	} 

	/**
	 * Initialize the graph database
	 * @param db The name of directory containing the database
	 * @return The generated GraphDatabaseService object.
	 */
	public GraphDatabaseService getGraphDb(String db){
		File db_file = new File(db);

		GraphDatabaseService graphDb = new GraphDatabaseFactory().newEmbeddedDatabaseBuilder( db_file ).loadPropertiesFromFile("neo4j.properties").newGraphDatabase();
		registerShutdownHook( graphDb );

		return graphDb;
	}

	// START SNIPPET: shutdownHook
	private static void registerShutdownHook( final GraphDatabaseService graphDb )
	{
		// Registers a shutdown hook for the Neo4j instance so that it
		// shuts down nicely when the VM exits (even if you "Ctrl-C" the
		// running application).
		Runtime.getRuntime().addShutdownHook( new Thread()
		{
			@Override
			public void run()
			{
				graphDb.shutdown();
			}
		} );
	}
	
	
	
//	public static void creatingSnapshotDiffGraph(int commonTimePoint) {
//		
//		Set<Relationship> commonPointRelationships = new HashSet<Relationship>();
//		Set<Relationship> allRelationships = new HashSet<Relationship>();
//		Set<Node> allNodes = new HashSet<Node>();
//		
//		try (Transaction tx = graphDb.beginTx()){
//			
//			ResourceIterable<Relationship> relationshipIterable  = graphDb.getAllRelationships();
//			ResourceIterable<Node> nodeIterable  = graphDb.getAllNodes();
//			ResourceIterator<Relationship> relationshipIterator  = relationshipIterable.iterator();
//			ResourceIterator<Node> nodeIterator  = nodeIterable.iterator();
//			
//			while(relationshipIterator.hasNext()) {
//				
//				Relationship relationship = relationshipIterator.next();
//				allRelationships.add(relationship);
//				
//				Integer startTime = Integer.valueOf(relationship.getProperty("startTime").toString());
//				Integer endTime =  Integer.valueOf(relationship.getProperty("endTime").toString());
//				
//				
//				// if the relationship contains the given time point then the relationship is added to the snapshot along with the nodes connected to the relationship. 
//				if(startTime <= commonTimePoint && endTime >= commonTimePoint) {
//					commonPointRelationships.add(relationship);
//				}
//			}
//			
//			while(nodeIterator.hasNext()) {
//				
//				Node node = nodeIterator.next();
//				allNodes.add(node);
//
//			}
//			
//			tx.success();
//		}
//		
//		getRelationshipCorrespondingToCommonTime(commonPointRelationships, allRelationships, allNodes, commonTimePoint);
//	}
//	
//	
//	public static void getRelationshipCorrespondingToCommonTime(Set<Relationship> commonPointRelationships, Set<Relationship> allRelationships, Set<Node> allNodes, int commonTimePoint) {
//		
//		Set<Relationship> relationshipsToAvoid = new HashSet<Relationship>();
//		Set<Node> nodesToAvoid = new HashSet<Node>();
//		Pair<GraphPattern, Map<MyNode, Node>> gpInfoPair = null;
//		
//		try (Transaction tx = graphDb.beginTx()){
//			
//			for(Relationship relationship: allRelationships) {
//				
//				Node sourceNode = relationship.getStartNode();
//				Node targetNode = relationship.getEndNode();
//				RelationshipType relType = relationship.getType();
//				
//				for(Relationship commonRelationship: commonPointRelationships) {
//					Node commonSourceNode = commonRelationship.getStartNode();
//					Node commonTargetNode = commonRelationship.getEndNode();
//					RelationshipType commonRelType = commonRelationship.getType();
//					
//					if(sourceNode.equals(commonSourceNode) && targetNode.equals(commonTargetNode) && relType.equals(commonRelType)) {
//						relationshipsToAvoid.add(relationship);
////						nodesToAvoid.add(sourceNode);
////						nodesToAvoid.add(targetNode);
//					}
//				}
//			}
//			
//			
//			for(Relationship commonRelationship: commonPointRelationships) {
//				Node commonSourceNode = commonRelationship.getStartNode();
//				Node commonTargetNode = commonRelationship.getEndNode();
////				RelationshipType commonRelType = commonRelationship.getType();
//				
////				if(sourceNode.equals(commonSourceNode) && targetNode.equals(commonTargetNode) && relType.equals(commonRelType)) {
////					relationshipsToAvoid.add(relationship);
//					nodesToAvoid.add(commonSourceNode);
//					nodesToAvoid.add(commonTargetNode);
////				}
//			}
//			
//			relationshipsToAvoid.addAll(commonPointRelationships);
//			
//			allRelationships.removeAll(relationshipsToAvoid);
//			allNodes.removeAll(nodesToAvoid);
//			
//			Pair<Set<Node>, Set<Relationship>> historyGraphSnapshot = new Pair(allNodes, allRelationships);
////			
//			RandomGPCreator randomGPCreator = new RandomGPCreator(historyGraphSnapshot, new Random(), 2, true);
////			
//			gpInfoPair = randomGPCreator.getRandomGP();
//			
//			tx.success();
//		}
//		
//		
//		long startTime = System.currentTimeMillis();
//				
//		//		DiscoverablePeriodFinder discoverablePeriodFinder = new DiscoverablePeriodFinder();
//		DiscoverablePeriodFinderWithDbAccess discoverablePeriodFinder = new DiscoverablePeriodFinderWithDbAccess(graphDb);
//		Set<ValueRange> discoverablePeriods = discoverablePeriodFinder.FCLBJTemporalInit(graphDb, gpInfoPair.first, gpInfoPair.second);
//		
//		Iterator<ValueRange> discoverablePeriodIterator = discoverablePeriods.iterator();
//
//		boolean snapshotTimePointExists = false;
//		while(discoverablePeriodIterator.hasNext()) {
//			ValueRange discoverablePeriod = discoverablePeriodIterator.next();
//		//				System.out.println("Discoverable period:"+ discoverablePeriod );
//			if(discoverablePeriod.getMinimum() <= commonTimePoint && commonTimePoint <= discoverablePeriod.getMaximum()) {
//				snapshotTimePointExists = true;
//			}
//		}
//		
//		//		System.out.println("Snapshot Time Point Exists: "+ snapshotTimePointExists);
//		
//		if(snapshotTimePointExists) {
//			System.out.println("Problem detected.");
//		}
//		
//		long endTime   = System.currentTimeMillis();
//		long totalTime = endTime - startTime;
//		System.out.println(totalTime);
//		
//	}
	
	
	
//	public static void historyGraphSnapshot(int snapshotTimePoint, int numberofNodeInGP) {
//		
//		ErdosRenyiGraphCreator randomHistoryGraphCreator = new ErdosRenyiGraphCreator(graphDb);
//		
//		Pair<GraphPattern, Map<MyNode, Node>> gpInfoPair = null;
//		
//		try (Transaction tx = graphDb.beginTx()){
//			
//			Pair<Set<Node>, Set<Relationship>> historyGraphSnapshot = randomHistoryGraphCreator.getHistoryGraphSnapshot(snapshotTimePoint);
//			
//			RandomGPCreator randomGPCreator = new RandomGPCreator(historyGraphSnapshot, new Random(), numberofNodeInGP, 1);
//			
//			gpInfoPair = randomGPCreator.getRandomGP();
//			
//			System.out.println("Stop point.");
//			
//			tx.success();
//		}
//		
//	}
//	
//	
//	public static void testRandomGraphPattern(int snapshotTimePoint, int numberofNodeInGP) {
//		ErdosRenyiGraphCreator randomHistoryGraphCreator = new ErdosRenyiGraphCreator(graphDb);
//		
//		Pair<GraphPattern, Map<MyNode, Node>> gpInfoPair = null;
//		
//		Pair<Set<Node>, Set<Relationship>> historyGraphSnapshot = null;
//		
//		try (Transaction tx = graphDb.beginTx()){
//			
//			historyGraphSnapshot = randomHistoryGraphCreator.getHistoryGraphSnapshot(snapshotTimePoint);
//			
//			RandomGPCreator randomGPCreator = new RandomGPCreator(historyGraphSnapshot, new Random(), numberofNodeInGP, 1);
//			
//			gpInfoPair = randomGPCreator.getRandomGP();
//			
//			long startTime = System.currentTimeMillis();
//			
//			
////			DiscoverablePeriodFinderWithDbAccess discoverablePeriodFinder = new DiscoverablePeriodFinderWithDbAccess(graphDb);
////			Set<ValueRange> discoverablePeriods = discoverablePeriodFinder.FCLBJTemporalInit(graphDb, gpInfoPair.first, gpInfoPair.second);
//			
//			DiscoverablePeriodFinder discoverablePeriodFinder = new DiscoverablePeriodFinder(AlgorithmType.FC);
//			Set<ValueRange> discoverablePeriods = discoverablePeriodFinder.FCLBJTemporalInit(gpInfoPair.first, gpInfoPair.second);
//			
//			Iterator<ValueRange> discoverablePeriodIterator = discoverablePeriods.iterator();
//			
//			boolean snapshotTimePointExists = false;
//			while(discoverablePeriodIterator.hasNext()) {
//				ValueRange discoverablePeriod = discoverablePeriodIterator.next();
//					System.out.println("Discoverable period:"+ discoverablePeriod );
//				if(discoverablePeriod.getMinimum() <= snapshotTimePoint && snapshotTimePoint <= discoverablePeriod.getMaximum()) {
//					snapshotTimePointExists = true;
//				}
//			}
//			
//	//		System.out.println("Snapshot Time Point Exists: "+ snapshotTimePointExists);
//			
//			if(!snapshotTimePointExists) {
//				System.out.println("Problem detected.");
//			}
//			
//			long endTime   = System.currentTimeMillis();
//			long totalTime = endTime - startTime;
//			System.out.println(totalTime);
//		
//			tx.success();
//		}
//	}
//	
//	
//	public static void testRandomGraphPatternSnapshotPeriodExcluded(int snapshotTimePoint, int numberofNodeInGP) {
//		
//		Pair<GraphPattern, Map<MyNode, Node>> gpInfoPair = null;
//		try {
//			
//			try (Transaction tx = graphDb.beginTx()){
//				
//				int totalGraphNodes = 100;
//				
//				RandomGPCreator randomGPCreator = new RandomGPCreator(graphDb, new Random(), numberofNodeInGP, 0, snapshotTimePoint, totalGraphNodes);
//				
//				gpInfoPair = randomGPCreator.getRandomGP();
//				
//				tx.success();
//			}
//		
////			System.out.println(gpInfoPair.first);
//		
//			long startTime = System.currentTimeMillis();
//			
//	//		DiscoverablePeriodFinder discoverablePeriodFinder = new DiscoverablePeriodFinder();
////			DiscoverablePeriodFinderWithDbAccess discoverablePeriodFinder = new DiscoverablePeriodFinderWithDbAccess(graphDb, AlgorithmType.FC);
////			Set<ValueRange> discoverablePeriods = discoverablePeriodFinder.FCLBJTemporalInit(graphDb, gpInfoPair.first, gpInfoPair.second);
////			
////			Iterator<ValueRange> discoverablePeriodIterator = discoverablePeriods.iterator();
////			
////			boolean snapshotTimePointExists = false;
////			while(discoverablePeriodIterator.hasNext()) {
////				ValueRange discoverablePeriod = discoverablePeriodIterator.next();
//////					System.out.println("Discoverable period:"+ discoverablePeriod );
////				if(discoverablePeriod.getMinimum() <= snapshotTimePoint && snapshotTimePoint <= discoverablePeriod.getMaximum()) {
////					snapshotTimePointExists = true;
////				}
////			}
//			
//	//		System.out.println("Snapshot Time Point Exists: "+ snapshotTimePointExists);
//			
////			if(snapshotTimePointExists) {
//////				System.out.println("Problem detected.");
////				countFailure++;
////			} else {
////				countSuccess++;
////			}
//			
//			long endTime   = System.currentTimeMillis();
//			long totalTime = endTime - startTime;
//			System.out.println(totalTime);
//		
//		} catch(Exception e) {
////			System.out.println(gpInfoPair.first);
//		}
//		
//	}
//	
//	
//	
//	public static void testFCForFailedCases() {
//		
//		try (Transaction tx = graphDb.beginTx()){
//
//			GraphPattern gp = new GraphPattern();
//			
//			MyNode testNode0 = new MyNode(0, "testNode");
//			MyNode testNode1 = new MyNode(1, "testNode");
//			MyNode testNode2 = new MyNode(2, "testNode");
//			MyNode testNode3 = new MyNode(3, "testNode");
//			
//			
//			RelType relType0 = RelType.RELTYPE;
//			RelType relType1 = RelType.RELTYPE1;
//			RelType relType2 = RelType.RELTYPE2;
//			RelType relType3 = RelType.RELTYPE3;
//			RelType relType4 = RelType.R4;
//			RelType relType5 = RelType.R5;
//			
//			MyEdge edge0 = new MyEdge(testNode0, testNode1, relType0, 0);
//			MyEdge edge1 = new MyEdge(testNode0, testNode1, relType1, 1);
//			MyEdge edge2 = new MyEdge(testNode1, testNode2, relType2, 2);
//			MyEdge edge3 = new MyEdge(testNode2, testNode3, relType3, 3);
//			
//						
//			gp.addNode(testNode0);
//			gp.addNode(testNode1);
//			gp.addNode(testNode2);
//			gp.addNode(testNode3);
//
//			gp.addEdge(edge0);
//			gp.addEdge(edge1);
//			gp.addEdge(edge2);
//			gp.addEdge(edge3);
//			
//			// creating info node.
//			Map<MyNode, Node> info = new HashMap<MyNode, Node>();
//			
//			Node startNode = graphDb.getNodeById(33034);
//			
//			info.put(testNode0, startNode);
//			
//			// finding discoverable time periods.
//			DiscoverablePeriodFinder discoverablePeriodFinder = new DiscoverablePeriodFinder(AlgorithmType.FC);
////			
//			Set<ValueRange> discoverablePeriods = discoverablePeriodFinder.FCLBJTemporalInit(gp, info);
//
////			DiscoverablePeriodFinderWithDbAccess discoverablePeriodFinder = new DiscoverablePeriodFinderWithDbAccess(graphDb);
////			Set<ValueRange> discoverablePeriods = discoverablePeriodFinder.FCLBJTemporalInit(graphDb, gp, info);
//			
//			Iterator<ValueRange> discoverablePeriodIterator = discoverablePeriods.iterator();
//			while(discoverablePeriodIterator.hasNext()) {
//				ValueRange discoverablePeriod = discoverablePeriodIterator.next();
//				System.out.println("Discoverable period:"+ discoverablePeriod );
//			}
//			
////			Node startNode = graphDb.getNodeById(33794);
//			
//			Iterable<Relationship> relations1 = startNode.getRelationships(Direction.OUTGOING, RelType.RELTYPE);
//			Iterable<Relationship> relations2 = startNode.getRelationships(Direction.OUTGOING, RelType.RELTYPE1);
//			Iterable<Relationship> relations3 = startNode.getRelationships(Direction.INCOMING, RelType.R1);
//			
//			boolean exists1 = false;
//			boolean exists2 = false;
////			boolean exists3 = false;
//			int pointToCheck = 25;
//			
//			for(Relationship rel: relations1) {
//				if(Integer.valueOf(rel.getProperty("startTime").toString()) <= pointToCheck && Integer.valueOf(rel.getProperty("endTime").toString()) >= pointToCheck) {
//					exists1= true;
////					System.out.println("Start time1:"+ rel.getProperty("startTime"));
////					System.out.println("End time1:"+ rel.getProperty("endTime"));
//				}
////				System.out.println("Rel id1:"+ rel.getId());
////				System.out.println("Start time1:"+ rel.getProperty("startTime"));
////				System.out.println("End time1:"+ rel.getProperty("endTime"));
//			}
//			
//			for(Relationship rel: relations2) {
//				if(Integer.valueOf(rel.getProperty("startTime").toString()) <= pointToCheck && Integer.valueOf(rel.getProperty("endTime").toString()) >= pointToCheck) {
//					exists2 = true;
////					System.out.println("Start time2:"+ rel.getProperty("startTime"));
////					System.out.println("End time2:"+ rel.getProperty("endTime"));
//				}
////				System.out.println("Rel id2:"+ rel.getId());
////				System.out.println("Start time2:"+ rel.getProperty("startTime"));
////				System.out.println("End time2:"+ rel.getProperty("endTime"));
//			}
//			
////			for(Relationship rel: relations3) {
////				if((int)rel.getProperty("startTime") <= 30 && (int)rel.getProperty("endTime") >= 30) {
////					exists3 = true;
////					System.out.println("Start time3:"+ rel.getProperty("startTime"));
////					System.out.println("End time3:"+ rel.getProperty("endTime"));
////				}
//////				System.out.println("Start time:"+ rel.getProperty("startTime"));
//////				System.out.println("End time:"+ rel.getProperty("endTime"));
////			}
//			
////			System.out.println("Exists1:"+ exists1);
////			System.out.println("Exists2:"+ exists2);
////			System.out.println("Exists3:"+ exists3);
//			
//			tx.success();
//		}
//		
//	}
//	
//	
//	public void testCBJLBJForFailedCases() {
//		int snapshotTimePoint = 150;
//		Node startNode = null;
//		Node endNode = null;
//		
//		
//		try (Transaction tx = graphDb.beginTx()){
//			
//			// make sure to change the start node after new test.
//			startNode = graphDb.getNodeById(247076);
//			endNode = graphDb.getNodeById(247035);
//			tx.success();
//		}
//			GraphPattern gp = new GraphPattern();
//			
//			MyNode testNode0 = new MyNode(0, "testNode");
//			MyNode testNode1 = new MyNode(1, "testNode");
//			MyNode testNode2 = new MyNode(2, "testNode");
////			MyNode testNode3 = new MyNode(3, "testNode");
////			MyNode testNode4 = new MyNode(4, "testNode");
////			MyNode testNode5 = new MyNode(5, "testNode");
////			MyNode testNode6 = new MyNode(6, "testNode");
////			MyNode testNode7 = new MyNode(7, "testNode");
////			MyNode testNode8 = new MyNode(8, "testNode");
////			MyNode testNode9 = new MyNode(9, "testNode");
////			MyNode testNode10 = new MyNode(10, "testNode");
////			MyNode testNode11 = new MyNode(11, "testNode");
////			MyNode testNode12 = new MyNode(12, "testNode");
//			
//			
//			RelType relType0 = RelType.R0;
//			RelType relType1 = RelType.R1;
//			RelType relType2 = RelType.R2;
//			RelType relType3 = RelType.R3;
//			RelType relType4 = RelType.R4;
//			RelType relType5 = RelType.R5;
//			RelType relType6 = RelType.R6;
//			
//			MyEdge edge0 = new MyEdge(testNode1, testNode2, relType6, 0);
//			MyEdge edge1 = new MyEdge(testNode1, testNode0, relType6, 1);
//			MyEdge edge2 = new MyEdge(testNode2, testNode1, relType0, 2);
//			MyEdge edge3 = new MyEdge(testNode0, testNode1, relType6, 3);
////			MyEdge edge4 = new MyEdge(testNode3, testNode2, relType5, 4);
////			MyEdge edge5 = new MyEdge(testNode3, testNode1, relType0, 5);
////			MyEdge edge6 = new MyEdge(testNode0, testNode2, relType5, 6);
////			MyEdge edge7 = new MyEdge(testNode0, testNode1, relType3, 7);
////			MyEdge edge8 = new MyEdge(testNode9, testNode12, relType1, 8);
////			MyEdge edge9 = new MyEdge(testNode9, testNode6, relType1, 9);
////			MyEdge edge10 = new MyEdge(testNode0, testNode2, relType2, 10);
////			MyEdge edge11 = new MyEdge(testNode0, testNode1, relType1, 11);
////			MyEdge edge12 = new MyEdge(testNode0, testNode5, relType6, 12);
////			MyEdge edge13 = new MyEdge(testNode6, testNode10, relType6, 13);
////			MyEdge edge14 = new MyEdge(testNode7, testNode10, relType3, 14);
////			MyEdge edge15 = new MyEdge(testNode7, testNode12, relType3, 15);
////			MyEdge edge16 = new MyEdge(testNode7, testNode11, relType1, 16);
////			MyEdge edge17 = new MyEdge(testNode8, testNode7, relType6, 17);
////			MyEdge edge18 = new MyEdge(testNode8, testNode11, relType2, 18);
////			MyEdge edge19 = new MyEdge(testNode9, testNode1, relType0, 19);
////			MyEdge edge20 = new MyEdge(testNode0, testNode2, relType1, 20);
////			MyEdge edge21 = new MyEdge(testNode0, testNode6, relType2, 21);
////			MyEdge edge22 = new MyEdge(testNode0, testNode3, relType5, 21);
////			MyEdge edge23 = new MyEdge(testNode0, testNode3, relType6, 21);
////			MyEdge edge24 = new MyEdge(testNode0, testNode7, relType0, 21);
////			MyEdge edge25 = new MyEdge(testNode0, testNode5, relType2, 21);
////			MyEdge edge26 = new MyEdge(testNode0, testNode2, relType1, 21);
////			MyEdge edge27 = new MyEdge(testNode0, testNode3, relType2, 21);
//			
//			
//						
//			gp.addNode(testNode0);
//			gp.addNode(testNode1);
//			gp.addNode(testNode2);
////			gp.addNode(testNode3);
////			gp.addNode(testNode4);
////			gp.addNode(testNode5);
////			gp.addNode(testNode6);
////			gp.addNode(testNode7);
////			gp.addNode(testNode8);
////			gp.addNode(testNode9);
////			gp.addNode(testNode10);
////			gp.addNode(testNode11);
////			gp.addNode(testNode12);
//
//			gp.addEdge(edge0);
//			gp.addEdge(edge1);
//			gp.addEdge(edge2);
//			gp.addEdge(edge3);
////			gp.addEdge(edge4);
////			gp.addEdge(edge5);
////			gp.addEdge(edge6);
////			gp.addEdge(edge7);
////			gp.addEdge(edge8);
////			gp.addEdge(edge9);
////			gp.addEdge(edge10);
////			gp.addEdge(edge11);
////			gp.addEdge(edge12);
////			gp.addEdge(edge13);
////			gp.addEdge(edge14);
////			gp.addEdge(edge15);
////			gp.addEdge(edge16);
////			gp.addEdge(edge17);
////			gp.addEdge(edge18);
////			gp.addEdge(edge19);
////			gp.addEdge(edge20);
////			gp.addEdge(edge21);
////			gp.addEdge(edge22);
////			gp.addEdge(edge23);
////			gp.addEdge(edge24);
////			gp.addEdge(edge25);
////			gp.addEdge(edge26);
////			gp.addEdge(edge27);
//			
//			
////			Node startNode = graphDb.getNodeById(33186);
//			
//			// creating info node.
//			Map<MyNode, Node> info = new HashMap<MyNode, Node>();
//			
//			info.put(testNode0, startNode);
//			info.put(testNode2, endNode);
////			System.out.println("GP: "+ gp );
////			System.out.println("Info: "+ info );
//			
//
////			DiscoverablePeriodFinderWithDbAccess discoverablePeriodFinderFC = new DiscoverablePeriodFinderWithDbAccess(graphDb, AlgorithmType.FC);
////			Set<ValueRange> discoverablePeriodsFC = discoverablePeriodFinderFC.FCLBJTemporalInit(graphDb, gp, info);
//				
////			DiscoverablePeriodFinderWithDbAccess discoverablePeriodFinderCBJ = new DiscoverablePeriodFinderWithDbAccess(graphDb, AlgorithmType.FC_CBJ);
////			Set<ValueRange> discoverablePeriodsCBJ = discoverablePeriodFinderCBJ.FCLBJTemporalInit(graphDb, gp, info);
//			
//			
////			DiscoverablePeriodFinderWithDbAccess discoverablePeriodFinderLBJ = new DiscoverablePeriodFinderWithDbAccess(graphDb, AlgorithmType.FC_LBJ);
////			Set<ValueRange> discoverablePeriodsLBJ = discoverablePeriodFinderLBJ.FCLBJTemporalInit(graphDb, gp, info);
//			
////			DiscoverablePeriodFinderWithDbAccess discoverablePeriodFinderLBJImproved = new DiscoverablePeriodFinderWithDbAccess(graphDb, AlgorithmType.FC_LBJ_Improved);
////			Set<ValueRange> discoverablePeriodsLBJImproved = discoverablePeriodFinderLBJImproved.FCLBJTemporalInit(graphDb, gp, info);
////			
////			Iterator<ValueRange> discoverablePeriodIterator = discoverablePeriodsLBJImproved.iterator();
////			
////			boolean snapshotTimePointExists = false;
////			while(discoverablePeriodIterator.hasNext()) {
////				ValueRange discoverablePeriod = discoverablePeriodIterator.next();
////				System.out.println("Discoverable period:"+ discoverablePeriod );
////				if(discoverablePeriod.getMinimum() <= snapshotTimePoint && snapshotTimePoint <= discoverablePeriod.getMaximum()) {
////					snapshotTimePointExists = true;
////				}
////			}
//			
//			
//			DiscoverablePeriodFinderWithDbAccess discoverablePeriodFinderLBJImproved = new DiscoverablePeriodFinderWithDbAccess(graphDb, AlgorithmType.FC_LBJ_Improved);
//			IntervalTree discoverablePeriodsLBJImproved = discoverablePeriodFinderLBJImproved.FCLBJTemporalInit(graphDb, gp, info);
//			
//			System.out.println(discoverablePeriodsLBJImproved);
//			
//			Iterator discoverablePeriodIterator = discoverablePeriodsLBJImproved.iterator();
//
//			boolean snapshotTimePointExists = false;
//			while(discoverablePeriodIterator.hasNext()) {
//				Interval discoverablePeriod = (Interval) discoverablePeriodIterator.next();
//				System.out.println("Official period:"+ discoverablePeriod );
//				if((int)discoverablePeriod.first <= snapshotTimePoint && snapshotTimePoint <= (int) discoverablePeriod.second) {
//					snapshotTimePointExists = true;
//				}
//			}
////			
////			boolean isCBJandFCEqual = discoverablePeriodsFC.equals(discoverablePeriodsCBJ);
////			
//			if(snapshotTimePointExists) {
//				System.out.println("Pass");
//			
//			} else {
//				System.out.println("Fail");
//				
//			}
//			
//			
//			
//			
//			
////			Relationship r1 = graphDb.getRelationshipById(2386);
////			Relationship r2 = graphDb.getRelationshipById(11317);
////			Relationship r3 = graphDb.getRelationshipById(5567);
////			Relationship r4 = graphDb.getRelationshipById(10097);
////			Relationship r5 = graphDb.getRelationshipById(11317);
////			
////			System.out.println("Start time: " + r1.getProperties("startTime") + "End time: "+ r1.getProperties("endTime"));
////			System.out.println("Start time: " + r2.getProperties("startTime") + "End time: "+ r2.getProperties("endTime"));
////			System.out.println("Start time: " + r3.getProperties("startTime") + "End time: "+ r3.getProperties("endTime"));
////			System.out.println("Start time: " + r4.getProperties("startTime") + "End time: "+ r4.getProperties("endTime"));
////			System.out.println("Start time: " + r5.getProperties("startTime") + "End time: "+ r5.getProperties("endTime"));
//			
////			tx.success();
////		}
//		
//	}
//	
//	
//	public void createMiniHistoryGraphNodes(int numberOfNodes) {
//		
//		try (Transaction tx = graphDb.beginTx()){
//			
//			Label label = Label.label("testNode");
//			
//			for(int i=0; i<5; i++) {
//				Node node = graphDb.createNode(label);
//				
//				System.out.println(node.getId());
//				
//			}
//			
//
//			tx.success();
//		}
//	}
//	
//	
//	public void createMiniHistoryGraphRelationships() {
//		
//		
//		try (Transaction tx = graphDb.beginTx()){
//			
//			Node node0 = graphDb.getNodeById(33240);
//			Node node1 = graphDb.getNodeById(33241);
//			Node node2 = graphDb.getNodeById(33242);
//			Node node3 = graphDb.getNodeById(33243);
//			Node node4 = graphDb.getNodeById(33244);
//			RelType relType0 = RelType.R0;
//			RelType relType1 = RelType.R1;
//			RelType relType2 = RelType.R2;
//			RelType relType3 = RelType.R3;
//			RelType relType4 = RelType.R4;
//			
//			Relationship rel0 = node0.createRelationshipTo(node1, relType0);
//			Relationship rel1 = node0.createRelationshipTo(node2, relType1);
//			Relationship rel2 = node1.createRelationshipTo(node2, relType2);
//			Relationship rel3 = node2.createRelationshipTo(node3, relType3);
//			Relationship rel4 = node3.createRelationshipTo(node4, relType4);
//			
//			rel0.setProperty("startTime", 0);
//			rel0.setProperty("endTime", 100);
//			
//			rel1.setProperty("startTime", 1);
//			rel1.setProperty("endTime", 20);
//			
//			rel2.setProperty("startTime", 0);
//			rel2.setProperty("endTime", 100);
//			
//			rel3.setProperty("startTime", 0);
//			rel3.setProperty("endTime", 100);
//			
//			rel4.setProperty("startTime", 21);
//			rel4.setProperty("endTime", 25);
//			
//			tx.success();
//		}
//		
//		
//		
//	}
//	
//	
//	public void testOfficialPeriods() {
//		Set<ValueRange> discoverablePeriodsSet = new HashSet<ValueRange>();
//		discoverablePeriodsSet.add(ValueRange.of(1, 5));
//		discoverablePeriodsSet.add(ValueRange.of(6, 20));
//		discoverablePeriodsSet.add(ValueRange.of(2, 15));
//		discoverablePeriodsSet.add(ValueRange.of(10, 30));
//		discoverablePeriodsSet.add(ValueRange.of(50, 60));
//		discoverablePeriodsSet.add(ValueRange.of(55, 75));
//		discoverablePeriodsSet.add(ValueRange.of(100, 200));
//		discoverablePeriodsSet.add(ValueRange.of(0, 50));
//		discoverablePeriodsSet.add(ValueRange.of(150, Int.MaxValue()));
//		
//		OfficialPeriodFinder OfficialPeriodFinder = new OfficialPeriodFinder();
//		Set<ValueRange> officialPeriodsSet = OfficialPeriodFinder.findOfficialPeriods(discoverablePeriodsSet);
//		
//		
//		Iterator<ValueRange> officialPeriodsIterator = officialPeriodsSet.iterator();
//		while(officialPeriodsIterator.hasNext()) {
//			ValueRange officialPeriod = officialPeriodsIterator.next();
//			System.out.println("official period:"+ officialPeriod);
//		}
//		
////		System.out.println(officialPeriodsSet.size());
//	}
//	
//	
//	
//	// testing algorithm.
//	public static void testDiscoverablePeriodAlgorithm() {
//		
//		try (Transaction tx = graphDb.beginTx()){
////			long startTime = System.currentTimeMillis();
//			// creating graph pattern.
//			GraphPattern gp = new GraphPattern();
//			
//			MyNode requestorVertex = new MyNode(0, "Requestor");
//			MyNode intermediate1Vertex = new MyNode(1, "Intermediate1");
//			MyNode intermediate2Vertex = new MyNode(2, "Intermediate2");
//			MyNode intermediate3Vertex = new MyNode(3, "Intermediate3");
//			MyNode intermediate4Vertex = new MyNode(4, "Intermediate4");
//			MyNode resourceVertex = new MyNode(5, "Resource");
//			
//			RelType relType = RelType.RELTYPE;
//			RelType relType1 = RelType.RELTYPE1;
//			RelType relType2 = RelType.RELTYPE2;
//			RelType relType3 = RelType.RELTYPE3;
//			RelType relType4 = RelType.RELTYPE4;
//			RelType relType5 = RelType.RELTYPE5;
//			
//			MyEdge edge1 = new MyEdge(requestorVertex, intermediate1Vertex, relType1, 0);
//			MyEdge edge2 = new MyEdge(intermediate1Vertex, intermediate2Vertex, relType2, 1);
//			MyEdge edge3 = new MyEdge(intermediate2Vertex, intermediate3Vertex, relType3, 2);
//			MyEdge edge4 = new MyEdge(intermediate3Vertex, intermediate4Vertex, relType4, 3);
//			MyEdge edge5 = new MyEdge(intermediate4Vertex, resourceVertex, relType5, 4);
//			MyEdge edge6 = new MyEdge(requestorVertex, intermediate1Vertex, relType, 5);
//			MyEdge edge7 = new MyEdge(intermediate2Vertex, intermediate1Vertex, relType3, 6);
//            MyEdge edge8 = new MyEdge(intermediate1Vertex, intermediate3Vertex, relType5, 7);
//            MyEdge edge9 = new MyEdge(intermediate3Vertex, intermediate1Vertex, relType4, 8);
//						
//			gp.addNode(requestorVertex);
//			gp.addNode(intermediate1Vertex);
//			gp.addNode(intermediate2Vertex);
//			gp.addNode(intermediate3Vertex);
//			gp.addNode(intermediate4Vertex);
//			gp.addNode(resourceVertex);
//
//			gp.addEdge(edge1);
//			gp.addEdge(edge2);
//			gp.addEdge(edge3);
//			gp.addEdge(edge4);
//			gp.addEdge(edge5);
//			gp.addEdge(edge6);
//			gp.addEdge(edge7);
//			gp.addEdge(edge8);
//			gp.addEdge(edge9);
//			
//			
//			// creating info node.
//			Map<MyNode, Node> info = new HashMap<MyNode, Node>();
//			
//			Node requestorNode = graphDb.getNodeById(33034);
//			Node intermediate1Node = graphDb.getNodeById(33038);
//			Node intermediate2Node = graphDb.getNodeById(33036);
//			Node intermediate3Node = graphDb.getNodeById(33037);
//			Node intermediate4Node = graphDb.getNodeById(33039);
//			Node resourceNode = graphDb.getNodeById(33040);
//			
//			info.put(resourceVertex, resourceNode);
//			
//			// finding discoverable time periods.
//			DiscoverablePeriodFinder discoverablePeriodFinder = new DiscoverablePeriodFinder(AlgorithmType.FC);
//			
//			Set<ValueRange> discoverablePeriods = discoverablePeriodFinder.FCLBJTemporalInit(gp, info);
//			
//			Iterator<ValueRange> discoverablePeriodIterator = discoverablePeriods.iterator();
//			while(discoverablePeriodIterator.hasNext()) {
//				ValueRange discoverablePeriod = discoverablePeriodIterator.next();
//				System.out.println("Discoverable period:"+ discoverablePeriod );
//			}
//			tx.success();
//		}
//	}
//	
//	
//
//	public void checkUpdateSqlTable() {
//		
//		GraphPattern gp = new GraphPattern();
//		
//		MyNode testNode0 = new MyNode(0, "testNode");
//		MyNode testNode1 = new MyNode(1, "testNode");
//		MyNode testNode2 = new MyNode(2, "testNode");
//		MyNode testNode3 = new MyNode(3, "testNode");
//		MyNode testNode4 = new MyNode(4, "testNode");
//		MyNode testNode5 = new MyNode(5, "testNode");
//		MyNode testNode6 = new MyNode(6, "testNode");
//		
//		
//		RelType relType0 = RelType.R0;
//		RelType relType1 = RelType.R1;
//		RelType relType2 = RelType.R2;
//		RelType relType3 = RelType.R3;
//		RelType relType4 = RelType.R4;
//		RelType relType5 = RelType.R5;
//		RelType relType6 = RelType.R6;
//		
//		MyEdge edge0 = new MyEdge(testNode2, testNode3, relType3, 0);
//		MyEdge edge1 = new MyEdge(testNode2, testNode0, relType4, 1);
//		MyEdge edge2 = new MyEdge(testNode3, testNode2, relType4, 2);
//		MyEdge edge3 = new MyEdge(testNode3, testNode4, relType1, 3);
//		MyEdge edge4 = new MyEdge(testNode5, testNode1, relType3, 4);
//		MyEdge edge5 = new MyEdge(testNode6, testNode2, relType4, 5);
//		MyEdge edge6 = new MyEdge(testNode0, testNode1, relType6, 6);
//
//		
//		
//					
//		gp.addNode(testNode0);
//		gp.addNode(testNode1);
//		gp.addNode(testNode2);
//		gp.addNode(testNode3);
//		gp.addNode(testNode4);
//		gp.addNode(testNode5);
//		gp.addNode(testNode6);
//
//
//		gp.addEdge(edge0);
//		gp.addEdge(edge1);
//		gp.addEdge(edge2);
//		gp.addEdge(edge3);
//		gp.addEdge(edge4);
//		gp.addEdge(edge5);
//		gp.addEdge(edge6);
//
//		
//		
//		Node startNode = null;
//		Node endNode = null;
//		
//		try (Transaction tx = graphDb.beginTx()){
//			
//			// make sure to change the start node after new test.
//			startNode = graphDb.getNodeById(33091);
//			endNode = graphDb.getNodeById(33169);
//			
//			tx.success();
//		}
//		
//		
//		Map<MyNode, Node> info = new HashMap<MyNode, Node>();
//		
//		info.put(testNode0, startNode);
//		info.put(testNode6, endNode);
//		
//		UpdatingOfficialPeriodsTable updatingOfficialPeriodsTable = new UpdatingOfficialPeriodsTable();
//		
//		updatingOfficialPeriodsTable.updateOfficialPeriodTables(graphDb, gp, 0, info);
//		
//		
//	}
//		
//	
//	public void writingToFile() {
//		
//		try {
//			
//			Pair<GraphPattern, Map<MyNode, Node>> gpInfoPair = null;
//			try (Transaction tx = graphDb.beginTx()){
//				
//				int totalGraphNodes = 200;
//				
//				RandomGPCreator randomGPCreator = new RandomGPCreator(graphDb, new Random(), 5, 0, 50, totalGraphNodes);
//				
//				gpInfoPair = randomGPCreator.getRandomGP();
//				
////				RandomGPCreator randomGPCreator = new RandomGPCreator(graphDb, new Random(), 5, 0, 50, totalGraphNodes);
//				
//				
//				tx.success();
//			}
//			
//			FileOutputStream f = new FileOutputStream(new File("myObjects.txt"));
//			ObjectOutputStream o = new ObjectOutputStream(f);
//			
//			
//			MyNode myNode = gpInfoPair.second.keySet().iterator().next();
////			gpInfoPair.second.get(myNode);
//			
//			Pair<MyNode, Long> vertexNodePair =  new Pair(myNode, gpInfoPair.second.get(myNode).getId());
//			
//			
//			Pair<GraphPattern, Pair<MyNode, Long>> gpInfoPairIn = new Pair(gpInfoPair.first, vertexNodePair);
//			
//			// Write objects to file
//			o.writeObject(gpInfoPairIn);
//
//			o.close();
//			f.close();
//
//			FileInputStream fi = new FileInputStream(new File("myObjects.txt"));
//			ObjectInputStream oi = new ObjectInputStream(fi);
//
//			// Read objects
//			Pair<GraphPattern, Pair<MyNode, Long>> gpInfoPairOut = (Pair<GraphPattern, Pair<MyNode, Long>>) oi.readObject();
////			Person pr2 = (Person) oi.readObject();
//
//			System.out.println(gpInfoPairOut.toString());
////			System.out.println(pr2.toString());
//
//			
//			try (Transaction tx = graphDb.beginTx()){
//				
//				MyNode outMyNode = gpInfoPairOut.second.first;
//				
//				Node outNode = graphDb.getNodeById(gpInfoPairOut.second.second);
//
//				Map outMap = new HashMap();
//				
//				outMap.put(outMyNode, outNode);
//				
//				Pair<GraphPattern, Map<MyNode, Node>> gpInfoPairOuput = new Pair (gpInfoPairOut.first, outMap); 
//				
//				tx.success();
//			}
//			
//
//			
//			oi.close();
//			fi.close();
//
//		} catch (FileNotFoundException e) {
//			System.out.println("File not found");
//		} catch (IOException e) {
//			System.out.println("Error initializing stream");
//		} catch (ClassNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
//	}
//	
//	
//	public void testPolicyTranslation() {
//		
//		AllenRelation[] allenRelationsList = new AllenRelation[] {AllenRelation.equals, AllenRelation.finishedBy, AllenRelation.finishes, AllenRelation.meets, AllenRelation.metBy, AllenRelation.overlappedBy, AllenRelation.overlaps, AllenRelation.precededBy, AllenRelation.precedes, AllenRelation.startedBy, AllenRelation.starts};
//
//		RebacRelationIdentifier rebacRelationIdentifierNext = new RebacRelationIdentifier(33169, 33091, 0);
//		
//		ExistentialQuantifier existentialQuantifierNext = ExistentialQuantifier.Regular;
//		
//		PolicyPrefix policyPrefix1 = new PolicyPrefixImpl(existentialQuantifierNext, null, rebacRelationIdentifierNext, null);
//		
//		RebacRelationIdentifier rebacRelationIdentifier = new RebacRelationIdentifier(33170, 33091, 0);
//		
//		ExistentialQuantifier existentialQuantifier = ExistentialQuantifier.Regular;
//				
//		PolicyPrefixImpl policyPrefix2 = new PolicyPrefixImpl(existentialQuantifier, null, rebacRelationIdentifier, policyPrefix1);
//		
//		PolicyPrefixImpl policyPrefix3 = new PolicyPrefixImpl(existentialQuantifier, null, rebacRelationIdentifier, policyPrefix2);
//		
//		PolicyPrefixImpl policyPrefix4 = new PolicyPrefixImpl(existentialQuantifier, null, rebacRelationIdentifier, policyPrefix3);
//	
//		PolicyPrefixImpl policyPrefix5 = new PolicyPrefixImpl(existentialQuantifier, null, rebacRelationIdentifier, policyPrefix4);
//	
//		PolicyPrefixImpl policyPrefix6 = new PolicyPrefixImpl(existentialQuantifier, null, rebacRelationIdentifier, policyPrefix5);
//		
//		PolicyPrefixImpl policyPrefix7 = new PolicyPrefixImpl(existentialQuantifier, null, rebacRelationIdentifier, policyPrefix6);
//		
//		PolicyPrefixImpl policyPrefix8 = new PolicyPrefixImpl(existentialQuantifier, null, rebacRelationIdentifier, policyPrefix7);
//		PolicyPrefixImpl policyPrefix9 = new PolicyPrefixImpl(existentialQuantifier, null, rebacRelationIdentifier, policyPrefix8);
//		PolicyPrefixImpl policyPrefix10 = new PolicyPrefixImpl(existentialQuantifier, null, rebacRelationIdentifier, policyPrefix9);
//		PolicyPrefixImpl policyPrefix11 = new PolicyPrefixImpl(existentialQuantifier, null, rebacRelationIdentifier, policyPrefix10);
//		PolicyPrefixImpl policyPrefix12 = new PolicyPrefixImpl(existentialQuantifier, null, rebacRelationIdentifier, policyPrefix11);
//		PolicyPrefixImpl policyPrefix13 = new PolicyPrefixImpl(existentialQuantifier, null, rebacRelationIdentifier, policyPrefix12);
//		PolicyPrefixImpl policyPrefix14 = new PolicyPrefixImpl(existentialQuantifier, null, rebacRelationIdentifier, policyPrefix13);
//		
//		Set<AllenRelation> allenRelations = new HashSet<AllenRelation>();
//		allenRelations.addAll(Arrays.asList(allenRelationsList));
//		
//		AtomicValue atomicValue1 = new AtomicValueImpl(rebacRelationIdentifier, rebacRelationIdentifierNext, allenRelations);
//		AtomicValue atomicValue2 = new AtomicValueImpl(rebacRelationIdentifier, rebacRelationIdentifierNext, allenRelations);
//		
//		DisjunctionMatrixImpl disjunctionMatrix1 = new DisjunctionMatrixImpl(atomicValue1, atomicValue2);
//		DisjunctionMatrixImpl disjunctionMatrix2 = new DisjunctionMatrixImpl(atomicValue1, atomicValue2);
//		DisjunctionMatrixImpl disjunctionMatrix3 = new DisjunctionMatrixImpl(atomicValue1, atomicValue2);
//		DisjunctionMatrixImpl disjunctionMatrix4 = new DisjunctionMatrixImpl(atomicValue1, atomicValue2);
//		
//		ConjunctionMatrixImpl conjunctionMatrix1 = new ConjunctionMatrixImpl(disjunctionMatrix1, atomicValue2);
//		ConjunctionMatrixImpl conjunctionMatrix2 = new ConjunctionMatrixImpl(disjunctionMatrix2, atomicValue2);
//		ConjunctionMatrixImpl conjunctionMatrix3 = new ConjunctionMatrixImpl(disjunctionMatrix3, atomicValue2);
//		ConjunctionMatrixImpl conjunctionMatrix4 = new ConjunctionMatrixImpl(disjunctionMatrix4, atomicValue2);
//		
//		
//		DisjunctionMatrixImpl disjunctionMatrix5 = new DisjunctionMatrixImpl(conjunctionMatrix4, conjunctionMatrix3);
//		DisjunctionMatrixImpl disjunctionMatrix6 = new DisjunctionMatrixImpl(conjunctionMatrix1, conjunctionMatrix2);
//		
//		ConjunctionMatrixImpl conjunctionMatrix = new ConjunctionMatrixImpl(disjunctionMatrix5, disjunctionMatrix6);
//		
//		NegationMatrixImpl negationMatrix = new NegationMatrixImpl(conjunctionMatrix);
//		
//		PolicyImpl policy = new PolicyImpl(policyPrefix14, negationMatrix);
//		
//		PolicyToSqlTraslator policyToSqlTraslator = new PolicyToSqlTraslator();
//		String policySqlString = policyToSqlTraslator.policyTranslator(policy);
//		
//		MysqlConnection mysqlConnection = new MysqlConnection();
//		Connection mysqlConn =  mysqlConnection.mysqlConn;
//		
//		PolicyExecute policyExecute = new PolicyExecute();
//		int resultRowsCount = policyExecute.executePolicySql(mysqlConn, policySqlString);
//		
//		System.out.println(policySqlString);
//		System.out.println(resultRowsCount);
//		
//	}
//	
//	
//	public static void testIntervalTree() {
//		
//		IntervalTree intervalTree = new IntervalTree();
//				
//		Interval interval1 = Interval.toInterval(5, 10);
//		
//		Interval interval3 = Interval.toInterval(100, 110);
//		Interval interval4 = Interval.toInterval(2, 50);
//		Interval interval2 = Interval.toInterval(15, 30);
//		
//		intervalTree.addNonNested(interval1);
//		intervalTree.addNonNested(interval2);
//		intervalTree.addNonNested(interval3);
//		intervalTree.addNonNested(interval4);
//
//		
////		intervalTree.add
//		
//		
//		
////		while(intervalTreeIterator.hasNext()) {
////			Interval interval = (Interval) intervalTreeIterator.next();
////			System.out.println(interval);
////		}
//		
//		OfficialPeriodFinder officialPeriodFinder = new OfficialPeriodFinder();
//		
//		intervalTree = officialPeriodFinder.findOfficialPeriods(intervalTree);
//		
//		Iterator intervalTreeIterator =  intervalTree.iterator();
//		while(intervalTreeIterator.hasNext()) {
//			Interval interval = (Interval) intervalTreeIterator.next();
////			System.out.println(interval);
//		}
//		
////		System.out.println(intervalTree);
//	}
//	
//	
//	public void testEvaluation() {
//		
////		EvaluateDiscoverablePeriodFinder evaluateDiscoverablePeriodFinder = new EvaluateDiscoverablePeriodFinder();	
//		
////		try {
////			
////			testIntervalTree();
////			
////			Set<Pair<GraphPattern, Map<MyNode, Node>>> gpInfoPairSet = evaluateDiscoverablePeriodFinder.readGPInfoPairFromFile(10, 10000, 1000, 13);
////			
////			evaluateDiscoverablePeriodFinder.graphDb = graphDb;
////			evaluateDiscoverablePeriodFinder.evaluateDiscoverblePeriodFinderTimePointPresent(null);
////			
////		} catch (IOException e) {
////			// TODO Auto-generated catch block
////			e.printStackTrace();
////		}
//	}
//	
//	public void testGenerateRandomGPInfo() {
//		
//		Node startNode = null;
//		Node endNode = null;
//		
//		
//		
//		try (Transaction tx = graphDb.beginTx()){
//			
//			Random random = new Random();
//			
//			int startNodeRandomIndex = random.nextInt(1000);
//			Iterator<Node> startNodeIterator = graphDb.getAllNodes().iterator();		
//			for (int i = 0; i < startNodeRandomIndex; i++) {
//				startNodeIterator.next();
//			}
//			
//			int endNodeRandomIndex = random.nextInt(1000);
//			Iterator<Node> endNodeIterator = graphDb.getAllNodes().iterator();		
//			for (int i = 0; i < endNodeRandomIndex; i++) {
//				endNodeIterator.next();
//			}
//			
//			startNode = startNodeIterator.next();
//			endNode = endNodeIterator.next();
//		
//			tx.success();
//		}
//		
//		
//		GraphPattern graphPattern = testGenerateRandomGP(8);
//		
//		Map<MyNode, Node> info = new HashMap<MyNode, Node>();
//		
//		info.put(graphPattern.getNodes().get(0), startNode);
//		info.put(graphPattern.getNodes().get(1), endNode);
//		
//		
//		DiscoverablePeriodFinderWithDbAccess discoverablePeriodFinderLBJImproved = new DiscoverablePeriodFinderWithDbAccess(graphDb, AlgorithmType.FC_LBJ_Improved);
//		
//		long startTime = System.currentTimeMillis();
//		IntervalTree discoverablePeriodsLBJImproved = discoverablePeriodFinderLBJImproved.FCLBJTemporalInit(graphDb, graphPattern, info);
//		
//		long endTime   = System.currentTimeMillis();
//		long timeForRun = endTime - startTime;
//		
//		totalTimeArray.add(timeForRun);
//		
//		System.out.println(discoverablePeriodsLBJImproved);
//	}
//	
//	
//	public GraphPattern testGenerateRandomGP(int numberOfNodes) {
//		
////		https://stackoverflow.com/questions/2041517/random-simple-connected-graph-generation-with-given-sparseness
//		
//		MyNode startNode = new MyNode(0, "testNode");
//		
//		List<MyNode> nodesSet = new ArrayList<>();
//		List<MyEdge> edgeSet = new ArrayList<>();
//		
//		nodesSet.add(startNode);
//		
//		Random random = new Random();
//		
//		for (int i=1; i < numberOfNodes; i++) {
//			
//			MyNode nextNode = new MyNode(i, "testNode");
//			
//			int randomNode = random.nextInt(nodesSet.size());
//			int randomRelationship = random.nextInt(6);
//			
//			MyEdge edge = new MyEdge(nextNode, (MyNode) nodesSet.get(randomNode), getRelationshipType(randomRelationship), i);
//			
//			nodesSet.add(nextNode);
//			edgeSet.add(edge);
//		}
//		
//		GraphPattern graphPattern = new GraphPattern();
//		
//		for(MyNode node: nodesSet) {
//			graphPattern.addNode(node);
//		}
//		
//		for(MyEdge edge: edgeSet) {
//			graphPattern.addEdge(edge);
//		}
//		
//		return graphPattern;
//	}
//	
//	
//	public void testGraphFromCSV() {
//		
//		SocialGraphCreator graphFromCSV = new SocialGraphCreator(graphDb);
//		try {
//			
//			graphFromCSV.createMatrixTriple();
//			graphFromCSV.createNeo4jNodesInBatch("nodes.csv");
//			graphFromCSV.createNeo4jRelationshipsInBatch("relationships.csv");
//			
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
//	
//	public RelType getRelationshipType(int i) {
//		
//		switch(i) {
//		  case 0:
//			  return RelType.R0;
//		  case 1:
//			  return RelType.R1;
//		  case 2:
//			  return RelType.R2;
//		  case 3:
//			  return RelType.R3;
//		  case 4:
//			  return RelType.R4;
//		  case 5:
//			  return RelType.R5;
//		  case 6:
//			  return RelType.R6;
//		  default:
//			  break;
//		}
//		
//		return null;
//	}
	
	
	
	
}
