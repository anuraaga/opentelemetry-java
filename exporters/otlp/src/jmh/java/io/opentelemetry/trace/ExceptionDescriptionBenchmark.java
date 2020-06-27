package io.opentelemetry.trace;

import com.google.common.io.CharStreams;
import io.opentelemetry.exporters.otlp.SpanAdapter;
import io.opentelemetry.proto.trace.v1.Status;
import java.io.PrintWriter;
import java.util.concurrent.TimeUnit;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;

@BenchmarkMode({Mode.AverageTime})
@Fork(1)
@Measurement(iterations = 15, time = 1)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Warmup(iterations = 5, time = 1)
@State(Scope.Benchmark)
public class ExceptionDescriptionBenchmark {

  private static final ThreadLocal<StringBuilder> BUFFER = new ThreadLocal<StringBuilder>() {
    @Override
    protected StringBuilder initialValue() {
      return new StringBuilder(512);
    }
  };

  private Throwable exception;

  @Setup(Level.Trial)
  public void setUp() {
    exception = createException1(size);
  }

  @Param({ "1", "5", "10", "20", "30", "50" })
  public int size;

  @Benchmark
  public String describeAsString() {
    StringBuilder buffer = BUFFER.get();
    buffer.setLength(0);
    exception.printStackTrace(new PrintWriter(CharStreams.asWriter(buffer)));
    return buffer.toString();
  }

  @Benchmark
  public ExceptionDescription describeAsStruct() {
    return ExceptionDescription.createCause(exception);
  }

  @Benchmark
  public byte[] describeAsStringAndSerialize() {
    return Status.newBuilder().setExceptionStr(describeAsString()).build().toByteArray();
  }

  @Benchmark
  public byte[] describeAsStructAndSerialize() {
    return Status.newBuilder().setException(SpanAdapter.toExceptionProto(describeAsStruct()))
        .build().toByteArray();
  }

  private Throwable createException1(int count) {
    if (count != 0) {
      return createException1(count - 1);
    }
    try {
      return createException2(size);
    } catch (Throwable t) {
      return new UnsupportedOperationException("Unsupported operation", t);
    }
  }

  private Throwable createException2(int count) {
    if (count != 0) {
      return createException2(count - 1);
    }
    try {
      return createException3(size);
    } catch (Throwable t) {
      throw new IllegalArgumentException("Illegal argument", t);
    }
  }

  private static Throwable createException3(int count) {
    if (count != 0) {
      return createException3(count - 1);
    }
    throw new IllegalStateException("Illegal state");
  }

  public static void main(String[] args) {
    ExceptionDescriptionBenchmark benchmark = new ExceptionDescriptionBenchmark();
    benchmark.size = 10;
    benchmark.setUp();
    System.out.println(benchmark.describeAsString());
    System.out.println(benchmark.describeAsStruct());
  }
}
