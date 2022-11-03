package ca.ucalgary.ispia.graphpatterns.gpchecker.opt;

import java.time.temporal.ValueRange;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
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
import ca.ucalgary.ispia.graphpatterns.tests.Killable;
import edu.stanford.nlp.util.HasInterval;
import edu.stanford.nlp.util.Interval;
import edu.stanford.nlp.util.IntervalTree;

public abstract class FCLBJTemporalGPCheckerWithDbAccess implements Killable {
	
	private IntervalTree  overlapPeriodSet = null;
	
//	private Set<Interval>  overlapPeriodSetDuplicate = null;
	
	protected  AlgorithmType Algorithm = null;
	
	protected boolean ValueOrdering = false;
	
	private GraphDatabaseService graphDb = null;
	
	private int numSolutions = 0;
	
	private int totalCandRelationships = 0;
	
	protected int initialTimePoint = 0;
	
	public List<Node> serachTreeNodesAccessed = new ArrayList<Node>();
	public List<Relationship> serachTreeRelationshipsAccessed = new ArrayList<Relationship>();
	
	public boolean killed;
	
	@Override
	public void kill() {
		// TODO Auto-generated method stub
		this.killed = true;
		
	}
	
	
	// initialize algorithm
	public IntervalTree FCLBJTemporalInit (GraphDatabaseService graphDb, GraphPattern gp, Map<MyNode, Node> info) {
		this.graphDb = graphDb;
		this.overlapPeriodSet = new IntervalTree();
		this.killed = false;
		
		Map<MyEdge, Set<Relationship>> candEdges = new HashMap<MyEdge, Set<Relationship>>();
		
		Map<MyNode, Node> assignmentVertices = new HashMap<MyNode, Node>();
		
		for(MyNode vertex: info.keySet()) {
		
			Node assignedVertex = info.get(vertex);
			
			for(MyEdge edge : GetEdgeForVertex(gp, vertex)) {
				candEdges.put(edge, GetRelevantRelationships(gp, edge, vertex, assignedVertex, this.initialTimePoint));
			}
			
			assignmentVertices.put(vertex, assignedVertex);	
		}
				
		FCLBJTemporalRec(gp, candEdges, assignmentVertices, new HashMap<MyEdge, Relationship>(), new HashMap<MyEdge, Set<MyEdge>>(), new HashSet<MyEdge>());				
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
		boolean deadEnd = true;
		
		// if the size of graph pattern is equal to assignment of vertices and edges.
		if (gp.getNodes().size() == assignmentVertices.keySet().size() && gp.getAllEdges().size() == assignmentEdges.keySet().size() ){
			
			Interval discoverablePeriod = CalculateDiscoverablePeriod(assignmentEdges.values());
			
//			System.out.println("Discoverable period: "+discoverablePeriod);
			
			// adding the discoverable time period calculated from assigned edges.
			this.overlapPeriodSet.addNonNested(discoverablePeriod);
			
			OfficialPeriodFinder officialPeriodFinder = new OfficialPeriodFinder();
			
			if(Algorithm.equals(AlgorithmType.FC) || Algorithm.equals(AlgorithmType.FC_CBJ)) {
				
				numSolutions++;
				
				return new HashSet<MyEdge>();
			
			} else if(Algorithm.equals(AlgorithmType.FC_LBJ)) {
				
				return dtpAffected;
			
			} else if(Algorithm.equals(AlgorithmType.FC_LBJ_Improved)) {
				
				this.overlapPeriodSet = officialPeriodFinder.findOfficialPeriods(this.overlapPeriodSet);
				
				this.overlapPeriodSet.balance();
				
				return dtpAffected;
				
			} else {
				
				return null;
			}
			
		}
		
		edge = PickNextEdge(gp, assignmentEdges, candEdges);
		
		if(ValueOrdering) {
			candRelationships = ValueOrdering(candEdges.get(edge));		
					
		} else {
			
			if(candEdges.get(edge) != null) {
			
				candRelationships = new ArrayList<Relationship>( candEdges.get(edge));
//				candRelationships = candEdges.get(edge);
			}
		}
		
		int bjFlag = numSolutions;
		
//		System.out.println("Candidate edge: "+ edge);
//		System.out.println("Candidate rel: "+ candRelationships);
		
		int relationshipsCheckedSize = 0;
		int candRelationshipsSize = candRelationships.size();
		
//		this.serachTreeRelationshipsAccessed.addAll(candRelationships);
		
		for (Relationship relationship: candRelationships) {
			
			if(killed) {
				break;
			}
			
//			System.out.println("Rel: "+ relationship);
			this.serachTreeRelationshipsAccessed.add(relationship);
			
			boolean isFullyContained = false;
			relationshipsCheckedSize++;
			
			if( Algorithm.equals(AlgorithmType.FC_LBJ_Improved)) {
				// to find whether the interval is contained in the already found official periods.
				IntervalTree officialPeriodSet = new IntervalTree();
				officialPeriodSet = this.overlapPeriodSet;
				
				isFullyContained = ContainedRelationshipCheck(new HashSet<Relationship>(assignmentEdges.values()), relationship, officialPeriodSet);
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
				
//				System.out.println("Current edge: "+ edge);
//				System.out.println("Current relationship: "+ relationship);
//				System.out.println("Current assignments: "+ assignmentEdgesPrime);
//				System.out.println("Candidate sets: "+ candEdgesPrime);
//				System.out.println("Conflicts: "+ conflicts);
//				System.out.println("ConflictsIn: "+ conflictsInPrime);
				
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
						
					} else if(Algorithm.equals(AlgorithmType.FC_LBJ)) {
						
						deadEnd = false;
						
						Set<MyEdge> allEdges =  FCLBJTemporalRec(gp, candEdgesPrime, assignmentVerticesPrime, assignmentEdgesPrime, conflictsInPrime, dtpAffectedPrime);
						
						if(allEdges != null) {
							
							if(!allEdges.contains(edge)) {
								
								return allEdges;
							
							} else {
								
								conflicts.addAll(allEdges);
								
							}
						}
					
					} else if(Algorithm.equals(AlgorithmType.FC_LBJ_Improved)) {
						
						deadEnd = false;
						
						Set<MyEdge> allEdges =  FCLBJTemporalRec(gp, candEdgesPrime, assignmentVerticesPrime, assignmentEdgesPrime, conflictsInPrime, dtpAffectedPrime);
						
						if(allEdges != null) {
							
//							if(!allEdges.contains(edge) && relationshipsCheckedSize == candRelationshipsSize) {
							
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

			
			} 
			
			else {
				// the assigned relationship time period is contained in already found official periods set.				
				if (conflictsIn.containsKey(edge)){
					conflicts.addAll(conflictsIn.get(edge));
				}
				
				deadEnd = false;
//				FCLBJTemporalRec(gp, candEdgesPrime, assignmentVerticesPrime, assignmentEdgesPrime, conflictsInPrime, dtpAffectedPrime);
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
					
					conflicts.addAll(dtpAffected);
					
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
		
		
		//If the search has been killed, return false
//		if (killed){
//			return false;
//		}
		
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
		
		
		//If the search has been killed, return false
//		if (killed){
//			return false;
//		}
			
		for (MyEdge currentEdge: candEdges.keySet()) {
			
			sizeCandEdges = candEdges.get(currentEdge).size();
			
			candEdges.put(currentEdge, GetOverlappingRelationships(candEdges.get(currentEdge), relationship));
			
			Node assnEdgeStartNode =  null;
			Node assnEdgeEndNode = null;
			
			try (Transaction tx = graphDb.beginTx()){
				
				assnEdgeStartNode =  assignmentEdges.get(edge).getStartNode();
				assnEdgeEndNode =  assignmentEdges.get(edge).getEndNode();
				
				this.serachTreeNodesAccessed.add(assnEdgeStartNode);
				this.serachTreeNodesAccessed.add(assnEdgeEndNode);
				
				
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
}
