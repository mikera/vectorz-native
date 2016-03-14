package mikera.vectorz.nativeimpl;

import org.junit.Test;

import mikera.vectorz.AVector;

public class TestBlasVector {
	
	public void doGenericTests(AVector a) {
		new mikera.vectorz.TestVectors ().doGenericTests(a);
	}
	
	@Test public void g_BlasVector() {
		double[] data=new double[100];
		for (int i=0; i<100; i++) {
			data[i]=i+(1.0/Math.PI);
		}
		
		BlasVector v=BlasVector.wrap(data);
		
		doGenericTests(v);
	}

}
