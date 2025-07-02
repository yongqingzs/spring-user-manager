package com.userdept.system.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * 分页数据对象
 */
@Data
@AllArgsConstructor
public class PageVO<T> {
    /**
     * 总记录数
     */
    private Long total;
    
    /**
     * 当前页码
     */
    private Integer page;
    
    /**
     * 每页记录数
     */
    private Integer perPage;
    
    /**
     * 数据列表
     */
    private List<T> list;
}
