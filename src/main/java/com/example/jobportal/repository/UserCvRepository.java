package com.example.jobportal.repository;

import com.example.jobportal.data.entity.UserCv;
import com.example.jobportal.extension.paging.Order;
import com.example.jobportal.extension.paging.Page;
import com.example.jobportal.extension.paging.Pageable;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.example.generated.jooq.tables.UserCvs.USER_CVS;
import static org.jooq.impl.DSL.noCondition;

@Repository
public class UserCvRepository {

    private final DSLContext dsl;

    public UserCvRepository(DSLContext dsl) {
        this.dsl = dsl;
    }

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

    public Condition getWhereCondition(UserCv filter) {
        Condition condition = noCondition();

        if (filter.getId() != null) {
            condition = condition.and(USER_CVS.ID.eq(filter.getId()));
        }
        if (filter.getUserId() != null) {
            condition = condition.and(USER_CVS.USER_ID.eq(filter.getUserId()));
        }
        if (filter.getTitle() != null) {
            condition = condition.and(USER_CVS.TITLE.likeIgnoreCase("%" + filter.getTitle() + "%"));
        }
        if (filter.getTemplateCode() != null) {
            condition = condition.and(USER_CVS.TEMPLATE_CODE.eq(filter.getTemplateCode()));
        }
        if (filter.getIsDefault() != null) {
            condition = condition.and(USER_CVS.IS_DEFAULT.eq(filter.getIsDefault()));
        }

        return condition;
    }

    public Optional<UserCv> findById(Long id) {
        return dsl.select(getFields())
                .from(USER_CVS)
                .where(USER_CVS.ID.eq(id))
                .fetchOptionalInto(UserCv.class);
    }

    public Optional<UserCv> findByFilter(UserCv filter) {
        return dsl.select(getFields())
                .from(USER_CVS)
                .where(getWhereCondition(filter))
                .fetchOptionalInto(UserCv.class);
    }

    public UserCv create(UserCv cv) {
        return dsl.insertInto(USER_CVS)
                .set(USER_CVS.USER_ID, cv.getUserId())
                .set(USER_CVS.TITLE, cv.getTitle())
                .set(USER_CVS.TEMPLATE_CODE, cv.getTemplateCode())
                .set(USER_CVS.SUMMARY, cv.getSummary())
                .set(USER_CVS.DATA, cv.getData())
                .set(USER_CVS.IS_DEFAULT, cv.getIsDefault())
                .set(USER_CVS.CREATED_AT, LocalDateTime.now())
                .set(USER_CVS.UPDATED_AT, LocalDateTime.now())
                .returning()
                .fetchOneInto(UserCv.class);
    }

    public Optional<UserCv> update(Long id, UserCv cv) {
        return dsl.update(USER_CVS)
                .set(USER_CVS.TITLE, cv.getTitle())
                .set(USER_CVS.TEMPLATE_CODE, cv.getTemplateCode())
                .set(USER_CVS.SUMMARY, cv.getSummary())
                .set(USER_CVS.DATA, cv.getData())
                .set(USER_CVS.IS_DEFAULT, cv.getIsDefault())
                .set(USER_CVS.UPDATED_AT, LocalDateTime.now())
                .where(USER_CVS.ID.eq(id))
                .returning()
                .fetchOptionalInto(UserCv.class);
    }

    public int delete(Long id) {
        return dsl.deleteFrom(USER_CVS)
                .where(USER_CVS.ID.eq(id))
                .execute();
    }

    public Page<UserCv> findAll(UserCv filter, Pageable pageable) {
        int offset = pageable.getOffset();
        int limit = pageable.getLimit();
        long total = count(filter);
        pageable.setTotal(total);

        Order order = pageable.getFirstOrder();
        String sortField = order.getPropertyOrDefault("ID");
        boolean isAsc = order.isAsc();

        List<UserCv> items = dsl.select(getFields())
                .from(USER_CVS)
                .where(getWhereCondition(filter))
                .orderBy(isAsc ? USER_CVS.field(sortField).asc() : USER_CVS.field(sortField).desc())
                .limit(limit)
                .offset(offset)
                .fetchInto(UserCv.class);

        return new Page<>(pageable, items);
    }

    public long count(UserCv filter) {
        return dsl.selectCount()
                .from(USER_CVS)
                .where(getWhereCondition(filter))
                .fetchOne(0, long.class);
    }

    /**
     * Tìm CV mặc định của user (is_default = true)
     */
    public Optional<UserCv> findDefaultByUserId(Long userId) {
        return dsl.select(getFields())
                .from(USER_CVS)
                .where(USER_CVS.USER_ID.eq(userId))
                .and(USER_CVS.IS_DEFAULT.isTrue())
                .fetchOptionalInto(UserCv.class);
    }
}
