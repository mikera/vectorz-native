package mikera.vectorz.jocl;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class TestJoclMatrix  {

	@Test public void testGetSet() {
		JoclMatrix m=JoclMatrix.newMatrix(2,2);
		
		m.set(0,1,2.0);
		assertEquals(2.0,m.get(0,1),0.0);
	}
	
	public void doGenericTests(JoclMatrix m) {
		new mikera.matrixx.TestMatrices().doGenericTests(m);
	}
	
	@Test public void g_JoclMatrix() {
		JoclMatrix m=JoclMatrix.newMatrix(2,2);
		
		doGenericTests(m);
	}

}
