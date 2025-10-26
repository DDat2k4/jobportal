package com.example.jobportal.repository;

import com.example.jobportal.data.entity.EmployerCompany;
import lombok.RequiredArgsConstructor;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.example.generated.jooq.tables.Companies.COMPANIES;
import static com.example.generated.jooq.tables.EmployerCompanies.EMPLOYER_COMPANIES;
import static com.example.generated.jooq.tables.Users.USERS;

@Repository
@RequiredArgsConstructor
public class EmployerCompanyRepository {

    private final DSLContext dsl;

    private static List<Field<?>> getFields() {
        return Arrays.asList(
                EMPLOYER_COMPANIES.EMPLOYER_ID,
                EMPLOYER_COMPANIES.COMPANY_ID
        );
    }

    private Condition getWhereCondition(EmployerCompany filter) {
        Condition condition = DSL.trueCondition();

        if (filter.getEmployerId() != null) {
            condition = condition.and(EMPLOYER_COMPANIES.EMPLOYER_ID.eq(filter.getEmployerId()));
        }
        if (filter.getCompanyId() != null) {
            condition = condition.and(EMPLOYER_COMPANIES.COMPANY_ID.eq(filter.getCompanyId()));
        }

        return condition;
    }

    public List<EmployerCompany> find(EmployerCompany filter) {
        Condition condition = DSL.trueCondition();

        if (filter.getEmployerId() != null)
            condition = condition.and(EMPLOYER_COMPANIES.EMPLOYER_ID.eq(filter.getEmployerId()));

        if (filter.getCompanyId() != null)
            condition = condition.and(EMPLOYER_COMPANIES.COMPANY_ID.eq(filter.getCompanyId()));

        return dsl.select(
                EMPLOYER_COMPANIES.EMPLOYER_ID,
                        EMPLOYER_COMPANIES.COMPANY_ID,
                        USERS.USERNAME.as("employerUsername"),
                        COMPANIES.NAME.as("companyName")
                )
                .from(EMPLOYER_COMPANIES)
                .join(USERS).on(USERS.ID.eq(EMPLOYER_COMPANIES.EMPLOYER_ID))
                .join(COMPANIES).on(COMPANIES.ID.eq(EMPLOYER_COMPANIES.COMPANY_ID))
                .where(condition)
                .fetchInto(EmployerCompany.class);
    }

    public long count() {
        return dsl.selectCount()
                .from(EMPLOYER_COMPANIES)
                .fetchOne(0, long.class);
    }

    public EmployerCompany create(EmployerCompany entity) {
        return dsl.insertInto(EMPLOYER_COMPANIES)
                .set(EMPLOYER_COMPANIES.EMPLOYER_ID, entity.getEmployerId())
                .set(EMPLOYER_COMPANIES.COMPANY_ID, entity.getCompanyId())
                .returning()
                .fetchOneInto(EmployerCompany.class);
    }

    public Optional<EmployerCompany> update(Long employerId, EmployerCompany updated) {
        return dsl.update(EMPLOYER_COMPANIES)
                .set(EMPLOYER_COMPANIES.COMPANY_ID, updated.getCompanyId())
                .where(EMPLOYER_COMPANIES.EMPLOYER_ID.eq(employerId))
                .returning()
                .fetchOptionalInto(EmployerCompany.class);
    }

    public int delete(EmployerCompany entity) {
        return dsl.deleteFrom(EMPLOYER_COMPANIES)
                .where(EMPLOYER_COMPANIES.EMPLOYER_ID.eq(entity.getEmployerId()))
                .and(EMPLOYER_COMPANIES.COMPANY_ID.eq(entity.getCompanyId()))
                .execute();
    }

    public List<EmployerCompany> findCompaniesByEmployerId(Long employerId) {
        return dsl.select(
                        EMPLOYER_COMPANIES.EMPLOYER_ID,
                        EMPLOYER_COMPANIES.COMPANY_ID,
                        COMPANIES.NAME.as("companyName"),
                        USERS.USERNAME.as("employerUsername")
                )
                .from(EMPLOYER_COMPANIES)
                .join(COMPANIES).on(COMPANIES.ID.eq(EMPLOYER_COMPANIES.COMPANY_ID))
                .join(USERS).on(USERS.ID.eq(EMPLOYER_COMPANIES.EMPLOYER_ID))
                .where(EMPLOYER_COMPANIES.EMPLOYER_ID.eq(employerId))
                .fetchInto(EmployerCompany.class);
    }

    public boolean existsByEmployerIdAndCompanyId(Long employerId, Long companyId) {
        return dsl.fetchExists(
                dsl.selectOne()
                        .from(EMPLOYER_COMPANIES)
                        .where(EMPLOYER_COMPANIES.EMPLOYER_ID.eq(employerId))
                        .and(EMPLOYER_COMPANIES.COMPANY_ID.eq(companyId))
        );
    }
}
