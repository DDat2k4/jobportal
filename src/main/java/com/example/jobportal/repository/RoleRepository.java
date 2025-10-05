package com.example.jobportal.repository;

import com.example.generated.jooq.tables.records.RolesRecord;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static com.example.generated.jooq.tables.Roles.ROLES;

@Repository
public class RoleRepository {

    private final DSLContext dsl;

    public RoleRepository(DSLContext dsl) {
        this.dsl = dsl;
    }

    public Optional<RolesRecord> findByName(String name) {
        return dsl.selectFrom(ROLES)
                .where(ROLES.NAME.eq(name))
                .fetchOptional();
    }
}

