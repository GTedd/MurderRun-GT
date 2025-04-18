/*

MIT License

Copyright (c) 2024 Brandon Li

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.

*/
package io.github.pulsebeat02.murderrun.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public final class StreamUtils {

  private static final Collector<?, ?, ?> SHUFFLER = Collectors.collectingAndThen(Collectors.toCollection(ArrayList::new), list -> {
    Collections.shuffle(list);
    return list;
  });

  private StreamUtils() {
    throw new UnsupportedOperationException("Utility class cannot be instantiated");
  }

  public static <T> Predicate<T> notEquals(final T compare) {
    return first -> first != null && !first.equals(compare);
  }

  public static <T> Predicate<T> inverse(final Predicate<T> predicate) {
    return predicate.negate();
  }

  public static <T, U> Predicate<T> isInstanceOf(final Class<U> clazz) {
    return clazz::isInstance;
  }

  @SuppressWarnings("unchecked")
  public static <T> Collector<T, ?, List<T>> toShuffledList() {
    return (Collector<T, ?, List<T>>) SHUFFLER;
  }

  public static <T> Collector<T, ?, Set<T>> toSynchronizedSet() {
    return Collectors.toCollection(() -> Collections.synchronizedSet(new HashSet<>()));
  }
}
