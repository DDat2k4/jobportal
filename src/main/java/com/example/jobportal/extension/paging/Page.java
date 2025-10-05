package com.example.jobportal.extension.paging;

import lombok.Data;

import java.util.Collections;
import java.util.List;

@Data
public class Page<T> {
    private Long total;
    private Integer page;
    private List<T> items;
    private Boolean loadMoreAble;

    public Page() {}

    public Page(Pageable pageable, List<T> items) {
        this.total = pageable.getTotal() != null ? pageable.getTotal() : 0L;
        this.page = pageable.getPage() != null ? pageable.getPage() : 1;
        this.items = items != null ? items : Collections.emptyList();
        this.loadMoreAble = this.total > (pageable.getOffset() + pageable.getLimit());
    }

    public Page(Long total, Integer page, List<T> items) {
        this.total = total != null ? total : 0L;
        this.page = page != null ? page : 1;
        this.items = items != null ? items : Collections.emptyList();
    }

    public long getTotalSafe() {
        return total != null ? total : 0L;
    }
}
