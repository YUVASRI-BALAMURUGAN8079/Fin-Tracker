package com.tracker.error;

import graphql.GraphQLError;
import graphql.GraphqlErrorBuilder;
import graphql.schema.DataFetchingEnvironment;
import org.springframework.graphql.execution.DataFetcherExceptionResolver;
import org.springframework.graphql.execution.ErrorType;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class GraphQLExceptionHandler implements DataFetcherExceptionResolver {

    @Override
    public Mono<List<GraphQLError>> resolveException(Throwable ex, DataFetchingEnvironment env) {

        if (ex instanceof FinTrackerException finTrackerException) {
            GraphQLError error = GraphqlErrorBuilder.newError()
                    .message(finTrackerException.getMessage())
                    .errorType(ErrorType.BAD_REQUEST)
                    .path(env.getExecutionStepInfo().getPath())
                    .location(env.getField().getSourceLocation())
                    .build();

            return Mono.just(List.of(error));
        }

        return Mono.empty();
    }
}
