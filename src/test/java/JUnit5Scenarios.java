import static org.assertj.core.api.BDDAssertions.then;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static org.junit.jupiter.api.Assumptions.assumingThat;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.assertj.core.util.Lists;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

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

    @Nested
    class SimpleJunit5Cases {

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
        @Disabled("You can add description here.")
        void shouldDisableTest() {

        }

        @Test
        void shouldPerformStandardAssertions() {
            // Given
            final short expected = 1;
            final short actual = 1;

            // Then
            assertEquals(expected, actual);
            assertEquals(expected, actual, "Expected is equal to actual.");
        }

    }

    @Nested
    class GroupedAssertions {

        @Test
        void shouldPerformGroupAssertions() {
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
        public void shouldMixGroupedAssertionsWithBDDThenAssertions() {
            // Given
            final short expected = 1;
            final short equal = 1;
            final short notEqual = 2;
            final short notEqualSecond = 4;

            // Then
            // All failed assertions will be reported. Test will not stop on first fail.
            assertAll("Grouped assertions mixed with BDD assertions.", //
                () -> then(equal).isEqualTo(expected), //
                () -> then(notEqual).isEqualTo(expected), // This will be reported as failed
                () -> then(equal).isEqualTo(expected), //
                () -> then(notEqualSecond).isEqualTo(expected) // This will be reported as failed
            );
        }
    }

    @Nested
    class ExceptionHandling {

        @Test
        void shouldThrowException() {
            // Given
            final Person person = new Person();

            // When
            final Throwable exception = assertThrows(NoTitleException.class, person::getTitle);

            // Then
            assertEquals("No title.", exception.getMessage());
        }
    }

    @Nested
    class Assumptions {

        @Test
        void shouldTestOnlyOnStationMachine() {
            // Assumptions
            final boolean stationMachine = "STATION".equals(System.getenv("COMPUTERNAME"));
            assumeTrue(stationMachine, "Aborting test. Not on STATION machine.");
        }

        @Test
        void shouldTestOnlyOnOuterSpaceMachine() {
            // Assumptions
            final boolean outerSpaceMachine = "OUTER_SPACE".equals(System.getenv("COMPUTERNAME"));
            assumeTrue(outerSpaceMachine, "Aborting test. Not on OUTER_SPACE machine.");
        }

        @Test
        void shouldPerformAdditionalAssertionsOnStationMachine() {
            // Given
            final boolean stationMachine = "STATION".equals(System.getenv("COMPUTERNAME"));
            final short expected = 1;
            final short equal = 1;
            final short notEqual = 2;

            // Then
            assumingThat(stationMachine, () -> {
                assertEquals(expected, equal, "Equal on station machine.");
                assertEquals(expected, notEqual, "Not equal on station machine."); // Not grouped - test will stop on first
                                                                                   // fail.
            });

            assertEquals(expected, equal, "Equal on any machine.");
            assertEquals(expected, notEqual, "Not equal on any machine.");
        }

        @Test
        void shouldPerformGroupedAdditionalAssertionsOnStationMachine() {
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
        void shouldPerformGroupedAdditionalAssertionsOnStationMachineRefactored() {
            // Given
            final boolean stationMachine = "STATION".equals(System.getenv("COMPUTERNAME"));
            final short expected = 1;
            final short actual = 1;

            // Then
            assertAll( //
                () -> assumingThat(stationMachine, () -> stationMachineAssertions(expected, actual)),
                () -> regularAssertions(expected, actual) //
            );
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

    @Nested
    class DynamicTests {

        @TestFactory
        Collection<DynamicTest> shouldCreateDynamicTestsFromCollection() {
            return Arrays.asList( //
                dynamicTest("1st dynamic test", () -> assertTrue(true)),
                dynamicTest("2nd dynamic test", () -> assertEquals(4, 2 * 2)) //
            );
        }

        @TestFactory
        Iterable<DynamicTest> shouldCreateDynamicTestsFromIterable() {
            return Arrays.asList( //
                dynamicTest("3rd dynamic test", () -> assertTrue(true)),
                dynamicTest("4th dynamic test", () -> assertEquals(4, 2 * 2)) //
            );
        }

        @TestFactory
        Iterator<DynamicTest> shouldCreateDynamicTestsFromIterator() {
            return Arrays.asList( //
                dynamicTest("5th dynamic test", () -> assertTrue(true)),
                dynamicTest("6th dynamic test", () -> assertEquals(4, 2 * 2)) //
            ).iterator();
        }

        @TestFactory
        Stream<DynamicTest> shouldCreateDynamicTestsFromStream() {
            return Stream //
                .of("A", "B", "C") //
                .map(name -> dynamicTest("test" + name, () -> {
                    /* ... */ }));
        }

        @TestFactory
        Stream<DynamicTest> shouldCreateDynamicTestsFromIntStream() {
            // Generates tests for the first 10 even integers.
            return IntStream //
                .iterate(0, n -> n + 2) //
                .limit(10) //
                .mapToObj(n -> dynamicTest("test" + n, () -> assertTrue(n % 2 == 0)));
        }

        @DisplayName("Parametrized test for even numbers.")
        @TestFactory
        Stream<DynamicTest> shouldCheckEvenNumbers() {
            //Given
            List<Integer> evenNumbers = Lists.newArrayList(2, 4, 6, 8);

            //When
            return evenNumbers
                .stream()
                .map(number -> DynamicTest.dynamicTest(
                    "Testing number: " + number,
                    () -> shouldCheckEvenNumber(number)
                ));
        }

        void shouldCheckEvenNumber(Integer number) {
            //When
            final boolean isEven = EvenNumberChecker.isEven(number);

            //Then
            then(isEven).isTrue();
        }

    }

    @Nested
    class TaggedTest {

        @Tag("fast")
        @Test
        void shouldBeTagged() {
            assertTrue(true);
        }

        @Fast
        @Test
        void shouldBeTaggedByCustomAnnotation() {
            assertTrue(true);
        }

    }
}
