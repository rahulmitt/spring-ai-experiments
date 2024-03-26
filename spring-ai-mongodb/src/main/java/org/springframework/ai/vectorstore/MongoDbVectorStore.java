package org.springframework.ai.vectorstore;

import com.mongodb.BasicDBObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingClient;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.*;

import static org.springframework.data.mongodb.core.query.Criteria.where;

@Slf4j
public class MongoDbVectorStore implements VectorStore, InitializingBean {

    public static final String ID_FIELD_NAME = "_id";

    public static final String SCORE_FIELD_NAME = "score";

    private final MongoTemplate mongoTemplate;

    private final EmbeddingClient embeddingClient;

    private final MongoDBVectorStoreConfig config;

    public MongoDbVectorStore(MongoTemplate mongoTemplate,
                              EmbeddingClient embeddingClient,
                              MongoDBVectorStoreConfig config) {

        this.mongoTemplate = mongoTemplate;
        this.embeddingClient = embeddingClient;
        this.config = config;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (!mongoTemplate.collectionExists(this.config.getCollectionName())) {
            mongoTemplate.createCollection(this.config.getCollectionName());
        }

        org.bson.Document searchIndex = createSearchIndex();
        // https://www.mongodb.com/community/forums/t/createsearchindexes-gives-mongocommandexception-in-java-code/272973
        // mongoTemplate.executeCommand(searchIndex);
    }

    private org.bson.Document createSearchIndex() {
        List<org.bson.Document> vectorFields = new ArrayList<>();
        vectorFields.add(new org.bson.Document().append("type", "knnVector")
                .append("path", this.config.getPathName())
                .append("dimensions", 1536)
                .append("similarity", "cosine"));

        return new org.bson.Document()
                .append("createSearchIndexes", this.config.getCollectionName())
                .append("indexes", List.of(new org.bson.Document()
                        .append("name", this.config.getVectorIndexName())
                        .append("type", "vectorSearch")
                        .append("definition", new org.bson.Document("fields", vectorFields))));
    }

    @SuppressWarnings("unchecked")
    private Document mapBasicDbObject(BasicDBObject basicDBObject) {
        String id = basicDBObject.getString(ID_FIELD_NAME);
        List<Double> embedding = (List<Double>) basicDBObject.get(this.config.getPathName());

        Document document = new Document(id, basicDBObject.toJson(), Collections.emptyMap());
        document.setEmbedding(embedding);

        return document;
    }

    @Override
    public void add(List<Document> documents) {
        for (Document document : documents) {
            log.info("Calling EmbeddingClient for document id = {}", document.getId());
            List<Double> embedding = this.embeddingClient.embed(document);
            document.setEmbedding(embedding);
            this.mongoTemplate.save(document, this.config.getCollectionName());
        }
    }

    @Override
    public Optional<Boolean> delete(List<String> idList) {
        Query query = new Query(where(ID_FIELD_NAME).in(idList));

        var deleteRes = this.mongoTemplate.remove(query, this.config.getCollectionName());
        long deleteCount = deleteRes.getDeletedCount();

        return Optional.of(deleteCount == idList.size());
    }

    @Override
    public List<Document> similaritySearch(String query) {
        return similaritySearch(SearchRequest.query(query).withTopK(1));
    }

    // https://www.mongodb.com/docs/atlas/atlas-vector-search/vector-search-stage/
    @Override
    public List<Document> similaritySearch(SearchRequest request) {
        List<Double> queryEmbedding = this.embeddingClient.embed(request.getQuery());

        AggregationOperation aggregationOperation = context -> {
            var vectorSearch = new org.bson.Document("queryVector", queryEmbedding)
                    .append("path", this.config.getPathName())
                    .append("numCandidates", this.config.getNumCandidates())
                    .append("index", this.config.getVectorIndexName())
                    .append("limit", request.getTopK());

            var doc = new org.bson.Document("$vectorSearch", vectorSearch);
            return context.getMappedObject(doc);
        };

        Aggregation aggregation = Aggregation.newAggregation(aggregationOperation,
                Aggregation.addFields()
                        .addField(SCORE_FIELD_NAME)
                        .withValueOfExpression("""
                                {
                                    "$meta": "vectorSearchScore"
                                }
                                """)
                        .build(),
                Aggregation.match(new Criteria(SCORE_FIELD_NAME).gte(request.getSimilarityThreshold())));

        return this.mongoTemplate.aggregate(aggregation, this.config.getCollectionName(), BasicDBObject.class)
                .getMappedResults()
                .stream()
                .map(this::mapBasicDbObject)
                .toList();
    }
}