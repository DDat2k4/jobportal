package com.example.jobportal.repository;

import com.example.jobportal.data.entity.JobCategory;
import com.example.jobportal.extension.paging.Order;
import com.example.jobportal.extension.paging.Page;
import com.example.jobportal.extension.paging.Pageable;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.SortField;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.example.generated.jooq.tables.JobCategories.JOB_CATEGORIES;

@Repository
public class JobCategoryRepository {

    private final DSLContext dsl;

    public JobCategoryRepository(DSLContext dsl) {
        this.dsl = dsl;
    }

    private static List<Field<?>> getFields() {
        return Arrays.asList(
                JOB_CATEGORIES.ID,
                JOB_CATEGORIES.NAME,
                JOB_CATEGORIES.DESCRIPTION
        );
    }

    private Condition getWhereCondition(JobCategory filter) {
        Condition condition = JOB_CATEGORIES.ID.isNotNull();

        if (filter.getId() != null) {
            condition = condition.and(JOB_CATEGORIES.ID.eq(filter.getId()));
        }

        if (filter.getName() != null && !filter.getName().isBlank()) {
            condition = condition.and(JOB_CATEGORIES.NAME.likeIgnoreCase("%" + filter.getName().trim() + "%"));
        }

        if (filter.getDescription() != null && !filter.getDescription().isBlank()) {
            condition = condition.and(JOB_CATEGORIES.DESCRIPTION.likeIgnoreCase("%" + filter.getDescription().trim() + "%"));
        }

        return condition;
    }

    public Optional<JobCategory> find(JobCategory filter) {
        return dsl.select(getFields())
                .from(JOB_CATEGORIES)
                .where(getWhereCondition(filter))
                .fetchOptionalInto(JobCategory.class);
    }

    public JobCategory create(JobCategory category) {
        return dsl.insertInto(JOB_CATEGORIES)
                .set(JOB_CATEGORIES.NAME, category.getName())
                .set(JOB_CATEGORIES.DESCRIPTION, category.getDescription())
                .returning(getFields())
                .fetchOneInto(JobCategory.class);
    }

    public Optional<JobCategory> update(JobCategory category) {
        return dsl.update(JOB_CATEGORIES)
                .set(JOB_CATEGORIES.NAME, category.getName())
                .set(JOB_CATEGORIES.DESCRIPTION, category.getDescription())
                .where(JOB_CATEGORIES.ID.eq(category.getId()))
                .returning(getFields())
                .fetchOptionalInto(JobCategory.class);
    }

    public int delete(JobCategory category) {
        return dsl.deleteFrom(JOB_CATEGORIES)
                .where(JOB_CATEGORIES.ID.eq(category.getId()))
                .execute();
    }

    public long count(JobCategory filter) {
        return dsl.selectCount()
                .from(JOB_CATEGORIES)
                .where(getWhereCondition(filter))
                .fetchOne(0, long.class);
    }

    public Page<JobCategory> findAll(JobCategory filter, Pageable pageable) {
        // Đếm tổng
        long total = count(filter);
        pageable.setTotal(total);

        // Lấy thông tin sắp xếp
        Order order = pageable.getFirstOrder();
        String sortField = order != null ? order.getPropertyOrDefault("id") : "id";
        boolean asc = order == null || order.isAsc();
        SortField<?> sort = asc
                ? JOB_CATEGORIES.field(sortField).asc()
                : JOB_CATEGORIES.field(sortField).desc();

        // Query dữ liệu
        List<JobCategory> items = dsl.select(getFields())
                .from(JOB_CATEGORIES)
                .where(getWhereCondition(filter))
                .orderBy(sort)
                .limit(pageable.getLimit())
                .offset(pageable.getOffset())
                .fetchInto(JobCategory.class);

        // Trả về page kết quả
        return new Page<>(pageable, items);
    }
}