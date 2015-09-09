package psn.zyh.benchmark;

import java.util.Arrays;

import org.openjdk.jmh.annotations.Benchmark;

public class BitwiseOperationBench {
	
	private final static String LIB_PATH = "/home/zhyhang/code/assembly/";
	private final static int LOOP_COUNT = 100;
	private static long[] ALs = new long[2000];
	private static long[] BLs = new long[2000];
	
	static {
		System.load(LIB_PATH.concat("bitwise-avx-bench.so"));
		Arrays.setAll(ALs, i -> i + 1);
		Arrays.setAll(ALs, i -> i + 2);
	}
	
	@Benchmark
	public void nativeUnion(){
		nativeUnion(ALs,BLs);
	}

	@Benchmark
    public void javaUnion() {
		for (int j = 0; j < LOOP_COUNT; j++) {
			for (int i = 0; i < ALs.length; i++) {
				ALs[i] |= BLs[i];
			}
		}
    }
    
    private static native long nativeUnion(long[] al, long[] bl);

}
