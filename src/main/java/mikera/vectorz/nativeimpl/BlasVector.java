package mikera.vectorz.nativeimpl;

import mikera.vectorz.AVector;
import mikera.vectorz.Op;
import mikera.vectorz.Vectorz;
import mikera.vectorz.impl.BaseStridedVector;
import mikera.vectorz.impl.Vector0;

public final class BlasVector extends BaseStridedVector {
	private static final long serialVersionUID = -7571795169659166258L;

	protected BlasVector(int length, double[] data, int offset, int stride) {
		super(length, data,offset,stride);
	}
	
	public static BlasVector wrap(double[] data) {
		return new BlasVector(data.length,data,0,1);
	}
	
	public static BlasVector wrapStrided(int length, double[] data, int offset, int stride) {
		return new BlasVector(length,data,offset,stride);
	}
	
	@Override
	public AVector subVector(int start, int len) {
		checkRange(start, len);
		if (len==0) return Vector0.INSTANCE;
		if (len==length) return this;
		return wrapStrided(len,data,index(start),stride);
	}

	@Override
	public AVector exactClone() {
		return new BlasVector(length,data.clone(),offset,stride);
	}

	@Override
	public void set(AVector v) {
		v.copyTo(0, data, offset, length, stride);
	}

	@Override
	public void setElements(double[] values, int offset) {
		Vectorz.wrap(values, offset, length).copyTo(0, data, this.offset, length, this.stride);
	}

	@Override
	public void setElements(int pos, double[] values, int offset, int length) {
		subVector(pos,length).set(Vectorz.wrap(values, offset, length));	
	}

	@Override
	public void addAt(int i, double v) {
		set(i,v+unsafeGet(i));
	}

	@Override
	public void applyOp(Op op) {
		op.applyTo(data, offset,stride,length);
	}

	@Override
	public void set(int i, double value) {
		checkIndex(i);
		data[index(i)]=value;
	}
}
