package top.hlx.test;



import top.hlx.rpc.HelloService;
import top.hlx.rpc.registry.ServiceRegistry;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class TestServer {
    public static void main(String[] args) {
        HelloService helloService = new HelloServiceImpl();
        ServiceRegistry serviceRegistry = new DefaultServiceRegistry();
        try {
            serviceRegistry.regist(helloService);
            RpcServer rpcServer = new RpcServer(serviceRegistry);
            rpcServer.start(9000);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("服务器开始监听！端口号为 ： " + 9000);
    }
}
