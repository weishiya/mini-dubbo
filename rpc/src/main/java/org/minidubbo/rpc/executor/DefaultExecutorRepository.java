package org.minidubbo.rpc.executor;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
//todo 后续拓展为SPI
public enum DefaultExecutorRepository {

    INSTANCE;

    private ExecutorService WORKER_EXECUTOR =
            new ThreadPoolExecutor(200,200, 60, TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(1024*4),
            new ThreadNameFactory("dubbo-worker",false));


    public ExecutorService getDefaultExecutorService(){
        return WORKER_EXECUTOR;
    }

    class ThreadNameFactory implements ThreadFactory{

        private String prefix;

        private boolean daemon;

        final AtomicInteger threadNum = new AtomicInteger(1);

        public ThreadNameFactory(String prefix,boolean daemon){
            this.prefix = prefix;
            this.daemon = daemon;
        }

        @Override
        public Thread newThread(Runnable r) {
            Thread thread =new Thread(r,prefix+"-thread-"+threadNum.getAndIncrement());
            thread.setDaemon(daemon);
            return thread;
        }
    }

}
