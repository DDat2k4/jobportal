package com.example.jobportal.repository;

import com.example.generated.jooq.tables.records.UserProfilesRecord;
import com.example.jobportal.data.entity.UserProfile;
import com.example.jobportal.extension.paging.Order;
import com.example.jobportal.extension.paging.Page;
import com.example.jobportal.extension.paging.Pageable;
import lombok.RequiredArgsConstructor;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.example.generated.jooq.tables.UserProfiles.USER_PROFILES;
import static org.jooq.impl.DSL.val;

@RequiredArgsConstructor
@Repository
public class UserProfileRepository {

    private final DSLContext dsl;

    private static List<Field<?>> getFields() {
        return Arrays.asList(
                USER_PROFILES.ID,
                USER_PROFILES.USER_ID,
                USER_PROFILES.NAME,
                USER_PROFILES.AVATAR,
                USER_PROFILES.GENDER,
                USER_PROFILES.BIRTH_DATE,
                USER_PROFILES.HEADLINE,
                USER_PROFILES.NOTE
        );
    }

    private Condition getWhereCondition(UserProfile filter) {
        Condition condition = USER_PROFILES.ID.isNotNull();
        if (filter == null) return condition;

        if (filter.getId() != null)
            condition = condition.and(USER_PROFILES.ID.eq(filter.getId()));

        if (filter.getUserId() != null)
            condition = condition.and(USER_PROFILES.USER_ID.eq(filter.getUserId()));

        if (filter.getName() != null && !filter.getName().isEmpty())
            condition = condition.and(USER_PROFILES.NAME.containsIgnoreCase(filter.getName()));

        if (filter.getGender() != null)
            condition = condition.and(USER_PROFILES.GENDER.eq(filter.getGender()));

        if (filter.getHeadline() != null && !filter.getHeadline().isEmpty())
            condition = condition.and(USER_PROFILES.HEADLINE.containsIgnoreCase(filter.getHeadline()));

        if (filter.getNote() != null && !filter.getNote().isEmpty())
            condition = condition.and(USER_PROFILES.NOTE.containsIgnoreCase(filter.getNote()));

        return condition;
    }

    public Optional<UserProfile> findById(Long id) {
        return dsl.select(getFields())
                .from(USER_PROFILES)
                .where(USER_PROFILES.ID.eq(id))
                .fetchOptionalInto(UserProfile.class);
    }

    public UserProfile create(UserProfile profile) {
        return dsl.insertInto(USER_PROFILES)
                .set(USER_PROFILES.USER_ID, profile.getUserId())
                .set(USER_PROFILES.NAME, profile.getName())
                .set(USER_PROFILES.AVATAR, profile.getAvatar())
                .set(USER_PROFILES.GENDER, profile.getGender())
                .set(USER_PROFILES.BIRTH_DATE, profile.getBirthDate())
                .set(USER_PROFILES.HEADLINE, profile.getHeadline())
                .set(USER_PROFILES.NOTE, profile.getNote())
                .returning()
                .fetchOneInto(UserProfile.class);
    }

    public Optional<UserProfile> update(UserProfile profile) {
        return dsl.update(USER_PROFILES)
                .set(USER_PROFILES.NAME, profile.getName())
                .set(USER_PROFILES.AVATAR, profile.getAvatar())
                .set(USER_PROFILES.GENDER, profile.getGender())
                .set(USER_PROFILES.BIRTH_DATE, profile.getBirthDate())
                .set(USER_PROFILES.HEADLINE, profile.getHeadline())
                .set(USER_PROFILES.NOTE, profile.getNote())
                .where(USER_PROFILES.USER_ID.eq(profile.getUserId()))
                .returning()
                .fetchOptionalInto(UserProfile.class);
    }

    public int deleteByUserId(Long userId) {
        return dsl.deleteFrom(USER_PROFILES)
                .where(USER_PROFILES.USER_ID.eq(userId))
                .execute();
    }

    /* ---- Pagination ---- */

    public Page<UserProfile> findAll(UserProfile filter, Pageable pageable) {
        pageable = pageable != null ? pageable : new Pageable();
        int offset = pageable.getOffset();
        int limit = pageable.getLimit();
        Order order = pageable.getFirstOrder();
        String sortField = order.getPropertyOrDefault("id");
        boolean isAsc = order.isAsc();

        Field<?> sort = USER_PROFILES.field(sortField);
        if (sort == null) sort = USER_PROFILES.ID;

        long total = count(filter);
        pageable.setTotal(total);

        List<UserProfile> items = dsl.select(getFields())
                .from(USER_PROFILES)
                .where(getWhereCondition(filter))
                .orderBy(isAsc ? sort.asc() : sort.desc())
                .limit(limit)
                .offset(offset)
                .fetchInto(UserProfile.class);

        return new Page<>(pageable, items);
    }

    public long count(UserProfile filter) {
        return dsl.fetchCount(
                dsl.selectOne()
                        .from(USER_PROFILES)
                        .where(getWhereCondition(filter))
        );
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