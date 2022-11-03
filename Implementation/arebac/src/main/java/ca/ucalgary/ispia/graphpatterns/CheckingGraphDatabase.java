package ca.ucalgary.ispia.graphpatterns;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.ResourceIterator;
import org.neo4j.graphdb.Transaction;

public class CheckingGraphDatabase {

	/**
	 * The main control for specifying tasks.
	 * @param args
	 */
	public static void main(String[] args){	
		
		Driver driver = new Driver();
		GraphDatabaseService graphDb = driver.getGraphDb("graph.db");
		try (Transaction tx = graphDb.beginTx()){
			ResourceIterator<Node> nodesIterator = graphDb.getAllNodes().iterator();
			ResourceIterator<Relationship> relIterator = graphDb.getAllRelationships().iterator();
			
			int nodeCount = 0;
			int relCount = 0;
			
			while(nodesIterator.hasNext()) {
				nodesIterator.next();
				nodeCount++;
			}
			
			while(relIterator.hasNext()) {
				relIterator.next();
				relCount++;
			}
			
			System.out.println("Number of nodes in graph: " + nodeCount);
			System.out.println("Number of relationships in graph: " + relCount);
		}
		
		System.out.println("Checking graph db done.");
		
	}
	
}
