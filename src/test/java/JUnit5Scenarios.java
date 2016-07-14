import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class JUnit5Scenarios {

    @BeforeAll
    static void beforeAll() {
        System.out.println("JUnit5Scenarios.beforeAll");
    }

    @BeforeEach
    void beforeEach() {
        System.out.println("JUnit5Scenarios.beforeEach");
    }

    @Test
    void shouldRunPassingTest() {
        // Then
        assertEquals(3, 1 + 2, "equal");
    }

    @Test
    void shouldRunFailingTest() {
        // Then
        fail("failing test");
    }
}
