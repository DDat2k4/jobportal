package com.example.jobportal.repository;

import com.example.generated.jooq.tables.records.UserProfilesRecord;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static com.example.generated.jooq.tables.UserProfiles.USER_PROFILES;
import static org.jooq.impl.DSL.val;

@Repository
public class UserProfileRepository {

    private final DSLContext dsl;

    public UserProfileRepository(DSLContext dsl) {
        this.dsl = dsl;
    }

    public Optional<UserProfilesRecord> findByUserId(Long userId) {
        return dsl.selectFrom(USER_PROFILES)
                .where(USER_PROFILES.USER_ID.eq(userId))
                .fetchOptional();
    }

    public void insert(Long userId, String name, String avatar) {
        dsl.insertInto(USER_PROFILES)
                .set(USER_PROFILES.USER_ID, val(userId))
                .set(USER_PROFILES.NAME, val(name))
                .set(USER_PROFILES.AVATAR, val(avatar))
                .execute();
    }

    public void update(Long userId, String name, String avatar) {
        dsl.update(USER_PROFILES)
                .set(USER_PROFILES.NAME, val(name))
                .set(USER_PROFILES.AVATAR, val(avatar))
                .where(USER_PROFILES.USER_ID.eq(userId))
                .execute();
    }
}
