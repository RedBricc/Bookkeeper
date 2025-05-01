package vallterra.bookkeeper.backend.adventure;

import jakarta.validation.constraints.NotNull;
import org.jooq.generated.tables.pojos.Adventure;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Optional;

/**
 * Repository for managing {@link Adventure} records.
 */
@Validated
public interface AdventureRepository {

    /**
     * Find all adventures.
     *
     * @return a list of all adventures
     */
    List<Adventure> findAll();

    /**
     * Find an adventure by its ID.
     *
     * @param id the adventure ID
     * @return the adventure, or empty if not found
     */
    Optional<Adventure> findById(@NotNull Integer id);

    /**
     * Save an adventure.
     *
     * @param adventure the adventure to save
     * @return the saved adventure
     */
    Adventure save(@NotNull Adventure adventure);

    /**
     * Delete an adventure by its ID.
     *
     * @param id the adventure ID
     */
    void deleteById(@NotNull Integer id);

    /**
     * Find adventures by quest ID.
     *
     * @param questId the quest ID
     * @return a list of adventures for the given quest
     */
    List<Adventure> findByQuestId(@NotNull Long questId);

    /**
     * Find adventures by slug (partial match).
     *
     * @param slug the slug to search for
     * @return a list of adventures with matching slugs
     */
    List<Adventure> findBySlugContaining(String slug);
}
