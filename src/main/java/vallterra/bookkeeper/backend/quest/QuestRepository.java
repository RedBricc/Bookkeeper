package vallterra.bookkeeper.backend.quest;

import jakarta.validation.constraints.NotNull;
import org.jooq.generated.tables.pojos.Quest;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Optional;

/**
 * Repository for managing {@link Quest} records.
 */
@Validated
public interface QuestRepository {

    /**
     * Find all quests.
     *
     * @return a list of all quests
     */
    List<Quest> findAll();

    /**
     * Find a quest by its ID.
     *
     * @param id the quest ID
     * @return the quest, or empty if not found
     */
    Optional<Quest> findById(@NotNull Integer id);

    /**
     * Save a quest.
     *
     * @param quest the quest to save
     * @return the saved quest
     */
    Quest save(@NotNull Quest quest);

    /**
     * Delete a quest by its ID.
     *
     * @param id the quest ID
     */
    void deleteById(@NotNull Integer id);

    /**
     * Find quests by name (partial match).
     *
     * @param name the name to search for
     * @return a list of quests with matching names
     */
    List<Quest> findByNameContaining(String name);

    /**
     * Find quests by location (partial match).
     *
     * @param location the location to search for
     * @return a list of quests with matching locations
     */
    List<Quest> findByLocationContaining(String location);
}
