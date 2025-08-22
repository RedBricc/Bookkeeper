package vallterra.bookkeeper.backend.user.impl;

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.generated.tables.pojos.WikiUser;
import org.springframework.stereotype.Repository;
import vallterra.bookkeeper.backend.user.WikiUserRepository;

import static org.jooq.generated.Tables.WIKI_USER;

@Repository
@RequiredArgsConstructor
public class WikiUserRepositoryImpl implements WikiUserRepository {

    private final DSLContext db;

    @Override
    public WikiUser getById(Integer wikiUserId) {
        return db.selectFrom(WIKI_USER)
                .where(WIKI_USER.ID.eq(wikiUserId))
                .fetchSingleInto(WikiUser.class);
    }

    @Override
    public WikiUser save(WikiUser user) {
        var record = db.newRecord(WIKI_USER, user);
        record.store();
        return record.into(WikiUser.class);
    }

}
