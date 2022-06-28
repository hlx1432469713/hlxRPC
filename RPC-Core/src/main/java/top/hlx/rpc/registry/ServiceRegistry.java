package top.hlx.rpc.registry;

import java.net.InetSocketAddress;

/**
 * 往服务注册中心注册服务接口
 */
public interface ServiceRegistry {
    /**
     * 将服务的名称(serviceName)和地址(InetSocketAddress)注册进服务注册中心
     * @param serviceName
     * @param inetSocketAddress
     */
    void register(String serviceName, InetSocketAddress inetSocketAddress);

}
