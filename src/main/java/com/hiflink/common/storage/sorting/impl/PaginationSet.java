package com.hiflink.common.storage.sorting.impl;

import com.hiflink.common.storage.sorting.IPaginationSet;
import lombok.Getter;


public class PaginationSet implements IPaginationSet {
    @Getter
    private String token;
    @Getter
    private Integer currentPage;
    @Getter
    private Integer recordsCount;
    @Getter
    private Integer paginSize = 10;
    @Getter
    private Integer paginCount;

    public PaginationSet() {
    }

    public PaginationSet(Integer paginSize) {
        this.paginSize = paginSize;
    }

    @Override
    public void nextPage() {
        if (this.getCurrentPage() < paginCount - 1) {
            this.currentPage++;
        }
    }

    @Override
    public void previousPage() {
        if (this.getCurrentPage() > 0) {
            this.currentPage--;
        }
    }

    @Override
    public void firstPage() {
        this.currentPage = 0;
    }

    @Override
    public void lastPage() {
        this.currentPage = paginCount - 1;
    }
}
