package top.hlx.test;

import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.hlx.rpc.handler.RequestHandler;
import top.hlx.rpc.registry.ServiceRegistry;

import java.io.IOException;
import java.net.ServerSocket;
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
    private RequestHandler requesthandler = new RequestHandler();
    private ServiceRegistry serviceRegistry;

    private static final Logger logger = LoggerFactory.getLogger(RpcServer.class);


//    public RpcServer( int corePoolSize, int maximumPoolSize,
//                     long keepAliveTime, BlockingQueue<Runnable> workingQueue) {
//        this.corePoolSize = corePoolSize;
//        this.maximumPoolSize = maximumPoolSize;
//        this.keepAliveTime = keepAliveTime;
//        this.workingQueue = workingQueue;
//        ThreadFactory threadFactory = Executors.defaultThreadFactory();
//        threadPool = new ThreadPoolExecutor(corePoolSize,maximumPoolSize,keepAliveTime,TimeUnit.SECONDS,workingQueue,threadFactory);
//    }
        public RpcServer(ServiceRegistry serviceRegistry) {
            int corePoolSize = 5;
            int maximumPoolSize = 50;
            long keepAliveTime = 60;
            BlockingQueue<Runnable> workingQueue = new ArrayBlockingQueue<>(100);
            ThreadFactory threadFactory = Executors.defaultThreadFactory();
            threadPool = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.SECONDS, workingQueue, threadFactory);
            this.serviceRegistry = serviceRegistry;
        }


    /**
     * 服务器端，提供对外的访问接口，调用该方法，能注册一个访问服务端的接口，
     * 并同时开启服务器端监听指定端口的任务
     *
     * 该方法通过ServerSocket类来监听指定的端口号
     *
     * @param port 端口号
     */
    public void start(int port){
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            logger.info("服务器正在启动.......");
            while (serverSocket.accept() != null){
                logger.info("消费者连接：{}:{}" + serverSocket.accept().getInetAddress(),port);
                threadPool.execute(new RequesthandlerThread(serverSocket.accept(),serviceRegistry,requesthandler));
            }
            threadPool.shutdown();
        } catch (IOException e) {
            logger.error("服务器启动时有错误发生:", e);
        }
    }
}
