package top.hlx.rpc.provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.hlx.rpc.enumeration.enumeration.RpcError;
import top.hlx.rpc.enumeration.exception.RpcException;


import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ServiceProviderImpl implements ServiceProvider {
    private static final Logger logger = LoggerFactory.getLogger(ServiceProviderImpl.class);
    /**
     * serviceMap:存储注册服务名称和注册服务信息的对应关系
     * static :这样就能保证全局唯一的注册信息，并且在创建 RpcServer 时也就不需要传入了。
     */
    private static final Map<String,Object> serviceMap = new ConcurrentHashMap<>();

    /**
     * registedService:存储已经注册的服务的名称
     * static :这样就能保证全局唯一的注册信息，并且在创建 RpcServer 时也就不需要传入了。
     */
    private static final Set<String> registeredService =  ConcurrentHashMap.newKeySet();
    @Override
    public <T> void addServiceProvider(T service ,String serviceName){
        if (registeredService.contains(serviceName)) return;
        registeredService.add(serviceName);
        serviceMap.put(serviceName, service);
        logger.info("向接口: {} 注册服务: {}", service.getClass().getInterfaces(), serviceName);
    }

    @Override
    public Object getServiceProvider(String serviceName) {
        Object service = serviceMap.get(serviceName);
        if (service == null)
            return new RpcException(RpcError.SERVICE_NOT_FOUND);
        else
            return service;
    }
}
