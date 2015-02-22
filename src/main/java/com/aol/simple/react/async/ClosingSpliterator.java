package com.aol.simple.react.async;

import java.util.Objects;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Supplier;

import com.aol.simple.react.async.Queue.ClosedQueueException;
import com.aol.simple.react.stream.Subscription;


public class ClosingSpliterator<T> implements Spliterator<T> {
        private long estimate;
        private final Subscription subscription;
        final Supplier<T> s;

        protected ClosingSpliterator(long estimate,Supplier<T> s, Subscription subscription) {
            this.estimate = estimate;
            this.s = s;
            this.subscription = subscription;
        }

        @Override
        public long estimateSize() {
            return estimate;
        }

        @Override
        public int characteristics() {
            return IMMUTABLE;
        }
        
    

		@Override
		public boolean tryAdvance(Consumer<? super T> action) {
			 Objects.requireNonNull(action);
			 if(!subscription.hasNext())
         		return false;
            try{ 
            	
            	action.accept(s.get());
            	subscription.getCount().incrementAndGet();
            	
             return true;
            }catch(ClosedQueueException e){
            	return false;
            }catch(Exception e){
            	e.printStackTrace();
            	return false;
            }
            
		}

		@Override
		public Spliterator<T> trySplit() {
			
			return new ClosingSpliterator(estimate >>>= 1, s,subscription);
		}

       
    }
  
    
