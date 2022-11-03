package ca.ucalgary.ispia.policy.opt;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import ca.ucalgary.ispia.policy.impl.AtomicValueImpl;
import ca.ucalgary.ispia.policy.impl.ConjunctionMatrixImpl;
import ca.ucalgary.ispia.policy.impl.DisjunctionMatrixImpl;
import ca.ucalgary.ispia.policy.impl.FalseMatrixImpl;
import ca.ucalgary.ispia.policy.impl.NegationMatrixImpl;
import ca.ucalgary.ispia.policy.impl.TrueMatrixImpl;

public class PolicyToSqlTraslator {
	
	private Map<RebacRelationIdentifier, String>  rebacRelationIdentifierSqlReferenceMap = new HashMap<RebacRelationIdentifier, String>();
	
	private AllenRelation[] allenRelations = new AllenRelation[] {AllenRelation.contains, AllenRelation.during, AllenRelation.equals, AllenRelation.finishedBy, AllenRelation.finishes, AllenRelation.meets, AllenRelation.metBy, AllenRelation.overlappedBy, AllenRelation.overlaps, AllenRelation.precededBy, AllenRelation.precedes, AllenRelation.startedBy, AllenRelation.starts};
	
//	private AllenRelation[] allenRelations = new AllenRelation[] {AllenRelation.equals, AllenRelation.overlaps};
	
	public String policyTranslator(Policy policy) {
				
		
		String sqlString = "";
		
		//getting matrix in negative normal form.
		
		
		sqlString += getSelectString();
		
		sqlString += getPrefixTranslation(policy.getPolicyPrefix());
		
		if(policy.getMatrix() != null) {
			Matrix nnfMatrix = preProcessPolicy(policy.getMatrix());
			sqlString += " AND " + getMatrixSqlTranslation(nnfMatrix);
		}
		
		sqlString += " ; ";
		
//		System.out.println(sqlString);
		
		
		return sqlString;
		
	}
	
	
	public Matrix preProcessPolicy(Matrix matrix) {
		
		
		if(matrix instanceof AtomicValue) {
			
			AtomicValue atomicValue = (AtomicValue) matrix;
			return atomicValue;
			
		} else if(matrix instanceof TrueMatrix) {
			
			return new TrueMatrixImpl();
			
		} else if(matrix instanceof FalseMatrix) {
			
			return new FalseMatrixImpl();
			
		}else if(matrix instanceof DisjunctionMatrix) {
			
			DisjunctionMatrix disjunctionMatrix = (DisjunctionMatrix) matrix;
		 	return new DisjunctionMatrixImpl(preProcessPolicy(disjunctionMatrix.getMatrixA()), preProcessPolicy(disjunctionMatrix.getMatrixB()));
			
		} else if(matrix instanceof ConjunctionMatrix) {
			
			ConjunctionMatrix conjunctionMatrix = (ConjunctionMatrix) matrix;
			return new ConjunctionMatrixImpl(preProcessPolicy(conjunctionMatrix.getMatrixA()), preProcessPolicy(conjunctionMatrix.getMatrixB()));
		
		}	else {
			
			NegationMatrix negationMatrix = (NegationMatrix) matrix;
			return processNegation(negationMatrix.getMatrixA()) ;
		}
		
	}
	
	public Matrix processNegation(Matrix negateMatrix) {
		
		if(negateMatrix instanceof AtomicValue) {
			
			AtomicValue atomicValue = (AtomicValue) negateMatrix;
			Set<AllenRelation> allenRelationsSet = new HashSet<AllenRelation>();
			allenRelationsSet.addAll(Arrays.asList(allenRelations));
			allenRelationsSet.removeAll(atomicValue.getAllenRelations());
			return new AtomicValueImpl(atomicValue.getFirstRebacRelation(), atomicValue.getSecondRebacRelation(), allenRelationsSet) ;
			
		} else if(negateMatrix instanceof TrueMatrix) {
			
			return new FalseMatrixImpl();
			
		} else if(negateMatrix instanceof FalseMatrix) {
			
			return new TrueMatrixImpl();
			
		} else if(negateMatrix instanceof DisjunctionMatrix) {
			
			DisjunctionMatrix disjunctionMatrix = (DisjunctionMatrix) negateMatrix;
		 	return new ConjunctionMatrixImpl(preProcessPolicy(new NegationMatrixImpl(disjunctionMatrix.getMatrixA())), preProcessPolicy(new NegationMatrixImpl(disjunctionMatrix.getMatrixB())));
			
		} else if(negateMatrix instanceof ConjunctionMatrix) {
			
			ConjunctionMatrix conjunctionMatrix = (ConjunctionMatrix) negateMatrix;
			return new DisjunctionMatrixImpl(preProcessPolicy(new NegationMatrixImpl(conjunctionMatrix.getMatrixA())), preProcessPolicy(new NegationMatrixImpl(conjunctionMatrix.getMatrixB())));
		
		} else {
			
			NegationMatrix negationMatrix = (NegationMatrix) negateMatrix;
			return preProcessPolicy(negationMatrix.getMatrixA());

		}
	}
	
	
	public String getSelectString() {
		return "SELECT Count(*) ";
	}
	
	public String getPrefixTranslation(PolicyPrefix policyPrefix) {
		
		String prefixFromString = "";
		
		String prefixWhereString = "";
		
		String resultString = "";
		
		int count = 0;
		
		while(policyPrefix != null) {
			
			prefixFromString += " official_periods as r"+count +" ";
			
			if(policyPrefix.getExistentialQuantifier().equals(ExistentialQuantifier.Current)) {
				prefixWhereString += "r"+count+".source_id = "+ policyPrefix.getRebacRelationIdentifier().getSourceId() + " AND ";
				prefixWhereString += "r"+count+".destination_id = "+ policyPrefix.getRebacRelationIdentifier().getDestinationId() + " AND ";
				prefixWhereString += "r"+count+".rebac_relationship_type_id = " + policyPrefix.getRebacRelationIdentifier().getRelationshipId() + " AND " ;
				prefixWhereString += "r"+count+".end_time = 2147483647";
			
			} else {
				prefixWhereString += "r"+count+".source_id = "+ policyPrefix.getRebacRelationIdentifier().getSourceId() + " AND ";
				prefixWhereString += "r"+count+".destination_id = "+ policyPrefix.getRebacRelationIdentifier().getDestinationId() + " AND ";
				prefixWhereString += "r"+count+".rebac_relationship_type_id = " + policyPrefix.getRebacRelationIdentifier().getRelationshipId();
			}
			
			rebacRelationIdentifierSqlReferenceMap.put(policyPrefix.getRebacRelationIdentifier(), "r"+count);
			
			count++;
			
			policyPrefix = policyPrefix.getNextPolicyPrefix();
			
			if(policyPrefix != null) {
				prefixFromString += " INNER JOIN ";
				prefixWhereString += " AND ";
			}
			
		}
		
		resultString = "FROM " + prefixFromString + " WHERE " + prefixWhereString;
		
		return resultString;
		
	}
	
	public String getMatrixSqlTranslation(Matrix matrix) {
		String resultString = "";
		
		if(matrix instanceof AtomicValue) {

			AtomicValue atomicValue = (AtomicValue) matrix;

			String firstReference = rebacRelationIdentifierSqlReferenceMap.get(atomicValue.getFirstRebacRelation());
			String secondReference = rebacRelationIdentifierSqlReferenceMap.get(atomicValue.getSecondRebacRelation());
			
			// if two intervals has no Allen relation between them.
			if(atomicValue.getAllenRelations().size() == 0) {
				
				resultString += "( False )";
				
			} else {
				
				// if two intervals has at least one Allen relation between them.
				
				Iterator<AllenRelation> allenRelationIterator = atomicValue.getAllenRelations().iterator();
				
				resultString += "( ";
				
				while(allenRelationIterator.hasNext()) {
					
					AllenRelation allenRelation = allenRelationIterator.next();
					resultString += allenRelationString(firstReference, secondReference, allenRelation);
					
					if(allenRelationIterator.hasNext()) {
						resultString += " OR ";
					}
				}
				
				resultString += " )";
				
			}
			
			
		} else if(matrix instanceof DisjunctionMatrix) {
			
			DisjunctionMatrix disjunctionMatrix = (DisjunctionMatrix) matrix;
			resultString += "( " + getMatrixSqlTranslation(disjunctionMatrix.getMatrixA()) + " OR " + getMatrixSqlTranslation(disjunctionMatrix.getMatrixB()) + " )";
		
		} else if(matrix instanceof ConjunctionMatrix) {
			
			ConjunctionMatrix conjunctionMatrix = (ConjunctionMatrix) matrix;
			resultString += "( " + getMatrixSqlTranslation(conjunctionMatrix.getMatrixA()) + " AND " + getMatrixSqlTranslation(conjunctionMatrix.getMatrixB()) + " )";
		
		}	else if(matrix instanceof NegationMatrix) {
			
			NegationMatrix negationMatrix = (NegationMatrix) matrix;
			resultString += "( " + " NOT " + getMatrixSqlTranslation(negationMatrix.getMatrixA()) + " )";
		
		} else if(matrix instanceof TrueMatrix) {
			
			resultString += "( " + " TRUE " + " )";
		
		} else if(matrix instanceof FalseMatrix) {
			
			resultString += "( " + " FALSE " + " )";
		} 
		
		
		return resultString;
	}
	
	
	public String allenRelationString(String firstReference, String secondReference, AllenRelation allenRelation) {
		
		String resultString = "";
		
		if(allenRelation.equals(AllenRelation.precedes)) {
			
			resultString += "( "+firstReference+".end_time < "+ secondReference+".start_time"+" )";
		
		} else if(allenRelation.equals(AllenRelation.meets)) {	
			
			resultString += "( "+firstReference+".end_time = "+ secondReference+".start_time"+" )";

		} else if(allenRelation.equals(AllenRelation.overlaps)) {	
			
			resultString += "( "+secondReference+".start_time > "+firstReference+".start_time AND "+secondReference+".start_time < "+ firstReference +".end_time AND "+ secondReference + ".end_time > "+ firstReference +".end_time"+" )";
		
		} else if(allenRelation.equals(AllenRelation.finishedBy)) {	
			
			resultString += "( "+secondReference+".start_time > "+firstReference+".start_time AND "+secondReference+".end_time = "+ firstReference +".end_time"+" )";
		
		} else if(allenRelation.equals(AllenRelation.contains)) {	
			
			resultString += "( "+secondReference+".start_time > "+firstReference+".start_time AND "+secondReference+".end_time < "+ firstReference +".end_time"+" )";
		
		} else if(allenRelation.equals(AllenRelation.starts)) {	
			
			resultString += "( "+secondReference+".start_time = "+firstReference+".start_time AND "+secondReference+".end_time > "+ firstReference +".end_time"+" )";
		
		} else if(allenRelation.equals(AllenRelation.equals)) {	
			
			resultString += "( "+secondReference+".start_time = "+firstReference+".start_time AND "+secondReference+".end_time = "+ firstReference +".end_time"+" )";
		
		} else if(allenRelation.equals(AllenRelation.startedBy)) {	
			
			resultString += "( "+secondReference+".start_time = "+firstReference+".start_time AND "+secondReference+".end_time < "+ firstReference +".end_time"+" )";
		
		} else if(allenRelation.equals(AllenRelation.during)) {	
			
			resultString += "( "+secondReference+".start_time < "+firstReference+".start_time AND "+secondReference+".end_time > "+ firstReference +".end_time"+" )";
		
		} else if(allenRelation.equals(AllenRelation.finishes)) {	
			
			resultString += "( "+secondReference+".start_time < "+firstReference+".start_time AND "+secondReference+".end_time = "+ firstReference +".end_time"+" )";
		
		} else if(allenRelation.equals(AllenRelation.overlappedBy)) {	
			
			resultString += "( "+secondReference+".start_time < "+firstReference+".start_time AND "+secondReference+".end_time > "+ firstReference +".start_time AND "+ secondReference + ".end_time < "+ firstReference +".end_time"+" )";
		
		} else if(allenRelation.equals(AllenRelation.metBy)) {	
			
			resultString += "( "+secondReference+".end_time = "+firstReference+".start_time "+ " )";
		
		} else if(allenRelation.equals(AllenRelation.precededBy)) {	
			
			resultString += "( "+secondReference+".end_time < "+firstReference+".start_time "+ " )";
		
		}
		
		return resultString;
	}
	
}
