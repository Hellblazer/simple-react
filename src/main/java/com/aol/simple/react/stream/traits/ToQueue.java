package com.aol.simple.react.stream.traits;

import com.aol.simple.react.async.Queue;
import com.aol.simple.react.stream.Subscription;

public interface ToQueue <U>{
	abstract  Queue<U> toQueue(); 
	void addOpenQueue(Queue queue);
	abstract Subscription getSubscription();
	static Object populateQueue(Subscription subscription,Queue<U> queue, U it){
		try{
			return queue.offer(it);
		}catch(Queue.ClosedQueueException e){
			subscription.setActive(false);
		}
	}
}
