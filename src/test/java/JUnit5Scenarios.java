import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.expectThrows;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static org.junit.jupiter.api.Assumptions.assumingThat;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Learning tests for junit5.")
class JUnit5Scenarios {

    @BeforeAll
    static void beforeAll() {
        System.out.println("JUnit5Scenarios.beforeAll");
    }

    @AfterAll
    static void afterAll() {
        System.out.println("JUnit5Scenarios.afterAll");
    }

    @BeforeEach
    void beforeEach() {
        System.out.println("JUnit5Scenarios.beforeEach");
    }

    @AfterEach
    void afterEach() {
        System.out.println("JUnit5Scenarios.afterEach");
    }

    @Test
    @DisplayName("Display this description instead of test name.")
    void shouldRunPassingTest() {
        // Then
        assertEquals(3, 1 + 2, "equal");
    }

    @Test
    void shouldRunFailingTest() {
        // Then
        fail("failing test");
    }

    @Test
    @Disabled
    public void shouldDisableTest() {

    }

    @Test
    public void shouldPerformStandardAssertions() {
        // Given
        final short expected = 1;
        final short actual = 1;

        // Then
        assertEquals(expected, actual);
        assertEquals(expected, actual, "Expected is equal to actual.");
    }

    @Test
    public void shouldPerformGroupAssertions() {
        // Given
        final short expected = 1;
        final short equal = 1;
        final short notEqual = 2;

        // Then
        // All failed assertions will be reported. Test will not stop on first fail.
        assertAll("Grouped assertions.", //
            () -> assertEquals(expected, equal, "Equal 1."), //
            () -> assertEquals(expected, notEqual, "Not equal 1"), // This will be reported as failed
            () -> assertEquals(expected, equal, "Equal 2."), //
            () -> assertEquals(expected, notEqual, "Not equal 2")); // This will be reported as failed
    }

    @Test
    public void shouldThrowException() {
        // Given
        final Person person = new Person();

        // When
        final Throwable exception = expectThrows(NoTitleException.class, person::getTitle);

        // Then
        assertEquals("No title.", exception.getMessage());
    }

    @Test
    public void shouldTestOnlyOnStationMachine() {
        // Assumptions
        final boolean stationMachine = "STATION".equals(System.getenv("COMPUTERNAME"));
        assumeTrue(stationMachine, "Aborting test. Not on STATION machine.");
    }

    @Test
    public void shouldTestOnlyOnOuterSpaceMachine() {
        // Assumptions
        final boolean outerSpaceMachine = "OUTER_SPACE".equals(System.getenv("COMPUTERNAME"));
        assumeTrue(outerSpaceMachine, "Aborting test. Not on OUTER_SPACE machine.");
    }

    @Test
    public void shouldPerformAdditionalAssertionsOnStationMachine() {
        // Given
        final boolean stationMachine = "STATION".equals(System.getenv("COMPUTERNAME"));
        final short expected = 1;
        final short equal = 1;
        final short notEqual = 2;

        // Then
        assumingThat(stationMachine, () -> {
            assertEquals(expected, equal, "Equal on station machine.");
            assertEquals(expected, notEqual, "Not equal on station machine."); // Not grouped - test will stop on first fail.
        });

        assertEquals(expected, equal, "Equal on any machine.");
        assertEquals(expected, notEqual, "Not equal on any machine.");
    }

    @Test
    public void shouldPerformGroupedAdditionalAssertionsOnStationMachine() {
        // Given
        final boolean stationMachine = "STATION".equals(System.getenv("COMPUTERNAME"));
        final short expected = 1;
        final short equal = 1;
        final short notEqual = 2;

        // Then
        // All failed assertions will be reported.
        assertAll( //
            () -> assumingThat(stationMachine, () -> {
                assertAll(() -> assertTrue(false, "Station assert fail"),
                    () -> assertEquals(expected, equal, "Equal on station machine."),
                    () -> assertEquals(expected, notEqual, "Not equal on station machine."));
            }), //

            () -> assertTrue(false, "Regular assert fail"), () -> assertEquals(expected, equal, "Equal on any machine."), //
            () -> assertEquals(expected, notEqual, "Not equal on any machine."));
    }

    @Test
    public void shouldPerformGroupedAdditionalAssertionsOnStationMachineRefactored() {
        // Given
        final boolean stationMachine = "STATION".equals(System.getenv("COMPUTERNAME"));
        final short expected = 1;
        final short actual = 1;

        // Then
        assertAll( //
            () -> assumingThat(stationMachine, () -> stationMachineAssertions(expected, actual)),
            () -> regularAssertions(expected, actual));
    }

    private void stationMachineAssertions(final short expected, final short equal) {
        assertAll( //
            () -> assertTrue(false, "First station assert fail."),
            () -> assertEquals(expected, equal, "Equal on station machine."),
            () -> assertTrue(false, "Second station assert fail.") //
        );
    }

    private void regularAssertions(final short expected, final short equal) {
        assertAll( //
            () -> assertTrue(false, "First regular assert fail."),
            () -> assertEquals(expected, equal, "Equal on any machine."), //
            () -> assertTrue(false, "Second regular assert fail.") //
        );
    }
}
