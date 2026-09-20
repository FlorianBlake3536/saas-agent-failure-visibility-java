package com.example.agent;

/** Models a compliance check in a tenant onboarding loop. */
public final class TenantOnboardingService {
    public enum Decision { ACTIVATED, HELD_FOR_REVIEW }
    private final InfraiErrorsClient errors;

    public TenantOnboardingService(InfraiErrorsClient errors) { this.errors = errors; }

    public Decision onboard(String tenantId, boolean registrationComplete, boolean adminVerified) {
        if (registrationComplete && adminVerified) return Decision.ACTIVATED;
        try {
            errors.capture("tenant onboarding held", "required verification is incomplete", "OnboardingCheck", tenantId + ":onboarding");
        } catch (Exception ignored) {
            // The business decision remains conservative when telemetry is unavailable.
        }
        return Decision.HELD_FOR_REVIEW;
    }
}
