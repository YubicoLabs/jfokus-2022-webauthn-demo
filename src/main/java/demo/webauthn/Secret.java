// Copyright 2022 Yubico AB
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package demo.webauthn;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.io.IOException;
import java.io.Serializable;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Generic container for some secret value. The value can only be accessed via the accessor methods,
 * and {@link #toString()} is overridden to prevent accidentally revealing the secret.
 */
@JsonSerialize(using = Secret.JsonSerializer.class)
public final class Secret<T> implements Serializable {
  private final T value;

  private Secret(T value) {
    this.value = value;
  }

  @JsonCreator
  /** Wrap the <code>value</code> as a secret. */
  public static <T> Secret<T> of(T value) {
    return new Secret<>(value);
  }

  /** Unwrap the contained secret value. */
  public T reveal() {
    return value;
  }

  /**
   * Unwrap the secret, apply <code>f</code> to it and return the result wrapped as a new {@link
   * Secret}.
   *
   * @param f a function to transform the contained secret value into a new secret value.
   */
  public <U, E extends Exception> Secret<U> map(Function<T, U> f) throws E {
    return new Secret<>(f.apply(value));
  }

  /**
   * Unwrap both <code>this</code> and <code>s</code>, apply <code>f</code> to both and return the
   * result wrapped as a new {@link Secret}.
   *
   * @param f a function to transform the contained secret values into one new secret value.
   */
  public <T2, U, E extends Exception> Secret<U> map2(Secret<T2> s, BiFunction<T, T2, U> f)
      throws E {
    return new Secret<>(f.apply(value, s.reveal()));
  }

  @Override
  public String toString() {
    return "<Secret>";
  }

  static class JsonSerializer extends com.fasterxml.jackson.databind.JsonSerializer<Secret<?>> {
    @Override
    public void serialize(Secret<?> value, JsonGenerator gen, SerializerProvider serializers)
        throws IOException {
      gen.writeString("<Secret>");
    }
  }
}
