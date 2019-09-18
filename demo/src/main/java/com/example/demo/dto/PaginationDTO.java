package com.example.demo.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class PaginationDTO {
    private List<QuestionDTO> questions;
    private boolean showPrevious;
    private boolean showFirstPage;
    private boolean showNext;
    private boolean showEndPage;
    private Integer page;
    private Integer totalPage;
    private List<Integer> pages = new ArrayList<>();

    public void setPagination(Integer totalCount, Integer size, Integer page) {
        Integer totalPage = totalCount % size == 0?totalCount/size :totalCount/size+1;
        if (page < 1)
            page = 1;

        if (page > totalPage)
            page = totalPage;

        this.page = page;
        this.totalPage = totalPage;
        pages.add(page);

        for (int i = 1; i <=3; i++){
            if(page > i)
                pages.add(0,page-i);

            if (page + i <= totalPage)
                pages.add(page+i);
        }
        showPrevious = page!=1;
        showNext = !page.equals(totalPage);
        showFirstPage = !pages.contains(1);
        showEndPage = !pages.contains(totalPage);
    }
}
