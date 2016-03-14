package mikera.vectorz.nativeimpl;

import static org.junit.Assert.*;

import mikera.vectorz.AVector;
import mikera.vectorz.nativeimpl.BlasVector;

import org.junit.Test;

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
