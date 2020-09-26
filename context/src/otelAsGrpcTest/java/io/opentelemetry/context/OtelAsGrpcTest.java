/*
 * Copyright 2020, OpenTelemetry Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.opentelemetry.context;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class OtelAsGrpcTest {

  private static final ContextKey<String> ANIMAL = ContextKey.named("animal");

  private static final io.grpc.Context.Key<String> FOOD = io.grpc.Context.key("food");
  private static final io.grpc.Context.Key<String> COUNTRY = io.grpc.Context.key("country");

  private static ExecutorService otherThread;

  @BeforeAll
  static void setUp() {
    otherThread = Executors.newSingleThreadExecutor();
  }

  @AfterAll
  static void tearDown() {
    otherThread.shutdown();
  }

  @Test
  void grpcOtelMix() {
    io.grpc.Context grpcContext = io.grpc.Context.current().withValue(COUNTRY, "japan");
    assertThat(COUNTRY.get()).isNull();
    io.grpc.Context root = grpcContext.attach();
    try {
      assertThat(COUNTRY.get()).isEqualTo("japan");
      try (Scope ignored = Context.current().withValues(ANIMAL, "cat").makeCurrent()) {
        assertThat(Context.current().getValue(ANIMAL)).isEqualTo("cat");
        assertThat(COUNTRY.get()).isEqualTo("japan");

        io.grpc.Context context2 = io.grpc.Context.current().withValue(FOOD, "cheese");
        assertThat(FOOD.get()).isNull();
        io.grpc.Context toRestore = context2.attach();
        try {
          assertThat(FOOD.get()).isEqualTo("cheese");
          assertThat(COUNTRY.get()).isEqualTo("japan");
          assertThat(Context.current().getValue(ANIMAL)).isEqualTo("cat");
        } finally {
          context2.detach(toRestore);
        }
      }
    } finally {
      grpcContext.detach(root);
    }
  }

  @Test
  void grpcWrap() throws Exception {
    io.grpc.Context grpcContext = io.grpc.Context.current().withValue(COUNTRY, "japan");
    io.grpc.Context root = grpcContext.attach();
    try {
      try (Scope ignored = Context.current().withValues(ANIMAL, "cat").makeCurrent()) {
        assertThat(COUNTRY.get()).isEqualTo("japan");
        assertThat(Context.current().getValue(ANIMAL)).isEqualTo("cat");

        AtomicReference<String> grpcValue = new AtomicReference<>();
        AtomicReference<String> otelValue = new AtomicReference<>();
        Runnable runnable =
            () -> {
              grpcValue.set(COUNTRY.get());
              otelValue.set(Context.current().getValue(ANIMAL));
            };
        otherThread.submit(runnable).get();
        assertThat(grpcValue).hasValue(null);
        assertThat(otelValue).hasValue(null);

        otherThread.submit(io.grpc.Context.current().wrap(runnable)).get();
        assertThat(grpcValue).hasValue("japan");
        assertThat(otelValue).hasValue("cat");
      }
    } finally {
      grpcContext.detach(root);
    }
  }

  @Test
  void otelWrap() throws Exception {
    io.grpc.Context grpcContext = io.grpc.Context.current().withValue(COUNTRY, "japan");
    io.grpc.Context root = grpcContext.attach();
    try {
      try (Scope ignored = Context.current().withValues(ANIMAL, "cat").makeCurrent()) {
        assertThat(COUNTRY.get()).isEqualTo("japan");
        assertThat(Context.current().getValue(ANIMAL)).isEqualTo("cat");

        AtomicReference<String> grpcValue = new AtomicReference<>();
        AtomicReference<String> otelValue = new AtomicReference<>();
        Runnable runnable =
            () -> {
              grpcValue.set(COUNTRY.get());
              otelValue.set(Context.current().getValue(ANIMAL));
            };
        otherThread.submit(runnable).get();
        assertThat(grpcValue).hasValue(null);
        assertThat(otelValue).hasValue(null);

        otherThread.submit(Context.current().wrap(runnable)).get();

        assertThat(grpcValue).hasValue("japan");
        assertThat(otelValue).hasValue("cat");
      }
    } finally {
      grpcContext.detach(root);
    }
  }
}
