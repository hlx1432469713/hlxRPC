package server.agreement;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 *   传输的协议--客户端请求服务端所需要的类
 */
@Data
@Builder
public class RpcRequest implements Serializable {
    /**
     * 待调用接口名称
     */
    private String interfaceName;

    /**
     * 待调用方法名称
     */
    private String methodName;

    /**
     * 调用方法的参数
     */
    private Object[] parameters;

    /**
     * 调用方法的参数类型
     */
    private Class<?>[] paramTypes;
}
