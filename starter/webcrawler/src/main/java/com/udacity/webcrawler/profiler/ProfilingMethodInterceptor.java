package com.udacity.webcrawler.profiler;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.Temporal;
import java.util.Objects;

/**
 * A method interceptor that checks whether {@link Method}s are annotated with the {@link Profiled}
 * annotation. If they are, the method interceptor records how long the method invocation took.
 */
final class ProfilingMethodInterceptor implements InvocationHandler {

  private final Clock clock;
  private final Object invokedObject;
  private final ProfilingState profilingState;

  ProfilingMethodInterceptor(Clock clock, Object invokedObject, ProfilingState profilingState) {

    this.clock = Objects.requireNonNull(clock);
    this.invokedObject = invokedObject;
    this.profilingState = profilingState;
  }

  @Override
  public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
    Object invoked;
    boolean profiled = method.isAnnotationPresent(Profiled.class);
    if (profiled) {
      Instant start = clock.instant();
      try {
        invoked = method.invoke(invokedObject, args);
      } catch (InvocationTargetException e) {
        throw e.getTargetException();
      } finally {
        profilingState.record(invokedObject.getClass(), method, Duration.between(start, clock.instant()));

      }
      return invoked;
    }
    return method.invoke(invokedObject, args);

  }
}



