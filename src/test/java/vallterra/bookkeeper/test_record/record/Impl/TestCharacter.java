package vallterra.bookkeeper.test_record.record.Impl;

import org.jooq.DSLContext;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.generated.tables.pojos.Character;
import org.jooq.generated.tables.records.CharacterRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import vallterra.bookkeeper.test_record.factory.JooqTestRecordService;
import vallterra.bookkeeper.test_record.record.JooqTestRecord;

import static org.jooq.generated.tables.Character.CHARACTER;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class TestCharacter extends JooqTestRecord<CharacterRecord, Character, Integer, TestCharacter> {

    @Autowired
    private JooqTestRecordService jooqTestRecordService;

    public TestCharacter(DSLContext db) {
        super(db);
    }

    @Override
    protected Table<CharacterRecord> getTable() {
        return CHARACTER;
    }

    @Override
    protected TableField<CharacterRecord, Integer> getIdField() {
        return CHARACTER.ID;
    }

    @Override
    protected Class<Character> getPojoClass() {
        return Character.class;
    }

    protected CharacterRecord setMinimal(CharacterRecord record) {
        return record
                .setName("Test character")
                .setRace("Human")
                .setMainClass("Fighter")
                .setLevel(1)
                .setBackground("Commoner")
                .setAlignment("Neutral")
                .setLanguages("Common")
                .setSpeed(30)
                .setXp(100)
                .setNotes("Test character's notes");
    }

    protected CharacterRecord setFull(CharacterRecord record) {
        var wikiUser = jooqTestRecordService.newTestWikiUser();

        return setMinimal(record)
                .setWikiUserId(wikiUser.getId()) // Not all characters have a login
                .setTools("Brewer’s supplies")
                .setPassivePerception(10)
                .setPassiveInsight(10)
                .setInitiative(0)
                .setArmorClass(10)
                .setBio("Character created for testing")
                .setPoints(200)
                .setSlug("test-character")
                .setImage("test-character-asd783asd261das182asdd76d3ad78d612d387adsd162.jpg");
    }

}
