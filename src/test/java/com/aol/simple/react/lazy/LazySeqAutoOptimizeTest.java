package com.aol.simple.react.lazy;

import static com.aol.simple.react.stream.traits.LazyFutureStream.parallel;

import java.util.function.Supplier;

import com.aol.simple.react.stream.lazy.LazyReact;
import com.aol.simple.react.stream.traits.LazyFutureStream;

public class LazySeqAutoOptimizeTest extends LazySeqTest {
	@Override
	protected <U> LazyFutureStream<U> of(U... array) {
		return new LazyReact()
							.autoOptimiseOn()
							.of(array);
	}
	@Override
	protected <U> LazyFutureStream<U> ofThread(U... array) {
		return new LazyReact()
							.autoOptimiseOn()
							.of(array);
	}

	@Override
	protected <U> LazyFutureStream<U> react(Supplier<U>... array) {
		return new LazyReact().autoOptimiseOn()
								.react(array);
	}
}
