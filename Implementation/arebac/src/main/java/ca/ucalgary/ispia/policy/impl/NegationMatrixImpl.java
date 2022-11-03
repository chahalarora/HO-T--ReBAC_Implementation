package ca.ucalgary.ispia.policy.impl;

import ca.ucalgary.ispia.policy.opt.Matrix;
import ca.ucalgary.ispia.policy.opt.NegationMatrix;

public class NegationMatrixImpl implements NegationMatrix {

	private Matrix matrixA;
	
	public NegationMatrixImpl(Matrix matrixA) {
		this.matrixA = matrixA;
	}
	
	@Override
	public Matrix getMatrixA() {
		// TODO Auto-generated method stub
		return matrixA;
	}
	
	public void setMatrixA(Matrix matrixA) {
		this.matrixA = matrixA;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((matrixA == null) ? 0 : matrixA.hashCode());
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
		NegationMatrixImpl other = (NegationMatrixImpl) obj;
		if (matrixA == null) {
			if (other.matrixA != null)
				return false;
		} else if (!matrixA.equals(other.matrixA))
			return false;
		return true;
	}

	

}
