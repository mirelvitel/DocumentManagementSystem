package org.example.backend.persistence.elasticsearch;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.DeleteRequest;
import co.elastic.clients.elasticsearch.core.IndexRequest;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class DocumentSearchRepository {

    private static final Logger logger = LoggerFactory.getLogger(DocumentSearchRepository.class);
    private static final String INDEX_NAME = "documents";

    private final ElasticsearchClient elasticsearchClient;

    public DocumentSearchRepository(ElasticsearchClient elasticsearchClient) {
        this.elasticsearchClient = elasticsearchClient;
    }

    public List<DocumentSearchEntity> searchByContent(String keyword) throws IOException {
        String wildcardKeyword = "*" + keyword.toLowerCase() + "*";

        SearchRequest searchRequest = SearchRequest.of(s -> s
                .index(INDEX_NAME)
                .query(q -> q
                        .wildcard(w -> w
                                .field("textContent")
                                .value(wildcardKeyword)
                                .caseInsensitive(true)
                        )
                )
        );

        SearchResponse<DocumentSearchEntity> searchResponse =
                elasticsearchClient.search(searchRequest, DocumentSearchEntity.class);

        return searchResponse.hits().hits().stream()
                .map(Hit::source)
                .collect(Collectors.toList());
    }

    public void indexDocument(DocumentSearchEntity documentEntity) throws IOException {
        IndexRequest<DocumentSearchEntity> indexRequest = IndexRequest.of(i -> i
                .index(INDEX_NAME)
                .id(String.valueOf(documentEntity.getId()))
                .document(documentEntity)
        );

        IndexResponse response = elasticsearchClient.index(indexRequest);
        logger.info("Document indexed with ID: {}, result: {}", response.id(), response.result().name());
    }

    public void deleteDocument(String id) throws IOException {
        DeleteRequest deleteRequest = DeleteRequest.of(d -> d
                .index(INDEX_NAME)
                .id(id)
        );
        elasticsearchClient.delete(deleteRequest);
        logger.info("Document deleted from index with ID: {}", id);
    }
}