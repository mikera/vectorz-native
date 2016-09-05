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
	public void set(int row, int column, double value) {
		checkIndex(row,column);
		int offset=column+rows*row;
		double[] buff=new double[1];
		buff[0]=value;
		Pointer src=Pointer.to(buff);
		CL.clEnqueueWriteBuffer(JoclContext.commandQueue(), data.mem, CL_TRUE, offset*Sizeof.cl_double, Sizeof.cl_double, src, 0, null, null);
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
