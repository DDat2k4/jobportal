package com.example.jobportal.repository.learningpath;

import com.example.jobportal.data.entity.learningpath.RoadmapTemplate;
import com.example.jobportal.extension.paging.Order;
import com.example.jobportal.extension.paging.Page;
import com.example.jobportal.extension.paging.Pageable;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.example.generated.jooq.tables.RoadmapTemplate.ROADMAP_TEMPLATE;
import static org.jooq.impl.DSL.noCondition;

@Repository
public class RoadmapTemplateRepository {

    private final DSLContext dsl;

    public RoadmapTemplateRepository(DSLContext dsl) {
        this.dsl = dsl;
    }

    private static List<Field<?>> getFields() {
        return Arrays.asList(
                ROADMAP_TEMPLATE.ID,
                ROADMAP_TEMPLATE.SKILL_ID,
                ROADMAP_TEMPLATE.STEP_ORDER,
                ROADMAP_TEMPLATE.TITLE,
                ROADMAP_TEMPLATE.ACTION,
                ROADMAP_TEMPLATE.DURATION_DAYS,
                ROADMAP_TEMPLATE.CREATED_AT
        );
    }

    private Condition getWhereCondition(RoadmapTemplate filter) {
        Condition cond = noCondition();
        if (filter == null) return cond;

        if (filter.getSkillId() != null)
            cond = cond.and(ROADMAP_TEMPLATE.SKILL_ID.eq(filter.getSkillId()));

        if (filter.getTitle() != null && !filter.getTitle().isEmpty())
            cond = cond.and(ROADMAP_TEMPLATE.TITLE.containsIgnoreCase(filter.getTitle()));

        return cond;
    }

    public Optional<RoadmapTemplate> findById(Long id) {
        return dsl.select(getFields())
                .from(ROADMAP_TEMPLATE)
                .where(ROADMAP_TEMPLATE.ID.eq(id))
                .fetchOptionalInto(RoadmapTemplate.class);
    }

    public List<RoadmapTemplate> findBySkillIdOrderByStepOrder(Long skillId) {
        return dsl.select(getFields())
                .from(ROADMAP_TEMPLATE)
                .where(ROADMAP_TEMPLATE.SKILL_ID.eq(skillId))
                .orderBy(ROADMAP_TEMPLATE.STEP_ORDER.asc())
                .fetchInto(RoadmapTemplate.class);
    }

    public RoadmapTemplate create(RoadmapTemplate t) {
        return dsl.insertInto(ROADMAP_TEMPLATE)
                .set(ROADMAP_TEMPLATE.SKILL_ID, t.getSkillId())
                .set(ROADMAP_TEMPLATE.STEP_ORDER, t.getStepOrder())
                .set(ROADMAP_TEMPLATE.TITLE, t.getTitle())
                .set(ROADMAP_TEMPLATE.ACTION, t.getAction())
                .set(ROADMAP_TEMPLATE.DURATION_DAYS, t.getDurationDays())
                .returning(getFields())
                .fetchOneInto(RoadmapTemplate.class);
    }

    public Optional<RoadmapTemplate> update(Long id, RoadmapTemplate t) {
        return dsl.update(ROADMAP_TEMPLATE)
                .set(ROADMAP_TEMPLATE.SKILL_ID, t.getSkillId())
                .set(ROADMAP_TEMPLATE.STEP_ORDER, t.getStepOrder())
                .set(ROADMAP_TEMPLATE.TITLE, t.getTitle())
                .set(ROADMAP_TEMPLATE.ACTION, t.getAction())
                .set(ROADMAP_TEMPLATE.DURATION_DAYS, t.getDurationDays())
                .where(ROADMAP_TEMPLATE.ID.eq(id))
                .returning(getFields())
                .fetchOptionalInto(RoadmapTemplate.class);
    }

    public int delete(Long id) {
        return dsl.deleteFrom(ROADMAP_TEMPLATE)
                .where(ROADMAP_TEMPLATE.ID.eq(id))
                .execute();
    }

    public Page<RoadmapTemplate> findAll(RoadmapTemplate filter, Pageable pageable) {
        if (pageable == null) pageable = new Pageable();

        int offset = pageable.getOffset();
        int limit = pageable.getLimit();
        long total = count(filter);
        pageable.setTotal(total);

        Order order = pageable.getFirstOrder();
        String sortField = order.getPropertyOrDefault("id");
        boolean isAsc = order.isAsc();

        Field<?> sort = ROADMAP_TEMPLATE.field(sortField);
        if (sort == null) sort = ROADMAP_TEMPLATE.ID;

        List<RoadmapTemplate> items = dsl.select(getFields())
                .from(ROADMAP_TEMPLATE)
                .where(getWhereCondition(filter))
                .orderBy(isAsc ? sort.asc() : sort.desc())
                .limit(limit)
                .offset(offset)
                .fetchInto(RoadmapTemplate.class);

        return new Page<>(pageable, items);
    }

    public long count(RoadmapTemplate filter) {
        return dsl.fetchCount(
                dsl.selectOne().from(ROADMAP_TEMPLATE).where(getWhereCondition(filter))
        );
    }
}

