package vallterra.bookkeeper.backend.quest.impl;

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.generated.tables.pojos.Quest;
import org.springframework.stereotype.Repository;
import vallterra.bookkeeper.backend.quest.QuestRepository;

import java.util.List;
import java.util.Optional;

import static org.jooq.generated.Tables.QUEST;

@Repository
@RequiredArgsConstructor
public class QuestRepositoryImpl implements QuestRepository {

    private final DSLContext db;

    @Override
    public List<Quest> findAll() {
        return db.selectFrom(QUEST)
                .fetchInto(Quest.class);
    }

    @Override
    public Optional<Quest> findById(Integer id) {
        return db.selectFrom(QUEST)
                .where(QUEST.ID.eq(id))
                .fetchOptionalInto(Quest.class);
    }

    @Override
    public Quest save(Quest quest) {
        var record = db.newRecord(QUEST, quest);
        record.store();
        return record.into(Quest.class);
    }

    @Override
    public void deleteById(Integer id) {
        db.deleteFrom(QUEST)
                .where(QUEST.ID.eq(id))
                .execute();
    }

    @Override
    public List<Quest> findByNameContaining(String name) {
        return db.selectFrom(QUEST)
                .where(QUEST.NAME.containsIgnoreCase(name))
                .fetchInto(Quest.class);
    }

    @Override
    public List<Quest> findByLocationContaining(String location) {
        return db.selectFrom(QUEST)
                .where(QUEST.LOCATION.containsIgnoreCase(location))
                .fetchInto(Quest.class);
    }
}
