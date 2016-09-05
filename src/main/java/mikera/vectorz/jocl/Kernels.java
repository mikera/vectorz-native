package mikera.vectorz.jocl;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Kernels {
	private static final Program program;
	
	static final Kernel addCopy=getKernel("addCopy");
	static final Kernel add=getKernel("add");
	
	public static String loadString(String filePath) {
		try {
			Path path=Paths.get(System.class.getResource(filePath).toURI());
			return new String(Files.readAllBytes(path));
		} catch (Throwable e) {
			throw new Error(e);
		} 
	}
	
	private static Kernel getKernel(String kernelName) {
		return new Kernel(program,kernelName);
	}

	static {	
        program=new Program(JoclContext.instance, loadString("/mikera/vectorz/jocl/kernels.cl"));
	}
	
	public static void main(String[] args) {
		System.out.println(program);
	}
}
