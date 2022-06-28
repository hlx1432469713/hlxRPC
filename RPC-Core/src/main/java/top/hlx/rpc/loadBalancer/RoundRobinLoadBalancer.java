package top.hlx.rpc.loadBalancer;

import com.alibaba.nacos.api.naming.pojo.Instance;

import java.util.List;

/**
 * 负载均衡：轮转算法
 */
public class RoundRobinLoadBalancer implements LoadBalancer{
    private int index = 0;
    @Override
    public Instance select(List<Instance> instances) {
        if(index >= instances.size())
            index %= instances.size();
        return instances.get(index++);
    }
}
