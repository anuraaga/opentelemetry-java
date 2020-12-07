/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.context;

import java.util.Arrays;
import javax.annotation.Nullable;

@SuppressWarnings("unchecked")
final class ContextImplementations {

  // Used by auto-instrumentation agent. Check with auto-instrumentation before making changes to
  // this method.
  //
  // In particular, do not change this return type to DefaultContext because auto-instrumentation
  // hijacks this method and returns a bridged implementation of Context.
  //
  // Ideally auto-instrumentation would hijack the public Context.root() instead of this
  // method, but auto-instrumentation also needs to inject its own implementation of Context
  // into the class loader at the same time, which causes a problem because injecting a class into
  // the class loader automatically resolves its super classes (interfaces), which in this case is
  // Context, which would be the same class (interface) being instrumented at that time,
  // which would lead to the JVM throwing a LinkageError "attempted duplicate interface definition"
  static Context root() {
    return EmptyContext.INSTANCE;
  }

  enum EmptyContext implements Context {
    INSTANCE;

    @Override
    @Nullable
    public <V> V get(ContextKey<V> key) {
      return null;
    }

    @Override
    public <V> Context with(ContextKey<V> key, V value) {
      return new OneElementContext(key, value);
    }
  }

  static final class OneElementContext implements Context {

    private final ContextKey<?> key1;
    private final Object value1;

    OneElementContext(ContextKey<?> key1, Object value1) {
      this.key1 = key1;
      this.value1 = value1;
    }

    @Override
    @Nullable
    public <V> V get(ContextKey<V> key) {
      if (key == key1) {
        return (V) value1;
      }
      return null;
    }

    @Override
    public <V> Context with(ContextKey<V> key, V value) {
      if (key == key1) {
        if (value == value1) {
          return this;
        }
        return new OneElementContext(key, value);
      }
      return new TwoElementContext(key1, value1, key, value);
    }
  }

  static final class TwoElementContext implements Context {

    private final ContextKey<?> key1;
    private final Object value1;
    private final ContextKey<?> key2;
    private final Object value2;

    TwoElementContext(ContextKey<?> key1, Object value1, ContextKey<?> key2, Object value2) {
      this.key1 = key1;
      this.value1 = value1;
      this.key2 = key2;
      this.value2 = value2;
    }

    @Override
    @Nullable
    public <V> V get(ContextKey<V> key) {
      if (key == key1) {
        return (V) value1;
      }
      if (key == key2) {
        return (V) value2;
      }
      return null;
    }

    @Override
    public <V> Context with(ContextKey<V> key, V value) {
      if (key == key1) {
        if (value == value1) {
          return this;
        }
        return new TwoElementContext(key, value, key2, value2);
      }
      if (key == key2) {
        if (value == value2) {
          return this;
        }
        return new TwoElementContext(key1, value1, key, value);
      }
      return new ThreeElementContext(key1, value1, key2, value2, key, value);
    }
  }

  static final class ThreeElementContext implements Context {

    private final ContextKey<?> key1;
    private final Object value1;
    private final ContextKey<?> key2;
    private final Object value2;
    private final ContextKey<?> key3;
    private final Object value3;

    ThreeElementContext(
        ContextKey<?> key1,
        Object value1,
        ContextKey<?> key2,
        Object value2,
        ContextKey<?> key3,
        Object value3) {
      this.key1 = key1;
      this.value1 = value1;
      this.key2 = key2;
      this.value2 = value2;
      this.key3 = key3;
      this.value3 = value3;
    }

    @Override
    @Nullable
    public <V> V get(ContextKey<V> key) {
      if (key == key1) {
        return (V) value1;
      }
      if (key == key2) {
        return (V) value2;
      }
      if (key == key3) {
        return (V) value3;
      }
      return null;
    }

    @Override
    public <V> Context with(ContextKey<V> key, V value) {
      if (key == key1) {
        if (value == value1) {
          return this;
        }
        return new ThreeElementContext(key, value, key2, value2, key3, value3);
      }
      if (key == key2) {
        if (value == value2) {
          return this;
        }
        return new ThreeElementContext(key1, value1, key, value, key3, value3);
      }
      if (key == key3) {
        if (value == value3) {
          return this;
        }
        return new ThreeElementContext(key1, value1, key2, value2, key, value);
      }
      return new FourElementContext(key1, value1, key2, value2, key3, value3, key, value);
    }
  }

  static final class FourElementContext implements Context {

    private final ContextKey<?> key1;
    private final Object value1;
    private final ContextKey<?> key2;
    private final Object value2;
    private final ContextKey<?> key3;
    private final Object value3;
    private final ContextKey<?> key4;
    private final Object value4;

    FourElementContext(
        ContextKey<?> key1,
        Object value1,
        ContextKey<?> key2,
        Object value2,
        ContextKey<?> key3,
        Object value3,
        ContextKey<?> key4,
        Object value4) {
      this.key1 = key1;
      this.value1 = value1;
      this.key2 = key2;
      this.value2 = value2;
      this.key3 = key3;
      this.value3 = value3;
      this.key4 = key4;
      this.value4 = value4;
    }

    @Override
    @Nullable
    public <V> V get(ContextKey<V> key) {
      if (key == key1) {
        return (V) value1;
      }
      if (key == key2) {
        return (V) value2;
      }
      if (key == key3) {
        return (V) value3;
      }
      if (key == key4) {
        return (V) value4;
      }
      return null;
    }

    @Override
    public <V> Context with(ContextKey<V> key, V value) {
      if (key == key1) {
        if (value == value1) {
          return this;
        }
        return new FourElementContext(key, value, key2, value2, key3, value3, key4, value4);
      }
      if (key == key2) {
        if (value == value2) {
          return this;
        }
        return new FourElementContext(key1, value1, key, value, key3, value3, key4, value4);
      }
      if (key == key3) {
        if (value == value3) {
          return this;
        }
        return new FourElementContext(key1, value1, key2, value2, key, value, key4, value4);
      }
      if (key == key4) {
        if (value == value4) {
          return this;
        }
        return new FourElementContext(key1, value1, key2, value2, key3, value3, key, value);
      }
      return new FiveElementContext(
          key1, value1, key2, value2, key3, value3, key4, value4, key, value);
    }
  }

  static final class FiveElementContext implements Context {

    private final ContextKey<?> key1;
    private final Object value1;
    private final ContextKey<?> key2;
    private final Object value2;
    private final ContextKey<?> key3;
    private final Object value3;
    private final ContextKey<?> key4;
    private final Object value4;
    private final ContextKey<?> key5;
    private final Object value5;

    FiveElementContext(
        ContextKey<?> key1,
        Object value1,
        ContextKey<?> key2,
        Object value2,
        ContextKey<?> key3,
        Object value3,
        ContextKey<?> key4,
        Object value4,
        ContextKey<?> key5,
        Object value5) {
      this.key1 = key1;
      this.value1 = value1;
      this.key2 = key2;
      this.value2 = value2;
      this.key3 = key3;
      this.value3 = value3;
      this.key4 = key4;
      this.value4 = value4;
      this.key5 = key5;
      this.value5 = value5;
    }

    @Override
    @Nullable
    public <V> V get(ContextKey<V> key) {
      if (key == key1) {
        return (V) value1;
      }
      if (key == key2) {
        return (V) value2;
      }
      if (key == key3) {
        return (V) value3;
      }
      if (key == key4) {
        return (V) value4;
      }
      if (key == key5) {
        return (V) value5;
      }
      return null;
    }

    @Override
    public <V> Context with(ContextKey<V> key, V value) {
      if (key == key1) {
        if (value == value1) {
          return this;
        }
        return new FiveElementContext(
            key, value, key2, value2, key3, value3, key4, value4, key5, value5);
      }
      if (key == key2) {
        if (value == value2) {
          return this;
        }
        return new FiveElementContext(
            key1, value1, key, value, key3, value3, key4, value4, key5, value5);
      }
      if (key == key3) {
        if (value == value3) {
          return this;
        }
        return new FiveElementContext(
            key1, value1, key2, value2, key, value, key4, value4, key5, value5);
      }
      if (key == key4) {
        if (value == value4) {
          return this;
        }
        return new FiveElementContext(
            key1, value1, key2, value2, key3, value3, key, value, key5, value5);
      }
      if (key == key5) {
        if (value == value5) {
          return this;
        }
        return new FiveElementContext(
            key1, value1, key2, value2, key3, value3, key4, value4, key, value);
      }
      Object[] entries = new Object[12];
      entries[0] = key1;
      entries[1] = value1;
      entries[2] = key2;
      entries[3] = value2;
      entries[4] = key3;
      entries[5] = value3;
      entries[6] = key4;
      entries[7] = value4;
      entries[8] = key5;
      entries[9] = value5;
      entries[10] = key;
      entries[11] = value;
      return new ArrayBasedContext(entries);
    }
  }

  static final class ArrayBasedContext implements Context {

    private final Object[] entries;

    ArrayBasedContext(Object[] entries) {
      this.entries = entries;
    }

    @Override
    @Nullable
    public <V> V get(ContextKey<V> key) {
      for (int i = 0; i < entries.length; i += 2) {
        if (entries[i] == key) {
          @SuppressWarnings("unchecked")
          V result = (V) entries[i + 1];
          return result;
        }
      }
      return null;
    }

    @Override
    public <V> Context with(ContextKey<V> key, V value) {
      for (int i = 0; i < entries.length; i += 2) {
        if (entries[i] == key) {
          if (entries[i + 1] == value) {
            return this;
          }
          Object[] newEntries = entries.clone();
          newEntries[i + 1] = value;
          return new ArrayBasedContext(newEntries);
        }
      }
      Object[] newEntries = Arrays.copyOf(entries, entries.length + 2);
      newEntries[newEntries.length - 2] = key;
      newEntries[newEntries.length - 1] = value;
      return new ArrayBasedContext(newEntries);
    }
  }

  private ContextImplementations() {}
}
