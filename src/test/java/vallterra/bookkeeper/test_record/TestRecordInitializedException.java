package vallterra.bookkeeper.test_record;

/**
 * Exception thrown when a test record is already initialized and should not be re-initialized.
 */
public class TestRecordInitializedException extends RuntimeException {
    public TestRecordInitializedException(String message) {
        super(message);
    }
}
