package mikera.vectorz.jocl;

import mikera.arrayz.impl.IDense;
import mikera.vectorz.AVector;
import mikera.vectorz.Op;
import mikera.vectorz.impl.ASizedVector;
import mikera.vectorz.util.DoubleArrays;

/**
 * Abstract base class for dense Jocl vectors
 * @author Mike
 *
 */
public abstract class ADenseJoclVector extends ASizedVector implements IDense {
	private static final long serialVersionUID = -6022914163576354860L;

	protected ADenseJoclVector(int length) {
		super(length);
	}

	public abstract JoclVector getData();
	
	public abstract int getDataOffset();
	
	public JoclSubVector asJoclSubVector() {
		if (this instanceof JoclSubVector) {
			return (JoclSubVector)this;
		} else {
			return JoclSubVector.wrap(getData(), getDataOffset(), length);
		}
	}
	
	@Override
	public JoclVector dense() {
		return clone();
	}
	
	@Override
	public JoclVector clone() {
		return JoclVector.create(this);
	}
	
	@Override
	public void applyOp(Op op) {
		if (op instanceof KernelOp) {
			// fast path for KernelOps
			applyOp((KernelOp) op,0,length);
			return;
		}
		
		KernelOp kop=KernelOps.findSubstitute(op);
		if (kop!=null) {
			// use substitute kernel op
			applyOp(kop,0,length);
			return;
		} else {
			// execute in Java array, best we can do...
			double[] xs=getElements();
			if (xs.length!=length) throw new Error("Unexpected length");
			op.applyTo(xs);
			setElements(xs);
		}	
	}
	
	protected abstract void applyOp(KernelOp op, int start, int length);
	
	@Override
	public boolean equals(AVector v) {
		if (v==this) return true;
		if (v.length()!=length) return false;
		return v.equalsArray(getElements(),0);
	}
	
	@Override
	public boolean equalsArray(double[] data, int offset) {
		return DoubleArrays.equals(data, offset, getElements(), 0, length);
	}
}
