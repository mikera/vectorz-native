package mikera.vectorz.jocl;

import mikera.arrayz.INDArray;
import mikera.matrixx.AMatrix;
import mikera.matrixx.Matrix;
import mikera.matrixx.impl.ARectangularMatrix;
import mikera.vectorz.Tools;

@SuppressWarnings("serial")
public class JoclMatrix extends ARectangularMatrix {
	private final DeviceVector data;
	
	protected JoclMatrix(int rows, int cols, DeviceVector src) {
		super(rows, cols);
		if (rows*cols!=src.length()) throw new Error("Invalid size of DeviceVector: "+src.length());
		data=src;
	}

	public static JoclMatrix newMatrix(int rows, int cols) {
		return new JoclMatrix(rows,cols);
	}
	
	/**
	 * Create a JoclMatrix as a copy of the given source array
	 * @param a
	 * @return
	 */
	public static JoclMatrix create(AMatrix a) {
		if (a instanceof JoclMatrix) {
			return create((JoclMatrix)a);
		} else {
			double[] srcArray=a.asDoubleArray();
			if (srcArray==null) srcArray=a.toDoubleArray();
			return create(a.rowCount(),a.columnCount(),srcArray);
		}
	}
	
	/**
	 * Create a JoclMatrix as a copy of the given source array
	 * @param a
	 * @return
	 */
	public static JoclMatrix create(INDArray a) {
		if (a instanceof AMatrix) {
			return create((AMatrix)a);
		} else {
			return create(Matrix.create(a));
		}
	}
	
	/**
	 * Creates a new JoclMatrix by copying the contents of the source double array
	 * @param rowCount
	 * @param columnCount
	 * @param source
	 * @return
	 */
	public static JoclMatrix create(int rowCount, int columnCount, double[] source) {
		JoclMatrix result=new JoclMatrix(rowCount,columnCount);
		result.setElements(source);
		return result;
	}
	
	/**
	 * Creates a new JoclMatrix by copying the contents of the source DeviceVector
	 * @param rowCount
	 * @param columnCount
	 * @param source
	 * @return
	 */
	public static JoclMatrix create(int rowCount, int columnCount, DeviceVector source) {
		return new JoclMatrix(rowCount,columnCount, DeviceVector.create(source));
	}

	public static JoclMatrix create(JoclMatrix a) {
		JoclMatrix result= new JoclMatrix(a.rows,a.cols,a.data);
		return result;
	}

	
	protected JoclMatrix(int rows, int cols) {
		super(rows, cols);
		int n=Tools.toInt(rows*cols);
		data=DeviceVector.createLength(n);
	}

	
	@Override
	public void fill(double value) {
		data.fill(value);
	}

	@Override
	public double get(int row, int column) {
		checkIndex(row,column);
		return data.unsafeGet(row*cols+column);
	}
	
	@Override
	public JoclVector getRow(int i) {
		return getRowView(i);
	}

	
	@Override
	public JoclVector getRowView(int i) {
		checkRow(i);
		return JoclVector.wrap(data,i*cols,cols);
	}

	@Override
	public void add(AMatrix a) {
		if (a instanceof JoclMatrix) {
			add((JoclMatrix) a);
		} else {
			add(JoclMatrix.create(a));
		}	
	}
	
	public void add(JoclMatrix a) {
		checkSameShape(a);
		data.add(a.data);
	}

	@Override
	public void set(int row, int column, double value) {
		checkIndex(row,column);
		data.unsafeSet(row*cols+column, value);
	}
	
	@Override
	public DeviceVector asVector() {
		return data;
	}
	
	@Override
	public void setElements(double[] source, int offset) {
		data.setElements(source, offset);
	}
	
	@Override
	public void getElements(double[] dest, int offset) {
		data.getElements(0,dest, offset,rows*cols);
	}

	@Override
	public boolean isFullyMutable() {
		return true;
	}

	@Override
	public AMatrix exactClone() {
		return JoclMatrix.create(rows,cols,data);
	}
}
