package top.hlx.rpc.transport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.hlx.rpc.annotation.Service;
import top.hlx.rpc.annotation.ServiceScan;
import top.hlx.rpc.enumeration.enumeration.RpcError;
import top.hlx.rpc.enumeration.exception.RpcException;
import top.hlx.rpc.enumeration.util.ReflectUtil;
import top.hlx.rpc.provider.ServiceProvider;
import top.hlx.rpc.registry.ServiceRegistry;

import java.net.InetSocketAddress;
import java.util.Set;

public abstract class AbstractRpcServer implements RpcServer{
    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    protected String host;
    protected int port;

    protected ServiceRegistry serviceRegistry;
    protected ServiceProvider serviceProvider;

    /**
     * 基于注解进行服务的自动注册
     *
     * 我们首先需要获得要扫描的包的范围，就需要获取到 ServiceScan 注解的值，而我们前面说过，这个注解是加在启动类上的，
     * 那么，我们怎么知道启动类是哪一个呢？答案是通过调用栈。
     * 方法的调用和返回是通过方法调用栈来实现的，当调用一个方法时，该方法入栈，该方法返回时，该方法出栈，控制回到栈顶的方法。
     * 那么，main 方法一定位于调用栈的最底端，在 ReflectUtils 中，我写了一个 getStackTrace 方法（名字起得不好），用于获取 main 所在的类。
     * 通过 Class 对象的 isAnnotationPresent 方法来判断该类是否有 ServiceScan 注解。
     * 如果有，通过startClass.getAnnotation(ServiceScan.class).value(); 获取注解的值。
     *
     * 当获得扫描的范围后，就可以通过ReflectUtil.getClasses(basePackage) 获取到所有的 Class ，
     * 逐个判断是否有 Service 注解，如果有的话，通过反射创建该对象，并且调用 publishService 注册即可。

     */
    //进行全局扫描服务
    public void ScanServices(){
        String mainClassName = ReflectUtil.getStackTrace();
        Class<?> startClass;
        try{
            startClass = Class.forName(mainClassName);
            if (!startClass.isAnnotationPresent(ServiceScan.class)){
                logger.error("启动类缺少 @ServiceScan 注解");
                throw new RpcException(RpcError.SERVICE_SCAN_PACKAGE_NOT_FOUND);
            }
        }catch (ClassNotFoundException e){
            logger.error("出现未知错误");
            throw new RpcException(RpcError.UNKNOWN_ERROR);
        }
        String basePackage = startClass.getAnnotation(ServiceScan.class).value();
        if ("".equals(basePackage))
            basePackage = mainClassName.substring(0,mainClassName.lastIndexOf("."));
        Set<Class<?>> classSet = ReflectUtil.getClasses(basePackage);
        for (Class<?> clazz : classSet) {
            if (clazz.isAnnotationPresent(Service.class)){
                String serviceName = clazz.getAnnotation(Service.class).name();
                Object obj;
                try {
                    obj = clazz.newInstance();
                } catch (InstantiationException  | IllegalAccessException e) {
                    logger.error("创建 " + clazz + " 时有错误发生");
                    continue;
                }
                if ("".equals(serviceName)){
                    Class<?>[] interfaces = clazz.getInterfaces();
                    for (Class<?> oneInterface : interfaces){
                        publishService(obj,oneInterface.getCanonicalName());
                    }
                }else{
                    publishService(obj,serviceName);
                }
            }
        }
    }

    @Override
    public <T> void publishService(T service, String serviceName){
        serviceProvider.addServiceProvider(service,serviceName);
        serviceRegistry.register(serviceName,new InetSocketAddress(host,port));
    }

}
