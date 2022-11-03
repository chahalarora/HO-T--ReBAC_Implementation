package ca.ucalgary.ispia.graphpatterns.gpchecker.opt;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;

import ca.ucalgary.ispia.graphpatterns.gpchecker.GPChecker;
import ca.ucalgary.ispia.graphpatterns.graph.AlgorithmType;
import ca.ucalgary.ispia.graphpatterns.graph.DiscoverablePeriod;
import ca.ucalgary.ispia.graphpatterns.graph.GPHolder;
import ca.ucalgary.ispia.graphpatterns.graph.GraphPattern;
import ca.ucalgary.ispia.graphpatterns.graph.MyDirection;
import ca.ucalgary.ispia.graphpatterns.graph.MyEdge;
import ca.ucalgary.ispia.graphpatterns.graph.MyNode;
import ca.ucalgary.ispia.graphpatterns.graph.MyRelationship;
import ca.ucalgary.ispia.graphpatterns.tests.Killable;
import ca.ucalgary.ispia.graphpatterns.util.HelperFunctions;
import scala.Int;

public class GPHGCheckerLBJFC<N, E> implements GPChecker<N, E>, Killable {

	private Set<DiscoverablePeriod> discoverablePeriodSet = null;
	
	private static final AlgorithmType algorithm = AlgorithmType.FC;
	
	private static final boolean ValueOrdering = false;
	
	// initialize algorithm
	public Set<DiscoverablePeriod> discoverablePeriodInit (GraphPattern gp, Map<MyNode, Node> info) {
		this.discoverablePeriodSet = new HashSet<DiscoverablePeriod>();
		Map<MyEdge, List<Relationship>> candEdges = new HashMap<MyEdge, List<Relationship>>();
		
		MyNode vertex = info.keySet().iterator().next();
		Node assignedVertex = info.get(vertex);
		
		for(MyEdge edge : HelperFunctions.getEdgeForVertex(gp, vertex)) {
			candEdges.put(edge, HelperFunctions.getRelevantRelationships(gp, edge, vertex, assignedVertex) );
		}
		
		Map<MyNode, Node> assignmentVertices = new HashMap<MyNode, Node>();
		
		assignmentVertices.put(vertex, assignedVertex);
		
		discoverablePeriodRec(gp, candEdges, assignmentVertices, new HashMap<MyEdge, Relationship>(), new HashMap<MyEdge, Set<MyEdge>>(), new HashSet<MyEdge>());
		
		return this.discoverablePeriodSet;
		
	}
	
	public Set<MyEdge> discoverablePeriodRec(GraphPattern gp, Map<MyEdge, List<Relationship>> candEdges, Map<MyNode, Node> assignmentVertices, Map<MyEdge, Relationship> assignmentEdges, Map<MyEdge, Set<MyEdge>> conflictsIn, Set<MyEdge> dtpAffected) {
		
		// initializing local variables.
		List<Relationship> candRelationships = new ArrayList<Relationship>();
		Set<MyEdge> conflicts = new HashSet<MyEdge>();
		MyEdge edge = null;
		boolean deadEnd= true;
		
		// if the size of graph pattern is equal to assignment of vertices and edges.
		if (gp.getNodes().size() == assignmentVertices.keySet().size() && gp.getAllEdges().size() == assignmentEdges.keySet().size() ){
			
			// adding the discoverable time period calculated from assigned edges.
			discoverablePeriodSet.add(HelperFunctions.calculateDiscoverablePeriod(assignmentEdges.values()));
			
			if(algorithm.equals(AlgorithmType.FC) || algorithm.equals(AlgorithmType.FC_CBJ)) {
				
				return null;
			
			} else if(algorithm.equals(AlgorithmType.FC_LBJ)) {
				
				return dtpAffected;
			
			} else {
				
				return null;
			}
			
		}
		
		edge = HelperFunctions.pickNextEdge(candEdges);
		
		if(ValueOrdering) {
			candRelationships = HelperFunctions.valueOrdering(candEdges.get(edge));		
					
		} else {
			
			candRelationships = candEdges.get(edge);
		}
		
		for (Relationship relationship: candRelationships) {
			
			Map<MyEdge, List<Relationship>> candEdgesPrime = new HashMap<MyEdge, List<Relationship>>(); 
			Set<MyEdge> dtpAffectedPrime = new HashSet<MyEdge>();
			Map<MyNode, Node> assignmentVerticesPrime = new HashMap<MyNode, Node>(); 
			Map<MyEdge, Relationship> assignmentEdgesPrime = new HashMap<MyEdge, Relationship>(); 
			Map<MyEdge, Set<MyEdge>> conflictsInPrime = new HashMap<MyEdge, Set<MyEdge>>();
//			MyNode vertex = null;
			boolean validVertex = true;
			boolean validEdge = false;
			
			candEdgesPrime.putAll(candEdges);
			candEdgesPrime.remove(edge);
			
			assignmentEdgesPrime.putAll(assignmentEdges);			
			assignmentEdgesPrime.put(edge, relationship);
			
			assignmentVerticesPrime.putAll(assignmentVertices);
			
			if(algorithm.equals(AlgorithmType.FC_LBJ)) {
				
				conflictsInPrime.putAll(conflictsIn);
				
			} else if(algorithm.equals(AlgorithmType.FC_LBJ)) {
				
				deadEnd = true;
				conflictsInPrime.putAll(conflictsIn);
				dtpAffectedPrime.addAll(dtpAffected);
				
				if (HelperFunctions.partialDiscoverableTimeChanges(new ArrayList<Relationship>(assignmentEdges.values()), relationship)) {
					dtpAffectedPrime.add(edge);
				}
			}
			
			if (HelperFunctions.bothVerticesNotAssigned(edge, assignmentVertices)) {
				
				// to check whether source was assigned to the edge or the target.
				MyNode v2 = null;
				if (assignmentVerticesPrime.containsKey(edge.getSource())) {
					v2 = edge.getTarget();
				} else {
					v2 = edge.getSource();
				}
			
//				boolean sourceAssigned = false;
//				
//				if (edge.getSource() == null) {
//					sourceAssigned = false;
//				} else {
//					sourceAssigned = true;
//				}
				
				HelperFunctions.assignOtherVertex(edge, relationship, assignmentVerticesPrime);
				
				// if source was assigned then initialize vertex with edge target and vice versa.
//				if (sourceAssigned) {
//					vertex = edge.getTarget();
//				} else {
//					vertex = edge.getSource();
//				}
				
				validVertex = FCV(gp, candEdgesPrime, assignmentEdgesPrime, assignmentVerticesPrime, v2, edge, conflictsInPrime);
			}
			
			if (validVertex) {
				validEdge = FCE(gp, candEdgesPrime, assignmentEdgesPrime, edge, relationship, conflictsInPrime); 
			}
			
			
			if (validEdge) {
				
				if(algorithm.equals(AlgorithmType.FC)) {		
					
					discoverablePeriodRec(gp, candEdgesPrime, assignmentVerticesPrime, assignmentEdgesPrime, conflictsInPrime, dtpAffectedPrime);
					
				} else if (algorithm.equals(AlgorithmType.FC_LBJ)) {
					
					Set<MyEdge> allEdges =  discoverablePeriodRec(gp, candEdgesPrime, assignmentVerticesPrime, assignmentEdgesPrime, conflictsInPrime, dtpAffectedPrime);
					
					if(!allEdges.contains(edge)) {
						return allEdges;
					
					} else {
						conflicts.addAll(allEdges);
					}
					
				} else if(algorithm.equals(AlgorithmType.FC_LBJ)) {
					
					deadEnd = false;
					
					Set<MyEdge> allEdges =  discoverablePeriodRec(gp, candEdgesPrime, assignmentVerticesPrime, assignmentEdgesPrime, conflictsInPrime, dtpAffectedPrime);
					
					if(!allEdges.contains(edge)) {
						return allEdges;
					
					} else {
						conflicts.addAll(allEdges);
					}
				}
			}
		}
		
		if(algorithm.equals(AlgorithmType.FC)) {
			
			return null;
			
		} else if (algorithm.equals(AlgorithmType.FC_CBJ)){
			
			Set<MyEdge> conlictEdges = conflictsIn.get(edge);
			
			if(conlictEdges == null) {
				// NOTE: figure out why conflictEdgesPrime is null and what to do when it is null.		
		
			} else {
				conflicts.addAll(conflictsIn.get(edge));
			}
			
			return conflicts;
			
		} else if (algorithm.equals(AlgorithmType.FC_LBJ)) {
			
			if (deadEnd) {
				
				Set<MyEdge> conlictEdges = conflictsIn.get(edge);
				
				if(conlictEdges == null) {
					// NOTE: figure out why conflictEdgesPrime is null and what to do when it is null.		
			
				} else {
					conflicts.addAll(conflictsIn.get(edge));
				}
				
			} else {
				
				Set<MyEdge> conlictEdges = conflictsIn.get(edge);
				
				if(conlictEdges == null) {		
			
				} else {
					conflicts.addAll(conflictsIn.get(edge));
				}
				
				if (dtpAffected.size() != 0) {
					
					for (MyEdge edgePrime : dtpAffected) {
						
						Set<MyEdge> conlictEdgesPrime = conflictsIn.get(edgePrime);
						
						if(conlictEdgesPrime == null) {
							// NOTE: figure out why conflictEdgesPrime is null and what to do when it is null.				
					
						} else {
							conflicts.addAll(conflictsIn.get(conlictEdgesPrime));
						}						
					}
				}
			}
				
			return conflicts;
		
		} else {
			
			return null;
		}	
	}
	
		
	public boolean FCV(GraphPattern gp, Map<MyEdge, List<Relationship>> candEdges, Map<MyEdge, Relationship> assignmentEdges, Map<MyNode, Node> assignmentVertices, MyNode vertex, MyEdge edge,  Map<MyEdge, Set<MyEdge>> conflictIn) {
		Set<MyEdge> edges = new HashSet<MyEdge>();
		List<Relationship> relationships = new ArrayList<Relationship>();
		int sizeCandEdges = 0;
		int newSizeCandEdges = 0;
		
		edges = HelperFunctions.getRelevantEdges(gp, vertex);
		
		for (MyEdge currentEdge : edges) {
			
			if (!(assignmentEdges.keySet().contains(currentEdge))) {
				
				relationships = HelperFunctions.getRelevantRelationships(gp, currentEdge, vertex, assignmentVertices.get(vertex));
				
				relationships = HelperFunctions.getOverlappingRelationships(relationships, new ArrayList<Relationship>(assignmentEdges.values()));
				
				List<Relationship> currentEdgeRelationships = candEdges.get(currentEdge);
				
				if(currentEdgeRelationships == null || currentEdgeRelationships.size() == 0) {
					
					candEdges.put(currentEdge, relationships);
					
					if(algorithm.equals(AlgorithmType.FC_CBJ) || algorithm.equals(AlgorithmType.FC_LBJ)) {	
						Set<MyEdge> conflictCurrentEdges = conflictIn.get(currentEdge);
						if(conflictCurrentEdges == null) {
							Set<MyEdge> edgeSet = new HashSet<MyEdge>();
							edgeSet.add(edge);
							conflictIn.put(currentEdge, edgeSet);
						
						} else {
							conflictCurrentEdges.add(edge);
							conflictIn.put(currentEdge, conflictCurrentEdges);
						}
					}
					
				} else {
					
					sizeCandEdges = candEdges.get(currentEdge).size();
					
					candEdges.put(currentEdge, HelperFunctions.findCommonRelationships(candEdges.get(currentEdge), relationships) );
					
					if(algorithm.equals(AlgorithmType.FC_CBJ) || algorithm.equals(AlgorithmType.FC_LBJ)) {
						newSizeCandEdges = candEdges.get(currentEdge).size();
						
						if(sizeCandEdges != newSizeCandEdges) {
							conflictIn.get(currentEdge).add(edge);
						}
					}
				}
				
				if (candEdges.get(currentEdge).size() == 0) {
					return false;
				}	
				
			}
				
		}
		
		return true;
		
	}
	
	
	public boolean FCE(GraphPattern gp, Map<MyEdge, List<Relationship>> candEdges, Map<MyEdge, Relationship> assignmentEdges, MyEdge edge, Relationship relationship,  Map<MyEdge, Set<MyEdge>> conflictIn) {
		
		int sizeCandEdges = 0;
		int newSizeCandEdges = 0;
		
		for (MyEdge currentEdge: candEdges.keySet()) {
			
			sizeCandEdges = candEdges.get(currentEdge).size();
			
			//NOTE : Algorithm says update candEdges but if we were updating it then it removed the relationships and future recursion calls were not satisfied.
			candEdges.put(currentEdge, HelperFunctions.getOverlappingRelationships(candEdges.get(currentEdge), relationship));
			
			if (currentEdge.getSource().equals(edge.getSource()) ) {
				candEdges.put(currentEdge, HelperFunctions.filterSource(candEdges.get(currentEdge), assignmentEdges.get(edge).getStartNode()));
			}
			
			if (currentEdge.getTarget().equals(edge.getTarget())) {
				candEdges.put(currentEdge, HelperFunctions.filterTarget(candEdges.get(currentEdge), assignmentEdges.get(edge).getEndNode()));
			}
			
			if (currentEdge.getSource().equals(edge.getTarget())) {
				candEdges.put(currentEdge, HelperFunctions.filterSource(candEdges.get(currentEdge), assignmentEdges.get(edge).getEndNode()));
			}
			
			if (currentEdge.getTarget().equals(edge.getSource()) ) {
				candEdges.put(currentEdge, HelperFunctions.filterTarget(candEdges.get(currentEdge), assignmentEdges.get(edge).getStartNode()));
			}
			
			newSizeCandEdges = candEdges.get(currentEdge).size();
			
			if(newSizeCandEdges == 0) {
				return false;
			}
			
			if(algorithm.equals(AlgorithmType.FC_CBJ) || algorithm.equals(AlgorithmType.FC_LBJ)) {
				
				if(newSizeCandEdges != sizeCandEdges) {
					
					Set<MyEdge> conflictCurrentEdges = conflictIn.get(currentEdge);
					if(conflictCurrentEdges == null) {
						Set<MyEdge> edgeSet = new HashSet<MyEdge>();
						edgeSet.add(edge);
						conflictIn.put(currentEdge, edgeSet);
					
					} else {
						conflictCurrentEdges.add(edge);
						conflictIn.put(currentEdge, conflictCurrentEdges);
					}
				}
			}
		}
		
		return true;
	}
	
	// Helper functions
	
	
	
	
	@Override
	public void kill() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<Map<MyNode, N>> check() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getQueryCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getMaxNeighbourhood() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getAllRes() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getSearchSpace() {
		// TODO Auto-generated method stub
		return 0;
	}

	

}
