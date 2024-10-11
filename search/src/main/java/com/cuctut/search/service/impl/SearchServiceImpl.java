package com.cuctut.search.service.impl;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.query_dsl.*;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.HighlightField;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.core.search.TotalHits;
import co.elastic.clients.json.JsonData;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.cuctut.book.dto.req.BookSearchReqDto;
import com.cuctut.book.dto.resp.BookEsRespDto;
import com.cuctut.book.dto.resp.BookInfoRespDto;
import com.cuctut.common.resp.PageRespDto;
import com.cuctut.common.resp.RestResp;
import com.cuctut.search.constant.EsConsts;
import com.cuctut.search.service.SearchService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Elasticsearch 搜索 服务实现类
 *
 * @author cuctut
 * @since 2024/10/07
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SearchServiceImpl implements SearchService {

    private final ElasticsearchClient esClient;

    @SneakyThrows
    @Override
    public RestResp<PageRespDto<BookInfoRespDto>> searchBooks(BookSearchReqDto condition) {


        SearchRequest searchRequest = buildSearchRequest(condition);
        SearchResponse<BookEsRespDto> response = esClient.search(searchRequest, BookEsRespDto.class);

        TotalHits total = response.hits().total();
        assert total != null;

        List<BookInfoRespDto> result = new ArrayList<>();
        List<Hit<BookEsRespDto>> hits = response.hits().hits();
        for (var hit : hits) {
            BookEsRespDto book = hit.source();
            assert book != null;
            if (!CollectionUtils.isEmpty(hit.highlight().get(EsConsts.BookIndex.FIELD_BOOK_NAME))) {
                book.setBookName(hit.highlight().get(EsConsts.BookIndex.FIELD_BOOK_NAME).getFirst());
            }
            if (!CollectionUtils.isEmpty(hit.highlight().get(EsConsts.BookIndex.FIELD_AUTHOR_NAME))) {
                book.setAuthorName(hit.highlight().get(EsConsts.BookIndex.FIELD_AUTHOR_NAME).getFirst());
            }
            result.add(BookInfoRespDto.builder()
                    .id(book.getId())
                    .bookName(book.getBookName())
                    .categoryId(book.getCategoryId())
                    .categoryName(book.getCategoryName())
                    .authorId(book.getAuthorId())
                    .authorName(book.getAuthorName())
                    .wordCount(book.getWordCount())
                    .lastChapterName(book.getLastChapterName())
                    .build());
        }

        return RestResp.ok(
            PageRespDto.of(condition.getPageNum(), condition.getPageSize(), total.value(), result)
        );

    }

    /**
     * 构建检索请求
     */
    private SearchRequest buildSearchRequest(BookSearchReqDto condition) {
        SearchRequest.Builder s = new SearchRequest.Builder();

        // 搜索给定的条件
        BoolQuery boolQuery = buildSearchCondition(condition);
        s.index(EsConsts.BookIndex.INDEX_NAME).query(q -> q.bool(boolQuery));

        // 按给定字段排序
        if (!StringUtils.isBlank(condition.getSort())) {
            s.sort(o -> o.field(f -> f.field(StringUtils.underlineToCamel(condition.getSort().split(" ")[0])).order(SortOrder.Desc)));
        }

        // 分页
        s.from((condition.getPageNum() - 1) * condition.getPageSize()).size(condition.getPageSize());

        // 设置高亮显示
        HighlightField highlightField = HighlightField.of(h -> h.preTags("<em style='color:red'>").postTags("</em>"));
        Map<String, HighlightField> fields = Map.of(
                EsConsts.BookIndex.FIELD_BOOK_NAME, highlightField,
                EsConsts.BookIndex.FIELD_AUTHOR_NAME, highlightField
        );
        s.highlight(h -> h.fields(fields));

        return s.build();
    }

    /**
     * 构建检索条件
     */
    private BoolQuery buildSearchCondition(BookSearchReqDto condition) {

        return BoolQuery.of(b -> {
            // 字数查询
            b.must(m -> m.range(r -> r.number(n -> n.field(EsConsts.BookIndex.FIELD_WORD_COUNT).gt(0.0))));
            if (Objects.nonNull(condition.getWordCountMin())) {
                b.must(m -> m.range(r -> r.number(n -> n.field(EsConsts.BookIndex.FIELD_WORD_COUNT).gte(condition.getWordCountMin().doubleValue()))));
            }
            if (Objects.nonNull(condition.getWordCountMax())) {
                b.must(m -> m.range(r -> r.number(n -> n.field(EsConsts.BookIndex.FIELD_WORD_COUNT).lte(condition.getWordCountMin().doubleValue()))));
            }

            // 关键词匹配
            if (!StringUtils.isBlank(condition.getKeyword())) {
                List<String> fields = Arrays.asList(
                        EsConsts.BookIndex.FIELD_BOOK_NAME + "^2",
                        EsConsts.BookIndex.FIELD_AUTHOR_NAME + "^1.8",
                        EsConsts.BookIndex.FIELD_BOOK_DESC + "^0.1"
                );
                b.must(m -> m.multiMatch(match -> match.fields(fields).query(condition.getKeyword())));
            }

            // 作品方向
            if (Objects.nonNull(condition.getWorkDirection())) {
                b.must(m -> m.term(t -> t.field(EsConsts.BookIndex.FIELD_WORK_DIRECTION).value(condition.getWorkDirection())));
            }

            // 分类
            if (Objects.nonNull(condition.getCategoryId())) {
                b.must(m -> m.term(t -> t.field(EsConsts.BookIndex.FIELD_CATEGORY_ID).value(condition.getCategoryId())));
            }

            // 更新时间
            if (Objects.nonNull(condition.getUpdateTimeMin())) {
                b.must(m -> m.range(r -> r.date(d -> d.field(EsConsts.BookIndex.FIELD_LAST_CHAPTER_UPDATE_TIME).gte(condition.getUpdateTimeMin().toString()))));
            }

            return b;
        });
    }

}
