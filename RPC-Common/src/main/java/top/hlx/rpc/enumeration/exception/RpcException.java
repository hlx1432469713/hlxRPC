package top.hlx.rpc.enumeration.exception;

import top.hlx.rpc.enumeration.enumeration.RpcError;

/**
 * RPC调用异常类
 */
public class RpcException extends RuntimeException {
    public RpcException(RpcError error,String detail){
        super(error.getMessage() + ":" + detail);
    }

    public RpcException(String message,Throwable cause){
        super(message,cause);
    }

    public RpcException(RpcError error){
        super(error.getMessage());
    }
}
