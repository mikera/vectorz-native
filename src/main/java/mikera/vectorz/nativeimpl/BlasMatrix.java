package mikera.vectorz.nativeimpl;

import java.nio.DoubleBuffer;

import mikera.indexz.Index;
import mikera.matrixx.AMatrix;
import mikera.matrixx.impl.ARectangularMatrix;
import mikera.matrixx.impl.AStridedMatrix;
import mikera.matrixx.impl.BaseStridedMatrix;
import mikera.matrixx.impl.StridedMatrix;

import static mikera.vectorz.nativeimpl.BlasInstance.*;

/**
 * A matrix class implemented using netlib-java BLAS operations
 * standard Java operations are still used where these are faster
 * 
 * @author Mike
 *
 */
public class BlasMatrix extends BaseStridedMatrix {

	private BlasMatrix(double[] data, int rowCount, int columnCount,
			int offset, int rowStride, int columnStride) {
		super(data,rowCount,columnCount,offset,rowStride,columnStride);
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
	public BlasVector getRow(int i) {
		return BlasVector.wrapStrided(cols, data, index(i,0), colStride);	
	}
	
	@Override
	public BlasVector getColumn(int i) {
		return BlasVector.wrapStrided(rows, data, index(0,i), rowStride);	
	}
	
	@Override
	public void set(int i, int j, double value) {
		checkIndex(i,j);
		unsafeSet(i,j,value);
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
	public void copyRowTo(int row, double[] dest, int destOffset) {
		blas.dcopy(cols, data, offset+row*rowStride, colStride, dest, destOffset, 1);
	}

	@Override
	public void copyColumnTo(int col, double[] dest, int destOffset) {
		blas.dcopy(rows, data, offset+col*colStride, rowStride, dest, destOffset, 1);
	}
}
