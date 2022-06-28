package top.hlx.rpc.loadBalancer;


import com.alibaba.nacos.api.naming.pojo.Instance;

import java.util.List;

/**
 * 主要用于负载均衡策略
 */
public interface LoadBalancer {
    /**
     *  select 方法用于从一系列 Instance 中选择一个 ,两个比较经典的算法：随机和轮转(是否可以在写第三个负载均衡算法)
     * @param instances
     * @return
     */
    Instance select(List<Instance> instances);
}
