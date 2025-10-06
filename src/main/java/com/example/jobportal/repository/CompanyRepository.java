package com.example.jobportal.repository;

import com.example.jobportal.data.entity.Company;
import com.example.jobportal.extension.paging.Order;
import com.example.jobportal.extension.paging.Page;
import com.example.jobportal.extension.paging.Pageable;
import org.jooq.*;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.*;

import static com.example.generated.jooq.tables.Companies.COMPANIES;

@Repository
public class CompanyRepository {

    private final DSLContext dsl;

    public CompanyRepository(DSLContext dsl) {
        this.dsl = dsl;
    }

    private static List<Field<?>> getFields() {
        return Arrays.asList(
                COMPANIES.ID,
                COMPANIES.NAME,
                COMPANIES.TAGLINE,
                COMPANIES.OWNER_NAME,
                COMPANIES.LOGO_URL,
                COMPANIES.CATEGORY_ID,
                COMPANIES.ESTABLISHED_YEAR,
                COMPANIES.EMPLOYEES,
                COMPANIES.WORKING_TIME,
                COMPANIES.DESCRIPTION,
                COMPANIES.WEBSITE,
                COMPANIES.CREATED_AT,
                COMPANIES.UPDATED_AT
        );
    }

    public Condition getWhereCondition(Company filter) {
        Condition condition = DSL.trueCondition();

        if (filter == null) return condition;

        if (filter.getId() != null) {
            condition = condition.and(COMPANIES.ID.eq(filter.getId()));
        }
        if (filter.getName() != null && !filter.getName().isEmpty()) {
            condition = condition.and(COMPANIES.NAME.likeIgnoreCase("%" + filter.getName() + "%"));
        }
        if (filter.getCategoryId() != null) {
            condition = condition.and(COMPANIES.CATEGORY_ID.eq(filter.getCategoryId()));
        }
        return condition;
    }

    public Optional<Company> findById(Long id) {
        return dsl.select(getFields())
                .from(COMPANIES)
                .where(COMPANIES.ID.eq(id))
                .fetchOptionalInto(Company.class);
    }

    public Optional<Company> findByFilter(Company filter) {
        return dsl.select(getFields())
                .from(COMPANIES)
                .where(getWhereCondition(filter))
                .limit(1)
                .fetchOptionalInto(Company.class);
    }

    public Company create(Company company) {
        return dsl.insertInto(COMPANIES)
                .set(COMPANIES.NAME, company.getName())
                .set(COMPANIES.TAGLINE, company.getTagline())
                .set(COMPANIES.OWNER_NAME, company.getOwnerName())
                .set(COMPANIES.LOGO_URL, company.getLogoUrl())
                .set(COMPANIES.CATEGORY_ID, company.getCategoryId())
                .set(COMPANIES.ESTABLISHED_YEAR, company.getEstablishedYear())
                .set(COMPANIES.EMPLOYEES, company.getEmployees())
                .set(COMPANIES.WORKING_TIME, company.getWorkingTime())
                .set(COMPANIES.DESCRIPTION, company.getDescription())
                .set(COMPANIES.WEBSITE, company.getWebsite())
                .set(COMPANIES.CREATED_AT, LocalDateTime.now())
                .set(COMPANIES.UPDATED_AT, LocalDateTime.now())
                .returning()
                .fetchOneInto(Company.class);
    }

    public Optional<Company> update(Long id, Company company) {
        return dsl.update(COMPANIES)
                .set(COMPANIES.NAME, company.getName())
                .set(COMPANIES.TAGLINE, company.getTagline())
                .set(COMPANIES.OWNER_NAME, company.getOwnerName())
                .set(COMPANIES.LOGO_URL, company.getLogoUrl())
                .set(COMPANIES.CATEGORY_ID, company.getCategoryId())
                .set(COMPANIES.ESTABLISHED_YEAR, company.getEstablishedYear())
                .set(COMPANIES.EMPLOYEES, company.getEmployees())
                .set(COMPANIES.WORKING_TIME, company.getWorkingTime())
                .set(COMPANIES.DESCRIPTION, company.getDescription())
                .set(COMPANIES.WEBSITE, company.getWebsite())
                .set(COMPANIES.UPDATED_AT, LocalDateTime.now())
                .where(COMPANIES.ID.eq(id))
                .returning()
                .fetchOptionalInto(Company.class);
    }

    public int delete(Long id) {
        return dsl.deleteFrom(COMPANIES)
                .where(COMPANIES.ID.eq(id))
                .execute();
    }

    public Page<Company> findAll(Company filter, Pageable pageable) {
        int offset = pageable.getOffset();
        int limit = pageable.getLimit();
        long total = count(filter);
        pageable.setTotal(total);

        // Sort
        Order order = pageable.getFirstOrder();
        String sortField = order.getPropertyOrDefault("ID");
        boolean isAsc = order.isAsc();

        List<Company> items = dsl.select(getFields())
                .from(COMPANIES)
                .where(getWhereCondition(filter))
                .orderBy(isAsc
                        ? COMPANIES.field(sortField).asc()
                        : COMPANIES.field(sortField).desc())
                .limit(limit)
                .offset(offset)
                .fetchInto(Company.class);

        return new Page<>(pageable, items);
    }

    public long count(Company filter) {
        return dsl.selectCount()
                .from(COMPANIES)
                .where(getWhereCondition(filter))
                .fetchOne(0, long.class);
    }
}
