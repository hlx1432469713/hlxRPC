package top.hlx.test;



import top.hlx.rpc.HelloService;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class TestServer {
    public static void main(String[] args) {
        HelloService helloService = new HelloServiceImpl();
        int corePoolSize = 5;
        int maximumPoolSize = 50;
        long keepAliveTime = 60;
        BlockingQueue<Runnable> workingQueue = new ArrayBlockingQueue<>(100);
        //RpcServer rpcServer = new RpcServer(corePoolSize,maximumPoolSize,keepAliveTime,workingQueue);
        //RpcServer rpcServer = new RpcServer();
        //rpcServer.register(helloService,9000);
        System.out.println("服务器开始监听！端口号为 ： " + 9000);
    }
}
