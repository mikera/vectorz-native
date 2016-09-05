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

import mikera.vectorz.AScalar;
import mikera.vectorz.AVector;
import mikera.vectorz.impl.Vector0;

/**
 * Class to wrap OpenCL device memory as Vectorz vector.
 * Other vectorz-opencl classes should use this for underlying storage 
 * 
 * Automatically frees memory object on finalise.
 * 
 * @author Mike
 *
 */
public class JoclVector extends ADenseJoclVector {
	private static final long serialVersionUID = -5687987534975036854L;

	private final cl_mem mem;
	
	private JoclVector(cl_mem mem,int length) {
		super(length);
		this.mem=mem;
	}

	/**
	 * Creates a device vector of the given length 
	 * IMPRTANT NOTE: memory remains uninitialised
	 * @param length
	 */
	private JoclVector(int length) {
		super(length);
		mem=clCreateBuffer(JoclContext.getInstance().context,CL_MEM_READ_WRITE,length*Sizeof.cl_double, null, null);
	}
	
	private JoclVector(double[] data, int offset, int length) {
		super(length);
		mem=clCreateBuffer(JoclContext.getInstance().context,CL_MEM_READ_WRITE,length*Sizeof.cl_double, null, null);
		setElements(data,offset);
	}


	@Override
	public void finalize() throws Throwable {
		clReleaseMemObject(mem);
		super.finalize();
	}
	
	@Override
	public JoclVector getData() {
		return this;
	}

	@Override
	public int getDataOffset() {
		return 0;
	}
	
	public static JoclVector createLength(int length) {
		JoclVector v= new JoclVector(length);
		v.fill(0.0);
		return v;
	}
	
	public static JoclVector create(AVector src) {
		if (src instanceof JoclVector) return create((JoclVector)src);
		int length=src.length();
		double[] srcArray=src.asDoubleArray();
		if (srcArray==null) srcArray=src.toDoubleArray();
		return new JoclVector(srcArray,0,length);
	}
	
	public static JoclVector create(double[] data, int offset, int length) {
		return new JoclVector(data,offset,length);
	}
	
	public static JoclVector create(JoclVector src) {
		return create(src,0,src.length());
	}

	public static JoclVector create(JoclVector src, int offset, int length) {
		src.checkRange(offset, length);
		JoclVector v=new JoclVector(length);
		CL.clEnqueueCopyBuffer(JoclContext.commandQueue(),src.mem,v.mem,offset*Sizeof.cl_double,0,length*Sizeof.cl_double,0,null,null);
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
	public boolean isView() {
		return false;
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
		checkSameLength(a);
		if (a instanceof ADenseJoclVector) {
			add(0,(ADenseJoclVector) a,0,length);
		} else {
			add(0,JoclVector.create(a),0,length);
		}	
	}
	
	public void add(int offset, ADenseJoclVector src,int srcOffset, int length) {
		checkRange(srcOffset,length);
		Kernel kernel=Kernels.getKernel("add");
		clSetKernelArg(kernel.kernel, 0, Sizeof.cl_mem, pointer(offset)); // target
		clSetKernelArg(kernel.kernel, 1, Sizeof.cl_mem, src.pointer(srcOffset)); // source
		
		long global_work_size[] = new long[]{length};
        
		clEnqueueNDRangeKernel(JoclContext.commandQueue(), kernel.kernel, 1, null,
				global_work_size, null, 0, null, null);
	}
	
	@Override
	public void multiply(AVector a) {
		checkSameLength(a);
		if (a instanceof ADenseJoclVector) {
			multiply(0,(ADenseJoclVector) a,0,length);
		} else {
			multiply(0,JoclVector.create(a),0,length);
		}	
	}
	
	public void multiply(int offset, ADenseJoclVector src,int srcOffset, int length) {
		checkRange(srcOffset,length);
		Kernel kernel=Kernels.getKernel("mul");
		clSetKernelArg(kernel.kernel, 0, Sizeof.cl_mem, pointer(offset)); // target
		clSetKernelArg(kernel.kernel, 1, Sizeof.cl_mem, src.pointer(srcOffset)); // source
		
		long global_work_size[] = new long[]{length};
        
		clEnqueueNDRangeKernel(JoclContext.commandQueue(), kernel.kernel, 1, null,
				global_work_size, null, 0, null, null);
	}

	@Override
	public Pointer pointer() {
		return Pointer.to(mem);
	}
	
	@Override
	public Pointer pointer(int offset) {
		return Pointer.to(mem).withByteOffset(offset*Sizeof.cl_double);
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
		return JoclSubVector.wrap(this, offset, length);
	}
	
	@Override
	public AScalar slice(int position) {
		checkIndex(position);
		return JoclScalar.wrap(this,position);
	}

	@Override
	public JoclVector exactClone() {
		return create(this);
	}
	
	@Override
	public JoclVector clone() {
		return create(this);
	}


}
