package com.example.jobportal.repository;

import com.example.jobportal.data.entity.Permission;
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

import static com.example.generated.jooq.tables.Permissions.PERMISSIONS;
import static org.jooq.impl.DSL.noCondition;

@Repository
public class PermissionRepository {

    private final DSLContext dsl;

    public PermissionRepository(DSLContext dsl) {
        this.dsl = dsl;
    }

    private static List<Field<?>> getFields() {
        return Arrays.asList(
                PERMISSIONS.ID,
                PERMISSIONS.CODE,
                PERMISSIONS.DESCRIPTION
        );
    }

    public Condition getWhereCondition(Permission filter) {
        Condition condition = noCondition();

        if (filter.getId() != null) {
            condition = condition.and(PERMISSIONS.ID.eq(filter.getId()));
        }
        if (filter.getCode() != null && !filter.getCode().isEmpty()) {
            condition = condition.and(PERMISSIONS.CODE.likeIgnoreCase("%" + filter.getCode() + "%"));
        }

        return condition;
    }

    public Optional<Permission> findById(Long id) {
        return dsl.select(getFields())
                .from(PERMISSIONS)
                .where(PERMISSIONS.ID.eq(id))
                .fetchOptionalInto(Permission.class);
    }

    public Optional<Permission> findByCode(String code) {
        return dsl.select(getFields())
                .from(PERMISSIONS)
                .where(PERMISSIONS.CODE.eq(code))
                .fetchOptionalInto(Permission.class);
    }

    public Optional<Permission> findByFilter(Permission filter) {
        return dsl.select(getFields())
                .from(PERMISSIONS)
                .where(getWhereCondition(filter))
                .fetchOptionalInto(Permission.class);
    }

    public Permission create(Permission permission) {
        return dsl.insertInto(PERMISSIONS)
                .set(PERMISSIONS.CODE, permission.getCode())
                .set(PERMISSIONS.DESCRIPTION, permission.getDescription())
                .returning()
                .fetchOneInto(Permission.class);
    }

    public Optional<Permission> update(Long id, Permission permission) {
        return dsl.update(PERMISSIONS)
                .set(PERMISSIONS.CODE, permission.getCode())
                .set(PERMISSIONS.DESCRIPTION, permission.getDescription())
                .where(PERMISSIONS.ID.eq(id))
                .returning()
                .fetchOptionalInto(Permission.class);
    }

    public int delete(Long id) {
        return dsl.deleteFrom(PERMISSIONS)
                .where(PERMISSIONS.ID.eq(id))
                .execute();
    }

    public Page<Permission> findAll(Permission filter, Pageable pageable) {
        int offset = pageable.getOffset();
        int limit = pageable.getLimit();
        long total = count(filter);
        pageable.setTotal(total);

        Order order = pageable.getFirstOrder();
        String sortField = order.getPropertyOrDefault("ID");
        boolean isAsc = order.isAsc();

        List<Permission> items = dsl.select(getFields())
                .from(PERMISSIONS)
                .where(getWhereCondition(filter))
                .orderBy(isAsc ? PERMISSIONS.field(sortField).asc() : PERMISSIONS.field(sortField).desc())
                .limit(limit)
                .offset(offset)
                .fetchInto(Permission.class);

        return new Page<>(pageable, items);
    }

    public long count(Permission filter) {
        return dsl.selectCount()
                .from(PERMISSIONS)
                .where(getWhereCondition(filter))
                .fetchOne(0, long.class);
    }
}
