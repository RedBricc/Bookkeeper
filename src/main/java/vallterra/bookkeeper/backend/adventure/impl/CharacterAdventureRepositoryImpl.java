package vallterra.bookkeeper.backend.adventure.impl;

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.generated.tables.pojos.CharacterAdventure;
import org.springframework.stereotype.Repository;
import vallterra.bookkeeper.backend.adventure.CharacterAdventureRepository;

import java.util.List;
import java.util.Optional;

import static org.jooq.generated.Tables.CHARACTER_ADVENTURE;

@Repository
@RequiredArgsConstructor
public class CharacterAdventureRepositoryImpl implements CharacterAdventureRepository {

    private final DSLContext db;

    @Override
    public List<CharacterAdventure> findAll() {
        return db.selectFrom(CHARACTER_ADVENTURE)
                .fetchInto(CharacterAdventure.class);
    }

    @Override
    public Optional<CharacterAdventure> findById(Integer id) {
        return db.selectFrom(CHARACTER_ADVENTURE)
                .where(CHARACTER_ADVENTURE.ID.eq(id))
                .fetchOptionalInto(CharacterAdventure.class);
    }

    @Override
    public CharacterAdventure save(CharacterAdventure characterAdventure) {
        var record = db.newRecord(CHARACTER_ADVENTURE, characterAdventure);
        record.store();
        return record.into(CharacterAdventure.class);
    }

    @Override
    public void deleteById(Integer id) {
        db.deleteFrom(CHARACTER_ADVENTURE)
                .where(CHARACTER_ADVENTURE.ID.eq(id))
                .execute();
    }

    @Override
    public List<CharacterAdventure> findByCharacterId(Integer characterId) {
        return db.selectFrom(CHARACTER_ADVENTURE)
                .where(CHARACTER_ADVENTURE.CHARACTER_ID.eq(characterId))
                .fetchInto(CharacterAdventure.class);
    }

    @Override
    public List<CharacterAdventure> findByAdventureId(Integer adventureId) {
        return db.selectFrom(CHARACTER_ADVENTURE)
                .where(CHARACTER_ADVENTURE.ADVENTURE_ID.eq(adventureId))
                .fetchInto(CharacterAdventure.class);
    }
}
