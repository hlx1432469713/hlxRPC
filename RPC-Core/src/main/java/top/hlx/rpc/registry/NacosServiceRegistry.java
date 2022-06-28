package top.hlx.rpc.registry;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.hlx.rpc.enumeration.enumeration.RpcError;
import top.hlx.rpc.enumeration.exception.RpcException;

import java.net.InetSocketAddress;
import java.util.List;

public class NacosServiceRegistry implements ServiceRegistry {
    private static final Logger logger = LoggerFactory.getLogger(NacosServiceRegistry.class);
    private static final String SERVER_ADDR = "127.0.0.1:8848";
    /**
     * Nacos 的使用很简单，通过 NamingFactory 创建 NamingService 连接 Nacos连接的过程写在了静态代码块中，在类加载时自动连接。
     * namingService 提供了两个很方便的接口，registerInstance 和 getAllInstances 方法，
     * 前者可以直接向 Nacos 注册服务，后者可以获得某个服务的所有提供者的列表。
     */
    private static final NamingService namingService;
    static {
        try {
            namingService = NamingFactory.createNamingService(SERVER_ADDR);
        } catch (NacosException e) {
            logger.error("连接到Nacos时有错误发生: ", e);
            throw new RpcException(RpcError.FAILED_TO_CONNECT_TO_SERVICE_REGISTRY);
        }
    }
    @Override
    public void register(String serviceName, InetSocketAddress inetSocketAddress) {
        try {
            //将服务名称 ip地址 和 端口号注册到服务中心
            namingService.registerInstance(serviceName,inetSocketAddress.getHostName(),inetSocketAddress.getPort());
        } catch (NacosException e) {
            logger.error("注册服务时有错误发生:", e);
            throw new RpcException(RpcError.REGISTER_SERVICE_FAILED);
        }
    }
}
