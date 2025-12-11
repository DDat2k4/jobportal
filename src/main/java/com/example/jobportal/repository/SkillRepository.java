package com.example.jobportal.repository;

import com.example.jobportal.data.entity.Skill;
import com.example.jobportal.extension.paging.Order;
import com.example.jobportal.extension.paging.Page;
import com.example.jobportal.extension.paging.Pageable;
import com.example.jobportal.util.CommonUtils;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.example.generated.jooq.tables.Skills.SKILLS;
import static org.jooq.impl.DSL.noCondition;

@Repository
public class SkillRepository {
    private final DSLContext dsl;
    public SkillRepository(DSLContext dsl) {
        this.dsl = dsl;
    }

    private static List<Field<?>> getFields() {
        return Arrays.asList(
                SKILLS.ID,
                SKILLS.NAME,
                SKILLS.TYPE,
                SKILLS.NORMALIZED_NAME,
                SKILLS.DIFFICULTY,
                SKILLS.ALIASES,
                SKILLS.DESCRIPTION,
                SKILLS.CREATED_AT
        );
    }

    public Condition getWhereCondition(Skill filter) {
        Condition condition = noCondition();
        if (filter == null) return condition;

        if (filter.getId() != null)
            condition = condition.and(SKILLS.ID.eq(filter.getId()));

        if (filter.getName() != null && !filter.getName().isEmpty())
            condition = condition.and(SKILLS.NAME.containsIgnoreCase(filter.getName()));

        if (filter.getType() != null && !filter.getType().isEmpty())
            condition = condition.and(SKILLS.TYPE.eq(filter.getType()));

        if (filter.getNormalizedName() != null && !filter.getNormalizedName().isEmpty())
            condition = condition.and(SKILLS.NORMALIZED_NAME.containsIgnoreCase(filter.getNormalizedName()));

        return condition;
    }

    public Optional<Skill> findById(Long id) {
        return dsl.select(getFields())
                .from(SKILLS)
                .where(SKILLS.ID.eq(id))
                .fetchOptionalInto(Skill.class);
    }

    public Optional<Skill> findByName(String name) {
        return dsl.select(getFields())
                .from(SKILLS)
                .where(SKILLS.NAME.eq(name))
                .fetchOptionalInto(Skill.class);
    }

    public Skill create(Skill skill) {
        return dsl.insertInto(SKILLS)
                .set(SKILLS.NAME, skill.getName())
                .set(SKILLS.TYPE, skill.getType())
                .set(SKILLS.NORMALIZED_NAME, skill.getNormalizedName())
                .set(SKILLS.DIFFICULTY, skill.getDifficulty())
                .set(SKILLS.ALIASES, CommonUtils.toJsonb(skill.getAliases()))
                .set(SKILLS.DESCRIPTION, skill.getDescription())
                .returning(getFields())
                .fetchOneInto(Skill.class);
    }

    public Optional<Skill> update(Long id, Skill skill) {
        return dsl.update(SKILLS)
                .set(SKILLS.NAME, skill.getName())
                .set(SKILLS.TYPE, skill.getType())
                .set(SKILLS.NORMALIZED_NAME, skill.getNormalizedName())
                .set(SKILLS.DIFFICULTY, skill.getDifficulty())
                .set(SKILLS.ALIASES, CommonUtils.toJsonb(skill.getAliases()))
                .set(SKILLS.DESCRIPTION, skill.getDescription())
                .where(SKILLS.ID.eq(id))
                .returning(getFields())
                .fetchOptionalInto(Skill.class);
    }

    public int delete(Long id) {
        return dsl.deleteFrom(SKILLS)
                .where(SKILLS.ID.eq(id))
                .execute();
    }

    public Page<Skill> findAll(Skill filter, Pageable pageable) {
        if (pageable == null) pageable = new Pageable();

        int offset = pageable.getOffset();
        int limit = pageable.getLimit();
        long total = count(filter);
        pageable.setTotal(total);

        Order order = pageable.getFirstOrder();
        String sortField = order.getPropertyOrDefault("id");
        boolean isAsc = order.isAsc();

        Field<?> sort = SKILLS.field(sortField);
        if (sort == null) sort = SKILLS.ID;

        List<Skill> items = dsl.select(getFields())
                .from(SKILLS)
                .where(getWhereCondition(filter))
                .orderBy(isAsc ? sort.asc() : sort.desc())
                .limit(limit)
                .offset(offset)
                .fetchInto(Skill.class);

        return new Page<>(pageable, items);
    }

    public long count(Skill filter) {
        return dsl.fetchCount(
                dsl.selectOne().from(SKILLS).where(getWhereCondition(filter))
        );
    }

    public Optional<Skill> findByNormalizedName(String normalizedName) {
        return dsl.select(getFields())
                .from(SKILLS)
                .where(SKILLS.NORMALIZED_NAME.eq(normalizedName))
                .fetchOptionalInto(Skill.class);
    }
}
