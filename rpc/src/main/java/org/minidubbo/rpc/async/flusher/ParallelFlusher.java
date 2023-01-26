package org.minidubbo.rpc.async.flusher;

import com.lmax.disruptor.*;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import io.netty.channel.ChannelHandlerContext;
import org.minidubbo.rpc.async.DubboEvent;

import java.util.concurrent.ExecutorService;

public class ParallelFlusher<T> implements Flusher<T>{

    private Disruptor<T> disruptor;

    private ExecutorService executorService;

    private RingBuffer<T> ringBuffer;

    private EventTranslatorTwoArg eventTranslatorTwoArg;

    public ParallelFlusher(EventFactory<T> eventFactory, int ringBufferSize, ExecutorService executor,EventTranslatorTwoArg eventTranslatorTwoArg){
        this.executorService = executor;
        this.eventTranslatorTwoArg = eventTranslatorTwoArg;
        disruptor = new Disruptor<T>(eventFactory,ringBufferSize,executor,ProducerType.MULTI,new BlockingWaitStrategy());
        ringBuffer = disruptor.getRingBuffer();
    }

    @Override
    public void handleWith(EventHandler<T>... eventHandler) {
        disruptor.handleEventsWith(eventHandler);
    }

    @Override
    public void add(Object msg, ChannelHandlerContext ctx) {
        ringBuffer.publishEvent(eventTranslatorTwoArg,msg,ctx);
    }

    @Override
    public void start() {
        disruptor.start();
    }

    @Override
    public void shutdown() {
        disruptor.shutdown();
        executorService.shutdown();
    }




}
