package top.hlx.rpc.enumeration.entity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 *   传输的协议--客户端请求服务端所需要的类,即服务端的接收对象
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RpcRequest implements Serializable {
    /**
     * 请求号
     */
    private String requestId;
    /**
     * 待调用接口名称
     */
    private String interfaceName;

    /**
     * 待调用接口的方法名称
     */
    private String methodName;

    /**
     * 调用方法的参数名称
     * （int max ,String str）
     *  parameters[0] = max parameters[1] = str
     */
    private Object[] parameters;

    /**
     * 调用方法的参数类型
     * （int max ,String str）
     *  paramTypes[0] = int paramTypes[1] = String
     */
    private Class<?>[] paramTypes;

    /**
     * 是否是心跳包
     */
    private Boolean heartBeat;
}
