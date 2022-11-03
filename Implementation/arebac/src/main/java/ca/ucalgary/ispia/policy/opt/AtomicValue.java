package ca.ucalgary.ispia.policy.opt;

import java.util.Set;

public interface AtomicValue extends Matrix {
	
	public RebacRelationIdentifier getFirstRebacRelation();
	
	public RebacRelationIdentifier getSecondRebacRelation();
	
	public Set<AllenRelation> getAllenRelations();
}
