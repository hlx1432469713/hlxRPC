package top.hlx.test;

import top.hlx.rpc.HelloService;
import top.hlx.rpc.annotation.ServiceScan;
import top.hlx.rpc.provider.ServiceProviderImpl;
import top.hlx.rpc.provider.ServiceProvider;
import top.hlx.rpc.serializer.CommonSerializer;
import top.hlx.rpc.transport.netty.server.NettyServer;

/**
 * 测试Netty服务提供者（服务端）
 */
@ServiceScan
public class NettyTestServer {
    public static void main(String[] args) {
        HelloService helloService = new HelloServiceImpl();
        NettyServer server = new NettyServer("127.0.0.1",9999, CommonSerializer.PROTOBUF_SERIALIZER);
        server.start();

    }

}
