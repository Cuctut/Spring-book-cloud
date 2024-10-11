package com.cuctut.search.task;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.BulkRequest;
import co.elastic.clients.elasticsearch.core.BulkResponse;
import co.elastic.clients.elasticsearch.core.bulk.BulkOperation;
import co.elastic.clients.elasticsearch.core.bulk.BulkResponseItem;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.annotation.XxlJob;
import com.cuctut.book.dto.resp.BookEsRespDto;
import com.cuctut.search.constant.EsConsts;
import com.cuctut.search.manager.feign.BookFeignManager;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 小说数据同步到 elasticsearch 任务
 *
 * @author cuctut
 * @since 2024/10/07
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class BookToEsTask {

    private final BookFeignManager bookFeignManager;

    private final ElasticsearchClient elasticsearchClient;

    /**
     * 周期的做一次全量数据同步
     */
    @SneakyThrows
    @XxlJob("saveToEsJobHandler")
    public ReturnT<String> saveToEs() {

        try {
            long maxId = 0;
            for (; ; ) {
                List<BookEsRespDto> books = bookFeignManager.listEsBooks(maxId);
                if (books.isEmpty()) break;
                maxId = books.getLast().getId();
                BulkRequest.Builder br = new BulkRequest.Builder();
                List<BulkOperation> bulkOperations = books.stream().map(
                        book -> BulkOperation.of(
                                op -> op.index(
                                        idx -> idx.index(EsConsts.BookIndex.INDEX_NAME)
                                                .id(book.getId().toString())
                                                .document(book)
                                )
                        )
                ).toList();
                br.operations(bulkOperations).timeout(t->t.time("10s"));

                BulkRequest request = br.build();
                BulkResponse response = elasticsearchClient.bulk(request);

                if (response.errors()) {
                    log.error("Bulk had errors");
                    for (BulkResponseItem item : response.items()) {
                        if (item.error() != null) {
                            log.error(item.error().reason());
                        }
                    }
                }
            }
            return ReturnT.SUCCESS;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ReturnT.FAIL;
        }
    }

}
