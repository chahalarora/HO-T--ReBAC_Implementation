package ca.ucalgary.ispia.policy.impl;

import ca.ucalgary.ispia.policy.opt.TrueMatrix;

public class TrueMatrixImpl implements TrueMatrix {
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + "trueMatrix".hashCode();
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
