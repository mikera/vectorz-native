package mikera.vectorz.jocl;

import static org.jocl.CL.CL_MEM_READ_WRITE;
import static org.jocl.CL.CL_TRUE;
import static org.jocl.CL.clCreateBuffer;
import static org.jocl.CL.clReleaseMemObject;

import org.jocl.CL;
import org.jocl.Pointer;
import org.jocl.Sizeof;
import org.jocl.cl_mem;

import mikera.vectorz.AVector;
import mikera.vectorz.impl.ASizedVector;

/**
 * Class to wrap OpenCL device memory. Automatically frees memory object on finalise.
 * 
 * @author Mike
 *
 */
public class DeviceVector extends ASizedVector {
	private static final long serialVersionUID = -5687987534975036854L;

	public final cl_mem mem;
	
	private DeviceVector(cl_mem mem,int length) {
		super(length);
		this.mem=mem;
	}

	private DeviceVector(int n) {
		super(n);
		mem=clCreateBuffer(JoclContext.getInstance().context,CL_MEM_READ_WRITE,n*Sizeof.cl_double, null, null);
		fill(0.0);
	}

	@Override
	public void finalize() throws Throwable {
		clReleaseMemObject(mem);
		super.finalize();
	}
	
	public static DeviceVector createLength(int length) {
		return new DeviceVector(length);
	}
	
	public static DeviceVector create(DeviceVector src) {
		int length=src.length;
		DeviceVector v=new DeviceVector(length);
		CL.clEnqueueCopyBuffer(JoclContext.commandQueue(),src.mem,v.mem,0,0,length*Sizeof.cl_double,0,null,null);
		return v;
	}
	
	@Override
	public void setElements(double[] source, int offset) {
		setElements(0,source,offset,this.length);
	}
	
	@Override
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

	@Override
	public void fill(double value) {
		fill(0,length,value);
	}
	
	public void fill(int offset, int length, double value) {
		double[] pattern=new double[]{value};
		long n=length;
		CL.clEnqueueFillBuffer(JoclContext.commandQueue(), mem, Pointer.to(pattern), Sizeof.cl_double, offset*Sizeof.cl_double, n*Sizeof.cl_double, 0,null,null);
	}

	@Override
	public double unsafeGet(int i) {
		double[] result=new double[1];
		Pointer dst=Pointer.to(result);
		CL.clEnqueueReadBuffer(JoclContext.commandQueue(), mem, CL_TRUE, i*Sizeof.cl_double, Sizeof.cl_double, dst, 0, null, null);
		return result[0];
	}

	@Override
	public void unsafeSet(int i, double value) {
		double[] buff=new double[1];
		buff[0]=value;
		Pointer src=Pointer.to(buff);
		CL.clEnqueueWriteBuffer(JoclContext.commandQueue(), mem, CL_TRUE, i*Sizeof.cl_double, Sizeof.cl_double, src, 0, null, null);
	}

	@Override
	public double get(int i) {
		checkIndex(i);
		return unsafeGet(i);
	}

	@Override
	public void set(int i, double value) {
		checkIndex(i);
		unsafeSet(i,value);
	}

	@Override
	public double dotProduct(double[] data, int offset) {
		return toVector().dotProduct(data,offset);
	}

	@Override
	public AVector exactClone() {
		return create(this);
	}




}
