package ca.ucalgary.ispia.graphpatterns.graph;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HistoryGraph implements Serializable{
	
	private static final long serialVersionUID = -3746611137149867001L;

	//The adjacency list for outgoing relationships. The key is a MyNode, and the value is the list of 
	//relationships where the key is the source node.
	private Map<MyNode, List<MyRelationship>> outgoingRels;

	//The adjacency list for incoming relationships. The key is a MyNode, and the value is the list of 
	//relationships where the key is the target node.
	private Map<MyNode, List<MyRelationship>> incomingRels;

	//The list of nodes
	private List<MyNode> nodes;

	
	public HistoryGraph() {
		//Initialize the map and the list of nodes
		outgoingRels = new HashMap<MyNode, List<MyRelationship>>();
		incomingRels = new HashMap<MyNode, List<MyRelationship>>();
		nodes = new ArrayList<MyNode>();
	}
}
