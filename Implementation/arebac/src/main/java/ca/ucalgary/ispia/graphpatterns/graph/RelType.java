package ca.ucalgary.ispia.graphpatterns.graph;

import java.io.Serializable;

import org.neo4j.graphdb.RelationshipType;

public enum RelType implements RelationshipType, Serializable{
	RelA(0), RelB(1), RelC(2), RelD(3), RelE(4), RelF(5), RelG(6), RELTYPE1(7), RELTYPE2(8), RELTYPE3(9), RELTYPE4(10), RELTYPE5(11), RELTYPE(12), R0(13),
	R1(14), R2(15), R3(16), R4(17), R5(18), R6(19); 
	
	private final int idx;
	
	private RelType(int idx){
		this.idx = idx;
	}
	
	public int getIdx(){
		return this.idx;
	}
}
