package com.aol.simple.react.stream;

import java.util.concurrent.atomic.AtomicLong;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Subscription {
	private volatile boolean active=true;
	private final AtomicLong count = new AtomicLong();
	private volatile int size= -1;
	private volatile int skip = 0;
}
