package com.example.jobportal.repository;

import com.example.jobportal.data.entity.Notification;
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
import java.util.Map;
import java.util.Optional;

import static com.example.generated.jooq.tables.Notifications.NOTIFICATIONS;
import static org.jooq.impl.DSL.noCondition;

@Repository
public class NotificationRepository {

    private final DSLContext dsl;

    public NotificationRepository(DSLContext dsl) {
        this.dsl = dsl;
    }

    private static List<Field<?>> getFields() {
        return Arrays.asList(
                NOTIFICATIONS.ID,
                NOTIFICATIONS.USER_ID,
                NOTIFICATIONS.MESSAGE,
                NOTIFICATIONS.IS_READ,
                NOTIFICATIONS.CREATED_AT
        );
    }

    public Condition getWhereCondition(Notification filter) {
        Condition condition = noCondition();

        if (filter.getId() != null) {
            condition = condition.and(NOTIFICATIONS.ID.eq(filter.getId()));
        }
        if (filter.getUserId() != null) {
            condition = condition.and(NOTIFICATIONS.USER_ID.eq(filter.getUserId()));
        }
        if (filter.getIsRead() != null) {
            condition = condition.and(NOTIFICATIONS.IS_READ.eq(filter.getIsRead()));
        }

        return condition;
    }

    public Optional<Notification> findById(Long id) {
        return dsl.select(getFields())
                .from(NOTIFICATIONS)
                .where(NOTIFICATIONS.ID.eq(id))
                .fetchOptionalInto(Notification.class);
    }

    public Optional<Notification> findByFilter(Notification filter) {
        return dsl.select(getFields())
                .from(NOTIFICATIONS)
                .where(getWhereCondition(filter))
                .fetchOptionalInto(Notification.class);
    }

    public Notification create(Notification notification) {
        return dsl.insertInto(NOTIFICATIONS)
                .set(NOTIFICATIONS.USER_ID, notification.getUserId())
                .set(NOTIFICATIONS.MESSAGE, notification.getMessage())
                .set(NOTIFICATIONS.IS_READ, notification.getIsRead() != null ? notification.getIsRead() : false)
                .set(NOTIFICATIONS.CREATED_AT, LocalDateTime.now())
                .returning()
                .fetchOneInto(Notification.class);
    }

    public Optional<Notification> update(Long id, Notification notification) {
        return dsl.update(NOTIFICATIONS)
                .set(NOTIFICATIONS.MESSAGE, notification.getMessage())
                .set(NOTIFICATIONS.IS_READ, notification.getIsRead())
                .where(NOTIFICATIONS.ID.eq(id))
                .returning()
                .fetchOptionalInto(Notification.class);
    }

    public int delete(Long id) {
        return dsl.deleteFrom(NOTIFICATIONS)
                .where(NOTIFICATIONS.ID.eq(id))
                .execute();
    }

    private static final Map<String, Field<?>> SORTABLE_FIELDS = Map.of(
            "id", NOTIFICATIONS.ID,
            "createdAt", NOTIFICATIONS.CREATED_AT,
            "isRead", NOTIFICATIONS.IS_READ,
            "userId", NOTIFICATIONS.USER_ID
    );

    public Page<Notification> findAll(Notification filter, Pageable pageable) {
        int offset = pageable.getOffset();
        int limit = pageable.getLimit();
        long total = count(filter);
        pageable.setTotal(total);

        Order order = pageable.getFirstOrder();
        String sortField = order.getPropertyOrDefault("id");
        boolean isAsc = order.isAsc();

        Field<?> sortColumn = SORTABLE_FIELDS.getOrDefault(sortField, NOTIFICATIONS.ID);

        List<Notification> items = dsl.select(getFields())
                .from(NOTIFICATIONS)
                .where(getWhereCondition(filter))
                .orderBy(isAsc ? sortColumn.asc() : sortColumn.desc())
                .limit(limit)
                .offset(offset)
                .fetchInto(Notification.class);

        return new Page<>(pageable, items);
    }

    public long count(Notification filter) {
        return dsl.selectCount()
                .from(NOTIFICATIONS)
                .where(getWhereCondition(filter))
                .fetchOne(0, long.class);
    }

    public Optional<Notification> markAsRead(Long id) {
        return dsl.update(NOTIFICATIONS)
                .set(NOTIFICATIONS.IS_READ, true)
                .where(NOTIFICATIONS.ID.eq(id))
                .returning()
                .fetchOptionalInto(Notification.class);
    }

    // Đánh dấu tất cả của user là đã đọc
    public int markAllAsRead(Long userId) {
        return dsl.update(NOTIFICATIONS)
                .set(NOTIFICATIONS.IS_READ, true)
                .where(NOTIFICATIONS.USER_ID.eq(userId).and(NOTIFICATIONS.IS_READ.eq(false)))
                .execute();
    }

    // Đếm số notification chưa đọc
    public long countUnread(Long userId) {
        return dsl.selectCount()
                .from(NOTIFICATIONS)
                .where(NOTIFICATIONS.USER_ID.eq(userId).and(NOTIFICATIONS.IS_READ.eq(false)))
                .fetchOne(0, long.class);
    }
}
