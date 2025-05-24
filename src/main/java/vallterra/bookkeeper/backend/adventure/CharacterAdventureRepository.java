package vallterra.bookkeeper.backend.adventure;

import jakarta.validation.constraints.NotNull;
import org.jooq.generated.tables.pojos.CharacterAdventure;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Optional;

/**
 * Repository for managing {@link CharacterAdventure} records.
 * This repository manages the many-to-many relationship between characters and adventures.
 */
@Validated
public interface CharacterAdventureRepository {

    /**
     * Find all character-adventure relationships.
     *
     * @return a list of all character-adventure relationships
     */
    List<CharacterAdventure> findAll();

    /**
     * Find a character-adventure relationship by its ID.
     *
     * @param id the relationship ID
     * @return the relationship, or empty if not found
     */
    Optional<CharacterAdventure> findById(@NotNull Integer id);

    /**
     * Save a character-adventure relationship.
     *
     * @param characterAdventure the relationship to save
     * @return the saved relationship
     */
    CharacterAdventure save(@NotNull CharacterAdventure characterAdventure);

    /**
     * Delete a character-adventure relationship by its ID.
     *
     * @param id the relationship ID
     */
    void deleteById(@NotNull Integer id);

    /**
     * Find character-adventure relationships by character ID.
     *
     * @param characterId the character ID
     * @return a list of relationships for the given character
     */
    List<CharacterAdventure> findByCharacterId(@NotNull Integer characterId);

    /**
     * Find character-adventure relationships by adventure ID.
     *
     * @param adventureId the adventure ID
     * @return a list of relationships for the given adventure
     */
    List<CharacterAdventure> findByAdventureId(@NotNull Integer adventureId);
}
