package mikera.vectorz;

import static org.junit.Assert.*;

import mikera.vectorz.impl.ArraySubVector;
import mikera.vectorz.nativeimpl.BlasVector;
import mikera.vectorz.nativeimpl.NativeUtil;

import org.junit.Test;

public class TestNative {
	
	@Test public void testBuffer() {
		assertEquals(9,NativeUtil.createBuffer(9).capacity());
	}
	
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
