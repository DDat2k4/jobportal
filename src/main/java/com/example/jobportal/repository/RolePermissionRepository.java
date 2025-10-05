package com.example.jobportal.repository;

import com.example.generated.jooq.tables.records.RolePermissionsRecord;
import com.example.generated.jooq.tables.records.PermissionsRecord;
import com.example.jobportal.data.entity.Permission;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.example.generated.jooq.tables.RolePermissions.ROLE_PERMISSIONS;
import static com.example.generated.jooq.tables.Permissions.PERMISSIONS;

@Repository
public class RolePermissionRepository {

    private final DSLContext dsl;

    public RolePermissionRepository(DSLContext dsl) {
        this.dsl = dsl;
    }

    // Lấy tất cả permission của role
    public List<Permission> findPermissionsByRoleId(Long roleId) {
        return dsl.select(
                        PERMISSIONS.ID,
                        PERMISSIONS.CODE,
                        PERMISSIONS.DESCRIPTION
                )
                .from(ROLE_PERMISSIONS)
                .join(PERMISSIONS)
                .on(ROLE_PERMISSIONS.PERMISSION_ID.eq(PERMISSIONS.ID))
                .where(ROLE_PERMISSIONS.ROLE_ID.eq(roleId))
                .fetch(record -> new Permission(
                        record.get(PERMISSIONS.ID),
                        record.get(PERMISSIONS.CODE),
                        record.get(PERMISSIONS.DESCRIPTION)
                ));
    }
    // Xóa tất cả permission của role
    public int deleteByRoleId(Long roleId) {
        return dsl.deleteFrom(ROLE_PERMISSIONS)
                .where(ROLE_PERMISSIONS.ROLE_ID.eq(roleId))
                .execute();
    }

    // Thêm permission cho role
    public int addPermissionToRole(Long roleId, Long permissionId) {
        return dsl.insertInto(ROLE_PERMISSIONS)
                .set(ROLE_PERMISSIONS.ROLE_ID, roleId)
                .set(ROLE_PERMISSIONS.PERMISSION_ID, permissionId)
                .execute();
    }

    // Lấy tất cả RolePermission của role
    public List<RolePermissionsRecord> findByRoleId(Long roleId) {
        return dsl.selectFrom(ROLE_PERMISSIONS)
                .where(ROLE_PERMISSIONS.ROLE_ID.eq(roleId))
                .fetchInto(RolePermissionsRecord.class);
    }

    // Xóa 1 permission cụ thể khỏi role
    public int deletePermissionFromRole(Long roleId, Long permissionId) {
        return dsl.deleteFrom(ROLE_PERMISSIONS)
                .where(ROLE_PERMISSIONS.ROLE_ID.eq(roleId))
                .and(ROLE_PERMISSIONS.PERMISSION_ID.eq(permissionId))
                .execute();
    }
}
