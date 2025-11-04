package com.example.jobportal.repository;

import com.example.jobportal.data.entity.Role;
import com.example.jobportal.extension.paging.Order;
import com.example.jobportal.extension.paging.Page;
import com.example.jobportal.extension.paging.Pageable;
import lombok.RequiredArgsConstructor;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Record1;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;

import static com.example.generated.jooq.tables.UserRoles.USER_ROLES;
import static com.example.generated.jooq.tables.Roles.ROLES;
import static com.example.generated.jooq.tables.RolePermissions.ROLE_PERMISSIONS;
import static com.example.generated.jooq.tables.Permissions.PERMISSIONS;
import static org.jooq.impl.DSL.noCondition;

@Repository
@RequiredArgsConstructor
public class UserRoleRepository {

    private final DSLContext dsl;

    private static List<Field<?>> getRoleFields() {
        return Arrays.asList(
                ROLES.ID,
                ROLES.NAME,
                ROLES.DESCRIPTION
        );
    }

    private Condition getWhereCondition(Long userId, Long roleId) {
        Condition condition = noCondition();
        if (userId != null) condition = condition.and(USER_ROLES.USER_ID.eq(userId));
        if (roleId != null) condition = condition.and(USER_ROLES.ROLE_ID.eq(roleId));
        return condition;
    }

    private Role mapRecordToRole(org.jooq.Record record) {
        if (record == null) return null;
        return new Role(
                record.get(ROLES.ID),
                record.get(ROLES.NAME),
                record.get(ROLES.DESCRIPTION)
        );
    }

    private Field<?> resolveSortField(String property) {
        if (property == null || property.isEmpty()) {
            return ROLES.ID;
        }
        String dbField = property
                .replaceAll("([a-z])([A-Z])", "$1_$2")
                .toLowerCase();
        Field<?> field = ROLES.field(dbField);
        return field != null ? field : ROLES.ID;
    }

    public List<Role> findRolesByUserId(Long userId) {
        return dsl.select(getRoleFields())
                .from(USER_ROLES)
                .join(ROLES).on(USER_ROLES.ROLE_ID.eq(ROLES.ID))
                .where(USER_ROLES.USER_ID.eq(userId))
                .fetch(this::mapRecordToRole);
    }

    public Set<String> findPermissionsByUserId(Long userId) {
        List<Record1<String>> records = dsl.select(PERMISSIONS.CODE)
                .from(USER_ROLES)
                .join(ROLE_PERMISSIONS).on(USER_ROLES.ROLE_ID.eq(ROLE_PERMISSIONS.ROLE_ID))
                .join(PERMISSIONS).on(ROLE_PERMISSIONS.PERMISSION_ID.eq(PERMISSIONS.ID))
                .where(USER_ROLES.USER_ID.eq(userId))
                .fetch();

        return records.stream()
                .map(Record1::value1)
                .collect(Collectors.toSet());
    }

    public Page<Role> findAllRoles(Pageable pageable) {
        int offset = pageable.getOffset();
        int limit = pageable.getLimit();
        long total = dsl.selectCount().from(ROLES).fetchOne(0, long.class);
        pageable.setTotal(total);

        Order order = pageable.getFirstOrder();
        String sortField = order.getPropertyOrDefault("id");
        boolean isAsc = order.isAsc();

        Field<?> sort = resolveSortField(sortField);

        List<Role> items = dsl.selectFrom(ROLES)
                .orderBy(isAsc ? sort.asc() : sort.desc())
                .limit(limit)
                .offset(offset)
                .fetch(this::mapRecordToRole);

        return new Page<>(pageable, items);
    }

    public int addRoleToUser(Long userId, Long roleId) {
        return dsl.insertInto(USER_ROLES)
                .set(USER_ROLES.USER_ID, userId)
                .set(USER_ROLES.ROLE_ID, roleId)
                .execute();
    }

    public int deleteRoleFromUser(Long userId, Long roleId) {
        return dsl.deleteFrom(USER_ROLES)
                .where(getWhereCondition(userId, roleId))
                .execute();
    }

    public int deleteAllRolesByUserId(Long userId) {
        return dsl.deleteFrom(USER_ROLES)
                .where(USER_ROLES.USER_ID.eq(userId))
                .execute();
    }

    public boolean existsUserRole(Long userId, Long roleId) {
        return dsl.fetchExists(
                dsl.selectOne()
                        .from(USER_ROLES)
                        .where(getWhereCondition(userId, roleId))
        );
    }

    public Optional<Role> findRoleById(Long roleId) {
        return dsl.selectFrom(ROLES)
                .where(ROLES.ID.eq(roleId))
                .fetchOptional(this::mapRecordToRole);
    }

    public Role createRole(Role role) {
        var record = dsl.insertInto(ROLES)
                .set(ROLES.NAME, role.getName())
                .set(ROLES.DESCRIPTION, role.getDescription())
                .returning()
                .fetchOne();

        return mapRecordToRole(record);
    }

    public Optional<Role> updateRole(Long id, Role role) {
        var record = dsl.update(ROLES)
                .set(ROLES.NAME, role.getName())
                .set(ROLES.DESCRIPTION, role.getDescription())
                .where(ROLES.ID.eq(id))
                .returning()
                .fetchOne();

        return Optional.ofNullable(mapRecordToRole(record));
    }

    public int deleteRole(Long id) {
        return dsl.deleteFrom(ROLES)
                .where(ROLES.ID.eq(id))
                .execute();
    }

    public long countRoles() {
        return dsl.selectCount()
                .from(ROLES)
                .fetchOne(0, long.class);
    }
}