package ca.ucalgary.ispia.policy.impl;

import ca.ucalgary.ispia.policy.opt.ExistentialQuantifier;
import ca.ucalgary.ispia.policy.opt.Interval;
import ca.ucalgary.ispia.policy.opt.PolicyPrefix;
import ca.ucalgary.ispia.policy.opt.RebacRelationIdentifier;

public class PolicyPrefixImpl implements PolicyPrefix {

	private ExistentialQuantifier existentialQuantifier;
	private Interval interval;
	private RebacRelationIdentifier rebacRelationIdentifier;
	private PolicyPrefix nextPolicyPrefix;
	
	public PolicyPrefixImpl(ExistentialQuantifier existentialQuantifier, Interval interval, RebacRelationIdentifier rebacRelationIdentifier, PolicyPrefix nextPolicyPrefix) {
		this.existentialQuantifier = existentialQuantifier;
		this.interval = interval;
		this.rebacRelationIdentifier = rebacRelationIdentifier;
		this.nextPolicyPrefix = nextPolicyPrefix;
	}
	
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((existentialQuantifier == null) ? 0 : existentialQuantifier.hashCode());
		result = prime * result + ((interval == null) ? 0 : interval.hashCode());
		result = prime * result + ((nextPolicyPrefix == null) ? 0 : nextPolicyPrefix.hashCode());
		result = prime * result + ((rebacRelationIdentifier == null) ? 0 : rebacRelationIdentifier.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PolicyPrefixImpl other = (PolicyPrefixImpl) obj;
		if (existentialQuantifier != other.existentialQuantifier)
			return false;
		if (interval == null) {
			if (other.interval != null)
				return false;
		} else if (!interval.equals(other.interval))
			return false;
		if (nextPolicyPrefix == null) {
			if (other.nextPolicyPrefix != null)
				return false;
		} else if (!nextPolicyPrefix.equals(other.nextPolicyPrefix))
			return false;
		if (rebacRelationIdentifier == null) {
			if (other.rebacRelationIdentifier != null)
				return false;
		} else if (!rebacRelationIdentifier.equals(other.rebacRelationIdentifier))
			return false;
		return true;
	}
	
	public Interval getInterval() {
		return interval;
	}
	
	public void setInterval(Interval interval) {
		this.interval = interval;
	}
	public ExistentialQuantifier getExistentialQuantifier() {
		return existentialQuantifier;
	}
	public void setExistentialQuantifier(ExistentialQuantifier existentialQuantifier) {
		this.existentialQuantifier = existentialQuantifier;
	}
	public RebacRelationIdentifier getRebacRelationIdentifier() {
		return rebacRelationIdentifier;
	}
	public void setRebacRelationIdentifier(RebacRelationIdentifier rebacRelationIdentifier) {
		this.rebacRelationIdentifier = rebacRelationIdentifier;
	}
	public PolicyPrefix getNextPolicyPrefix() {
		return nextPolicyPrefix;
	}
	public void setNextPolicyPrefix(PolicyPrefix nextPolicyPrefix) {
		this.nextPolicyPrefix = nextPolicyPrefix;
	}
}
