package com.example.agent;

public final class TenantOnboardingServiceTest {
    public static void main(String[] args) {
        TenantOnboardingService service = new TenantOnboardingService(null);
        assert service.onboard("t-1", true, true) == TenantOnboardingService.Decision.ACTIVATED;
        assert service.onboard("t-2", true, false) == TenantOnboardingService.Decision.HELD_FOR_REVIEW;
        System.out.println("onboarding decisions pass");
    }
}
