# Unit Testing Guidelines

This document outlines the standards and best practices for unit testing in this project.

## Tools
- **JUnit 5**: The core testing framework for Java.
- **Mockito**: Used for mocking dependencies to isolate the unit under test.
- **AssertJ**: (If available) Preferred for fluent and readable assertions.

## Test Structure
We follow the **AAA (Arrange-Act-Assert)** pattern:
1. **Arrange**: Set up the environment, mocks, and input data.
2. **Act**: Execute the method being tested.
3. **Assert**: Verify the results and behavior.

## Naming Conventions
Test methods should be descriptive. A common pattern is:
`test[MethodName]_[Scenario]_[ExpectedOutcome]`
Example: `testReplace_WhenWarehouseNotFound_ThrowsException`

## Testing Scenarios

### Positive Conditions
Verify that the system behaves correctly when given valid inputs.
- Ensure state changes are correct.
- Ensure dependencies are called with expected parameters.

### Negative Conditions
Verify that the system handles invalid or unexpected inputs gracefully.
- Ensure correct exceptions are thrown.
- Ensure no unintended side effects occur.

### Error Conditions
Verify how the system behaves when external dependencies fail.
- Mock dependencies to throw exceptions.
- Verify that the system handles these errors (e.g., retries, logging, or re-throwing).

## Example: Mocking with Mockito

```java
@ExtendWith(MockitoExtension.class)
class MyServiceTest {
    @Mock
    MyRepository repository;

    @InjectMocks
    MyService service;

    @Test
    void testDoWork_Success() {
        // Arrange
        when(repository.find(anyString())).thenReturn(new Data());

        // Act
        service.doWork("input");

        // Assert
        verify(repository).save(any());
    }
}
```

## Running Tests
Tests can be run via Maven:
```bash
mvn test
```
Or for a specific class:
```bash
mvn test -Dtest=ClassName
```
