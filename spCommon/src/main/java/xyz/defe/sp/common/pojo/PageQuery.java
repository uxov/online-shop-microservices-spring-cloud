package xyz.defe.sp.common.pojo;

import org.springframework.data.domain.PageRequest;

import java.io.Serializable;

public class PageQuery implements Serializable {
    private Integer pageNum = 1;
    private Integer pageSize = 10;

    public Integer getPageNum() {
        return pageNum;
    }

    public void setPageNum(Integer pageNum) {
        this.pageNum = pageNum;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public PageRequest getPageRequest() {
        return PageRequest.of(pageNum - 1, pageSize);
    }

}
