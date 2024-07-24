package software.amazon.cloudwatchlogs.emf.annotations;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.ElementFilter;

import java.lang.reflect.Method;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;

import software.amazon.cloudwatchlogs.emf.logger.MetricsLogger;
import software.amazon.cloudwatchlogs.emf.model.Unit;

import java.util.HashMap;

@Aspect
class MetricAnnotationProcessor {

    
    /**
     * Puts a metric with the method count based on the parameters provided in the annotation.
     *
     * @param point The point for the annotated method.
     * @return The result of the method call.
     * @throws Throwable if the method fails.
     */
    @Around(
            "execution(* *(..)) && @annotation(software.amazon.cloudwatchlogs.emf.annotations.CountMetric)")
    public Object aroundCountMetric(final ProceedingJoinPoint point) throws Throwable {

        // Execute the method and capture whether a throwable is thrown.
        Throwable throwable = null;
        System.out.println("triggered countMetric");
        try {
            return point.proceed();
        } catch (final Throwable t) {
            throwable = t;
            throw t;
        } finally {
            final Method method = ((MethodSignature) point.getSignature()).getMethod();
            final CountMetric countMetricAnnotation = method.getAnnotation(CountMetric.class);

            // Determine if we consider this method to have failed.
            boolean shouldLog = false;
            for (final Class<? extends Throwable> failureClass :
                    countMetricAnnotation.logExceptions()) {
                shouldLog |= failureClass.isInstance(throwable);
            }

            shouldLog |= throwable == null && countMetricAnnotation.logSuccess();

            // If the annotation applies, put the metric.
            if (shouldLog) {
                final String metricName =
                countMetricAnnotation.name().isEmpty()
                        ? String.format(
                                "%s.%s.%s",
                                method.getDeclaringClass().getSimpleName(),
                                method.getName(),
                                        "Count")
                        : countMetricAnnotation.name();
                final double value = countMetricAnnotation.value();

                MetricsLogger logger = MetricAnnotationMediator.getLogger(countMetricAnnotation.logger());
                logger.putMetric(
                        metricName, value, Unit.COUNT, countMetricAnnotation.aggregationType());
            }
        }
    }

    /**
     * Puts a metric with the method time based on the parameters provided in the annotation.
     *
     * @param point The point for the annotated method.
     * @return The result of the method call.
     * @throws Throwable if the method fails.
     */
    @Around("execution(* *(..)) && @annotation(software.amazon.cloudwatchlogs.emf.annotations.TimeMetric)")
    public Object aroundTimeMetric(final ProceedingJoinPoint point) throws Throwable {

        // Execute the method and capture whether a throwable is thrown.
        System.out.println("triggered timeMetric");
        final double startTime = System.currentTimeMillis(); // capture the start time
        Throwable throwable = null;
        try {
            return point.proceed();
        } catch (final Throwable t) {
            throwable = t;
            throw t;
        } finally {
            final double time = System.currentTimeMillis() - startTime; // capture the total time
            final Method method = ((MethodSignature) point.getSignature()).getMethod();
            final TimeMetric timeMetricAnnotation = method.getAnnotation(TimeMetric.class);

            boolean shouldLog = false;
            for (final Class<? extends Throwable> failureClass :
                    timeMetricAnnotation.logExceptions()) {
                shouldLog |= failureClass.isInstance(throwable);
            }

            shouldLog |= throwable == null && timeMetricAnnotation.logSuccess();

            // If the annotation applies, put the metric.
            if (shouldLog) {
                final String metricName =
                timeMetricAnnotation.name().isEmpty()
                        ? String.format(
                                "%s.%s.%s",
                                method.getDeclaringClass().getSimpleName(),
                                method.getName(),
                                        "Time")
                        : timeMetricAnnotation.name();

                MetricsLogger logger = MetricAnnotationMediator.getLogger(timeMetricAnnotation.logger());
                logger.putMetric(
                        metricName, time, Unit.MILLISECONDS, timeMetricAnnotation.aggregationType());
            }
        }
    }
}
