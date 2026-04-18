package com.hnkjzy.ecg_collection.model.vo.common;

import com.hnkjzy.ecg_collection.model.vo.BaseVo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 通用分页返回结构。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class PageResultVo<T> extends BaseVo {

    private Long pageNum;
    private Long pageSize;
    private Long total;
    private Long pages;
    private List<T> records;
}
