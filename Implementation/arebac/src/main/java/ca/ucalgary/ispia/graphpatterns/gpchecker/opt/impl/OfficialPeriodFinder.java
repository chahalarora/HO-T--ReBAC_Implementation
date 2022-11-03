package ca.ucalgary.ispia.graphpatterns.gpchecker.opt.impl;

import java.time.temporal.ValueRange;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import edu.stanford.nlp.util.Interval;
import edu.stanford.nlp.util.IntervalTree;
import scala.Int;

public class OfficialPeriodFinder {
	
	public Set<ValueRange> findOfficialPeriods(Set<ValueRange> discoverablePeriodsSet) {
		
		// initializing official period set.
		Set<ValueRange> officialPeriodsSet = new HashSet<ValueRange>();
		
		if(discoverablePeriodsSet.size() == 0) {
			return officialPeriodsSet;
		}
		
		// converting discoverable period set to list in order to use sorting.
		List<ValueRange> discoverablePeriodsList = new ArrayList<ValueRange>(discoverablePeriodsSet);
		
		// sorting discoverable periods in ascending order of start time.
		sortDiscoverablePeriod(discoverablePeriodsList);
		
		// intialising official period start and end time.
		int officialPeriodStartTime = (int) discoverablePeriodsList.get(0).getMinimum();
		int officialPeriodEndTime = (int) discoverablePeriodsList.get(0).getMaximum();
		
		// removing the first discoverable period as it has already been used.
		discoverablePeriodsList.remove(0);
		
		// looping through the remaining discoverable periods to find the official periods.
		for(ValueRange discoverablePeriod: discoverablePeriodsList) {
			
			// checking if the current discoverable period overlaps with the current official period.
			if((int) discoverablePeriod.getMinimum() <= officialPeriodEndTime) {
				
				// checking if the end time of current discoverable period is infinity i.e INT_MAX.
				if((int) discoverablePeriod.getMaximum() == Int.MaxValue()) {
					officialPeriodEndTime = Int.MaxValue();
					break;
					
				} else if((int) discoverablePeriod.getMaximum() > officialPeriodEndTime) {
					officialPeriodEndTime = (int) discoverablePeriod.getMaximum();
				}
				
			} else {
				
				// appending the official period list when the current discoverable period doesn't overlaps with the current official period.
				officialPeriodsSet.add(ValueRange.of(officialPeriodStartTime, officialPeriodEndTime));
				
				// updating the official period start time and end time for the new official period value.
				officialPeriodStartTime = (int) discoverablePeriod.getMinimum();
				officialPeriodEndTime = (int) discoverablePeriod.getMaximum();
				
			}
		}
		
		// adding the last calculated official period to the official period set.
		officialPeriodsSet.add(ValueRange.of(officialPeriodStartTime, officialPeriodEndTime));
			
		// returning the official periods set.
		return officialPeriodsSet;
	}
	
	// sorting the discoverable periods based on the start time in ascending order.
	private void sortDiscoverablePeriod(List<ValueRange> discoverablePeriodsList) {
		Collections.sort(discoverablePeriodsList, (dp1, dp2) -> Integer.valueOf((int)dp1.getMinimum()).compareTo( Integer.valueOf((int)dp2.getMinimum())));
	}
	
	// finding official periods from discoverable periods stored in interval tree.
		public IntervalTree findOfficialPeriods(IntervalTree discoverablePeriodsTree) {
			
			// initializing official period set.
			IntervalTree officialPeriodsTree = new IntervalTree();
			
			if(discoverablePeriodsTree.size() == 0) {
				return officialPeriodsTree;
			}
			
			Iterator discoverablePeriodsSetIterator =  discoverablePeriodsTree.iterator();
			
			Interval firstInterval = (Interval) discoverablePeriodsSetIterator.next();
		
			// intialising official period start and end time.
			int officialPeriodStartTime = (int) firstInterval.first;
			int officialPeriodEndTime = (int) firstInterval.second;
			
			// looping through the remaining discoverable periods to find the official periods.		
			while(discoverablePeriodsSetIterator.hasNext()) {
				
				Interval discoverablePeriod = (Interval) discoverablePeriodsSetIterator.next();
				
				// checking if the current discoverable period overlaps with the current official period.
				if((int) discoverablePeriod.first <= officialPeriodEndTime) {
					
					// checking if the end time of current discoverable period is infinity i.e INT_MAX.
					if((int) discoverablePeriod.second == Int.MaxValue()) {
						officialPeriodEndTime = Int.MaxValue();
						break;
						
					} else if((int) discoverablePeriod.second > officialPeriodEndTime) {
						officialPeriodEndTime = (int) discoverablePeriod.second;
					}
					
				} else {
					
					// appending the official period list when the current discoverable period doesn't overlaps with the current official period.
					officialPeriodsTree.addNonNested(Interval.toInterval((int)officialPeriodStartTime, (int) officialPeriodEndTime));
					
					// updating the official period start time and end time for the new official period value.
					officialPeriodStartTime = (int) discoverablePeriod.first;
					officialPeriodEndTime = (int) discoverablePeriod.second;
					
				}
			}
			
			// adding the last calculated official period to the official period set.
			officialPeriodsTree.addNonNested(Interval.toInterval((int)officialPeriodStartTime, (int) officialPeriodEndTime));
				
			// returning the official periods set.
			return officialPeriodsTree;
		}
	
	// finding official periods from discoverable periods stored in interval tree.
	public IntervalTree findOfficialPeriods(IntervalTree discoverablePeriodsTree, Interval discoverablePeriodNew) {
		
		// initializing official period set.
		IntervalTree officialPeriodsTree = new IntervalTree();
		
		discoverablePeriodsTree.addNonNested(discoverablePeriodNew);
		
		if(discoverablePeriodsTree.size() == 0) {
			return officialPeriodsTree;
		}
		
		Iterator discoverablePeriodsSetIterator =  discoverablePeriodsTree.iterator();
		
		Interval firstInterval = (Interval) discoverablePeriodsSetIterator.next();
	
		// intialising official period start and end time.
		int officialPeriodStartTime = (int) firstInterval.first;
		int officialPeriodEndTime = (int) firstInterval.second;
		
		// looping through the remaining discoverable periods to find the official periods.		
		while(discoverablePeriodsSetIterator.hasNext()) {
			
			Interval discoverablePeriod = (Interval) discoverablePeriodsSetIterator.next();
			
			// checking if the current discoverable period overlaps with the current official period.
			if((int) discoverablePeriod.first <= officialPeriodEndTime) {
				
				// checking if the end time of current discoverable period is infinity i.e INT_MAX.
				if((int) discoverablePeriod.second == Int.MaxValue()) {
					officialPeriodEndTime = Int.MaxValue();
					break;
					
				} else if((int) discoverablePeriod.second > officialPeriodEndTime) {
					officialPeriodEndTime = (int) discoverablePeriod.second;
				}
				
			} else {
				
				// appending the official period list when the current discoverable period doesn't overlaps with the current official period.
				officialPeriodsTree.addNonNested(Interval.toInterval((int)officialPeriodStartTime, (int) officialPeriodEndTime));
				
				// updating the official period start time and end time for the new official period value.
				officialPeriodStartTime = (int) discoverablePeriod.first;
				officialPeriodEndTime = (int) discoverablePeriod.second;
				
			}
		}
		
		// adding the last calculated official period to the official period set.
		officialPeriodsTree.addNonNested(Interval.toInterval((int)officialPeriodStartTime, (int) officialPeriodEndTime));
			
		// returning the official periods set.
		return officialPeriodsTree;
	}
	
}
