package com.example.jobportal.repository;

import com.example.jobportal.data.entity.EmployerCompany;
import lombok.RequiredArgsConstructor;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.OrderField;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.example.generated.jooq.tables.EmployerCompanies.EMPLOYER_COMPANIES;

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

    private Condition buildCondition(EmployerCompany filter) {
        Condition condition = DSL.trueCondition();

        if (filter.getEmployerId() != null) {
            condition = condition.and(EMPLOYER_COMPANIES.EMPLOYER_ID.eq(filter.getEmployerId()));
        }
        if (filter.getCompanyId() != null) {
            condition = condition.and(EMPLOYER_COMPANIES.COMPANY_ID.eq(filter.getCompanyId()));
        }

        return condition;
    }

    public Optional<EmployerCompany> find(EmployerCompany filter) {
        return dsl.select(getFields())
                .from(EMPLOYER_COMPANIES)
                .where(buildCondition(filter))
                .fetchOptionalInto(EmployerCompany.class);
    }

    public long count() {
        return dsl.selectCount()
                .from(EMPLOYER_COMPANIES)
                .fetchOne(0, long.class);
    }

    public int create(EmployerCompany entity) {
        return dsl.insertInto(EMPLOYER_COMPANIES)
                .set(EMPLOYER_COMPANIES.EMPLOYER_ID, entity.getEmployerId())
                .set(EMPLOYER_COMPANIES.COMPANY_ID, entity.getCompanyId())
                .execute();
    }

    public int delete(EmployerCompany entity) {
        return dsl.deleteFrom(EMPLOYER_COMPANIES)
                .where(EMPLOYER_COMPANIES.EMPLOYER_ID.eq(entity.getEmployerId()))
                .and(EMPLOYER_COMPANIES.COMPANY_ID.eq(entity.getCompanyId()))
                .execute();
    }

}
