package ca.ucalgary.ispia.graphpatterns.graph;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class MyEdge implements HasAttributes, Serializable {
	
private static final long serialVersionUID = -2934381425526755937L;
	
	//The fields
	private final MyNode source;
	private final MyNode target;
	private final RelType identifier;
	private final int id;
	private Map<String, String> attributes;
	/**
	 * Initializes the given fields
	 * @param source The source node
	 * @param target The target node
	 * @param identifier The relation identifier
	 */
	public MyEdge(MyNode source, MyNode target, RelType identifier, int id){
		//Set the fields
		this.source = source;
		this.target = target;
		this.identifier = identifier;
		this.id = id;
		this.attributes = new HashMap<String, String>();
	}
	
	/**
	 * @return The source node
	 */
	public MyNode getSource() {
		return source;
	}

	/**
	 * @return The target node
	 */
	public MyNode getTarget() {
		return target;
	}

	/**
	 * @return The relation identifier
	 */
	public RelType getIdentifier() {
		return identifier;
	}
	
	public int getId(){
		return this.id;
	}

	/**
	 * Returns the opposite node in the relationship. IE if the given node is the source node, then this
	 * method returns the target node, if the given node is the target node, then this method returns the
	 * source node. If the given node is not in the relationship, then returns null.
	 * @param node
	 * @return
	 */
	public MyNode getOther(MyNode node){
		
		if (source.equals(node)){
			return target;
		} 
		else if (target.equals(node)){
			return source;
		}
		
		// Return null if the given node is not either the source or target node
		return null;
		
		
	}

	/**
	 * Adds the given attribute name, value pair to the map of attributes.
	 * @param name The name of the attribute.
	 * @param val The value of the attribute.
	 * @return True if the pair is added to the attributes map, else false.
	 */
	public boolean addAttribute(String name, String val){
		
		//If the attribute already exists, then return false.
		//Else add the pair to the map and return true.
		if (attributes.containsKey(name) || val == null){
			return false;
		}
		else {
			attributes.put(name, val);
		}
		
		return true;
	}
	
	/**
	 * Returns the value of given attribute name.
	 * @param name The attribute name
	 * @return
	 */
	public String getAttribute(String name){
		return attributes.get(name);
	}
	
	/**
	 * Checks if the node has at least 1 attribute
	 * @return True if the node has at least 1 attribute, else false.
	 */
	public boolean hasAttributes(){
		if (attributes.isEmpty()){
			return false;
		} else {
			return true;
		}
	}
	
	/**
	 * Checks if the node has at least 1 attribute
	 * @return True if the node has at least 1 attribute, else false.
	 */
	public boolean hasAttribute(String attrName){
		return attributes.containsKey(attrName);
	}
	
	public void setAttributes(Map<String, String> attributes){
		this.attributes = attributes;
	}
	
	/**
	 * @return The map of attributes
	 */
	public Map<String, String> getAttributes(){
		return this.attributes;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((attributes == null) ? 0 : attributes.hashCode());
		result = prime * result + ((identifier == null) ? 0 : identifier.hashCode());
		result = prime * result + ((source == null) ? 0 : source.hashCode());
		result = prime * result + ((target == null) ? 0 : target.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MyEdge other = (MyEdge) obj;
		if (attributes == null) {
			if (other.attributes != null)
				return false;
		} else if (!attributes.equals(other.attributes))
			return false;
		if (identifier == null) {
			if (other.identifier != null)
				return false;
		} else if (!identifier.equals(other.identifier))
			return false;
		if (source == null) {
			if (other.source != null)
				return false;
		} else if (!source.equals(other.source))
			return false;
		if (target == null) {
			if (other.target != null)
				return false;
		} else if (!target.equals(other.target))
			return false;
		return true;
	}

	@Override
	public String toString() {
		
		StringBuilder sb = new StringBuilder();
		sb.append(identifier + "(" + source.getId() + ", " + target.getId() +") -- ");
		for (String key : attributes.keySet()){
			sb.append(key + " : " + attributes.get(key) + ", ");
		}
		return sb.toString();
	}
}
