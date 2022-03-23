package server;

import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;

/**
 * 服务端---使用一个ServerSocket监听某个端口，循环接收连接请求，
 * 如果发来了请求就创建一个线程，在新线程中处理调用。这里创建线程采用线程池：
 */
@Data
public class RpcServer {
    private final ExecutorService threadPool;

    private int corePoolSize;//线程池大小--核心线程数
    private int maximumPoolSize;//线程池中允许的最大线程数
    private long keepAliveTime;//超时限制时间
    private BlockingQueue<Runnable> workingQueue;//阻塞队列

    private static final Logger logger = LoggerFactory.getLogger(RpcServer.class);


    public RpcServer( int corePoolSize, int maximumPoolSize,
                     long keepAliveTime, BlockingQueue<Runnable> workingQueue) {
        this.corePoolSize = corePoolSize;
        this.maximumPoolSize = maximumPoolSize;
        this.keepAliveTime = keepAliveTime;
        this.workingQueue = workingQueue;
        ThreadFactory threadFactory = Executors.defaultThreadFactory();
        threadPool = new ThreadPoolExecutor(corePoolSize,maximumPoolSize,keepAliveTime,TimeUnit.SECONDS,workingQueue,threadFactory);
    }

    /**
     * 服务器端，提供对外的访问接口，调用该方法，能注册一个访问服务端的接口，
     * 并同时开启服务器端监听指定端口的任务
     *
     * 该方法通过ServerSocket类来监听指定的端口号
     *
     * @param service
     * @param port 端口号
     */
    public void register(Object service ,int port){
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            logger.info("服务器正在启动");
            while (serverSocket.accept() != null){
                logger.info("客户端连接服务端！IP地址 = " + serverSocket.accept().getInetAddress());
                threadPool.execute(new WorkerThread(serverSocket.accept(),service));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
