package com.digout.processor;

public interface DataInputPreProcessor<T> {

    T preProcess(final T data);

}
