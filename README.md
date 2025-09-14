
# Common Service

This module provides shared code and utilities used by both the `transfer-service` and `ledger-service` in the microservices architecture. It includes common exception handling, validation classes, and other reusable components.

## Purpose

- Centralizes code that is shared between services to avoid duplication.
- Ensures consistent error handling and validation logic across the system.

## Build Instructions

**Important:**
You must build the `common` module first before building or running the `transfer-service` or `ledger-service`, as they depend on it.

### Build the Common Module

```sh
cd common
mvn clean install
```

This will install the `common` module into your local Maven repository, making it available for the other services.

### Next Steps

After building the `common` module, you can proceed to build and run the `transfer-service` and `ledger-service` as described in their respective `README.md` files.

