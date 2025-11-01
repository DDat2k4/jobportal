package com.example.jobportal.repository;

import com.example.jobportal.data.entity.Role;
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

import static com.example.generated.jooq.tables.Roles.ROLES;
import static org.jooq.impl.DSL.noCondition;

@Repository
public class RoleRepository {

    private final DSLContext dsl;

    public RoleRepository(DSLContext dsl) {
        this.dsl = dsl;
    }

    private static List<Field<?>> getFields() {
        return Arrays.asList(
                ROLES.ID,
                ROLES.NAME,
                ROLES.DESCRIPTION
        );
    }

    public Condition getWhereCondition(Role filter) {
        Condition condition = noCondition();

        if (filter.getId() != null) {
            condition = condition.and(ROLES.ID.eq(filter.getId()));
        }
        if (filter.getName() != null && !filter.getName().isEmpty()) {
            condition = condition.and(ROLES.NAME.likeIgnoreCase("%" + filter.getName() + "%"));
        }

        return condition;
    }

    public Optional<Role> findById(Long id) {
        return dsl.select(getFields())
                .from(ROLES)
                .where(ROLES.ID.eq(id))
                .fetchOptionalInto(Role.class);
    }

    public Optional<Role> findByName(String name) {
        return dsl.select(getFields())
                .from(ROLES)
                .where(ROLES.NAME.eq(name))
                .fetchOptionalInto(Role.class);
    }

    public Optional<Role> findByFilter(Role filter) {
        return dsl.select(getFields())
                .from(ROLES)
                .where(getWhereCondition(filter))
                .fetchOptionalInto(Role.class);
    }

    public Role create(Role role) {
        return dsl.insertInto(ROLES)
                .set(ROLES.NAME, role.getName())
                .set(ROLES.DESCRIPTION, role.getDescription())
                .returning()
                .fetchOneInto(Role.class);
    }

    public Optional<Role> update(Long id, Role role) {
        return dsl.update(ROLES)
                .set(ROLES.NAME, role.getName())
                .set(ROLES.DESCRIPTION, role.getDescription())
                .where(ROLES.ID.eq(id))
                .returning()
                .fetchOptionalInto(Role.class);
    }

    public int delete(Long id) {
        return dsl.deleteFrom(ROLES)
                .where(ROLES.ID.eq(id))
                .execute();
    }

    public Page<Role> findAll(Role filter, Pageable pageable) {
        int offset = pageable.getOffset();
        int limit = pageable.getLimit();
        long total = count(filter);
        pageable.setTotal(total);

        Order order = pageable.getFirstOrder();
        String sortField = order.getPropertyOrDefault("ID");
        boolean isAsc = order.isAsc();

        List<Role> items = dsl.select(getFields())
                .from(ROLES)
                .where(getWhereCondition(filter))
                .orderBy(isAsc ? ROLES.field(sortField).asc() : ROLES.field(sortField).desc())
                .limit(limit)
                .offset(offset)
                .fetchInto(Role.class);

        return new Page<>(pageable, items);
    }

    public long count(Role filter) {
        return dsl.selectCount()
                .from(ROLES)
                .where(getWhereCondition(filter))
                .fetchOne(0, long.class);
    }
}
