package mikera.vectorz.nativeimpl;

import static mikera.vectorz.nativeimpl.BlasInstance.blas;

import mikera.indexz.Index;
import mikera.matrixx.AMatrix;
import mikera.matrixx.impl.AStridedMatrix;
import mikera.matrixx.impl.BaseStridedMatrix;
import mikera.matrixx.impl.DenseColumnMatrix;
import mikera.vectorz.AVector;
import mikera.vectorz.Vector;
import mikera.vectorz.impl.AStridedVector;
import mikera.vectorz.util.DoubleArrays;
import mikera.vectorz.util.ErrorMessages;

/**
 * A matrix class implemented using netlib-java BLAS operations
 * standard Java operations are still used where these are faster
 * 
 * @author Mike
 *
 */
public class BlasMatrix extends BaseStridedMatrix {
	private static final long serialVersionUID = 3922540890838969427L;

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
	
	public static BlasMatrix wrap(double[] source, int rows, int cols, int offset, int rowStride, int columnStride) {
		if (source.length!=(rows*cols)) throw new IllegalArgumentException("Wrong array size for matrix of shape "+Index.of(rows,cols));
		return new BlasMatrix(source,rows,cols,offset,rowStride,columnStride);
	}
	
	public static BlasMatrix create(AMatrix m) {
		return wrap(m.toDoubleArray(),m.rowCount(),m.columnCount());
	}
	
	/**
	 * Wraps data wrom source matrix in a BlasMatrix if possible, otherwise creates a nwew BlasMatrix
	 * @param m
	 * @return
	 */
	public static BlasMatrix wrapOrCreate(AMatrix m) {
		if (m instanceof AStridedMatrix) {
			AStridedMatrix sm=(AStridedMatrix)m;
			if (sm.getArrayOffset()==0) {
				if (sm.columnStride()==1) {
					return new BlasMatrix(sm.getArray(),m.rowCount(),m.columnCount(),0,sm.rowStride(),1);
				}
				if (sm.rowStride()==1) {
					return new BlasMatrix(sm.getArray(),m.rowCount(),m.columnCount(),0,1,sm.columnStride());
				}
			}
		}
		return create(m);
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
	
//	@Override
//	public double determinant() {
//		// TODO figure out best way to do this
//	}
	
	@Override
	public Vector innerProduct(Vector v) {
		BlasVector result= innerProduct((AStridedVector)v);
		return Vector.wrap(result.getArray());
	}
	
	public BlasVector innerProduct(AStridedVector v) {
		// ensure vector is in zero-offset format
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
	public BlasMatrix innerProduct(AMatrix a) {
		return innerProduct(wrapOrCreate(a));
	}
	
	public BlasMatrix innerProduct(AStridedMatrix a) {
		// ensure vector is in zero-offset format
		if (!((a.getArrayOffset()==0)&&(a.rowStride()==1))) a=DenseColumnMatrix.create(a);
		if (cols!=a.rowCount()) throw new Error(ErrorMessages.incompatibleShapes(this, a));
		int n=a.columnCount();
		int m=rows;
		int k=cols;
		
		if (this.isPackedArray()) {
			// row major format
			double[] dest=new double[m*n];
			double[] src=a.getArray();
			blas.dgemm("T","N", m, n, k, 1.0, data, k, src, k, 0.0, dest, m);
			return BlasMatrix.wrap(dest,m,n,0,1,rows);
		} else if ((this.getArrayOffset()==0) && (this.rowStride()==1)) {
			// column major format
			// row major format
			double[] dest=new double[m*n];
			double[] src=a.getArray();
			blas.dgemm("N","N", m, n, k, 1.0, data, m, src, k, 0.0, dest, m);
			return BlasMatrix.wrap(dest,m,n,0,1,rows);
		} else {
			return BlasMatrix.create(this).innerProduct(a);
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
		for (int i=0; i<rows; i++) {
			if (!DoubleArrays.isZero(data, index(i,0), cols, colStride)) return false;
		}
		return true;
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
