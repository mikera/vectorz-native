package mikera.vectorz.jocl;

import static org.jocl.CL.clBuildProgram;
import static org.jocl.CL.clCreateProgramWithSource;
import static org.jocl.CL.clReleaseProgram;

import org.jocl.cl_program;

public class Program {
	public final cl_program program;
	
	public Program(JoclContext context, String source) {
		this.program=clCreateProgramWithSource(context.context,
	            1, new String[]{ source }, null, null);
		clBuildProgram(program, 0, null, null, null, null);
	}

	@Override
	public void finalize() throws Throwable {
		clReleaseProgram(program);
		super.finalize();
	}

}
