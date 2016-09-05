package mikera.vectorz.jocl;

import static org.jocl.CL.CL_MEM_READ_WRITE;
import static org.jocl.CL.clCreateBuffer;
import static org.jocl.CL.clReleaseMemObject;

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
	
	public DeviceMem(cl_mem mem) {
		this.mem=mem;
	}

	public DeviceMem(int n) {
		mem=clCreateBuffer(JoclContext.context,CL_MEM_READ_WRITE,n*Sizeof.cl_double, null, null);
	}

	@Override
	public void finalize() throws Throwable {
		clReleaseMemObject(mem);
		super.finalize();
	}
}
