package ca.ucalgary.ispia.graphpatterns.tests;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.ResourceIterable;
import org.neo4j.graphdb.Transaction;

import ca.ucalgary.ispia.graphpatterns.gpchecker.opt.GPHGCheckerLBJFC;
import ca.ucalgary.ispia.graphpatterns.gpchecker.opt.impl.DiscoverablePeriodFinder;
import ca.ucalgary.ispia.graphpatterns.graph.GPHolder;
import ca.ucalgary.ispia.graphpatterns.graph.GraphPattern;
import ca.ucalgary.ispia.graphpatterns.graph.MyEdge;
import ca.ucalgary.ispia.graphpatterns.graph.MyNode;
import ca.ucalgary.ispia.graphpatterns.graph.RelType;

public class HelperFunctionsTests {
	
	static GPHGCheckerLBJFC gpHGCheckerLBJFC =  new GPHGCheckerLBJFC();
	static GraphDatabaseService graphDb = null;
	
	// testing helper function of the algorithm.
		public static void testGetRelevantRelationships() {
			
			try (Transaction tx = graphDb.beginTx()){
				
				Node assignedVertex  = graphDb.getNodeById(33034);
				
				GraphPattern gp = new GraphPattern();
				MyNode sourceVertex = new MyNode(0, "source");
				MyNode targetVertex = new MyNode(1, "target1");
				MyNode target2Vertex = new MyNode(2, "target2");
				
				RelType relType1 = RelType.RELTYPE1;
				RelType relType2 = RelType.RELTYPE2;
				
				MyEdge edge = new MyEdge(sourceVertex, targetVertex, relType1, 0);
				MyEdge edge2 = new MyEdge(targetVertex, target2Vertex, relType2, 1);
				
				gp.addNode(sourceVertex);
				gp.addNode(targetVertex);
				gp.addNode(target2Vertex);
				gp.addEdge(edge);;
				gp.addEdge(edge2);
				
				DiscoverablePeriodFinder discoverablePeriodFinder = new DiscoverablePeriodFinder();
				
				List<Relationship> relationships = discoverablePeriodFinder.GetRelevantRelationships(gp, edge, sourceVertex, assignedVertex);
							
				tx.success();
			}
			
		}
		
		public static void testPickNextEdge() {
			Map<MyEdge, List<Relationship>> candEdges = new HashMap();
			MyEdge edge = new MyEdge(null, null, null, 0);
			List<Relationship> relationships = new ArrayList<Relationship>();
			
			candEdges.put(edge, relationships);
			
			DiscoverablePeriodFinder discoverablePeriodFinder = new DiscoverablePeriodFinder();
			
			MyEdge nextEdge = discoverablePeriodFinder.PickNextEdge(candEdges);
			
			System.out.println(nextEdge.getId());
		}
		
		public static void testValueOrdering() {
			List<Relationship> relationships = new ArrayList<Relationship>();
			
			try (Transaction tx = graphDb.beginTx()){
				
				ResourceIterable<Relationship> relationshipIterable  = graphDb.getAllRelationships();
				
				Iterator<Relationship> relationshipIterator = relationshipIterable.iterator();
				
//				System.out.println("Unsorted start time:");
				
				while(relationshipIterator.hasNext()) {
					Relationship relationship = relationshipIterator.next();
					relationships.add(relationship);
					
//					Object startTimeObject = relationship.getProperty("startTime");
//					int startTime = Integer.valueOf(startTimeObject.toString());
//					Object endTimeObject = relationship.getProperty("endTime");
//					int endTime = Integer.valueOf(endTimeObject.toString());
//					System.out.println(startTime);
					
				}
				
//				System.out.println("Sorted start time:");
				
				DiscoverablePeriodFinder discoverablePeriodFinder = new DiscoverablePeriodFinder();
				relationships = discoverablePeriodFinder.ValueOrdering(relationships);
				Iterator<Relationship> itr = relationships.iterator();
				while(itr.hasNext()){
					Relationship relationship = itr.next();
					Map<String, Object> relationshipProperties = relationship.getAllProperties();
					Object startTimeObject = relationshipProperties.get("startTime");
					int startTime = Integer.valueOf(startTimeObject.toString());
//					System.out.println(startTime);
				}
				
				tx.success();
			}
			
		}
		
		public static void testBothVerticesNotAssigned() {
			Map<MyNode, Node> assignmentVertices = new HashMap<MyNode, Node>();
			
			MyNode sourceNode = new MyNode(0,"source");
			MyNode targetNode = new MyNode(1,"target");
			
			MyEdge edge = new MyEdge(sourceNode, targetNode, null, 0);
			
			Node node = null;
			
			assignmentVertices.put(sourceNode, null);
			assignmentVertices.put(targetNode, null);
			
			DiscoverablePeriodFinder discoverablePeriodFinder = new DiscoverablePeriodFinder();
			boolean bothVerticeNotAssigned = discoverablePeriodFinder.BothVerticesNotAssigned(edge, assignmentVertices);
			
			System.out.println(bothVerticeNotAssigned);
		}
		
		
		public static void testAssignOtherVertex() {
			
			List<Relationship> relationships = new ArrayList<Relationship>();
			
			try (Transaction tx = graphDb.beginTx()){
				
				ResourceIterable<Relationship> relationshipIterable  = graphDb.getAllRelationships();
				
				Iterator<Relationship> relationshipIterator = relationshipIterable.iterator();
				
				while(relationshipIterator.hasNext()) {
					Relationship relationship = relationshipIterator.next();
					relationships.add(relationship);
				}
				
				Map<MyNode, Node> assignmentVertices = new HashMap<MyNode, Node>();
				
				MyNode sourceNode = new MyNode(0,"source");
				MyNode targetNode = new MyNode(1,"target");
				
				MyEdge edge = new MyEdge(sourceNode, targetNode, null, 0);
				
				assignmentVertices.put(targetNode, relationships.get(0).getEndNode());
				
				DiscoverablePeriodFinder discoverablePeriodFinder = new DiscoverablePeriodFinder();
				
				discoverablePeriodFinder.AssignOtherVertex(edge, relationships.get(0), assignmentVertices);
					
				tx.success();
			}
		}
		
			
		public static void testGetRelevantEdges() {
			GraphPattern gp = new GraphPattern();
			MyNode sourceVertex = new MyNode(0, "source");
			MyNode targetVertex = new MyNode(1, "target1");
			MyNode target2Vertex = new MyNode(2, "target2");
			
			MyEdge edge = new MyEdge(sourceVertex, targetVertex, null, 0);
			MyEdge edge2 = new MyEdge(sourceVertex, target2Vertex, null, 1);
			
			gp.addNode(sourceVertex);
			gp.addNode(targetVertex);
			gp.addEdge(edge);;
			gp.addEdge(edge2);
			
			DiscoverablePeriodFinder discoverablePeriodFinder = new DiscoverablePeriodFinder();
			Set<MyEdge> edges = discoverablePeriodFinder.GetRelevantEdges(gp, sourceVertex);
			
		}
		
		
		public static void testGetOverlappingRelationships() {
			List<Relationship> relationships = new ArrayList<Relationship>();
			
			List<Relationship> assignedRelationships = new ArrayList<Relationship>();
	 
			try (Transaction tx = graphDb.beginTx()){
				
				ResourceIterable<Relationship> relationshipIterable  = graphDb.getAllRelationships();
				
				assignedRelationships.add(graphDb.getRelationshipById(73));
				assignedRelationships.add(graphDb.getRelationshipById(213));
				assignedRelationships.add(graphDb.getRelationshipById(216));
				
				Iterator<Relationship> relationshipIterator = relationshipIterable.iterator();
				
				while(relationshipIterator.hasNext()) {
					Relationship relationship = relationshipIterator.next();
					relationships.add(relationship);
				}
				
				DiscoverablePeriodFinder discoverablePeriodFinder = new DiscoverablePeriodFinder();
				List<Relationship> filteredRelationship = discoverablePeriodFinder.GetOverlappingRelationships(relationships, assignedRelationships); 
				
//				System.out.println("Stop point");
			}
			
		}
		
		
		public static void testGetOverlappingRelationship() {
			List<Relationship> relationships = new ArrayList<Relationship>();
			
			Relationship assignedRelationship = null;
	 		
			try (Transaction tx = graphDb.beginTx()){
				
				ResourceIterable<Relationship> relationshipIterable  = graphDb.getAllRelationships();
				
				assignedRelationship = graphDb.getRelationshipById(73);
				
				Iterator<Relationship> relationshipIterator = relationshipIterable.iterator();
				
				while(relationshipIterator.hasNext()) {
					Relationship relationship = relationshipIterator.next();
					relationships.add(relationship);
				}
				
				DiscoverablePeriodFinder discoverablePeriodFinder = new DiscoverablePeriodFinder();
				List<Relationship> filteredRelationship = discoverablePeriodFinder.GetOverlappingRelationships(relationships, assignedRelationship); 
				
//				System.out.println("Stop point");
			}
			
		}
		
		
		public static void testFindCommonRelationships() {
			
			List<Relationship> relationships = new ArrayList<Relationship>();
			List<Relationship> candiadateRelationships = new ArrayList<Relationship>();
	 
			try (Transaction tx = graphDb.beginTx()){
				
				ResourceIterable<Relationship> relationshipIterable  = graphDb.getAllRelationships();
				
				relationships.add(graphDb.getRelationshipById(73));
				relationships.add(graphDb.getRelationshipById(213));
				relationships.add(graphDb.getRelationshipById(216));
				
				Iterator<Relationship> relationshipIterator = relationshipIterable.iterator();
				
				while(relationshipIterator.hasNext()) {
					Relationship relationship = relationshipIterator.next();
					candiadateRelationships.add(relationship);
				}
				
				candiadateRelationships.remove(graphDb.getRelationshipById(213));
				
				DiscoverablePeriodFinder discoverablePeriodFinder = new DiscoverablePeriodFinder();
				List<Relationship> filteredRelationship = discoverablePeriodFinder.FindCommonRelationships(candiadateRelationships, relationships); 
				
//				System.out.println("Stop point");
			}
		}
		
		
		public static void testFilterSource() {
			
			try (Transaction tx = graphDb.beginTx()){
				
				List<Relationship> candRelationships = new ArrayList<Relationship>();
				
				ResourceIterable<Relationship> relationshipIterable  = graphDb.getAllRelationships();
				
				candRelationships.add(graphDb.getRelationshipById(73));
				candRelationships.add(graphDb.getRelationshipById(213));
				candRelationships.add(graphDb.getRelationshipById(216));
				candRelationships.add(graphDb.getRelationshipById(74));
				
				Node sourceNode = graphDb.getNodeById(33034);
				
				DiscoverablePeriodFinder discoverablePeriodFinder = new DiscoverablePeriodFinder();
				List<Relationship> filteredRelationship = discoverablePeriodFinder.FilterSource(candRelationships, sourceNode); 
				
//				System.out.println("Stop point");
			}
		}
		
		public static void testFilterTarget() {
			
			try (Transaction tx = graphDb.beginTx()){
				
				List<Relationship> candRelationships = new ArrayList<Relationship>();
				
				ResourceIterable<Relationship> relationshipIterable  = graphDb.getAllRelationships();
				
				candRelationships.add(graphDb.getRelationshipById(73));
				candRelationships.add(graphDb.getRelationshipById(213));
				candRelationships.add(graphDb.getRelationshipById(216));
				candRelationships.add(graphDb.getRelationshipById(74));
				
				Node targetNode = graphDb.getNodeById(33038);
				
				DiscoverablePeriodFinder discoverablePeriodFinder = new DiscoverablePeriodFinder();
				List<Relationship> filteredRelationship = discoverablePeriodFinder.FilterTarget(candRelationships, targetNode); 
				
//				System.out.println("Stop point");
			}
		}
		
		
		public static void testPartialDiscoverableTimeChanges() {
		
			List<Relationship> assignedRelationships = new ArrayList<Relationship>();
			 
			try (Transaction tx = graphDb.beginTx()){
				
				ResourceIterable<Relationship> relationshipIterable  = graphDb.getAllRelationships();
				
				assignedRelationships.add(graphDb.getRelationshipById(73));
				assignedRelationships.add(graphDb.getRelationshipById(213));
				assignedRelationships.add(graphDb.getRelationshipById(216));
				
				Relationship relationship = graphDb.getRelationshipById(213);
				
				DiscoverablePeriodFinder discoverablePeriodFinder = new DiscoverablePeriodFinder();
				boolean partialDiscoverableTimeChanges = discoverablePeriodFinder.PartialDiscoverableTimeChanges(assignedRelationships, relationship); 
				
//				System.out.println("Stop point");
			}
		}
		
		
		public static void testGraphPatternGenerator() {
			 
			try (Transaction tx = graphDb.beginTx()){
				Random random = new Random();
				SubgraphGenerator subgraphGenerator = new SubgraphGenerator(graphDb, 4, random, 4, 0, 8, 0, 0, 0, 0);
				
				Node requestorNode = graphDb.getNodeById(33034);
				
				GPHolder gpHolder = subgraphGenerator.createDBBasedGP(requestorNode);
				
				System.out.println("Stop point");
			}
		}
}
