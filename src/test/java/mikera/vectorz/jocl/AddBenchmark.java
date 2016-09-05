package mikera.vectorz.jocl;

import java.util.Arrays;

import mikera.matrixx.Matrix;
import mikera.vectorz.Vector;
import mikera.vectorz.impl.RangeVector;
import mikera.vectorz.util.DoubleArrays;

import com.google.caliper.Runner;
import com.google.caliper.SimpleBenchmark;

/**
 * Caliper based benchmarks for sublist iteration
 * 
 * See debate at: http://stackoverflow.com/questions/17302130/enhanced-for-loop/17302215
 * 
 * @author Mike
 */
@SuppressWarnings("unused")
public class AddBenchmark extends SimpleBenchmark {
	double result;
	
	int LIST_SIZE=100;

	public void timeAdd(int runs) {
		JoclMatrix m=JoclMatrix.create(RangeVector.create(0, 10000).reshape(100,100));
		JoclMatrix m2=JoclMatrix.create(RangeVector.create(0, 10000).reshape(100,100));

		for (int i=0; i<runs; i++) {
			m.add(m2);
		}
		result=m.get(99,99);
		if (result!=9999.0*(1+runs)) throw new Error("Wrong result: "+ result);
	}
	
	public void timeAddJava(int runs) {
		Matrix m=Matrix.create(RangeVector.create(0, 10000).reshape(100,100));
		Matrix m2=Matrix.create(RangeVector.create(0, 10000).reshape(100,100));

		for (int i=0; i<runs; i++) {
			m.add(m2);
		}
		result=m.get(99,99);
		if (result!=9999.0*(1+runs)) throw new Error("Wrong result: "+ result);
	}

	public static void main(String[] args) {
		new AddBenchmark().run();
	}

	private void run() {
		Runner runner=new Runner();
		runner.run(new String[] {this.getClass().getCanonicalName()});
	}

}
