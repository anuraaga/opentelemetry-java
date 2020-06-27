package io.opentelemetry.trace;

import com.google.auto.value.AutoValue;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.concurrent.Immutable;

@Immutable
@AutoValue
public abstract class StackTrace {

  private static final StackTraceElement[] EMPTY_STACK = new StackTraceElement[0];

  public static StackTrace create(StackTraceElement[] stackTrace) {
    return create(stackTrace, EMPTY_STACK);
  }

  static StackTrace create(StackTraceElement[] stackTrace, StackTraceElement[] parentStackTrace) {
    int limit = stackTrace.length - 1;
    int parentLimit = parentStackTrace.length - 1;
    while (limit >= 0 && parentLimit >= 0) {
      if (!stackTrace[limit].equals(parentStackTrace[parentLimit])) {
        break;
      }
      limit--;
      parentLimit--;
    }

    List<Frame> frames = new ArrayList<>(limit + 1);
    for (int i = 0; i <= limit; i++) {
      frames.add(Frame.create(stackTrace[i]));
    }
    return new AutoValue_StackTrace(frames, stackTrace.length);
  }

  public abstract List<Frame> getFrames();

  public abstract int getNumFrames();

  @Immutable
  @AutoValue
  public static abstract class Frame {
    public static Frame create(StackTraceElement frame) {
      return new AutoValue_StackTrace_Frame(frame.getClassName() + '.' + frame.getMethodName(), frame.getFileName(),
          frame.getLineNumber(), 0);
    }

    public abstract String getFunctionName();

    public abstract String getFileName();

    public abstract int getLineNumber();

    public abstract int getColumnNumber();
  }
}
