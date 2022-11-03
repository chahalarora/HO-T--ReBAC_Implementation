package ca.ucalgary.ispia.policy.impl;

import ca.ucalgary.ispia.policy.opt.Matrix;
import ca.ucalgary.ispia.policy.opt.Policy;
import ca.ucalgary.ispia.policy.opt.PolicyPrefix;

public class PolicyImpl implements Policy {

	private PolicyPrefix policyPrefix;
	private Matrix matrix;
	
	public PolicyImpl(PolicyPrefix policyPrefix, Matrix matrix) {
		this.policyPrefix = policyPrefix;
		this.matrix = matrix;
	}
	
	public PolicyPrefix getPolicyPrefix() {
		return policyPrefix;
	}
	public void setPolicyPrefix(PolicyPrefix policyPrefix) {
		this.policyPrefix = policyPrefix;
	}
	public Matrix getMatrix() {
		return matrix;
	}
	public void setMatrix(Matrix matrix) {
		this.matrix = matrix;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((matrix == null) ? 0 : matrix.hashCode());
		result = prime * result + ((policyPrefix == null) ? 0 : policyPrefix.hashCode());
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
		PolicyImpl other = (PolicyImpl) obj;
		if (matrix == null) {
			if (other.matrix != null)
				return false;
		} else if (!matrix.equals(other.matrix))
			return false;
		if (policyPrefix == null) {
			if (other.policyPrefix != null)
				return false;
		} else if (!policyPrefix.equals(other.policyPrefix))
			return false;
		return true;
	}
	

}
