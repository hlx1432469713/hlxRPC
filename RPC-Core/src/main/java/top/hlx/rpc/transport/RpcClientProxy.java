package top.hlx.rpc.transport;

import lombok.Data;
import top.hlx.rpc.enumeration.entity.RpcRequest;
import top.hlx.rpc.enumeration.entity.RpcResponse;


import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

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
    /**
     * IP地址
     */
    private String host;

    /**
     * 端口号
     */
    private int port;

    public RpcClientProxy(String host, int port) {
        this.host = host;
        this.port = port;
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
        RpcRequest rpcRequest = RpcRequest.builder()
                .interfaceName(method.getDeclaringClass().getName())  //接口名称
                .methodName(method.getName())                         //接口中所调用的方法名称
                .parameters(args)                                     //方法中的参数名称
                .paramTypes(method.getParameterTypes())               //方法中的参数类型
                .build();
        RpcClient rpcClient = new RpcClient();
        return ((RpcResponse)rpcClient.sendRequest(rpcRequest, host, port)).getData();
    }
}
