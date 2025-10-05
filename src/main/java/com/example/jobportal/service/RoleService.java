package com.example.jobportal.service;

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.example.generated.jooq.tables.RolePermissions.ROLE_PERMISSIONS;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final DSLContext dsl;

    /**
     * Gán permission cho role
     */
    public void assignPermissionsToRole(Long roleId, List<Long> permissionIds) {
        // Xóa hết permission cũ
        dsl.deleteFrom(ROLE_PERMISSIONS)
                .where(ROLE_PERMISSIONS.ROLE_ID.eq(roleId))
                .execute();

        // Thêm permission mới
        for (Long pid : permissionIds) {
            dsl.insertInto(ROLE_PERMISSIONS)
                    .set(ROLE_PERMISSIONS.ROLE_ID, roleId)
                    .set(ROLE_PERMISSIONS.PERMISSION_ID, pid)
                    .execute();
        }
    }

    /**
     * Lấy permissionIds của role
     */
    public List<Long> getPermissionsOfRole(Long roleId) {
        return dsl.select(ROLE_PERMISSIONS.PERMISSION_ID)
                .from(ROLE_PERMISSIONS)
                .where(ROLE_PERMISSIONS.ROLE_ID.eq(roleId))
                .fetchInto(Long.class);
    }
}
