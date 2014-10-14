package com.digout.utils;

import com.digout.artifact.Pagination;
import org.springframework.data.domain.Page;

public final class PagingUtils {

    public static int[] getOffsetAndLimit(final Integer pageNum, final Integer pageSize) {
        final int offset = pageNum == null ? 0 : (pageSize == null && pageNum > 0) ? 0 : pageNum - 1;
        final int limit = pageSize == null ? 50 : pageSize;
        return new int[] { offset, limit };
    }

    public static int limit(final Integer pageSize, final Integer defaultSize) {
        return pageSize == null ? defaultSize : pageSize;
    }

    public static int offset(final Integer pageNum, final Integer pageSize) {
        return pageNum == null ? 0 : (pageSize == null && pageNum > 0) ? 0 : pageNum - 1;
    }

    public static Pagination pageOf(final Page<?> page) {
        Pagination pagination = new Pagination();
        pagination.setPageNum(page.getNumber() + 1);
        pagination.setTotalSize((int) page.getTotalElements());
        pagination.setTotalPages(page.getTotalPages());
        return pagination;
    }

    private PagingUtils() {
    }
}
