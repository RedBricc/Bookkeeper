package vallterra.bookkeeper.backend.user;

import jakarta.validation.constraints.NotNull;
import org.jooq.generated.public_.tables.pojos.VallterraUser;
import org.springframework.validation.annotation.Validated;

import java.util.Optional;

/**
 * Repository for {@link VallterraUser} records.
 * <p>
 *  A Vallterra user represents a unique identity that is shared across both the website
 *  and the admin panel. In the website, user-specific data is accessed through the {@link CharacterRepository},
 *  while user data for the bookkeeper admin panel is accessed through the {@link VallterraUserRepository}.
 * </p>
 */
@Validated
public interface VallterraUserRepository {

    VallterraUser getById(@NotNull Integer vallterraUserId);
    VallterraUser save(@NotNull VallterraUser user);
    Optional<VallterraUser> findById(@NotNull Integer vallterraUserId);

}
