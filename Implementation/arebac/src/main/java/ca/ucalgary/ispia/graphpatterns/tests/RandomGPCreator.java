package ca.ucalgary.ispia.graphpatterns.tests;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.apache.commons.lang3.tuple.Triple;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;

import ca.ucalgary.ispia.graphpatterns.graph.GraphPattern;
import ca.ucalgary.ispia.graphpatterns.graph.MyEdge;
import ca.ucalgary.ispia.graphpatterns.graph.MyNode;
import ca.ucalgary.ispia.graphpatterns.graph.RelType;
import ca.ucalgary.ispia.graphpatterns.util.GPUtil;
import ca.ucalgary.ispia.graphpatterns.util.LabelEnum;
import ca.ucalgary.ispia.graphpatterns.util.Pair;

public class RandomGPCreator {
	
	GraphDatabaseService graphDb;
	Pair<Set<Node>, Set<Relationship>> historyGraphSnapshot;
	Random random;
	int gpSize;
	int isSnapshotPeriodIncluded;
	int snapshotTimePoint;
	int totalGraphNodes;
	private List<Node> allNodes = new ArrayList<Node>();
	private List<Relationship> allRelationships = new ArrayList<Relationship>();
	public Map<Node, MyNode> nodesMap;
	public Map<Relationship, MyEdge> relsMap;
	
	public RandomGPCreator (Pair<Set<Node>, Set<Relationship>> historyGraphSnapshot, Random random, int gpSize, int isSnapshotPeriodIncluded) {
		this.historyGraphSnapshot = historyGraphSnapshot;
		this.random = random;
		this.gpSize = gpSize;
		this.isSnapshotPeriodIncluded = isSnapshotPeriodIncluded;
		//initialize the lists
		nodesMap = new HashMap<Node, MyNode>();
		relsMap = new HashMap<Relationship, MyEdge>();
	}
	
	public RandomGPCreator (GraphDatabaseService graphDb, Random random, int gpSize, int isSnapshotPeriodIncluded, int snapshotTimePoint, int totalGraphNodes) {
		this.graphDb = graphDb;
		this.random = random;
		this.gpSize = gpSize;
		this.isSnapshotPeriodIncluded = isSnapshotPeriodIncluded;
		this.snapshotTimePoint = snapshotTimePoint;
		this.totalGraphNodes = totalGraphNodes;
		//initialize the lists
		nodesMap = new HashMap<Node, MyNode>();
		relsMap = new HashMap<Relationship, MyEdge>();
	}
	
	public RandomGPCreator (GraphDatabaseService graphDb, Random random, int gpSize, int isSnapshotPeriodIncluded, int totalGraphNodes) {
		this.graphDb = graphDb;
		this.random = random;
		this.gpSize = gpSize;
		this.isSnapshotPeriodIncluded = isSnapshotPeriodIncluded;
		this.totalGraphNodes = totalGraphNodes;
		//initialize the lists
		nodesMap = new HashMap<Node, MyNode>();
		relsMap = new HashMap<Relationship, MyEdge>();
	}
	
	
	public Pair<GraphPattern, Map<MyNode, Node>> getRandomGP() {
		GraphPattern graphPattern = null;
		Set<Relationship> allRelationshipsToExclude = null;
		
		if(isSnapshotPeriodIncluded == 1) {
			Node startNode =  pickRandomEdgeSnapshotPeriodIncluded();
			allNodes.add(startNode);
		
		} else if (isSnapshotPeriodIncluded == 0) {
			Node startNode =  pickRandomEdgeSnapshotPeriodExcluded();
			allNodes.add(startNode);
		
		} else {
			Node startNode =  pickRandomEdge();
			allNodes.add(startNode);
		} 
		
		if(isSnapshotPeriodIncluded == 1) {
			getNodesForGPSnapshotPeriodIncluded();
		
		} else if (isSnapshotPeriodIncluded == 0) {
			allRelationshipsToExclude = getNodesForGPSnapshotPeriodExcluded(snapshotTimePoint);
		
		} else {
			getNodesForGP();
		}
				
		if(isSnapshotPeriodIncluded == 1) {
			getRelationshipsForGPSnapshotPeriodIncluded();
		
		} else if (isSnapshotPeriodIncluded == 0) {
			getRelationshipsForGPSnapshotPeriodExcluded(allRelationshipsToExclude);
		
		} else {
			getRelationshipsForGP();
		}
		
		if(isSnapshotPeriodIncluded == 1) {
			graphPattern = getGraphPatternFromSubGraphSnapshotPeriodIncluded();
		
		} else if (isSnapshotPeriodIncluded == 0){
			graphPattern = getGraphPatternFromSubGraphSnapshotPeriodExcluded();
		
		} else {
			graphPattern = getGraphPatternFromSubGraph();
		}
		
		Map<MyNode, Node> info = new HashMap<MyNode, Node>();
		info.put(nodesMap.get(allNodes.get(0)), allNodes.get(0));
		info.put(nodesMap.get(allNodes.get(allNodes.size()-1)), allNodes.get(allNodes.size()-1));
		
//		for(Map.Entry<Node, MyNode> entry : nodesMap.entrySet()) {
//			info.put(entry.getValue(), entry.getKey());
//		}
		
		
		Pair<GraphPattern, Map<MyNode, Node>> resultPair = new Pair(graphPattern, info);
		return resultPair;
	}
	
	private Node pickRandomEdgeSnapshotPeriodIncluded() {
		Set<Node> nodes = historyGraphSnapshot.first;
		
		//https://stackoverflow.com/questions/124671/picking-a-random-element-from-a-set
		int randomIndex = random.nextInt(nodes.size());
		Iterator<Node> nodeIterator = nodes.iterator();		
		for (int i = 0; i < randomIndex; i++) {
			nodeIterator.next();
		}
		return nodeIterator.next();
	}
	
	private Node pickRandomEdgeSnapshotPeriodExcluded() {
		Node node = null;
		try (Transaction tx = graphDb.beginTx()){
			
			//Find a random node from the database, using the unique id attribute
			int nodeId = random.nextInt(totalGraphNodes);
			node = graphDb.findNode(LabelEnum.testNode, "nodeId" , nodeId);
			
			tx.success();
		}
		return node;
	}
	
	private Node pickRandomEdge() {
		Node node = null;
		try (Transaction tx = graphDb.beginTx()){
			
			//Find a random node from the database, using the unique id attribute
			int nodeId = random.nextInt(totalGraphNodes);
			node = graphDb.findNode(LabelEnum.testNode, "nodeId" , nodeId);
			tx.success();
		}
		return node;
	}
	
	private void getNodesForGPSnapshotPeriodIncluded() {
		
		while (allNodes.size() < this.gpSize){
			
			//Pick a random node from allNodes
			int idx = random.nextInt(allNodes.size());
			Node node = allNodes.get(idx);

			//Get all relationships of node				
			Iterable<Relationship> ite = node.getRelationships(Direction.BOTH);
			List<Relationship> tempRels = new ArrayList<Relationship>();

			//Pick one of the relationships uniformly at random.
			for (Relationship rel : ite){
				
				if(historyGraphSnapshot.second.contains(rel)) {
					tempRels.add(rel);
				}
			}

			idx = random.nextInt(tempRels.size());
			Relationship rel = tempRels.get(idx);
			Node neighbour = rel.getOtherNode(node);

			//Add the neighbour to allNodes
			if (!allNodes.contains(neighbour)){
				allNodes.add(neighbour);
			} else {
				allNodes.clear();
				Node startNode =  pickRandomEdgeSnapshotPeriodIncluded();
				allNodes.add(startNode);
			}
		}
		
		return;
	}
	
	
	private Set<Relationship> getNodesForGPSnapshotPeriodExcluded(int snapshotTimePoint) {
		
		Set<Relationship> allTempRelstoBeRemoved = new HashSet<Relationship>();
		
		while (allNodes.size() < this.gpSize){
			
			//Pick a random node from allNodes
			int idx = random.nextInt(allNodes.size());
			Node node = allNodes.get(idx);

			//Get all relationships of node				
			Iterable<Relationship> ite = node.getRelationships(Direction.BOTH);
			Set<Relationship> tempRels = new HashSet<Relationship>();
			
			Map<Relationship, Pair<RelationshipType, Direction>> relationshipsOverlappingWithSnapshotPoint = new HashMap<Relationship, Pair<RelationshipType, Direction>>();
			
			//Pick one of the relationships uniformly at random.
			for (Relationship rel : ite){
				
				tempRels.add(rel);
				
				if(Integer.valueOf(rel.getProperty("startTime").toString()) <= snapshotTimePoint && Integer.valueOf(rel.getProperty("endTime").toString()) >= snapshotTimePoint) {
					RelationshipType relType = null;
					Direction relDirection = null;
					relType = rel.getType();
					if (rel.getStartNode().equals(node)){
						relDirection = Direction.OUTGOING;
					} else {
						relDirection = Direction.INCOMING;
					}
					
					Pair<RelationshipType, Direction> relTypeDirectionPair = new Pair(relType, relDirection);
					relationshipsOverlappingWithSnapshotPoint.put(rel, relTypeDirectionPair);
				}
				
			}
			
			Set<Relationship> tempRelstoBeRemoved = new HashSet<Relationship>();
			
			for(Relationship relationshiptoCheck: tempRels) {
				
				RelationshipType relationshiptoCheckRelType = relationshiptoCheck.getType();
				Direction relationshiToCheckDirection = null;
				if (relationshiptoCheck.getStartNode().equals(node)){
					relationshiToCheckDirection = Direction.OUTGOING;
				} else {
					relationshiToCheckDirection = Direction.INCOMING;
				}
				
				for (Map.Entry<Relationship, Pair<RelationshipType, Direction>> entry : relationshipsOverlappingWithSnapshotPoint.entrySet())  {
		            
//					System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
		            
		            Pair<RelationshipType, Direction> pairtoCheckWith = entry.getValue();
		            
		            if(pairtoCheckWith.first.equals(relationshiptoCheckRelType) && pairtoCheckWith.second.equals(relationshiToCheckDirection)) {
		            	tempRelstoBeRemoved.add(relationshiptoCheck);
		            }
		            
				}
			}
			
			
			tempRels.removeAll(tempRelstoBeRemoved);
			
			if(tempRels.size() == 0) {
				allNodes.clear();
				Node startNode =  pickRandomEdgeSnapshotPeriodExcluded();
				
				
				allNodes.add(startNode);
			
			} else {
			
				idx = random.nextInt(tempRels.size());
				// randomise picking the next node using the idx variable defined in previous line.
				
				Relationship rel = null;
				
				int i = 0;
				for(Relationship tempRel : tempRels)
				{
				    if (i == idx) {
				    	rel = tempRel;
				    	break;
					}
				    i++;
				}
				
				Node neighbour = rel.getOtherNode(node);
				
//				Relationship rel = tempRels.iterator().next();
//				Node neighbour = rel.getOtherNode(node);
	
				//Add the neighbour to allNodes
				if (!allNodes.contains(neighbour)){
					allNodes.add(neighbour);
					// adding relationships to be used later when we we will be adding relationships for all nodes in gp.
					allTempRelstoBeRemoved.addAll(tempRelstoBeRemoved);
					
				} else {
					allNodes.clear();
					Node startNode =  pickRandomEdgeSnapshotPeriodExcluded();
					allNodes.add(startNode);
				}
			}
		}
		
		return allTempRelstoBeRemoved;
	}
	
	private void getNodesForGP() {
		
		while (allNodes.size() < this.gpSize){
			
			//Pick a random node from allNodes
			int idx = random.nextInt(allNodes.size());
			Node node = allNodes.get(idx);

			//Get all relationships of node				
			Iterable<Relationship> ite = node.getRelationships(Direction.BOTH);
			List<Relationship> tempRels = new ArrayList<Relationship>();

			//Pick one of the relationships uniformly at random.
			for (Relationship rel : ite){
				tempRels.add(rel);
			}

			idx = random.nextInt(tempRels.size());
			Relationship rel = tempRels.get(idx);
			Node neighbour = rel.getOtherNode(node);

			//Add the neighbour to allNodes
			if (!allNodes.contains(neighbour)){
				allNodes.add(neighbour);
			}
		}
		
		return;
	}
	
	private void getRelationshipsForGPSnapshotPeriodIncluded(){
		
		for(Relationship relationship : historyGraphSnapshot.second) {
			
			if(allNodes.contains(relationship.getStartNode()) && allNodes.contains(relationship.getEndNode())) {
//				if(relationship.getStartNode() != relationship.getEndNode()) {
					allRelationships.add(relationship);
//				}
			}
		} 
		
		return;
	}
	
	
	private void getRelationshipsForGPSnapshotPeriodExcluded(Set<Relationship> allRelationshipsToExclude){
		
		List<Relationship> tempAllRelationships = new ArrayList<Relationship>();
		
//		for(Node node: allNodes) {
//			Iterable<Relationship> relationshipIterable = node.getRelationships();
//			Iterator<Relationship> relationshipIterator = relationshipIterable.iterator();
//			
//			while(relationshipIterator.hasNext()) {
//				Relationship tempRel = relationshipIterator.next();
//				if(!allRelationshipsToExclude.contains(tempRel)) {
//					tempAllRelationships.add(tempRel);
//				}
//				
////				if(!(Integer.valueOf(tempRel.getProperty("startTime").toString()) <= snapshotTimePoint && Integer.valueOf(tempRel.getProperty("endTime").toString()) >= snapshotTimePoint)) {
////					tempAllRelationships.add(tempRel);
////				}
//			}
//		}
//		
//		
//		for(Relationship relationship : tempAllRelationships) {
//		
//			if(allNodes.contains(relationship.getStartNode()) && allNodes.contains(relationship.getEndNode())) {
////				if(relationship.getStartNode() != relationship.getEndNode()) {
//					allRelationships.add(relationship);
////				}
//			}
//		}

		
		
		
		
		
		for(Node node: allNodes) {
			Iterable<Relationship> relationshipIterable = node.getRelationships();
			Iterator<Relationship> relationshipIterator = relationshipIterable.iterator();
			
			while(relationshipIterator.hasNext()) {
				
				Relationship tempRel = relationshipIterator.next();
//				if(!allRelationshipsToExclude.contains(tempRel)) {
//					tempAllRelationships.add(tempRel);
//				}
				
				if(allNodes.contains(tempRel.getStartNode()) && allNodes.contains(tempRel.getEndNode())) {
					allRelationships.add(tempRel);
				}
				
			}
		}
		
		
//		allRelationships.removeAll(allRelationshipsToExclude);
		
//		for(Relationship relationship : tempAllRelationships) {
//		
//			if(allNodes.contains(relationship.getStartNode()) && allNodes.contains(relationship.getEndNode())) {
//				allRelationships.add(relationship);
//			}
//		}
		
		
		
		return;
	}
	
	
	private void getRelationshipsForGP(){
		
//		for(Relationship relationship : historyGraphSnapshot.second) {
//			
//			if(allNodes.contains(relationship.getStartNode()) && allNodes.contains(relationship.getEndNode())) {
//					allRelationships.add(relationship);
//			}
//		} 
		
		
		
		for(Node node: allNodes) {
			Iterable<Relationship> relationshipIterable = node.getRelationships();
			Iterator<Relationship> relationshipIterator = relationshipIterable.iterator();
			
			while(relationshipIterator.hasNext()) {
				
				Relationship tempRel = relationshipIterator.next();
//				if(!allRelationshipsToExclude.contains(tempRel)) {
//					tempAllRelationships.add(tempRel);
//				}
				
				if(allNodes.contains(tempRel.getStartNode()) && allNodes.contains(tempRel.getEndNode())) {
					allRelationships.add(tempRel);
				}
				
			}
		}
		
		return;
	}
	
	
	private GraphPattern getGraphPatternFromSubGraphSnapshotPeriodIncluded() {
		
		GraphPattern gp = new GraphPattern();
		int nodeCount = 0;
		for (Node node : allNodes){
			MyNode myNode = new MyNode(nodeCount, "testNode");
			nodesMap.put(node, myNode);
			gp.addNode(myNode);
			nodeCount++;
		}
		
		int relCount = 0;
//		String relPrefix = "rel";
		
		Set<Triple> edgeSet = new HashSet<Triple>();
		
		for (Relationship r : allRelationships){
			MyNode source = null, target = null;
			RelType type = null;
			source = nodesMap.get(r.getStartNode());
			target = nodesMap.get(r.getEndNode());
			type = GPUtil.translateRelType(r.getType());
			if(source != target) {
				
				
				if(source != target) {
					Triple edgeTriple = Triple.of(source, target, type);
					edgeSet.add(edgeTriple);
				}
				
				
//				MyEdge rel = new MyEdge(source, target, type, relCount);
//				gp.addEdge(rel);
//				relsMap.put(r, rel);
//				
//				int startTime = (int) r.getProperty("startTime");
//				int endTime = (int) r.getProperty("endTime");
//				System.out.println("StartTime: " + startTime+ " EndTime: "+endTime);
			}
				
		}
		
		for(Triple edgeTriple: edgeSet) {
			
			MyEdge rel = new MyEdge((MyNode) edgeTriple.getLeft(), (MyNode) edgeTriple.getMiddle(), (RelType) edgeTriple.getRight(), relCount);
			gp.addEdge(rel);
//			relsMap.put(r, rel);
		}

		return gp;
		
	}
	
	
	private GraphPattern getGraphPatternFromSubGraphSnapshotPeriodExcluded() {
		
		GraphPattern gp = new GraphPattern();
		int nodeCount = 0;
		for (Node node : allNodes){
			MyNode myNode = new MyNode(nodeCount, "testNode");
			nodesMap.put(node, myNode);
			gp.addNode(myNode);
			nodeCount++;
		}
		
		int relCount = 0;
		Set<Triple> edgeSet = new HashSet<Triple>();
//		String relPrefix = "rel";
		
		for (Relationship r : allRelationships){
			MyNode source = null, target = null;
			RelType type = null;
			source = nodesMap.get(r.getStartNode());
			target = nodesMap.get(r.getEndNode());
			type = GPUtil.translateRelType(r.getType());
			// creating triple to be added in set to remove redundancy.
			if(source != target) {
				Triple edgeTriple = Triple.of(source, target, type);
				edgeSet.add(edgeTriple);
			}
		}
		
		for(Triple edgeTriple: edgeSet) {
			
			MyEdge rel = new MyEdge((MyNode) edgeTriple.getLeft(), (MyNode) edgeTriple.getMiddle(), (RelType) edgeTriple.getRight(), relCount);
			gp.addEdge(rel);
//			relsMap.put(r, rel);
		}
		
		return gp;
	}
	
	
	private GraphPattern getGraphPatternFromSubGraph() {
		
		GraphPattern gp = new GraphPattern();
		int nodeCount = 0;
		for (Node node : allNodes){
			MyNode myNode = new MyNode(nodeCount, "testNode");
			nodesMap.put(node, myNode);
			gp.addNode(myNode);
			nodeCount++;
		}
		
		int relCount = 0;
		Set<Triple> edgeSet = new HashSet<Triple>();
//		String relPrefix = "rel";
		
		for (Relationship r : allRelationships){
			MyNode source = null, target = null;
			RelType type = null;
			source = nodesMap.get(r.getStartNode());
			target = nodesMap.get(r.getEndNode());
			type = GPUtil.translateRelType(r.getType());
			// creating triple to be added in set to remove redundancy.
			
			if(source != target) {
				Triple edgeTriple = Triple.of(source, target, type);
				edgeSet.add(edgeTriple);
			}
		}
		
		for(Triple edgeTriple: edgeSet) {
			
			MyEdge rel = new MyEdge((MyNode) edgeTriple.getLeft(), (MyNode) edgeTriple.getMiddle(), (RelType) edgeTriple.getRight(), relCount);
			gp.addEdge(rel);
//			relsMap.put(r, rel);
		}
		
		return gp;
		
	}
}
