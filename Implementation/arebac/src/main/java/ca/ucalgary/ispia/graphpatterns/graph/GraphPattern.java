package ca.ucalgary.ispia.graphpatterns.graph;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Represents a graph pattern. The Edges are stored as an adjacency list.
 * @author szrrizvi
 *
 */
public class GraphPattern implements Serializable{

	private static final long serialVersionUID = -3746611137149867000L;

	//The adjacency list for outgoing Edges. The key is a MyNode, and the value is the list of 
	//Edges where the key is the source node.
	private Map<MyNode, List<MyEdge>> outgoingEdges;

	//The adjacency list for incoming Edges. The key is a MyNode, and the value is the list of 
	//Edges where the key is the target node.
	private Map<MyNode, List<MyEdge>> incomingEdges;

	//The list of nodes
	private List<MyNode> nodes;

	/**
	 * Default constructor.
	 */
	public GraphPattern(){
		//Initialize the map and the list of nodes
		outgoingEdges = new HashMap<MyNode, List<MyEdge>>();
		incomingEdges = new HashMap<MyNode, List<MyEdge>>();
		nodes = new ArrayList<MyNode>();
	}

	/**
	 * Makes a copy of the given gp; Note that this does not duplicate the MyNode and MyEdge
	 * objects, but on the list and map containing the information.
	 * @param gp
	 */
	public GraphPattern(GraphPattern gp){
		this(); //initialize the list and map

		//Make a copy of the nodes list
		List<MyNode> nodes = gp.getNodes();
		for (MyNode node : nodes){
			addNode(node);
		}

		//Make a copy of the Edges map
		List<MyEdge> rels = gp.getAllEdges();
		for (MyEdge rel : rels){
			addEdge(rel);
		}
	}

	/**
	 * Add the node to the nodes list, as long as the node already doesn't exist in the list.
	 * @param node The node to add to the nodes list.
	 */
	public void addNode(MyNode node){
		if (!nodes.contains(node)){
			nodes.add(node);
		}
	}

	/**
	 * Add the Edge to the graph pattern.
	 * @param rel The Edge to add.
	 */
	public void addEdge(MyEdge rel){

		//Get the source and target nodes,
		MyNode source = rel.getSource();
		MyNode target = rel.getTarget();

		//If the nodes list doesn't contain the source and target nodes, add them.
		if (!nodes.contains(source)){
			nodes.add(source);
		}
		if (!nodes.contains(target)){
			nodes.add(target);
		}

		//Add the Edge to the outgoing Rels map (adjacency list).
		if (outgoingEdges.containsKey(source)){
			//If the map already contains the source node as a key,
			//add the Edge to the value list.
			outgoingEdges.get(source).add(rel);
		} else {
			//If the map doesn't already contain the source node as a key,
			//Generate the value list and add the given Edge.
			List<MyEdge> list = new ArrayList<MyEdge>();
			list.add(rel);

			//Put the key, value pair in the map
			outgoingEdges.put(source, list);
		}

		//Add the Edge to the incoming Rels map (adjacency list).
		if (incomingEdges.containsKey(target)){
			//If the map already contains the target node as a key,
			//add the Edge to the value list.
			incomingEdges.get(target).add(rel);
		} else {
			//If the map doesn't already contain the target node as a key,
			//Generate the value list and add the given Edge.
			List<MyEdge> list = new ArrayList<MyEdge>();
			list.add(rel);

			//Put the key, value pair in the map
			incomingEdges.put(target, list);
		}

	}

	/**
	 * Returns the key set for the outgoing Edges. IE returns the set of nodes that have at least 1 outgoing
	 * edge.
	 * @return The key set for the adjacency list.
	 */
	public Set<MyNode> srcKeySet(){
		return outgoingEdges.keySet();
	}

	/**
	 * Returns the set of Edges from the given node, based on the given direction.
	 * @param node The node
	 * @param dir The direction of Edges
	 * @return The set of Edges to/from the given node.
	 */
	public List<MyEdge> getEdges(MyNode node, MyDirection dir){
		
		//Initialize result list
		List<MyEdge> result = new ArrayList<MyEdge>();	
		
		if (dir == MyDirection.OUTGOING){
			//Outgoing Edges; node = src
			result.addAll(outgoingEdges.get(node));
		} else if (dir == MyDirection.INCOMING) {
			//Incoming Edges; node = tgt
			result.addAll(incomingEdges.get(node));
		} else {
			//Both directions; node = src || node = tgt
			result = getAllEdges(node);	
		}
		
		return result;
	}

	/**
	 * @return The set of nodes
	 */
	public List<MyNode> getNodes(){
		return nodes;
	}

	/**
	 * Checks if the graph pattern contains at least 1 Edge.
	 * @return True if the graph pattern contains 0 Edges, else False. 
	 * NOTE: There cannot be less than 0 Edges.
	 */
	public boolean isEmpty(){

		//Iterate through the key set, and return false when the first 
		//Edge is encountered.
		for (MyNode source : outgoingEdges.keySet()){
			List<MyEdge> rels = outgoingEdges.get(source);

			if (!rels.isEmpty()){
				return false;
			}
		}
		//The map is exhausted and no Edges found, thus return true.
		return true;
	}

	/**
	 * @return The list of all Edges in the graph pattern
	 */
	public List<MyEdge> getAllEdges(){

		List<MyEdge> rels = new ArrayList<MyEdge>();

		//Obtain the Edges from the map, and add them to the list.
		for (MyNode node : outgoingEdges.keySet()){
			List<MyEdge> temp = outgoingEdges.get(node);
			rels.addAll(temp);
		}

		return rels;
	}

	/**
	 * Returns all of the Edges in the graph pattern that contain the given node
	 * @param node The node
	 * @return all of the Edges in the graph pattern that contain the given node
	 */
	public List<MyEdge> getAllEdges(MyNode node){
		//Initialize result list
		List<MyEdge> result = new ArrayList<MyEdge>();

		//Add all outgoing and incoming Edges for the node
		//Add all outgoing and incoming Edges for the node
		List<MyEdge> temp = outgoingEdges.get(node);
		if (temp != null){
			result.addAll(temp);
		}
		
		temp = incomingEdges.get(node);
		if (temp != null){
			result.addAll(temp);
		}
		return result;
	}

	/**
	 * Removes the given Edge from the graph pattern
	 * @param rel
	 * @return rel if the Edge was removed, else null
	 */
	public MyEdge removeEdge(MyEdge rel){

		//Get the source node of rel and check if it is in the graph pattern
		MyNode source = rel.getSource();
		if (!nodes.contains(source)){
			//If the source node is not found, then return null
			return null;
		}

		//If the source node was found, get its Edges
		List<MyEdge> rels = getEdges(source, MyDirection.OUTGOING);

		//If the given Edge is found in rels, the remove it
		if (rels.contains(rel)){
			rels.remove(rel);
			//Also remove the Edge from incomingEdges
			MyNode target = rel.getTarget();
			incomingEdges.get(target).remove(rel);
		} else {
			//If the given Edge is not found in rels, then return null
			return null;
		}

		return rel;
	}


	/**
	 * Returns the total (= in + out) degree for the given node.
	 * @param node The target node.
	 * @return the total (= in + out) degree for the given node. If node is not part of graph pattern, then return -1.
	 */
	public int getDegree(MyNode node){

		//Check if node is part of graph pattern.
		if (!nodes.contains(node)){
			return -1;
		}

		int degree = outgoingEdges.get(node).size() + incomingEdges.get(node).size();

		return degree;
	}

	/**
	 * @return A human readable representation of the graph pattern
	 */
	public String toString(){

		StringBuilder str = new StringBuilder("Graph Pattern:\n");

		//Add the nodes info
		for (MyNode node : nodes){
			str.append(node + "\n");
		}

		//Add the Edges info
		for (MyNode source : outgoingEdges.keySet()){
			List<MyEdge> list = outgoingEdges.get(source);

			for (MyEdge rel : list){
				str.append(rel.toString()+"\n");
			}
		}

		return str.toString();
	}

}
