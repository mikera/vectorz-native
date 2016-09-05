package mikera.vectorz.jocl;

import mikera.vectorz.AVector;
import mikera.vectorz.impl.ASizedVector;
import mikera.vectorz.impl.Vector0;
import mikera.vectorz.util.DoubleArrays;

@SuppressWarnings("serial")
public class JoclVector extends ASizedVector {
	private final DeviceVector data;
	private final int offset;

	public static JoclVector newVector(int length) {
		return new JoclVector(length);
	}
	
	private JoclVector(int length) {
		super(length);
		offset=0;
		data=DeviceVector.createLength(length);
	}
	
	private JoclVector(DeviceVector data, int offset, int length) {
		super(length);
		this.data=data;
		this.offset=offset;
	}

	public static JoclVector wrap(DeviceVector data, int offset, int length) {
		return new JoclVector(data,offset,length);
	}
	
	public static JoclVector create(AVector src) {
		if (src instanceof JoclVector) return create((JoclVector)src);
		double[] srcArray=src.asDoubleArray();
		if (srcArray==null) srcArray=src.asDoubleArray();
		return create(srcArray,0,srcArray.length);
	}
	
	public static JoclVector create(double[] srcArray, int offset, int length) {
		return wrap(DeviceVector.create(srcArray,offset,length),0,length);
	}

	public static JoclVector create(JoclVector src) {
		return src.exactClone();
	}
	
	@Override
	public void add(AVector a) {
		checkSameLength(a);
		data.add(offset,a,0,length);
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
		data.setElements(this.offset, source, offset, length);
	}
	
	@Override
	public void getElements(double[] dest, int offset) {
		data.getElements(this.offset,dest, offset,length);
	}
	
	@Override
	public AVector subVector(int offset, int length) {
		checkRange(offset,length);
		if (length==0) return Vector0.INSTANCE;
		if (length==this.length) return this;
		return JoclVector.wrap(data, offset+this.offset, length);
	}
	
	@Override
	public DeviceVector clone() {
		return DeviceVector.create(data,offset,length);
	}
	
	@Override
	public JoclVector exactClone() {
		DeviceVector dv=DeviceVector.create(this);
		return new JoclVector(dv,0,length);
	}

	@Override
	public double dotProduct(double[] data, int offset) {
		return DoubleArrays.dotProduct(getElements(), 0, data, offset, length);
	}



}
