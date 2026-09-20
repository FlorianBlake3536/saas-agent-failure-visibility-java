package com.example.agent;

public final class Main {
    public static void main(String[] args) {
        String key = System.getenv("INFRAI_API_KEY");
        if (key == null || key.isBlank()) throw new IllegalStateException("Set INFRAI_API_KEY");
        TenantOnboardingService service = new TenantOnboardingService(new InfraiErrorsClient(key));
        TenantOnboardingService.Decision result = service.onboard("tenant-acme", true, true);
        System.out.println("tenant-acme: " + result);
    }
}
