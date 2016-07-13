import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class JUnit5Scenarios {

    @Test
    void shouldRunTest() {
        // Then
        assertEquals(3, 1 + 2, "equal");
    }
}
