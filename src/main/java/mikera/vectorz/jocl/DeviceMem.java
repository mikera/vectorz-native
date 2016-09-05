package mikera.vectorz.jocl;

import static org.jocl.CL.CL_MEM_READ_WRITE;
import static org.jocl.CL.CL_TRUE;
import static org.jocl.CL.clCreateBuffer;
import static org.jocl.CL.clReleaseMemObject;

import org.jocl.CL;
import org.jocl.Pointer;
import org.jocl.Sizeof;
import org.jocl.cl_mem;

/**
 * Class to wrap OpenCL device memory. Automatically frees memory object on finalise.
 * 
 * @author Mike
 *
 */
public class DeviceMem {
	public final cl_mem mem;
	private final int length;
	
	public DeviceMem(cl_mem mem,int length) {
		this.mem=mem;
		this.length=length;
	}

	public DeviceMem(int n) {
		length=n;
		mem=clCreateBuffer(JoclContext.getInstance().context,CL_MEM_READ_WRITE,n*Sizeof.cl_double, null, null);
	}

	@Override
	public void finalize() throws Throwable {
		clReleaseMemObject(mem);
		super.finalize();
	}
	
	public void setElements(double[] source, int offset) {
		setElements(0,source,offset,this.length);
	}
	
	public void setElements(int offset, double[] source, int srcOffset,int length) {
		if (length+offset>source.length) throw new IllegalArgumentException("Insufficient elements in source: "+source.length);
		Pointer src=Pointer.to(source).withByteOffset(srcOffset*Sizeof.cl_double);
		CL.clEnqueueWriteBuffer(JoclContext.commandQueue(), mem, CL_TRUE, offset*Sizeof.cl_double, length*Sizeof.cl_double, src, 0, null, null);		
	}
	
	public void getElements(int srcOffset,double[] dest, int destOffset, int length) {
		if (length+destOffset>dest.length) throw new IllegalArgumentException("Insufficient elements in dest: "+dest.length);
		Pointer dst=Pointer.to(dest).withByteOffset(destOffset*Sizeof.cl_double);
		CL.clEnqueueReadBuffer(JoclContext.commandQueue(), mem, CL_TRUE, srcOffset*Sizeof.cl_double, length*Sizeof.cl_double, dst, 0, null, null);
	}

	
	public void fill(double value) {
		double[] pattern=new double[]{value};
		long n=length;
		CL.clEnqueueFillBuffer(JoclContext.commandQueue(), mem, Pointer.to(pattern), Sizeof.cl_double, 0L, n*Sizeof.cl_double, 0,null,null);
	}
	
	public double unsafeGet(int i) {
		double[] result=new double[1];
		Pointer dst=Pointer.to(result);
		CL.clEnqueueReadBuffer(JoclContext.commandQueue(), mem, CL_TRUE, i*Sizeof.cl_double, Sizeof.cl_double, dst, 0, null, null);
		return result[0];
	}

	public void unsafeSet(int i, double value) {
		double[] buff=new double[1];
		buff[0]=value;
		Pointer src=Pointer.to(buff);
		CL.clEnqueueWriteBuffer(JoclContext.commandQueue(), mem, CL_TRUE, i*Sizeof.cl_double, Sizeof.cl_double, src, 0, null, null);
	}
}
