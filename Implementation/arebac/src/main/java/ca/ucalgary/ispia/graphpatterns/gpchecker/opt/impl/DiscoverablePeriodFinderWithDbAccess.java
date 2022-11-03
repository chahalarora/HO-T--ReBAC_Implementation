package ca.ucalgary.ispia.graphpatterns.gpchecker.opt.impl;

import java.time.temporal.ValueRange;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Map;
import java.util.Set;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;

import ca.ucalgary.ispia.graphpatterns.gpchecker.opt.FCLBJTemporalGPChecker;
import ca.ucalgary.ispia.graphpatterns.gpchecker.opt.FCLBJTemporalGPCheckerWithDbAccess;
import ca.ucalgary.ispia.graphpatterns.graph.AlgorithmType;
import ca.ucalgary.ispia.graphpatterns.graph.GraphPattern;
import ca.ucalgary.ispia.graphpatterns.graph.MyDirection;
import ca.ucalgary.ispia.graphpatterns.graph.MyEdge;
import ca.ucalgary.ispia.graphpatterns.graph.MyNode;
import ca.ucalgary.ispia.graphpatterns.graph.RelType;
import ca.ucalgary.ispia.graphpatterns.tests.Killable;
import edu.stanford.nlp.util.Interval;
import edu.stanford.nlp.util.IntervalTree;

public class DiscoverablePeriodFinderWithDbAccess extends FCLBJTemporalGPCheckerWithDbAccess {

	private GraphDatabaseService graphDb = null;
	
	public DiscoverablePeriodFinderWithDbAccess(GraphDatabaseService graphDb, AlgorithmType algorithm){
		super.Algorithm = algorithm;
		this.graphDb = graphDb;
	}
	
	public DiscoverablePeriodFinderWithDbAccess(GraphDatabaseService graphDb, AlgorithmType algorithm, boolean valueOrdering, int initialTimePoint){
		super.Algorithm = algorithm;
		super.ValueOrdering = valueOrdering;
		super.initialTimePoint = initialTimePoint;
		this.graphDb = graphDb;
	}
	
	// get all edges for a vertex.
		@Override
		public Set<MyEdge> GetEdgeForVertex(GraphPattern gp, MyNode vertex){
			
			Set<MyEdge> resultEdges = new HashSet<MyEdge>();
			// getting all edges from graph pattern for a vertex.
			
			try (Transaction tx = graphDb.beginTx()){
				resultEdges = new HashSet<MyEdge>(gp.getEdges(vertex, MyDirection.BOTH));
				tx.success();
			}
			return resultEdges;
		}
					
		// calculating discoverable period from assigned relationships.
		@Override
		public Interval CalculateDiscoverablePeriod(Collection<Relationship> collection) {
			
			int startTime = Integer.MIN_VALUE;
			int endTime = Integer.MAX_VALUE;
			
			Iterator<Relationship> assignedRelationshipIterator = collection.iterator();
			
			// finding common overlapping period.
			while(assignedRelationshipIterator.hasNext()){
				Relationship assignedRelationship = assignedRelationshipIterator.next();
				
				try (Transaction tx = graphDb.beginTx()){
					
					int assignedStartTime = (int)assignedRelationship.getProperty("startTime");
					int assignedEndTime = (int)assignedRelationship.getProperty("endTime");
					
					if (assignedStartTime >= startTime) {
						startTime = assignedStartTime;
					}
					
					if (assignedEndTime <= endTime) {
						endTime = assignedEndTime;
					}
				
					tx.success();
				}
			}
			
			// setting result discoverable period with the common period.
			
			Interval resultDiscoverablePeriod = Interval.toInterval(startTime, endTime);
			
			return resultDiscoverablePeriod;
		}
					
		// getting relationships for the corresponding edges.
		@Override
		public Set<Relationship> GetRelevantRelationships(GraphPattern gp, MyEdge edge, MyNode vertex, Node assignedVertex, int initialTimePoint) {
			
			Set<Relationship> relevantRelationships = new HashSet<Relationship>();
			Iterable<Relationship> queryResult = null;
			
			try (Transaction tx = graphDb.beginTx()){
				//Getting direction from edge to vertex.
				Direction dir = null;
				if (edge.getSource().equals(vertex)){
					dir = Direction.OUTGOING;
				} else {
					dir = Direction.INCOMING;
				}
				queryResult = assignedVertex.getRelationships(edge.getIdentifier(), dir);
				
			
				for (Relationship rel : queryResult){
//					relevantRelationships.add(rel);
					
					if(Integer.valueOf(rel.getProperty("endTime").toString()) >= initialTimePoint) {
						relevantRelationships.add(rel);
					}
					
				}
				
				tx.success();
			}
			
			return relevantRelationships;
		}
		
		
		// getting relationships for the corresponding edges.
//		@Override
//		public Set<Relationship> GetInfoRelevantRelationships(GraphPattern gp, MyEdge edge, MyNode vertex, Node assignedVertex, int initialTimePoint) {
//			
//			Set<Relationship> relevantInfoRelationships = new HashSet<Relationship>();
//			Iterable<Relationship> queryResult = null;
//			
//			try (Transaction tx = graphDb.beginTx()){
//				//Getting direction from edge to vertex.
//				Direction dir = null;
//				if (edge.getSource().equals(vertex)){
//					dir = Direction.OUTGOING;
//				} else {
//					dir = Direction.INCOMING;
//				}
//				queryResult = assignedVertex.getRelationships(edge.getIdentifier(), dir);
//				
//			
//				for (Relationship rel : queryResult){
//					
//					if(Integer.valueOf(rel.getProperty("endTime").toString()) >= initialTimePoint) {
//						relevantInfoRelationships.add(rel);
//					}
//				}
//							
//				tx.success();
//			}
//			
//			System.out.println("Number of info rel: "+ relevantInfoRelationships.size());
//			
//			return relevantInfoRelationships;
//		}
		
					
		// next edge from the map of candidate edges.
		@Override
		public MyEdge PickNextEdge(GraphPattern gp, Map<MyEdge, Relationship> assignmentEdges, Map<MyEdge, Set<Relationship>> candEdges) {
			
			Set<MyEdge> allEdges = new HashSet(gp.getAllEdges());
			// Find nodes such that they are populated but not yet assigned.
			// Pick the node with the smallest candidates size
			// Optimization idea: When a node is assigned, remove it from candidates. Thus,
			// candidates only consists of unassigned nodes.

			MyEdge nextEdge = null;
			int candidatesSize = 0;

			//Loop through the nodes
			for (MyEdge edge : allEdges){
				//If the node is populated but not assigned
				if ((!assignmentEdges.containsKey(edge)) && (candEdges.containsKey(edge))){

					//Get the candidates size
					int newSize = candEdges.get(edge).size();

					//Update nextNode with the current node if nextNode is null or the current node has a smaller candidate size 
					//than the nextNode
					if ((nextEdge == null) || (newSize < candidatesSize)){
						nextEdge = edge;
						candidatesSize = newSize;
					}
				}
			}
			
			return nextEdge;
			
		}
					
		// sorting relationships, based on starting time.
		@Override
		public List<Relationship> ValueOrdering(Set<Relationship> relationships){
			
			List<Relationship> relationshipsList = new ArrayList<Relationship>(relationships);
			
			try (Transaction tx = graphDb.beginTx()){
				Collections.sort(relationshipsList, (r1, r2) -> Integer.valueOf(r1.getProperty("startTime").toString()).compareTo(Integer.valueOf(r2.getProperty("startTime").toString())));
				tx.success();
			}
			
//			return relationships;
			
			return relationshipsList;
			
//			return new HashSet<Relationship>(relationshipsList);
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
			try (Transaction tx = graphDb.beginTx()){
				
				if(!(assignmentVerticesPrime.keySet().contains(edge.getSource()))) {
					assignmentVerticesPrime.put(edge.getSource(), relationship.getStartNode());
				}
				
				if(!(assignmentVerticesPrime.keySet().contains(edge.getTarget()))){
					assignmentVerticesPrime.put(edge.getTarget(), relationship.getEndNode());
				}
			
				tx.success();
			}
		}
					
		// get edges corresponding to a vertex in the graph pattern.
		@Override
		public Set<MyEdge> GetRelevantEdges(GraphPattern gp, MyNode vertex) {
			Set<MyEdge> relevantEdges = new HashSet<MyEdge>();
			try (Transaction tx = graphDb.beginTx()){
				relevantEdges.addAll(new HashSet<MyEdge>(gp.getEdges(vertex, MyDirection.BOTH)));
				tx.success();
			}
			return relevantEdges;
		}
					
		
		@Override
		public Set<Relationship> GetTimeOverlapRelationships(Set<Relationship> relationships, Map<MyEdge, Relationship> assignedRelationships, MyEdge sourceEdge, Map<MyEdge, Set<MyEdge>> conflictIn){
			Set<Relationship> overlappingRelationships =  new HashSet<Relationship>();
			
			Iterator<Relationship> relationshipsIterator = relationships.iterator();

			try (Transaction tx = graphDb.beginTx()){
				
				// iterating through all the relationships that needs to be checked.
				while(relationshipsIterator.hasNext()){
					
					Relationship relationshipToCheck = relationshipsIterator.next();
					
					Integer relationshipToCheckStartTime = Integer.valueOf(relationshipToCheck.getProperty("startTime").toString());
					Integer relationshipToCheckEndTime = Integer.valueOf(relationshipToCheck.getProperty("endTime").toString());
					
					boolean relationshipOverlap = true;
					
					// Checking if the given relationship to overlaps with all the assigned relationships.
					for (MyEdge currEdge : assignedRelationships.keySet()){				
						Relationship assignedRelationship = assignedRelationships.get(currEdge);
						Integer assignedRelationshipStartTime = Integer.valueOf(assignedRelationship.getProperty("startTime").toString());
						Integer assignedRelationshipEndTime = Integer.valueOf(assignedRelationship.getProperty("endTime").toString());
						
						// checking for overlapping time periods between relationships.
						if(((relationshipToCheckStartTime <= assignedRelationshipStartTime) && (assignedRelationshipStartTime <= relationshipToCheckEndTime))) {
		//								//System.out.println("test stop");
						
						} else if(((relationshipToCheckStartTime <= assignedRelationshipEndTime) && (assignedRelationshipEndTime <= relationshipToCheckEndTime))) {
		//								//System.out.println("test stop");
						
						}else if(((assignedRelationshipStartTime <= relationshipToCheckStartTime) && (relationshipToCheckStartTime <= assignedRelationshipEndTime))) {
		//								//System.out.println("test stop");
						
						} else if(((assignedRelationshipStartTime <= relationshipToCheckEndTime) && (relationshipToCheckEndTime <= assignedRelationshipEndTime))) {
		//								//System.out.println("test stop");
		
						} else {
							addConflictIn(currEdge, sourceEdge, conflictIn);
							relationshipOverlap = false;
						} 
					}
							
					// if the relationship overlaps with all assigned relationships then it is added to the result set.
					if(relationshipOverlap) {
						overlappingRelationships.add(relationshipToCheck);
					}
				}
			
				tx.success();
				
			}
			
			return overlappingRelationships;
		}
		
					
		// getting relationships that overlap with the given relationship.
		@Override
		public Set<Relationship> GetOverlappingRelationships(Set<Relationship> relationships, Relationship relationship) {
			
			Set<Relationship> overlappingRelationships =  new HashSet<Relationship>();
			
			Iterator<Relationship> relationshipIterator = relationships.iterator();
			
			try (Transaction tx = graphDb.beginTx()){
			
				int relationshipToCheckStartTime = (int) relationship.getProperty("startTime");
				int relationshipToCheckEndTime = (int) relationship.getProperty("endTime");
				
				while(relationshipIterator.hasNext()){
					
					Relationship relationshipToCheck = relationshipIterator.next();
					
					int assignedRelationshipStartTime = (int) relationshipToCheck.getProperty("startTime");
					int assignedRelationshipEndTime = (int) relationshipToCheck.getProperty("endTime");
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
				
				tx.success();
			}
			
			
			
			
			
			
			return overlappingRelationships;
		}
					
		// finding relationships common to given two set of relationships.
		@Override
		public Set<Relationship> FindCommonRelationships(Set<Relationship> candidateRelationships, Set<Relationship> relationships) {
			candidateRelationships.retainAll(relationships);
			return candidateRelationships;
		}
					
		// pruning candidate set using the given vertex
		@Override
		public Set<Relationship> FilterSource(Set<Relationship> candRelationships, Node assignedSource){
			
			Set<Relationship> filteredRelationships =  new HashSet<Relationship>();
			
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
		public Set<Relationship> FilterTarget(Set<Relationship> candRelationships, Node assignedTarget){
			
			Set<Relationship> filteredRelationships =  new HashSet<Relationship>();
			
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
		public boolean PartialDiscoverableTimeChanges(Set<Relationship> assignedRelationships, Relationship relationship) {
			int startTime = Integer.MIN_VALUE;
			int endTime = Integer.MAX_VALUE;
			int relationshipToCheckStartTime = Integer.MIN_VALUE;
			int relationshipToCheckEndTime = Integer.MAX_VALUE;
			boolean relationshipOverlap = false;
			
			Iterator<Relationship> assignedRelationshipIterator = assignedRelationships.iterator();
			
			try (Transaction tx = graphDb.beginTx()){
				
				// Iterating through assigned relationships to find common overlapping time period.
				while(assignedRelationshipIterator.hasNext()){
					Relationship assignedRelationship = assignedRelationshipIterator.next();
					
					if ((int) assignedRelationship.getProperty("startTime") >= startTime) {
						startTime = (int) assignedRelationship.getProperty("startTime");
					}
					
					if ((int) assignedRelationship.getProperty("endTime") <= endTime) {
						endTime = (int) assignedRelationship.getProperty("endTime");
					}	
				}
				
				relationshipToCheckStartTime = (int) relationship.getProperty("startTime");
				relationshipToCheckEndTime = (int) relationship.getProperty("endTime");
				
				
				tx.success();
			}
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
					if(relationshipToCheckStartTime >= startTime || relationshipToCheckEndTime <= endTime) {
						return true;
					}
				}
			
			return false;
		}
		
		
		// whether a partial discoverable time changes when a relationship is assigned.
//			@Override
//			public boolean PartialDiscoverableTimeChangesUpdated(Set<ValueRange> partialDiscoverablePeriods, Set<Relationship> assignedRelationships, Relationship relationship) {
//				int startTime = Integer.MIN_VALUE;
//				int endTime = Integer.MAX_VALUE;
//				int relationshipToCheckStartTime = Integer.MIN_VALUE;
//				int relationshipToCheckEndTime = Integer.MAX_VALUE;
//				boolean relationshipOverlap = false;
//				
//				Iterator<Relationship> assignedRelationshipIterator = assignedRelationships.iterator();
//				
//				try (Transaction tx = graphDb.beginTx()){
//					
//					// Iterating through assigned relationships to find common overlapping time period.
//					while(assignedRelationshipIterator.hasNext()){
//						Relationship assignedRelationship = assignedRelationshipIterator.next();
//						
//						if ((int) assignedRelationship.getProperty("startTime") >= startTime) {
//							startTime = (int) assignedRelationship.getProperty("startTime");
//						}
//						
//						if ((int) assignedRelationship.getProperty("endTime") <= endTime) {
//							endTime = (int) assignedRelationship.getProperty("endTime");
//						}	
//					}
//					
//					relationshipToCheckStartTime = (int) relationship.getProperty("startTime");
//					relationshipToCheckEndTime = (int) relationship.getProperty("endTime");
//					
//					
//					tx.success();
//				}
//					// checking for overlapping time periods between relationships.
//					if(((relationshipToCheckStartTime <= startTime) && (startTime <= relationshipToCheckEndTime))) {
//						relationshipOverlap = true;
//					
//					} else if(((relationshipToCheckStartTime <= endTime) && (endTime <= relationshipToCheckEndTime))) {
//						relationshipOverlap = true;
//					
//					}else if(((startTime <= relationshipToCheckStartTime) && (relationshipToCheckStartTime <= endTime))) {
//						relationshipOverlap = true;
//					
//					} else if(((startTime <= relationshipToCheckEndTime) && (relationshipToCheckEndTime <= endTime))) {
//						relationshipOverlap = true;
//			
//					}
//					
//					// checking if the relationship overlap with the intersection of assigned relationships.
//					if(relationshipOverlap) {
//						
//						// checking if the relationship is outside the the overlapping period.
//						if(relationshipToCheckStartTime >= startTime || relationshipToCheckEndTime <= endTime) {
//							
////							boolean containedInDiscoverablePeriods = false;
//							
//							for(ValueRange discoverblePeriod: partialDiscoverablePeriods) {
//								
//								int dpStartTime = (int) discoverblePeriod.getMinimum();
//								int dpEndTime = (int) discoverblePeriod.getMaximum();
//								
//								if(relationshipToCheckStartTime >= dpStartTime && relationshipToCheckEndTime <= dpEndTime) {
//									return false;
//								}
//								
//							}
//							
//							return true;
//						}
//					}
//				
//				return false;
//			}
		
		@Override
		public void addConflictIn(MyEdge src, MyEdge tgt, Map<MyEdge, Set<MyEdge>> confIn){
			
			if (confIn.containsKey(tgt)){
				//If the target already has incoming conflicts, then add the src to the conflict in set.
				confIn.get(tgt).add(src);
			} else {
				//If the target doesn't have incoming conflicts, then create a new set, add the src, and then 
				//put the key-value pair in the confIn map.
				Set<MyEdge> confSet = new HashSet<MyEdge>();
				confSet.add(src);
				confIn.put(tgt, confSet);
			}

			//Maintain the influence chains.
			//Add the src's confIn conflicts to the tgt's.
			if (confIn.containsKey(src)){
				confIn.get(tgt).addAll(confIn.get(src));
			}
		}
		
		@Override
		public Set<MyEdge> deadEndJump(MyEdge edge, Set<MyEdge> confOut, Map<MyEdge, Set<MyEdge>> confIn) {
			
			//The set to help the return value
			Set<MyEdge> jumpVars = new HashSet<MyEdge>();

			//For the nodes whose candidate set was emptied, backjump to their confIn set.
			//Perhaps this will allow us to retain a different set of candidates that would work.
			for (MyEdge node : confOut){
				if (confIn.containsKey(node)){
					jumpVars.addAll(confIn.get(node));
				}
			}

			//Backjump to the nodes that filtered the candidate set for the current node.
			//Perhaps this will allows us to retain a different set of candidates that would work.
			if (confIn.containsKey(edge)){
				jumpVars.addAll(confIn.get(edge));
			}

			return jumpVars;
			
		}

		@Override
		public boolean ContainedRelationshipCheck(Set<Relationship> assignedRelationships, Relationship relationship, IntervalTree discoveredPeriods) {
			
			if(discoveredPeriods.size() == 0) {
				return false;
				
			} else {
				Set<Relationship> assignedRelationshipsPrime = new HashSet<Relationship>();
				assignedRelationshipsPrime.addAll(assignedRelationships);	
				assignedRelationshipsPrime.add(relationship);
					
				Interval assignedDiscoverablePeriod = CalculateDiscoverablePeriod(assignedRelationshipsPrime);
				
				int relationshipToCheckStartTime = (int) assignedDiscoverablePeriod.first;
				int relationshipToCheckEndTime = (int) assignedDiscoverablePeriod.second;
				
				boolean result = false;
				
				result = discoveredPeriods.containsInterval( Interval.toInterval(relationshipToCheckStartTime, relationshipToCheckEndTime), false);
				
				
				if(result) {
//					System.out.println("Assigned DP: "+assignedDiscoverablePeriod);
					Iterator<Interval> iterator =  discoveredPeriods.iterator();
					
					while(iterator.hasNext()) {
						Interval dp = iterator.next();
//						System.out.println("dp: "+dp);
					}
					
					
					boolean x = true;
				}
				
				return result;
			}
		}

//		@Override
//		public void kill() {
//			// TODO Auto-generated method stub
//			this.killed = true;
//			System.out.print("KILLED ");
//			
//		}

//		@Override
//		public void UpdateOfficialPeriodToTree(IntervalTree intervalTree, Set<ValueRange> overlapPeriodSet) {
//			
//			intervalTree.clear();
//			
//			for(ValueRange overlapPeriod: overlapPeriodSet) {
//				
//				intervalTree.add(Interval.toInterval( (int) overlapPeriod.getMinimum(), (int) overlapPeriod.getMaximum()));
//			}
//			
//		}
}
