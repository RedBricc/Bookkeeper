package vallterra.bookkeeper.test_record;

/**
 * Exception thrown when attempting to perform operations on a test record that has not been initialized.
 */
public class TestRecordNotInitializedException extends RuntimeException {

    public TestRecordNotInitializedException() {
        super("Test record has not been initialized.");
    }

    public TestRecordNotInitializedException(String message) {
        super(message);
    }

}
