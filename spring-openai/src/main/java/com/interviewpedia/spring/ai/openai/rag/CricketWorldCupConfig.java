package com.interviewpedia.spring.ai.openai.rag;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.springframework.ai.embedding.EmbeddingClient;
import org.springframework.ai.image.ImageClient;
import org.springframework.ai.openai.OpenAiImageClient;
import org.springframework.ai.openai.api.OpenAiImageApi;
import org.springframework.ai.vectorstore.HanaCloudVectorStore;
import org.springframework.ai.vectorstore.HanaCloudVectorStoreConfig;
import org.springframework.ai.vectorstore.MongoDBVectorStoreConfig;
import org.springframework.ai.vectorstore.MongoDbVectorStore;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.mongodb.core.MongoTemplate;

@Configuration
public class CricketWorldCupConfig {
    @Autowired
    private ResourceLoader resourceLoader;

    @Value("${spring.data.mongodb.uri}")
    String mongodbUri;

    @Value("${spring.data.mongodb.database}")
    String databaseName;

    @Value("${mongodb.collection.name}")
    String collectionName;

    @Value("${mongodb.collection.search-field}")
    String searchField;

    @Value("${mongodb.collection.index-name}")
    String vectorIndexName;

    @Value("${hana.table.name}")
    private String tableName;

    @Value("${hana.similarity.search.topK}")
    private int topK;

    @Bean
    public MongoClient mongoClient() {
        CodecRegistry pojoCodecRegistry = CodecRegistries.fromRegistries(
                MongoClientSettings.getDefaultCodecRegistry(),
                CodecRegistries.fromProviders(PojoCodecProvider.builder().automatic(true).build())
        );

        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(mongodbUri))
                .codecRegistry(pojoCodecRegistry)
                .build();

        return MongoClients.create(settings);
    }

    @Bean
    public MongoTemplate mongoTemplate(MongoClient mongoClient) {
        return new MongoTemplate(mongoClient, databaseName);
    }

    @Bean
    public VectorStore simpleVectorStore(EmbeddingClient embedding) {
        return new SimpleVectorStore(embedding);
    }

    @Bean
    public VectorStore mongoDbVectorStore(MongoTemplate mongoTemplate, EmbeddingClient embeddingClient) {
        return new MongoDbVectorStore(mongoTemplate, embeddingClient, MongoDBVectorStoreConfig.builder()
                .collectionName(collectionName)
                .pathName(searchField)
                .vectorIndexName(vectorIndexName)
                .numCandidates(200)
                .build());
    }

    @Bean
    public VectorStore hanaCloudVectorStore(CricketWorldCupRepository cricketWorldCupRepository,
                                            EmbeddingClient embeddingClient) {
        return new HanaCloudVectorStore(cricketWorldCupRepository, embeddingClient,
                HanaCloudVectorStoreConfig.builder()
                        .tableName(tableName)
                        .topK(topK)
                        .build());
    }

    @Bean
    public ImageClient imageClient(@Value("${spring.ai.openai.api-key}") String apiKey) {
        return new OpenAiImageClient(new OpenAiImageApi(apiKey));
    }
}



