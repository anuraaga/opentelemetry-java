/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.sdk.autoconfigure;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.github.netmikey.logunit.api.LogCapturer;
import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.context.propagation.ContextPropagators;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junitpioneer.jupiter.SetSystemProperty;
import org.slf4j.event.Level;
import org.slf4j.event.LoggingEvent;

// All tests fail due to config errors so never register a global. We can test everything here
// without separating test sets.
class ConfigErrorTest {

  @RegisterExtension
  LogCapturer logs = LogCapturer.create().captureForType(GlobalOpenTelemetry.class);

  @Test
  @SetSystemProperty(key = "otel.exporter", value = "otlp_metrics,prometheus")
  void multipleMetricExportersPrometheusThrows() {
    assertThatThrownBy(OpenTelemetrySdkAutoConfiguration::initialize)
        .isInstanceOf(ConfigurationException.class)
        .hasMessage(
            "Multiple metrics exporters configured. "
                + "Only one metrics exporter can be configured at a time.");
  }

  @Test
  @SetSystemProperty(key = "otel.exporter", value = "prometheus,otlp_metrics")
  void multipleMetricExportersOtlpThrows() {
    assertThatThrownBy(OpenTelemetrySdkAutoConfiguration::initialize)
        .isInstanceOf(ConfigurationException.class)
        .hasMessage(
            "Multiple metrics exporters configured. "
                + "Only one metrics exporter can be configured at a time.");
  }

  @Test
  @SetSystemProperty(key = "otel.propagators", value = "cat")
  void invalidPropagator() {
    assertThatThrownBy(OpenTelemetrySdkAutoConfiguration::initialize)
        .isInstanceOf(ConfigurationException.class)
        .hasMessage("Unrecognized value for otel.propagators: cat");
  }

  @Test
  @SetSystemProperty(key = "otel.trace.sampler", value = "traceidratio")
  void missingTraceIdRatio() {
    assertThatThrownBy(OpenTelemetrySdkAutoConfiguration::initialize)
        .isInstanceOf(ConfigurationException.class)
        .hasMessage(
            "otel.trace.sampler=traceidratio but otel.trace.sampler.arg is not provided. "
                + "Set otel.trace.sampler.arg to a value in the range [0.0, 1.0].");
  }

  @Test
  @SetSystemProperty(key = "otel.trace.sampler", value = "traceidratio")
  @SetSystemProperty(key = "otel.trace.sampler.arg", value = "bar")
  void invalidTraceIdRatio() {
    assertThatThrownBy(OpenTelemetrySdkAutoConfiguration::initialize)
        .isInstanceOf(ConfigurationException.class)
        .hasMessage("Invalid value for property otel.trace.sampler.arg=bar. Must be a double.");
  }

  @Test
  @SetSystemProperty(key = "otel.trace.sampler", value = "parentbased_traceidratio")
  void missingTraceIdRatioWithParent() {
    assertThatThrownBy(OpenTelemetrySdkAutoConfiguration::initialize)
        .isInstanceOf(ConfigurationException.class)
        .hasMessage(
            "otel.trace.sampler=parentbased_traceidratio but otel.trace.sampler.arg is "
                + "not provided. Set otel.trace.sampler.arg to a value in the range [0.0, 1.0].");
  }

  @Test
  @SetSystemProperty(key = "otel.trace.sampler", value = "parentbased_traceidratio")
  @SetSystemProperty(key = "otel.trace.sampler.arg", value = "bar")
  void invalidTraceIdRatioWithParent() {
    assertThatThrownBy(OpenTelemetrySdkAutoConfiguration::initialize)
        .isInstanceOf(ConfigurationException.class)
        .hasMessage("Invalid value for property otel.trace.sampler.arg=bar. Must be a double.");
  }

  @Test
  @SetSystemProperty(key = "otel.trace.sampler", value = "cat")
  void invalidSampler() {
    assertThatThrownBy(OpenTelemetrySdkAutoConfiguration::initialize)
        .isInstanceOf(ConfigurationException.class)
        .hasMessage("Unrecognized value for otel.trace.sampler: cat");
  }

  @Test
  @SetSystemProperty(key = "otel.exporter", value = "otlp,cat,dog")
  void invalidExporter() {
    assertThatThrownBy(OpenTelemetrySdkAutoConfiguration::initialize)
        .isInstanceOf(ConfigurationException.class)
        .hasMessage("Unrecognized value for otel.exporter: cat,dog");
  }

  @Test
  @SetSystemProperty(key = "otel.exporter", value = "bar")
  void globalOpenTelemetryWhenError() {
    assertThat(GlobalOpenTelemetry.get())
        .isInstanceOf(OpenTelemetrySdk.class)
        .extracting("propagators")
        // Failed to initialize so is no-op
        .isEqualTo(ContextPropagators.noop());

    LoggingEvent log =
        logs.assertContains(
            "Error automatically configuring OpenTelemetry SDK. "
                + "OpenTelemetry will not be enabled.");
    assertThat(log.getLevel()).isEqualTo(Level.ERROR);
    assertThat(log.getThrowable())
        .isInstanceOf(ConfigurationException.class)
        .hasMessage("Unrecognized value for otel.exporter: bar");
  }
}
