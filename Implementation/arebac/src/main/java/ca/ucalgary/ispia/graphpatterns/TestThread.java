package ca.ucalgary.ispia.graphpatterns;

import java.time.temporal.ValueRange;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;

import ca.ucalgary.ispia.graphpatterns.gpchecker.opt.impl.DiscoverablePeriodFinderCBJ;
import ca.ucalgary.ispia.graphpatterns.graph.GraphPattern;
import ca.ucalgary.ispia.graphpatterns.graph.MyEdge;
import ca.ucalgary.ispia.graphpatterns.graph.MyNode;
import ca.ucalgary.ispia.graphpatterns.graph.RelType;

public class TestThread implements Runnable {

	@Override
	public void run() {
		int snapshotTimePoint = 50;
		Node startNode = null;
		
		Driver d = new Driver();
		GraphDatabaseService graphDb = d.getGraphDb("graph.db");
		
		try (Transaction tx = graphDb.beginTx()){
			
			// make sure to change the start node after new test.
			startNode = graphDb.getNodeById(33068);
//			tx.success();
//		}
			GraphPattern gp = new GraphPattern();
			
			MyNode testNode0 = new MyNode(0, "testNode");
			MyNode testNode1 = new MyNode(1, "testNode");
			MyNode testNode2 = new MyNode(2, "testNode");
			MyNode testNode3 = new MyNode(3, "testNode");
			MyNode testNode4 = new MyNode(4, "testNode");
			MyNode testNode5 = new MyNode(5, "testNode");
			MyNode testNode6 = new MyNode(6, "testNode");
			MyNode testNode7 = new MyNode(7, "testNode");
//			MyNode testNode8 = new MyNode(8, "testNode");
//			MyNode testNode9 = new MyNode(9, "testNode");
//			MyNode testNode10 = new MyNode(10, "testNode");
//			MyNode testNode11 = new MyNode(11, "testNode");
//			MyNode testNode12 = new MyNode(12, "testNode");
			
			
			RelType relType0 = RelType.R0;
			RelType relType1 = RelType.R1;
			RelType relType2 = RelType.R2;
			RelType relType3 = RelType.R3;
			RelType relType4 = RelType.R4;
			RelType relType5 = RelType.R5;
			RelType relType6 = RelType.R6;
			
			MyEdge edge0 = new MyEdge(testNode1, testNode2, relType6, 0);
			MyEdge edge1 = new MyEdge(testNode1, testNode6, relType0, 1);
			MyEdge edge2 = new MyEdge(testNode2, testNode5, relType0, 2);
			MyEdge edge3 = new MyEdge(testNode3, testNode1, relType0, 3);
			MyEdge edge4 = new MyEdge(testNode5, testNode7, relType2, 4);
			MyEdge edge5 = new MyEdge(testNode6, testNode6, relType4, 5);
			MyEdge edge6 = new MyEdge(testNode6, testNode5, relType4, 6);
			MyEdge edge7 = new MyEdge(testNode0, testNode1, relType1, 7);
			MyEdge edge8 = new MyEdge(testNode0, testNode4, relType3, 8);
//			MyEdge edge9 = new MyEdge(testNode5, testNode7, relType1, 9);
//			MyEdge edge10 = new MyEdge(testNode6, testNode10, relType2, 10);
//			MyEdge edge11 = new MyEdge(testNode7, testNode7, relType2, 11);
//			MyEdge edge12 = new MyEdge(testNode7, testNode4, relType5, 12);
//			MyEdge edge13 = new MyEdge(testNode7, testNode12, relType1, 13);
//			MyEdge edge14 = new MyEdge(testNode8, testNode5, relType6, 14);
//			MyEdge edge15 = new MyEdge(testNode9, testNode3, relType4, 15);
//			MyEdge edge16 = new MyEdge(testNode9, testNode6, relType4, 16);
//			MyEdge edge17 = new MyEdge(testNode11, testNode9, relType4, 17);
//			MyEdge edge18 = new MyEdge(testNode11, testNode4, relType5, 18);
//			MyEdge edge19 = new MyEdge(testNode0, testNode1, relType1, 19);
//			MyEdge edge20 = new MyEdge(testNode0, testNode9, relType2, 20);
//			MyEdge edge21 = new MyEdge(testNode0, testNode6, relType2, 21);
//			MyEdge edge22 = new MyEdge(testNode0, testNode3, relType5, 21);
//			MyEdge edge23 = new MyEdge(testNode0, testNode3, relType6, 21);
//			MyEdge edge24 = new MyEdge(testNode0, testNode7, relType0, 21);
//			MyEdge edge25 = new MyEdge(testNode0, testNode5, relType2, 21);
//			MyEdge edge26 = new MyEdge(testNode0, testNode2, relType1, 21);
//			MyEdge edge27 = new MyEdge(testNode0, testNode3, relType2, 21);
			
			
						
			gp.addNode(testNode0);
			gp.addNode(testNode1);
			gp.addNode(testNode2);
			gp.addNode(testNode3);
			gp.addNode(testNode4);
			gp.addNode(testNode5);
			gp.addNode(testNode6);
			gp.addNode(testNode7);
//			gp.addNode(testNode8);
//			gp.addNode(testNode9);
//			gp.addNode(testNode10);
//			gp.addNode(testNode11);
//			gp.addNode(testNode12);

			gp.addEdge(edge0);
			gp.addEdge(edge1);
			gp.addEdge(edge2);
			gp.addEdge(edge3);
			gp.addEdge(edge4);
			gp.addEdge(edge5);
			gp.addEdge(edge6);
			gp.addEdge(edge7);
			gp.addEdge(edge8);
//			gp.addEdge(edge9);
//			gp.addEdge(edge10);
//			gp.addEdge(edge11);
//			gp.addEdge(edge12);
//			gp.addEdge(edge13);
//			gp.addEdge(edge14);
//			gp.addEdge(edge15);
//			gp.addEdge(edge16);
//			gp.addEdge(edge17);
//			gp.addEdge(edge18);
//			gp.addEdge(edge19);
//			gp.addEdge(edge20);
//			gp.addEdge(edge21);
//			gp.addEdge(edge22);
//			gp.addEdge(edge23);
//			gp.addEdge(edge24);
//			gp.addEdge(edge25);
//			gp.addEdge(edge26);
//			gp.addEdge(edge27);
			
			
//			Node startNode = graphDb.getNodeById(33186);
			
			// creating info node.
			Map<MyNode, Node> info = new HashMap<MyNode, Node>();
			
			info.put(testNode0, startNode);
//			System.out.println("GP: "+ gp );
//			System.out.println("Info: "+ info );
			

//			DiscoverablePeriodFinderWithDbAccess discoverablePeriodFinder = new DiscoverablePeriodFinderWithDbAccess(graphDb);
//			Set<ValueRange> discoverablePeriods = discoverablePeriodFinder.FCLBJTemporalInit(graphDb, gp, info);
			
			DiscoverablePeriodFinderCBJ discoverablePeriodFinder = new DiscoverablePeriodFinderCBJ(graphDb);
			Set<ValueRange> discoverablePeriods = discoverablePeriodFinder.CBJTemporalInit(graphDb, gp, info);
			
//			DiscoverablePeriodFinder discoverablePeriodFinder = new DiscoverablePeriodFinder(AlgorithmType.FC, new ArrayList<ValueRange>());	
//			Set<ValueRange> discoverablePeriods = discoverablePeriodFinder.FCLBJTemporalInit(gp, info);
			
			
			Iterator<ValueRange> discoverablePeriodIterator = discoverablePeriods.iterator();
			
			boolean snapshotTimePointExists = false;
			while(discoverablePeriodIterator.hasNext()) {
				ValueRange discoverablePeriod = discoverablePeriodIterator.next();
//					System.out.println("Discoverable period:"+ discoverablePeriod );
				if(discoverablePeriod.getMinimum() <= snapshotTimePoint && snapshotTimePoint <= discoverablePeriod.getMaximum()) {
					snapshotTimePointExists = true;
				}
			}
			System.out.println("Discoverable period:"+ discoverablePeriods );
			

			if(!snapshotTimePointExists) {
				System.out.println("Fail");
			
			} else {
				System.out.println("Pass");
			}
			
			
			
			
			
//			Relationship r1 = graphDb.getRelationshipById(2386);
//			Relationship r2 = graphDb.getRelationshipById(11317);
//			Relationship r3 = graphDb.getRelationshipById(5567);
//			Relationship r4 = graphDb.getRelationshipById(10097);
//			Relationship r5 = graphDb.getRelationshipById(11317);
//			
//			System.out.println("Start time: " + r1.getProperties("startTime") + "End time: "+ r1.getProperties("endTime"));
//			System.out.println("Start time: " + r2.getProperties("startTime") + "End time: "+ r2.getProperties("endTime"));
//			System.out.println("Start time: " + r3.getProperties("startTime") + "End time: "+ r3.getProperties("endTime"));
//			System.out.println("Start time: " + r4.getProperties("startTime") + "End time: "+ r4.getProperties("endTime"));
//			System.out.println("Start time: " + r5.getProperties("startTime") + "End time: "+ r5.getProperties("endTime"));
			
//			tx.success();
		}
		
	}

}
