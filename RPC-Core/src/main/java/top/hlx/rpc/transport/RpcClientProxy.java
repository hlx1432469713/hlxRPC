package top.hlx.rpc.transport;

import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.hlx.rpc.enumeration.entity.RpcRequest;
import top.hlx.rpc.enumeration.entity.RpcResponse;
import top.hlx.rpc.enumeration.util.RpcMessageChecker;
import top.hlx.rpc.transport.netty.client.NettyClient;


import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * 客户端代理实例，用于客户端访问服务端
 *
 *
 * 通过动态代理的方式生成实例，并且调用方法时生产需要的RpcRequest对象并且发送给服务端。
 * 本类采用的是JDK动态代理，代理类实现InvocationHandler接口
 *
 *
 * InvocationHandler接口是proxy代理实例的调用处理程序实现的一个接口，
 * 每一个proxy代理实例都有一个关联的调用处理程序；
 * 在代理实例调用方法时，方法调用被编码分派到调用处理程序的invoke方法。
 */
@Data
public class RpcClientProxy implements InvocationHandler {

    private static final Logger logger = LoggerFactory.getLogger(RpcClientProxy.class);

    private final RpcClient client;

    public RpcClientProxy(RpcClient client) {
        this.client = client;
    }

    /**
     * 生成一个代理对象
     *  @SuppressWarnings("unchecked") :告诉编译器忽略 unchecked 警告信息，如使用List，ArrayList等未进行参数化产生的警告信息。
     * @param classz
     * @param <T>
     * @return
     */
    @SuppressWarnings("unchecked")
    public <T> T getProxy(Class<T> classz){
        return (T) Proxy.newProxyInstance(classz.getClassLoader(),new Class[]{classz},this);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        logger.info("调用方法：{}#{}",method.getDeclaringClass().getName(),method.getName());
        RpcRequest rpcRequest = new RpcRequest(UUID.randomUUID().toString(),method.getDeclaringClass().getName(),
               method.getName(),args,method.getParameterTypes(),false);
        RpcResponse rpcResponse = null;
        if (client instanceof NettyClient){
            try {
//                CompletableFuture<RpcResponse> completableFuture = (CompletableFuture<RpcResponse>) client.sendRequest(rpcRequest);
//                rpcResponse = completableFuture.get();
                //rpcResponse = (RpcResponse) client.sendRequest(rpcRequest);
                return  client.sendRequest(rpcRequest);
            }catch (Exception e){
                logger.error("方法调用请求发送失败", e);
                return null;
            }
        }
        if (client instanceof SocketClient){
            rpcResponse = (RpcResponse) client.sendRequest(rpcRequest);
        }
        RpcMessageChecker.check(rpcRequest,rpcResponse);
        return rpcResponse.getData();

    }
}
