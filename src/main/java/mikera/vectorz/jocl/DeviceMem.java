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
		if (length+offset>source.length) throw new IllegalArgumentException("Insufficient elements in source: "+source.length);
		Pointer src=Pointer.to(source).withByteOffset(offset*Sizeof.cl_double);
		CL.clEnqueueWriteBuffer(JoclContext.commandQueue(), mem, CL_TRUE, 0, length*Sizeof.cl_double, src, 0, null, null);		
	}
}
