package com.example.jobportal.repository;

import com.example.jobportal.data.entity.Application;
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

import static com.example.generated.jooq.tables.Applications.APPLICATIONS;
import static org.jooq.impl.DSL.noCondition;

@Repository
public class ApplicationRepository {

    private final DSLContext dsl;

    public ApplicationRepository(DSLContext dsl) {
        this.dsl = dsl;
    }

    private static List<Field<?>> getFields() {
        return Arrays.asList(
                APPLICATIONS.ID,
                APPLICATIONS.JOB_ID,
                APPLICATIONS.SEEKER_ID,
                APPLICATIONS.CV_ID,
                APPLICATIONS.COVER_LETTER,
                APPLICATIONS.STATUS,
                APPLICATIONS.FEEDBACK,
                APPLICATIONS.APPLIED_AT
        );
    }

    public Condition getWhereCondition(Application filter) {
        Condition condition = noCondition();

        if (filter.getId() != null) {
            condition = condition.and(APPLICATIONS.ID.eq(filter.getId()));
        }
        if (filter.getJobId() != null) {
            condition = condition.and(APPLICATIONS.JOB_ID.eq(filter.getJobId()));
        }
        if (filter.getSeekerId() != null) {
            condition = condition.and(APPLICATIONS.SEEKER_ID.eq(filter.getSeekerId()));
        }
        if (filter.getStatus() != null) {
            condition = condition.and(APPLICATIONS.STATUS.eq(filter.getStatus()));
        }

        return condition;
    }

    public Optional<Application> findById(Long id) {
        return dsl.select(getFields())
                .from(APPLICATIONS)
                .where(APPLICATIONS.ID.eq(id))
                .fetchOptionalInto(Application.class);
    }

    public Optional<Application> findByFilter(Application filter) {
        return dsl.select(getFields())
                .from(APPLICATIONS)
                .where(getWhereCondition(filter))
                .fetchOptionalInto(Application.class);
    }

    public Application create(Application app) {
        return dsl.insertInto(APPLICATIONS)
                .set(APPLICATIONS.JOB_ID, app.getJobId())
                .set(APPLICATIONS.SEEKER_ID, app.getSeekerId())
                .set(APPLICATIONS.CV_ID, app.getCvId())
                .set(APPLICATIONS.COVER_LETTER, app.getCoverLetter())
                .set(APPLICATIONS.STATUS, app.getStatus())
                .set(APPLICATIONS.FEEDBACK, app.getFeedback())
                .set(APPLICATIONS.APPLIED_AT, LocalDateTime.now())
                .returning()
                .fetchOneInto(Application.class);
    }

    public Optional<Application> update(Long id, Application app) {
        return dsl.update(APPLICATIONS)
                .set(APPLICATIONS.CV_ID, app.getCvId())
                .set(APPLICATIONS.COVER_LETTER, app.getCoverLetter())
                .set(APPLICATIONS.STATUS, app.getStatus())
                .set(APPLICATIONS.FEEDBACK, app.getFeedback())
                .where(APPLICATIONS.ID.eq(id))
                .returning()
                .fetchOptionalInto(Application.class);
    }

    public int delete(Long id) {
        return dsl.deleteFrom(APPLICATIONS)
                .where(APPLICATIONS.ID.eq(id))
                .execute();
    }

    public Page<Application> findAll(Application filter, Pageable pageable) {
        int offset = pageable.getOffset();
        int limit = pageable.getLimit();
        long total = count(filter);
        pageable.setTotal(total);

        Order order = pageable.getFirstOrder();
        String sortField = order.getPropertyOrDefault("ID");
        boolean isAsc = order.isAsc();

        List<Application> items = dsl.select(getFields())
                .from(APPLICATIONS)
                .where(getWhereCondition(filter))
                .orderBy(isAsc ? APPLICATIONS.field(sortField).asc() : APPLICATIONS.field(sortField).desc())
                .limit(limit)
                .offset(offset)
                .fetchInto(Application.class);

        return new Page<>(pageable, items);
    }

    public long count(Application filter) {
        return dsl.selectCount()
                .from(APPLICATIONS)
                .where(getWhereCondition(filter))
                .fetchOne(0, long.class);
    }
}
