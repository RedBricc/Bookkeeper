package vallterra.bookkeeper.backend.adventure.impl;

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.generated.tables.pojos.Adventure;
import org.springframework.stereotype.Repository;
import vallterra.bookkeeper.backend.adventure.AdventureRepository;

import java.util.List;
import java.util.Optional;

import static org.jooq.generated.Tables.ADVENTURE;

@Repository
@RequiredArgsConstructor
public class AdventureRepositoryImpl implements AdventureRepository {

    private final DSLContext db;

    @Override
    public List<Adventure> findAll() {
        return db.selectFrom(ADVENTURE)
                .fetchInto(Adventure.class);
    }

    @Override
    public Optional<Adventure> findById(Integer id) {
        return db.selectFrom(ADVENTURE)
                .where(ADVENTURE.ID.eq(id))
                .fetchOptionalInto(Adventure.class);
    }

    @Override
    public Adventure save(Adventure adventure) {
        var record = db.newRecord(ADVENTURE, adventure);
        record.store();
        return record.into(Adventure.class);
    }

    @Override
    public void deleteById(Integer id) {
        db.deleteFrom(ADVENTURE)
                .where(ADVENTURE.ID.eq(id))
                .execute();
    }

    @Override
    public List<Adventure> findByQuestId(Integer questId) {
        return db.selectFrom(ADVENTURE)
                .where(ADVENTURE.QUEST_ID.eq(questId))
                .fetchInto(Adventure.class);
    }

    @Override
    public List<Adventure> findBySlugContaining(String slug) {
        return db.selectFrom(ADVENTURE)
                .where(ADVENTURE.SLUG.containsIgnoreCase(slug))
                .fetchInto(Adventure.class);
    }
}
