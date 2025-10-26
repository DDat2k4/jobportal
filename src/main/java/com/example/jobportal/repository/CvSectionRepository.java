package com.example.jobportal.repository;

import com.example.jobportal.data.entity.CvSection;
import com.example.jobportal.extension.paging.Order;
import com.example.jobportal.extension.paging.Page;
import com.example.jobportal.extension.paging.Pageable;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.JSONB;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.*;

import static com.example.generated.jooq.tables.CvSections.CV_SECTIONS;
import static org.jooq.impl.DSL.noCondition;

@Repository
@RequiredArgsConstructor
public class CvSectionRepository {

    private final DSLContext dsl;
    private final ObjectMapper objectMapper = new ObjectMapper();

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

    private Condition getWhereCondition(CvSection filter) {
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

    private JSONB toJsonb(Object content) {
        if (content == null) return null;
        try {
            return JSONB.valueOf(objectMapper.writeValueAsString(content));
        } catch (Exception e) {
            throw new RuntimeException("Failed to convert content to JSONB", e);
        }
    }

    private Map<String, Object> fromJsonb(JSONB jsonb) {
        if (jsonb == null) return null;
        try {
            return objectMapper.readValue(jsonb.data(), new TypeReference<Map<String, Object>>() {});
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse JSONB content", e);
        }
    }

    private CvSection mapRecordToEntity(com.example.generated.jooq.tables.records.CvSectionsRecord record) {
        if (record == null) return null;
        return new CvSection()
                .setId(record.getId())
                .setCvId(record.getCvId())
                .setType(record.getType())
                .setTitle(record.getTitle())
                .setContent(fromJsonb(record.getContent()))
                .setPosition(record.getPosition())
                .setCreatedAt(record.getCreatedAt());
    }

    public Optional<CvSection> findById(Long id) {
        return dsl.selectFrom(CV_SECTIONS)
                .where(CV_SECTIONS.ID.eq(id))
                .fetchOptional(this::mapRecordToEntity);
    }

    public Optional<CvSection> findByFilter(CvSection filter) {
        return dsl.selectFrom(CV_SECTIONS)
                .where(getWhereCondition(filter))
                .fetchOptional(this::mapRecordToEntity);
    }

    public CvSection create(CvSection section) {
        var record = dsl.insertInto(CV_SECTIONS)
                .set(CV_SECTIONS.CV_ID, section.getCvId())
                .set(CV_SECTIONS.TYPE, section.getType())
                .set(CV_SECTIONS.TITLE, section.getTitle())
                .set(CV_SECTIONS.CONTENT, toJsonb(section.getContent()))
                .set(CV_SECTIONS.POSITION, section.getPosition())
                .set(CV_SECTIONS.CREATED_AT, LocalDateTime.now())
                .returning()
                .fetchOne();

        return mapRecordToEntity(record);
    }

    public Optional<CvSection> update(Long id, CvSection section) {
        var record = dsl.update(CV_SECTIONS)
                .set(CV_SECTIONS.TITLE, section.getTitle())
                .set(CV_SECTIONS.CONTENT, toJsonb(section.getContent()))
                .set(CV_SECTIONS.POSITION, section.getPosition())
                .where(CV_SECTIONS.ID.eq(id))
                .returning()
                .fetchOne();

        return Optional.ofNullable(mapRecordToEntity(record));
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
        String sortField = order.getPropertyOrDefault("position");
        boolean isAsc = order.isAsc();

        List<CvSection> items = dsl.selectFrom(CV_SECTIONS)
                .where(getWhereCondition(filter))
                .orderBy(isAsc
                        ? CV_SECTIONS.field(sortField).asc()
                        : CV_SECTIONS.field(sortField).desc())
                .limit(limit)
                .offset(offset)
                .fetch(this::mapRecordToEntity);

        return new Page<>(pageable, items);
    }

    public long count(CvSection filter) {
        return dsl.selectCount()
                .from(CV_SECTIONS)
                .where(getWhereCondition(filter))
                .fetchOne(0, long.class);
    }

    public List<CvSection> findByCvId(Long cvId) {
        return dsl.selectFrom(CV_SECTIONS)
                .where(CV_SECTIONS.CV_ID.eq(cvId))
                .orderBy(CV_SECTIONS.POSITION.asc())
                .fetch(this::mapRecordToEntity);
    }

    public int deleteByCvId(Long cvId) {
        return dsl.deleteFrom(CV_SECTIONS)
                .where(CV_SECTIONS.CV_ID.eq(cvId))
                .execute();
    }
}
