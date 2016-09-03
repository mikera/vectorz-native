package mikera.vectorz.jocl;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import mikera.matrixx.AMatrix;
import mikera.matrixx.Matrix;
import mikera.matrixx.Matrix22;
import mikera.vectorz.AVector;
import mikera.vectorz.Vector;

public class TestJoclMatrix  {

	@Test public void testGetSet() {
		JoclMatrix m=JoclMatrix.newMatrix(2,2);
		
		m.set(0,1,2.0);
		assertEquals(2.0,m.get(0,1),0.0);
	}
}
