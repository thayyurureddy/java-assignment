# Questions

Here we have 3 questions related to the code base for you to answer. It is not about right or wrong, but more about what's the reasoning behind your decisions.

1. In this code base, we have some different implementation strategies when it comes to database access layer and manipulation. If you would maintain this code base, would you refactor any of those? Why?

**Answer:**
```txt
In the Warehouse module, a hexagonal architecture (Ports and Adapters) is used, which is excellent for decoupling business logic from external concerns like the database or API. However, the Store module uses the Active Record pattern (PanacheEntity) and is more tightly coupled to the REST layer. 

If I were to maintain this code base, I would favor the hexagonal approach for complex business logic (like Warehouse validations) but might keep the simpler Active Record pattern for basic CRUD modules if they don't involve complex rules. Consistency is key, so I might eventually refactor everything to follow one pattern—preferring hexagonal for its testability and maintainability in an "enterprise" setting, as it makes unit testing much more straightforward without needing a heavy framework or database.
```
----
2. When it comes to API spec and endpoints handlers, we have an Open API yaml file for the `Warehouse` API from which we generate code, but for the other endpoints - `Product` and `Store` - we just coded directly everything. What would be your thoughts about what are the pros and cons of each approach and what would be your choice?

**Answer:**
```txt
Pros of Code-First (Product/Store):
- Faster to implement initially.
- Less boilerplate if the API is simple.
- Code is the "single source of truth."

Cons of Code-First:
- Harder to maintain client-server parity without manual updates.
- API documentation can drift from implementation.

Pros of API-First (Warehouse OpenAPI):
- Strong contract between client and server.
- Guaranteed up-to-date documentation.
- Client SDKs and server stubs can be generated automatically.

Cons of API-First:
- Initial setup overhead.
- Generation toolchain complexity.

My Choice: I prefer API-First for public or shared APIs because it forces design thinking before implementation and ensures a reliable contract. For internal, rapidly evolving services, Code-First can be acceptable if combined with tools like Swagger/SmallRye to auto-generate the spec.
```
----
3. Given the need to balance thorough testing with time and resource constraints, how would you prioritize and implement tests for this project? Which types of tests would you focus on, and how would you ensure test coverage remains effective over time?

**Answer:**
```txt
Prioritization:
1. Core Business Logic: Focus on unit testing Use Cases (Domain logic) as these are the most critical and have the highest "return on investment."
2. Integration Points: Test repository layer (DB interactions) and API contracts (REST endpoints) to ensure the system works as a whole.
3. Edge Cases: Test validations and constraints (e.g., max capacity, stock matching).

Implementation:
- Use JUnit 5 and Mockito for unit testing to isolate logic.
- Use QuarkusTest for integration tests to verify CDI and database interactions.
- Use Testcontainers for real database testing in CI/CD pipeline.

Ensuring Coverage:
- Integrate JaCoCo for coverage reporting.
- Implement "fail-fast" tests in the build pipeline.
- Encourage a TDD-like approach for new features.
```