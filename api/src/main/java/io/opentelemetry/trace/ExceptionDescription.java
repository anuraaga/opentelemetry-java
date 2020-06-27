package io.opentelemetry.trace;

import com.google.auto.value.AutoValue;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Set;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

@Immutable
@AutoValue
public abstract class ExceptionDescription {

  public static ExceptionDescription createCause(Throwable t) {
    Set<Throwable> seen = Collections.newSetFromMap(new IdentityHashMap<Throwable, Boolean>());
    seen.add(t);

    StackTraceElement[] stackTraceElements = t.getStackTrace();
    StackTrace stackTrace = StackTrace.create(t.getStackTrace());
    ExceptionDescription cause =
        t.getCause() != null ? ExceptionDescription.createCause(t.getCause(), seen, stackTraceElements) : null;
    return new AutoValue_ExceptionDescription(t.getMessage(), t.getClass().getCanonicalName(),
        stackTrace, cause);
  }

  @Nullable
  private static ExceptionDescription createCause(Throwable t, Set<Throwable> seen, StackTraceElement[] parentStackTrace) {
    if (!seen.add(t)) {
      return null;
    }

    StackTraceElement[] stackTraceElements = t.getStackTrace();
    StackTrace stackTrace = StackTrace.create(t.getStackTrace(), parentStackTrace);
    ExceptionDescription cause =
        t.getCause() != null ? ExceptionDescription.createCause(t.getCause(), seen, stackTraceElements) : null;
    return new AutoValue_ExceptionDescription(t.getMessage(), t.getClass().getCanonicalName(),
        stackTrace, cause);
  }

  @Nullable
  public abstract String getMessage();

  public abstract String getType();

  public abstract StackTrace getStack();

  @Nullable
  public abstract ExceptionDescription getCause();
}
