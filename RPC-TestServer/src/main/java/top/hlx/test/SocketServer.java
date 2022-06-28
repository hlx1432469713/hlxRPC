package top.hlx.test;

import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.hlx.rpc.enumeration.factory.ThreadPoolFactory;
import top.hlx.rpc.handler.RequestHandler;
import top.hlx.rpc.hook.ShutdownHook;
import top.hlx.rpc.provider.ServiceProvider;
import top.hlx.rpc.provider.ServiceProviderImpl;
import top.hlx.rpc.registry.NacosServiceRegistry;
import top.hlx.rpc.serializer.CommonSerializer;
import top.hlx.rpc.transport.AbstractRpcServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.util.concurrent.*;

/**
 * 服务端---使用一个ServerSocket监听某个端口，循环接收连接请求，
 * 如果发来了请求就创建一个线程，在新线程中处理调用。这里创建线程采用线程池：
 *
 * 实现RpcServer服务器通用类
 */
public class SocketServer extends AbstractRpcServer {
    private final ExecutorService threadPool;
    private final CommonSerializer commonSerializer;
    private RequestHandler requesthandler = new RequestHandler();

    public SocketServer(String host,int port){
        this(host,port,DEFAULT_SERIALIZER);
    }


    public SocketServer(String host,int port,Integer serializer) {
      this.host = host;
      this.port = port;
      threadPool = ThreadPoolFactory.createDefaultThreadPool("socket-rpc-server");
      this.serviceRegistry = new NacosServiceRegistry();
      this.serviceProvider = new ServiceProviderImpl();
      this.commonSerializer = CommonSerializer.getByCode(serializer);
      ScanServices();
    }


    /**
     * 服务器端，提供对外的访问接口，调用该方法，能注册一个访问服务端的接口，
     * 并同时开启服务器端监听指定端口的任务
     *
     * 该方法通过ServerSocket类来监听指定的端口号
     *
     */
    public void start(){
        try {
            ServerSocket serverSocket = new ServerSocket();
            serverSocket.bind(new InetSocketAddress(host,port));
            logger.info("服务器正在启动.......");
            ShutdownHook.getShutdownHook().addClearAllHook();
            while (serverSocket.accept() != null){
                logger.info("消费者连接：{}:{}" + serverSocket.accept().getInetAddress(),port);
                threadPool.execute(new RequesthandlerThread(serverSocket.accept(), serviceProvider,requesthandler));
            }
            threadPool.shutdown();
        } catch (IOException e) {
            logger.error("服务器启动时有错误发生:", e);
        }
    }

    @Override
    public <T> void publishService(Object service, Class<T> serviceClass) {

    }
}
