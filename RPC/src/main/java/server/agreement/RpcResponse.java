package server.agreement;

import lombok.Data;

import java.io.Serializable;

/**
 * 服务器  返回信息   封装的对象
 */
@Data
public class RpcResponse<T> implements Serializable {
    /**
     * 响应状态码
     */
    private Integer status;

    /**
     * 响应状态补充信息
     */
    private String message;
    /**
     * 响应数据
     */
    private T data;

    public static<T> RpcResponse<T> success(T data){
        RpcResponse<T> rpcResponse = new RpcResponse<>();
        rpcResponse.setData(data);
        rpcResponse.setStatus(200);
        return rpcResponse;
    }
    public static<T> RpcResponse<T> fail(T data){
        RpcResponse<T> rpcResponse = new RpcResponse<>();
        rpcResponse.setData(data);
        rpcResponse.setStatus(404);
        return rpcResponse;
    }
}
