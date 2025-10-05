package com.example.jobportal.repository;

import com.example.jobportal.data.entity.CompanySocial;
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

import static com.example.generated.jooq.tables.CompanySocials.COMPANY_SOCIALS;
import static org.jooq.impl.DSL.noCondition;

@Repository
public class CompanySocialRepository {

    private final DSLContext dsl;

    public CompanySocialRepository(DSLContext dsl) {
        this.dsl = dsl;
    }

    private static List<Field<?>> getFields() {
        return Arrays.asList(
                COMPANY_SOCIALS.ID,
                COMPANY_SOCIALS.COMPANY_ID,
                COMPANY_SOCIALS.PLATFORM,
                COMPANY_SOCIALS.URL,
                COMPANY_SOCIALS.CREATED_AT
        );
    }

    public Condition getWhereCondition(CompanySocial filter) {
        Condition condition = noCondition();

        if (filter.getId() != null) {
            condition = condition.and(COMPANY_SOCIALS.ID.eq(filter.getId()));
        }
        if (filter.getCompanyId() != null) {
            condition = condition.and(COMPANY_SOCIALS.COMPANY_ID.eq(filter.getCompanyId()));
        }
        if (filter.getPlatform() != null) {
            condition = condition.and(COMPANY_SOCIALS.PLATFORM.eq(filter.getPlatform()));
        }

        return condition;
    }

    public Optional<CompanySocial> findById(Long id) {
        return dsl.select(getFields())
                .from(COMPANY_SOCIALS)
                .where(COMPANY_SOCIALS.ID.eq(id))
                .fetchOptionalInto(CompanySocial.class);
    }

    public Optional<CompanySocial> findByFilter(CompanySocial filter) {
        return dsl.select(getFields())
                .from(COMPANY_SOCIALS)
                .where(getWhereCondition(filter))
                .fetchOptionalInto(CompanySocial.class);
    }

    public Long create(CompanySocial social) {
        return dsl.insertInto(COMPANY_SOCIALS)
                .set(COMPANY_SOCIALS.COMPANY_ID, social.getCompanyId())
                .set(COMPANY_SOCIALS.PLATFORM, social.getPlatform())
                .set(COMPANY_SOCIALS.URL, social.getUrl())
                .set(COMPANY_SOCIALS.CREATED_AT, LocalDateTime.now())
                .returning(COMPANY_SOCIALS.ID)
                .fetchOptionalInto(CompanySocial.class)
                .map(CompanySocial::getId)
                .orElseThrow(() -> new RuntimeException("Failed to insert company social"));
    }

    public int update(Long id, CompanySocial social) {
        return dsl.update(COMPANY_SOCIALS)
                .set(COMPANY_SOCIALS.PLATFORM, social.getPlatform())
                .set(COMPANY_SOCIALS.URL, social.getUrl())
                .where(COMPANY_SOCIALS.ID.eq(id))
                .execute();
    }

    public int delete(Long id) {
        return dsl.deleteFrom(COMPANY_SOCIALS)
                .where(COMPANY_SOCIALS.ID.eq(id))
                .execute();
    }

    public Page<CompanySocial> findAll(CompanySocial filter, Pageable pageable) {
        int offset = pageable.getOffset();
        int limit = pageable.getLimit();
        long total = count(filter);
        pageable.setTotal(total);

        Order order = pageable.getFirstOrder();
        String sortField = order.getPropertyOrDefault("ID");
        boolean isAsc = order.isAsc();

        List<CompanySocial> items = dsl.select(getFields())
                .from(COMPANY_SOCIALS)
                .where(getWhereCondition(filter))
                .orderBy(isAsc
                        ? COMPANY_SOCIALS.field(sortField).asc()
                        : COMPANY_SOCIALS.field(sortField).desc())
                .limit(limit)
                .offset(offset)
                .fetchInto(CompanySocial.class);

        return new Page<>(pageable, items);
    }

    public long count(CompanySocial filter) {
        return dsl.selectCount()
                .from(COMPANY_SOCIALS)
                .where(getWhereCondition(filter))
                .fetchOne(0, long.class);
    }
}
