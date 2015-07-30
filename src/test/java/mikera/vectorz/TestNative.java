package mikera.vectorz;

import static org.junit.Assert.*;
import mikera.vectorz.nativeimpl.NativeUtil;

import org.junit.Test;

public class TestNative {
	
	@Test public void testBuffer() {
		assertEquals(9,NativeUtil.createBuffer(9).capacity());
	}

}
