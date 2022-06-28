package top.hlx.rpc.registry;

import java.net.InetSocketAddress;

/**
 * 服务发现接口
 */
public interface ServiceDiscovery {

    /**
     * 根据服务名称(serviceName)从注册中心获取到一个服务提供者的地址(InetSocketAddress)
     * 根据服务名称查找服务实体
     *
     * @param serviceName
     * @return
     */
    InetSocketAddress lookupService(String serviceName);
}
