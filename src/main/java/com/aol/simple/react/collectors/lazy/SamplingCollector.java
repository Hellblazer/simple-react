package com.aol.simple.react.collectors.lazy;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

import lombok.AllArgsConstructor;
import lombok.experimental.Builder;
import lombok.experimental.Wither;

import com.aol.simple.react.config.MaxActive;
import com.aol.simple.react.stream.traits.ConfigurableStream;

/**
 * 
 * Class that allows client code to only collect a sample of results from an Infinite SimpleReact Stream
 * 
 * The SamplingCollector won't collect results itself, but hand of control to a consumer that can when Sampling triggered.
 * @author johnmcclean
 *
 * @param <T> Result type
 */
@AllArgsConstructor
@Wither
@Builder
public class SamplingCollector<T> implements LazyResultConsumer<T>{

	private final int sampleRate;
	private long count = 0;
	private final LazyResultConsumer<T> consumer;

	
	
	/**
	 * 
	 * @param sampleRate Modulus of sampleRate will determine result collection
	 * @param consumer  SamplingCollector won't actually collect results, it passes control to another consumer when triggered.
	 */
	public SamplingCollector(int sampleRate, LazyResultConsumer<T> consumer) {
		this.sampleRate = sampleRate;
		this.consumer = consumer;
	}
	
	/* (non-Javadoc)
	 * @see java.util.function.Consumer#accept(java.lang.Object)
	 */
	@Override
	public void accept(CompletableFuture<T> t) {
		if(count++%sampleRate ==0)
			consumer.accept(t);
		
	}

	/* (non-Javadoc)
	 * @see com.aol.simple.react.collectors.lazy.LazyResultConsumer#withResults(java.util.Collection)
	 */
	@Override
	public LazyResultConsumer<T> withResults(Collection<CompletableFuture<T>> t) {
		return this.withConsumer(consumer.withResults(t));
	}

	/* (non-Javadoc)
	 * @see com.aol.simple.react.collectors.lazy.LazyResultConsumer#getResults()
	 */
	@Override
	public Collection<CompletableFuture<T>> getResults() {
		return consumer.getResults();
	}

	@Override
	public MaxActive getMaxActive() {
		return consumer.getMaxActive();
	}

	@Override
	public ConfigurableStream<T> getBlocking() {
		return consumer.getBlocking();
	}

	

	


	
}
