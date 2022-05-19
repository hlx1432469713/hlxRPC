package top.hlx.test;



import top.hlx.rpc.HelloObject;
import top.hlx.rpc.HelloService;
import top.hlx.rpc.transport.RpcClientProxy;

public class TestClient {
    public static void main(String[] args) {
        RpcClientProxy rpcClientProxy = new RpcClientProxy("127.0.0.1",9000);
        HelloService helloService = rpcClientProxy.getProxy(HelloService.class);
        HelloObject object = new HelloObject(1,"This is a message");
        String res = helloService.hello(object);
        System.out.println(res);
    }
}
