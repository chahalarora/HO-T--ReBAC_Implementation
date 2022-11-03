package ca.ucalgary.ispia.graphpatterns.graph;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;

import org.apache.commons.lang3.tuple.Triple;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;

import com.opencsv.CSVReader;

public class SocialGraphCreator {
	
	GraphDatabaseService graphDb = null;
	private Set<Triple<Integer, Integer, Integer>> matrixTripleSet = new HashSet<>();
	private int batchSize = 1000;
	private int nodesCount = 1000;
	public double probabilityofEventStart = 0.1;
	public double probabilityofEventEnd = 0.02;
	private int historyStartTime = 0;
	private int historyEndTime = 100;
	private List<Integer> eventLengthList = new ArrayList<Integer>();
	private String graphFileLocation = "";
	
	public double probablityEventActualization = 1;
	public double eventAverageLength = 0;
	// class constructor.
	public SocialGraphCreator(GraphDatabaseService graphDb) {
		this.graphDb = graphDb;
	}
	
	public SocialGraphCreator(GraphDatabaseService graphDb, int nodesCount, String graphFileLocation, double probablityEventActualization, int batchSize, int historyStartTime, int historyEndTime) {
		this.graphDb = graphDb;
		this.nodesCount = nodesCount;
		this.probablityEventActualization = probablityEventActualization;
		this.batchSize = batchSize;
		this.historyStartTime = historyStartTime;
		this.historyEndTime = historyEndTime;
		this.graphFileLocation = graphFileLocation;
	}
	
	public SocialGraphCreator(GraphDatabaseService graphDb, int nodesCount, String graphFileLocation, double probabilityofEventStart, double probabilityofEventEnd, int batchSize, int historyStartTime, int historyEndTime) {
		this.graphDb = graphDb;
		this.nodesCount = nodesCount;
		this.probabilityofEventStart = probabilityofEventStart;
		this.probabilityofEventEnd = probabilityofEventEnd;
		this.batchSize = batchSize;
		this.historyStartTime = historyStartTime;
		this.historyEndTime = historyEndTime;
		this.graphFileLocation = graphFileLocation;
	}
	
	// reading csv file to create matrix.
	public void createMatrixTriple() throws Exception {
	    
		BufferedReader reader;
		try {
			reader = new BufferedReader(new FileReader(this.graphFileLocation));
			String line = reader.readLine();
			while (line != null) {
				
				String[] values = line.split(",");
	    		Random rand = new Random();
	    		int randomRelationshipIndex = rand.nextInt(7);
	    		Triple matrixTriple = Triple.of(Integer.valueOf(values[0]), Integer.valueOf(values[1]), randomRelationshipIndex);
	    		this.matrixTripleSet.add(matrixTriple);
				
				// read next line
				line = reader.readLine();
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	// neo4j nodes creation using number of nodes in matrix.
	public void createNeo4jNodesInBatch(String nodesFileName) throws IOException {
		// creating nodes with label and property for it.
		
		int startCount = 0;
		int batchSize = this.batchSize;
		int endCount = 0;
		
		FileWriter myWriter = new FileWriter(nodesFileName);
		
		String nodesWriteToFile = "";
		
		nodesWriteToFile += "nodeId:ID,:LABEL";
		nodesWriteToFile += "\n";
		
		for(int i = startCount; i< nodesCount;) {
				
				endCount = startCount + batchSize;
				if(nodesCount < endCount) {
					endCount = nodesCount;
				}
				
				for(int j= startCount; j < endCount; j++) {
					
					nodesWriteToFile += j+","+"testNode";
					nodesWriteToFile += "\n";
										
					startCount++;
				}
				
				myWriter.write(nodesWriteToFile);
				nodesWriteToFile = "";
				
				i = i + batchSize;
				
				System.out.println("Neo4j nodes created count: " + endCount);
		}
		
		myWriter.close();
	    System.out.println("Successfully wrote to the file.");

	}
	
	// creating relationships in neo4j using matrix. 
	public void createNeo4jRelationshipsInBatch(String relationshipsFileName) throws IOException {
		
		// Creating an array list using constructor 
	    List<Triple> matrixTripleList = new ArrayList<Triple>(matrixTripleSet); 
		
	    this.eventLengthList.clear();
	    this.eventAverageLength = 0;
	    
	    int startCount = 0;
		int batchSize = this.batchSize;
		int endCount = 0;
		Label label = Label.label("testNode");
		
		FileWriter myWriter = new FileWriter(relationshipsFileName);
		
		String relationshipWriteToFile = "";
		relationshipWriteToFile +=":START_ID,startTime:int,endTime:int,:END_ID,:TYPE";
		relationshipWriteToFile +="\n";
		
		for(int i = startCount; i< matrixTripleList.size();) {
				
				endCount = startCount + batchSize;
				
				if(matrixTripleList.size() < endCount) {
					endCount = matrixTripleList.size();
				}
				
				for(int j= startCount; j < endCount; j++) {
					
					
					Triple<Integer, Integer, Integer> matrixTiple =  matrixTripleList.get(j);

					RelType relType = getRelationshipType(matrixTiple.getRight());
					int[] eventActualizationArray = createHistorySequence(this.historyStartTime, this.historyEndTime, this.probablityEventActualization);
					relationshipWriteToFile += addRelationshipHistoryToFile(matrixTiple.getLeft().longValue(), matrixTiple.getMiddle().longValue(), relType, eventActualizationArray);
					
//					relationshipWriteToFile += addRelationshipHistory(matrixTiple.getLeft().longValue(), matrixTiple.getMiddle().longValue(), relType, this.historyStartTime, this.historyEndTime, this.probabilityofEventStart, this.probabilityofEventEnd);
					startCount++;
				}
				
				myWriter.write(relationshipWriteToFile);
				relationshipWriteToFile = "";
				
				i = i + batchSize;
				
				System.out.println("Neo4j relationships created count: " + endCount);
		}

		myWriter.close();
		
		Integer sumLength = 0;
		for(Integer eventLength: this.eventLengthList) {
			sumLength += eventLength;
		}
		
		this.eventAverageLength = (double) sumLength/ (double) this.eventLengthList.size();
		
		System.out.println("Average Interval length: " + this.eventAverageLength);
		
	}
	
	
	public int[] createHistorySequence(int historyStartTime, int historyEndTime, double probablityEventActualization) {
		
		int timelineLength = historyEndTime - historyStartTime;
		int[] eventActualizationArray = new int[timelineLength];
		
		for(int i=0; i < timelineLength; i++) {
		
			float checkProbablityEventActualization = (float) Math.random();
			if(probablityEventActualization > checkProbablityEventActualization) {
				eventActualizationArray[i] = 1;
				
			}else {
				eventActualizationArray[i] = -1;
			}
		}
		
		return eventActualizationArray;
	}
	
	
	public int[] createHistorySequence(int historyStartTime, int historyEndTime, double probablityToControlEventCloseness, double probablityToControlIntervalLength) {
		
		int timelineLength = historyEndTime - historyStartTime;
		int[] eventActualizationArray = new int[timelineLength];
		
		for(int i=0; i < timelineLength; i++) {
		
			float checkProbablityEventActualization = (float) Math.random();
			if(probablityToControlEventCloseness > checkProbablityEventActualization) {
				
					eventActualizationArray[i] = 1;
					
			}else {
				eventActualizationArray[i] = -1;
			}
		}
		
		return eventActualizationArray;
	}
	
	
	
	
	
	
	
	
	
	
	// creating relationship history for a given source, target node and relationship type. 
	public String addRelationshipHistory(Long sourceNodeId, Long targetNodeId, RelType relType, int minTime, int maxTime, double startProbability, double endProbability) {
		
		boolean startEvent = true;
		Integer startTime = 0;
		Integer endTime = 0;
		
		String relationshipWriteToFile ="";
		
		for(int i = minTime; i < maxTime; i++) {
			
			float checkProbablityofEventStart = (float) Math.random();
			float checkProbablityofEventEnd = (float) Math.random();
	 
						
			// checking if the event of change will happen using the given probability.
			if(startEvent) {
				// if startEvent == true that means the relationship will start.
				
				if(startProbability > checkProbablityofEventStart) {
					startTime = i;
					startEvent = false;
				}
				
			} else {
				// else that means the relationship will end.
				
				if(endProbability > checkProbablityofEventEnd) {
					endTime = i;
					startEvent = true;					
//					Relationship createdRelationship = sourceNode.createRelationshipTo(targetNode, relType);
//					createdRelationship.setProperty("startTime", startTime);
//					createdRelationship.setProperty("endTime", endTime);
					relationshipWriteToFile += sourceNodeId + ","+startTime+","+endTime+","+targetNodeId+","+relType.toString();
            		relationshipWriteToFile += "\n";
            		this.eventLengthList.add(endTime-startTime);
					
				}
			}
		}
		
		// if relationship didn't end in the loop then we assign a max integer value to the end time.
		if(!startEvent) {
			endTime = Integer.MAX_VALUE;
//			Relationship createdRelationship = sourceNode.createRelationshipTo(targetNode, relType);
//			createdRelationship.setProperty("startTime", startTime);
//			createdRelationship.setProperty("endTime", endTime);
			relationshipWriteToFile += sourceNodeId + ","+startTime+","+endTime+","+targetNodeId+","+relType.toString();
    		relationshipWriteToFile += "\n";
    		this.eventLengthList.add(maxTime-startTime);
		}
		
		return relationshipWriteToFile;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public void addRelationshipHistory(Node sourceNode, Node targetNode, RelType relType, int[] eventActualizationArray){

        int currentSequenceStartIndex = 0;
        int currentSequenceLength = 0;
        int currentSequenceEndIndex = 0;
        int timelineLength = this.historyEndTime - this.historyStartTime;
        
        boolean eventActualization = false; 
        
        for (int i = 0; i < eventActualizationArray.length; i++)
        {
            if(eventActualizationArray[i] < 0)
            {
                currentSequenceStartIndex = i + 1;
                currentSequenceEndIndex = currentSequenceStartIndex + currentSequenceLength;
                
                if(currentSequenceEndIndex <= timelineLength) {
                    
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
                }
            
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
	
	
	public String addRelationshipHistoryToFile(Long sourceNodeId, Long targetNodeId, RelType relType, int[] eventActualizationArray)
    {

        int currentSequenceStartIndex = 0;
        int currentSequenceLength = 0;
        int currentSequenceEndIndex = 0;
        int timelineLength = this.historyEndTime - this.historyStartTime;
        String relationshipWriteToFile = "";
        
        boolean eventActualization = false; 
        
        for (int i = 0; i < eventActualizationArray.length; i++)
        {
            if(eventActualizationArray[i] < 0)
            {
                currentSequenceStartIndex = i + 1;
                currentSequenceEndIndex = currentSequenceStartIndex + currentSequenceLength;
                
                if(currentSequenceEndIndex <= timelineLength) {
                    
	                if(eventActualization) {
	                	if(sourceNodeId != null && targetNodeId != null) {
	                		relationshipWriteToFile += sourceNodeId + ","+currentSequenceStartIndex+","+currentSequenceEndIndex+","+targetNodeId+","+relType.toString();
	                		relationshipWriteToFile += "\n";
							this.eventLengthList.add(currentSequenceLength);
	                	}
						eventActualization = false;
	                }
	                
	                currentSequenceLength = 0;
                }
            
            } else {
            	
            	eventActualization = true;
                currentSequenceLength++;
            }
        }
        
        if(eventActualization) {
        	
        	currentSequenceEndIndex = Integer.MAX_VALUE;
        	
        	if(sourceNodeId != null && targetNodeId != null) {
	        	
        		relationshipWriteToFile += sourceNodeId + ","+currentSequenceStartIndex+","+currentSequenceEndIndex+","+targetNodeId+","+relType;
        		relationshipWriteToFile += "\n";
				this.eventLengthList.add(currentSequenceLength);
        	}
        }
        
        return relationshipWriteToFile;
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
