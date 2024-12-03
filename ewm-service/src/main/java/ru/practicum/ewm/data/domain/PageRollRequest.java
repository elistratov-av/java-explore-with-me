package ru.practicum.ewm.data.domain;

import org.springframework.data.domain.Sort;
import org.springframework.util.Assert;

public class PageRollRequest extends AbstractPageRollRequest {

    private final Sort sort;

    /**
     * Creates a new {@link PageRollRequest} with sort parameters applied.
     *
     * @param startOffset zero-based offset of first item, must not be negative.
     * @param pageSize the size of the page to be returned, must be greater than 0.
     * @param sort must not be {@literal null}, use {@link Sort#unsorted()} instead.
     */
    protected PageRollRequest(long startOffset, int pageSize, Sort sort) {

        super(startOffset, pageSize);

        Assert.notNull(sort, "Sort must not be null");

        this.sort = sort;
    }

    /**
     * Creates a new unsorted {@link PageRollRequest}.
     *
     * @param startOffset zero-based offset of first item, must not be negative.
     * @param pageSize    the size of the page to be returned, must be greater than 0.
     */
    public static PageRollRequest of(long startOffset, int pageSize) {
        return of(startOffset, pageSize, Sort.unsorted());
    }

    /**
     * Creates a new {@link PageRollRequest} with sort parameters applied.
     *
     * @param startOffset zero-based offset of first item, must not be negative.
     * @param pageSize the size of the page to be returned, must be greater than 0.
     * @param sort must not be {@literal null}, use {@link Sort#unsorted()} instead.
     */
    public static PageRollRequest of(long startOffset, int pageSize, Sort sort) {
        return new PageRollRequest(startOffset, pageSize, sort);
    }

    /**
     * Creates a new {@link PageRollRequest} with sort direction and properties applied.
     *
     * @param startOffset zero-based offset of first item, must not be negative.
     * @param pageSize the size of the page to be returned, must be greater than 0.
     * @param direction must not be {@literal null}.
     * @param properties must not be {@literal null}.
     */
    public static PageRollRequest of(long startOffset, int pageSize, Sort.Direction direction, String... properties) {
        return of(startOffset, pageSize, Sort.by(direction, properties));
    }

    /**
     * Creates a new {@link PageRollRequest} for the first page (page number {@code 0}) given {@code pageSize} .
     *
     * @param pageSize the size of the page to be returned, must be greater than 0.
     * @return a new {@link PageRollRequest}.
     */
    public static PageRollRequest ofSize(int pageSize) {
        return PageRollRequest.of(0, pageSize);
    }

    public Sort getSort() {
        return sort;
    }

    @Override
    public PageRollRequest next() {
        return new PageRollRequest(getOffset() + getPageSize(), getPageSize(), getSort());
    }

    @Override
    public PageRollRequest previous() {
        return getOffset() == 0 ? this : new PageRollRequest(
                getOffset() > getPageSize() ? getOffset() - getPageSize() : 0,
                getPageSize(),
                getSort());
    }

    @Override
    public PageRollRequest first() {
        return new PageRollRequest(0, getPageSize(), getSort());
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        }

        if (!(obj instanceof PageRollRequest that)) {
            return false;
        }

        return super.equals(that) && sort.equals(that.sort);
    }

    /**
     * Creates a new {@link PageRollRequest} with {@code pageNumber} applied.
     *
     * @param pageNumber
     * @return a new {@link PageRollRequest}.
     */
    @Override
    public PageRollRequest withPage(int pageNumber) {
        return new PageRollRequest((long) pageNumber * getPageSize(), getPageSize(), getSort());
    }

    /**
     * Creates a new {@link PageRollRequest} with {@link Sort.Direction} and {@code properties} applied.
     *
     * @param direction must not be {@literal null}.
     * @param properties must not be {@literal null}.
     * @return a new {@link PageRollRequest}.
     */
    public PageRollRequest withSort(Sort.Direction direction, String... properties) {
        return new PageRollRequest(getOffset(), getPageSize(), Sort.by(direction, properties));
    }

    /**
     * Creates a new {@link PageRollRequest} with {@link Sort} applied.
     *
     * @param sort must not be {@literal null}.
     * @return a new {@link PageRollRequest}.
     */
    public PageRollRequest withSort(Sort sort) {
        return new PageRollRequest(getOffset(), getPageSize(), sort);
    }

    @Override
    public int hashCode() {
        return 31 * super.hashCode() + sort.hashCode();
    }

    @Override
    public String toString() {
        return String.format("Page request [offset: %d, size %d, sort: %s]", getOffset(), getPageSize(), sort);
    }
}
