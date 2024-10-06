package com.cuctut.book.dto.req;

import com.cuctut.common.req.PageReqDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 章节发布页 请求DTO
 *
 * @author cuctut
 * @since 2024/10/03
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class BookPageReqDto extends PageReqDto {

    /**
     * 作家ID
     */
    @Schema(description = "作家ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long authorId;


}
