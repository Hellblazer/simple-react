package com.aol.simple.react.stream.traits;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

import com.aol.simple.react.exceptions.SimpleReactFailedStageException;
import com.aol.simple.react.stream.StreamWrapper;

class StreamUtils {
	
	static <R> SimpleReactStream<R> fromStreamCompletableFuture(ConfigurableStream cs,StreamWrapper lastActive,Stream<CompletableFuture<R>> stream) {
		
		
		return (SimpleReactStream<R>) cs.withLastActive(lastActive
				.withNewStream((Stream)stream));
	}
	
	static  <R> SimpleReactStream<R> fromStreamCompletableFutureReplace( ConfigurableStream cs,StreamWrapper lastActive,
			Stream<CompletableFuture<R>> stream) {
		Stream noType = stream;
		return (SimpleReactStream<R>) cs.withLastActive(lastActive
				.withStream(noType));
	}

	static <R> SimpleReactStream<R> fromListCompletableFuture(ConfigurableStream cs,StreamWrapper lastActive,
			List<CompletableFuture<R>> list) {
		List noType = list;
		return (SimpleReactStream<R>) cs.withLastActive(lastActive
				.withList(noType));
	}
	static SimpleReactFailedStageException assureSimpleReactException(
			Throwable throwable){
		if(throwable instanceof SimpleReactFailedStageException)
			return (SimpleReactFailedStageException)throwable;
		return new SimpleReactFailedStageException(null,(throwable));
	}

}
