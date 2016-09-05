package mikera.vectorz.jocl;

import org.junit.Test;

import mikera.vectorz.AVector;

public class TestJoclVector  {

	public void doGenericTests(AVector m) {
		new mikera.vectorz.TestVectors().doGenericTests(m);
	}
	
	@Test public void g_JoclVector3() {
		JoclVector m=JoclVector.newVector(3);
		
		doGenericTests(m);
	}
	
	@Test public void g_DeviceVector3() {
		DeviceVector m=DeviceVector.createLength(3);
		
		doGenericTests(m);
	}

}
