package vallterra.bookkeeper.backend.user.impl;

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.generated.tables.pojos.Character;
import org.springframework.stereotype.Repository;
import vallterra.bookkeeper.backend.user.CharacterRepository;

import java.util.List;
import java.util.Optional;

import static org.jooq.generated.Tables.CHARACTER;

@Repository
@RequiredArgsConstructor
public class CharacterRepositoryImpl implements CharacterRepository {

    private final DSLContext db;

    @Override
    public List<Character> findAll() {
        return db.selectFrom(CHARACTER)
                .fetchInto(Character.class);
    }

    @Override
    public Optional<Character> findById(Integer id) {
        return db.selectFrom(CHARACTER)
                .where(CHARACTER.ID.eq(id))
                .fetchOptionalInto(Character.class);
    }

    @Override
    public Character save(Character character) {
        var record = db.newRecord(CHARACTER, character);
        record.store();
        return record.into(Character.class);
    }

    @Override
    public void deleteById(Integer id) {
        db.deleteFrom(CHARACTER)
                .where(CHARACTER.ID.eq(id))
                .execute();
    }

    @Override
    public List<Character> findByNameContaining(String name) {
        return db.selectFrom(CHARACTER)
                .where(CHARACTER.NAME.containsIgnoreCase(name))
                .fetchInto(Character.class);
    }

    @Override
    public List<Character> findByVallterraUserId(Long vallterraUserId) {
        return db.selectFrom(CHARACTER)
                .where(CHARACTER.VALLTERRA_USER_ID.eq(vallterraUserId))
                .fetchInto(Character.class);
    }
}