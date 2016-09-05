package mikera.vectorz.jocl;

import static org.jocl.CL.clReleaseMemObject;

import org.jocl.cl_mem;

/**
 * Class to wrap OpenCL device memory. Automatically frees memory object on finalise.
 * 
 * @author Mike
 *
 */
public class DeviceMem {
	public cl_mem data;
	
	@Override
	public void finalize() throws Throwable {
		clReleaseMemObject(data);
		super.finalize();
	}
}
