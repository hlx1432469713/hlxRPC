package top.hlx.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.hlx.rpc.registry.ServiceRegistry;


import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultServiceRegistry implements ServiceRegistry {
    private static final Logger logger = LoggerFactory.getLogger(DefaultServiceRegistry.class);
    /**
     * serviceMap:存储注册服务名称和注册服务信息的对应关系
     */
    private final Map<String,Object> serviceMap = new ConcurrentHashMap<>();

    /**
     * registedService:存储已经注册的服务的名称
     */
    private final Set<String> registedService =  ConcurrentHashMap.newKeySet();
    @Override
    public <T> void regist(T service) throws Exception {
       String serviceName = service.getClass().getCanonicalName();
       if (registedService.contains(serviceName))
           return;
       registedService.add(serviceName);
       //获取接口中的
       Class<?>[] interfaces = service.getClass().getInterfaces();
       if(interfaces.length == 0)
           throw new Exception("接口为空");
        for (Class<?> anInterface : interfaces) {
            serviceMap.put(anInterface.getCanonicalName(),service);
        }
        logger.info("向接口：{}，注册服务：{}",interfaces,service);
    }

    @Override
    public Object getService(String serviceName) {
        Object service = serviceMap.get(serviceName);
        if (service == null)
            return null;
        else
            return service;
    }
}
