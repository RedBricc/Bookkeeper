package vallterra.bookkeeper.backend.role.impl;

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;
import vallterra.bookkeeper.backend.role.BookkeeperUserRoleRepository;

import java.util.List;

import static org.jooq.generated.public_.tables.BookkeeperUserRole.BOOKKEEPER_USER_ROLE;

@Repository
@RequiredArgsConstructor
public class BookkeeperUserRoleRepositoryImpl implements BookkeeperUserRoleRepository {

    private final DSLContext db;

    @Override
    public void addRole(Integer bookkeeperUserId, String role) {
        db.insertInto(BOOKKEEPER_USER_ROLE)
                .set(BOOKKEEPER_USER_ROLE.BOOKKEEPER_USER_ID, bookkeeperUserId)
                .set(BOOKKEEPER_USER_ROLE.ROLE, role)
                .execute();
    }

    @Override
    public void removeRole(Integer bookkeeperUserId, String role) {
        db.deleteFrom(BOOKKEEPER_USER_ROLE)
                .where(BOOKKEEPER_USER_ROLE.BOOKKEEPER_USER_ID.eq(bookkeeperUserId))
                .and(BOOKKEEPER_USER_ROLE.ROLE.eq(role))
                .execute();
    }

    @Override
    public boolean hasRole(Integer bookkeeperUserId, String role) {
        return db.fetchExists(
                db.selectFrom(BOOKKEEPER_USER_ROLE)
                        .where(BOOKKEEPER_USER_ROLE.BOOKKEEPER_USER_ID.eq(bookkeeperUserId))
                        .and(BOOKKEEPER_USER_ROLE.ROLE.eq(role))
        );
    }

    @Override
    public List<String> getRoles(Integer bookkeeperUserId) {
        return db.select(BOOKKEEPER_USER_ROLE.ROLE)
                .from(BOOKKEEPER_USER_ROLE)
                .where(BOOKKEEPER_USER_ROLE.BOOKKEEPER_USER_ID.eq(bookkeeperUserId))
                .fetch(BOOKKEEPER_USER_ROLE.ROLE);
    }

}
