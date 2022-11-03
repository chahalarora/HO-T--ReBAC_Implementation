package ca.ucalgary.ispia.graphpatterns.tests;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.dbutils.DbUtils;
import org.neo4j.graphdb.Node;

import ca.ucalgary.ispia.graphpatterns.Driver;
import ca.ucalgary.ispia.graphpatterns.MysqlConnection;
import ca.ucalgary.ispia.graphpatterns.graph.AlgorithmType;
import ca.ucalgary.ispia.graphpatterns.graph.GraphPattern;
import ca.ucalgary.ispia.graphpatterns.graph.MyNode;
import ca.ucalgary.ispia.graphpatterns.util.Pair;
import ca.ucalgary.ispia.policy.impl.AtomicValueImpl;
import ca.ucalgary.ispia.policy.impl.ConjunctionMatrixImpl;
import ca.ucalgary.ispia.policy.impl.DisjunctionMatrixImpl;
import ca.ucalgary.ispia.policy.impl.FalseMatrixImpl;
import ca.ucalgary.ispia.policy.impl.NegationMatrixImpl;
import ca.ucalgary.ispia.policy.impl.PolicyImpl;
import ca.ucalgary.ispia.policy.impl.PolicyPrefixImpl;
import ca.ucalgary.ispia.policy.impl.TrueMatrixImpl;
import ca.ucalgary.ispia.policy.opt.AllenRelation;
import ca.ucalgary.ispia.policy.opt.AtomicValue;
import ca.ucalgary.ispia.policy.opt.ConjunctionMatrix;
import ca.ucalgary.ispia.policy.opt.DisjunctionMatrix;
import ca.ucalgary.ispia.policy.opt.ExistentialQuantifier;
import ca.ucalgary.ispia.policy.opt.Matrix;
import ca.ucalgary.ispia.policy.opt.NegationMatrix;
import ca.ucalgary.ispia.policy.opt.PolicyExecute;
import ca.ucalgary.ispia.policy.opt.PolicyPrefix;
import ca.ucalgary.ispia.policy.opt.PolicyToSqlTraslator;
import ca.ucalgary.ispia.policy.opt.RebacRelationIdentifier;
import scala.util.Random;

public class PolicyEvaluationExperiments {
	
//	static AllenRelation[] allenRelationsList = new AllenRelation[] {AllenRelation.equals, AllenRelation.finishedBy, AllenRelation.finishes, AllenRelation.meets, AllenRelation.metBy, AllenRelation.overlappedBy, AllenRelation.overlaps, AllenRelation.precededBy, AllenRelation.precedes, AllenRelation.startedBy, AllenRelation.starts};
	static AllenRelation[] overlappingAllenRelationsList = new AllenRelation[] {
			AllenRelation.equals,
			AllenRelation.finishedBy,
			AllenRelation.finishes,
			AllenRelation.meets, 
			AllenRelation.metBy, 
			AllenRelation.overlappedBy, 
			AllenRelation.overlaps,
//			AllenRelation.precededBy,
//			AllenRelation.precedes,
			AllenRelation.startedBy, 
			AllenRelation.starts,
			AllenRelation.contains,
			AllenRelation.during};

//	static AllenRelation[] overlappingAllenRelationsList = new AllenRelation[] {
//			AllenRelation.equals,
//			AllenRelation.finishedBy, 
////			AllenRelation.finishes, 
//			AllenRelation.meets, 
////			AllenRelation.metBy, 
////			AllenRelation.overlappedBy, 
//			AllenRelation.overlaps, 
////			AllenRelation.precededBy, 
//			AllenRelation.precedes,
//			AllenRelation.startedBy, 
//			AllenRelation.starts,
//			AllenRelation.contains,
////			AllenRelation.during
//			};
	
	
	static AllenRelation[] allAllenRelationsList = new AllenRelation[] {
			AllenRelation.equals,
			AllenRelation.finishedBy, 
			AllenRelation.finishes, 
			AllenRelation.meets, 
			AllenRelation.metBy, 
			AllenRelation.overlappedBy, 
			AllenRelation.overlaps, 
			AllenRelation.precededBy, 
			AllenRelation.precedes,
			AllenRelation.startedBy, 
			AllenRelation.starts,
			AllenRelation.contains,
			AllenRelation.during};

	
	
//	static AllenRelation[] allenRelationsList = new AllenRelation[] {AllenRelation.equals, AllenRelation.finishedBy, AllenRelation.finishes, AllenRelation.meets, AllenRelation.metBy, AllenRelation.overlappedBy, AllenRelation.overlaps};
	
	static int NumberofRecords = 700000;
	
	static Connection mysqlConn = null;
	
	static String propFileLocation= "./properties/Evaluate.properties";
	
	public static long timeoutlimit = 60000l;
	
	public static void main(String[] args){	
		
		try {
			
			File file = new File(propFileLocation);
			InputStream in = new FileInputStream(file);
		    Properties properties = new Properties();
		    properties.load(in);
			
			int numberofIterationPolicy = Integer.parseInt(properties.getProperty("numberofIterationPolicy"));
			int numberofGPInPolicy = Integer.parseInt(properties.getProperty("numberofGPInPolicy"));
			
			String authorizationResultString = properties.getProperty("authorizationResult").toString();			
			boolean authorizationResult = false;
			if(authorizationResultString.equalsIgnoreCase("true")) {
				authorizationResult = true;
		    
		    }else if(authorizationResultString.equalsIgnoreCase("false")) {
		    	authorizationResult = false;
		    	
		    }
			
			
			int recordedResultCount = 0;
			List<Long> executionTimeList = new ArrayList<Long>();

			
			while(recordedResultCount < numberofIterationPolicy) {	
				
				MysqlConnection mysqlConnection = new MysqlConnection();
				mysqlConn =  mysqlConnection.mysqlConn;
				
				PolicyEvaluationExperiments policyEvaluationExperiments = new PolicyEvaluationExperiments();
				
				System.out.println(recordedResultCount);
				recordedResultCount++;
				
				//int recordsReturned = getRealtimeMonitoring(executionTimeList, authorizationResult);
//				int recordsReturned = getRemoteAccess(executionTimeList, authorizationResult);
//				int recordsReturned = getMedicalDataGathering(executionTimeList, authorizationResult);
				
				
				
				int recordsReturned = policyEvaluationExperiments.createArtificialPolicy(numberofGPInPolicy, executionTimeList, authorizationResult);						
//				if(authorizationResult) {
//					
//					if(recordsReturned > 0) {
//						System.out.println(recordedResultCount);
//						recordedResultCount++;
//					}
//					
//				} else {
//					
//					if(recordsReturned == 0) {
//						System.out.println(recordedResultCount);
//						recordedResultCount++;
//					}
//					
//				}
									
				DbUtils.closeQuietly(mysqlConn);
			}
			
			long executionTimeTotal = 0;
			for(int i=0; i< executionTimeList.size(); i++) {
				executionTimeTotal = executionTimeTotal + executionTimeList.get(i);
			}
			
			executionTimeTotal = executionTimeTotal + (numberofIterationPolicy - executionTimeList.size())*timeoutlimit;
			
			System.out.println("Number of GP in Policy: " + numberofGPInPolicy);
//			System.out.println("Authorization Decision: " + authorizationResult);
			
			double averegeExecutionTime = executionTimeTotal / (float) executionTimeList.size();
			System.out.println("Total time: " +averegeExecutionTime);
			System.out.println("Total results: " + executionTimeList.size());
//			double averegeTrimmedTimeForRun = trimRunTime(executionTimeList, 10);
//			System.out.println("Total trimmed time: " + averegeTrimmedTimeForRun);
			
		} catch(Exception e) {
			
		} finally {
//			DbUtils.closeQuietly(mysqlConn);
		}
			
	}
	
	
	public static double trimRunTime( List<Long> totalTimeArray, int percent) {
	    
		List<Long> resultTimeArray = new ArrayList<>();
		
		if ( percent < 0 || 100 < percent ) {
	        throw new IllegalArgumentException("Unexpected value: " + percent);
	    }

	    if ( 0 == totalTimeArray.size() ) {
	        return Double.NaN;
	    }

	    final int n = totalTimeArray.size();
	    final int k = (int) Math.round(n * ( percent / 100.0 ) / 2.0); // Check overflow
	    
	    Collections.sort(totalTimeArray);
	    
	    for(int i=0; i<totalTimeArray.size(); i++) {
	    	
	    	if(!(i<k || i> n-k)) {
	    		resultTimeArray.add(totalTimeArray.get(i));
	    	}
	    	
	    	
	    }
	    
	    
	    long totalTrimmedTimeForRuns = 0;
		for(int i=0; i< resultTimeArray.size(); i++) {
			totalTrimmedTimeForRuns = totalTrimmedTimeForRuns + resultTimeArray.get(i);
		}
		
		double averegeTrimmedTimeForRun = totalTrimmedTimeForRuns/ (float) resultTimeArray.size();

		return averegeTrimmedTimeForRun;

	}
	
	public static int getRealtimeMonitoring(List<Long> executionTimeList, boolean authorizationResult) {
		
		List<RebacRelationIdentifier> rebacRelationList = getBinaryRelations(NumberofRecords, 3);
		
		RebacRelationIdentifier authorization = rebacRelationList.get(0);
		RebacRelationIdentifier colocation = rebacRelationList.get(1);
		RebacRelationIdentifier connection = rebacRelationList.get(2);
		
		
		ExistentialQuantifier existentialQuantifierAuthorization = ExistentialQuantifier.Regular;
		PolicyPrefix policyPrefixAuthorization = new PolicyPrefixImpl(existentialQuantifierAuthorization, null, authorization, null);
		
		ExistentialQuantifier existentialQuantifierColocation = ExistentialQuantifier.Regular;
		PolicyPrefix policyPrefixColocation = new PolicyPrefixImpl(existentialQuantifierColocation, null, colocation, policyPrefixAuthorization);
		
		ExistentialQuantifier existentialQuantifierConnection = ExistentialQuantifier.Regular;
		PolicyPrefix policyPrefixConnection = new PolicyPrefixImpl(existentialQuantifierConnection, null, connection, policyPrefixColocation);
		
		Set<AllenRelation> allenRelations = new HashSet<AllenRelation>();
		allenRelations.addAll(Arrays.asList(overlappingAllenRelationsList));
		
//		AtomicValue atomicValue1 = new AtomicValueImpl(authorization, colocation, allenRelations);
//		AtomicValue atomicValue2 = new AtomicValueImpl(rebacRelationDesignated, rebacRelationDeployed, allenRelations);
//		AtomicValue atomicValue7 = new AtomicValueImpl(rebacRelationSchedule, rebacRelationDeployed, allenRelations);
//		
//		ConjunctionMatrixImpl conjunctionMatrix1 = new ConjunctionMatrixImpl(atomicValue1, atomicValue2);
//		ConjunctionMatrixImpl conjunctionMatrix6 = new ConjunctionMatrixImpl(conjunctionMatrix1, atomicValue7);
		
		TrueMatrixImpl trueMatrixImpl = new TrueMatrixImpl();

		PolicyImpl policy = new PolicyImpl(policyPrefixConnection, trueMatrixImpl);
		
		PolicyToSqlTraslator policyToSqlTraslator = new PolicyToSqlTraslator();
		String policySqlString = policyToSqlTraslator.policyTranslator(policy);
		
		// execute policy sql
		PolicyExecute policyExecute = new PolicyExecute();
		long startTime = System.currentTimeMillis();
		int resultRowsCount = policyExecute.executePolicySql(mysqlConn, policySqlString);
		long endTime   = System.currentTimeMillis();
		long totalTime = endTime - startTime;
//		System.out.println("Execution time (milliseconds): "+ totalTime);	
//		System.out.println("Result count: "+ resultRowsCount);
		
		executionTimeList.add(totalTime);
		
//		if(authorizationResult) {
//			if(resultRowsCount > 0) {
//				executionTimeList.add(totalTime);
//			}
//		} else {
//			
//			if(resultRowsCount == 0) {
//				executionTimeList.add(totalTime);
//			}
//		}
		
		return resultRowsCount;
	}
	
	
	
	public static int getRemoteAccess(List<Long> executionTimeList, boolean authorizationResult) {
		
		List<RebacRelationIdentifier> rebacRelationList = getBinaryRelations(NumberofRecords, 3);
		
		RebacRelationIdentifier authorization = rebacRelationList.get(0);
//		RebacRelationIdentifier colocation = rebacRelationList.get(1);
		RebacRelationIdentifier connection = rebacRelationList.get(2);
		
		
		ExistentialQuantifier existentialQuantifierAuthorization = ExistentialQuantifier.Regular;
		PolicyPrefix policyPrefixAuthorization = new PolicyPrefixImpl(existentialQuantifierAuthorization, null, authorization, null);
		
//		ExistentialQuantifier existentialQuantifierColocation = ExistentialQuantifier.Regular;
//		PolicyPrefix policyPrefixColocation = new PolicyPrefixImpl(existentialQuantifierColocation, null, colocation, policyPrefixAuthorization);
		
		ExistentialQuantifier existentialQuantifierConnection = ExistentialQuantifier.Regular;
		PolicyPrefix policyPrefixConnection = new PolicyPrefixImpl(existentialQuantifierConnection, null, connection, policyPrefixAuthorization);
		
		Set<AllenRelation> allenRelations = new HashSet<AllenRelation>();
		allenRelations.addAll(Arrays.asList(overlappingAllenRelationsList));
		
		AtomicValue atomicValue1 = new AtomicValueImpl(authorization, connection, allenRelations);
//		AtomicValue atomicValue2 = new AtomicValueImpl(rebacRelationDesignated, rebacRelationDeployed, allenRelations);
//		AtomicValue atomicValue7 = new AtomicValueImpl(rebacRelationSchedule, rebacRelationDeployed, allenRelations);
//		
//		ConjunctionMatrixImpl conjunctionMatrix1 = new ConjunctionMatrixImpl(atomicValue1, atomicValue2);
//		ConjunctionMatrixImpl conjunctionMatrix6 = new ConjunctionMatrixImpl(conjunctionMatrix1, atomicValue7);
		
//		TrueMatrixImpl trueMatrixImpl = new TrueMatrixImpl();

		PolicyImpl policy = new PolicyImpl(policyPrefixConnection, atomicValue1);
		
		PolicyToSqlTraslator policyToSqlTraslator = new PolicyToSqlTraslator();
		String policySqlString = policyToSqlTraslator.policyTranslator(policy);
		
		// execute policy sql
		PolicyExecute policyExecute = new PolicyExecute();
		long startTime = System.currentTimeMillis();
		int resultRowsCount = policyExecute.executePolicySql(mysqlConn, policySqlString);
		long endTime   = System.currentTimeMillis();
		long totalTime = endTime - startTime;
//		System.out.println("Execution time (milliseconds): "+ totalTime);	
//		System.out.println("Result count: "+ resultRowsCount);
		
		executionTimeList.add(totalTime);
		
		
//		if(authorizationResult) {
//			if(resultRowsCount > 0) {
//				executionTimeList.add(totalTime);
//			}
//		} else {
//			
//			if(resultRowsCount == 0) {
//				executionTimeList.add(totalTime);
//			}
//		}
		
		return resultRowsCount;
	}
	
	public static int getMedicalDataGathering(List<Long> executionTimeList, boolean authorizationResult) {
		
		List<RebacRelationIdentifier> rebacRelationList = getBinaryRelations(NumberofRecords, 3);
		
		RebacRelationIdentifier authorization = rebacRelationList.get(0);
		RebacRelationIdentifier colocation = rebacRelationList.get(1);
		RebacRelationIdentifier connection = rebacRelationList.get(2);
		
		
		ExistentialQuantifier existentialQuantifierAuthorization = ExistentialQuantifier.Regular;
		PolicyPrefix policyPrefixAuthorization = new PolicyPrefixImpl(existentialQuantifierAuthorization, null, authorization, null);
		
		ExistentialQuantifier existentialQuantifierColocation = ExistentialQuantifier.Regular;
		PolicyPrefix policyPrefixColocation = new PolicyPrefixImpl(existentialQuantifierColocation, null, colocation, policyPrefixAuthorization);
		
		ExistentialQuantifier existentialQuantifierConnection = ExistentialQuantifier.Regular;
		PolicyPrefix policyPrefixConnection = new PolicyPrefixImpl(existentialQuantifierConnection, null, connection, policyPrefixColocation);
		
		Set<AllenRelation> allenRelations = new HashSet<AllenRelation>();
		allenRelations.addAll(Arrays.asList(overlappingAllenRelationsList));
		
		AtomicValue atomicValue1 = new AtomicValueImpl(authorization, colocation, allenRelations);
		AtomicValue atomicValue2 = new AtomicValueImpl(colocation, connection, allenRelations);
//		AtomicValue atomicValue7 = new AtomicValueImpl(rebacRelationSchedule, rebacRelationDeployed, allenRelations);
//		
		ConjunctionMatrixImpl conjunctionMatrix1 = new ConjunctionMatrixImpl(atomicValue1, atomicValue2);
//		ConjunctionMatrixImpl conjunctionMatrix6 = new ConjunctionMatrixImpl(conjunctionMatrix1, atomicValue7);
		
//		TrueMatrixImpl trueMatrixImpl = new TrueMatrixImpl();

		PolicyImpl policy = new PolicyImpl(policyPrefixConnection, conjunctionMatrix1);
		
		PolicyToSqlTraslator policyToSqlTraslator = new PolicyToSqlTraslator();
		String policySqlString = policyToSqlTraslator.policyTranslator(policy);
		
		// execute policy sql
		PolicyExecute policyExecute = new PolicyExecute();
		long startTime = System.currentTimeMillis();
		int resultRowsCount = policyExecute.executePolicySql(mysqlConn, policySqlString);
		long endTime   = System.currentTimeMillis();
		long totalTime = endTime - startTime;
//		System.out.println("Execution time (milliseconds): "+ totalTime);	
//		System.out.println("Result count: "+ resultRowsCount);
		
		executionTimeList.add(totalTime);
		
//		if(authorizationResult) {
//			if(resultRowsCount > 0) {
//				executionTimeList.add(totalTime);
//			}
//		} else {
//			
//			if(resultRowsCount == 0) {
//				executionTimeList.add(totalTime);
//			}
//		}
		
		return resultRowsCount;
	}
	
		
	public static List<RebacRelationIdentifier> getBinaryRelations(Integer recordsSize, Integer policySize) {
		
		Random random = new Random();
		int containedTimePoint = random.nextInt(100);
		
		if(containedTimePoint == 0) {
			containedTimePoint = random.nextInt(100);
		}
		
		List<Integer> randomRecordIds = getRandomRecordsFromSqlTable(containedTimePoint, recordsSize, policySize);
		
		List<RebacRelationIdentifier> rebacRelationList = new ArrayList<RebacRelationIdentifier>();
		
		for(Integer recordId: randomRecordIds) {
			RebacRelationIdentifier rebacRelation = executeSqlQueryToRetrieveRecord(getSqlQueryToRetrieveRecord(recordId)); 
			rebacRelationList.add(rebacRelation);
		}

		return rebacRelationList;
	}
	
	public static List<Integer> getRandomRecordsFromSqlTable(int containtedTimePoint, int numberOfRecords, int numberOfRelations) {
		List<Integer> recordsCollection = new ArrayList<Integer>();
		
		if(numberOfRecords < numberOfRelations) {
			return new ArrayList<Integer>();
		}
		
//		for(int i=0; i<numberOfRecords; i++) {
//			recordsCollection.add(i);
//		}
		
		recordsCollection = getRecordIdsFromSqlTable(containtedTimePoint); 
		
		Collections.shuffle(recordsCollection);
		
		List<Integer> randomRecords = new ArrayList<Integer>();
		
		for(int j=0; j < numberOfRelations; j++) {
			randomRecords.add(recordsCollection.get(j));
		}
		
		return randomRecords;
	}
	
	
	public static List<Integer> getRecordIdsFromSqlTable(int containtedTimePoint) {
		
		List<Integer> recordsCollection = new ArrayList<Integer>();
		
		MysqlConnection mysqlConnection = new MysqlConnection();
		Connection mysqlConn = mysqlConnection.mysqlConn;
		
		PreparedStatement policySqlQueryPrepareStatement = null;
		ResultSet resultSet = null;
		
	 	try {
	 		
//	 		policySqlQueryPrepareStatement = mysqlConn.prepareStatement("SELECT id as id FROM official_periods Where start_time < " + containtedTimePoint +" and end_time >" +containtedTimePoint+";");
	 		
	 		policySqlQueryPrepareStatement = mysqlConn.prepareStatement("SELECT id as id FROM official_periods Where start_time < " + containtedTimePoint + ";");
	 		
//	 		policySqlQueryPrepareStatement = mysqlConn.prepareStatement("SELECT id as id FROM official_periods;");

	 		resultSet = policySqlQueryPrepareStatement.executeQuery();

			
			while (resultSet.next()) {
				recordsCollection.add(resultSet.getInt("id"));
			}
			
		} catch (SQLException e) {
			 e.printStackTrace();
			 
		} finally {
			DbUtils.closeQuietly(mysqlConn);
			DbUtils.closeQuietly(resultSet);
			DbUtils.closeQuietly(policySqlQueryPrepareStatement);
		}
		
		return recordsCollection;
	}
	
	
	public static String getSqlQueryToRetrieveRecord(Integer recordId) {
		 String resultSqlQuery = "SELECT * from official_periods WHERE id = "+ recordId;
		
		return resultSqlQuery;
	}
	
	public class Terminator {

		private Killable obj;
		private ExecutorService service;

		/**
		 * Constructor. 
		 * @param obj The object to kill
		 */
		Terminator( Killable obj ) {
			this.obj = obj;
			service = null;
		}

		/**
		 * Removes the reference to the object
		 */
		public void nullifyObj(){
			this.obj = null;
		}

		/**
		 * Terminates the process after the specified time.
		 * @param millis The specified time to terminate the process.
		 */
		public void terminateAfter( final long millis ) {
			service = Executors.newSingleThreadExecutor();
			service.submit( new Runnable() {
				@Override
				public void run() {

					//Make the threat sleep for the specified time
					long startTime = System.currentTimeMillis();
					do {
						try {
							Thread.sleep( millis );
						} catch ( InterruptedException ignored ){
							return;
						}
					}
					while ((System.currentTimeMillis() - startTime) < millis );

					//Kill the process if we still have reference to the object
					if (obj != null) {
						obj.kill();
					}

				}
			}
					);
		}

		//Stops this service.
		public void stop(){
			service.shutdownNow();
		}
	}
	
	public static RebacRelationIdentifier executeSqlQueryToRetrieveRecord(String policySqlQuery) {
		
		MysqlConnection mysqlConnection = new MysqlConnection();
		Connection mysqlConn = mysqlConnection.mysqlConn;
		
		PreparedStatement policySqlQueryPrepareStatement = null;
		ResultSet resultSet = null;
		
		RebacRelationIdentifier rebacRelation = null;
		
	 	try {
	 		
	 		policySqlQueryPrepareStatement = mysqlConn.prepareStatement(policySqlQuery);

	 		resultSet = policySqlQueryPrepareStatement.executeQuery();

			
			while (resultSet.next()) {
				rebacRelation = new RebacRelationIdentifier(resultSet.getInt("source_id"), resultSet.getInt("destination_id"), resultSet.getInt("rebac_relationship_type_id"));
			}
			
		} catch (SQLException e) {
			 e.printStackTrace();
			 
		} finally {
			DbUtils.closeQuietly(mysqlConn);
			DbUtils.closeQuietly(resultSet);
			DbUtils.closeQuietly(policySqlQueryPrepareStatement);
		}
		
		return rebacRelation;
		
	}
	
	
	
	
	public int createArtificialPolicy(int numberOfRelationships, List<Long> executionTimeList,  boolean authorizationResult) {
		
		List<RebacRelationIdentifier> rebacRelationList = getBinaryRelations(NumberofRecords, numberOfRelationships);
		
		List<AtomicValue> atomicValueList = new ArrayList<AtomicValue>();
		
		for(int i=0; i< numberOfRelationships; i++) {
			
			for(int j=i+1; j<numberOfRelationships; j++) {
				
				if(i == j-1) {
					
					List<AllenRelation> allenRelationsList = new ArrayList<AllenRelation>();
					allenRelationsList.addAll(Arrays.asList(overlappingAllenRelationsList));
					Collections.shuffle(allenRelationsList);
					
					Random random = new Random();
					int allenRelationsCount = random.nextInt(10);
					
					Set<AllenRelation> allenRelations = new HashSet<AllenRelation>();
					
					for(int k=0; k < allenRelationsCount; k++) {
						allenRelations.add(allenRelationsList.get(k));
					}

					AtomicValue atomicValue = new AtomicValueImpl(rebacRelationList.get(i), rebacRelationList.get(j), allenRelations);
					
					atomicValueList.add(atomicValue);
				}
			}
		}
		
		
		
		PolicyPrefix policyPrefix = generatePolicyPrefix(numberOfRelationships, rebacRelationList);
		
		Matrix matrix = generateMatrix(atomicValueList);
		
		PolicyImpl policy = new PolicyImpl(policyPrefix, matrix);
		
		PolicyToSqlTraslator policyToSqlTraslator = new PolicyToSqlTraslator();
		String policySqlString = policyToSqlTraslator.policyTranslator(policy);
		
//		System.out.println(policySqlString);
		
		int resultRowsCount = 0 ;
		
		try {
			// execute policy sql
			PolicyExecute policyExecute = new PolicyExecute();
			
			long startTime = System.currentTimeMillis();
			resultRowsCount = policyExecute.executePolicySql(mysqlConn, policySqlString);
			long endTime   = System.currentTimeMillis();
			long totalTime = endTime - startTime;
			System.out.println("Execution time (milliseconds): "+ totalTime);	
			System.out.println("Result count: "+ resultRowsCount);
			
			System.out.println("Total time less than timeout time");
		
			if(!(resultRowsCount == -1)) {
				executionTimeList.add(totalTime);
			}
			
//			if(authorizationResult) {
//				if(resultRowsCount > 0) {
//					executionTimeList.add(totalTime);
//				}
//			} else {
//				
//				if(resultRowsCount == 0) {
//					executionTimeList.add(totalTime);
//				}
//			}
			
		} catch(Exception e) {
			System.out.println("Exception: "+ e);
		}
		
		
		return resultRowsCount;
		
	}
	
	public static PolicyPrefix generatePolicyPrefix(int numberOfBinaryRelationships, List<RebacRelationIdentifier> rebacRelationList) {
		
		ExistentialQuantifier existentialQuantifierDesignated = ExistentialQuantifier.Regular;
		ExistentialQuantifier existentialQuantifierOngoing = ExistentialQuantifier.Current;
		
		Random random = new Random();
		int existentialQuantifierCount = random.nextInt(2);
		
		if(existentialQuantifierCount == 0) {
			
			if(numberOfBinaryRelationships == 1) {
				return new PolicyPrefixImpl(existentialQuantifierDesignated, null, rebacRelationList.get(numberOfBinaryRelationships-1), null);
			
			} else {
				
				return new PolicyPrefixImpl(existentialQuantifierDesignated, null, rebacRelationList.get(numberOfBinaryRelationships-1), generatePolicyPrefix(numberOfBinaryRelationships-1, rebacRelationList));
			}
			
		} else {
			
			if(numberOfBinaryRelationships == 1) {
				return new PolicyPrefixImpl(existentialQuantifierOngoing, null, rebacRelationList.get(numberOfBinaryRelationships-1), null);
			
			} else {
				
				return new PolicyPrefixImpl(existentialQuantifierOngoing, null, rebacRelationList.get(numberOfBinaryRelationships-1), generatePolicyPrefix(numberOfBinaryRelationships-1, rebacRelationList));
			}
			
		}
		
		
		
	}
	
	
	public static Matrix generateMatrix(List<AtomicValue> atomicValueList) {
		
		Collections.shuffle(atomicValueList);
		
		Matrix matrix = atomicValueList.get(0);
		
		atomicValueList.remove(atomicValueList.size()-1);
		
		while(atomicValueList.size() > 0 ) {
			
			Collections.shuffle(atomicValueList);

			Random random = new Random();
			int matrixOperationCount = random.nextInt(6);
			
			if(matrixOperationCount == 0) {
				matrix =  new TrueMatrixImpl();
			
			} else if(matrixOperationCount == 1) {
				matrix = new FalseMatrixImpl();
			
			} else if(matrixOperationCount == 2) {
				matrix =  new ConjunctionMatrixImpl(atomicValueList.get(0), matrix);
				atomicValueList.remove(0);
				
			} else if(matrixOperationCount == 3) {
				matrix =  new DisjunctionMatrixImpl(atomicValueList.get(0), matrix);
				atomicValueList.remove(0);
			
			} else if(matrixOperationCount == 4) {
				matrix =  new NegationMatrixImpl(matrix);
			
			} else if(matrixOperationCount == 5) {
				matrix =  new AtomicValueImpl(atomicValueList.get(0));
				atomicValueList.remove(0);
			}
			
			
//			if(atomicValueList.size() > 0) {
//				matrix =  new DisjunctionMatrixImpl(atomicValueList.get(0), matrix);
//				atomicValueList.remove(atomicValueList.size()-1);
//			}
			
//			if(atomicValueList.size() > 0) {
//				matrix =  new TrueMatrixImpl();
//				atomicValueList.remove(atomicValueList.size()-1);
//			}
			
			
//			if(atomicValueList.size() > 0) {
//				matrix =  new NegationMatrixImpl(matrix);
//			}
			
//		Collections.shuffle(atomicValueList);
//		matrix = new DisjunctionMatrixImpl(atomicValueList.get(0), matrix);
//		atomicValueList.remove(atomicValueList.size()-1);
					
//			if(randValue <= 1) {
//				
////				Collections.shuffle(atomicValueList);
////				matrix = new DisjunctionMatrixImpl(atomicValueList.get(0), matrix);
////				atomicValueList.remove(atomicValueList.size()-1);
//			
//			} else if(randValue < 15 && randValue > 10) {
//				
////				Collections.shuffle(atomicValueList);
////				matrix = new NegationMatrixImpl(matrix);
////				atomicValueList.remove(atomicValueList.size()-1);
//			
//			} else {
//				Collections.shuffle(atomicValueList);
//				matrix =  new ConjunctionMatrixImpl(atomicValueList.get(0), matrix);
//				atomicValueList.remove(atomicValueList.size()-1);
//			}
		}
		
		return matrix;
	}
		
}
