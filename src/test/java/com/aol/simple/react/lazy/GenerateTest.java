package com.aol.simple.react.lazy;

import static org.junit.Assert.assertEquals;

import java.util.Collections;

import org.junit.Test;

import com.aol.simple.react.predicates.Predicates;
import com.aol.simple.react.stream.lazy.LazyFutureStream;
import com.aol.simple.react.stream.traits.SimpleReactStream;

public class GenerateTest {
    @Test
    public void testGenerateLazyNothing() {
        assertEquals(10, LazyFutureStream.generate().limit(10).toList().size());
    }
    @Test
    public void generateLazyNull(){
        assertEquals(10, LazyFutureStream.generate((Object) null).limit(10).toList().size());
    }
    
    @Test
    public void generateLazySupplyNull(){
        assertEquals(10,LazyFutureStream.generate(() -> null).limit(10).toList().size());
    }
    @Test
    public void generateLazyValue(){
        assertEquals(10, LazyFutureStream.generate(1).limit(10).toList().size());
    }
    @Test
    public void generateLazySequential(){
        assertEquals(Collections.nCopies(10, 1), LazyFutureStream.generate(1).limit(10).toList());
    }
    
   
}
