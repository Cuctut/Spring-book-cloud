package com.cuctut.book.service;

import com.cuctut.book.dto.req.*;
import com.cuctut.book.dto.resp.*;
import com.cuctut.common.resp.PageRespDto;
import com.cuctut.common.resp.RestResp;

import java.util.List;

/**
 * Book模块服务类
 *
 * @author cuctut
 * @since 2024/10/03
 */
public interface BookService {

    /**
     * 小说分类列表查询
     *
     * @param workDirection 作品方向;0-男频 1-女频
     * @return 分类列表
     */
    RestResp<List<BookCategoryRespDto>> listCategory(Integer workDirection);

    /**
     * 小说信息查询
     *
     * @param bookId 小说ID
     * @return 小说信息
     */
    RestResp<BookInfoRespDto> getBookById(Long bookId);

    /**
     * 增加小说点击量
     *
     * @param bookId 小说ID
     * @return 成功状态
     */
    RestResp<Void> addVisitCount(Long bookId);

    /**
     * 小说最新章节相关信息查询
     *
     * @param bookId 小说ID
     * @return 章节相关联的信息
     */
    RestResp<BookChapterAboutRespDto> getLastChapterAbout(Long bookId);

    /**
     * 小说推荐列表查询
     *
     * @param bookId 小说ID
     * @return 小说信息列表
     */
    RestResp<List<BookInfoRespDto>> listRecBooks(Long bookId);

    /**
     * 小说章节列表查询
     *
     * @param bookId 小说ID
     * @return 小说章节列表
     */
    RestResp<List<BookChapterRespDto>> listChapters(Long bookId);

    /**
     * 小说内容相关信息查询
     *
     * @param chapterId 章节ID
     * @return 内容相关联的信息
     */
    RestResp<BookContentAboutRespDto> getBookContentAbout(Long chapterId);

    /**
     * 获取上一章节ID
     *
     * @param chapterId 章节ID
     * @return 上一章节ID
     */
    RestResp<Long> getPreChapterId(Long chapterId);

    /**
     * 获取下一章节ID
     *
     * @param chapterId 章节ID
     * @return 下一章节ID
     */
    RestResp<Long> getNextChapterId(Long chapterId);

    /**
     * 小说点击榜查询
     *
     * @return 小说点击排行列表
     */
    RestResp<List<BookRankRespDto>> listVisitRankBooks();

    /**
     * 小说新书榜查询
     *
     * @return 小说新书排行列表
     */
    RestResp<List<BookRankRespDto>> listNewestRankBooks();

    /**
     * 小说更新榜查询
     *
     * @return 小说更新排行列表
     */
    RestResp<List<BookRankRespDto>> listUpdateRankBooks();

    /**
     * 小说最新评论查询
     *
     * @param bookId 小说ID
     * @return 小说最新评论数据
     */
    RestResp<BookCommentRespDto> listNewestComments(Long bookId);


    // --- --- --- ---
    // 下面为内部调用方法
    // --- --- --- ---

    /**
     * 发表评论
     *
     * @param dto 评论相关 DTO
     * @return void
     */
    RestResp<Void> saveComment(BookCommentReqDto dto);

    /**
     * 修改评论
     *
     * @param dto 评论相关 DTO
     * @return void
     */
    RestResp<Void> updateComment(BookCommentReqDto dto);

    /**
     * 删除评论
     *
     * @param dto 评论相关 DTO
     * @return void
     */
    RestResp<Void> deleteComment(BookCommentReqDto dto);


    /**
     * 小说信息保存
     *
     * @param dto 小说信息
     * @return void
     */
    RestResp<Void> saveBook(BookAddReqDto dto);

    /**
     * 小说章节信息保存
     *
     * @param dto 章节信息
     * @return void
     */
    RestResp<Void> saveBookChapter(ChapterAddReqDto dto);

    /**
     * 查询作家发布小说列表
     *
     * @param dto 分页请求参数
     * @return 小说分页列表数据
     */
    RestResp<PageRespDto<BookInfoRespDto>> listAuthorBooks(BookPageReqDto dto);

    /**
     * 查询小说发布章节列表
     *
     * @param dto    分页请求参数
     * @return 章节分页列表数据
     */
    RestResp<PageRespDto<BookChapterRespDto>> listBookChapters(ChapterPageReqDto dto);

    /**
     * 批量查询小说信息
     *
     * @param bookIds 小说ID列表
     * @return 小说信息列表
     */
    RestResp<List<BookInfoRespDto>> listBookInfoByIds(List<Long> bookIds);
}