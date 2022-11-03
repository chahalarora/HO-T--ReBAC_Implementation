package ca.ucalgary.ispia.graphpatterns.gpchecker.opt.impl;

import java.time.temporal.ValueRange;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;

import ca.ucalgary.ispia.graphpatterns.gpchecker.opt.FCLBJTemporalGPChecker;
import ca.ucalgary.ispia.graphpatterns.graph.AlgorithmType;
import ca.ucalgary.ispia.graphpatterns.graph.DiscoverablePeriod;
import ca.ucalgary.ispia.graphpatterns.graph.GraphPattern;
import ca.ucalgary.ispia.graphpatterns.graph.MyDirection;
import ca.ucalgary.ispia.graphpatterns.graph.MyEdge;
import ca.ucalgary.ispia.graphpatterns.graph.MyNode;

public class DiscoverablePeriodFinder extends FCLBJTemporalGPChecker {
	
	public DiscoverablePeriodFinder() {
	}
	
	public DiscoverablePeriodFinder(AlgorithmType algorithm){
		super.Algorithm = algorithm;
		super.totalCandRelationship = 0;
	}
	
	// get all edges for a vertex.
	@Override
	public Set<MyEdge> GetEdgeForVertex(GraphPattern gp, MyNode vertex){
		Set<MyEdge> resultEdges = new HashSet<MyEdge>();
		// getting all edges from graph pattern for a vertex.
		resultEdges = new HashSet<MyEdge>(gp.getEdges(vertex, MyDirection.BOTH));				
		return resultEdges;
	}
				
	// calculating discoverable period from assigned relationships.
	@Override
	public  ValueRange CalculateDiscoverablePeriod(Collection<Relationship> collection) {
		
		Integer startTime = Integer.MIN_VALUE;
		Integer endTime = Integer.MAX_VALUE;
		
		Iterator<Relationship> assignedRelationshipIterator = collection.iterator();
		
		// finding common overlapping period.
		while(assignedRelationshipIterator.hasNext()){
			Relationship assignedRelationship = assignedRelationshipIterator.next();
			
			if (Integer.valueOf(assignedRelationship.getProperty("startTime").toString()) >= startTime) {
				startTime = Integer.valueOf(assignedRelationship.getProperty("startTime").toString());
			}
			
			if (Integer.valueOf(assignedRelationship.getProperty("endTime").toString()) <= endTime) {
				endTime = Integer.valueOf(assignedRelationship.getProperty("endTime").toString());
			}
		}
		
		// setting result discoverable period with the common period.
//		resultDiscoverablePeriod.setStartTime(startTime);
//		resultDiscoverablePeriod.setEndTime(endTime);
		
//		DiscoverablePeriod resultDiscoverablePeriod = new DiscoverablePeriod(startTime, endTime);
		ValueRange resultDiscoverablePeriod = ValueRange.of(startTime, endTime);
		
		return resultDiscoverablePeriod;
	}
				
	// getting relationships for the corresponding edges.
	@Override
	public List<Relationship> GetRelevantRelationships(GraphPattern gp, MyEdge edge, MyNode vertex, Node assignedVertex) {
		
		List<Relationship> relevantRelationships = new ArrayList<Relationship>();
		
		//Getting direction from edge to vertex.
		Direction dir = null;
		if (edge.getSource().equals(vertex)){
			dir = Direction.OUTGOING;
		} else {
			dir = Direction.INCOMING;
		}

		Iterable<Relationship> queryResult = assignedVertex.getRelationships(edge.getIdentifier(), dir);
		
		for (Relationship rel : queryResult){
			relevantRelationships.add(rel);
		}
		
		return relevantRelationships;
	}
				
	// next edge from the map of candidate edges.
	@Override
	public MyEdge PickNextEdge(Map<MyEdge, List<Relationship>> candEdges) {
		
		MyEdge firstEdge = candEdges.keySet().iterator().next();
		Integer minimumCandidates = candEdges.get(firstEdge).size();
		MyEdge edgeWithMinimumCandidates = firstEdge;
		
		
		for(MyEdge edgeToCheck : candEdges.keySet()) {
			if(candEdges.get(edgeToCheck).size() < minimumCandidates) {
				edgeWithMinimumCandidates = edgeToCheck;
			}
		}
		
		return edgeWithMinimumCandidates;
	}
				
	// sorting relationships, based on starting time.
	@Override
	public List<Relationship> ValueOrdering(List<Relationship> relationships){
		Collections.sort(relationships, (r1, r2) -> Integer.valueOf(r1.getProperty("startTime").toString()).compareTo(Integer.valueOf(r2.getProperty("startTime").toString())));
		
		return relationships;
	}
				
	// finding if both vertices to an edge are assigned or not.
	@Override
	public boolean BothVerticesNotAssigned(MyEdge edge, Map<MyNode, Node> assignmentVertices) {
		MyNode sourceNode = edge.getSource();
		MyNode targetNode = edge.getTarget();
		
		if (!assignmentVertices.keySet().contains(sourceNode) && !assignmentVertices.keySet().contains(targetNode)) {
			//THROW EXCEPTION
		}
		
		
		if (!assignmentVertices.keySet().contains(sourceNode) || !assignmentVertices.keySet().contains(targetNode)) {
			return true;
		}
		
		return false;
	}
				
	// assigning the unassigned vertex to an edge.
	@Override
	public void AssignOtherVertex(MyEdge edge, Relationship relationship, Map<MyNode, Node> assignmentVerticesPrime) {
		
		if(!(assignmentVerticesPrime.keySet().contains(edge.getSource()))) {
			assignmentVerticesPrime.put(edge.getSource(), relationship.getStartNode());
		}
		
		if(!(assignmentVerticesPrime.keySet().contains(edge.getTarget()))){
			assignmentVerticesPrime.put(edge.getTarget(), relationship.getEndNode());
		}
	}
				
	// get edges corresponding to a vertex in the graph pattern.
	@Override
	public Set<MyEdge> GetRelevantEdges(GraphPattern gp, MyNode vertex) {
		return new HashSet<MyEdge>(gp.getEdges(vertex, MyDirection.BOTH));
	}
				
	// getting relationships that overlap with the given relationships.
	@Override
	public List<Relationship> GetOverlappingRelationships(List<Relationship> relationships, List<Relationship> assignedRelationships) {
		List<Relationship> overlappingRelationships =  new ArrayList<Relationship>();
		
		Iterator<Relationship> relationshipsIterator = relationships.iterator();

		// iterating through all the relationships that needs to be checked.
		while(relationshipsIterator.hasNext()){
			
			Relationship relationshipToCheck = relationshipsIterator.next();
			
			Integer relationshipToCheckStartTime = Integer.valueOf(relationshipToCheck.getProperty("startTime").toString());
			Integer relationshipToCheckEndTime = Integer.valueOf(relationshipToCheck.getProperty("endTime").toString());
			
			Iterator<Relationship> assignedRelationshipsIterator = assignedRelationships.iterator();
			
			boolean relationshipOverlap = true;
			
			// Checking if the given relationship to overlaps with all the assigned relationships.
			while(assignedRelationshipsIterator.hasNext()){				
				Relationship assignedRelationship = assignedRelationshipsIterator.next();
				Integer assignedRelationshipStartTime = Integer.valueOf(assignedRelationship.getProperty("startTime").toString());
				Integer assignedRelationshipEndTime = Integer.valueOf(assignedRelationship.getProperty("endTime").toString());
				
				// checking for overlapping time periods between relationships.
				if(((relationshipToCheckStartTime <= assignedRelationshipStartTime) && (assignedRelationshipStartTime <= relationshipToCheckEndTime))) {
//								System.out.println("test stop");
				
				} else if(((relationshipToCheckStartTime <= assignedRelationshipEndTime) && (assignedRelationshipEndTime <= relationshipToCheckEndTime))) {
//								System.out.println("test stop");
				
				}else if(((assignedRelationshipStartTime <= relationshipToCheckStartTime) && (relationshipToCheckStartTime <= assignedRelationshipEndTime))) {
//								System.out.println("test stop");
				
				} else if(((assignedRelationshipStartTime <= relationshipToCheckEndTime) && (relationshipToCheckEndTime <= assignedRelationshipEndTime))) {
//								System.out.println("test stop");

				} else {
					relationshipOverlap = false;
				} 
				
				
			}
					
			// if the relationship overlaps with all assigned relationships then it is added to the result set.
			if(relationshipOverlap) {
				overlappingRelationships.add(relationshipToCheck);
			}	
		}
		
		return overlappingRelationships;
	}
				
	// getting relationships that overlap with the given relationship.
	@Override
	public List<Relationship> GetOverlappingRelationships(List<Relationship> relationships, Relationship relationship) {
		int relationshipToCheckStartTime = Integer.valueOf(relationship.getProperty("startTime").toString());
		int relationshipToCheckEndTime = Integer.valueOf(relationship.getProperty("endTime").toString());
		List<Relationship> overlappingRelationships =  new ArrayList<Relationship>();
		
		Iterator<Relationship> relationshipIterator = relationships.iterator();
		while(relationshipIterator.hasNext()){
			
			Relationship relationshipToCheck = relationshipIterator.next();
			
			Integer assignedRelationshipStartTime = Integer.valueOf(relationshipToCheck.getProperty("startTime").toString());
			Integer assignedRelationshipEndTime = Integer.valueOf(relationshipToCheck.getProperty("endTime").toString());
			boolean relationshipOverlap = false;
			
			// checking for overlapping time periods between relationships.
			if(((relationshipToCheckStartTime <= assignedRelationshipStartTime) && (assignedRelationshipStartTime <= relationshipToCheckEndTime))) {
				relationshipOverlap = true;
			
			} else if(((relationshipToCheckStartTime <= assignedRelationshipEndTime) && (assignedRelationshipEndTime <= relationshipToCheckEndTime))) {
				relationshipOverlap = true;
			
			}else if(((assignedRelationshipStartTime <= relationshipToCheckStartTime) && (relationshipToCheckStartTime <= assignedRelationshipEndTime))) {
				relationshipOverlap = true;
			
			} else if(((assignedRelationshipStartTime <= relationshipToCheckEndTime) && (relationshipToCheckEndTime <= assignedRelationshipEndTime))) {
				relationshipOverlap = true;

			} else {
				relationshipOverlap = false;
			}
			
			if(relationshipOverlap) {
				// if overlap is present then add to result set.
				overlappingRelationships.add(relationshipToCheck);
			}
			
		}
		return overlappingRelationships;
	}
				
	// finding relationships common to given two set of relationships.
	@Override
	public List<Relationship> FindCommonRelationships(List<Relationship> candidateRelationships, List<Relationship> relationships) {
		candidateRelationships.retainAll(relationships);
		return candidateRelationships;
	}
				
	// pruning candidate set using the given vertex
	@Override
	public List<Relationship> FilterSource(List<Relationship> candRelationships, Node assignedSource){
		
		List<Relationship> filteredRelationships =  new ArrayList<Relationship>();
		
		Iterator<Relationship> candRelationshipIterator = candRelationships.iterator();
		
		// Iterating through candidate relationships.
		while(candRelationshipIterator.hasNext()){
			Relationship candRelationshipToCheck = candRelationshipIterator.next();
			
			// Checking if the source node of the relationship is same as the assigned source node.
			if (candRelationshipToCheck.getStartNode().equals(assignedSource)) {
				filteredRelationships.add(candRelationshipToCheck);
			} 	
		}
		
		return filteredRelationships;
	}
				
	// pruning candidate set using the given vertex
	@Override
	public List<Relationship> FilterTarget(List<Relationship> candRelationships, Node assignedTarget){
		
		List<Relationship> filteredRelationships =  new ArrayList<Relationship>();
		
		Iterator<Relationship> candRelationshipIterator = candRelationships.iterator();
		
		// Iterating through candidate relationships.
		while(candRelationshipIterator.hasNext()){
			Relationship candRelationshipToCheck = candRelationshipIterator.next();
			
			// Checking if the source node of the relationship is same as the assigned source node.
			if (candRelationshipToCheck.getEndNode().equals(assignedTarget)) {
				filteredRelationships.add(candRelationshipToCheck);
			} 	
		}
		
		return filteredRelationships;
	}
				
	// whether a partial discoverable time changes when a relationship is assigned.
	@Override
	public boolean PartialDiscoverableTimeChanges(List<Relationship> assignedRelationships, Relationship relationship) {
		Integer startTime = Integer.MIN_VALUE;
		Integer endTime = Integer.MAX_VALUE;
		
		Iterator<Relationship> assignedRelationshipIterator = assignedRelationships.iterator();
		
		// Iterating through assigned relationships to find common overlapping time period.
		while(assignedRelationshipIterator.hasNext()){
			Relationship assignedRelationship = assignedRelationshipIterator.next();
			
			if (Integer.valueOf(assignedRelationship.getProperty("startTime").toString()) >= startTime) {
				startTime = Integer.valueOf(assignedRelationship.getProperty("startTime").toString());
			}
			
			if (Integer.valueOf(assignedRelationship.getProperty("endTime").toString()) <= endTime) {
				endTime = Integer.valueOf(assignedRelationship.getProperty("endTime").toString());
			}	
		}
		
		Integer relationshipToCheckStartTime = Integer.valueOf(relationship.getProperty("startTime").toString());
		Integer relationshipToCheckEndTime = Integer.valueOf(relationship.getProperty("endTime").toString());
		boolean relationshipOverlap = false;
		
		// checking for overlapping time periods between relationships.
		if(((relationshipToCheckStartTime <= startTime) && (startTime <= relationshipToCheckEndTime))) {
			relationshipOverlap = true;
		
		} else if(((relationshipToCheckStartTime <= endTime) && (endTime <= relationshipToCheckEndTime))) {
			relationshipOverlap = true;
		
		}else if(((startTime <= relationshipToCheckStartTime) && (relationshipToCheckStartTime <= endTime))) {
			relationshipOverlap = true;
		
		} else if(((startTime <= relationshipToCheckEndTime) && (relationshipToCheckEndTime <= endTime))) {
			relationshipOverlap = true;

		}
		
		// checking if the relationship overlap with the intersection of assigned relationships.
		if(relationshipOverlap) {
			
			// checking if the relationship is outside the the overlapping period.
			if(relationshipToCheckStartTime > startTime || relationshipToCheckEndTime < endTime) {
				return true;
			}
		}
		
		return false;
	}
}
