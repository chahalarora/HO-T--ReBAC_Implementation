package ca.ucalgary.ispia.graphpatterns.gpchecker.opt;
//ZAIN: TAKE A LOOK HERE!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
import java.time.temporal.ValueRange;
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
import org.neo4j.graphdb.Transaction;

import ca.ucalgary.ispia.graphpatterns.graph.AlgorithmType;
import ca.ucalgary.ispia.graphpatterns.graph.DiscoverablePeriod;
import ca.ucalgary.ispia.graphpatterns.graph.GraphPattern;
import ca.ucalgary.ispia.graphpatterns.graph.MyDirection;
import ca.ucalgary.ispia.graphpatterns.graph.MyEdge;
import ca.ucalgary.ispia.graphpatterns.graph.MyNode;
import ca.ucalgary.ispia.graphpatterns.util.HelperFunctions;

public abstract class FCLBJTemporalGPChecker {

	private Set<ValueRange> overlapPeriodSet = null;
	
	protected AlgorithmType Algorithm = null;
	
	private final boolean ValueOrdering = false;
	
	private int numSolutions = 0;
	
	private int totalConflicts = 0;
	
	protected int totalCandRelationship = 0;
	
	// initialize algorithm
	public Set<ValueRange> FCLBJTemporalInit (GraphPattern gp, Map<MyNode, Node> info) {
//		System.out.println("Algorithm running: " + Algorithm);
		this.totalCandRelationship = 0;
		this.overlapPeriodSet = new HashSet<ValueRange>();
		Map<MyEdge, List<Relationship>> candEdges = new HashMap<MyEdge, List<Relationship>>();
		
		MyNode vertex = info.keySet().iterator().next();
		Node assignedVertex = info.get(vertex);
		
		for(MyEdge edge : GetEdgeForVertex(gp, vertex)) {
			candEdges.put(edge, GetRelevantRelationships(gp, edge, vertex, assignedVertex) );
		}
		
		Map<MyNode, Node> assignmentVertices = new HashMap<MyNode, Node>();
		
		assignmentVertices.put(vertex, assignedVertex);
		
		FCLBJTemporalRec(gp, candEdges, assignmentVertices, new HashMap<MyEdge, Relationship>(), new HashMap<MyEdge, Set<MyEdge>>(), new HashSet<MyEdge>());
		
//		System.out.println("Total conflicts: " + totalConflicts);
//		System.out.println("Total Cand Relationship: " + this.totalCandRelationship);
//		System.out.println("GP: " + gp);
//		System.out.println("Info: " + info);
		
		Set<ValueRange> resultSet =  new HashSet<ValueRange>(this.overlapPeriodSet);
//		Integer totalCandRelationships = new Integer(this.totalCandRelationship);
//		System.out.println("Result Set: " + resultSet.size());
		
//		if(resultSet.size() == 0) {
//			System.out.println("GP: " + gp);
//			System.out.println("Info: " + info);
//		}
		
		try {
//			assert resultSet.size() != 0;
		
		}catch(Exception e) {
//			System.out.println("Result Set: " + resultSet);
		}
//		System.out.println("Total Cand Relationship: " + totalCandRelationships);
		
		return resultSet;
		
	}
	
	public Set<MyEdge> FCLBJTemporalRec(GraphPattern gp, Map<MyEdge, List<Relationship>> candEdges, Map<MyNode, Node> assignmentVertices, Map<MyEdge, Relationship> assignmentEdges, Map<MyEdge, Set<MyEdge>> conflictsIn, Set<MyEdge> dtpAffected) {
		
		// initializing local variables.
		List<Relationship> candRelationships = new ArrayList<Relationship>();
		Set<MyEdge> conflicts = new HashSet<MyEdge>();
		Set<MyEdge> confOut = new HashSet<MyEdge>();
		MyEdge edge = null;
		boolean deadEnd= true;
		
		// if the size of graph pattern is equal to assignment of vertices and edges.
		if (gp.getNodes().size() == assignmentVertices.keySet().size() && gp.getAllEdges().size() == assignmentEdges.keySet().size() ){
			
			// adding the discoverable time period calculated from assigned edges.
			this.overlapPeriodSet.add(CalculateDiscoverablePeriod(assignmentEdges.values()));
			
			if(Algorithm.equals(AlgorithmType.FC) || Algorithm.equals(AlgorithmType.FC_CBJ)) {
				
				numSolutions++;
//				return null;
				return new HashSet<MyEdge>();
			
			} else if(Algorithm.equals(AlgorithmType.FC_LBJ)) {
				
				return dtpAffected;
			
			} else {
				
				return null;
			}
			
		}
		
		edge = PickNextEdge(candEdges);
		
		
		if(ValueOrdering) {
			candRelationships = ValueOrdering(candEdges.get(edge));		
					
		} else {
			
			candRelationships = candEdges.get(edge);
		}
		
		int bjFlag = numSolutions;
		
		
		for (Relationship relationship: candRelationships) {
			////System.out.println("\nSTART ITERATION");
			////System.out.println("Edge: " + edge);
			////System.out.println("Relationship: " + relationship);
			
			this.totalCandRelationship = this.totalCandRelationship + candRelationships.size();
//			System.out.println(candRelationships.size());
//			//System.out.println(conflictsIn);
//			//System.out.println(edge);
//			if(candRelationships.size() == 13) {
//				//System.out.println("Stop point");
//			}
			
			Map<MyEdge, List<Relationship>> candEdgesPrime = new HashMap<MyEdge, List<Relationship>>(); 
			Set<MyEdge> dtpAffectedPrime = new HashSet<MyEdge>();
			Map<MyNode, Node> assignmentVerticesPrime = new HashMap<MyNode, Node>(); 
			Map<MyEdge, Relationship> assignmentEdgesPrime = new HashMap<MyEdge, Relationship>(); 
			Map<MyEdge, Set<MyEdge>> conflictsInPrime = new HashMap<MyEdge, Set<MyEdge>>();
//			MyNode vertex = null;
			boolean validVertex = true;
			boolean validEdge = false;
			
//			candEdgesPrime.putAll(candEdges);
			for(Map.Entry<MyEdge, List<Relationship>> entry : candEdges.entrySet()) {
				List<Relationship> relationshipPrime = new ArrayList<Relationship>();
				
				relationshipPrime.addAll(entry.getValue());
				
				candEdgesPrime.put(entry.getKey(), relationshipPrime);
			}
			
			candEdgesPrime.remove(edge);
			
//			assignmentEdgesPrime.putAll(assignmentEdges);	
			for(Map.Entry<MyEdge, Relationship> entry : assignmentEdges.entrySet()) {
				
				assignmentEdgesPrime.put(entry.getKey(), entry.getValue());
			}
			
			assignmentEdgesPrime.put(edge, relationship);
			
//			assignmentVerticesPrime.putAll(assignmentVertices);
			for(Map.Entry<MyNode, Node> entry : assignmentVertices.entrySet()) {
				
				assignmentVerticesPrime.put(entry.getKey(), entry.getValue());
			}
			
//			//System.out.println("Assn vertices: "+ assignmentVerticesPrime);
//			//System.out.println("Assn edges: "+ assignmentEdgesPrime);
			
			
			if(Algorithm.equals(AlgorithmType.FC_CBJ)) {
				
				for(Map.Entry<MyEdge, Set<MyEdge>> entry : conflictsIn.entrySet()) {
					
					Set<MyEdge> edgePrime = new HashSet<MyEdge>(entry.getValue());
					
//					edgePrime.addAll(entry.getValue());
					
					conflictsInPrime.put(entry.getKey(), edgePrime);
				}
								
			} else if(Algorithm.equals(AlgorithmType.FC_LBJ)) {
				
				deadEnd = true;
//				conflictsInPrime.putAll(conflictsIn);
				
				for(Map.Entry<MyEdge, Set<MyEdge>> entry : conflictsIn.entrySet()) {
					
					Set<MyEdge> edgePrime = new HashSet<MyEdge>();
					
					edgePrime.addAll(entry.getValue());
					
					conflictsInPrime.put(entry.getKey(), edgePrime);
				}

//				dtpAffectedPrime.addAll(dtpAffected);
						
				for(MyEdge dtpAffectedEdge : dtpAffected) {
					
					dtpAffectedPrime.add(dtpAffectedEdge);
				}
				
				if (PartialDiscoverableTimeChanges(new ArrayList<Relationship>(assignmentEdges.values()), relationship)) {
					dtpAffectedPrime.add(edge);
				}
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
			
			////System.out.println("Candidate for edges:");
			for (MyEdge e : candEdgesPrime.keySet()){
				////System.out.println(e + ": " + candEdgesPrime.get(e));
			}
			
			////System.out.println("ConfOut: " + confOut);
			////System.out.println("ConfIn:" );
			for (MyEdge e : conflictsInPrime.keySet()){
				////System.out.println(e + ": " + conflictsInPrime.get(e));
			}
			
			if (validEdge) {
				
				if(Algorithm.equals(AlgorithmType.FC)) {		
					
					FCLBJTemporalRec(gp, candEdgesPrime, assignmentVerticesPrime, assignmentEdgesPrime, conflictsInPrime, dtpAffectedPrime);
					
					
				} else if (Algorithm.equals(AlgorithmType.FC_CBJ)) {
					
					deadEnd = false;
					
					Set<MyEdge> allEdges =  FCLBJTemporalRec(gp, candEdgesPrime, assignmentVerticesPrime, assignmentEdgesPrime, conflictsInPrime, dtpAffectedPrime);
					
//					//System.out.println("All edges: "+ allEdges);
					
					if(allEdges != null) {
						
						if(!(allEdges.size() == 0) && !allEdges.contains(edge)) {
							//System.out.println("\nBack Jumping from " + edge + " to: "  + allEdges + "\n");
							return allEdges;
						
						} else {
							
//							ArrayList<MyEdge> edgesToCopy = new ArrayList<MyEdge>(allEdges);
//							conflicts.addAll(edgesToCopy);
							
//							ArrayList<MyEdge> edgesToCopy = new ArrayList<MyEdge>(allEdges);
							//System.out.println("Updating Conflicts: " + conflicts + ", " + allEdges);
							conflicts.addAll(allEdges);
						}
					} else {
						//System.out.println("Error!!!");
					}
					
				} else if(Algorithm.equals(AlgorithmType.FC_LBJ)) {
					
					deadEnd = false;
					
					Set<MyEdge> allEdges =  FCLBJTemporalRec(gp, candEdgesPrime, assignmentVerticesPrime, assignmentEdgesPrime, conflictsInPrime, dtpAffectedPrime);
					
					if(allEdges != null) {
						
						if(!allEdges.contains(edge)) {
							
							return allEdges;
						
						} else {
							
//							for(MyEdge singleEdge: allEdges) {
//								conflicts.add(singleEdge);
//							}
							
							conflicts.addAll(allEdges);
							
						}
					}
				}
			}
		}
		
		if(Algorithm.equals(AlgorithmType.FC)) {
			
			return null;
			
		} else if (Algorithm.equals(AlgorithmType.FC_CBJ)){
			totalConflicts = totalConflicts + conflicts.size();
			
			if(deadEnd || bjFlag == numSolutions) {
				
//				Set<MyEdge> conlictEdges = conflictsIn.get(edge);
//				
//				if(conlictEdges == null) {
//					// NOTE: figure out why conflictEdgesPrime is null and what to do when it is null.		
//					return null;
//				} else {
//					conflicts.addAll(conflictsIn.get(edge));
//				}
//				
//				return conflicts;
			
				Set<MyEdge> conflictsToAdd = new HashSet<MyEdge>(deadEndJump(edge, confOut, conflictsIn));	
				conflicts.addAll(conflictsToAdd);
				
//				//System.out.println(conflicts);
				//System.out.println("Returning: " + conflicts);
				return conflicts;
				
			} else {
				//System.out.println("Returning Empty");
				return new HashSet<MyEdge>();
			}

			
		} else if (Algorithm.equals(AlgorithmType.FC_LBJ)) {
			
			if (deadEnd ) {
				
//				Set<MyEdge> conlictEdges = conflictsIn.get(edge);
//				
//				if(conlictEdges == null) {
//					// NOTE: figure out why conflictEdgesPrime is null and what to do when it is null.		
//					
//				} else {
//					conflicts.addAll(conflictsIn.get(edge));
//				}
				
				
				Set<MyEdge> conflictsToAdd = new HashSet<MyEdge>(deadEndJump(edge, confOut, conflictsIn));	
				conflicts.addAll(conflictsToAdd);
				
//				//System.out.println(conflicts);
				//System.out.println("Returning: " + conflicts);
				return conflicts;
				
			} else {
				
//				Set<MyEdge> conlictEdges = conflictsIn.get(edge);
//				
//				if(conlictEdges == null) {		
//			
//				} else {
//					conflicts.addAll(conflictsIn.get(edge));
//				}
				
				Set<MyEdge> conflictsToAdd = new HashSet<MyEdge>(deadEndJump(edge, confOut, conflictsIn));	
				conflicts.addAll(conflictsToAdd);
				
				if (dtpAffected.size() != 0) {
					
					for (MyEdge edgePrime : dtpAffected) {
						
//						Set<MyEdge> conlictEdgesPrime = conflictsIn.get(edgePrime);
//						
//						if(conlictEdgesPrime == null) {
//							// NOTE: figure out why conflictEdgesPrime is null and what to do when it is null.				
//					
//						} else {
//							conflicts.addAll(conflictsIn.get(edgePrime));
//						}
						
						Set<MyEdge> conflictsToAddPrime = new HashSet<MyEdge>(deadEndJump(edgePrime, confOut, conflictsIn));	
						conflicts.addAll(conflictsToAddPrime);
					}
				}
			}
				
			return conflicts;
		
		} else {
			
			return null;
		}	
	}
	
		
	public boolean FCV(	GraphPattern gp, 
						Map<MyEdge, List<Relationship>> candEdges, 
						Map<MyEdge, Relationship> assignmentEdges, 
						Map<MyNode, Node> assignmentVertices, 
						MyNode vertex, 
						MyEdge edge,  
						Map<MyEdge, Set<MyEdge>> conflictIn, 
						Set<MyEdge> confOut) {
		
		Set<MyEdge> edges = new HashSet<MyEdge>();
		List<Relationship> relationships = new ArrayList<Relationship>();
		int sizeCandEdges = 0;
		int newSizeCandEdges = 0;
		
		edges = GetRelevantEdges(gp, vertex);
		
		for (MyEdge currentEdge : edges) {
			
			if (!(assignmentEdges.keySet().contains(currentEdge))) {
				
				relationships = GetRelevantRelationships(gp, currentEdge, vertex, assignmentVertices.get(vertex));
				
//				relationships = GetOverlappingRelationships(relationships, new ArrayList<Relationship>(assignmentEdges.values()));
				relationships = timeOverlap(relationships, assignmentEdges, currentEdge, conflictIn);
				
				List<Relationship> currentEdgeRelationships = candEdges.get(currentEdge);
				
//				if(currentEdgeRelationships == null || currentEdgeRelationships.size() == 0) {
				if(currentEdgeRelationships == null) {
					
					candEdges.put(currentEdge, relationships);
					
					if(Algorithm.equals(AlgorithmType.FC_CBJ) || Algorithm.equals(AlgorithmType.FC_LBJ)) {	
							addConflictIn(edge, currentEdge, conflictIn);
					}
					
				} else {
					
					sizeCandEdges = candEdges.get(currentEdge).size();
					
					//NOTE: Testing this part.
//					candEdges.put(currentEdge, relationships);
					
					candEdges.put(currentEdge, FindCommonRelationships(candEdges.get(currentEdge), relationships) );
					
					
					if(Algorithm.equals(AlgorithmType.FC_CBJ) || Algorithm.equals(AlgorithmType.FC_LBJ)) {
						newSizeCandEdges = candEdges.get(currentEdge).size();
						
						if(sizeCandEdges != newSizeCandEdges) {
							addConflictIn(edge, currentEdge, conflictIn);
						}
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
						Map<MyEdge, List<Relationship>> candEdges, 
						Map<MyEdge, Relationship> assignmentEdges, 
						MyEdge edge, 
						Relationship relationship,  
						Map<MyEdge, Set<MyEdge>> conflictIn, 
						Set<MyEdge> confOut) {
		
		int sizeCandEdges = 0;
		int newSizeCandEdges = 0;
			
		for (MyEdge currentEdge: candEdges.keySet()) {
			
			sizeCandEdges = candEdges.get(currentEdge).size();
			
//			candEdges.put(currentEdge, GetOverlappingRelationships(candEdges.get(currentEdge), relationship));
			candEdges.put(currentEdge, timeOverrlapRelationships(candEdges.get(currentEdge), relationship, currentEdge , edge, conflictIn));
			
			boolean filteringSourceTarget = false;
			
			if (currentEdge.getSource().equals(edge.getSource()) ) {
				candEdges.put(currentEdge, FilterSource(candEdges.get(currentEdge), assignmentEdges.get(edge).getStartNode()));
				filteringSourceTarget = true;
			}
			
			if (currentEdge.getTarget().equals(edge.getTarget())) {
				candEdges.put(currentEdge, FilterTarget(candEdges.get(currentEdge), assignmentEdges.get(edge).getEndNode()));
				filteringSourceTarget = true;
			}
			
			if (currentEdge.getSource().equals(edge.getTarget())) {
				candEdges.put(currentEdge, FilterSource(candEdges.get(currentEdge), assignmentEdges.get(edge).getEndNode()));
				filteringSourceTarget = true;
			}
			
			if (currentEdge.getTarget().equals(edge.getSource()) ) {
				candEdges.put(currentEdge, FilterTarget(candEdges.get(currentEdge), assignmentEdges.get(edge).getStartNode()));
				filteringSourceTarget = true;
			}
			
			
			newSizeCandEdges = candEdges.get(currentEdge).size();
			
			if(newSizeCandEdges == 0) {
				confOut.add(currentEdge);
				return false;
			}
			
			if(Algorithm.equals(AlgorithmType.FC_CBJ) || Algorithm.equals(AlgorithmType.FC_LBJ)) {
				
				if(newSizeCandEdges != sizeCandEdges) {
					
					Set<MyEdge> conflictCurrentEdges = conflictIn.get(currentEdge);
					if(conflictCurrentEdges == null) {
						Set<MyEdge> edgeSet = new HashSet<MyEdge>();
						edgeSet.add(edge);
//						conflictIn.put(currentEdge, edgeSet);
						addConflictIn(edge, currentEdge, conflictIn);
						
					} else {
						//conflictCurrentEdges.add(edge);
//						conflictIn.put(currentEdge, conflictCurrentEdges);
						addConflictIn(edge, currentEdge, conflictIn);
						
					}
				}
			}
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
				
				ArrayList confOutEdgesToCopy = new ArrayList(confIn.get(confOutEdge));
				
				jumpVars.addAll(confOutEdgesToCopy);
			}
		}

		//Backjump to the nodes that filtered the candidate set for the current node.
		//Perhaps this will allows us to retain a different set of canddiates taht would work.
		if (confIn.containsKey(edge)){
			ArrayList edgesToCopy = new ArrayList(confIn.get(edge));
			jumpVars.addAll(edgesToCopy);
		}


		return jumpVars;
		
	}
	
	private void addConflictIn(MyEdge src, MyEdge tgt, Map<MyEdge, Set<MyEdge>> confIn){

		if (confIn.containsKey(tgt)){
			//If the target already has incoming conflicts, then add the src to the conflict in set.
			confIn.get(tgt).add(src);
		} else {
			//If the target doesn't have incoming conflicts, then create a new set, add the src, and then 
			//put the key-value pair in the confIn map.
			Set<MyEdge> confList = new HashSet<MyEdge>();
			confList.add(src);
			confIn.put(tgt, confList);
		}

		//Maintain the influence chains.
		//Add the src's confIn conflicts to the tgt's.
		if (confIn.containsKey(src)){
			confIn.get(tgt).addAll(confIn.get(src));
		}
	}
	
	
	private List<Relationship> timeOverlap (List<Relationship> relationships, Map<MyEdge, Relationship> assignedRelationships, MyEdge sourceEdge, Map<MyEdge, Set<MyEdge>> conflictIn){
		List<Relationship> overlappingRelationships =  new ArrayList<Relationship>();
		
		Iterator<Relationship> relationshipsIterator = relationships.iterator();

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
					addConflictIn(sourceEdge, currEdge, conflictIn);
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
	
	
	
	
	public List<Relationship> timeOverrlapRelationships(List<Relationship> relationships, Relationship relationship, MyEdge currEdge, MyEdge tgtEdge, Map<MyEdge, Set<MyEdge>> conflictIn) {
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
//				addConflictIn( tgtEdge, currEdge, conflictIn);
				
				addConflictIn( currEdge, tgtEdge, conflictIn);
			}
			
			if(relationshipOverlap) {
				// if overlap is present then add to result set.
				overlappingRelationships.add(relationshipToCheck);
			}
			
		}
		return overlappingRelationships;
	}
	
	
	
	
	
	
	
	
	
	
	
	// get all edges for a vertex.
	public abstract Set<MyEdge> GetEdgeForVertex(GraphPattern gp, MyNode vertex);
	
	// calculating discoverable period from assigned relationships.
	public abstract ValueRange CalculateDiscoverablePeriod(Collection<Relationship> collection);
	
	// getting relationships for the corresponding edges.
	public abstract List<Relationship> GetRelevantRelationships(GraphPattern gp, MyEdge edge, MyNode vertex, Node assignedVertex);
	
	// next edge from the map of candidate edges.
	public abstract MyEdge PickNextEdge(Map<MyEdge, List<Relationship>> candEdges);
	
	// sorting relationships, based on starting time.
	public abstract List<Relationship> ValueOrdering(List<Relationship> relationships);
	
	// finding if both vertices to an edge are assigned or not.
	public abstract boolean BothVerticesNotAssigned(MyEdge edge, Map<MyNode, Node> assignmentVertices);
	
	// assigning the unassigned vertex to an edge.
	public abstract void AssignOtherVertex(MyEdge edge, Relationship relationship, Map<MyNode, Node> assignmentVerticesPrime);
	
	// get edges corresponding to a vertex in the graph pattern.
	public abstract Set<MyEdge> GetRelevantEdges(GraphPattern gp, MyNode vertex);
	
	// getting relationships that overlap with the given relationships.
	public abstract List<Relationship> GetOverlappingRelationships(List<Relationship> relationships, List<Relationship> assignedRelationships);
	
	// getting relationships that overlap with the given relationship.
	public abstract List<Relationship> GetOverlappingRelationships(List<Relationship> relationships, Relationship relationship);
	
	// finding relationships common to given two set of relationships.
	public abstract List<Relationship> FindCommonRelationships(List<Relationship> candidateRelationships, List<Relationship> relationships);
	
	// pruning candidate set using the given vertex
	public abstract List<Relationship> FilterSource(List<Relationship> candRelationships, Node assignedSource);
	
	// pruning candidate set using the given vertex
	public abstract List<Relationship> FilterTarget(List<Relationship> candRelationships, Node assignedTarget);
	
	// whether a partial discoverable time changes when a relationship is assigned.
	public abstract boolean PartialDiscoverableTimeChanges(List<Relationship> assignedRelationships, Relationship relationship);

}
