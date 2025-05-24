package vallterra.bookkeeper.test_record.record;

import org.jooq.Record;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.generated.tables.pojos.Character;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.transaction.annotation.Transactional;
import vallterra.bookkeeper.abstarct.AbstractDatabaseIntegrationTest;
import vallterra.bookkeeper.test_record.TestRecordInitializedException;
import vallterra.bookkeeper.test_record.TestRecordNotInitializedException;
import vallterra.bookkeeper.test_record.record.Impl.TestCharacter;

import java.lang.reflect.Method;
import java.util.stream.Stream;

import static org.jooq.generated.tables.Character.CHARACTER;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class JooqTestRecordTest extends AbstractDatabaseIntegrationTest {

    @Autowired
    private ApplicationContext applicationContext;

    private TestCharacter testCharacter;

    @BeforeEach
    public void setUp() {
        testCharacter = testRecordService.create(TestCharacter.class);
    }

    @Test
    void shouldCreateMinimal() {
        testCharacter.createMinimal();
        Integer id = testCharacter.getId();
        assertNotNull(id, "ID should not be null after createMinimal");

        var record = testCharacter.getRecord();

        assertNotNull(record, "Fetched record should not be null");
        assertOnlyMinimalFieldsSet(CHARACTER, CHARACTER.ID, record);
    }

    @Test
    public void shouldCreateMinimalAndFetch() {
        var pojo = testCharacter.createMinimalAndFetch();
        Integer id = testCharacter.getId();
        assertNotNull(id, "ID should not be null after createMinimal");

        var record = testCharacter.getRecord();

        assertNotNull(record, "Fetched record should not be null");
        assertOnlyMinimalFieldsSet(CHARACTER, CHARACTER.ID, record);

        assertEquals(record.into(Character.class), pojo, "Fetched POJO should match the record");
    }

    @Test
    public void shouldResetToMinimal() {
        var fullPojo = testCharacter.createFullAndFetch();
        testCharacter.resetToMinimal();

        var pojo = testCharacter.fetch();
        assertNotEquals(fullPojo, pojo, "Reset minimal POJO should not match initial full POJO");

        var record = testCharacter.getRecord();

        assertNotNull(record, "Record should not be null after resetToMinimal");
        assertOnlyMinimalFieldsSet(CHARACTER, CHARACTER.ID, record);

        assertEquals(record.into(Character.class), pojo, "Fetched POJO should match the record");
    }

    @Test
    public void shouldCreateFull() {
        testCharacter.createFull();
        Integer id = testCharacter.getId();
        assertNotNull(id, "ID should not be null after createFull");

        var record = testCharacter.getRecord();

        assertNotNull(record, "Fetched record should not be null");
        assertAllFieldsSet(CHARACTER, record);
    }

    @Test
    public void shouldCreateFullAndFetch() {
        var pojo = testCharacter.createFullAndFetch();
        assertNotNull(pojo.getId(), "ID should not be null after createFullAndFetch");

        var record = testCharacter.getRecord();

        assertAllFieldsSet(CHARACTER, record);
        assertEquals(record.into(Character.class), pojo, "Fetched POJO should match the record");
    }

    @Test
    public void shouldResetToFull() {
        var minimalPojo = testCharacter.createMinimalAndFetch();
        testCharacter.resetToFull();

        var pojo = testCharacter.fetch();
        assertNotEquals(minimalPojo, pojo, "Reset full POJO should not match initial minimal POJO");

        var record = testCharacter.getRecord();

        assertNotNull(record, "Record should not be null after resetToMinimal");
        assertAllFieldsSet(CHARACTER, record);

        assertEquals(record.into(Character.class), pojo, "Fetched POJO should match the record");
    }

    @Test
    public void shouldUpdate() {
        testCharacter.createMinimal();
        var updated = testCharacter.update(pojo ->
                pojo.setName("Updated Name")
        );

        assertEquals("Updated Name", updated.fetch().getName(), "Name should be updated");
    }

    @Test
    public void shouldUpdateAndFetch() {
        testCharacter.createMinimal();
        var updated = testCharacter.updateAndFetch(pojo ->
                pojo.setName("Updated Name")
        );

        assertEquals("Updated Name", updated.getName(), "Name should be updated");
    }

    @Test
    public void shouldCreateMinimalWithOverrides() {
        testCharacter.createMinimal(pojo -> pojo.setName("Custom Name"));
        Integer id = testCharacter.getId();
        assertNotNull(id, "ID should not be null after createMinimal with overrides");

        var record = testCharacter.getRecord();

        assertNotNull(record, "Fetched record should not be null");
        assertOnlyMinimalFieldsSet(CHARACTER, CHARACTER.ID, record);
        assertEquals("Custom Name", record.getName(), "Name should match the override");
    }

    @Test
    public void shouldCreateFullWithOverrides() {
        testCharacter.createFull(pojo -> pojo.setName("Custom Full Name"));
        Integer id = testCharacter.getId();
        assertNotNull(id, "ID should not be null after createFull with overrides");

        var record = testCharacter.getRecord();

        assertNotNull(record, "Fetched record should not be null");
        assertAllFieldsSet(CHARACTER, record);
        assertEquals("Custom Full Name", record.getName(), "Name should match the override");
    }

    @Test
    public void shouldDeleteAndBeInactive() {
        testCharacter.createMinimal();
        testCharacter.delete();

        assertFalse(testCharacter.isActive(), "Record should not be active after delete");
        assertThrows(TestRecordNotInitializedException.class, testCharacter::getId,
                "getId() should throw if record is not initialized");
    }

    @Test
    public void shouldThrowWhenDoubleCreateMinimal() {
        testCharacter.createMinimal();

        assertThrows(TestRecordInitializedException.class,
                () -> testCharacter.createMinimal(),
                "Creating twice should throw TestRecordInitializedException");
    }

    @Test
    public void shouldThrowWhenDoubleCreateFull() {
        testCharacter.createFull();

        assertThrows(TestRecordInitializedException.class,
                () -> testCharacter.createFull(),
                "Creating twice should throw TestRecordInitializedException");
    }

    @Test
    public void shouldThrowWhenNotInitialized() {
        assertThrows(TestRecordNotInitializedException.class,
                () -> testCharacter.getId(),
                "getId() should throw when not initialized");
        assertThrows(TestRecordNotInitializedException.class,
                () -> testCharacter.fetch(),
                "fetch() should throw when not initialized");
        assertThrows(TestRecordNotInitializedException.class,
                () -> testCharacter.update(p -> p),
                "update() should throw when not initialized");
        assertThrows(TestRecordNotInitializedException.class,
                () -> testCharacter.resetToMinimal(),
                "resetToMinimal() should throw when not initialized");
        assertThrows(TestRecordNotInitializedException.class,
                () -> testCharacter.resetToFull(),
                "resetToFull() should throw when not initialized");
    }

    /**
     * Dynamically test each JooqTestRecord implementation
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    @TestFactory
    public Stream<DynamicTest> shouldSetAllRequiredFieldsInMinimal() {
        var beans = applicationContext.getBeansOfType(JooqTestRecord.class, false, true).values();

        return beans.stream().map(recordInstance -> {
            String name = recordInstance.getClass().getSimpleName();
            return DynamicTest.dynamicTest(
                    "Minimal sets all non-nullable fields for " + name,
                    () -> {
                        Table table = recordInstance.getTable();
                        TableField idField = recordInstance.getIdField();
                        Record record = db.newRecord(table);

                        Method setMinimal = recordInstance.getClass()
                                .getDeclaredMethod("setMinimal", record.getClass());
                        setMinimal.setAccessible(true);
                        Record updated = (Record) setMinimal.invoke(recordInstance, record);

                        assertOnlyMinimalFieldsSet(table, idField, updated);
                    }
            );
        });
    }

    /**
     * Dynamically test each JooqTestRecord implementation
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    @TestFactory
    public Stream<DynamicTest> shouldSetAllFieldsInFull() {
        var beans = applicationContext.getBeansOfType(JooqTestRecord.class, false, true).values();

        return beans.stream().map(recordInstance -> {
            String name = recordInstance.getClass().getSimpleName();
            return DynamicTest.dynamicTest(
                    "Full sets all fields for " + name,
                    () -> {
                        Table table = recordInstance.getTable();
                        Record record = db.newRecord(table);

                        Method setFull = recordInstance.getClass()
                                .getDeclaredMethod("setFull", record.getClass());
                        setFull.setAccessible(true);
                        Record updated = (Record) setFull.invoke(recordInstance, record);

                        assertAllFieldsSet(table, updated);
                    }
            );
        });
    }

    private <R extends Record> void assertOnlyMinimalFieldsSet(Table<R> table, TableField<R, ?> idField, R updated) {
        for (var f : table.fields()) {
            if (f.equals(idField)) continue;

            if (!f.getDataType().nullable()) {
                assertNotNull(updated.get(f),
                        "Field '" + f.getName() + "' should be non-null in minimal");
            } else {
                assertNull(updated.get(f),
                        "Field '" + f.getName() + "' should be null in minimal");
            }
        }
    }

    private <R extends Record> void assertAllFieldsSet(Table<R> table, R updated) {
        for (var f : table.fields()) {
            assertNotNull(updated.get(f),
                    "Field '" + f.getName() + "' should not be null in full");
        }
    }
}
