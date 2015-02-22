package com.aol.simple.react.async;

import java.util.function.Supplier;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import com.aol.simple.react.stream.Subscription;

public class ClosingStreamGenerator {
	public static<T> Stream<T> closingStream(Supplier<T> s, Subscription subscription){
		
		 return StreamSupport.stream(
	                new ClosingSpliterator(Long.MAX_VALUE, s,subscription), false);
	}
}
