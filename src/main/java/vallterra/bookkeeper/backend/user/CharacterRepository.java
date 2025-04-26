package vallterra.bookkeeper.backend.user;

import org.jooq.generated.tables.pojos.Character;
import org.springframework.validation.annotation.Validated;

/**
 * Repository for {@link Character} records.
 * <p>
 *  A character represents a user's in-game character. This repository is used to access
 *  character-specific data.
 * </p>
 */
@Validated
public interface CharacterRepository {
}
