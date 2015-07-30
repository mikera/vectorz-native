package mikera.vectorz.nativeimpl;

import java.nio.DoubleBuffer;

import mikera.indexz.Index;
import mikera.matrixx.AMatrix;
import mikera.matrixx.impl.ARectangularMatrix;
import mikera.matrixx.impl.AStridedMatrix;
import mikera.matrixx.impl.BaseStridedMatrix;
import mikera.matrixx.impl.StridedMatrix;
import mikera.vectorz.AVector;
import mikera.vectorz.Vector;
import mikera.vectorz.impl.AStridedVector;

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
	
	public static BlasMatrix wrap(AStridedMatrix m) {
		return new BlasMatrix(m.getArray(),m.rowCount(),m.columnCount(),m.getArrayOffset(),m.rowStride(),m.columnStride());
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
	public BlasMatrix getTranspose() {
		return new BlasMatrix(data,cols,rows,offset,colStride,rowStride);
	}
	
	@Override
	public BlasVector innerProduct(AVector v) {
		if (v instanceof AStridedVector) {
			return innerProduct((AStridedVector)v);
		} else {
			return innerProduct((AStridedVector)Vector.create(v));
		}
	}
	
	@Override
	public Vector innerProduct(Vector v) {
		BlasVector result= innerProduct((AStridedVector)v);
		return Vector.wrap(result.getArray());
	}
	
	public BlasVector innerProduct(AStridedVector v) {
		boolean voffset=v.getArrayOffset()==0;
		if (!voffset) v=Vector.create(v);
		if (this.isPackedArray()) {
			// row major format
			double[] dest=new double[rows];
			double[] src=v.getArray();
			blas.dgemv("T", cols, rows, 1.0, data, cols, src, v.getStride(), 0.0, dest, 1);
			return BlasVector.wrap(dest);
		} else if ((this.getArrayOffset()==0) && (this.rowStride()==1)) {
			// column major format
			double[] dest=new double[rows];
			double[] src=v.getArray();
			blas.dgemv("N", rows, cols, 1.0, data, rows, src, v.getStride(), 0.0, dest, 1);		
			return BlasVector.wrap(dest);
		} else {
			return BlasMatrix.create(this).innerProduct(v);
		}
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
