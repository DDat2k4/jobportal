package com.example.jobportal.repository.learningpath;

import com.example.jobportal.data.entity.learningpath.SkillMapping;
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

import static com.example.generated.jooq.tables.SkillMapping.SKILL_MAPPING;
import static org.jooq.impl.DSL.noCondition;

@Repository
public class SkillMappingRepository {

    private final DSLContext dsl;

    public SkillMappingRepository(DSLContext dsl) {
        this.dsl = dsl;
    }

    private static List<Field<?>> getFields() {
        return Arrays.asList(
                SKILL_MAPPING.ID,
                SKILL_MAPPING.SKILL_ID,
                SKILL_MAPPING.ALIAS
        );
    }

    private Condition getWhereCondition(SkillMapping filter) {
        Condition cond = noCondition();
        if (filter == null) return cond;

        if (filter.getSkillId() != null)
            cond = cond.and(SKILL_MAPPING.SKILL_ID.eq(filter.getSkillId()));

        if (filter.getAlias() != null && !filter.getAlias().isEmpty())
            cond = cond.and(SKILL_MAPPING.ALIAS.containsIgnoreCase(filter.getAlias()));

        return cond;
    }

    public Optional<SkillMapping> findById(Long id) {
        return dsl.select(getFields())
                .from(SKILL_MAPPING)
                .where(SKILL_MAPPING.ID.eq(id))
                .fetchOptionalInto(SkillMapping.class);
    }

    public List<SkillMapping> findBySkillId(Long skillId) {
        return dsl.select(getFields())
                .from(SKILL_MAPPING)
                .where(SKILL_MAPPING.SKILL_ID.eq(skillId))
                .fetchInto(SkillMapping.class);
    }

    public Optional<SkillMapping> findByAlias(String alias) {
        return dsl.select(getFields())
                .from(SKILL_MAPPING)
                .where(SKILL_MAPPING.ALIAS.eq(alias))
                .fetchOptionalInto(SkillMapping.class);
    }

    public SkillMapping create(SkillMapping m) {
        return dsl.insertInto(SKILL_MAPPING)
                .set(SKILL_MAPPING.SKILL_ID, m.getSkillId())
                .set(SKILL_MAPPING.ALIAS, m.getAlias())
                .returning(getFields())
                .fetchOneInto(SkillMapping.class);
    }

    public Optional<SkillMapping> update(Long id, SkillMapping m) {
        return dsl.update(SKILL_MAPPING)
                .set(SKILL_MAPPING.SKILL_ID, m.getSkillId())
                .set(SKILL_MAPPING.ALIAS, m.getAlias())
                .where(SKILL_MAPPING.ID.eq(id))
                .returning(getFields())
                .fetchOptionalInto(SkillMapping.class);
    }

    public int delete(Long id) {
        return dsl.deleteFrom(SKILL_MAPPING)
                .where(SKILL_MAPPING.ID.eq(id))
                .execute();
    }

    public Page<SkillMapping> findAll(SkillMapping filter, Pageable pageable) {
        if (pageable == null) pageable = new Pageable();

        int offset = pageable.getOffset();
        int limit = pageable.getLimit();
        long total = count(filter);
        pageable.setTotal(total);

        Order order = pageable.getFirstOrder();
        String sortField = order.getPropertyOrDefault("id");
        boolean isAsc = order.isAsc();

        Field<?> sort = SKILL_MAPPING.field(sortField);
        if (sort == null) sort = SKILL_MAPPING.ID;

        List<SkillMapping> items = dsl.select(getFields())
                .from(SKILL_MAPPING)
                .where(getWhereCondition(filter))
                .orderBy(isAsc ? sort.asc() : sort.desc())
                .limit(limit)
                .offset(offset)
                .fetchInto(SkillMapping.class);

        return new Page<>(pageable, items);
    }

    public long count(SkillMapping filter) {
        return dsl.fetchCount(
                dsl.selectOne().from(SKILL_MAPPING).where(getWhereCondition(filter))
        );
    }
}

