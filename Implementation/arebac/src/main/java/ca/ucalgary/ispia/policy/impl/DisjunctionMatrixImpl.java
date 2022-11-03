package ca.ucalgary.ispia.policy.impl;

import ca.ucalgary.ispia.policy.opt.DisjunctionMatrix;
import ca.ucalgary.ispia.policy.opt.Matrix;

public class DisjunctionMatrixImpl implements DisjunctionMatrix{

	private Matrix matrixA;
	private Matrix matrixB;
	
	public DisjunctionMatrixImpl(Matrix matrixA, Matrix matrixB) {
		this.setMatrixA(matrixA);
		this.setMatrixB(matrixB);
	}
	
	@Override
	public Matrix getMatrixA() {
		// TODO Auto-generated method stub
		return this.matrixA;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + "disjunctionMatrix".hashCode();
		result = prime * result + ((matrixA == null) ? 0 : matrixA.hashCode());
		result = prime * result + ((matrixB == null) ? 0 : matrixB.hashCode());
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
		DisjunctionMatrixImpl other = (DisjunctionMatrixImpl) obj;
		if (matrixA == null) {
			if (other.matrixA != null)
				return false;
		} else if (!matrixA.equals(other.matrixA))
			return false;
		if (matrixB == null) {
			if (other.matrixB != null)
				return false;
		} else if (!matrixB.equals(other.matrixB))
			return false;
		return true;
	}

	@Override
	public Matrix getMatrixB() {
		// TODO Auto-generated method stub
		return this.matrixB;
	}

	public void setMatrixA(Matrix matrixA) {
		this.matrixA = matrixA;
	}

	public void setMatrixB(Matrix matrixB) {
		this.matrixB = matrixB;
	}

}
