package ca.ucalgary.ispia.graphpatterns.util;

public class DiscoverablePeriod {

	private Integer startTime;
	private Integer endTime;
	
	public DiscoverablePeriod(Integer startTime, Integer endTime) {
		this.startTime = startTime;
		this.endTime = endTime;
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
	
	 @Override
	    public boolean equals(Object obj) {
	        if (obj == this) {
	            return true;
	        }
	        if (obj instanceof DiscoverablePeriod) {
	        	DiscoverablePeriod other = (DiscoverablePeriod) obj;
	            return startTime == other.startTime && endTime == other.endTime;
	        }
	        return false;
	    }

    @Override
    public int hashCode() {
        long hash = startTime + (endTime << 16) + (endTime >> 48);
        return (int) (hash ^ (hash >>> 32));
    }
}
