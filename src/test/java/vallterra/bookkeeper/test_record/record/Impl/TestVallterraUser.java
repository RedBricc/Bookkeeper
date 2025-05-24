package vallterra.bookkeeper.test_record.record.Impl;

import org.jooq.DSLContext;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.generated.tables.VallterraUser;
import org.jooq.generated.tables.records.VallterraUserRecord;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import vallterra.bookkeeper.test_record.record.JooqTestRecord;

import static org.jooq.generated.tables.VallterraUser.VALLTERRA_USER;


@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class TestVallterraUser extends JooqTestRecord<VallterraUserRecord, VallterraUser, Integer, TestVallterraUser> {

    public TestVallterraUser(DSLContext db) {
        super(db);
    }

    @Override
    protected Table<VallterraUserRecord> getTable() {
        return VALLTERRA_USER;
    }

    @Override
    protected TableField<VallterraUserRecord, Integer> getIdField() {
        return VALLTERRA_USER.ID;
    }

    @Override
    protected Class<VallterraUser> getPojoClass() {
        return VallterraUser.class;
    }

    protected VallterraUserRecord setMinimal(VallterraUserRecord record) {
        return record
                .setPrefersDark(true)
                .setPrefersLarge(true)
                .setAllowLarge(true);
    }

    protected VallterraUserRecord setFull(VallterraUserRecord record) {
        return setMinimal(record)
                .setPlayerName("Test Player");
    }

}
