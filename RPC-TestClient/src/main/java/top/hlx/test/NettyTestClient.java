package top.hlx.test;

import top.hlx.rpc.HelloObject;
import top.hlx.rpc.HelloService;
import top.hlx.rpc.transport.RpcClient;
import top.hlx.rpc.transport.RpcClientProxy;
import top.hlx.rpc.transport.netty.client.NettyClient;

/**
 * 测试用Netty消费者（客户端）
 */
public class NettyTestClient {
    public static void main(String[] args) {
        RpcClient client = new NettyClient();
        RpcClientProxy rpcClientProxy = new RpcClientProxy(client);
        HelloService helloService = rpcClientProxy.getProxy(HelloService.class);
        HelloObject helloObject = new HelloObject(12,"This is Message");
        String res = helloService.hello(helloObject);
        System.out.println(res);
    }
}
