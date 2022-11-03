package ca.ucalgary.ispia.graphpatterns;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.Properties;

import org.neo4j.graphdb.GraphDatabaseService;

import ca.ucalgary.ispia.graphpatterns.graph.ErdosRenyiGraphCreator;
import ca.ucalgary.ispia.graphpatterns.graph.SocialGraphCreator;

public class Neo4jSocialGraphDbCreator {
	
	/**
	 * The main control for specifying tasks.
	 * @param args
	 */
	
	

	public static void main(String[] args){	
		
		Driver driver = new Driver();
		GraphDatabaseService graphDb = driver.getGraphDb("graph.db");		
		String propFileLocation= "./properties/GraphCreation.properties";
		File file = new File(propFileLocation);
		InputStream in;
		DecimalFormat df2 = new DecimalFormat("#.##");
		try {
			in = new FileInputStream(file);
			Properties properties = new Properties();
			properties.load(in);
		    int numberofNodeInNeo4jGraphDatabse = Integer.parseInt(properties.getProperty("numberofNodeInNeo4jGraphDatabse"));
		    int batchSize = Integer.parseInt(properties.getProperty("batchSize"));
		    int historyStartTime = Integer.parseInt(properties.getProperty("historyStartTime"));
		    int historyEndTime = Integer.parseInt(properties.getProperty("historyEndTime")); 
		    double probablityEventActualization = Double.parseDouble(properties.getProperty("probablityEventActualization"));
		    String graphFileLocation = properties.getProperty("graphFileLocation").toString();
		    
//		    double probabilityStart = 0.1;
//		    double probabilityEnd = 0.02;
		    
//			SocialGraphCreator randomHistoryGraphCreator = new SocialGraphCreator(graphDb, numberofNodeInNeo4jGraphDatabse, graphFileLocation, probabilityStart, probabilityEnd, batchSize, historyStartTime, historyEndTime);
		    SocialGraphCreator randomHistoryGraphCreator = new SocialGraphCreator(graphDb, numberofNodeInNeo4jGraphDatabse, graphFileLocation, probablityEventActualization, batchSize, historyStartTime, historyEndTime);
		    
			randomHistoryGraphCreator.createMatrixTriple();
			randomHistoryGraphCreator.createNeo4jNodesInBatch("GraphFiles/nodes.csv");
			
			
//			randomHistoryGraphCreator.probabilityofEventEnd = probabilityEnd;
			randomHistoryGraphCreator.createNeo4jRelationshipsInBatch("GraphFiles/"+"relationships.csv");
			File relationshipFile = new File("GraphFiles/"+"relationships.csv");
//			File relationshiWithLength = new File("GraphFiles/"+"relationships"+ df2.format(randomHistoryGraphCreator.eventAverageLength) +".csv");
			File relationshiWithLength = new File("GraphFiles/"+"relationships"+ probablityEventActualization +".csv");
			boolean fileRenamed = relationshipFile.renameTo(relationshiWithLength);
			
//			for(probabilityEnd = 0.0; probabilityEnd <= 0.5; probabilityEnd = probabilityEnd + 0.005) {
//				
//				System.out.println("Probability of start: " + probabilityStart);
//				System.out.println("Probability of end: " + probabilityEnd);
//				
//				randomHistoryGraphCreator.probabilityofEventEnd = probabilityEnd;
//				randomHistoryGraphCreator.createNeo4jRelationshipsInBatch("GraphFiles/"+"relationships.csv");
//				File relationshipFile = new File("GraphFiles/"+"relationships.csv");
//				File relationshiWithLength = new File("GraphFiles/"+"relationships"+ df2.format(randomHistoryGraphCreator.eventAverageLength) +".csv");
//				boolean fileRenamed = relationshipFile.renameTo(relationshiWithLength);
//				
//			}
			
			
			System.out.println("Graph creation ended.");
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
				
	}

}
