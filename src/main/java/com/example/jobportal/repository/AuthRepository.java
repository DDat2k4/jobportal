package com.example.jobportal.repository;

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.*;

import static com.example.generated.jooq.tables.Users.USERS;
import static com.example.generated.jooq.tables.UserRoles.USER_ROLES;
import static com.example.generated.jooq.tables.Roles.ROLES;
import static com.example.generated.jooq.tables.RolePermissions.ROLE_PERMISSIONS;
import static com.example.generated.jooq.tables.Permissions.PERMISSIONS;

@Repository
@RequiredArgsConstructor
public class AuthRepository {

    private final DSLContext dsl;

    public Optional<UserAuthRecord> findUserWithRolesAndPermissions(String username) {
        var result = dsl.select(
                        USERS.ID, USERS.USERNAME, USERS.PASSWORD_HASH,
                        ROLES.NAME.as("role_name"),
                        PERMISSIONS.CODE.as("perm_code")
                )
                .from(USERS)
                .leftJoin(USER_ROLES).on(USER_ROLES.USER_ID.eq(USERS.ID))
                .leftJoin(ROLES).on(ROLES.ID.eq(USER_ROLES.ROLE_ID))
                .leftJoin(ROLE_PERMISSIONS).on(ROLE_PERMISSIONS.ROLE_ID.eq(ROLES.ID))
                .leftJoin(PERMISSIONS).on(PERMISSIONS.ID.eq(ROLE_PERMISSIONS.PERMISSION_ID))
                .where(USERS.USERNAME.eq(username))
                .fetch();

        if (result.isEmpty()) return Optional.empty();

        var first = result.get(0);
        var roles = new HashSet<String>();
        var perms = new HashSet<String>();

        result.forEach(r -> {
            if (r.get("role_name") != null) roles.add(r.get("role_name", String.class));
            if (r.get("perm_code") != null) perms.add(r.get("perm_code", String.class));
        });

        return Optional.of(new UserAuthRecord(
                first.get(USERS.ID),
                first.get(USERS.USERNAME),
                first.get(USERS.PASSWORD_HASH),
                roles,
                perms
        ));
    }

    public record UserAuthRecord(Long id, String username, String password, Set<String> roles, Set<String> perms) {}
}
