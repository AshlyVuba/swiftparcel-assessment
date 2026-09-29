package za.co.swiftparcel;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

/**
 * Q3.2 - ParcelValidator has no meaningful test coverage yet.
 * The rules it must follow are written in the Javadoc of ParcelValidator.
 * One worked example is provided. Add your own tests below it.
 */
class ParcelValidatorTest {

    @Test
    void requireValidWeight_acceptsTypicalWeight() {
        assertDoesNotThrow(() -> ParcelValidator.requireValidWeight(15.0));
    }

    // TODO (Q3.2): add at least SIX more tests. Do not edit the test above.
}
