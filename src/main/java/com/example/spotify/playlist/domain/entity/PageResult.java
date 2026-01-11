package com.example.spotify.playlist.domain.entity;

import java.util.List;
import java.util.Objects;

public class PageResult<T> {
    private final List<T> items;
    private final Integer total;
    private final Integer limit;
    private final Integer offset;
    private final String next;
    private final String previous;

    public PageResult(List<T> items, Integer total, Integer limit, Integer offset, String next, String previous) {
        this.items = Objects.requireNonNull(items, "Items cannot be null");
        this.total = total;
        this.limit = limit;
        this.offset = offset;
        this.next = next;
        this.previous = previous;
    }

    public List<T> getItems() {
        return items;
    }

    public Integer getTotal() {
        return total;
    }

    public Integer getLimit() {
        return limit;
    }

    public Integer getOffset() {
        return offset;
    }

    public String getNext() {
        return next;
    }

    public String getPrevious() {
        return previous;
    }

    public boolean hasNext() {
        return next != null;
    }

    public boolean hasPrevious() {
        return previous != null;
    }
}
