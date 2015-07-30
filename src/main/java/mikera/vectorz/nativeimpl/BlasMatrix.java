package mikera.vectorz.nativeimpl;

import java.nio.DoubleBuffer;

import mikera.indexz.Index;
import mikera.matrixx.AMatrix;
import mikera.matrixx.impl.ARectangularMatrix;
import mikera.matrixx.impl.AStridedMatrix;
import mikera.matrixx.impl.StridedMatrix;

import static mikera.vectorz.nativeimpl.BlasInstance.*;

/**
 * A matrix class implemented using a java.nio.DoubleBuffer
 * 
 * Intended for use with native libraries that require interop with buffer memory
 * 
 * @author Mike
 *
 */
public class BlasMatrix extends AStridedMatrix {

	private final int rowStride;
	private final int colStride;
	private final int offset;

	private BlasMatrix(double[] data, int rowCount, int columnCount,
			int offset, int rowStride, int columnStride) {
		super(data,rowCount,columnCount);
		this.offset = offset;
		this.rowStride = rowStride;
		this.colStride = columnStride;
	}

	public static BlasMatrix create(int rowCount, int columnCount) {
		double[] data = new double[rowCount * columnCount];
		return new BlasMatrix(data, rowCount, columnCount, 0, columnCount, 1);
	}
	
	/**
	 * Wraps a row-major ordered array as a BlasMatrix
	 * @param source
	 * @param rows
	 * @param cols
	 * @return
	 */
	public static BlasMatrix wrap(double[] source, int rows, int cols) {
		if (source.length!=(rows*cols)) throw new IllegalArgumentException("Wrong array size for matrix of shape "+Index.of(rows,cols));
		return new BlasMatrix(source,rows,cols,0,cols,1);
	}
	
	public static BlasMatrix create(AMatrix m) {
		return wrap(m.toDoubleArray(),m.rowCount(),m.columnCount());
	}

	@Override
	public double get(int i, int j) {
		checkIndex(i,j);
		return unsafeGet(i,j);
	}
	
	@Override
	public void set(int i, int j, double value) {
		checkIndex(i,j);
		unsafeSet(i,j,value);
	}
	
	@Override
	public double unsafeGet(int i, int j) {
		return data[index(i,j)];
	}

	@Override
	public void unsafeSet(int i, int j, double value) {
		data[index(i,j)]=value;
	}

	@Override
	public boolean isFullyMutable() {
		return true;
	}
	
	@Override
	public BlasMatrix clone() {
		return exactClone();
	}

	@Override
	public BlasMatrix exactClone() {
		return new BlasMatrix(data.clone(),rows,cols,offset,rowStride,colStride);
	}

	@Override
	public boolean isZero() {
		return asVector().isZero();
	}

	@Override
	public int getArrayOffset() {
		return offset;
	}

	@Override
	public int rowStride() {
		return rowStride;
	}

	@Override
	public int columnStride() {
		return colStride;
	}

	@Override
	public void copyRowTo(int row, double[] dest, int destOffset) {
		blas.dcopy(cols, data, offset+row*rowStride, colStride, dest, destOffset, 1);
	}

	@Override
	public void copyColumnTo(int col, double[] dest, int destOffset) {
		blas.dcopy(rows, data, offset+col*colStride, rowStride, dest, destOffset, 1);
	}

	@Override
	protected int index(int i, int j) {
		return offset+i*rowStride+j*colStride;
	}
}
