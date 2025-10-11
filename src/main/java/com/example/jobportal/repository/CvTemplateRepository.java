package com.example.jobportal.repository;

import com.example.jobportal.data.entity.CvTemplate;
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

import static com.example.generated.jooq.tables.CvTemplates.CV_TEMPLATES;
import static org.jooq.impl.DSL.noCondition;

@Repository
public class CvTemplateRepository {

    private final DSLContext dsl;

    public CvTemplateRepository(DSLContext dsl) {
        this.dsl = dsl;
    }

    private static List<Field<?>> getFields() {
        return Arrays.asList(
                CV_TEMPLATES.ID,
                CV_TEMPLATES.CODE,
                CV_TEMPLATES.NAME,
                CV_TEMPLATES.PREVIEW_URL,
                CV_TEMPLATES.CONFIG,
                CV_TEMPLATES.CREATED_AT
        );
    }

    public Condition getWhereCondition(CvTemplate filter) {
        Condition condition = noCondition();

        if (filter.getId() != null) {
            condition = condition.and(CV_TEMPLATES.ID.eq(filter.getId()));
        }
        if (filter.getCode() != null) {
            condition = condition.and(CV_TEMPLATES.CODE.eq(filter.getCode()));
        }
        if (filter.getName() != null) {
            condition = condition.and(CV_TEMPLATES.NAME.likeIgnoreCase("%" + filter.getName() + "%"));
        }

        return condition;
    }

    public Optional<CvTemplate> findById(Long id) {
        return dsl.select(getFields())
                .from(CV_TEMPLATES)
                .where(CV_TEMPLATES.ID.eq(id))
                .fetchOptionalInto(CvTemplate.class);
    }

    public Optional<CvTemplate> findByCode(String code) {
        return dsl.select(getFields())
                .from(CV_TEMPLATES)
                .where(CV_TEMPLATES.CODE.eq(code))
                .fetchOptionalInto(CvTemplate.class);
    }

    public CvTemplate create(CvTemplate template) {
        return dsl.insertInto(CV_TEMPLATES)
                .set(CV_TEMPLATES.CODE, template.getCode())
                .set(CV_TEMPLATES.NAME, template.getName())
                .set(CV_TEMPLATES.PREVIEW_URL, template.getPreviewUrl())
                .set(CV_TEMPLATES.CONFIG, template.getConfig())
                .set(CV_TEMPLATES.CREATED_AT, LocalDateTime.now())
                .returning()
                .fetchOneInto(CvTemplate.class);
    }

    public Optional<CvTemplate> update(Long id, CvTemplate template) {
        return dsl.update(CV_TEMPLATES)
                .set(CV_TEMPLATES.NAME, template.getName())
                .set(CV_TEMPLATES.PREVIEW_URL, template.getPreviewUrl())
                .set(CV_TEMPLATES.CONFIG, template.getConfig())
                .where(CV_TEMPLATES.ID.eq(id))
                .returning()
                .fetchOptionalInto(CvTemplate.class);
    }

    public int delete(Long id) {
        return dsl.deleteFrom(CV_TEMPLATES)
                .where(CV_TEMPLATES.ID.eq(id))
                .execute();
    }

    public Page<CvTemplate> findAll(CvTemplate filter, Pageable pageable) {
        int offset = pageable.getOffset();
        int limit = pageable.getLimit();
        long total = count(filter);
        pageable.setTotal(total);

        Order order = pageable.getFirstOrder();
        String sortField = order.getPropertyOrDefault("ID");
        boolean isAsc = order.isAsc();

        List<CvTemplate> items = dsl.select(getFields())
                .from(CV_TEMPLATES)
                .where(getWhereCondition(filter))
                .orderBy(isAsc ? CV_TEMPLATES.field(sortField).asc() : CV_TEMPLATES.field(sortField).desc())
                .limit(limit)
                .offset(offset)
                .fetchInto(CvTemplate.class);

        return new Page<>(pageable, items);
    }

    public long count(CvTemplate filter) {
        return dsl.selectCount()
                .from(CV_TEMPLATES)
                .where(getWhereCondition(filter))
                .fetchOne(0, long.class);
    }

    /**
     * Kiểm tra xem template code đã tồn tại chưa (để tránh trùng mã)
     */
    public boolean existsByCode(String code) {
        Integer count = dsl.selectCount()
                .from(CV_TEMPLATES)
                .where(CV_TEMPLATES.CODE.eq(code))
                .fetchOne(0, int.class);
        return count != null && count > 0;
    }

    /**
     * Lấy danh sách tất cả template (dùng cho dropdown hoặc gallery)
     */
    public List<CvTemplate> findAllTemplates() {
        return dsl.select(getFields())
                .from(CV_TEMPLATES)
                .orderBy(CV_TEMPLATES.CREATED_AT.desc())
                .fetchInto(CvTemplate.class);
    }
}
