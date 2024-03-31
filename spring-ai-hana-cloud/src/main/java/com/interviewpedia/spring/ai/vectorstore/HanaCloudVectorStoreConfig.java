package com.interviewpedia.spring.ai.vectorstore;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class HanaCloudVectorStoreConfig {
    private final String tableName;
    private final String vectorIndexName;
    private final String pathName;
    private final List<String> metadataFieldsToFilter;
    private final int numCandidates;
}
