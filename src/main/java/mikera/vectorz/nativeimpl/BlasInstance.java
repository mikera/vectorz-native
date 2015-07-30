package mikera.vectorz.nativeimpl;

import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import com.github.fommil.netlib.BLAS;
import com.github.fommil.netlib.LAPACK;

public class BlasInstance {

	public static BLAS blas=BLAS.getInstance();
	public static LAPACK lapack=LAPACK.getInstance();
}
