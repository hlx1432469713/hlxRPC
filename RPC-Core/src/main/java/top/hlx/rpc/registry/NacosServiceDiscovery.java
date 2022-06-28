package top.hlx.rpc.registry;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.pojo.Instance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.hlx.rpc.enumeration.util.NacosUtil;
import top.hlx.rpc.loadBalancer.LoadBalancer;
import top.hlx.rpc.loadBalancer.RandomLoadBalancer;

import java.net.InetSocketAddress;
import java.util.List;

public class NacosServiceDiscovery implements ServiceDiscovery{

    private static final Logger logger = LoggerFactory.getLogger(NacosServiceDiscovery.class);

    private final LoadBalancer loadBalancer;

    public NacosServiceDiscovery(LoadBalancer loadBalancer){
        if (loadBalancer == null)
            this.loadBalancer = new RandomLoadBalancer();//随机方法----负载均衡
        else
            this.loadBalancer = loadBalancer;
    }
    @Override
    public InetSocketAddress lookupService(String serviceName) {
        try {
            List<Instance> instanceList = NacosUtil.getAllInstance(serviceName);
            //在 lookupService 方法中，通过 getAllInstance 获取到某个服务的所有提供者列表后，
            // 需要选择一个，这里就涉及了负载均衡策略，这里我们先选择第 0 个，后面某节会详细讲解负载均衡。
            Instance instance = loadBalancer.select(instanceList);
            return new InetSocketAddress(instance.getIp(),instance.getPort());
        } catch (NacosException e) {
            logger.error("获取服务时有错误发生:", e);
        }
        return null;
    }
}
