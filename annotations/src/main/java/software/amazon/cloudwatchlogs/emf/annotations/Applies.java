package software.amazon.cloudwatchlogs.emf.annotations;

/** Represents when to publish metrics for a method annotated with @CountMetric or @TimeMetric. */
public enum Applies {
    SUCCESS, // only if the method doesn't throw a failure
    FAILURE, // only if the method throws a failure
    ALWAYS; // always (regardless of failures)

    /**
     * Determines if a metric should be published based on whether or not a method failed.
     *
     * @param isFailure Whether or not the method failed.
     * @return True if a metric should be published, otherwise false.
     */
    public boolean matches(final boolean isFailure) {
        return (isFailure && !this.equals(SUCCESS)) || (!isFailure && !this.equals(FAILURE));
    }

    @Override
    public String toString() {
        return name().charAt(0) + name().substring(1).toLowerCase();
    }
}
