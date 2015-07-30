package mikera.vectorz;

import static org.junit.Assert.assertEquals;

import mikera.matrixx.AMatrix;
import mikera.matrixx.Matrix;
import mikera.matrixx.Matrix22;
import mikera.vectorz.nativeimpl.NativeUtil;
import mikera.vectorz.nativeimpl.BlasMatrix;
import mikera.vectorz.nativeimpl.BlasVector;

import org.junit.Test;

public class TestBlasMatrix extends TestNative {

	@Test public void testAdd() {
		BlasMatrix m=BlasMatrix.create(Matrix22.create(1, 2, 3, 4));
		
		BlasMatrix m2=m.clone();
		
		m2.add(m);
		
		assertEquals(m2,Matrix22.create(2, 4, 6, 8));
	}
	
	@Test public void testClone() {
		BlasMatrix m=BlasMatrix.create(Matrix22.create(1, 2, 3, 4));
		assertEquals(m,m.clone());
		AMatrix m2=m.subMatrix(1, 1, 0, 2);
		assertEquals(m2,m2.clone());
	}
	
	@Test public void testRowcopy() {
		BlasMatrix m=BlasMatrix.create(Matrix22.create(1, 2, 3, 4));
		double[] data=new double[4];
		m.copyRowTo(1, data, 1);
		assertEquals(Vector.of(0,3,4,0),Vector.wrap(data));
	}
	
	@Test public void testInnerProduct() {
		BlasMatrix m=BlasMatrix.create(Matrix.create(new double[][] {{1,2,3},{4,5,6}}));
		Vector v=Vector.of(1,2,3);
		AVector result=m.innerProduct(v);
		assertEquals(Vector.of(14,32),result);
		
		BlasMatrix mt=m.getTranspose();
		Vector v2=Vector.of(1,2);
		AVector result2=mt.innerProduct(v2);
		assertEquals(Vector.of(9,12,15),result2);
	}
}
