package ca.ucalgary.ispia.policy.opt;

public class RebacRelationIdentifier {
	
	private int sourceId;
	private int destinationId;
	private int relationshipId;
	
	public RebacRelationIdentifier (int sourceId, int destinationId, int relationshipId) {
		this.sourceId = sourceId;
		this.destinationId = destinationId;
		this.relationshipId = relationshipId;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + destinationId;
		result = prime * result + relationshipId;
		result = prime * result + sourceId;
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
		RebacRelationIdentifier other = (RebacRelationIdentifier) obj;
		if (destinationId != other.destinationId)
			return false;
		if (relationshipId != other.relationshipId)
			return false;
		if (sourceId != other.sourceId)
			return false;
		return true;
	}
	public int getSourceId() {
		return sourceId;
	}
	public void setSourceId(int sourceId) {
		this.sourceId = sourceId;
	}
	public int getDestinationId() {
		return destinationId;
	}
	public void setDestinationId(int destinationId) {
		this.destinationId = destinationId;
	}
	public int getRelationshipId() {
		return relationshipId;
	}
	public void setRelationshipId(int relationshipId) {
		this.relationshipId = relationshipId;
	}

}
