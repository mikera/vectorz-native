package mikera.vectorz.jocl;

import mikera.vectorz.AScalar;
import mikera.vectorz.AVector;
import mikera.vectorz.impl.Vector0;
import mikera.vectorz.util.DoubleArrays;

@SuppressWarnings("serial")
public class JoclSubVector extends ADenseJoclVector {
	private final JoclVector data;
	private final int offset;

	public static JoclSubVector newVector(int length) {
		return new JoclSubVector(length);
	}
	
	private JoclSubVector(int length) {
		super(length);
		offset=0;
		data=JoclVector.createLength(length);
	}
	
	private JoclSubVector(JoclVector data, int offset, int length) {
		super(length);
		this.data=data;
		this.offset=offset;
	}

	public static JoclSubVector wrap(JoclVector data, int offset, int length) {
		return new JoclSubVector(data,offset,length);
	}
	
	public static JoclSubVector create(AVector src) {
		if (src instanceof JoclSubVector) return create((JoclSubVector)src);
		double[] srcArray=src.asDoubleArray();
		if (srcArray==null) srcArray=src.asDoubleArray();
		return create(srcArray,0,srcArray.length);
	}
	
	public static JoclSubVector create(double[] srcArray, int offset, int length) {
		return wrap(JoclVector.create(srcArray,offset,length),0,length);
	}

	public static JoclSubVector create(JoclSubVector src) {
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
	public void applyOp(KernelOp op, int start, int length) {
		checkRange(start,length);
		data.applyOp(op,offset+start,length);
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
		data.copyTo(this.offset, dest, offset,length);
	}
	
	@Override
	public AVector subVector(int offset, int length) {
		checkRange(offset,length);
		if (length==0) return Vector0.INSTANCE;
		if (length==this.length) return this;
		return JoclSubVector.wrap(data, offset+this.offset, length);
	}
	
	@Override
	public AScalar slice(int position) {
		checkIndex(position);
		return JoclScalar.wrap(this.data,position+offset);
	}
	
	@Override
	public JoclVector clone() {
		return JoclVector.create(data,offset,length);
	}
	
	@Override
	public JoclSubVector exactClone() {
		JoclVector dv=JoclVector.create(this);
		return new JoclSubVector(dv,0,length);
	}

	@Override
	public double dotProduct(double[] data, int offset) {
		return DoubleArrays.dotProduct(getElements(), 0, data, offset, length);
	}

	@Override
	public JoclVector getData() {
		return data;
	}

	@Override
	public int getDataOffset() {
		return offset;
	}




}
