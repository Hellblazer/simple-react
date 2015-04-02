package com.aol.simple.react.stream.eager;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;
import java.util.function.Consumer;
import java.util.stream.Collector;
import java.util.stream.Stream;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Builder;
import lombok.experimental.Wither;
import lombok.extern.slf4j.Slf4j;

import com.aol.simple.react.RetryBuilder;
import com.aol.simple.react.async.AlwaysContinue;
import com.aol.simple.react.async.Continueable;
import com.aol.simple.react.async.QueueFactories;
import com.aol.simple.react.async.QueueFactory;
import com.aol.simple.react.capacity.monitor.LimitingMonitor;
import com.aol.simple.react.collectors.lazy.BatchingCollector;
import com.aol.simple.react.collectors.lazy.LazyResultConsumer;
import com.aol.simple.react.stream.BaseSimpleReact;
import com.aol.simple.react.stream.StreamWrapper;
import com.aol.simple.react.stream.traits.EagerFutureStream;
import com.aol.simple.react.stream.traits.LazyFutureStream;
import com.nurkiewicz.asyncretry.RetryExecutor;


@Wither
@Builder
@Getter
@Slf4j
@AllArgsConstructor
public class EagerFutureStreamImpl<U> implements EagerFutureStream<U>{
	


	private final Optional<Consumer<Throwable>> errorHandler;
	private final StreamWrapper lastActive;
	private final boolean eager;
	private final Consumer<CompletableFuture> waitStrategy;
	private final LazyResultConsumer<U> lazyCollector;
	private final QueueFactory<U> queueFactory;
	private final EagerReact simpleReact;
	private final Continueable subscription;
	private final List<CompletableFuture> originalFutures;


	/**
	 * 
	 * Construct a SimpleReact stage - this acts as a fluent SimpleReact builder
	 * 
	 * @param stream
	 *            Stream that will generate the events that will be reacted to.
	 * @param executor
	 *            The next stage's tasks will be submitted to this executor
	 */
	public EagerFutureStreamImpl(EagerReact eagerReact,final Stream<CompletableFuture<U>> stream) {
		this(eagerReact, stream,null);
	}
	public EagerFutureStreamImpl(EagerReact eagerReact, final Stream<CompletableFuture<U>> stream,List<CompletableFuture> org) {
		this.simpleReact =eagerReact;

		Stream s = stream;
		this.lastActive = new StreamWrapper(s, true);
		this.originalFutures = org!=null ? org : this.lastActive.list();
		this.errorHandler = Optional.of((e) -> log.error(e.getMessage(), e));
		this.eager = true;

		this.waitStrategy = new LimitingMonitor();
		this.lazyCollector = new BatchingCollector<>(this);
		this.queueFactory = QueueFactories.unboundedQueue();
		subscription = new AlwaysContinue();

		
	}
	
	public void cancelOriginal(){
		this.originalFutures.stream().forEach(cf -> cf.cancel(true));
	}

	@Override
	public <R, A> R collect(Collector<? super U, A, R> collector) {
		return block(collector);
	}

	public BaseSimpleReact getPopulator(){
		return null;
	}
	public void returnPopulator(BaseSimpleReact service){
		
	}
	
	@Override
	public EagerFutureStream<U> withAsync(boolean b) {
		
		return this.withSimpleReact(this.simpleReact.withAsync(b));
	}



	@Override
	public ExecutorService getTaskExecutor() {
		return this.simpleReact.getExecutor();
	}



	@Override
	public RetryExecutor getRetrier() {
		return this.simpleReact.getRetrier();
	}



	@Override
	public boolean isAsync() {
		return this.simpleReact.isAsync();
	}



	@Override
	public EagerFutureStream<U> withTaskExecutor(ExecutorService e) {
		return this.withSimpleReact(simpleReact.withExecutor(e));
	}



	@Override
	public EagerFutureStream<U> withRetrier(RetryExecutor retry) {
		return this.withSimpleReact(simpleReact.withRetrier(retry));
	}
	
}
