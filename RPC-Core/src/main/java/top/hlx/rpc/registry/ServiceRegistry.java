package top.hlx.rpc.registry;



/**
 * 服务的容器，能够通过该容器创建多个服务，同时也能根据服务名称，获取相对应服务的信息
 * 服务注册表
 */
public interface ServiceRegistry {
    /**
     * 注册创建服务
     */
    <T> void regist(T service) throws Exception;
    Object getService(String serviceName);

}
