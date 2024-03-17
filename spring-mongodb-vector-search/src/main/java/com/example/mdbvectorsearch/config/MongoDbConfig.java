package com.example.mdbvectorsearch.config;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import com.mongodb.reactivestreams.client.MongoDatabase;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MongoDbConfig {
    @Value("${mongodb.uri}")
    private String MONGODB_URI;

    @Value("${mongodb.database}")
    private String MONGODB_DATABASE;

    @Value("${mongodb.collection.name}")
    private String COLLECTION_NAME;

    @Value("${mongodb.collection.index-name}")
    private String INDEX_NAME;

    @Value("${mongodb.collection.search-field}")
    private String SEARCH_FIELD;

    @Bean
    public MongoClient mongoClient() {
        CodecRegistry pojoCodecRegistry = CodecRegistries.fromRegistries(
                MongoClientSettings.getDefaultCodecRegistry(),
                CodecRegistries.fromProviders(PojoCodecProvider.builder().automatic(true).build())
        );

        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(MONGODB_URI))
                .codecRegistry(pojoCodecRegistry)
                .build();

        return MongoClients.create(settings);
    }

    @Bean
    public MongoDatabase mongoDatabase(MongoClient mongoClient) {
        return mongoClient.getDatabase(MONGODB_DATABASE);
    }

    /*
    @Bean
    public Publisher<String> init(MongoDatabase mongoDatabase) {
        MongoCollection<Document> collection = mongoDatabase.getCollection(COLLECTION_NAME);

        Document searchIndex = new Document("mappings",
                new Document("dynamic", true).append("fields", new Document(SEARCH_FIELD, new Document("dimensions", 1536)
                        .append("similarity", "euclidean") // euclidean/cosine/dotProduct
                        .append("type", "knnVector")
                ))
        );

        Publisher<String> publisher = collection.createSearchIndex(INDEX_NAME, searchIndex);
        return publisher;
    }
    */
}
