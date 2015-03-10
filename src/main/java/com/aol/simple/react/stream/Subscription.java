package com.aol.simple.react.stream;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.Setter;

import org.jooq.lambda.Seq;

import com.aol.simple.react.async.Queue;
import com.aol.simple.react.async.Queue.ClosedQueueException;

@Getter
@Setter
public class Subscription {
	private Map<Queue,AtomicLong> limits;
	private Map<Queue,AtomicLong> count;
	private List<Queue> queues = new LinkedList();
	
	public void registerSkip(Queue q, int skip){
		limits.get(q).addAndGet(skip);
	}
	public void registerLimit(Queue q, int limit){
		limits.get(q).addAndGet(limit);
	}
	public void addQueue(Queue q){
		queues.add(q);
		limits.put(q, new AtomicLong(Long.MAX_VALUE));
		count.put(q, new AtomicLong(0l));
	}
	public boolean shouldContinue(Queue queue){
		long queueCount = count.get(queue).incrementAndGet();
		long limit = valuesToRight(queue).stream().reduce((acc,next)-> Math.min(acc, next)).get();
		if(queueCount < limit+1)
			return true;
		throw new ClosedQueueException();
		
	}
	private List<Long> valuesToRight(Queue queue) {
		return Seq.seq(queues.stream()).splitAt(findQueue(queue)).v2.map(limits::get).map(AtomicLong::get).collect(Collectors.toList());
		
	}
	
	private int findQueue(Queue queue){
		for(int i=0;i< queues.size();i++){
			if(queues.get(i) == queue)
				return i;
		}
		return -1;
	}
}
/**
stream.map().iterator().limit(4).flatMap().limit(2).iterator().limit(8)
subscription

stream no limit
	q1:limit (4)
	q2:limit (2)
	q3:limit (8)
	**/
