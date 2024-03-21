package org.springframework.ai.vectorstore;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class MongoDBVectorStoreConfig {
    private final String collectionName;
    private final String vectorIndexName;
    private final String pathName;
    private final List<String> metadataFieldsToFilter;
    private final int numCandidates;
}
