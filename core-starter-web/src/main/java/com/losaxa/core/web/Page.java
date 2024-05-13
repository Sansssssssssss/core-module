package com.losaxa.core.web;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ApiModel(description = "分页")
@Data
public class Page {

    @ApiModelProperty("页大小")
    private int pageSize = 20;

    @ApiModelProperty("当前页数")
    private int pageNum = 1;

    public Pageable toPageable() {
        return PageRequest.of(pageNum - 1, pageSize);
    }

}
