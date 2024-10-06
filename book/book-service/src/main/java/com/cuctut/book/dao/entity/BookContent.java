package com.cuctut.book.dao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 小说内容
 * </p>
 *
 * @author cuctut
 * @since 2024/10/03
 */
@Data
@TableName("book_content")
public class BookContent implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 章节ID
     */
    private Long chapterId;

    /**
     * 小说章节内容
     */
    private String content;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

}
