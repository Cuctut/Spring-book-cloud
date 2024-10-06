package com.cuctut.common.resp;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

/**
 * 分页响应数据格式封装
 *
 * @author cuctut
 * @since 2024/10/01
 */
@Getter
@AllArgsConstructor
public class PageRespDto<T> {

    /**
     * 页码
     */
    private final long pageNum;

    /**
     * 每页大小
     */
    private final long pageSize;

    /**
     * 总记录数
     */
    private final long total;

    /**
     * 分页数据集
     */
    private final List<? extends T> list;


    public static <T> PageRespDto<T> of(long pageNum, long pageSize, long total, List<T> list) {
        return new PageRespDto<>(pageNum, pageSize, total, list);
    }

    /**
     * 获取分页数
     */
    public long getPages() {
        return (total == 0L || pageSize == 0L) ? 0L : (total + pageSize - 1) / pageSize;
    }
}
