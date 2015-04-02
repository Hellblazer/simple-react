package com.aol.simple.react.lazy;

import static com.aol.simple.react.stream.traits.LazyFutureStream.parallel;
import static com.aol.simple.react.stream.traits.LazyFutureStream.parallelBuilder;
import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.*;
import static org.jooq.lambda.tuple.Tuple.tuple;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jooq.lambda.Seq;
import org.jooq.lambda.tuple.Tuple2;
import org.junit.Ignore;
import org.junit.Test;

import com.aol.simple.react.async.Queue;
import com.aol.simple.react.async.QueueFactories;
import com.aol.simple.react.base.BaseSeqTest;
import com.aol.simple.react.stream.ThreadPools;
import com.aol.simple.react.stream.simple.SimpleReact;
import com.aol.simple.react.stream.traits.EagerFutureStream;
import com.aol.simple.react.stream.traits.FutureStream;
import com.aol.simple.react.stream.traits.LazyFutureStream;

public class LazySeqTest extends BaseSeqTest {
	
	

	
	
	@Test
	public void testZipWithFutures(){
		FutureStream stream = of("a","b");
		LazyFutureStream<Tuple2<Integer,String>> seq = of(1,2).zipFutures(stream);
		List<Tuple2<Integer,String>> result = seq.block();//.map(tuple -> Tuple.tuple(tuple.v1.join(),tuple.v2)).collect(Collectors.toList());
		assertThat(result.size(),is(asList(tuple(1,"a"),tuple(2,"b")).size()));
	}
	
	@Test
	public void testZipWithFuturesStream(){
		Stream stream = of("a","b");
		LazyFutureStream<Tuple2<Integer,String>> seq = of(1,2).zipFutures(stream);
		List<Tuple2<Integer,String>> result = seq.block();//.map(tuple -> Tuple.tuple(tuple.v1.join(),tuple.v2)).collect(Collectors.toList());
		assertThat(result.size(),is(asList(tuple(1,"a"),tuple(2,"b")).size()));
	}
	@Test
	public void testZipWithFuturesCoreStream(){
		Stream stream = Stream.of("a","b");
		LazyFutureStream<Tuple2<Integer,String>> seq = of(1,2).zipFutures(stream);
		List<Tuple2<Integer,String>> result = seq.block();//.map(tuple -> Tuple.tuple(tuple.v1.join(),tuple.v2)).collect(Collectors.toList());
		assertThat(result.size(),is(asList(tuple(1,"a"),tuple(2,"b")).size()));
	}
	

	@Test
	public void testZipFuturesWithIndex(){
		
		 LazyFutureStream<Tuple2<String,Long>> seq = of("a","b").zipFuturesWithIndex();
		List<Tuple2<String,Long>> result = seq.block();//.map(tuple -> Tuple.tuple(tuple.v1.join(),tuple.v2)).collect(Collectors.toList());
		assertThat(result.size(),is(asList(tuple("a",0l),tuple("b",1l)).size()));
	}
	@Test
	public void duplicateFutures(){
		List<String> list = of("a","b").duplicateFutures().v1.block();
		assertThat(sortedList(list),is(asList("a","b")));
	}
	private <T> List<T> sortedList(List<T> list) {
		return list.stream().sorted().collect(Collectors.toList());
	}

	@Test
	public void duplicateFutures2(){
		List<String> list = of("a","b").duplicateFutures().v2.block();
		assertThat(sortedList(list),is(asList("a","b")));
	}
	

	
	@Test
	public void batchSinceLastReadIterator() throws InterruptedException{
		Iterator<Collection<Integer>> it = of(1,2,3,4,5,6).chunkLastReadIterator();
	
		Thread.sleep(10);
		
		Collection one = it.next();
		
		Collection two = it.next();
		
		
		assertThat(one.size(),greaterThan(0));
		assertThat(two.size(),greaterThan(0));
		
	
		
	}
	@Test
	public void batchSinceLastRead() throws InterruptedException{
		List<Collection> cols = of(1,2,3,4,5,6).chunkSinceLastRead().peek(System.out::println).peek(it->{sleep(50);}).collect(Collectors.toList());
		
		System.out.println(cols.get(0));
		assertThat(cols.get(0).size(),is(1));
		assertThat(cols.size(),greaterThan(0));
		
		
	
		
	}
	
	@Test
	public void zipFastSlow() {
		Queue q = new Queue();
		LazyFutureStream.parallelBuilder().reactInfinitely(() -> sleep(100))
				.then(it -> q.add("100")).run(new ForkJoinPool(1));
		parallel(1, 2, 3, 4, 5, 6).zip(q.stream())
				.peek(it -> System.out.println(it))
				.collect(Collectors.toList());

	}

	@Test @Ignore
	public void testBackPressureWhenZippingUnevenStreams() throws InterruptedException {

		LazyFutureStream stream =  parallelBuilder().withExecutor(new ForkJoinPool(2))
								.reactInfinitely(() -> "100").peek(System.out::println)
				.withQueueFactory(QueueFactories.boundedQueue(2));
		Queue fast = stream.toQueue();

		Thread t = new Thread(() -> {
			parallelBuilder().withExecutor(new ForkJoinPool(2)).react(() -> 1, SimpleReact.times(10)).peek(c -> sleep(10))
					.zip(fast.stream()).forEach(it -> {
					});
		});
		t.start();

		int max = fast.getSizeSignal().getDiscrete().stream()
				.mapToInt(it -> (int) it).limit(5).max().getAsInt();
		assertThat(max, is(2));
		t.join();
	
	}

	@Test 
	public void testBackPressureWhenZippingUnevenStreams2() {

		Queue fast = parallelBuilder().withExecutor(new ForkJoinPool(2)).reactInfinitely(() -> "100")
				.withQueueFactory(QueueFactories.boundedQueue(10)).toQueue();

		new Thread(() -> {
			parallelBuilder().withExecutor(new ForkJoinPool(2)).react(() -> 1, SimpleReact.times(1000)).peek(c -> sleep(10))
					.zip(fast.stream()).forEach(it -> {
					});
		}).start();
		;

		int max = fast.getSizeSignal().getContinuous().stream()
				.mapToInt(it -> (int) it).limit(50).max().getAsInt();
		
		assertThat(max, lessThan(11));
	}

	

	@Test
	public void testOfType() {
		assertThat(of(1, "a", 2, "b", 3, null).ofType(Integer.class).toList(),containsInAnyOrder(1, 2, 3));
		assertThat(of(1, "a", 2, "b", 3, null).ofType(Integer.class).toList(),not(containsInAnyOrder("a", "b",null)));
		assertThat(of(1, "a", 2, "b", 3, null)
				.ofType(Serializable.class).toList(),containsInAnyOrder(1, "a", 2, "b", 3));
	}
	@Test @Ignore
	public void shouldZipTwoInfiniteSequences() throws Exception {
		
		final FutureStream<Integer> units = LazyFutureStream.iterate(1,n -> n+1);
		final FutureStream<Integer> hundreds = LazyFutureStream.iterate(100,n-> n+100);
		final Seq<String> zipped = units.zip(hundreds, (n, p) -> n + ": " + p);

		
		assertThat(zipped.limit(5).join(),equalTo(LazyFutureStream.of("1: 100", "2: 200", "3: 300", "4: 400", "5: 500").join()));
	}

	@Test
	public void shouldZipFiniteWithInfiniteSeq() throws Exception {
		ThreadPools.setUseCommon(false);
		final Seq<Integer> units = LazyFutureStream.iterate(1,n -> n+1).limit(5);
		final FutureStream<Integer> hundreds = LazyFutureStream.iterate(100,n-> n+100); // <-- MEMORY LEAK! - no auto-closing yet, so writes infinetely to it's async queue
		final Seq<String> zipped = units.zip(hundreds, (n, p) -> n + ": " + p);
		
		assertThat(zipped.limit(5).join(),equalTo(LazyFutureStream.of("1: 100", "2: 200", "3: 300", "4: 400", "5: 500").join()));
		ThreadPools.setUseCommon(true);
	}

	@Test
	public void shouldZipInfiniteWithFiniteSeq() throws Exception {
		ThreadPools.setUseCommon(false);
		final FutureStream<Integer> units = LazyFutureStream.iterate(1,n -> n+1); // <-- MEMORY LEAK!- no auto-closing yet, so writes infinetely to it's async queue
		final Seq<Integer> hundreds = LazyFutureStream.iterate(100,n-> n+100).limit(5);
		final Seq<String> zipped = units.zip(hundreds, (n, p) -> n + ": " + p);
		assertThat(zipped.limit(5).join(),equalTo(LazyFutureStream.of("1: 100", "2: 200", "3: 300", "4: 400", "5: 500").join()));
		ThreadPools.setUseCommon(true);
	}

	
	@Test
	public void testCastPast() {
		assertThat(
				of(1, "a", 2, "b", 3, null).capture(e -> e.printStackTrace())
						.cast(Serializable.class).toList(),containsInAnyOrder(1, "a", 2, "b", 3, null));

	}

	@Override
	protected <U> LazyFutureStream<U> of(U... array) {

		return parallel(array);
	}
	@Override
	protected <U> LazyFutureStream<U> react(Supplier<U>... array) {
		return LazyFutureStream.parallelBuilder().react(array);
		
	}
	protected Object sleep(int i) {
		try {
			Thread.currentThread().sleep(i);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return i;
	}

}
