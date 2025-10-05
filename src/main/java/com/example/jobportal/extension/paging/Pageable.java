package com.example.jobportal.extension.paging;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
@Accessors(chain = true)
public class Pageable {

    public static final Integer DEFAULT_LIMIT = 10;
    public static final Integer DEFAULT_PAGE = 1;
    public static final Integer MAX_LIMIT = 200;

    private List<Order> sort = new ArrayList<>();
    private Integer page = DEFAULT_PAGE;
    private Integer limit = DEFAULT_LIMIT;
    private Integer offset;
    private Long total;
    private Map<String, String> params;

    public Pageable() {}

    public Pageable(int page, int limit) {
        this.page = page > 0 ? page : DEFAULT_PAGE;
        this.limit = Math.min(limit, MAX_LIMIT);
    }

    public Integer getOffset() {
        if (offset != null && offset >= 0) return offset;
        return (page - 1) * getLimit();
    }

    public Integer getLimit() {
        if (limit == null || limit <= 0) return DEFAULT_LIMIT;
        return Math.min(limit, MAX_LIMIT);
    }

    public Pageable addOrder(String property, Order.Direction direction) {
        this.sort.add(new Order(property, direction));
        return this;
    }

    public Pageable setDefaultSort(String property) {
        if (this.sort.isEmpty()) {
            this.sort.add(new Order(property, Order.Direction.ASC));
        }
        return this;
    }

    public Order getFirstOrder() {
        return sort.isEmpty() ? new Order("id", Order.Direction.ASC) : sort.get(0);
    }
}
