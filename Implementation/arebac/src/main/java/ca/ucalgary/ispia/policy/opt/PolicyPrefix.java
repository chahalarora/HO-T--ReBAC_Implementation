package ca.ucalgary.ispia.policy.opt;

public interface PolicyPrefix {
	
	public ExistentialQuantifier getExistentialQuantifier();
	public Interval getInterval();
	public RebacRelationIdentifier getRebacRelationIdentifier();
	public PolicyPrefix getNextPolicyPrefix();
	
}
