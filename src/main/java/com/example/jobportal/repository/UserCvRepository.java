package com.example.jobportal.repository;

import com.example.jobportal.data.entity.UserCv;
import com.example.jobportal.extension.paging.Order;
import com.example.jobportal.extension.paging.Page;
import com.example.jobportal.extension.paging.Pageable;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.JSONB;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.*;

import static com.example.generated.jooq.tables.UserCvs.USER_CVS;
import static org.jooq.impl.DSL.noCondition;

@Repository
@RequiredArgsConstructor
public class UserCvRepository {

    private final DSLContext dsl;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static List<Field<?>> getFields() {
        return Arrays.asList(
                USER_CVS.ID,
                USER_CVS.USER_ID,
                USER_CVS.TITLE,
                USER_CVS.TEMPLATE_CODE,
                USER_CVS.SUMMARY,
                USER_CVS.DATA,
                USER_CVS.IS_DEFAULT,
                USER_CVS.CREATED_AT,
                USER_CVS.UPDATED_AT
        );
    }

    private Condition getWhereCondition(UserCv filter) {
        Condition condition = noCondition();

        if (filter == null) return condition;

        if (filter.getId() != null)
            condition = condition.and(USER_CVS.ID.eq(filter.getId()));

        if (filter.getUserId() != null)
            condition = condition.and(USER_CVS.USER_ID.eq(filter.getUserId()));

        if (filter.getTitle() != null && !filter.getTitle().isEmpty())
            condition = condition.and(USER_CVS.TITLE.likeIgnoreCase("%" + filter.getTitle() + "%"));

        if (filter.getTemplateCode() != null)
            condition = condition.and(USER_CVS.TEMPLATE_CODE.eq(filter.getTemplateCode()));

        if (filter.getIsDefault() != null)
            condition = condition.and(USER_CVS.IS_DEFAULT.eq(filter.getIsDefault()));

        return condition;
    }

    private JSONB toJsonb(Object data) {
        if (data == null) return null;
        try {
            return JSONB.valueOf(objectMapper.writeValueAsString(data));
        } catch (Exception e) {
            throw new RuntimeException("Failed to convert data to JSONB", e);
        }
    }

    private Map<String, Object> fromJsonb(JSONB jsonb) {
        if (jsonb == null) return null;
        try {
            return objectMapper.readValue(jsonb.data(), new TypeReference<Map<String, Object>>() {});
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse JSONB data", e);
        }
    }

    private UserCv mapRecordToEntity(com.example.generated.jooq.tables.records.UserCvsRecord record) {
        if (record == null) return null;
        return new UserCv()
                .setId(record.getId())
                .setUserId(record.getUserId())
                .setTitle(record.getTitle())
                .setTemplateCode(record.getTemplateCode())
                .setSummary(record.getSummary())
                .setData(fromJsonb(record.getData()))
                .setIsDefault(record.getIsDefault())
                .setCreatedAt(record.getCreatedAt())
                .setUpdatedAt(record.getUpdatedAt());
    }

    public Optional<UserCv> findById(Long id) {
        return dsl.selectFrom(USER_CVS)
                .where(USER_CVS.ID.eq(id))
                .fetchOptional(this::mapRecordToEntity);
    }

    public Optional<UserCv> findByFilter(UserCv filter) {
        return dsl.selectFrom(USER_CVS)
                .where(getWhereCondition(filter))
                .fetchOptional(this::mapRecordToEntity);
    }

    public UserCv create(UserCv cv) {
        var record = dsl.insertInto(USER_CVS)
                .set(USER_CVS.USER_ID, cv.getUserId())
                .set(USER_CVS.TITLE, cv.getTitle())
                .set(USER_CVS.TEMPLATE_CODE, cv.getTemplateCode())
                .set(USER_CVS.SUMMARY, cv.getSummary())
                .set(USER_CVS.DATA, toJsonb(cv.getData()))
                .set(USER_CVS.IS_DEFAULT, cv.getIsDefault())
                .set(USER_CVS.CREATED_AT, LocalDateTime.now())
                .set(USER_CVS.UPDATED_AT, LocalDateTime.now())
                .returning()
                .fetchOne();

        return mapRecordToEntity(record);
    }

    public Optional<UserCv> update(Long id, UserCv cv) {
        var record = dsl.update(USER_CVS)
                .set(USER_CVS.TITLE, cv.getTitle())
                .set(USER_CVS.TEMPLATE_CODE, cv.getTemplateCode())
                .set(USER_CVS.SUMMARY, cv.getSummary())
                .set(USER_CVS.DATA, toJsonb(cv.getData()))
                .set(USER_CVS.IS_DEFAULT, cv.getIsDefault())
                .set(USER_CVS.UPDATED_AT, LocalDateTime.now())
                .where(USER_CVS.ID.eq(id))
                .returning()
                .fetchOne();

        return Optional.ofNullable(mapRecordToEntity(record));
    }

    public int delete(Long id) {
        return dsl.deleteFrom(USER_CVS)
                .where(USER_CVS.ID.eq(id))
                .execute();
    }

    private Field<?> resolveSortField(String property) {
        if (property == null || property.isEmpty()) {
            return USER_CVS.ID;
        }

        String dbField = property
                .replaceAll("([a-z])([A-Z])", "$1_$2")
                .toLowerCase();

        Field<?> field = USER_CVS.field(dbField);
        return field != null ? field : USER_CVS.ID;
    }

    public Page<UserCv> findAll(UserCv filter, Pageable pageable) {
        int offset = pageable.getOffset();
        int limit = pageable.getLimit();
        long total = count(filter);
        pageable.setTotal(total);

        Order order = pageable.getFirstOrder();
        String sortField = order.getPropertyOrDefault("id");
        boolean isAsc = order.isAsc();

        Field<?> sort = resolveSortField(sortField);

        List<UserCv> items = dsl.selectFrom(USER_CVS)
                .where(getWhereCondition(filter))
                .orderBy(isAsc ? sort.asc() : sort.desc())
                .limit(limit)
                .offset(offset)
                .fetch(this::mapRecordToEntity);

        return new Page<>(pageable, items);
    }

    public long count(UserCv filter) {
        return dsl.selectCount()
                .from(USER_CVS)
                .where(getWhereCondition(filter))
                .fetchOne(0, long.class);
    }

    public Optional<UserCv> findDefaultByUserId(Long userId) {
        return dsl.selectFrom(USER_CVS)
                .where(USER_CVS.USER_ID.eq(userId))
                .and(USER_CVS.IS_DEFAULT.isTrue())
                .fetchOptional(this::mapRecordToEntity);
    }

    public void unsetDefaultByUserId(Long userId) {
        dsl.update(USER_CVS)
                .set(USER_CVS.IS_DEFAULT, false)
                .where(USER_CVS.USER_ID.eq(userId))
                .and(USER_CVS.IS_DEFAULT.isTrue())
                .execute();
    }

    public Optional<UserCv> updateIsDefault(Long id, boolean isDefault) {
        var record = dsl.update(USER_CVS)
                .set(USER_CVS.IS_DEFAULT, isDefault)
                .set(USER_CVS.UPDATED_AT, LocalDateTime.now())
                .where(USER_CVS.ID.eq(id))
                .returning()
                .fetchOne();

        return Optional.ofNullable(mapRecordToEntity(record));
    }

    /**
     * Lấy CV mặc định (is_default = true) của user dưới dạng JSON string.
     * Nếu không có CV mặc định -> Optional.empty()
     */
    public Optional<String> findDefaultCvJsonByUserId(Long userId) {
        return dsl.select(USER_CVS.DATA)
                .from(USER_CVS)
                .where(USER_CVS.USER_ID.eq(userId).and(USER_CVS.IS_DEFAULT.eq(true)))
                .fetchOptional(r -> {
                    Object v = r.get(USER_CVS.DATA);
                    return v == null ? null : v.toString();
                });
    }

    public Optional<String> findCvJsonById(Long cvId) {
        return dsl.select(USER_CVS.DATA)
                .from(USER_CVS)
                .where(USER_CVS.ID.eq(cvId))
                .fetchOptional(r -> {
                    Object v = r.get(USER_CVS.DATA);
                    return v == null ? null : v.toString();
                });
    }

    /** Lấy tất cả CV mặc định (is_default = true) */
    public List<UserCv> findAllDefault() {
        return dsl.selectFrom(USER_CVS)
                .where(USER_CVS.IS_DEFAULT.isTrue())
                .fetch(this::mapRecordToEntity);
    }
}
