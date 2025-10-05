package com.example.jobportal.repository;

import com.example.generated.jooq.tables.records.PermissionsRecord;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static com.example.generated.jooq.tables.Permissions.PERMISSIONS;

@Repository
public class PermissionRepository {

    private final DSLContext dsl;

    public PermissionRepository(DSLContext dsl) {
        this.dsl = dsl;
    }

    public Optional<PermissionsRecord> findByCode(String code) {
        return dsl.selectFrom(PERMISSIONS)
                .where(PERMISSIONS.CODE.eq(code))
                .fetchOptional();
    }
}



