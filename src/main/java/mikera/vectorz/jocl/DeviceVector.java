package mikera.vectorz.jocl;

import static org.jocl.CL.CL_MEM_READ_WRITE;
import static org.jocl.CL.CL_TRUE;
import static org.jocl.CL.clCreateBuffer;
import static org.jocl.CL.clEnqueueNDRangeKernel;
import static org.jocl.CL.clReleaseMemObject;
import static org.jocl.CL.clSetKernelArg;

import org.jocl.CL;
import org.jocl.Pointer;
import org.jocl.Sizeof;
import org.jocl.cl_mem;

import mikera.vectorz.AVector;
import mikera.vectorz.impl.ASizedVector;
import mikera.vectorz.impl.Vector0;

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

	/**
	 * Creates a device vector of the given length 
	 * IMPRTANT NOTE: memory remains uninitialised
	 * @param length
	 */
	private DeviceVector(int length) {
		super(length);
		mem=clCreateBuffer(JoclContext.getInstance().context,CL_MEM_READ_WRITE,length*Sizeof.cl_double, null, null);
	}
	
	private DeviceVector(double[] data, int offset, int length) {
		super(length);
		mem=clCreateBuffer(JoclContext.getInstance().context,CL_MEM_READ_WRITE,length*Sizeof.cl_double, null, null);
		setElements(data,offset);
	}


	@Override
	public void finalize() throws Throwable {
		clReleaseMemObject(mem);
		super.finalize();
	}
	
	public static DeviceVector createLength(int length) {
		DeviceVector v= new DeviceVector(length);
		v.fill(0.0);
		return v;
	}
	
	public static DeviceVector create(AVector src) {
		if (src instanceof DeviceVector) return create((DeviceVector)src);
		int length=src.length();
		double[] srcArray=src.asDoubleArray();
		if (srcArray==null) srcArray=src.asDoubleArray();
		return new DeviceVector(srcArray,0,length);
	}
	
	public static DeviceVector create(double[] data, int offset, int length) {
		return new DeviceVector(data,offset,length);
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
		fillRange(0,length,value);
	}
	
	@Override
	public void fillRange(int offset, int length, double value) {
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
	public void add(AVector a) {
		if (a instanceof DeviceVector) {
			add((DeviceVector) a);
		} else {
			add(DeviceVector.create(a));
		}	
	}
	
	public void add(DeviceVector a) {
		checkSameLength(a);
		Kernel kernel=Kernels.getKernel("add");
		clSetKernelArg(kernel.kernel, 0, (long)Sizeof.cl_mem, Pointer.to(mem));
		clSetKernelArg(kernel.kernel, 1, (long)Sizeof.cl_mem, Pointer.to(a.mem));
		
		long global_work_size[] = new long[]{length()};
        
		clEnqueueNDRangeKernel(JoclContext.commandQueue(), kernel.kernel, 1, null,
				global_work_size, null, 0, null, null);
	}

	@Override
	public double dotProduct(double[] data, int offset) {
		return toVector().dotProduct(data,offset);
	}
	
	@Override
	public AVector subVector(int offset, int length) {
		checkRange(offset,length);
		if (length==0) return Vector0.INSTANCE;
		if (length==this.length) return this;
		return JoclVector.wrap(this, offset, length);
	}

	@Override
	public AVector exactClone() {
		return create(this);
	}
}
