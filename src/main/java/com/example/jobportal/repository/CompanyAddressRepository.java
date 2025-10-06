package com.example.jobportal.repository;

import com.example.jobportal.data.entity.CompanyAddress;
import com.example.jobportal.extension.paging.Order;
import com.example.jobportal.extension.paging.Page;
import com.example.jobportal.extension.paging.Pageable;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.SortField;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.example.generated.jooq.tables.CompanyAddresses.COMPANY_ADDRESSES;

@Repository
public class CompanyAddressRepository {

    private final DSLContext dsl;

    public CompanyAddressRepository(DSLContext dsl) {
        this.dsl = dsl;
    }

    private static List<Field<?>> getFields() {
        return Arrays.asList(
                COMPANY_ADDRESSES.ID,
                COMPANY_ADDRESSES.COMPANY_ID,
                COMPANY_ADDRESSES.EMAIL,
                COMPANY_ADDRESSES.PHONE,
                COMPANY_ADDRESSES.LANDLINE,
                COMPANY_ADDRESSES.ADDRESS,
                COMPANY_ADDRESSES.ADDRESS2,
                COMPANY_ADDRESSES.CITY,
                COMPANY_ADDRESSES.STATE,
                COMPANY_ADDRESSES.COUNTRY,
                COMPANY_ADDRESSES.ZIP_CODE,
                COMPANY_ADDRESSES.CREATED_AT
        );
    }

    private Condition getWhereCondition(CompanyAddress filter) {
        Condition condition = COMPANY_ADDRESSES.ID.isNotNull();
        if (filter.getId() != null) {
            condition = condition.and(COMPANY_ADDRESSES.ID.eq(filter.getId()));
        }
        if (filter.getCompanyId() != null) {
            condition = condition.and(COMPANY_ADDRESSES.COMPANY_ID.eq(filter.getCompanyId()));
        }
        if (filter.getEmail() != null && !filter.getEmail().isBlank()) {
            condition = condition.and(COMPANY_ADDRESSES.EMAIL.likeIgnoreCase("%" + filter.getEmail().trim() + "%"));
        }
        if (filter.getCity() != null && !filter.getCity().isBlank()) {
            condition = condition.and(COMPANY_ADDRESSES.CITY.likeIgnoreCase("%" + filter.getCity().trim() + "%"));
        }
        if (filter.getCountry() != null && !filter.getCountry().isBlank()) {
            condition = condition.and(COMPANY_ADDRESSES.COUNTRY.likeIgnoreCase("%" + filter.getCountry().trim() + "%"));
        }
        return condition;
    }

    public Optional<CompanyAddress> findById(Long id) {
        return dsl.select(getFields())
                .from(COMPANY_ADDRESSES)
                .where(COMPANY_ADDRESSES.ID.eq(id))
                .fetchOptionalInto(CompanyAddress.class);
    }

    public Optional<CompanyAddress> findByFilter(CompanyAddress filter) {
        return dsl.select(getFields())
                .from(COMPANY_ADDRESSES)
                .where(getWhereCondition(filter))
                .fetchOptionalInto(CompanyAddress.class);
    }

    public CompanyAddress create(CompanyAddress address) {
        return dsl.insertInto(COMPANY_ADDRESSES)
                .set(COMPANY_ADDRESSES.COMPANY_ID, address.getCompanyId())
                .set(COMPANY_ADDRESSES.EMAIL, address.getEmail())
                .set(COMPANY_ADDRESSES.PHONE, address.getPhone())
                .set(COMPANY_ADDRESSES.LANDLINE, address.getLandline())
                .set(COMPANY_ADDRESSES.ADDRESS, address.getAddress())
                .set(COMPANY_ADDRESSES.ADDRESS2, address.getAddress2())
                .set(COMPANY_ADDRESSES.CITY, address.getCity())
                .set(COMPANY_ADDRESSES.STATE, address.getState())
                .set(COMPANY_ADDRESSES.COUNTRY, address.getCountry())
                .set(COMPANY_ADDRESSES.ZIP_CODE, address.getZipCode())
                .set(COMPANY_ADDRESSES.CREATED_AT, LocalDateTime.now())
                .returning()
                .fetchOneInto(CompanyAddress.class);
    }

    public Optional<CompanyAddress> update(Long id, CompanyAddress address) {
        return dsl.update(COMPANY_ADDRESSES)
                .set(COMPANY_ADDRESSES.EMAIL, address.getEmail())
                .set(COMPANY_ADDRESSES.PHONE, address.getPhone())
                .set(COMPANY_ADDRESSES.LANDLINE, address.getLandline())
                .set(COMPANY_ADDRESSES.ADDRESS, address.getAddress())
                .set(COMPANY_ADDRESSES.ADDRESS2, address.getAddress2())
                .set(COMPANY_ADDRESSES.CITY, address.getCity())
                .set(COMPANY_ADDRESSES.STATE, address.getState())
                .set(COMPANY_ADDRESSES.COUNTRY, address.getCountry())
                .set(COMPANY_ADDRESSES.ZIP_CODE, address.getZipCode())
                .where(COMPANY_ADDRESSES.ID.eq(id))
                .returning()
                .fetchOptionalInto(CompanyAddress.class);
    }

    public int delete(Long id) {
        return dsl.deleteFrom(COMPANY_ADDRESSES)
                .where(COMPANY_ADDRESSES.ID.eq(id))
                .execute();
    }

    public long count(CompanyAddress filter) {
        return dsl.selectCount()
                .from(COMPANY_ADDRESSES)
                .where(getWhereCondition(filter))
                .fetchOne(0, long.class);
    }

    public Page<CompanyAddress> findAll(CompanyAddress filter, Pageable pageable) {
        // Đếm tổng
        long total = count(filter);
        pageable.setTotal(total);

        // Lấy thông tin sắp xếp
        Order order = pageable.getFirstOrder();
        String sortField = order != null ? order.getPropertyOrDefault("id") : "id";
        boolean asc = order == null || order.isAsc();

        SortField<?> sort = asc
                ? COMPANY_ADDRESSES.field(sortField).asc()
                : COMPANY_ADDRESSES.field(sortField).desc();

        // Query dữ liệu
        List<CompanyAddress> items = dsl.select(getFields())
                .from(COMPANY_ADDRESSES)
                .where(getWhereCondition(filter))
                .orderBy(sort)
                .limit(pageable.getLimit())
                .offset(pageable.getOffset())
                .fetchInto(CompanyAddress.class);

        // Trả về Page
        return new Page<>(pageable, items);
    }
}
