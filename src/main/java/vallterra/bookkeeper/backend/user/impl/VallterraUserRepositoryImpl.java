package vallterra.bookkeeper.backend.user.impl;

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.generated.public_.tables.pojos.VallterraUser;
import org.springframework.stereotype.Repository;
import vallterra.bookkeeper.backend.user.VallterraUserRepository;

import java.util.Optional;

import static org.jooq.generated.public_.Tables.VALLTERRA_USER;

@Repository
@RequiredArgsConstructor
public class VallterraUserRepositoryImpl implements VallterraUserRepository {

    private final DSLContext db;

    @Override
    public VallterraUser save(VallterraUser user) {
        var record = db.newRecord(VALLTERRA_USER, user);
        record.store();
        return record.into(VallterraUser.class);
    }

    @Override
    public VallterraUser getById(Integer vallterraUserId) {
        return db.selectFrom(VALLTERRA_USER)
                .where(VALLTERRA_USER.ID.eq(vallterraUserId))
                .fetchSingle().into(VallterraUser.class);
    }

    @Override
    public Optional<VallterraUser> findById(Integer vallterraUserId) {
        return db.selectFrom(VALLTERRA_USER)
                .where(VALLTERRA_USER.ID.eq(vallterraUserId))
                .fetchOptionalInto(VallterraUser.class);
    }
}
