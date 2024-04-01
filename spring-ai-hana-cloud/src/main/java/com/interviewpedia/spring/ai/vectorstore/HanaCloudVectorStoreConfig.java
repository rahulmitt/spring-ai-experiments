package com.interviewpedia.spring.ai.vectorstore;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class HanaCloudVectorStoreConfig {
    private final String tableName;
    private final int topK;
}
