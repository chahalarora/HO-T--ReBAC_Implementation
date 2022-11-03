package ca.ucalgary.ispia.graphpatterns.gpchecker.opt;

import java.time.temporal.ValueRange;
import java.util.HashSet;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;

import ca.ucalgary.ispia.graphpatterns.graph.GraphPattern;
import ca.ucalgary.ispia.graphpatterns.graph.MyEdge;
import ca.ucalgary.ispia.graphpatterns.graph.MyNode;

public abstract class CBJTemporalGPChecker {

	
	private Set<ValueRange> overlapPeriodSet = new HashSet<ValueRange>();
		
	private final boolean ValueOrdering = false;
	
	private int numSolutions = 0;
	
	private GraphDatabaseService graphDb = null;
		
	private int totalCandRelationship = 0;
	
	private boolean killed;
	
	// initialize algorithm
	public Set<ValueRange> CBJTemporalInit (GraphDatabaseService graphDb, GraphPattern gp, Map<MyNode, Node> info) {
		
//		System.out.println("Algorithm: CBJ");
		
		this.graphDb = graphDb;
		
//		this.totalCandRelationship = 0;
//		this.overlapPeriodSet.clear();
//		Set<ValueRange> overlapPeriodSet = new HashSet<ValueRange>();
		Map<MyEdge, Set<Relationship>> candEdges = new HashMap<MyEdge, Set<Relationship>>();
		
		MyNode vertex = info.keySet().iterator().next();
		Node assignedVertex = info.get(vertex);
		
		for(MyEdge edge : GetEdgeForVertex(gp, vertex)) {
			
			candEdges.put(edge, GetRelevantRelationships(gp, edge, vertex, assignedVertex) );
		}
		
		Map<MyNode, Node> assignmentVertices = new HashMap<MyNode, Node>();
		
		assignmentVertices.put(vertex, assignedVertex);
		
		int numSolution = 0;
		
		CBJTemporalRec( gp, candEdges, assignmentVertices, new HashMap<MyEdge, Relationship>(), new HashMap<MyEdge, Set<MyEdge>>());
		

		
		Set<ValueRange> resultSet =  new HashSet<ValueRange>(overlapPeriodSet);
		
		
		
		
		
//		Integer totalCandRelationships = new Integer(this.totalCandRelationship);
//		
		
//		System.out.println("Total cand relationships: " + totalCandRelationships);
//		System.out.println("Result Set: " + resultSet.size());
		
//		if(resultSet.size() == 0) {
//			System.out.println("GP: " + gp);
//			System.out.println("Info: " + info);
//		}
		
		return resultSet;
		
	}
	
	public Set<MyEdge> CBJTemporalRec( GraphPattern gp, Map<MyEdge, Set<Relationship>> candEdges, Map<MyNode, Node> assignmentVertices, Map<MyEdge, Relationship> assignmentEdges, Map<MyEdge, Set<MyEdge>> conflictsIn) {
		
		
		if (killed){
			return null;
		}
		
		// initializing local variables.
		Set<Relationship> candRelationships = new HashSet<Relationship>();
		Set<MyEdge> conflicts = new HashSet<MyEdge>();
		Set<MyEdge> confOut = new HashSet<MyEdge>();
		MyEdge edge = null;
		
		// if the size of graph pattern is equal to assignment of vertices and edges.
		if (gp.getNodes().size() == assignmentVertices.keySet().size() && gp.getAllEdges().size() == assignmentEdges.keySet().size() ){
			
			numSolutions++;
			
			// adding the discoverable time period calculated from assigned edges.
			ValueRange discoverablePeriod = CalculateDiscoverablePeriod(assignmentEdges.values());
			
//			this.overlapPeriodSet.add(CalculateDiscoverablePeriod(assignmentEdges.values()));
			
//			this.overlapPeriodSet.add(discoverablePeriod);
			
//			System.out.println("test print");
			
			overlapPeriodSet.add(discoverablePeriod);
			
//			this.numSolutions = increamentNumSolution(this.numSolutions);
			
//			increamentNumSolution(numSolution);
			
			
			return new HashSet<MyEdge>();
			
		}
		
//		Map<MyEdge, Set<Relationship>> candEdgesClone = new HashMap(candEdges);
		
		edge = PickNextEdge(gp, assignmentEdges, candEdges);
		
		
//		System.out.println("Current edge: " + edge);
//		System.out.println("Cand edges: " + candEdges.keySet());
//		System.out.println("Assignment edges: " + assignmentEdges.keySet());
//		System.out.println("\n");
		
//		try {
//			TimeUnit.MILLISECONDS.sleep(100);
//			edge = PickNextEdge(candEdges);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		if(ValueOrdering) {
			candRelationships = ValueOrdering(candEdges.get(edge));		
					
		} else {
			
			candRelationships = candEdges.get(edge);

			//			try {
//				candRelationships = candEdges.get(edge);
//				TimeUnit.MILLISECONDS.sleep(1000);
//				
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
			
		}
			
		
		boolean deadEnd= true;
		int bjFlag = numSolutions;
		
//		System.out.println("Num solution: " + bjFlag);
//		System.out.println("bjFlage: " + this.numSolutions);
		
//		System.out.println("NumSolutions: "+numSolutions);
//		System.out.println("OverlapPeriodSet size: " + overlapPeriodSet.size());
//		System.out.println("OverlapPeriodSet size: " + overlapPeriodSet.size());
		
		
		for (Relationship relationship: candRelationships) {
						
//			this.totalCandRelationship = this.totalCandRelationship + candRelationships.size();
			
//			System.out.println(candRelationships.size());
			
			
			
			Map<MyEdge, Set<Relationship>> candEdgesPrime = new HashMap<MyEdge, Set<Relationship>>(); 
			Map<MyNode, Node> assignmentVerticesPrime = new HashMap<MyNode, Node>(); 
			Map<MyEdge, Relationship> assignmentEdgesPrime = new HashMap<MyEdge, Relationship>(); 
			Map<MyEdge, Set<MyEdge>> conflictsInPrime = new HashMap<MyEdge, Set<MyEdge>>();
			boolean validVertex = true;
			boolean validEdge = false;
			
//			System.out.println("Cand Edges: "+ candEdges.size());
			
			for(Map.Entry<MyEdge, Set<Relationship>> entry : candEdges.entrySet()) {
				
				Set<Relationship> relationshipPrime = new HashSet<Relationship>();
				
				relationshipPrime.addAll(entry.getValue());
				
				candEdgesPrime.put(entry.getKey(), relationshipPrime);
			}
			
			candEdgesPrime.remove(edge);
			
			
//			System.out.println("Cand Edges Prime: "+ candEdgesPrime.size());
			
			for(Map.Entry<MyEdge, Relationship> entry : assignmentEdges.entrySet()) {
				
				assignmentEdgesPrime.put(entry.getKey(), entry.getValue());
			}
			
			assignmentEdgesPrime.put(edge, relationship);
			
			for(Map.Entry<MyNode, Node> entry : assignmentVertices.entrySet()) {
				
				assignmentVerticesPrime.put(entry.getKey(), entry.getValue());
			}
				
			for(Map.Entry<MyEdge, Set<MyEdge>> entry : conflictsIn.entrySet()) {
				
				Set<MyEdge> edgePrime = new HashSet<MyEdge>(entry.getValue());
				conflictsInPrime.put(entry.getKey(), edgePrime);
			} 
			
			if (BothVerticesNotAssigned(edge, assignmentVertices)) {
				
				// to check whether source was assigned to the edge or the target.
				MyNode unassignedVertex = null;
				if (assignmentVerticesPrime.containsKey(edge.getSource())) {
					unassignedVertex = edge.getTarget();
				} else {
					unassignedVertex = edge.getSource();
				}
			
				AssignOtherVertex(edge, relationship, assignmentVerticesPrime);
								
				validVertex = FCV(gp, candEdgesPrime, assignmentEdgesPrime, assignmentVerticesPrime, unassignedVertex, edge, conflictsInPrime, confOut);
			}
			
			if (validVertex) {
				validEdge = FCE(gp, candEdgesPrime, assignmentEdgesPrime, edge, relationship, conflictsInPrime, confOut); 
			}
			
//			System.out.println("Candidate for edges:");
			for (MyEdge e : candEdgesPrime.keySet()){
//				System.out.println(e + ": " + candEdgesPrime.get(e));
			}
			
//			System.out.println("ConfOut: " + confOut);
//			System.out.println("ConfIn:" );
			for (MyEdge e : conflictsInPrime.keySet()){
//				System.out.println(e + ": " + conflictsInPrime.get(e));
			}
			
			if (validEdge) {
				
				
									
				deadEnd = false;
				
				Set<MyEdge> allEdges =  CBJTemporalRec( gp, candEdgesPrime, assignmentVerticesPrime, assignmentEdgesPrime, conflictsInPrime);
				
				
				if (killed){
					return null;
				}
				
				if(allEdges != null) {
				
//					System.out.println("All edges: "+ allEdges);
					
					if(!(allEdges.isEmpty()) && !allEdges.contains(edge)) {
//						System.out.println("\nBack Jumping from " + edge + " to: "  + allEdges + "\n");
						return allEdges;
					
					} else {
						
						conflicts.addAll(allEdges);
					}
					
				} else {
					//System.out.println("Error!!!");
					System.out.println("Unexpected Return value: NULL");
					killed = true;
					return null;
				}
			}
		}
		
//		System.out.println("Num solutionPrime: " + numSolutions);
//		System.out.println("bjFlagPrime: " + bjFlag );
//		System.out.println("overlapPeriodSet.size(): " + overlapPeriodSet.size());
		
		if(deadEnd) {
			
			Set<MyEdge> conflictsToAdd = new HashSet<MyEdge>(deadEndJump(edge, confOut, conflictsIn));	
			conflicts.addAll(conflictsToAdd);
			return conflicts;
//			return new HashSet<MyEdge>();
			
		} else {

//			System.out.println("Test Stop.!!!!!!!!!!!!!!!!!!!!!!!");
			return new HashSet<MyEdge>();
//			return null;
		}
	
	}
	
		
	public boolean FCV(	GraphPattern gp, 
						Map<MyEdge, Set<Relationship>> candEdges, 
						Map<MyEdge, Relationship> assignmentEdges, 
						Map<MyNode, Node> assignmentVertices, 
						MyNode vertex, 
						MyEdge edge,  
						Map<MyEdge, Set<MyEdge>> conflictIn, 
						Set<MyEdge> confOut) {
		
		Set<MyEdge> edges = new HashSet<MyEdge>();
		Set<Relationship> relationships = new HashSet<Relationship>();
		int sizeCandEdges = 0;
		int newSizeCandEdges = 0;
		
		edges = GetRelevantEdges(gp, vertex);
		
		for (MyEdge currentEdge : edges) {
			
			if (!(assignmentEdges.keySet().contains(currentEdge))) {
				
				relationships = GetRelevantRelationships(gp, currentEdge, vertex, assignmentVertices.get(vertex));
				
//				relationships = GetOverlappingRelationships(relationships, new HashSet<Relationship>(assignmentEdges.values()));
				relationships = timeOverlap(relationships, assignmentEdges, currentEdge, conflictIn);
				
				Set<Relationship> currentEdgeRelationships = candEdges.get(currentEdge);
				
				if(currentEdgeRelationships == null) {
					
					candEdges.put(currentEdge, relationships);
					addConflictIn(edge, currentEdge, conflictIn);
							
				} else {
					
					sizeCandEdges = candEdges.get(currentEdge).size();
					
					candEdges.put(currentEdge, FindCommonRelationships(candEdges.get(currentEdge), relationships) );
					
					newSizeCandEdges = candEdges.get(currentEdge).size();
					
										
					
					if(sizeCandEdges != newSizeCandEdges) {
						addConflictIn(edge, currentEdge, conflictIn);
					}
				}
				
				if (candEdges.get(currentEdge).size() == 0) {
					confOut.add(currentEdge);
					
					return false;
				}	
				
			}
				
		}
		
		return true;
		
	}
	
	
	public boolean FCE(	GraphPattern gp, 
						Map<MyEdge, Set<Relationship>> candEdges, 
						Map<MyEdge, Relationship> assignmentEdges, 
						MyEdge edge, 
						Relationship relationship,  
						Map<MyEdge, Set<MyEdge>> conflictIn, 
						Set<MyEdge> confOut) {
		
		try (Transaction tx = graphDb.beginTx()){
			int sizeCandEdges = 0;
			int newSizeCandEdges = 0;
				
			for (MyEdge currentEdge: candEdges.keySet()) {
				
				sizeCandEdges = candEdges.get(currentEdge).size();
				
				candEdges.put(currentEdge, GetOverlappingRelationships(candEdges.get(currentEdge), relationship));
	//			candEdges.put(currentEdge, timeOverrlapRelationships(candEdges.get(currentEdge), relationship, currentEdge , edge, conflictIn));
				
				
				Node assnEdgeStartNode =  null;
				Node assnEdgeEndNode = null;
				
			
					
					assnEdgeStartNode =  assignmentEdges.get(edge).getStartNode();
					assnEdgeEndNode =  assignmentEdges.get(edge).getEndNode();
	
				
				
	//				if (currentEdge.getSource().equals(edge.getSource()) ) {
	//					candEdges.put(currentEdge, FilterSource(candEdges.get(currentEdge), assignmentEdges.get(edge).getStartNode()));
	//				}
	//				
	//				if (currentEdge.getTarget().equals(edge.getTarget())) {
	//					candEdges.put(currentEdge, FilterTarget(candEdges.get(currentEdge), assignmentEdges.get(edge).getEndNode()));
	//				}
	//				
	//				if (currentEdge.getSource().equals(edge.getTarget())) {
	//					candEdges.put(currentEdge, FilterSource(candEdges.get(currentEdge), assignmentEdges.get(edge).getEndNode()));
	//				}
	//				
	//				if (currentEdge.getTarget().equals(edge.getSource()) ) {
	//					candEdges.put(currentEdge, FilterTarget(candEdges.get(currentEdge), assignmentEdges.get(edge).getStartNode()));
	//				}
					
					
				boolean otherVertexExists = false;
				
				if (currentEdge.getSource().equals(edge.getSource()) ) {
					candEdges.put(currentEdge, FilterSource(candEdges.get(currentEdge), assnEdgeStartNode));
					otherVertexExists = true;
				}
				
				if (currentEdge.getTarget().equals(edge.getTarget())) {
					candEdges.put(currentEdge, FilterTarget(candEdges.get(currentEdge), assnEdgeEndNode));
					otherVertexExists = true;
				}
				
				if (currentEdge.getSource().equals(edge.getTarget())) {
					candEdges.put(currentEdge, FilterSource(candEdges.get(currentEdge), assnEdgeEndNode));
					otherVertexExists = true;
				}
				
				if (currentEdge.getTarget().equals(edge.getSource()) ) {
					candEdges.put(currentEdge, FilterTarget(candEdges.get(currentEdge), assnEdgeStartNode));
					otherVertexExists = true;
				}
					
	//			if(!otherVertexExists) {
	//				addConflictIn(edge, currentEdge, conflictIn);
	//			}
				
				newSizeCandEdges = candEdges.get(currentEdge).size();
				
				if(newSizeCandEdges == 0) {
					confOut.add(currentEdge);
					return false;
				}
					
				if(newSizeCandEdges != sizeCandEdges) {
					
					addConflictIn(edge, currentEdge, conflictIn);
					
//					Set<MyEdge> conflictCurrentEdges = conflictIn.get(currentEdge);
					
//					if(conflictCurrentEdges == null) {
//						Set<MyEdge> edgeSet = new HashSet<MyEdge>();
//						edgeSet.add(edge);
//						addConflictIn(edge, currentEdge, conflictIn);
//						
//					} else {
//						addConflictIn(edge, currentEdge, conflictIn);
//					}
				}
			}
				
			tx.success();
		}	
		
		return true;
	}
	
	public Set<MyEdge> deadEndJump(MyEdge edge, Set<MyEdge> confOut, Map<MyEdge, Set<MyEdge>> confIn) {
		
		//The set to help the return value
		Set<MyEdge> jumpVars = new HashSet<MyEdge>();

		//For the nodes whose candidate set was emptied, backjump to their confIn set.
		//Perhaps this will allow us to retain a different set of candidates that would work.
		for (MyEdge confOutEdge : confOut){
			
			if (confIn.containsKey(confOutEdge)){
				
				Set<MyEdge> confOutEdgesToCopy = new HashSet<MyEdge>(confIn.get(confOutEdge));
				
				jumpVars.addAll(confOutEdgesToCopy);
			}
		}

		//Backjump to the nodes that filtered the candidate set for the current node.
		//Perhaps this will allows us to retain a different set of canddiates taht would work.
		if (confIn.containsKey(edge)){
			Set<MyEdge> edgesToCopy = new HashSet<MyEdge>(confIn.get(edge));
			jumpVars.addAll(edgesToCopy);
		}


		return jumpVars;
		
	}
	
	private void addConflictIn(MyEdge src, MyEdge tgt, Map<MyEdge, Set<MyEdge>> confIn){
		
//		if(src != tgt) {
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
//		}
	}
	
	
	private Set<Relationship> timeOverlap (Set<Relationship> relationships, Map<MyEdge, Relationship> assignedRelationships, MyEdge sourceEdge, Map<MyEdge, Set<MyEdge>> conflictIn){
		Set<Relationship> overlappingRelationships =  new HashSet<Relationship>();
		
		
		try (Transaction tx = graphDb.beginTx()){
		
			Iterator<Relationship> relationshipsIterator = relationships.iterator();
	
			// iterating through all the relationships that needs to be checked.
			while(relationshipsIterator.hasNext()){
							
				boolean relationshipOverlap = true;
				
				Relationship relationshipToCheck = relationshipsIterator.next();
				
				
					
	
					Integer relationshipToCheckStartTime = Integer.valueOf(relationshipToCheck.getProperty("startTime").toString());
					Integer relationshipToCheckEndTime = Integer.valueOf(relationshipToCheck.getProperty("endTime").toString());
				
				
				
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
							addConflictIn(sourceEdge, currEdge, conflictIn);
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
	
	
	
	
	public Set<Relationship> timeOverrlapRelationships(Set<Relationship> relationships, Relationship relationship, MyEdge currEdge, MyEdge tgtEdge, Map<MyEdge, Set<MyEdge>> conflictIn) {
		int relationshipToCheckStartTime = Integer.valueOf(relationship.getProperty("startTime").toString());
		int relationshipToCheckEndTime = Integer.valueOf(relationship.getProperty("endTime").toString());
		Set<Relationship> overlappingRelationships =  new HashSet<Relationship>();
		
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
				addConflictIn( currEdge, tgtEdge, conflictIn);
			}
			
			if(relationshipOverlap) {
				// if overlap is present then add to result set.
				overlappingRelationships.add(relationshipToCheck);
				
				
//				addConflictIn( currEdge, tgtEdge, conflictIn);
			}
			
		}
		return overlappingRelationships;
	}
	
	
	public int increamentNumSolution(int numSolution)
	{
	    int tmp = numSolution;
	    tmp = tmp + 1;
	    numSolution = tmp;
	    return tmp;
	}
	
	
	// get all edges for a vertex.
	public abstract Set<MyEdge> GetEdgeForVertex(GraphPattern gp, MyNode vertex);
	
	// calculating discoverable period from assigned relationships.
	public abstract ValueRange CalculateDiscoverablePeriod(Collection<Relationship> collection);
	
	// getting relationships for the corresponding edges.
	public abstract Set<Relationship> GetRelevantRelationships(GraphPattern gp, MyEdge edge, MyNode vertex, Node assignedVertex);
	
	// next edge from the map of candidate edges.
	public abstract MyEdge PickNextEdge(GraphPattern gp, Map<MyEdge, Relationship> assignmentEdges, Map<MyEdge, Set<Relationship>> candEdges);
	
	// sorting relationships, based on starting time.
	public abstract Set<Relationship> ValueOrdering(Set<Relationship> relationships);
	
	// finding if both vertices to an edge are assigned or not.
	public abstract boolean BothVerticesNotAssigned(MyEdge edge, Map<MyNode, Node> assignmentVertices);
	
	// assigning the unassigned vertex to an edge.
	public abstract void AssignOtherVertex(MyEdge edge, Relationship relationship, Map<MyNode, Node> assignmentVerticesPrime);
	
	// get edges corresponding to a vertex in the graph pattern.
	public abstract Set<MyEdge> GetRelevantEdges(GraphPattern gp, MyNode vertex);
	
	// getting relationships that overlap with the given relationships.
	public abstract Set<Relationship> GetOverlappingRelationships(Set<Relationship> relationships, Set<Relationship> assignedRelationships);
	
	// getting relationships that overlap with the given relationship.
	public abstract Set<Relationship> GetOverlappingRelationships(Set<Relationship> relationships, Relationship relationship);
	
	// finding relationships common to given two set of relationships.
	public abstract Set<Relationship> FindCommonRelationships(Set<Relationship> candidateRelationships, Set<Relationship> relationships);
	
	// pruning candidate set using the given vertex
	public abstract Set<Relationship> FilterSource(Set<Relationship> candRelationships, Node assignedSource);
	
	// pruning candidate set using the given vertex
	public abstract Set<Relationship> FilterTarget(Set<Relationship> candRelationships, Node assignedTarget);
	
	// whether a partial discoverable time changes when a relationship is assigned.
	public abstract boolean PartialDiscoverableTimeChanges(Set<Relationship> assignedRelationships, Relationship relationship);
}
