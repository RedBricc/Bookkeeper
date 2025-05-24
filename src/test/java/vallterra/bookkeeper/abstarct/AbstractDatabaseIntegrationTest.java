package vallterra.bookkeeper.abstarct;

import org.jooq.DSLContext;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.reflections.Reflections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import vallterra.bookkeeper.test_record.factory.JooqTestRecordService;
import vallterra.bookkeeper.test_record.record.JooqTestRecord;

import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

@Rollback
@Transactional
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class AbstractDatabaseIntegrationTest extends AbstractTest {

    @Autowired
    protected DSLContext db;

    @Autowired
    protected JooqTestRecordService testRecordService;

    @Container
    static final PostgreSQLContainer<?> POSTGRES = createPostgresContainer();

    private static final int MAX_MINIMAL_RECORDS = 10;
    private static final int MIN_MINIMAL_RECORDS = 3;
    private static final int MAX_FULL_RECORDS = 10;
    private static final int MIN_FULL_RECORDS = 3;

    private static final AtomicBoolean initialized = new AtomicBoolean(false);

    private static PostgreSQLContainer<?> createPostgresContainer() {
        try (var container = new PostgreSQLContainer<>("postgres:17")) {
            return container
                    .withDatabaseName("test_db")
                    .withUsername("bookkeeper")
                    .withPassword("test")
                    .withReuse(true);
        }
    }

    static {
        POSTGRES.start();
    }

    @BeforeAll
    public void initDb() {
        if (initialized.getAndSet(true)) {
            return;
        }

        generateRecords();
    }

    /**
     * Generate random amounts of both minimal and full test records to create a more realistic test environment.
     * This is done to catch issues where the ID of one record is used to access a different type of record,
     * e.g. an adventure ID is used to access a character_adventure record - in a blank database both records would have an ID of 1 and the test would pass,
     * but would result in unexpected behavior in a real-world scenario.
     */
    @SuppressWarnings("unchecked")
    private void generateRecords() {
        var ref = new Reflections("vallterra.bookkeeper.test_record");
        var implementations = ref.getSubTypesOf(JooqTestRecord.class);
        var rnd = new Random();

        for (var impl : implementations) {
            int minimalCount = rnd.nextInt(MAX_MINIMAL_RECORDS - MIN_MINIMAL_RECORDS + 1) + MIN_MINIMAL_RECORDS;
            for (int i = 0; i < minimalCount; i++) {
                testRecordService.create(impl).createMinimal();
            }

            int fullCount = rnd.nextInt(MAX_FULL_RECORDS - MIN_FULL_RECORDS + 1) + MIN_FULL_RECORDS;
            for (int i = 0; i < fullCount; i++) {
                testRecordService.create(impl).createFull();
            }
        }
    }

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
    }

}
