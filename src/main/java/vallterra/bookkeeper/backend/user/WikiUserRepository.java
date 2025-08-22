package vallterra.bookkeeper.backend.user;

import jakarta.validation.constraints.NotNull;
import org.jooq.generated.tables.pojos.WikiUser;

public interface WikiUserRepository {

    WikiUser getById(@NotNull Integer wikiUserId);

    WikiUser save(@NotNull WikiUser user);

}
