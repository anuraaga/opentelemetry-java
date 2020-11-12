/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.context.propagation;

import io.opentelemetry.context.Context;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;

/**
 * Default context propagators.
 *
 * @deprecated Use {@link ContextPropagators#builder()}.
 */
@Deprecated
public final class DefaultContextPropagators implements ContextPropagators {
  private final TextMapPropagator textMapPropagator;

  @Override
  public TextMapPropagator getTextMapPropagator() {
    return textMapPropagator;
  }

  /**
   * Create a builder for DefaultContextPropagators.
   *
   * @deprecated Use {@link ContextPropagators#builder()}.
   */
  @Deprecated
  public static Builder builder() {
    return new DefaultContextPropagators.Builder();
  }

  private DefaultContextPropagators(TextMapPropagator textMapPropagator) {
    this.textMapPropagator = textMapPropagator;
  }

  /**
   * Builder for DefaultContextPropagators.
   *
   * @deprecated Use {@link ContextPropagators#builder()}.
   */
  @Deprecated
  public static final class Builder implements ContextPropagatorsBuilder {
    List<TextMapPropagator> textPropagators = new ArrayList<>();

    @Override
    public Builder addTextMapPropagator(TextMapPropagator textMapPropagator) {
      if (textMapPropagator == null) {
        throw new NullPointerException("textMapPropagator");
      }

      textPropagators.add(textMapPropagator);
      return this;
    }

    @Override
    public ContextPropagators build() {
      if (textPropagators.isEmpty()) {
        return new DefaultContextPropagators(NoopTextMapPropagator.INSTANCE);
      }

      return new DefaultContextPropagators(new MultiTextMapPropagator(textPropagators));
    }
  }

  private static final class MultiTextMapPropagator implements TextMapPropagator {
    private final TextMapPropagator[] textPropagators;
    private final List<String> allFields;

    private MultiTextMapPropagator(List<TextMapPropagator> textPropagators) {
      this.textPropagators = new TextMapPropagator[textPropagators.size()];
      textPropagators.toArray(this.textPropagators);
      this.allFields = Collections.unmodifiableList(getAllFields(this.textPropagators));
    }

    @Override
    public List<String> fields() {
      return allFields;
    }

    private static List<String> getAllFields(TextMapPropagator[] textPropagators) {
      Set<String> fields = new LinkedHashSet<>();
      for (int i = 0; i < textPropagators.length; i++) {
        fields.addAll(textPropagators[i].fields());
      }

      return new ArrayList<>(fields);
    }

    @Override
    public <C> void inject(Context context, @Nullable C carrier, Setter<C> setter) {
      for (int i = 0; i < textPropagators.length; i++) {
        textPropagators[i].inject(context, carrier, setter);
      }
    }

    @Override
    public <C> Context extract(Context context, @Nullable C carrier, Getter<C> getter) {
      for (int i = 0; i < textPropagators.length; i++) {
        context = textPropagators[i].extract(context, carrier, getter);
      }
      return context;
    }
  }

  private static final class NoopTextMapPropagator implements TextMapPropagator {
    private static final NoopTextMapPropagator INSTANCE = new NoopTextMapPropagator();

    @Override
    public List<String> fields() {
      return Collections.emptyList();
    }

    @Override
    public <C> void inject(Context context, @Nullable C carrier, Setter<C> setter) {}

    @Override
    public <C> Context extract(Context context, @Nullable C carrier, Getter<C> getter) {
      return context;
    }
  }
}
