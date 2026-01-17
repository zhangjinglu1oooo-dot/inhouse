package com.inhouse.iam;

import java.util.List;

/**
 * 通用分页响应体。
 */
public class PageResponse<T> {
    private final List<T> items;
    private final int page;
    private final int size;
    private final long total;

    public PageResponse(List<T> items, int page, int size, long total) {
        this.items = items;
        this.page = page;
        this.size = size;
        this.total = total;
    }

    public List<T> getItems() {
        return items;
    }

    public int getPage() {
        return page;
    }

    public int getSize() {
        return size;
    }

    public long getTotal() {
        return total;
    }
}
