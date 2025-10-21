package com.example.jobportal.repository;

import com.example.jobportal.data.entity.Job;
import com.example.jobportal.data.pojo.CategoryJobCount;
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

import static com.example.generated.jooq.tables.Jobs.JOBS;
import static com.example.generated.jooq.tables.JobCategories.JOB_CATEGORIES;

@RequiredArgsConstructor
@Repository
public class JobRepository {

    private final DSLContext dsl;

    private static List<Field<?>> getFields() {
        return Arrays.asList(
                JOBS.ID, JOBS.COMPANY_ID, JOBS.TITLE, JOBS.DESCRIPTION,
                JOBS.REQUIREMENTS, JOBS.SALARY_RANGE, JOBS.LOCATION,
                JOBS.CATEGORY_ID, JOBS.TYPE, JOBS.DEADLINE,
                JOBS.STATUS, JOBS.CREATED_AT, JOBS.UPDATED_AT
        );
    }

    private Condition getWhereCondition(Job filter) {
        Condition condition = JOBS.ID.isNotNull();

        if (filter == null) return condition;

        if (filter.getId() != null)
            condition = condition.and(JOBS.ID.eq(filter.getId()));

        if (filter.getCompanyId() != null)
            condition = condition.and(JOBS.COMPANY_ID.eq(filter.getCompanyId()));

        if (filter.getCategoryId() != null)
            condition = condition.and(JOBS.CATEGORY_ID.eq(filter.getCategoryId()));

        if (filter.getStatus() != null)
            condition = condition.and(JOBS.STATUS.eq(filter.getStatus()));

        if (filter.getTitle() != null && !filter.getTitle().isEmpty())
            condition = condition.and(JOBS.TITLE.containsIgnoreCase(filter.getTitle()));

        if (filter.getLocation() != null && !filter.getLocation().isEmpty())
            condition = condition.and(JOBS.LOCATION.containsIgnoreCase(filter.getLocation()));

        if (filter.getDescription() != null && !filter.getDescription().isEmpty())
            condition = condition.and(JOBS.DESCRIPTION.containsIgnoreCase(filter.getDescription()));
        return condition;
    }

    public Optional<Job> findById(Long id) {
        return dsl.select(getFields())
                .from(JOBS)
                .where(JOBS.ID.eq(id))
                .fetchOptionalInto(Job.class);
    }

    public Optional<Job> findByFilter(Job filter) {
        return dsl.select(getFields())
                .from(JOBS)
                .where(getWhereCondition(filter))
                .fetchOptionalInto(Job.class);
    }

    public Job create(Job job) {
        return dsl.insertInto(JOBS)
                .set(JOBS.COMPANY_ID, job.getCompanyId())
                .set(JOBS.TITLE, job.getTitle())
                .set(JOBS.DESCRIPTION, job.getDescription())
                .set(JOBS.REQUIREMENTS, job.getRequirements())
                .set(JOBS.SALARY_RANGE, job.getSalaryRange())
                .set(JOBS.LOCATION, job.getLocation())
                .set(JOBS.CATEGORY_ID, job.getCategoryId())
                .set(JOBS.TYPE, job.getType())
                .set(JOBS.DEADLINE, job.getDeadline())
                .set(JOBS.STATUS, job.getStatus() != null ? job.getStatus() : (short) 1)
                .set(JOBS.CREATED_AT, LocalDateTime.now())
                .set(JOBS.UPDATED_AT, LocalDateTime.now())
                .returning()
                .fetchOneInto(Job.class);
    }

    public Optional<Job> update(Job job) {
        return dsl.update(JOBS)
                .set(JOBS.TITLE, job.getTitle())
                .set(JOBS.DESCRIPTION, job.getDescription())
                .set(JOBS.REQUIREMENTS, job.getRequirements())
                .set(JOBS.SALARY_RANGE, job.getSalaryRange())
                .set(JOBS.LOCATION, job.getLocation())
                .set(JOBS.CATEGORY_ID, job.getCategoryId())
                .set(JOBS.TYPE, job.getType())
                .set(JOBS.STATUS, job.getStatus())
                .set(JOBS.UPDATED_AT, LocalDateTime.now())
                .where(JOBS.ID.eq(job.getId()))
                .returning()
                .fetchOptionalInto(Job.class);
    }

    public int delete(Long id) {
        return dsl.deleteFrom(JOBS)
                .where(JOBS.ID.eq(id))
                .execute();
    }

    public Page<Job> findAll(Job filter, Pageable pageable) {
        pageable = pageable != null ? pageable : new Pageable();
        int offset = pageable.getOffset();
        int limit = pageable.getLimit();

        // Sort
        Order order = pageable.getFirstOrder();
        String sortField = order.getPropertyOrDefault("id");
        boolean isAsc = order.isAsc();

        Field<?> sort = JOBS.field(sortField);
        if (sort == null) sort = JOBS.ID; // fallback

        // Count tổng
        long total = count(filter);
        pageable.setTotal(total);

        // Query chính
        List<Job> items = dsl.select(getFields())
                .from(JOBS)
                .where(getWhereCondition(filter))
                .orderBy(isAsc ? sort.asc() : sort.desc())
                .limit(limit)
                .offset(offset)
                .fetchInto(Job.class);

        return new Page<>(pageable, items);
    }

    public long count(Job filter) {
        return dsl.fetchCount(
                dsl.selectOne().from(JOBS).where(getWhereCondition(filter))
        );
    }

    public List<CategoryJobCount> countJobsByCategory(Job filter) {
        return dsl.select(
                        JOB_CATEGORIES.ID.as("categoryId"),
                        JOB_CATEGORIES.NAME.as("categoryName"),
                        DSL.count(JOBS.ID).as("jobCount")
                )
                .from(JOBS)
                .leftJoin(JOB_CATEGORIES).on(JOBS.CATEGORY_ID.eq(JOB_CATEGORIES.ID))
                .where(getWhereCondition(filter))
                .groupBy(JOB_CATEGORIES.ID, JOB_CATEGORIES.NAME)
                .orderBy(JOB_CATEGORIES.NAME.asc())
                .fetchInto(CategoryJobCount.class);
    }
}
