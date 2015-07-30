package mikera.vectorz.nativeimpl;

import mikera.vectorz.AVector;
import mikera.vectorz.impl.AStridedVector;
import mikera.vectorz.impl.BaseStridedVector;
import mikera.vectorz.impl.Vector0;

public final class BlasVector extends BaseStridedVector {

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
	public int getArrayOffset() {
		return offset;
	}

	@Override
	public int getStride() {
		return stride;
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
}
