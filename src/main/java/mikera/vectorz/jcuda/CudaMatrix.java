package mikera.vectorz.jcuda;

import jcuda.Pointer;
import jcuda.Sizeof;
import jcuda.jcublas.JCublas;
import mikera.matrixx.AMatrix;
import mikera.matrixx.impl.ARectangularMatrix;

public class CudaMatrix extends ARectangularMatrix {
	private static final long serialVersionUID = 7746497731642370173L;

	Pointer data;
	
	protected CudaMatrix(int rows, int cols) {
		super(rows, cols);
		JCublas.cublasAlloc(rows * cols, Sizeof.DOUBLE, data);
	}

	@Override
	public double get(int row, int column) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void set(int row, int column, double value) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isFullyMutable() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public AMatrix exactClone() {
		// TODO Auto-generated method stub
		return null;
	}

}
