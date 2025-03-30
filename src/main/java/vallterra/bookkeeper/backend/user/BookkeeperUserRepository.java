package vallterra.bookkeeper.backend.user;

import jakarta.validation.constraints.NotNull;
import org.jooq.generated.public_.tables.pojos.BookkeeperUser;
import org.springframework.validation.annotation.Validated;

import java.util.Optional;

/**
 * Repository for {@link BookkeeperUser} records.
 * <p>
 *  A Bookkeeper user represents an admin user that has access to the bookkeeper admin panel.
 * </p>
 */
@Validated
public interface BookkeeperUserRepository {

    BookkeeperUser getById(@NotNull Integer bookkeeperUserId);
    BookkeeperUser save(@NotNull BookkeeperUser user);
    Optional<BookkeeperUser> findById(@NotNull Integer bookkeeperUserId);
    Optional<BookkeeperUser> findByUsername(@NotNull String username);

}
