package vallterra.bookkeeper.test_record.factory;

import lombok.RequiredArgsConstructor;
import org.jooq.Record;
import org.jooq.UpdatableRecord;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import vallterra.bookkeeper.test_record.record.Impl.TestCharacter;
import vallterra.bookkeeper.test_record.record.Impl.TestVallterraUser;
import vallterra.bookkeeper.test_record.record.Impl.TestWikiUser;
import vallterra.bookkeeper.test_record.record.JooqTestRecord;

import java.io.Serializable;

@Service
@RequiredArgsConstructor
public class JooqTestRecordService {

    private final ApplicationContext ctx;

    /**
     * Creates a new instance of the specified JooqTestRecord type.
     */
    public <R extends Record & UpdatableRecord<R>, P extends Serializable, PK extends Serializable, S extends JooqTestRecord<R, P, PK, S>>
    S create(Class<S> testRecordType) {
        return ctx.getBean(testRecordType);
    }

    /**
     * Creates a new instance of TestCharacter with all required fields set to valid defaults.
     */
    public TestCharacter newTestCharacter() {
        return ctx.getBean(TestCharacter.class).createMinimal();
    }

    /**
     * Creates a new instance of TestCharacter with all fields set to valid defaults.
     */
    public TestCharacter newFullTestCharacter() {
        return ctx.getBean(TestCharacter.class).createFull();
    }

    /**
     * Creates a new instance of TestWikiUser with all required fields set to valid defaults.
     */
    public TestWikiUser newTestWikiUser() {
        return ctx.getBean(TestWikiUser.class).createMinimal();
    }

    /**
     * Creates a new instance of TestWikiUser with all fields set to valid defaults.
     */
    public TestWikiUser newFullTestWikiUser() {
        return ctx.getBean(TestWikiUser.class).createFull();
    }

    /**
     * Creates a new instance of TestVallterraUser with all required fields set to valid defaults.
     */
    public TestVallterraUser newTestVallterraUser() {
        return ctx.getBean(TestVallterraUser.class).createMinimal();
    }

    /**
     * Creates a new instance of TestVallterraUser with all fields set to valid defaults.
     */
    public TestVallterraUser newFullTestVallterraUser() {
        return ctx.getBean(TestVallterraUser.class).createFull();
    }

}
