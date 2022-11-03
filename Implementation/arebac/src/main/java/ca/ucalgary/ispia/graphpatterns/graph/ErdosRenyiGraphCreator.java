package ca.ucalgary.ispia.graphpatterns.graph;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.tuple.Triple;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.ResourceIterable;
import org.neo4j.graphdb.ResourceIterator;
import org.neo4j.graphdb.Transaction;

import ca.ucalgary.ispia.graphpatterns.util.Pair;

public class ErdosRenyiGraphCreator {
	
	private boolean[][][] graphMatrix;
	GraphDatabaseService graphDb = null;
	private int nodesCount = 0;
	private int batchSize = 1000;
	private int historyStartTime = 0;
	private int historyEndTime = 24;
	private double probablityEventActualization = 1;
	private double[] probabilityRelTypeArray = new double[0];
	private Set<Triple<Integer, Integer, Integer>> matrixTripleSet = new HashSet<>();
	private List<Integer> eventLengthList = new ArrayList<Integer>();
	
	public ErdosRenyiGraphCreator(GraphDatabaseService graphDb) {
		this.graphDb = graphDb;
	}
	
	
	public ErdosRenyiGraphCreator(GraphDatabaseService graphDb, int nodesCount, double[] probabilityRelTypeArray, double probablityEventActualization, int batchSize, int historyStartTime, int historyEndTime) {
		this.graphDb = graphDb;
		this.nodesCount = nodesCount;
		this.probabilityRelTypeArray = probabilityRelTypeArray;
//		this.probabilityofEventStart = probabilityofEventStart;
		this.probablityEventActualization = probablityEventActualization;
		this.batchSize = batchSize;
		this.historyStartTime = historyStartTime;
		this.historyEndTime = historyEndTime;
	}
	
	public ErdosRenyiGraphCreator(int nodesCount, double[] probabilityRelTypeArray) {
		this.nodesCount = nodesCount;
		this.probabilityRelTypeArray = probabilityRelTypeArray;
	}
	
	private void edgePresent(int i, int j, int k) {
		// if edge of type k is present then between vertex i and j then the value in matrix at i,j,k is true. 
		this.graphMatrix[i][j][k]  = true;
	}
	
	private void edgeAbsent(int i, int j, int k) {
		// if edge of type k is absent then between vertex i and j then the value in matrix at i,j,k is false.
		this.graphMatrix[i][j][k]  = false;
	}
	
	// randomly creating 3D matrix for creating graph in which two vertices can have edges of multiple types.
	public boolean[][][] createRandomGraphMatrix() {
		
		for(int i = 0; i < nodesCount; i++){
			
			for(int j = 0; j < nodesCount; j++) {
				
				for(int k = 0; k < probabilityRelTypeArray.length; k++) {
					
					float randomNumberToCheckEdgePresence = (float) Math.random();
					
					// checking with probability whether edge of type k is present or absent.
					if(probabilityRelTypeArray[k] > randomNumberToCheckEdgePresence) {
						edgePresent(i, j, k);
					
					} else {
						edgeAbsent(i, j, k);
					}
					
				}
			}
		}
		
		return this.graphMatrix;
	}
	
	
	
	// randomly creating 3D matrix for creating graph in which two vertices can have edges of multiple types.
	public Set<Triple<Integer, Integer, Integer>> createRandomGraphMatrixList() {
		
		for(int i = 0; i < nodesCount; i++){
			
			for(int j = 0; j < nodesCount; j++) {
				
				for(int k = 0; k < probabilityRelTypeArray.length; k++) {
					
					float randomNumberToCheckEdgePresence = (float) Math.random();
					
					// checking with probability whether edge of type k is present or absent.
					if(probabilityRelTypeArray[k] > randomNumberToCheckEdgePresence) {
						Triple matrixTriple = Triple.of(i, j, k);
						this.matrixTripleSet.add(matrixTriple);
						
					}
				}
			}
		}
		
		return this.matrixTripleSet;
	}
	
//	// creating relationships in neo4j using matrix. 
//	public void createNeo4jRelationshipsUsingMartix(boolean[][][] graphMatrix) {
//		
//		// getting label for nodes.
//		Label label = Label.label("testNode");
//		
//		for(int i=0; i < graphMatrix.length; i++) {
//			
//			// getting the source node.
//			Node sourceNode = graphDb.findNode(label, "nodeId", i);
//			
//			for(int j=0; j < graphMatrix[i].length; j++) {
//			
//				// getting the target node.
//				Node targetNode = graphDb.findNode(label, "nodeId", j);
//				
//				for(int k=0; k < graphMatrix[i][j].length; k++) {
//					
//					// if edge of type k is present then we randomly create history for start and end of relationship and add multiple realtionship for each interval.
//					if(graphMatrix[i][j][k]) {
//						RelType relType = getRelationshipType(k);
//						// adding relationship history.
//						addRelationshipHistory(sourceNode, targetNode, relType, 0, 100);
//					}
//					
//				}
//			}
//		}
//	}
	
	
	// neo4j nodes creation using number of nodes in matrix.
	public void createNeo4jNodesInBatch() {
		// creating nodes with label and property for it.
		
		int startCount = 0;
		int batchSize = this.batchSize;
		int endCount = 0;
		
		for(int i = startCount; i< nodesCount;) {
			
			try (Transaction tx = graphDb.beginTx()){
				
				endCount = startCount + batchSize;
				if(nodesCount < endCount) {
					endCount = nodesCount;
				}
				
				for(int j= startCount; j < endCount; j++) {
					Node node = graphDb.createNode();
					Label label = Label.label("testNode");
					node.addLabel(label);
					node.setProperty("nodeId", j);
					startCount++;
				}
				tx.success();
				
				i = i + batchSize;
				
				System.out.println("Neo4j nodes created count: " + endCount);
			}
		}
	}
	

	
	
	// creating relationships in neo4j using matrix. 
	public void createNeo4jRelationshipsInBatch() {
			
		// Creating an array list using constructor 
	    List<Triple> matrixTripleList = new ArrayList<Triple>(matrixTripleSet); 
		
	    int startCount = 0;
		int batchSize = this.batchSize;
		int endCount = 0;
		Label label = Label.label("testNode");
		
		for(int i = startCount; i< matrixTripleList.size();) {
			
			try (Transaction tx = graphDb.beginTx()){
				
				endCount = startCount + batchSize;
				
				if(matrixTripleList.size() < endCount) {
					endCount = matrixTripleList.size();
				}
				
				for(int j= startCount; j < endCount; j++) {
					
					Triple<Integer, Integer, Integer> matrixTiple =  matrixTripleList.get(j);
					Node sourceNode = graphDb.findNode(label, "nodeId", matrixTiple.getLeft());
					Node targetNode = graphDb.findNode(label, "nodeId", matrixTiple.getMiddle());
					RelType relType = getRelationshipType(matrixTiple.getRight());
					int[] eventActualizationArray = createHistorySequence(this.historyStartTime, this.historyEndTime, this.probablityEventActualization);
					
					addRelationshipHistory(sourceNode, targetNode, relType, eventActualizationArray);
					
					startCount++;
				}
				tx.success();
				
				i = i + batchSize;
				
				System.out.println("Neo4j relationships created count: " + endCount);
			}
		}
		
		Integer sumLength = 0;
		for(Integer eventLength: this.eventLengthList) {
			sumLength += eventLength;
		}
		
		System.out.println("Average Interval length: " + (double) sumLength/ (double) this.eventLengthList.size());
		
	}
	
//	// creating relationship history for a given source, target node and relationship type. 
//	public void addRelationshipHistory(Node sourceNode, Node targetNode, RelType relType, int minTime, int maxTime) {
//		
//
//		
//		boolean startEvent = true;
//		Integer startTime = 0;
//		Integer endTime = 0;
//		for(int i = minTime; i < maxTime; i++) {
//			
//			float checkProbablityofEventStart = (float) Math.random();
//			float checkProbablityofEventEnd = (float) Math.random();			
//			
//			// checking if the event of change will happen using the given probability.
//			if(startEvent) {
//				// if startEvent == true that means the relationship will start.
//				
//				if(this.probabilityofEventStart > checkProbablityofEventStart) {
//					startTime = i;
//					startEvent = false;
//				}
//				
//			} else {
//				// else that means the relationship will end.
//				
//				if(this.probabilityofEventEnd > checkProbablityofEventEnd) {
//					endTime = i;
//					startEvent = true;					
//					Relationship createdRelationship = sourceNode.createRelationshipTo(targetNode, relType);
//					createdRelationship.setProperty("startTime", startTime);
//					createdRelationship.setProperty("endTime", endTime);
//				}
//			}
//		}
//		
//		// if relationship didn't end in the loop then we assign a max integer value to the end time.
//		if(!startEvent) {
//			endTime = Integer.MAX_VALUE;
//			Relationship createdRelationship = sourceNode.createRelationshipTo(targetNode, relType);
//			createdRelationship.setProperty("startTime", startTime);
//			createdRelationship.setProperty("endTime", endTime);
//		}
//	}
	
	// getting snapshot of the history graph for a given time point.
	public Pair<Set<Node>, Set<Relationship>> getHistoryGraphSnapshot(int snapshotTimePoint) {

		// getting all relationships from the neo4j database.
		ResourceIterator<Relationship> relIterator = graphDb.getAllRelationships().iterator();
		
		Set<Node> nodesInSnapshot = new HashSet<Node>();
		Set<Relationship> relationshipsInSnapshot = new HashSet<Relationship>();
		
		while(relIterator.hasNext()) {
			Relationship rel = relIterator.next();
			
			Integer startTime = Integer.valueOf(rel.getProperty("startTime").toString());
			Integer endTime =  Integer.valueOf(rel.getProperty("endTime").toString());
			
			
			// if the relationship contains the given time point then the relationship is added to the snapshot along with the nodes connected to the relationship. 
			if(startTime <= snapshotTimePoint && endTime >= snapshotTimePoint) {
				nodesInSnapshot.add(rel.getStartNode());
				nodesInSnapshot.add(rel.getEndNode());
				relationshipsInSnapshot.add(rel);
			}
		}
		
		Pair<Set<Node>, Set<Relationship>> historyGraphSnapshot = new Pair<Set<Node>, Set<Relationship>>(nodesInSnapshot, relationshipsInSnapshot);
		
		return historyGraphSnapshot;
	}
	
	
	
public int[] createHistorySequence(int historyStartTime, int historyEndTime, double probablityEventActualization) {
		
		int timelineLength = historyEndTime - historyStartTime;
		int[] eventActualizationArray = new int[timelineLength];
		
		for(int i=0; i < timelineLength; i++) {
		
			float checkProbablityEventActualization = (float) Math.random();
			if(this.probablityEventActualization > checkProbablityEventActualization) {
				eventActualizationArray[i] = 1;
				
			}else {
				eventActualizationArray[i] = -1;
			}
		}
		
		return eventActualizationArray;
	}
	
	public void addRelationshipHistory(Node sourceNode, Node targetNode, RelType relType, int[] eventActualizationArray)
    {

        int currentSequenceStartIndex = 0;
        int currentSequenceLength = 0;
        int currentSequenceEndIndex = 0;
        
        boolean eventActualization = false; 
        
        for (int i = 0; i < eventActualizationArray.length; i++)
        {
            if(eventActualizationArray[i] < 0)
            {
                currentSequenceStartIndex = i + 1;
                currentSequenceEndIndex = currentSequenceStartIndex + currentSequenceLength;
                
                if(eventActualization) {
                	if(sourceNode != null && targetNode != null) {
	                	Relationship createdRelationship = sourceNode.createRelationshipTo(targetNode, relType);
						createdRelationship.setProperty("startTime", currentSequenceStartIndex);
						createdRelationship.setProperty("endTime", currentSequenceEndIndex);
						this.eventLengthList.add(currentSequenceLength);
                	}
					eventActualization = false;
                }
                
                currentSequenceLength = 0;
            
            } else {
            	
            	eventActualization = true;
                currentSequenceLength++;
            }
        }
        
        if(eventActualization) {
        	
        	currentSequenceEndIndex = Integer.MAX_VALUE;
        	
        	if(sourceNode != null && targetNode != null) {
	        	Relationship createdRelationship = sourceNode.createRelationshipTo(targetNode, relType);
				createdRelationship.setProperty("startTime", currentSequenceStartIndex);
				createdRelationship.setProperty("endTime", currentSequenceEndIndex);
				this.eventLengthList.add(currentSequenceLength);
        	}
        }
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

	public int getNodesCount() {
		return nodesCount;
	}

	public void setNodesCount(int nodesCount) {
		this.nodesCount = nodesCount;
	}



}
