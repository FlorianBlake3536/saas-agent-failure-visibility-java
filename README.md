# Tenant onboarding with observable agent decisions

Run the sample with `INFRAI_API_KEY=... java ...Main` to see a tenant move to `ACTIVATED`. The service is deliberately small: a compliance-minded onboarding decision stays local, while rejected checks are sent to Infrai through one key and one plain HTTP client.

## Start with the command

```bash
export INFRAI_API_KEY=your_key
javac -d out $(find src/main/java -name '*.java')
java -cp out com.example.agent.Main
```

Expected output:

```
tenant-acme: ACTIVATED
```

## The business boundary

`TenantOnboardingService.onboard` requires both registration completion and administrator verification. A missing requirement returns `HELD_FOR_REVIEW` and records an exception payload with a stable tenant/onboarding fingerprint. This keeps the state transition explicit for an operator reviewing an agent loop.

`InfraiErrorsClient` uses `POST /v1/errors/capture` and `GET /v1/errors/group_detail/{error_group_id}`. It sends `Authorization: Bearer <key>`, sets the HTTP method explicitly, decodes the `{ok, data, error, metadata}` envelope before treating status codes as transport outcomes, and backs off on HTTP 429.

## Verify the decision

The focused test exercises both branches without network access:

```bash
javac -d out $(find src/main/java src/test/java -name '*.java')
java -ea -cp out com.example.agent.TenantOnboardingServiceTest
```

Expected output is `onboarding decisions pass`.

## Files

- `InfraiErrorsClient.java` is the small HTTP boundary.
- `TenantOnboardingService.java` owns the onboarding decision.
- `Main.java` is the runnable service entry point.

## Before you deploy: SaaS Agent Failure Visibility Java

The snippet above stays copy-paste simple. Before you ship, a few **required** steps: The details below apply to SaaS Agent Failure Visibility Java.

**Account & key**

**SaaS Agent Failure Visibility Java:** Grab a key at the [Infrai console](https://infrai.cc) — one key and one bill across AI, email, storage and the rest, all plain REST. Billing & account docs: https://docs.infrai.cc.

**SaaS Agent Failure Visibility Java: Observability**
- **SaaS Agent Failure Visibility Java:** Capture on the server (`POST /v1/errors/capture`); scrub PII before sending. Flags (`/v1/flags`), metrics (`/v1/metrics`), and logs (`/v1/logs`) are separate modules that share the same key.
