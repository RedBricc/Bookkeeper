package vallterra.bookkeeper.backend.provider;

import com.vaadin.flow.data.provider.Query;
import org.jooq.Condition;
import org.jooq.generated.tables.records.CharacterRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.BadSqlGrammarException;
import vallterra.bookkeeper.abstarct.AbstractDatabaseIntegrationTest;
import vallterra.bookkeeper.ui.data.ContextAccess;

import java.util.UUID;

import static org.jooq.generated.tables.Character.CHARACTER;
import static org.jooq.generated.tables.WikiUser.WIKI_USER;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class JooqDataProviderTest extends AbstractDatabaseIntegrationTest {

    @Autowired
    private ContextAccess contextAccess;

    private JooqDataProvider<CharacterRecord, Void> dataProvider;

    @BeforeEach
    void setUp() {
        dataProvider = spy(new JooqDataProvider<>(CHARACTER, contextAccess));
    }

    @Test
    void testConstructorInitialization() {
        assertNotNull(dataProvider);
        assertEquals(CHARACTER, dataProvider.getTable());
        assertFalse(dataProvider.hasConditions());
    }

    @Test
    void testRegisterCondition() {
        var character = testRecordService.newTestCharacter();
        Condition condition = CHARACTER.ID.eq(character.getId());

        UUID conditionUuid = dataProvider.registerCondition(condition);

        assertNotNull(conditionUuid);
        assertTrue(dataProvider.hasConditions());
        assertTrue(dataProvider.fetchFromBackEnd(new Query<>())
                .allMatch(record -> record.getId().equals(character.getId()))
        );
    }

    @Test
    void testRegisterNullCondition() {
        UUID id = dataProvider.registerCondition(null);

        assertNotNull(id);
        assertFalse(dataProvider.hasConditions());
    }

    @Test
    void testApplyCondition() {
        var character1 = testRecordService
                .newTestCharacter()
                .update(c -> c.setLevel(4)
                        .setInitiative(3));
        var character2 = testRecordService
                .newTestCharacter()
                .update(c -> c.setLevel(5)
                        .setInitiative(5));

        Condition condition1 = CHARACTER.LEVEL.ge(4);
        Condition condition2 = CHARACTER.INITIATIVE.ge(5);

        UUID id = dataProvider.registerCondition(condition1);

        assertTrue(dataProvider.hasConditions());
        assertEquals(2, dataProvider.fetchFromBackEnd(new Query<>())
                .filter(record -> record.getId().equals(character1.getId())
                        || record.getId().equals(character2.getId()))
                .count());

        dataProvider.applyCondition(id, condition2);

        assertTrue(dataProvider.hasConditions());
        assertTrue(dataProvider.fetchFromBackEnd(new Query<>())
                .allMatch(record -> record.getId().equals(character2.getId()))
        );

        verify(dataProvider).refreshAll();
    }

    @Test
    void testRemoveCondition() {
        var character1 = testRecordService.newTestCharacter();
        var character2 = testRecordService.newTestCharacter();

        Condition condition = CHARACTER.ID.eq(character1.getId());
        UUID id = dataProvider.registerCondition(condition);

        assertTrue(dataProvider.hasConditions());
        assertTrue(dataProvider.fetchFromBackEnd(new Query<>())
                .allMatch(record -> record.getId().equals(character1.getId()))
        );

        dataProvider.removeCondition(id);

        assertFalse(dataProvider.hasConditions());
        assertTrue(dataProvider.fetchFromBackEnd(new Query<>())
                .anyMatch(record -> record.getId().equals(character1.getId()))
        );
        assertTrue(dataProvider.fetchFromBackEnd(new Query<>())
                .anyMatch(record -> record.getId().equals(character2.getId()))
        );

        verify(dataProvider).refreshAll();
    }

    @Test
    void testClearConditions() {
        var character1 = testRecordService.newTestCharacter();
        var character2 = testRecordService.newTestCharacter();

        dataProvider.registerCondition(CHARACTER.ID.eq(character1.getId()));
        dataProvider.registerCondition(CHARACTER.ID.eq(character2.getId()));

        assertTrue(dataProvider.hasConditions());
        assertTrue(dataProvider.fetchFromBackEnd(new Query<>())
                .noneMatch(record -> record.getId().equals(character1.getId())
                        || record.getId().equals(character2.getId()))
        );

        dataProvider.clearConditions();

        assertFalse(dataProvider.hasConditions());
        assertTrue(dataProvider.fetchFromBackEnd(new Query<>())
                .anyMatch(record -> record.getId().equals(character1.getId()))
        );
        assertTrue(dataProvider.fetchFromBackEnd(new Query<>())
                .anyMatch(record -> record.getId().equals(character2.getId()))
        );

        verify(dataProvider).refreshAll();
    }

    @Test
    void testAddFixedCondition() {
        var character = testRecordService.newTestCharacter();

        Condition fixedCondition = CHARACTER.ID.isDistinctFrom(character.getId());

        dataProvider.addFixedCondition(fixedCondition);

        assertFalse(dataProvider.hasConditions());
        assertTrue(dataProvider.fetchFromBackEnd(new Query<>())
                .noneMatch(record -> record.getId().equals(character.getId()))
        );
        verify(dataProvider, never()).refreshAll();

        dataProvider.clearConditions();

        assertFalse(dataProvider.hasConditions());
        assertTrue(dataProvider.fetchFromBackEnd(new Query<>())
                .noneMatch(record -> record.getId().equals(character.getId()))
        );
        verify(dataProvider).refreshAll();

        dataProvider.clearFixedConditions(false);

        assertFalse(dataProvider.hasConditions());
        assertTrue(dataProvider.fetchFromBackEnd(new Query<>())
                .anyMatch(record -> record.getId().equals(character.getId()))
        );
        verify(dataProvider, atMostOnce()).refreshAll();

        dataProvider.clearFixedConditions(true);

        verify(dataProvider, times(2)).refreshAll();
    }

    @Test
    void testMultipleConditions() {
        var character1 = testRecordService.newTestCharacter()
                .update(c -> c.setLevel(1));
        var character2 = testRecordService.newTestCharacter()
                .update(c -> c.setLevel(4));
        var character3 = testRecordService.newTestCharacter()
                .update(c -> c.setLevel(10));

        dataProvider.registerCondition(CHARACTER.LEVEL.ge(3));
        dataProvider.registerCondition(CHARACTER.LEVEL.le(7));

        assertTrue(dataProvider.hasConditions());
        assertTrue(dataProvider.fetchFromBackEnd(new Query<>())
                        .allMatch(record -> record.getLevel() >= 3 && record.getLevel() <= 7)
        );
        assertTrue(dataProvider.fetchFromBackEnd(new Query<>())
                .noneMatch(record -> character1.getId().equals(record.getId())
                        || character3.getId().equals(record.getId()))
        );
        assertTrue(dataProvider.fetchFromBackEnd(new Query<>())
                .anyMatch(record -> record.getId().equals(character2.getId()))
        );

        dataProvider.clearConditions();
        assertFalse(dataProvider.hasConditions());

        assertTrue(dataProvider.fetchFromBackEnd(new Query<>())
                .anyMatch(record -> record.getId().equals(character1.getId()))
        );
        assertTrue(dataProvider.fetchFromBackEnd(new Query<>())
                .anyMatch(record -> record.getId().equals(character2.getId()))
        );
        assertTrue(dataProvider.fetchFromBackEnd(new Query<>())
                .anyMatch(record -> record.getId().equals(character3.getId()))
        );
    }

    @Test
    void testFixedConditionsWithRegularConditions() {
        var character1 = testRecordService.newTestCharacter()
                .update(c -> c.setLevel(1));
        var character2 = testRecordService.newTestCharacter()
                .update(c -> c.setLevel(4));
        var character3 = testRecordService.newTestCharacter()
                .update(c -> c.setLevel(10));

        dataProvider.addFixedCondition(CHARACTER.LEVEL.le(7));
        dataProvider.registerCondition(CHARACTER.LEVEL.ge(3));

        assertTrue(dataProvider.hasConditions());
        assertTrue(dataProvider.fetchFromBackEnd(new Query<>())
                .allMatch(record -> record.getLevel() >= 3 && record.getLevel() <= 7)
        );
        assertTrue(dataProvider.fetchFromBackEnd(new Query<>())
                .noneMatch(record -> character1.getId().equals(record.getId())
                        || character3.getId().equals(record.getId()))
        );
        assertTrue(dataProvider.fetchFromBackEnd(new Query<>())
                .anyMatch(record -> record.getId().equals(character2.getId()))
        );

        dataProvider.clearConditions();

        assertFalse(dataProvider.hasConditions());
        assertTrue(dataProvider.fetchFromBackEnd(new Query<>())
                .anyMatch(record -> record.getId().equals(character1.getId()))
        );
        assertTrue(dataProvider.fetchFromBackEnd(new Query<>())
                .anyMatch(record -> record.getId().equals(character2.getId()))
        );
        assertFalse(dataProvider.fetchFromBackEnd(new Query<>())
                .anyMatch(record -> record.getId().equals(character3.getId()))
        );
    }

    @Test
    void testInvalidField() {
        dataProvider.registerCondition(WIKI_USER.ID.isNotNull());

        assertTrue(dataProvider.hasConditions());
        assertThrows(BadSqlGrammarException.class, () -> dataProvider.fetchFromBackEnd(new Query<>())
                .anyMatch(record -> record.getId() != null), "Fetching from a different table should throw an exception");
    }
}
