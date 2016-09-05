package mikera.vectorz.jocl;

import static org.jocl.CL.CL_TRUE;

import org.jocl.CL;
import org.jocl.Pointer;
import org.jocl.Sizeof;

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
		data=new DeviceMem(length);
		offset=0;
	}
	
	protected JoclVector(int length, DeviceMem src) {
		this(length);
		CL.clEnqueueCopyBuffer(JoclContext.commandQueue(),src.mem,data.mem,0,offset*Sizeof.cl_double,length*Sizeof.cl_double,0,null,null);
	}

	@Override
	public double get(int i) {
		checkIndex(i);
		double[] result=new double[1];
		Pointer dst=Pointer.to(result);
		CL.clEnqueueReadBuffer(JoclContext.commandQueue(), data.mem, CL_TRUE, (offset+i)*Sizeof.cl_double, Sizeof.cl_double, dst, 0, null, null);
		return result[0];
	}

	@Override
	public void set(int i, double value) {
		checkIndex(i);
		double[] buff=new double[1];
		buff[0]=value;
		Pointer src=Pointer.to(buff);
		CL.clEnqueueWriteBuffer(JoclContext.commandQueue(), data.mem, CL_TRUE, (offset+i)*Sizeof.cl_double, Sizeof.cl_double, src, 0, null, null);
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
	public double dotProduct(double[] data, int offset) {
		return DoubleArrays.dotProduct(getElements(), 0, data, offset, length);
	}


}
