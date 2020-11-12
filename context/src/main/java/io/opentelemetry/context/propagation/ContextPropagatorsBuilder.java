/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.context.propagation;

/**
 * A builder of {@link ContextPropagators}.
 *
 * <p>Invocation order of {@code TextMapPropagator#inject()} and {@code TextMapPropagator#extract()}
 * for registered trace propagators is undefined.
 *
 * <p>This is a example of a {@code ContextPropagators} object being created:
 *
 * <pre>{@code
 * ContextPropagators propagators = DefaultContextPropagators.builder()
 *     .addTextMapPropagator(new HttpTraceContext())
 *     .addTextMapPropagator(new HttpBaggage())
 *     .addTextMapPropagator(new MyCustomContextPropagator())
 *     .build();
 * }</pre>
 */
public interface ContextPropagatorsBuilder {

  /**
   * Adds a {@link TextMapPropagator} propagator.
   *
   * <p>One propagator per concern (traces, correlations, etc) should be added if this format is
   * supported.
   *
   * @param textMapPropagator the propagator to be added.
   * @return this.
   * @throws NullPointerException if {@code textMapPropagator} is {@code null}.
   */
  ContextPropagatorsBuilder addTextMapPropagator(TextMapPropagator textMapPropagator);

  /**
   * Builds a new {@code ContextPropagators} with the specified propagators.
   *
   * @return the newly created {@code ContextPropagators} instance.
   */
  ContextPropagators build();
}
