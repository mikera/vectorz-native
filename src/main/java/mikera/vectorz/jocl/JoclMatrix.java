package mikera.vectorz.jocl;

import static org.jocl.CL.CL_TRUE;

import org.jocl.CL;
import org.jocl.Pointer;
import org.jocl.Sizeof;

import mikera.matrixx.AMatrix;
import mikera.matrixx.impl.ARectangularMatrix;
import mikera.vectorz.Tools;

@SuppressWarnings("serial")
public class JoclMatrix extends ARectangularMatrix {
	private final DeviceMem data;

	public static JoclMatrix newMatrix(int rows, int cols) {
		return new JoclMatrix(rows,cols);
	}
	
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

	public static JoclMatrix create(JoclMatrix a) {
		JoclMatrix result= new JoclMatrix(a.rows,a.cols,a.data);
		return result;
	}

	
	protected JoclMatrix(int rows, int cols) {
		super(rows, cols);
		int n=Tools.toInt(rows*cols);
		data=new DeviceMem(n);
	}
	
	protected JoclMatrix(int rows, int cols, DeviceMem src) {
		this(rows, cols);
		int n=Tools.toInt(rows*cols);
		CL.clEnqueueCopyBuffer(JoclContext.commandQueue(),src.mem,data.mem,0,0,n*Sizeof.cl_double,0,null,null);
	}

	@Override
	public double get(int row, int column) {
		checkIndex(row,column);
		int offset=column+rows*row;
		double[] result=new double[1];
		Pointer dst=Pointer.to(result);
		CL.clEnqueueReadBuffer(JoclContext.commandQueue(), data.mem, CL_TRUE, offset*Sizeof.cl_double, Sizeof.cl_double, dst, 0, null, null);
		return result[0];
	}
	
	@Override
	public void add(AMatrix a) {
		if (a instanceof JoclMatrix) {
			
		}
		
	}

	@Override
	public void set(int row, int column, double value) {
		checkIndex(row,column);
		int offset=column+rows*row;
		double[] buff=new double[1];
		buff[0]=value;
		Pointer src=Pointer.to(buff);
		CL.clEnqueueWriteBuffer(JoclContext.commandQueue(), data.mem, CL_TRUE, offset*Sizeof.cl_double, Sizeof.cl_double, src, 0, null, null);
	}
	
	@Override
	public void setElements(double[] source, int offset) {
		Pointer src=Pointer.to(source).withByteOffset(offset*Sizeof.cl_double);
		int ec=(int)elementCount();
		CL.clEnqueueWriteBuffer(JoclContext.commandQueue(), data.mem, CL_TRUE, 0, ec*Sizeof.cl_double, src, 0, null, null);		
	}

	@Override
	public boolean isFullyMutable() {
		return true;
	}

	@Override
	public AMatrix exactClone() {
		return new JoclMatrix(rows,cols,data);
	}
}
