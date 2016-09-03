package mikera.vectorz.jocl;

import static org.jocl.CL.*;

import java.nio.DoubleBuffer;

import org.jocl.*;


import mikera.matrixx.AMatrix;
import mikera.matrixx.impl.ARectangularMatrix;
import mikera.vectorz.Tools;

@SuppressWarnings("serial")
public class JoclMatrix extends ARectangularMatrix {
	private final cl_mem data;
	private final DoubleBuffer buffer;
	
	public static JoclMatrix newMatrix(int rows, int cols) {
		return new JoclMatrix(rows,cols);
	}
	
	protected JoclMatrix(int rows, int cols) {
		super(rows, cols);
		int n=Tools.toInt(rows*cols);
		data=clCreateBuffer(JoclContext.context,CL_MEM_READ_WRITE,n*Sizeof.cl_float, null, null);
		buffer=DoubleBuffer.allocate(n);
	}

	@Override
	public double get(int row, int column) {
		int offset=column+rows*row;
		Pointer dst=Pointer.to(buffer).withByteOffset(offset*Sizeof.cl_double);
		CL.clEnqueueReadBuffer(JoclContext.commandQueue, data, CL_TRUE, offset*Sizeof.cl_double, Sizeof.cl_double, dst, 0, null, null);
		return buffer.get(offset);
	}

	@Override
	public void set(int row, int column, double value) {
		int offset=column+rows*row;
		buffer.put(offset, value);
		Pointer dst=Pointer.to(buffer).withByteOffset(offset*Sizeof.cl_double);
		CL.clEnqueueWriteBuffer(JoclContext.commandQueue, data, CL_TRUE, offset*Sizeof.cl_double, Sizeof.cl_double, dst, 0, null, null);
	}

	@Override
	public boolean isFullyMutable() {
		return true;
	}

	@Override
	public AMatrix exactClone() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void finalize() throws Throwable {
		clReleaseMemObject(data);
		super.finalize();
	}


}
