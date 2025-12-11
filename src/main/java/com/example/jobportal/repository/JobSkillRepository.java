package com.example.jobportal.repository;

import com.example.jobportal.data.entity.JobSkill;
import com.example.jobportal.data.pojo.JobSkillWithName;
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

import static com.example.generated.jooq.Tables.SKILLS;
import static com.example.generated.jooq.tables.JobSkills.JOB_SKILLS;
import static org.jooq.impl.DSL.noCondition;

@Repository
public class JobSkillRepository {

    private final DSLContext dsl;

    public JobSkillRepository(DSLContext dsl) {
        this.dsl = dsl;
    }

    private static List<Field<?>> getFields() {
        return Arrays.asList(
                JOB_SKILLS.ID,
                JOB_SKILLS.JOB_ID,
                JOB_SKILLS.SKILL_ID,
                JOB_SKILLS.REQUIRED_LEVEL,
                JOB_SKILLS.PRIORITY
        );
    }

    public Condition getWhereCondition(JobSkill filter) {
        Condition condition = noCondition();

        if (filter == null) return condition;

        if (filter.getId() != null) condition = condition.and(JOB_SKILLS.ID.eq(filter.getId()));
        if (filter.getJobId() != null) condition = condition.and(JOB_SKILLS.JOB_ID.eq(filter.getJobId()));
        if (filter.getSkillId() != null) condition = condition.and(JOB_SKILLS.SKILL_ID.eq(filter.getSkillId()));
        if (filter.getRequiredLevel() != null) condition = condition.and(JOB_SKILLS.REQUIRED_LEVEL.eq(filter.getRequiredLevel()));
        if (filter.getPriority() != null) condition = condition.and(JOB_SKILLS.PRIORITY.eq(filter.getPriority()));

        return condition;
    }

    public Optional<JobSkill> findById(Long id) {
        return dsl.select(getFields())
                .from(JOB_SKILLS)
                .where(JOB_SKILLS.ID.eq(id))
                .fetchOptionalInto(JobSkill.class);
    }

    public JobSkill create(JobSkill jobSkill) {
        return dsl.insertInto(JOB_SKILLS)
                .set(JOB_SKILLS.JOB_ID, jobSkill.getJobId())
                .set(JOB_SKILLS.SKILL_ID, jobSkill.getSkillId())
                .set(JOB_SKILLS.REQUIRED_LEVEL, jobSkill.getRequiredLevel())
                .set(JOB_SKILLS.PRIORITY, jobSkill.getPriority())
                .returning()
                .fetchOneInto(JobSkill.class);
    }

    public Optional<JobSkill> update(Long id, JobSkill jobSkill) {
        return dsl.update(JOB_SKILLS)
                .set(JOB_SKILLS.JOB_ID, jobSkill.getJobId())
                .set(JOB_SKILLS.SKILL_ID, jobSkill.getSkillId())
                .set(JOB_SKILLS.REQUIRED_LEVEL, jobSkill.getRequiredLevel())
                .set(JOB_SKILLS.PRIORITY, jobSkill.getPriority())
                .where(JOB_SKILLS.ID.eq(id))
                .returning()
                .fetchOptionalInto(JobSkill.class);
    }

    public int delete(Long id) {
        return dsl.deleteFrom(JOB_SKILLS)
                .where(JOB_SKILLS.ID.eq(id))
                .execute();
    }

    public Page<JobSkill> findAll(JobSkill filter, Pageable pageable) {
        if (pageable == null) pageable = new Pageable();
        int offset = pageable.getOffset();
        int limit = pageable.getLimit();
        long total = count(filter);
        pageable.setTotal(total);

        Order order = pageable.getFirstOrder();
        String sortField = order.getPropertyOrDefault("id");
        boolean isAsc = order.isAsc();

        Field<?> sort = JOB_SKILLS.field(sortField);
        if (sort == null) sort = JOB_SKILLS.ID;

        List<JobSkill> items = dsl.select(getFields())
                .from(JOB_SKILLS)
                .where(getWhereCondition(filter))
                .orderBy(isAsc ? sort.asc() : sort.desc())
                .limit(limit)
                .offset(offset)
                .fetchInto(JobSkill.class);

        return new Page<>(pageable, items);
    }

    public long count(JobSkill filter) {
        return dsl.fetchCount(
                dsl.selectOne().from(JOB_SKILLS).where(getWhereCondition(filter))
        );
    }

    public List<JobSkill> findByJobId(Long jobId) {
        return dsl.select(getFields())
                .from(JOB_SKILLS)
                .where(JOB_SKILLS.JOB_ID.eq(jobId))
                .fetchInto(JobSkill.class);
    }

    public int deleteByJobId(Long jobId) {
        return dsl.deleteFrom(JOB_SKILLS)
                .where(JOB_SKILLS.JOB_ID.eq(jobId))
                .execute();
    }

    public List<String> findSkillNamesByJobId(Long jobId) {
        return dsl.select(SKILLS.NAME)
                .from(JOB_SKILLS)
                .join(SKILLS).on(SKILLS.ID.eq(JOB_SKILLS.SKILL_ID))
                .where(JOB_SKILLS.JOB_ID.eq(jobId))
                .fetchInto(String.class);
    }

    public List<JobSkillWithName> findByJobIdWithSkillName(Long jobId) {
        return dsl.select(
                        JOB_SKILLS.ID,
                        JOB_SKILLS.JOB_ID,
                        JOB_SKILLS.SKILL_ID,
                        SKILLS.NAME.as("skillName"),
                        JOB_SKILLS.REQUIRED_LEVEL,
                        JOB_SKILLS.PRIORITY
                )
                .from(JOB_SKILLS)
                .join(SKILLS).on(SKILLS.ID.eq(JOB_SKILLS.SKILL_ID))
                .where(JOB_SKILLS.JOB_ID.eq(jobId))
                .fetchInto(JobSkillWithName.class);
    }
}
