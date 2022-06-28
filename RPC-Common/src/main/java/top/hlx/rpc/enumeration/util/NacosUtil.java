package top.hlx.rpc.enumeration.util;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.hlx.rpc.enumeration.enumeration.RpcError;
import top.hlx.rpc.enumeration.exception.RpcException;

import java.net.InetSocketAddress;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * 管理Nacos连接等工具类
 *
 *
 * 那么我们就需要一种办法，在服务端关闭之前自动向 Nacos 注销服务。但是有一个问题，我们不知道什么时候服务器会关闭，也就不知道这个方法调用的时机，就没有办法手工去调用。这时，我们就需要钩子。
 *
 * 钩子是什么呢？是在某些事件发生后自动去调用的方法。那么我们只需要把注销服务的方法写到关闭系统的钩子方法里就行了。
 *
 * 首先先写向 Nacos 注销所有服务的方法，这部分被放在了 NacosUtils 中作为一个静态方法，NacosUtils 是一个 Nacos 相关的工具类：

 */
public class NacosUtil {

    /**
     * 所有的服务名称都被存储在 NacosUtils 类中的 serviceNames 中，在注销时只需要用迭代器迭代所有服务名，调用 deregisterInstance 即可。
     */
    private static final Logger logger = LoggerFactory.getLogger(NacosUtil.class);

    private static final NamingService namingService;
    private static final Set<String> serviceNames = new HashSet<>();
    private static InetSocketAddress address;

    private static final String seSERVER_ADDR = "127.0.0.1:8848";

    static {
        namingService = getNacosNamingService();
    }

    public static NamingService getNacosNamingService(){
        try {
            return NamingFactory.createNamingService(seSERVER_ADDR);
        } catch (NacosException e) {
            logger.error("连接到Nacos时有错误发生: ", e);
            throw new RpcException(RpcError.FAILED_TO_CONNECT_TO_SERVICE_REGISTRY);
        }
    }

    public static void registerService(String serviceName, InetSocketAddress address) throws NacosException {
        namingService.registerInstance(serviceName,address.getHostName(),address.getPort());
        NacosUtil.address = address;
        serviceNames.add(serviceName);
    }

    public static List<Instance> getAllInstance(String serviceName) throws NacosException {
        return namingService.getAllInstances(serviceName);
    }

    public static void clearRegistry(){
        if(!serviceNames.isEmpty() && address != null){
            String host = address.getHostName();
            int port = address.getPort();
            Iterator<String> iterator = serviceNames.iterator();
            while (iterator.hasNext()){
                String serviceName = iterator.next();
                try {
                    namingService.deregisterInstance(serviceName,host,port);
                } catch (NacosException e) {
                    logger.error("注销服务 {} 失败", serviceName, e);
                }
            }
        }
    }
}
