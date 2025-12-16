package com.example.jobportal.repository.learningpath;

import com.example.jobportal.data.entity.learningpath.LearningResource;
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

import static com.example.generated.jooq.tables.LearningResource.LEARNING_RESOURCE;
import static org.jooq.impl.DSL.noCondition;

@Repository
public class LearningResourceRepository {

    private final DSLContext dsl;

    public LearningResourceRepository(DSLContext dsl) {
        this.dsl = dsl;
    }

    private static List<Field<?>> getFields() {
        return Arrays.asList(
                LEARNING_RESOURCE.ID,
                LEARNING_RESOURCE.SKILL_ID,
                LEARNING_RESOURCE.TITLE,
                LEARNING_RESOURCE.URL,
                LEARNING_RESOURCE.TYPE,
                LEARNING_RESOURCE.DIFFICULTY,
                LEARNING_RESOURCE.DURATION_MINUTES,
                LEARNING_RESOURCE.PROVIDER,
                LEARNING_RESOURCE.CREATED_AT
        );
    }

    private Condition getWhereCondition(LearningResource filter) {
        Condition cond = noCondition();
        if (filter == null) return cond;

        if (filter.getSkillId() != null)
            cond = cond.and(LEARNING_RESOURCE.SKILL_ID.eq(filter.getSkillId()));

        if (filter.getType() != null && !filter.getType().isEmpty())
            cond = cond.and(LEARNING_RESOURCE.TYPE.eq(filter.getType()));

        if (filter.getTitle() != null && !filter.getTitle().isEmpty())
            cond = cond.and(LEARNING_RESOURCE.TITLE.containsIgnoreCase(filter.getTitle()));

        return cond;
    }

    public Optional<LearningResource> findById(Long id) {
        return dsl.select(getFields())
                .from(LEARNING_RESOURCE)
                .where(LEARNING_RESOURCE.ID.eq(id))
                .fetchOptionalInto(LearningResource.class);
    }

    public List<LearningResource> findBySkillId(Long skillId) {
        return dsl.select(getFields())
                .from(LEARNING_RESOURCE)
                .where(LEARNING_RESOURCE.SKILL_ID.eq(skillId))
                .fetchInto(LearningResource.class);
    }

    public LearningResource create(LearningResource r) {
        return dsl.insertInto(LEARNING_RESOURCE)
                .set(LEARNING_RESOURCE.SKILL_ID, r.getSkillId())
                .set(LEARNING_RESOURCE.TITLE, r.getTitle())
                .set(LEARNING_RESOURCE.URL, r.getUrl())
                .set(LEARNING_RESOURCE.TYPE, r.getType())
                .set(LEARNING_RESOURCE.DIFFICULTY, r.getDifficulty())
                .set(LEARNING_RESOURCE.DURATION_MINUTES, r.getDurationMinutes())
                .set(LEARNING_RESOURCE.PROVIDER, r.getProvider())
                .returning(getFields())
                .fetchOneInto(LearningResource.class);
    }

    public Optional<LearningResource> update(Long id, LearningResource r) {
        return dsl.update(LEARNING_RESOURCE)
                .set(LEARNING_RESOURCE.SKILL_ID, r.getSkillId())
                .set(LEARNING_RESOURCE.TITLE, r.getTitle())
                .set(LEARNING_RESOURCE.URL, r.getUrl())
                .set(LEARNING_RESOURCE.TYPE, r.getType())
                .set(LEARNING_RESOURCE.DIFFICULTY, r.getDifficulty())
                .set(LEARNING_RESOURCE.DURATION_MINUTES, r.getDurationMinutes())
                .set(LEARNING_RESOURCE.PROVIDER, r.getProvider())
                .where(LEARNING_RESOURCE.ID.eq(id))
                .returning(getFields())
                .fetchOptionalInto(LearningResource.class);
    }

    public int delete(Long id) {
        return dsl.deleteFrom(LEARNING_RESOURCE)
                .where(LEARNING_RESOURCE.ID.eq(id))
                .execute();
    }

    public Page<LearningResource> findAll(LearningResource filter, Pageable pageable) {
        if (pageable == null) pageable = new Pageable();

        int offset = pageable.getOffset();
        int limit = pageable.getLimit();
        long total = count(filter);
        pageable.setTotal(total);

        Order order = pageable.getFirstOrder();
        String sortField = order.getPropertyOrDefault("id");
        boolean isAsc = order.isAsc();

        Field<?> sort = LEARNING_RESOURCE.field(sortField);
        if (sort == null) sort = LEARNING_RESOURCE.ID;

        List<LearningResource> items = dsl.select(getFields())
                .from(LEARNING_RESOURCE)
                .where(getWhereCondition(filter))
                .orderBy(isAsc ? sort.asc() : sort.desc())
                .limit(limit)
                .offset(offset)
                .fetchInto(LearningResource.class);

        return new Page<>(pageable, items);
    }

    public long count(LearningResource filter) {
        return dsl.fetchCount(
                dsl.selectOne().from(LEARNING_RESOURCE).where(getWhereCondition(filter))
        );
    }

    public boolean existsBySkillIdAndUrl(Long skillId, String url) {
        return dsl.fetchExists(
                dsl.selectOne()
                        .from(LEARNING_RESOURCE)
                        .where(LEARNING_RESOURCE.SKILL_ID.eq(skillId))
                        .and(LEARNING_RESOURCE.URL.eq(url))
        );
    }
}

