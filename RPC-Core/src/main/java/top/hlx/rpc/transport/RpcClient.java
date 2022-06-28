package top.hlx.rpc.transport;

import top.hlx.rpc.enumeration.entity.RpcRequest;
import top.hlx.rpc.serializer.CommonSerializer;

/**
 * 客户端的通用接口--用于向服务器发送请求
 */
public interface RpcClient {
    int DEFAULT_SERIALIZER = CommonSerializer.KRYO_SERIALIZER;

    Object sendRequest(RpcRequest rpcRequest);
}
