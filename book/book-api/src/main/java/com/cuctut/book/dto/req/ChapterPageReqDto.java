package com.cuctut.book.dto.req;

import com.cuctut.common.req.PageReqDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
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
public class ChapterPageReqDto extends PageReqDto {

    /**
     * 小说ID
     */
    @NotBlank
    @Schema(description = "小说ID", required = true)
    private Long bookId;


}
