package com.cuctut.book.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cuctut.book.dao.entity.BookChapter;
import com.cuctut.book.dao.entity.BookComment;
import com.cuctut.book.dao.entity.BookContent;
import com.cuctut.book.dao.entity.BookInfo;
import com.cuctut.book.dao.mapper.BookChapterMapper;
import com.cuctut.book.dao.mapper.BookCommentMapper;
import com.cuctut.book.dao.mapper.BookContentMapper;
import com.cuctut.book.dao.mapper.BookInfoMapper;
import com.cuctut.book.dto.req.*;
import com.cuctut.book.dto.resp.*;
import com.cuctut.book.manager.cache.*;
import com.cuctut.book.manager.feign.UserFeignManager;
import com.cuctut.book.service.BookService;
import com.cuctut.common.constant.DatabaseConsts;
import com.cuctut.common.constant.ErrorCodeEnum;
import com.cuctut.common.resp.PageRespDto;
import com.cuctut.common.resp.RestResp;
import com.cuctut.config.annotation.Key;
import com.cuctut.config.annotation.Lock;
import com.cuctut.config.exception.BusinessException;
import com.cuctut.user.dto.resp.UserInfoRespDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookServiceImpl implements BookService {

    private static final Integer REC_BOOK_COUNT = 4;

    private final BookInfoCacheManager bookInfoCacheManager;
    private final BookCategoryCacheManager bookCategoryCacheManager;
    private final BookChapterCacheManager bookChapterCacheManager;
    private final BookContentCacheManager bookContentCacheManager;
    private final BookRankCacheManager bookRankCacheManager;

    private final BookInfoMapper bookInfoMapper;
    private final BookChapterMapper bookChapterMapper;
    private final BookContentMapper bookContentMapper;
    private final BookCommentMapper bookCommentMapper;

    private final UserFeignManager userFeignManager;

    @Override
    public RestResp<List<BookCategoryRespDto>> listCategory(Integer workDirection) {
        return RestResp.ok(bookCategoryCacheManager.listCategory(workDirection));
    }

    @Override
    public RestResp<BookInfoRespDto> getBookById(Long bookId) {
        return RestResp.ok(bookInfoCacheManager.getBookInfoById(bookId));
    }

    @Override
    public RestResp<Void> addVisitCount(Long bookId) {
        return bookInfoMapper.addVisitCount(bookId)>0 ? RestResp.ok() : RestResp.fail(ErrorCodeEnum.USER_REQUEST_PARAM_ERROR);
    }

    @Override
    public RestResp<BookChapterAboutRespDto> getLastChapterAbout(Long bookId) {
        // 查询最新章节的内容
        BookInfoRespDto bookInfo = bookInfoCacheManager.getBookInfoById(bookId);
        BookChapterRespDto bookChapter = bookChapterCacheManager.getChapter(bookInfo.getLastChapterId());
        String content = bookContentCacheManager.getBookContent(bookInfo.getLastChapterId());
        // 查询章节总数
        LambdaQueryWrapper<BookChapter> query = new LambdaQueryWrapper<BookChapter>()
                .eq(BookChapter::getBookId, bookId);
        Long chapterTotal = bookChapterMapper.selectCount(query);
        // 组装数据并返回
        return RestResp.ok(BookChapterAboutRespDto.builder()
                .chapterInfo(bookChapter)
                .chapterTotal(chapterTotal)
                .contentSummary(content.substring(0, 30))
                .build());
    }

    @Override
    public RestResp<List<BookInfoRespDto>> listRecBooks(Long bookId) {
        // 获取该小说的分类，查询该分类下最新更新的 30 本小说
        Long categoryId = bookInfoCacheManager.getBookInfoById(bookId).getCategoryId();
        ArrayList<Long> lastUpdateIdList = new ArrayList<>(bookInfoCacheManager.getLastUpdateIdList(categoryId));
        // 打乱达到随机的目的，选取前 REC_BOOK_COUNT 本
        Collections.shuffle(lastUpdateIdList);
        List<Long> randomIdList = lastUpdateIdList.subList(0, REC_BOOK_COUNT);
        List<BookInfoRespDto> result = randomIdList.stream().map(bookInfoCacheManager::getBookInfoById).toList();
        return RestResp.ok(result);
    }

    @Override
    public RestResp<List<BookChapterRespDto>> listChapters(Long bookId) {
        LambdaQueryWrapper<BookChapter> query = new LambdaQueryWrapper<BookChapter>()
                .eq(BookChapter::getBookId, bookId)
                .orderByAsc(BookChapter::getChapterNum);
        List<BookChapter> bookChapters = bookChapterMapper.selectList(query);
        List<BookChapterRespDto> result = bookChapters.stream().map(
                v -> BookChapterRespDto.builder().id(v.getId()).isVip(v.getIsVip()).chapterNum(v.getChapterNum()).build()
        ).toList();
        return RestResp.ok(result);
    }

    @Override
    public RestResp<BookContentAboutRespDto> getBookContentAbout(Long chapterId) {
        // 查询章节、内容、小说信息
        BookChapterRespDto bookChapter = bookChapterCacheManager.getChapter(chapterId);
        String content = bookContentCacheManager.getBookContent(chapterId);
        BookInfoRespDto bookInfo = bookInfoCacheManager.getBookInfoById(bookChapter.getBookId());
        // 组装数据并返回
        return RestResp.ok(BookContentAboutRespDto.builder()
                .bookInfo(bookInfo)
                .chapterInfo(bookChapter)
                .bookContent(content)
                .build());
    }

    @Override
    public RestResp<Long> getPreChapterId(Long chapterId) {
        // 查询小说ID、章节号
        BookChapterRespDto chapter = bookChapterCacheManager.getChapter(chapterId);
        Long bookId = chapter.getBookId();
        Integer chapterNum = chapter.getChapterNum();
        // 查询上一章信息并返回章节ID
        LambdaQueryWrapper<BookChapter> query = new LambdaQueryWrapper<BookChapter>()
                .eq(BookChapter::getBookId, bookId)
                .lt(BookChapter::getChapterNum, chapterNum)
                .orderByDesc(BookChapter::getChapterNum)
                .last(DatabaseConsts.SqlEnum.LIMIT_1.getSql());
        return RestResp.ok(
                Optional.ofNullable(bookChapterMapper.selectOne(query))
                        .map(BookChapter::getId)
                        .orElse(null)
        );
    }

    @Override
    public RestResp<Long> getNextChapterId(Long chapterId) {
        // 查询小说ID、章节号
        BookChapterRespDto chapter = bookChapterCacheManager.getChapter(chapterId);
        Long bookId = chapter.getBookId();
        Integer chapterNum = chapter.getChapterNum();
        // 查询上一章信息并返回章节ID
        LambdaQueryWrapper<BookChapter> query = new LambdaQueryWrapper<BookChapter>()
                .eq(BookChapter::getBookId, bookId)
                .gt(BookChapter::getChapterNum, chapterNum)
                .orderByAsc(BookChapter::getChapterNum)
                .last(DatabaseConsts.SqlEnum.LIMIT_1.getSql());
        return RestResp.ok(
                Optional.ofNullable(bookChapterMapper.selectOne(query))
                        .map(BookChapter::getId)
                        .orElse(null)
        );
    }

    @Override
    public RestResp<List<BookRankRespDto>> listVisitRankBooks() {
        return RestResp.ok(bookRankCacheManager.listVisitRankBooks());
    }

    @Override
    public RestResp<List<BookRankRespDto>> listNewestRankBooks() {
        return RestResp.ok(bookRankCacheManager.listNewestRankBooks());
    }

    @Override
    public RestResp<List<BookRankRespDto>> listUpdateRankBooks() {
        return RestResp.ok(bookRankCacheManager.listUpdateRankBooks());
    }

    @Override
    public RestResp<BookCommentRespDto> listNewestComments(Long bookId) {
        // 查询评论总数
        LambdaQueryWrapper<BookComment> query = new LambdaQueryWrapper<BookComment>().eq(BookComment::getBookId, bookId);
        Long commentTotal = bookCommentMapper.selectCount(query);
        BookCommentRespDto bookCommentRespDto = BookCommentRespDto.builder().commentTotal(commentTotal).build();
        if (commentTotal > 0) {
            // 查询最新的评论列表
            query.orderByDesc(BookComment::getCreateTime).last(DatabaseConsts.SqlEnum.LIMIT_5.getSql());
            List<BookComment> bookComments = bookCommentMapper.selectList(query);
            // 查询评论用户信息，并设置需要返回的评论用户名
            List<Long> userIds = bookComments.stream().map(BookComment::getUserId).toList();
            List<UserInfoRespDto> userInfos = userFeignManager.listUserInfoByIds(userIds);
            Map<Long, UserInfoRespDto> userInfoMap = userInfos.stream()
                    .collect(Collectors.toMap(UserInfoRespDto::getId, Function.identity()));
            List<BookCommentRespDto.CommentInfo> commentInfos = bookComments.stream()
                    .map(v -> BookCommentRespDto.CommentInfo.builder()
                            .id(v.getId())
                            .commentUserId(v.getUserId())
                            .commentUser(userInfoMap.get(v.getUserId()).getUsername())
                            .commentUserPhoto(userInfoMap.get(v.getUserId()).getUserPhoto())
                            .commentContent(v.getCommentContent())
                            .commentTime(v.getCreateTime()).build()
                    ).toList();
            bookCommentRespDto.setComments(commentInfos);
        } else {
            bookCommentRespDto.setComments(Collections.emptyList());
        }
        return RestResp.ok(bookCommentRespDto);
    }

    // --- --- --- ---
    // 下面为内部调用方法
    // --- --- --- ---

    @Lock(prefix = "userComment")
    @Override
    public RestResp<Void> saveComment(
            @Key(expr = "#{userId + '::' + bookId}") BookCommentReqDto dto
    ) {
        // 校验用户是否已发表评论
        LambdaQueryWrapper<BookComment> query = new LambdaQueryWrapper<BookComment>()
                .eq(BookComment::getUserId, dto.getUserId())
                .eq(BookComment::getBookId, dto.getBookId());
        if (bookCommentMapper.selectCount(query) > 0)
            return RestResp.fail(ErrorCodeEnum.USER_COMMENTED);

        BookComment bookComment = BookComment.builder()
                .bookId(dto.getBookId())
                .userId(dto.getUserId())
                .commentContent(dto.getCommentContent())
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .build();
        bookCommentMapper.insert(bookComment);
        return RestResp.ok();
    }

    @Override
    public RestResp<Void> updateComment(BookCommentReqDto dto) {
        LambdaQueryWrapper<BookComment> query = new LambdaQueryWrapper<BookComment>()
                .eq(BookComment::getUserId, dto.getUserId())
                .eq(BookComment::getId, dto.getCommentId());
        BookComment bookComment = BookComment.builder().commentContent(dto.getCommentContent()).build();
        bookCommentMapper.update(bookComment, query);
        return RestResp.ok();
    }

    @Override
    public RestResp<Void> deleteComment(BookCommentReqDto dto) {
        LambdaQueryWrapper<BookComment> query = new LambdaQueryWrapper<BookComment>()
                .eq(BookComment::getUserId, dto.getUserId())
                .eq(BookComment::getId, dto.getCommentId());
        bookCommentMapper.delete(query);
        return RestResp.ok();
    }

    @Override
    public RestResp<Void> saveBook(BookAddReqDto dto) {
        // 校验小说名是否已存在
        LambdaQueryWrapper<BookInfo> queryWrapper = new LambdaQueryWrapper<BookInfo>()
                .eq(BookInfo::getBookName, dto.getBookName());
        if (bookInfoMapper.selectCount(queryWrapper) > 0)
            return RestResp.fail(ErrorCodeEnum.AUTHOR_BOOK_NAME_EXIST);

        BookInfo bookInfo = new BookInfo();
        bookInfo.setAuthorId(dto.getAuthorId());
        bookInfo.setAuthorName(dto.getPenName());
        bookInfo.setWorkDirection(dto.getWorkDirection());
        bookInfo.setCategoryId(dto.getCategoryId());
        bookInfo.setCategoryName(dto.getCategoryName());
        bookInfo.setBookName(dto.getBookName());
        bookInfo.setPicUrl(dto.getPicUrl());
        bookInfo.setBookDesc(dto.getBookDesc());
        bookInfo.setIsVip(dto.getIsVip());
        bookInfo.setScore(0);
        bookInfo.setCreateTime(LocalDateTime.now());
        bookInfo.setUpdateTime(LocalDateTime.now());
        bookInfoMapper.insert(bookInfo);

        return RestResp.ok();
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public RestResp<Void> saveBookChapter(ChapterAddReqDto dto) {
        // 涉及多表写入，引入事务
        // 1. 校验作品是否属于作者
        BookInfo bookInfo = bookInfoMapper.selectById(dto.getBookId());
        if (Objects.isNull(bookInfo))
            return RestResp.fail(ErrorCodeEnum.USER_REQUEST_PARAM_ERROR);
        if (!Objects.equals(bookInfo.getAuthorId(), dto.getAuthorId()))
            return RestResp.fail(ErrorCodeEnum.USER_UN_AUTH);
        // 2.保存章节信息
        int chapterNum = 0;
        LambdaQueryWrapper<BookChapter> query = new LambdaQueryWrapper<BookChapter>()
                .eq(BookChapter::getBookId, dto.getBookId())
                .orderByDesc(BookChapter::getChapterNum)
                .last(DatabaseConsts.SqlEnum.LIMIT_1.getSql());
        BookChapter lastChapter = bookChapterMapper.selectOne(query);
        if (Objects.nonNull(lastChapter)) chapterNum = lastChapter.getChapterNum() + 1;
        BookChapter newBookChapter = new BookChapter();
        newBookChapter.setBookId(dto.getBookId());
        newBookChapter.setChapterNum(chapterNum);
        newBookChapter.setChapterName(dto.getChapterName());
        newBookChapter.setIsVip(dto.getIsVip());
        newBookChapter.setWordCount(dto.getChapterContent().length());
        newBookChapter.setCreateTime(LocalDateTime.now());
        newBookChapter.setUpdateTime(LocalDateTime.now());
        bookChapterMapper.insert(newBookChapter);
        // 3.保存内容信息
        BookContent bookContent = new BookContent();
        bookContent.setContent(dto.getChapterContent());
        bookContent.setChapterId(newBookChapter.getId());
        bookContent.setCreateTime(LocalDateTime.now());
        bookContent.setUpdateTime(LocalDateTime.now());
        bookContentMapper.insert(bookContent);
        // 4.更新小说最新章节和小说总字数
        BookInfo bookInfoForUpt = new BookInfo();
        bookInfoForUpt.setId(dto.getBookId());
        bookInfoForUpt.setLastChapterId(newBookChapter.getId());
        bookInfoForUpt.setLastChapterName(newBookChapter.getChapterName());
        bookInfoForUpt.setLastChapterUpdateTime(newBookChapter.getUpdateTime());
        bookInfoForUpt.setUpdateTime(LocalDateTime.now());
        bookInfoForUpt.setWordCount(bookInfo.getWordCount() + newBookChapter.getWordCount());
        bookInfoMapper.updateById(bookInfoForUpt);
        // 5.清理缓存
        bookInfoCacheManager.evictBookInfoCache(dto.getBookId());
        // TODO 发送小说信息更新的 MQ 消息
        // amqpMsgManager.sendBookChangeMsg(dto.getBookId());
        return RestResp.ok();
    }

    @Override
    public RestResp<PageRespDto<BookInfoRespDto>> listAuthorBooks(BookPageReqDto dto) {
        IPage<BookInfo> bookInfoIPage = new Page<>();
        bookInfoIPage.setCurrent(dto.getPageNum());
        bookInfoIPage.setSize(dto.getPageSize());
        LambdaQueryWrapper<BookInfo> query = new LambdaQueryWrapper<BookInfo>()
                .eq(BookInfo::getAuthorId, dto.getAuthorId())
                .orderByDesc(BookInfo::getUpdateTime);
        bookInfoMapper.selectPage(bookInfoIPage, query);
        List<BookInfoRespDto> data = bookInfoIPage.getRecords().stream().map(
                v -> BookInfoRespDto.builder()
                        .id(v.getId())
                        .bookName(v.getBookName())
                        .picUrl(v.getPicUrl())
                        .categoryName(v.getCategoryName())
                        .wordCount(v.getWordCount())
                        .visitCount(v.getVisitCount())
                        .updateTime(v.getUpdateTime())
                        .build()
        ).toList();
        return RestResp.ok(
                PageRespDto.of(dto.getPageNum(), dto.getPageSize(), bookInfoIPage.getTotal(), data)
        );
    }

    @Override
    public RestResp<PageRespDto<BookChapterRespDto>> listBookChapters(ChapterPageReqDto dto) {
        IPage<BookChapter> bookChapterPage = new Page<>();
        bookChapterPage.setCurrent(dto.getPageNum());
        bookChapterPage.setSize(dto.getPageSize());
        LambdaQueryWrapper<BookChapter> query = new LambdaQueryWrapper<BookChapter>()
                .eq(BookChapter::getBookId, dto.getBookId())
                .orderByDesc(BookChapter::getChapterNum);
        bookChapterMapper.selectPage(bookChapterPage, query);
        List<BookChapterRespDto> data = bookChapterPage.getRecords().stream().map(
                v -> BookChapterRespDto.builder()
                        .id(v.getId())
                        .chapterName(v.getChapterName())
                        .chapterUpdateTime(v.getUpdateTime())
                        .isVip(v.getIsVip())
                        .build()
        ).toList();
        return RestResp.ok(
                PageRespDto.of(dto.getPageNum(), dto.getPageSize(), bookChapterPage.getTotal(), data)
        );
    }

    @Override
    public RestResp<List<BookInfoRespDto>> listBookInfoByIds(List<Long> bookIds) {
        LambdaQueryWrapper<BookInfo> query = new LambdaQueryWrapper<BookInfo>().in(BookInfo::getId, bookIds);
        return RestResp.ok(
                bookInfoMapper.selectList(query).stream().map(
                        v -> BookInfoRespDto.builder()
                                .id(v.getId())
                                .bookName(v.getBookName())
                                .authorName(v.getAuthorName())
                                .picUrl(v.getPicUrl())
                                .bookDesc(v.getBookDesc())
                                .build()
                ).toList()
        );
    }
}