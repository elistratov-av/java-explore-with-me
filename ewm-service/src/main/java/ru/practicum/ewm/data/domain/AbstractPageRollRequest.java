package ru.practicum.ewm.data.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.Serializable;

public abstract class AbstractPageRollRequest implements Pageable, Serializable {

    private final long startOffset;
    private final int pageSize;

    /**
     * Creates a new {@link AbstractPageRollRequest}.
     *
     * @param startOffset zero-based offset of first item, must not be negative.
     * @param pageSize the size of the page to be returned, must be greater than 0.
     */
    public AbstractPageRollRequest(long startOffset, int pageSize) {

        if (startOffset < 0) {
            throw new IllegalArgumentException("Start offset must not be less than zero");
        }

        if (pageSize < 1) {
            throw new IllegalArgumentException("Page size must not be less than one");
        }

        this.startOffset = startOffset;
        this.pageSize = pageSize;
    }

    @Override
    public int getPageSize() {
        return pageSize;
    }

    @Override
    public int getPageNumber() {
        return (int) (startOffset / pageSize);
    }

    @Override
    public long getOffset() {
        return startOffset;
    }

    @Override
    public boolean hasPrevious() {
        return startOffset > 0;
    }

    @Override
    public Pageable previousOrFirst() {
        return hasPrevious() ? previous() : first();
    }

    @Override
    public abstract Pageable next();

    /**
     * Returns the {@link Pageable} requesting the previous {@link Page}.
     *
     * @return
     */
    public abstract Pageable previous();

    @Override
    public abstract Pageable first();

    @Override
    public int hashCode() {

        final int prime = 31;
        int result = 1;

        result = (int) (prime * result + startOffset);
        result = prime * result + pageSize;

        return result;
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        }

        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        AbstractPageRollRequest other = (AbstractPageRollRequest) obj;
        return startOffset == other.startOffset && pageSize == other.pageSize;
    }
}
