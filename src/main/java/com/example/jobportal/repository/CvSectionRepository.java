package com.example.jobportal.repository;

import com.example.jobportal.data.entity.CvSection;
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

import static com.example.generated.jooq.tables.CvSections.CV_SECTIONS;
import static org.jooq.impl.DSL.noCondition;

@Repository
public class CvSectionRepository {

    private final DSLContext dsl;

    public CvSectionRepository(DSLContext dsl) {
        this.dsl = dsl;
    }

    private static List<Field<?>> getFields() {
        return Arrays.asList(
                CV_SECTIONS.ID,
                CV_SECTIONS.CV_ID,
                CV_SECTIONS.TYPE,
                CV_SECTIONS.TITLE,
                CV_SECTIONS.CONTENT,
                CV_SECTIONS.POSITION,
                CV_SECTIONS.CREATED_AT
        );
    }

    public Condition getWhereCondition(CvSection filter) {
        Condition condition = noCondition();

        if (filter.getId() != null) {
            condition = condition.and(CV_SECTIONS.ID.eq(filter.getId()));
        }
        if (filter.getCvId() != null) {
            condition = condition.and(CV_SECTIONS.CV_ID.eq(filter.getCvId()));
        }
        if (filter.getType() != null) {
            condition = condition.and(CV_SECTIONS.TYPE.eq(filter.getType()));
        }
        if (filter.getTitle() != null) {
            condition = condition.and(CV_SECTIONS.TITLE.likeIgnoreCase("%" + filter.getTitle() + "%"));
        }
        if (filter.getPosition() != null) {
            condition = condition.and(CV_SECTIONS.POSITION.eq(filter.getPosition()));
        }

        return condition;
    }

    public Optional<CvSection> findById(Long id) {
        return dsl.select(getFields())
                .from(CV_SECTIONS)
                .where(CV_SECTIONS.ID.eq(id))
                .fetchOptionalInto(CvSection.class);
    }

    public Optional<CvSection> findByFilter(CvSection filter) {
        return dsl.select(getFields())
                .from(CV_SECTIONS)
                .where(getWhereCondition(filter))
                .fetchOptionalInto(CvSection.class);
    }

    public CvSection create(CvSection section) {
        return dsl.insertInto(CV_SECTIONS)
                .set(CV_SECTIONS.CV_ID, section.getCvId())
                .set(CV_SECTIONS.TYPE, section.getType())
                .set(CV_SECTIONS.TITLE, section.getTitle())
                .set(CV_SECTIONS.CONTENT, section.getContent())
                .set(CV_SECTIONS.POSITION, section.getPosition())
                .set(CV_SECTIONS.CREATED_AT, LocalDateTime.now())
                .returning()
                .fetchOneInto(CvSection.class);
    }

    public Optional<CvSection> update(Long id, CvSection section) {
        return dsl.update(CV_SECTIONS)
                .set(CV_SECTIONS.TITLE, section.getTitle())
                .set(CV_SECTIONS.CONTENT, section.getContent())
                .set(CV_SECTIONS.POSITION, section.getPosition())
                .where(CV_SECTIONS.ID.eq(id))
                .returning()
                .fetchOptionalInto(CvSection.class);
    }

    public int delete(Long id) {
        return dsl.deleteFrom(CV_SECTIONS)
                .where(CV_SECTIONS.ID.eq(id))
                .execute();
    }

    public Page<CvSection> findAll(CvSection filter, Pageable pageable) {
        int offset = pageable.getOffset();
        int limit = pageable.getLimit();
        long total = count(filter);
        pageable.setTotal(total);

        Order order = pageable.getFirstOrder();
        String sortField = order.getPropertyOrDefault("POSITION");
        boolean isAsc = order.isAsc();

        List<CvSection> items = dsl.select(getFields())
                .from(CV_SECTIONS)
                .where(getWhereCondition(filter))
                .orderBy(isAsc ? CV_SECTIONS.field(sortField).asc() : CV_SECTIONS.field(sortField).desc())
                .limit(limit)
                .offset(offset)
                .fetchInto(CvSection.class);

        return new Page<>(pageable, items);
    }

    public long count(CvSection filter) {
        return dsl.selectCount()
                .from(CV_SECTIONS)
                .where(getWhereCondition(filter))
                .fetchOne(0, long.class);
    }

    /**
     * Lấy danh sách tất cả section theo CV ID, sắp xếp theo thứ tự hiển thị
     */
    public List<CvSection> findByCvId(Long cvId) {
        return dsl.select(getFields())
                .from(CV_SECTIONS)
                .where(CV_SECTIONS.CV_ID.eq(cvId))
                .orderBy(CV_SECTIONS.POSITION.asc())
                .fetchInto(CvSection.class);
    }

    /**
     * Xóa toàn bộ section theo CV ID (khi xóa CV hoặc reset)
     */
    public int deleteByCvId(Long cvId) {
        return dsl.deleteFrom(CV_SECTIONS)
                .where(CV_SECTIONS.CV_ID.eq(cvId))
                .execute();
    }
}
