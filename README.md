## Order and Inventory Services

A Spring Boot project with two services:

| Service | Port | Responsibility |
|---------|------|----------------|
| Inventory | 8080 | Manages products, stock, and stock deductions |
| Orders | 8081 | Creates orders and coordinates stock deductions |

### Order processing

- Orders start as PENDING.
- Successful stock deductions move orders to READY.
- Business failures, such as insufficient stock, move orders to REJECTED.
- Temporary connection failures leave orders PENDING for automatic retries.
- Repeated deductions for the same order do not deduct stock twice.
- Conflicting deduction details move orders to NEEDS_REVIEW.

### Continuous integration and security

GitHub Actions builds and tests both services.

Dependabot checks dependencies for known vulnerabilities.
CodeQL scans source code for potential security vulnerabilities.
Secret scanning and push protection help detect exposed credentials.

Changes to main require a pull request and passing CI checks.