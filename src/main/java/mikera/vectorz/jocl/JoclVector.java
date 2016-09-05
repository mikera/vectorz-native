package mikera.vectorz.jocl;

import static org.jocl.CL.clEnqueueNDRangeKernel;
import static org.jocl.CL.clSetKernelArg;

import org.jocl.CL;
import org.jocl.Pointer;
import org.jocl.Sizeof;

import mikera.vectorz.AVector;
import mikera.vectorz.impl.ASizedVector;
import mikera.vectorz.util.DoubleArrays;

@SuppressWarnings("serial")
public class JoclVector extends ASizedVector {
	private final DeviceMem data;
	private final int offset;

	public static JoclVector newVector(int length) {
		return new JoclVector(length);
	}
	
	protected JoclVector(int length) {
		super(length);
		offset=0;
		data=new DeviceMem(length);
		data.fill(0.0);
	}
	
	public JoclVector(DeviceMem data, int offset, int length) {
		super(length);
		this.data=data;
		this.offset=offset;
	}

	public static JoclVector wrap(DeviceMem data, int offset, int length) {
		return new JoclVector(data,offset,length);
	}
	
	public static JoclVector create(AVector src) {
		if (src instanceof JoclVector) return create((JoclVector)src);
		double[] srcArray=src.asDoubleArray();
		if (srcArray==null) srcArray=src.asDoubleArray();
		return create(srcArray,0,srcArray.length);
	}
	
	public static JoclVector create(double[] srcArray, int offset, int length) {
		JoclVector v=new JoclVector(length);
		v.data.setElements(0, srcArray, offset,length);
		return v;
	}

	public static JoclVector create(JoclVector src) {
		int length=src.length;
		JoclVector v=new JoclVector(length);
		CL.clEnqueueCopyBuffer(JoclContext.commandQueue(),src.data.mem,v.data.mem,src.offset*Sizeof.cl_double,0,length*Sizeof.cl_double,0,null,null);
		return v;
	}
	
	@Override
	public void add(AVector a) {
		if (a instanceof JoclVector) {
			add((JoclVector) a);
		} else {
			add(JoclVector.create(a));
		}	
	}
	
	public void add(JoclVector a) {
		checkSameLength(a);
		Kernel kernel=Kernels.getKernel("add");
		clSetKernelArg(kernel.kernel, 0, (long)Sizeof.cl_mem, Pointer.to(data.mem).withByteOffset(offset*Sizeof.cl_double));
		clSetKernelArg(kernel.kernel, 1, (long)Sizeof.cl_mem, Pointer.to(a.data.mem).withByteOffset(a.offset*Sizeof.cl_double));
		
		long global_work_size[] = new long[]{elementCount()};
        
		clEnqueueNDRangeKernel(JoclContext.commandQueue(), kernel.kernel, 1, null,
				global_work_size, null, 0, null, null);
	}

	@Override
	public double get(int i) {
		checkIndex(i);
		return data.unsafeGet(i+offset);
	}

	@Override
	public void set(int i, double value) {
		checkIndex(i);
		data.unsafeSet(i+offset,value);
	}
	
	@Override
	public void unsafeSet(int i, double value) {
		data.unsafeSet(i+offset,value);
	}

	@Override
	public boolean isFullyMutable() {
		return true;
	}
	
	@Override
	public void setElements(double[] source, int offset) {
		data.setElements(source, this.offset+offset);
	}
	
	@Override
	public void getElements(double[] dest, int offset) {
		data.getElements(this.offset,dest, offset,length);
	}

	
	@Override
	public JoclVector exactClone() {
		return JoclVector.create(this);
	}

	@Override
	public double dotProduct(double[] data, int offset) {
		return DoubleArrays.dotProduct(getElements(), 0, data, offset, length);
	}



}
