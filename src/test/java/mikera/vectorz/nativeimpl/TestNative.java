package mikera.vectorz.nativeimpl;

import static org.junit.Assert.*;

import mikera.vectorz.impl.ArraySubVector;
import mikera.vectorz.nativeimpl.BlasVector;
import mikera.vectorz.nativeimpl.NativeUtil;

import org.junit.Test;

public class TestNative {
	
	@Test public void testBuffer() {
		assertEquals(9,NativeUtil.createBuffer(9).capacity());
	}
}
