package ca.ucalgary.ispia.graphpatterns;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Transaction;

import ca.ucalgary.ispia.graphpatterns.util.DeletingDataFromNeo4j;

public class DeleteAllNodesAndRelationships {
	
	/**
	 * The main control for specifying tasks.
	 * @param args
	 */
	public static void main(String[] args){	
		
		Driver driver = new Driver();
		GraphDatabaseService graphDb = driver.getGraphDb("graph.db");
		DeletingDataFromNeo4j randomHistoryGraphCreator = new DeletingDataFromNeo4j(graphDb);
		
		
		System.out.println("Deleting node and relationships.");
		
		randomHistoryGraphCreator.deleteNodesAndRelationship();
	} 

}
