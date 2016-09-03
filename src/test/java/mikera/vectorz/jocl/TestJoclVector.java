package mikera.vectorz.jocl;

import org.junit.Test;

public class TestJoclVector  {

	public void doGenericTests(JoclVector m) {
		new mikera.vectorz.TestVectors().doGenericTests(m);
	}
	
	@Test public void g_JoclVector3() {
		JoclVector m=JoclVector.newVector(3);
		
		doGenericTests(m);
	}

}
