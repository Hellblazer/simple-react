package com.aol.simple.react.stream.traits;

import java.util.concurrent.CompletableFuture;

public class ToCompletableFuture {

	public static CompletableFuture completedFuture(Object o){
		if(o instanceof CompletableFuture)
			return (CompletableFuture)o;
		else
			return CompletableFuture.completedFuture(o);
	}
}
