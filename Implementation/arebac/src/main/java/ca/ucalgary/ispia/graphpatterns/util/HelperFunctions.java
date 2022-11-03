package ca.ucalgary.ispia.graphpatterns.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;

import ca.ucalgary.ispia.graphpatterns.graph.DiscoverablePeriod;
import ca.ucalgary.ispia.graphpatterns.graph.GraphPattern;
import ca.ucalgary.ispia.graphpatterns.graph.MyDirection;
import ca.ucalgary.ispia.graphpatterns.graph.MyEdge;
import ca.ucalgary.ispia.graphpatterns.graph.MyNode;

public class HelperFunctions {
	
	// get all edges for a vertex.
		public static Set<MyEdge> getEdgeForVertex(GraphPattern gp, MyNode vertex){
			Set<MyEdge> resultEdges = new HashSet<MyEdge>();
			// getting all edges from graph pattern for a vertex.
			resultEdges = new HashSet<MyEdge>(gp.getEdges(vertex, MyDirection.BOTH));
//			gp.getEdges(vertex, MyDirection.BOTH);
			
			
			return resultEdges;
		}
		
		// calculating discoverable period from assigned relationships.
		public static DiscoverablePeriod calculateDiscoverablePeriod(Collection<Relationship> collection) {
			DiscoverablePeriod resultDiscoverablePeriod = new DiscoverablePeriod();
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
			resultDiscoverablePeriod.setStartTime(startTime);
			resultDiscoverablePeriod.setEndTime(endTime);
			
			return resultDiscoverablePeriod;
		}
		
		// getting relationships for the corresponding edges.
		public static List<Relationship> getRelevantRelationships(GraphPattern gp, MyEdge edge, MyNode vertex, Node assignedVertex) {
			
			List<Relationship> relevantRelationships = new ArrayList<Relationship>();
			
			//Getting direction from edge to vertex.
			Direction dir = null;
			if (edge.getSource().equals(vertex)){
				dir = Direction.OUTGOING;
			} else {
				dir = Direction.INCOMING;
			}

//			try (Transaction tx = graphDb.beginTx()){
				Iterable<Relationship> queryResult = assignedVertex.getRelationships(edge.getIdentifier(), dir);
				
				for (Relationship rel : queryResult){
					relevantRelationships.add(rel);
				}
				
//				tx.success();
//			}
			
			return relevantRelationships;
		}
		
		// next edge from the map of candidate edges.
		public static MyEdge pickNextEdge(Map<MyEdge, List<Relationship>> candEdges) {
			
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
		public static List<Relationship> valueOrdering(List<Relationship> relationships){
			
//			List<Relationship> newrelationships = new ArrayList();
			
//			newrelationships.addAll(relationships);
			Collections.sort(relationships, (r1, r2) -> Integer.valueOf(r1.getProperty("startTime").toString()).compareTo(Integer.valueOf(r2.getProperty("startTime").toString())));
			
			return relationships;
		}
		
		// finding if both vertices to an edge are assigned or not.
		public static boolean bothVerticesNotAssigned(MyEdge edge, Map<MyNode, Node> assignmentVertices) {
			MyNode sourceNode = edge.getSource();
			MyNode targetNode = edge.getTarget();
			
			// if any node both source node and target node are not null then return false otherwise return true.
//			if(sourceNode == null || targetNode == null) {
//				return true;			
//			}
			
			if (!assignmentVertices.keySet().contains(sourceNode) && !assignmentVertices.keySet().contains(targetNode)) {
				//THROW EXCEPTION
			}
			
			
			if (!assignmentVertices.keySet().contains(sourceNode) || !assignmentVertices.keySet().contains(targetNode)) {
				return true;
			}
			
			// if source node in not assigned.
//			if (sourceNode != null) {
//				if(!assignmentVertices.keySet().contains(sourceNode)) {
//					return true;
//				}
			
			
//			} 
			
			// if target node in not assigned.
//			if (targetNode != null) {
//				if(!assignmentVertices.keySet().contains(targetNode)) {
//					return true;
//				}
//			}
			
			return false;
		}
		
		// assigning the unassigned vertex to an edge.
		public static void assignOtherVertex(MyEdge edge, Relationship relationship, Map<MyNode, Node> assignmentVerticesPrime) {
			
//			Map<MyNode, Node> assignmentVerticesPrime = new HashMap<MyNode, Node>();
			
			
//			assignmentVerticesPrime.putAll(assignmentVertices);
			
			if(!(assignmentVerticesPrime.keySet().contains(edge.getSource()))) {
				assignmentVerticesPrime.put(edge.getSource(), relationship.getStartNode());
			}
			
			if(!(assignmentVerticesPrime.keySet().contains(edge.getTarget()))){
				assignmentVerticesPrime.put(edge.getTarget(), relationship.getEndNode());
			}
			
//			return assignmentVerticesPrime;
		}
		
		// get edges corresponding to a vertex in the graph pattern.
		public static Set<MyEdge> getRelevantEdges(GraphPattern gp, MyNode vertex) {
//			Set<Item> items = new HashSet<MyEdge>(gp.getAllEdges(vertex)); 
			
//			return new HashSet<MyEdge>(gp.getAllEdges(vertex));
			return new HashSet<MyEdge>(gp.getEdges(vertex, MyDirection.BOTH));
			
//			return (Set<MyEdge>) gp.getAllEdges(vertex);
		}
		
		// getting relationships that overlap with the given relationships.
		public static List<Relationship> getOverlappingRelationships(List<Relationship> relationships, List<Relationship> assignedRelationships) {
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
//						System.out.println("test stop");
					
					} else if(((relationshipToCheckStartTime <= assignedRelationshipEndTime) && (assignedRelationshipEndTime <= relationshipToCheckEndTime))) {
//						System.out.println("test stop");
					
					}else if(((assignedRelationshipStartTime <= relationshipToCheckStartTime) && (relationshipToCheckStartTime <= assignedRelationshipEndTime))) {
//						System.out.println("test stop");
					
					} else if(((assignedRelationshipStartTime <= relationshipToCheckEndTime) && (relationshipToCheckEndTime <= assignedRelationshipEndTime))) {
//						System.out.println("test stop");

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
		public static List<Relationship> getOverlappingRelationships(List<Relationship> relationships, Relationship relationship) {
			int relationshipToCheckStartTime = Integer.valueOf(relationship.getProperty("startTime").toString());
			int relationshipToCheckEndTime = Integer.valueOf(relationship.getProperty("endTime").toString());
			List<Relationship> overlappingRelationships =  new ArrayList<Relationship>();
			
			Iterator<Relationship> relationshipIterator = relationships.iterator();
			while(relationshipIterator.hasNext()){
				
				Relationship relationshipToCheck = relationshipIterator.next();
				
//				
//				if(((overlapStartTime <= Integer.valueOf(relationshipToCheck.getProperty("startTime").toString())) && (Integer.valueOf(relationshipToCheck.getProperty("startTime").toString()) <= overlapEndTime)) || ((overlapStartTime <= Integer.valueOf(relationshipToCheck.getProperty("endTime").toString())) && (Integer.valueOf(relationshipToCheck.getProperty("endTime").toString()) <= overlapEndTime))) {
//					// if overlap is present then add to result set.
//					overlappingRelationships.add(relationshipToCheck);
//				}
				
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
		public static List<Relationship> findCommonRelationships(List<Relationship> candidateRelationships, List<Relationship> relationships) {
			candidateRelationships.retainAll(relationships);
			return candidateRelationships;
		}
		
		// pruning candidate set using the given vertex
		public static List<Relationship> filterSource(List<Relationship> candRelationships, Node assignedSource){
			
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
		public static List<Relationship> filterTarget(List<Relationship> candRelationships, Node assignedTarget){
			
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
		public static boolean partialDiscoverableTimeChanges(List<Relationship> assignedRelationships, Relationship relationship) {
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
			
//			// checking if the relationship overlap with the intersection of assigned relationships.
//			if(!((startTime <= Integer.valueOf(relationship.getProperty("startTime").toString())) && (Integer.valueOf(relationship.getProperty("startTime").toString()) <= endTime)) || ((startTime <= Integer.valueOf(relationship.getProperty("endTime").toString())) && (Integer.valueOf(relationship.getProperty("endTime").toString()) <= endTime))) {
//				
//				// checking if the relationship is outside the the overlapping period.
//				if(Integer.valueOf(relationship.getProperty("startTime").toString()) < startTime || Integer.valueOf(relationship.getProperty("endTime").toString()) > endTime) {
//					return true;
//				}
//			}
			
			
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
