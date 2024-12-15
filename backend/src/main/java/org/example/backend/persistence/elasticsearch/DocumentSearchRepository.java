package org.example.backend.persistence.elasticsearch;

import org.springframework.stereotype.Repository;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.IndexRequest;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class DocumentSearchRepository {

    private final ElasticsearchClient elasticsearchClient;

    public DocumentSearchRepository(ElasticsearchClient elasticsearchClient) {
        this.elasticsearchClient = elasticsearchClient;
    }

    /**
     * Search documents by keyword with wildcard for partial matches.
     */
    public List<DocumentSearchEntity> searchByContent(String keyword) throws IOException {
        // Use wildcard for partial matches
        String wildcardKeyword = "*" + keyword.toLowerCase() + "*";

        SearchRequest searchRequest = SearchRequest.of(s -> s
                .index("documents") // Elasticsearch index name
                .query(q -> q
                        .wildcard(w -> w
                                .field("textContent") // Field in the Elasticsearch document
                                .value(wildcardKeyword) // Wildcard keyword
                                .caseInsensitive(true) // Case-insensitive search
                        )
                )
        );

        // Execute the search request
        SearchResponse<DocumentSearchEntity> searchResponse = elasticsearchClient.search(searchRequest, DocumentSearchEntity.class);

        // Extract the search results
        return searchResponse.hits().hits().stream()
                .map(Hit::source)
                .collect(Collectors.toList());
    }

    /**
     * Index a document into Elasticsearch.
     */
    public void indexDocument(DocumentSearchEntity documentEntity) throws IOException {
        IndexRequest<DocumentSearchEntity> indexRequest = IndexRequest.of(i -> i
                .index("documents") // Elasticsearch index name
                .id(String.valueOf(documentEntity.getId())) // Document ID
                .document(documentEntity) // Document content
        );

        // Execute the index request
        IndexResponse response = elasticsearchClient.index(indexRequest);
        if (response.result().name().equalsIgnoreCase("Created") || response.result().name().equalsIgnoreCase("Updated")) {
            System.out.println("Document indexed successfully with ID: " + response.id());
        } else {
            throw new IOException("Failed to index document with ID: " + response.id());
        }
    }
}
