package ca.ucalgary.ispia.graphpatterns.util;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Transaction;

public class DeletingDataFromNeo4j {
	
	GraphDatabaseService graphDb = null;
	
	public DeletingDataFromNeo4j(GraphDatabaseService graphDb) {
		this.graphDb = graphDb;
	}
	
	// deleting all nodes and  relationships in neo4j database.
	public void deleteNodesAndRelationship() {
		try (Transaction tx = graphDb.beginTx()){
			String query = "MATCH (n) DETACH DELETE n";
			graphDb.execute(query);
			System.out.println("Deleting nodes and relationship from graph db done.");
			tx.success();
		}
	}

}
