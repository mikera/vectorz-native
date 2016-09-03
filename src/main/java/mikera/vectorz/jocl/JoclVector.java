package mikera.vectorz.jocl;

import static org.jocl.CL.CL_MEM_READ_WRITE;
import static org.jocl.CL.CL_TRUE;
import static org.jocl.CL.clCreateBuffer;
import static org.jocl.CL.clReleaseMemObject;

import org.jocl.CL;
import org.jocl.Pointer;
import org.jocl.Sizeof;
import org.jocl.cl_mem;

import mikera.vectorz.impl.ASizedVector;
import mikera.vectorz.util.DoubleArrays;

@SuppressWarnings("serial")
public class JoclVector extends ASizedVector {
	private final cl_mem data;

	public static JoclVector newVector(int length) {
		return new JoclVector(length);
	}
	
	protected JoclVector(int length) {
		super(length);
		data=clCreateBuffer(JoclContext.context,CL_MEM_READ_WRITE,length*Sizeof.cl_double, null, null);
	}
	
	protected JoclVector(int length, cl_mem src) {
		super(length);
		data=clCreateBuffer(JoclContext.context,CL_MEM_READ_WRITE,length*Sizeof.cl_double, null, null);
		CL.clEnqueueCopyBuffer(JoclContext.commandQueue,src,data,0,0,length*Sizeof.cl_double,0,null,null);
	}

	@Override
	public double get(int i) {
		checkIndex(i);
		double[] result=new double[1];
		Pointer dst=Pointer.to(result);
		CL.clEnqueueReadBuffer(JoclContext.commandQueue, data, CL_TRUE, i*Sizeof.cl_double, Sizeof.cl_double, dst, 0, null, null);
		return result[0];
	}

	@Override
	public void set(int i, double value) {
		checkIndex(i);
		double[] buff=new double[1];
		buff[0]=value;
		Pointer src=Pointer.to(buff);
		CL.clEnqueueWriteBuffer(JoclContext.commandQueue, data, CL_TRUE, i*Sizeof.cl_double, Sizeof.cl_double, src, 0, null, null);
	}

	@Override
	public boolean isFullyMutable() {
		return true;
	}

	@Override
	public JoclVector exactClone() {
		return new JoclVector(length,data);
	}
	
	@Override
	public void finalize() throws Throwable {
		clReleaseMemObject(data);
		super.finalize();
	}

	@Override
	public double dotProduct(double[] data, int offset) {
		return DoubleArrays.dotProduct(getElements(), 0, data, offset, length);
	}


}
