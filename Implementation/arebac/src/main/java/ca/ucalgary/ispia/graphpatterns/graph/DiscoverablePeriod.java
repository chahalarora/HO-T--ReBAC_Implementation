package ca.ucalgary.ispia.graphpatterns.graph;

public class DiscoverablePeriod {

	private Integer startTime;
	private Integer endTime;
	
	public DiscoverablePeriod(Integer startTime, Integer endTime) {
		this.startTime = startTime;
		this.endTime = endTime;
	}
	
	public DiscoverablePeriod() {
		this.startTime = 0;
		this.endTime = 0;
	}
	
	public Integer getStartTime() {
		return startTime;
	}
	public void setStartTime(Integer startTime) {
		this.startTime = startTime;
	}
	public Integer getEndTime() {
		return endTime;
	}
	public void setEndTime(Integer endTime) {
		this.endTime = endTime;
	}
	
    public String toString() {
        return "[" + startTime + ", " + endTime + "]";
    }

  
    public boolean equals(Object other) {
        if (other == this) return true;
        if (other == null) return false;
        if (other.getClass() != this.getClass()) return false;
        DiscoverablePeriod that = (DiscoverablePeriod) other;
        return this.startTime == that.startTime && this.endTime == that.endTime;
    }

  
    public int hashCode() {
        int hash1 = ((Integer) startTime).hashCode();
        int hash2 = ((Integer) endTime).hashCode();
        return 31*hash1 + hash2;
    }
}
