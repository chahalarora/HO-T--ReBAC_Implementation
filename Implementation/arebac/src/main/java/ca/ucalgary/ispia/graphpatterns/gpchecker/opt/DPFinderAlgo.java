package ca.ucalgary.ispia.graphpatterns.gpchecker.opt;

//import java.time.temporal.ValueRange;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;

import ca.ucalgary.ispia.graphpatterns.gpchecker.opt.impl.OfficialPeriodFinder;
import ca.ucalgary.ispia.graphpatterns.graph.AlgorithmType;
import ca.ucalgary.ispia.graphpatterns.graph.GraphPattern;
import ca.ucalgary.ispia.graphpatterns.graph.MyEdge;
import ca.ucalgary.ispia.graphpatterns.graph.MyNode;
import edu.stanford.nlp.util.Interval;
import edu.stanford.nlp.util.IntervalTree;

public abstract class DPFinderAlgo {

	private IntervalTree  overlapPeriodSet = null;
	
	protected  AlgorithmType Algorithm = null;
	
	protected boolean ValueOrdering = false;
	
	private GraphDatabaseService graphDb = null;
	
	private int numSolutions = 0;
	
	private int totalCandRelationships = 0;
	
	protected int initialTimePoint = 0;
	
	// initialize algorithm
	public IntervalTree FCLBJTemporalInit (GraphDatabaseService graphDb, GraphPattern gp, Map<MyNode, Node> info) {
//		System.out.println(Algorithm);
		this.graphDb = graphDb;
		this.overlapPeriodSet = new IntervalTree ();		
		
		Map<MyEdge, Set<Relationship>> candEdges = new HashMap<MyEdge, Set<Relationship>>();
		
		Map<MyNode, Node> assignmentVertices = new HashMap<MyNode, Node>();
		
		for(MyNode vertex: info.keySet()) {
		
//			MyNode vertex = info.keySet().iterator().next();
			Node assignedVertex = info.get(vertex);
			
			for(MyEdge edge : GetEdgeForVertex(gp, vertex)) {
				candEdges.put(edge, GetRelevantRelationships(gp, edge, vertex, assignedVertex, this.initialTimePoint));
			}
			
			assignmentVertices.put(vertex, assignedVertex);	
		}
		
//		System.out.println("GP: " + gp);
		
		FCLBJTemporalRec(gp, candEdges, assignmentVertices, new HashMap<MyEdge, Relationship>(), new HashMap<MyEdge, Set<MyEdge>>(), new HashSet<MyEdge>());
		
//		return this.overlapPeriodSet;
		
//		IntervalTree resultSet =  new HashSet<ValueRange>(this.overlapPeriodSet);
//		System.out.println("Result Set: " + resultSet.size());
//		System.out.println("GP: " + gp);
//		System.out.println("Info: " + info);
//		try {
//			assert resultSet.size() != 0;
//		
//		}catch(Exception e) {
//			System.out.println("Result Set: " + resultSet);
//		}
//		System.out.println("Total Cand Relationship: " + totalCandRelationships);
		
		System.out.println("overlapPeriodSet: " + overlapPeriodSet.size());
				
		return this.overlapPeriodSet;
		
		
	}
	
	// Recursive phase.
	public Set<MyEdge> FCLBJTemporalRec(GraphPattern gp, Map<MyEdge, Set<Relationship>> candEdges, Map<MyNode, Node> assignmentVertices, Map<MyEdge, Relationship> assignmentEdges, Map<MyEdge, Set<MyEdge>> conflictsIn, Set<MyEdge> dtpAffected) {
		
		// initializing local variables.
//		Set<Relationship> candRelationships = new HashSet<Relationship>();
		
		List<Relationship> candRelationships = new ArrayList<Relationship>();
		
		Set<MyEdge> conflicts = new HashSet<MyEdge>();
		Set<MyEdge> confOut = new HashSet<MyEdge>();
		MyEdge edge = null;
		boolean deadEnd= true;
		Interval overlappedInterval = null;
		
		// if the size of graph pattern is equal to assignment of vertices and edges.
		if (gp.getNodes().size() == assignmentVertices.keySet().size() && gp.getAllEdges().size() == assignmentEdges.keySet().size() ){
			
			Interval discoverablePeriod = overlappedInterval;
			
//			Interval discoverablePeriod = CalculateDiscoverablePeriod(assignmentEdges.values());
			
			// adding the discoverable time period calculated from assigned edges.
//			this.overlapPeriodSet.addNonNested(discoverablePeriod);
			
			OfficialPeriodFinder officialPeriodFinder = new OfficialPeriodFinder();
			
			this.overlapPeriodSet = officialPeriodFinder.findOfficialPeriods(this.overlapPeriodSet, discoverablePeriod);
			
//			UpdateOfficialPeriodToTree(this.officialPeriodTree, this.overlapPeriodSet);
			
			if(Algorithm.equals(AlgorithmType.FC) || Algorithm.equals(AlgorithmType.FC_CBJ)) {
				
				numSolutions++;
				
				return new HashSet<MyEdge>();
			
			} else if(Algorithm.equals(AlgorithmType.FC_LBJ) || Algorithm.equals(AlgorithmType.FC_LBJ_Improved)) {
				
				return dtpAffected;
			
			} else {
				
				return null;
			}
			
		}
		
		edge = PickNextEdge(gp, assignmentEdges, candEdges);
		
		if(ValueOrdering) {
			candRelationships = ValueOrdering(candEdges.get(edge));		
					
		} else {
			
			candRelationships = new ArrayList<Relationship>( candEdges.get(edge));
		}
		
		int bjFlag = numSolutions;
		
		for (Relationship relationship: candRelationships) {
			
			this.totalCandRelationships = this.totalCandRelationships + candRelationships.size();
			
			boolean isFullyContained = false;
			
			overlappedInterval = CalculateOverlappedPeriod(new HashSet<Relationship>(assignmentEdges.values()), relationship);
			
			if( Algorithm.equals(AlgorithmType.FC_LBJ_Improved)) {
				// to find whether the interval is contained in the already found official periods.
				isFullyContained = ContainedRelationshipCheck(new HashSet<Relationship>(assignmentEdges.values()), relationship, this.overlapPeriodSet);	
			}
			
			
			if(!isFullyContained) {	
				
				Map<MyEdge, Set<Relationship>> candEdgesPrime = new HashMap<MyEdge, Set<Relationship>>(); 
				Set<MyEdge> dtpAffectedPrime = new HashSet<MyEdge>();
				Map<MyNode, Node> assignmentVerticesPrime = new HashMap<MyNode, Node>(); 
				Map<MyEdge, Relationship> assignmentEdgesPrime = new HashMap<MyEdge, Relationship>(); 
				Map<MyEdge, Set<MyEdge>> conflictsInPrime = new HashMap<MyEdge, Set<MyEdge>>();
				boolean validVertex = true;
				boolean validEdge = false;
				
				for(Map.Entry<MyEdge, Set<Relationship>> entry : candEdges.entrySet()) {
					Set<Relationship> relationshipPrime = new HashSet<Relationship>();
					
					relationshipPrime.addAll(entry.getValue());
					
					candEdgesPrime.put(entry.getKey(), relationshipPrime);
				}
				
				candEdgesPrime.remove(edge);
				
				for(Map.Entry<MyEdge, Relationship> entry : assignmentEdges.entrySet()) {
					
					assignmentEdgesPrime.put(entry.getKey(), entry.getValue());
				}
				
				assignmentEdgesPrime.put(edge, relationship);
				
				for(Map.Entry<MyNode, Node> entry : assignmentVertices.entrySet()) {
					
					assignmentVerticesPrime.put(entry.getKey(), entry.getValue());
				}
				
				if(Algorithm.equals(AlgorithmType.FC_CBJ)) {
					
					for(Map.Entry<MyEdge, Set<MyEdge>> entry : conflictsIn.entrySet()) {
						
						Set<MyEdge> edgePrime = new HashSet<MyEdge>(entry.getValue());
						
						conflictsInPrime.put(entry.getKey(), edgePrime);
					}
									
				} else if(Algorithm.equals(AlgorithmType.FC_LBJ) || Algorithm.equals(AlgorithmType.FC_LBJ_Improved)) {
					
					deadEnd = true;
					
					for(Map.Entry<MyEdge, Set<MyEdge>> entry : conflictsIn.entrySet()) {
						
						Set<MyEdge> edgePrime = new HashSet<MyEdge>(entry.getValue());
						
						conflictsInPrime.put(entry.getKey(), edgePrime);
					}
					
					dtpAffectedPrime.addAll(new HashSet<>(dtpAffected));
					
					if (PartialDiscoverableTimeChanges(new HashSet<Relationship>(assignmentEdgesPrime.values()), relationship)) {
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
				
				
				if (validEdge) {
					
					if(Algorithm.equals(AlgorithmType.FC)) {		
						
						FCLBJTemporalRec(gp, candEdgesPrime, assignmentVerticesPrime, assignmentEdgesPrime, conflictsInPrime, dtpAffectedPrime);
						
						
					} else if (Algorithm.equals(AlgorithmType.FC_CBJ)) {
						
						deadEnd = false;
						
						Set<MyEdge> allEdges =  FCLBJTemporalRec(gp, candEdgesPrime, assignmentVerticesPrime, assignmentEdgesPrime, conflictsInPrime, dtpAffectedPrime);
						
						
						if(allEdges != null) {
							
							if(!(allEdges.size() == 0) && !allEdges.contains(edge)) {
								return allEdges;
							
							} else {
								conflicts.addAll(allEdges);
							}
						} else {
							//System.out.println("Error!!!");
						}
						
					} else if(Algorithm.equals(AlgorithmType.FC_LBJ) || Algorithm.equals(AlgorithmType.FC_LBJ_Improved)) {
						
						deadEnd = false;
						
						Set<MyEdge> allEdges =  FCLBJTemporalRec(gp, candEdgesPrime, assignmentVerticesPrime, assignmentEdgesPrime, conflictsInPrime, dtpAffectedPrime);
						
						if(allEdges != null) {
							
							if(!allEdges.contains(edge)) {
								
								return allEdges;
							
							} else {
								
								conflicts.addAll(allEdges);
								
							}
						}
					
					}
					
				} else {
					
					// adding conflicts after deadend.
					conflicts.addAll(deadEndJump(edge, confOut, conflictsInPrime));
				}

			
			} else {
				
				// the assigned relationship time period is contained in already found offical periods set.
				if (conflictsIn.containsKey(edge)){
					conflicts.addAll(conflictsIn.get(edge));
				}
			}
		}
		
		if(Algorithm.equals(AlgorithmType.FC)) {
			
			return null;
			
		} else if (Algorithm.equals(AlgorithmType.FC_CBJ)){
			
			if(deadEnd || bjFlag == numSolutions) {
				
				Set<MyEdge> conflictsToAdd = new HashSet<MyEdge>(deadEndJump(edge, confOut, conflictsIn));	
				conflicts.addAll(conflictsToAdd);
				return conflicts;
				
			} else {
				
				return new HashSet<MyEdge>();
			}

			
		} else if (Algorithm.equals(AlgorithmType.FC_LBJ) || Algorithm.equals(AlgorithmType.FC_LBJ_Improved)) {
			
			if (deadEnd) {
								
				Set<MyEdge> conflictsToAdd = new HashSet<MyEdge>(deadEndJump(edge, confOut, conflictsIn));	
				conflicts.addAll(conflictsToAdd);

				
			} else {
				
				Set<MyEdge> conlictEdges = conflictsIn.get(edge);
				
				if(conlictEdges == null) {		
			
				} else {
					conflicts.addAll(conflictsIn.get(edge));
				}
				
				Set<MyEdge> conflictsToAdd = new HashSet<MyEdge>(deadEndJump(edge, confOut, conflictsIn));	
				conflicts.addAll(conflictsToAdd);
				
				if (dtpAffected.size() != 0) {
					
					for (MyEdge edgePrime : dtpAffected) {
												
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
				
				relationships = GetRelevantRelationships(gp, currentEdge, vertex, assignmentVertices.get(vertex), this.initialTimePoint);
				
				relationships = GetTimeOverlapRelationships(relationships, assignmentEdges, currentEdge, conflictIn);
				
				Set<Relationship> currentEdgeRelationships = candEdges.get(currentEdge);
				
				if(currentEdgeRelationships == null) {
					
					candEdges.put(currentEdge, relationships);
					
					if(Algorithm.equals(AlgorithmType.FC_CBJ) || Algorithm.equals(AlgorithmType.FC_LBJ ) || Algorithm.equals(AlgorithmType.FC_LBJ_Improved)) {	
							addConflictIn(edge, currentEdge, conflictIn);
					}
					
				} else {
					
					sizeCandEdges = candEdges.get(currentEdge).size();
					
					candEdges.put(currentEdge, FindCommonRelationships(candEdges.get(currentEdge), relationships) );
					
					if(Algorithm.equals(AlgorithmType.FC_CBJ) || Algorithm.equals(AlgorithmType.FC_LBJ) || Algorithm.equals(AlgorithmType.FC_LBJ_Improved)) {
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
						Map<MyEdge, Set<Relationship>> candEdges, 
						Map<MyEdge, Relationship> assignmentEdges, 
						MyEdge edge, 
						Relationship relationship,  
						Map<MyEdge, Set<MyEdge>> conflictIn, 
						Set<MyEdge> confOut) {
		
		int sizeCandEdges = 0;
		int newSizeCandEdges = 0;
			
		for (MyEdge currentEdge: candEdges.keySet()) {
			
			sizeCandEdges = candEdges.get(currentEdge).size();
			
			candEdges.put(currentEdge, GetOverlappingRelationships(candEdges.get(currentEdge), relationship));
			
			Node assnEdgeStartNode =  null;
			Node assnEdgeEndNode = null;
			
			try (Transaction tx = graphDb.beginTx()){
				
				assnEdgeStartNode =  assignmentEdges.get(edge).getStartNode();
				assnEdgeEndNode =  assignmentEdges.get(edge).getEndNode();
				
				if (currentEdge.getSource().equals(edge.getSource()) ) {
					candEdges.put(currentEdge, FilterSource(candEdges.get(currentEdge), assnEdgeStartNode));
				}
				
				if (currentEdge.getTarget().equals(edge.getTarget())) {
					candEdges.put(currentEdge, FilterTarget(candEdges.get(currentEdge), assnEdgeEndNode));
				}
				
				if (currentEdge.getSource().equals(edge.getTarget())) {
					candEdges.put(currentEdge, FilterSource(candEdges.get(currentEdge), assnEdgeEndNode));
				}
				
				if (currentEdge.getTarget().equals(edge.getSource()) ) {
					candEdges.put(currentEdge, FilterTarget(candEdges.get(currentEdge), assnEdgeStartNode));
				}
			
				tx.success();
			}	
				
			
			newSizeCandEdges = candEdges.get(currentEdge).size();
			
			if(newSizeCandEdges == 0) {
				confOut.add(currentEdge);
				return false;
			}
			
			if(Algorithm.equals(AlgorithmType.FC_CBJ) || Algorithm.equals(AlgorithmType.FC_LBJ) || Algorithm.equals(AlgorithmType.FC_LBJ_Improved)) {
				
				if(newSizeCandEdges != sizeCandEdges) {
					
					Set<MyEdge> conflictCurrentEdges = conflictIn.get(currentEdge);
					if(conflictCurrentEdges == null) {
						Set<MyEdge> edgeSet = new HashSet<MyEdge>();
						edgeSet.add(edge);
						addConflictIn(edge, currentEdge, conflictIn);
						
					} else {
						addConflictIn(edge, currentEdge, conflictIn);
					}
				}
			}
		}
		
		return true;
	}
	
	
	// get all edges for a vertex.
	public abstract Set<MyEdge> GetEdgeForVertex(GraphPattern gp, MyNode vertex);
	
	// calculating discoverable period from assigned relationships.
	public abstract Interval CalculateDiscoverablePeriod(Collection<Relationship> collection);
	
	// getting relationships for the corresponding info edges.
//	public abstract Set<Relationship> GetInfoRelevantRelationships(GraphPattern gp, MyEdge edge, MyNode vertex, Node assignedVertex, int intialTimePoint);
	
	// getting relationships for the corresponding edges.
	public abstract Set<Relationship> GetRelevantRelationships(GraphPattern gp, MyEdge edge, MyNode vertex, Node assignedVertex, int initialTimePoint);
	
	// next edge from the map of candidate edges.
	public abstract MyEdge PickNextEdge(GraphPattern gp, Map<MyEdge, Relationship> assignmentEdges, Map<MyEdge, Set<Relationship>> candEdges);
	
	// sorting relationships, based on starting time.
	public abstract List<Relationship> ValueOrdering(Set<Relationship> relationships);
	
	// finding if both vertices to an edge are assigned or not.
	public abstract boolean BothVerticesNotAssigned(MyEdge edge, Map<MyNode, Node> assignmentVertices);
	
	// assigning the unassigned vertex to an edge.
	public abstract void AssignOtherVertex(MyEdge edge, Relationship relationship, Map<MyNode, Node> assignmentVerticesPrime);
	
	// get edges corresponding to a vertex in the graph pattern.
	public abstract Set<MyEdge> GetRelevantEdges(GraphPattern gp, MyNode vertex);
	
	// getting relationships that overlap with the given relationships.
	public abstract Set<Relationship> GetTimeOverlapRelationships(Set<Relationship> relationships, Map<MyEdge, Relationship> assignedRelationships, MyEdge sourceEdge, Map<MyEdge, Set<MyEdge>> conflictIn);
	
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
	
	// adding incoming conflict.
	public abstract void addConflictIn(MyEdge src, MyEdge tgt, Map<MyEdge, Set<MyEdge>> confIn);
	
	// adding conflict after deadend.
	public abstract Set<MyEdge> deadEndJump(MyEdge edge, Set<MyEdge> confOut, Map<MyEdge, Set<MyEdge>> confIn);
	
	// finding whether relationship is already contained in already discovered period.
	public abstract boolean ContainedRelationshipCheck(Set<Relationship> assignedRelationships, Relationship relationship, IntervalTree discoveredPeriods);
	
	// finding whether relationship is already contained in already discovered period.
	public abstract boolean ContainedRelationshipCheck(Interval overlappedInterval, IntervalTree discoveredPeriods);

	// finding whether relationship is already contained in already discovered period.
	public abstract Interval CalculateOverlappedPeriod(Set<Relationship> assignedRelationships, Relationship relationship);
	
	// add official period to interval tree.
//	public abstract void UpdateOfficialPeriodToTree(IntervalTree intervalTree, Set<ValueRange> overlapPeriodSet);
	
}
