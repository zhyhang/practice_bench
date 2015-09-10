package psn.zyh.benchmark;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;

@Fork(value = 1)
@Warmup(iterations = 5, time = 200, timeUnit = TimeUnit.MILLISECONDS)
@Measurement(iterations = 5, time = 200, timeUnit = TimeUnit.MILLISECONDS)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@State(Scope.Thread)
public class BitwiseOperationBench {
	
	private final static String LIB_PATH = "/home/zhyhang/code/assembly/";
	private final static int LOOP_COUNT = 100;
	private static long[] ALs = new long[3000];
	private static long[] BLs = new long[3000];
	private static ByteBuffer BB1 =ByteBuffer.allocateDirect(24000);
	private static ByteBuffer BB2 =ByteBuffer.allocateDirect(24000);
	
	
	static {
		System.load(LIB_PATH.concat("bitwise-avx-bench.so"));
		Arrays.setAll(ALs, i -> i + 1);
		Arrays.setAll(ALs, i -> i + 2);
	}
	
	@Benchmark
	public void nativeArrayUnion(){
		nativeUnion(ALs,BLs);
	}
	
	@Benchmark
	public void nativeDirectUnion(){
		nativeBBUnion(BB1, BB2);
	}

	@Benchmark
    public void javaUnion() {
		for (int j = 0; j < LOOP_COUNT; j++) {
			for (int i = 0; i < ALs.length; i++) {
				ALs[i] |= BLs[i];
			}
		}
    }
    
    private native long nativeUnion(long[] al, long[] bl);
    
    private native long nativeBBUnion(ByteBuffer bb1,ByteBuffer bb2);
    
    public static void main(String[] args) {
    	BitwiseOperationBench operate = new BitwiseOperationBench();
    	long[] lALs = new long[3000];
    	long[] lBLs = new long[3000];
    	ByteBuffer lBB1 =ByteBuffer.allocateDirect(24000);
    	ByteBuffer lBB2 =ByteBuffer.allocateDirect(24000);
		for (int i = 0; i < 5; i++) {
			operate.nativeUnion(lALs, lBLs);
			operate.nativeBBUnion(lBB1, lBB2);
			operate.javaUnion();
		}
		int loop=10;
		long tsb=System.nanoTime();
		for (int i = 0; i < loop; i++) {
			operate.nativeBBUnion(lBB1, lBB2);
		}
		System.out.printf("native direct union time cost %dns.\n",System.nanoTime()-tsb);
		tsb=System.nanoTime();
		for (int i = 0; i < loop; i++) {
			operate.nativeUnion(lALs, lBLs);
		}
		System.out.printf("native array union time cost %dns.\n",System.nanoTime()-tsb);
		tsb=System.nanoTime();
		for (int i = 0; i < loop; i++) {
			operate.javaUnion();
		}
		System.out.printf("javaUnion union time cost %dns.\n",System.nanoTime()-tsb);
	}

}
