package mikera.vectorz;

import static org.junit.Assert.assertEquals;
import mikera.matrixx.Matrix22;
import mikera.vectorz.nativeimpl.NativeUtil;
import mikera.vectorz.nativeimpl.BufferMatrix;

import org.junit.Test;

public class TestBufferMatrix extends TestNative {

	@Test public void testAdd() {
		BufferMatrix m=BufferMatrix.create(Matrix22.create(1, 2, 3, 4));
		
		BufferMatrix m2=m.clone();
		
		m2.add(m);
		
		assertEquals(m2,Matrix22.create(2, 4, 6, 8));
	}
}
