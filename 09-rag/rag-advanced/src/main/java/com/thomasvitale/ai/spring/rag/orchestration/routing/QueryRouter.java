package com.thomasvitale.ai.spring.rag.orchestration.routing;

import com.thomasvitale.ai.spring.rag.Query;
import com.thomasvitale.ai.spring.rag.retrieval.source.DocumentRetriever;

import java.util.List;
import java.util.function.Function;

/**
 * A component that routes a query to one or more document retrievers based on a specific strategy.
 */
public interface QueryRouter extends Function<Query, List<DocumentRetriever>> {

    List<DocumentRetriever> route(Query query);

    default List<DocumentRetriever> apply(Query query) {
        return route(query);
    }

}