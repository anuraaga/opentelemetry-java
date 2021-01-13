/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.spi.trace;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.trace.TracerProvider;
import javax.annotation.concurrent.ThreadSafe;

/**
 * TracerProviderFactory is a service provider for a {@link TracerProvider}. Fully qualified class
 * name of the implementation should be registered in {@code
 * META-INF/services/io.opentelemetry.trace.spi.TracerProviderFactory}. <br>
 * <br>
 * A specific implementation can be selected by a system property {@code
 * io.opentelemetry.trace.spi.TracerProviderFactory} with value of fully qualified class name.
 *
 * @see OpenTelemetry
 * @deprecated Use {@link io.opentelemetry.api.DefaultOpenTelemetry#builder} to initialize
 *     OpenTelemetry with a custom provider, or {@code OpenTelemetrySdk#builder} or {@code
 *     opentelemetry-sdk-extension-autoconfigure} to configure the default SDK.
 */
@ThreadSafe
@Deprecated
public interface TracerProviderFactory {

  /**
   * Creates a new TracerProvider.
   *
   * @return a new TracerProvider.
   */
  TracerProvider create();
}
