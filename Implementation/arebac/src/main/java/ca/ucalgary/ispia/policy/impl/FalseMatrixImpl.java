package ca.ucalgary.ispia.policy.impl;

import ca.ucalgary.ispia.policy.opt.FalseMatrix;

public class FalseMatrixImpl implements FalseMatrix {

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + "falseMatrix".hashCode();
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
		return true;
	}
}
