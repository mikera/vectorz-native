package mikera.vectorz.nativeimpl;

import mikera.matrixx.AMatrix;
import mikera.vectorz.nativeimpl.BlasMatrix;
import org.junit.Test;

public class TestBlasMatrices extends TestNative {
	
	public void doGenericTests(AMatrix m) {
		new mikera.matrixx.TestMatrices().doGenericTests(m);
	}
	
	@Test public void g_BlasMatrix() {
		double[] data=new double[30];
		for (int i=0; i<30; i++) {
			data[i]=i+(1.0/Math.PI);
		}
		
		BlasMatrix m1=BlasMatrix.wrap(data,5,6);
		doGenericTests(m1);
		doGenericTests(m1.getTranspose());	
		
		BlasMatrix m2=BlasMatrix.wrap(data,6,5);
		doGenericTests(m2);
		doGenericTests(m2.getTranspose());	
	}
	
	@Test public void g_SquareMatrix() {
		double[] data=new double[16];
		for (int i=0; i<16; i++) {
			data[i]=i+(1.0/Math.PI);
		}
		
		BlasMatrix m1=BlasMatrix.wrap(data,4,4);
		doGenericTests(m1);
		doGenericTests(m1.getTranspose());
	}
}
