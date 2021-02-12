/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.sdk.trace.config;

import static org.assertj.core.api.Assertions.assertThat;

import io.opentelemetry.sdk.trace.SpanLimits;
import org.junit.jupiter.api.Test;

class SpanLimitsTest {

  @Test
  void defaultSpanLimits() {
    assertThat(SpanLimits.getDefault().getSpanAttributeCountLimit()).isEqualTo(128);
    assertThat(SpanLimits.getDefault().getEventCountLimit()).isEqualTo(128);
    assertThat(SpanLimits.getDefault().getLinkCountLimit()).isEqualTo(128);
    assertThat(SpanLimits.getDefault().getAttributePerEventCountLimit()).isEqualTo(128);
    assertThat(SpanLimits.getDefault().getAttributePerLinkCountLimit()).isEqualTo(128);
  }

  @Test
  void updateSpanLimits_All() {
    SpanLimits spanLimits =
        SpanLimits.builder()
            .setAttributeCountLimit(8)
            .setEventCountLimit(10)
            .setLinkCountLimit(11)
            .setAttributePerEventCountLimit(1)
            .setAttributePerLinkCountLimit(2)
            .build();
    assertThat(spanLimits.getSpanAttributeCountLimit()).isEqualTo(8);
    assertThat(spanLimits.getEventCountLimit()).isEqualTo(10);
    assertThat(spanLimits.getLinkCountLimit()).isEqualTo(11);
    assertThat(spanLimits.getAttributePerEventCountLimit()).isEqualTo(1);
    assertThat(spanLimits.getAttributePerLinkCountLimit()).isEqualTo(2);

    // Preserves values
    SpanLimits spanLimitsDupe = spanLimits.toBuilder().build();
    // Use reflective comparison to catch when new fields are added.
    assertThat(spanLimitsDupe).usingRecursiveComparison().isEqualTo(spanLimits);
  }
}
