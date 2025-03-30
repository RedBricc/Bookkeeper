package vallterra.bookkeeper.backend.user.impl;

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.generated.public_.tables.pojos.BookkeeperUser;
import org.springframework.stereotype.Repository;
import vallterra.bookkeeper.backend.user.BookkeeperUserRepository;

import java.util.Optional;

import static org.jooq.generated.public_.tables.BookkeeperUser.BOOKKEEPER_USER;

@Repository
@RequiredArgsConstructor
public class BookkeeperUserRepositoryImpl implements BookkeeperUserRepository {

    private final DSLContext db;

    @Override
    public BookkeeperUser getById(Integer bookkeeperUserId) {
        return db.selectFrom(BOOKKEEPER_USER)
                .where(BOOKKEEPER_USER.ID.eq(bookkeeperUserId))
                .fetchSingle().into(BookkeeperUser.class);
    }

    @Override
    public BookkeeperUser save(BookkeeperUser user) {
        var record = db.newRecord(BOOKKEEPER_USER, user);
        record.store();
        return record.into(BookkeeperUser.class);
    }

    @Override
    public Optional<BookkeeperUser> findById(Integer bookkeeperUserId) {
        return db.selectFrom(BOOKKEEPER_USER)
                .where(BOOKKEEPER_USER.ID.eq(bookkeeperUserId))
                .fetchOptionalInto(BookkeeperUser.class);
    }

    @Override
    public Optional<BookkeeperUser> findByUsername(String username) {
        return db.selectFrom(BOOKKEEPER_USER)
                .where(BOOKKEEPER_USER.USERNAME.eq(username))
                .fetchOptionalInto(BookkeeperUser.class);
    }
}
