package vallterra.bookkeeper.backend.user;

import jakarta.validation.constraints.NotNull;
import org.jooq.generated.tables.pojos.Character;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Optional;

/**
 * Repository for {@link Character} records.
 * <p>
 *  A character represents a user's in-game character. This repository is used to access
 *  character-specific data.
 * </p>
 */
@Validated
public interface CharacterRepository {

    /**
     * Find all characters.
     *
     * @return a list of all characters
     */
    List<Character> findAll();

    /**
     * Find a character by its ID.
     *
     * @param id the character ID
     * @return the character, or empty if not found
     */
    Optional<Character> findById(@NotNull Integer id);

    /**
     * Save a character.
     *
     * @param character the character to save
     * @return the saved character
     */
    Character save(@NotNull Character character);

    /**
     * Delete a character by its ID.
     *
     * @param id the character ID
     */
    void deleteById(@NotNull Integer id);

    /**
     * Find characters by name (partial match).
     *
     * @param name the name to search for
     * @return a list of characters with matching names
     */
    List<Character> findByNameContaining(String name);

    /**
     * Find characters by Vallterra user ID.
     *
     * @param vallterraUserId the Vallterra user ID
     * @return a list of characters for the given user
     */
    List<Character> findByVallterraUserId(@NotNull Long vallterraUserId);
}
