package mikera.vectorz.jocl;

import static org.jocl.CL.CL_MEM_READ_WRITE;
import static org.jocl.CL.CL_TRUE;
import static org.jocl.CL.clCreateBuffer;
import static org.jocl.CL.clReleaseMemObject;

import org.jocl.CL;
import org.jocl.Pointer;
import org.jocl.Sizeof;
import org.jocl.cl_mem;

import mikera.matrixx.AMatrix;
import mikera.matrixx.impl.ARectangularMatrix;
import mikera.vectorz.Tools;

@SuppressWarnings("serial")
public class JoclMatrix extends ARectangularMatrix {
	private final cl_mem data;

	public static JoclMatrix newMatrix(int rows, int cols) {
		return new JoclMatrix(rows,cols);
	}
	
	protected JoclMatrix(int rows, int cols) {
		super(rows, cols);
		int n=Tools.toInt(rows*cols);
		data=clCreateBuffer(JoclContext.context,CL_MEM_READ_WRITE,n*Sizeof.cl_double, null, null);
	}
	
	protected JoclMatrix(int rows, int cols, cl_mem src) {
		super(rows, cols);
		int n=Tools.toInt(rows*cols);
		data=clCreateBuffer(JoclContext.context,CL_MEM_READ_WRITE,n*Sizeof.cl_double, null, null);
		CL.clEnqueueCopyBuffer(JoclContext.commandQueue,src,data,0,0,n*Sizeof.cl_double,0,null,null);
	}

	@Override
	public double get(int row, int column) {
		checkIndex(row,column);
		int offset=column+rows*row;
		double[] result=new double[1];
		Pointer dst=Pointer.to(result);
		CL.clEnqueueReadBuffer(JoclContext.commandQueue, data, CL_TRUE, offset*Sizeof.cl_double, Sizeof.cl_double, dst, 0, null, null);
		return result[0];
	}

	@Override
	public void set(int row, int column, double value) {
		checkIndex(row,column);
		int offset=column+rows*row;
		double[] buff=new double[1];
		buff[0]=value;
		Pointer src=Pointer.to(buff);
		CL.clEnqueueWriteBuffer(JoclContext.commandQueue, data, CL_TRUE, offset*Sizeof.cl_double, Sizeof.cl_double, src, 0, null, null);
	}

	@Override
	public boolean isFullyMutable() {
		return true;
	}

	@Override
	public AMatrix exactClone() {
		return new JoclMatrix(rows,cols,data);
	}
	
	@Override
	public void finalize() throws Throwable {
		clReleaseMemObject(data);
		super.finalize();
	}


}
