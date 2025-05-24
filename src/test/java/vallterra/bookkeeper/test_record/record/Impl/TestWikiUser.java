package vallterra.bookkeeper.test_record.record.Impl;

import org.jooq.DSLContext;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.generated.tables.pojos.WikiUser;
import org.jooq.generated.tables.records.WikiUserRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import vallterra.bookkeeper.test_record.factory.JooqTestRecordService;
import vallterra.bookkeeper.test_record.record.JooqTestRecord;

import static org.jooq.generated.tables.WikiUser.WIKI_USER;


@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class TestWikiUser extends JooqTestRecord<WikiUserRecord, WikiUser, Integer, TestWikiUser> {
    
    @Autowired
    private JooqTestRecordService jooqTestRecordService;

    public TestWikiUser(DSLContext db) {
        super(db);
    }

    @Override
    protected Table<WikiUserRecord> getTable() {
        return WIKI_USER;
    }

    @Override
    protected TableField<WikiUserRecord, Integer> getIdField() {
        return WIKI_USER.ID;
    }

    @Override
    protected Class<WikiUser> getPojoClass() {
        return WikiUser.class;
    }

    protected WikiUserRecord setMinimal(WikiUserRecord record) {
        var vallterraUser = jooqTestRecordService.newTestVallterraUser();
        
        return record
                .setVallterraUserId(vallterraUser.getId())
                .setUsername("Test User %s".formatted(vallterraUser.getId()))
                .setPassword("TestPassword123");
    }

    protected WikiUserRecord setFull(WikiUserRecord record) {
        return setMinimal(record)
                .setPType("p");
    }

}
