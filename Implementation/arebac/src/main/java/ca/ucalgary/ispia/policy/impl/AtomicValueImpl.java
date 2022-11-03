package ca.ucalgary.ispia.policy.impl;

import java.util.Set;

import ca.ucalgary.ispia.policy.opt.AllenRelation;
import ca.ucalgary.ispia.policy.opt.AtomicValue;
import ca.ucalgary.ispia.policy.opt.RebacRelationIdentifier;

public class AtomicValueImpl implements AtomicValue {
	
//	private Interval firstInterval;
//	private Interval secondInterval;
	
	private RebacRelationIdentifier firstRebacRelation;
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((allenRelations == null) ? 0 : allenRelations.hashCode());
		result = prime * result + ((firstRebacRelation == null) ? 0 : firstRebacRelation.hashCode());
		result = prime * result + ((secondRebacRelation == null) ? 0 : secondRebacRelation.hashCode());
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
		AtomicValueImpl other = (AtomicValueImpl) obj;
		if (allenRelations == null) {
			if (other.allenRelations != null)
				return false;
		} else if (!allenRelations.equals(other.allenRelations))
			return false;
		if (firstRebacRelation == null) {
			if (other.firstRebacRelation != null)
				return false;
		} else if (!firstRebacRelation.equals(other.firstRebacRelation))
			return false;
		if (secondRebacRelation == null) {
			if (other.secondRebacRelation != null)
				return false;
		} else if (!secondRebacRelation.equals(other.secondRebacRelation))
			return false;
		return true;
	}



	public RebacRelationIdentifier getFirstRebacRelation() {
		return firstRebacRelation;
	}



	public void setFirstRebacRelation(RebacRelationIdentifier firstRebacRelation) {
		this.firstRebacRelation = firstRebacRelation;
	}



	public RebacRelationIdentifier getSecondRebacRelation() {
		return secondRebacRelation;
	}



	public void setSecondRebacRelation(RebacRelationIdentifier secondRebacRelation) {
		this.secondRebacRelation = secondRebacRelation;
	}

	private RebacRelationIdentifier secondRebacRelation;
	
	private Set<AllenRelation> allenRelations;
	
//	public AtomicValueImpl(Interval firstInterval, Interval secondInterval, Set<AllenRelation> allenRelations) {
//		this.firstInterval = firstInterval;
//		this.secondInterval = secondInterval;
//		this.allenRelations = allenRelations;
//	}
	
	
	public AtomicValueImpl(RebacRelationIdentifier firstRebacRelation, RebacRelationIdentifier secondRebacRelation, Set<AllenRelation> allenRelations) {
		this.firstRebacRelation = firstRebacRelation;
		this.secondRebacRelation = secondRebacRelation;
		this.allenRelations = allenRelations;
	}
	
	public AtomicValueImpl(AtomicValue atomicValue) {
		this.firstRebacRelation = atomicValue.getFirstRebacRelation();
		this.secondRebacRelation = atomicValue.getSecondRebacRelation();
		this.allenRelations = atomicValue.getAllenRelations();
	}
	


//	public void setFirstInterval(Interval firstInterval) {
//		this.firstInterval = firstInterval;
//	}
//
//	public void setSecondInterval(Interval secondInterval) {
//		this.secondInterval = secondInterval;
//	}

	public void setAllenRelations(Set<AllenRelation> allenRelations) {
		this.allenRelations = allenRelations;
	}

//	@Override
//	public Interval getFirstInterval() {
//		// TODO Auto-generated method stub
//		return firstInterval;
//	}
//
//	@Override
//	public Interval getSecondInterval() {
//		// TODO Auto-generated method stub
//		return secondInterval;
//	}

	@Override
	public Set<AllenRelation> getAllenRelations() {
		// TODO Auto-generated method stub
		return allenRelations;
	}

}
