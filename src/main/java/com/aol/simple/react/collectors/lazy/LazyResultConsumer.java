package com.aol.simple.react.collectors.lazy;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import com.aol.simple.react.config.MaxActive;
import com.aol.simple.react.stream.traits.ConfigurableStream;

/**
 * Interface that defines the rules for Collecting results from Infinite SimpleReact Streams
 * 
 * @author johnmcclean
 *
 * @param <T>
 */
public interface LazyResultConsumer<T> extends Consumer<CompletableFuture<T>>{

	/**
	 * Used to generate a new instance for result collection - populates the supplied Collection
	 * 
	 * @param t Collection to be populated
	 * @return Consumer that will populate the collection
	 */
	public LazyResultConsumer<T> withResults(Collection<CompletableFuture<T>> t);

	/**
	 * @return Completed results
	 */
	public Collection<CompletableFuture<T>> getResults();

	public MaxActive getMaxActive();

	public ConfigurableStream<T> getBlocking();
}
