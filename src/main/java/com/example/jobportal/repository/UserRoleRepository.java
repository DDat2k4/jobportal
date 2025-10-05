package com.example.jobportal.repository;

import com.example.jobportal.data.entity.Role;
import org.jooq.DSLContext;
import org.jooq.Record1;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.example.generated.jooq.tables.UserRoles.USER_ROLES;
import static com.example.generated.jooq.tables.Roles.ROLES;
import static com.example.generated.jooq.tables.RolePermissions.ROLE_PERMISSIONS;
import static com.example.generated.jooq.tables.Permissions.PERMISSIONS;

@Repository
public class UserRoleRepository {

    private final DSLContext dsl;

    public UserRoleRepository(DSLContext dsl) {
        this.dsl = dsl;
    }

    // Lấy tất cả tên role của user
    public List<Role> findRolesByUserId(Long userId) {
        return dsl.select(ROLES.ID, ROLES.NAME, ROLES.DESCRIPTION)
                .from(USER_ROLES)
                .join(ROLES).on(USER_ROLES.ROLE_ID.eq(ROLES.ID))
                .where(USER_ROLES.USER_ID.eq(userId))
                .fetch(record -> new Role(
                        record.get(ROLES.ID),
                        record.get(ROLES.NAME),
                        record.get(ROLES.DESCRIPTION)
                ));
    }

    // Lấy tất cả permission của user
    public Set<String> findPermissionsByUserId(Long userId) {
        List<Record1<String>> records = dsl.select(PERMISSIONS.CODE)
                .from(USER_ROLES)
                .join(ROLE_PERMISSIONS).on(USER_ROLES.ROLE_ID.eq(ROLE_PERMISSIONS.ROLE_ID))
                .join(PERMISSIONS).on(ROLE_PERMISSIONS.PERMISSION_ID.eq(PERMISSIONS.ID))
                .where(USER_ROLES.USER_ID.eq(userId))
                .fetch();
        return records.stream().map(r -> r.value1()).collect(Collectors.toSet());
    }

    // Xóa tất cả role của user
    public int deleteByUserId(Long userId) {
        return dsl.deleteFrom(USER_ROLES)
                .where(USER_ROLES.USER_ID.eq(userId))
                .execute();
    }

    // Thêm role cho user
    public int addRoleToUser(Long userId, Long roleId) {
        return dsl.insertInto(USER_ROLES)
                .set(USER_ROLES.USER_ID, userId)
                .set(USER_ROLES.ROLE_ID, roleId)
                .execute();
    }


    // Xóa 1 role của user (method bạn cần)
    public int deleteRoleFromUser(Long userId, Long roleId) {
        return dsl.deleteFrom(USER_ROLES)
                .where(USER_ROLES.USER_ID.eq(userId))
                .and(USER_ROLES.ROLE_ID.eq(roleId))
                .execute();
    }
}
