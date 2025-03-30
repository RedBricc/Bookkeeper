package vallterra.bookkeeper.backend.role;

import jakarta.validation.constraints.NotNull;
import org.jooq.generated.public_.tables.pojos.BookkeeperUser;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for managing {@link BookkeeperUser} roles.
 */
@Repository
public interface BookkeeperUserRoleRepository {
    void addRole(Integer bookkeeperUserId, String role);
    void removeRole(Integer bookkeeperUserId, String role);
    boolean hasRole(Integer bookkeeperUserId, String role);
    List<String> getRoles(@NotNull Integer bookkeeperUserId);

}
