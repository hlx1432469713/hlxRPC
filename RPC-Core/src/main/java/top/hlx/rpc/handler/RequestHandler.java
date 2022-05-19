package top.hlx.rpc.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.hlx.rpc.enumeration.entity.RpcRequest;
import top.hlx.rpc.enumeration.entity.RpcResponse;
import top.hlx.rpc.enumeration.enumeration.ResponseCode;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 进行过程调用的处理器
 */
public class RequestHandler {
    private static final Logger logger = LoggerFactory.getLogger(RequestHandler.class);
    public Object handle(RpcRequest rpcRequest,Object service){
        Object result = null;
        try {
            result = invokeTargetMethod(rpcRequest,service);
            //服务的名称，就是客户端调用的对外的接口名称-----服务 <-----> 接口
            logger.info("服务:{} 成功调用方法:{}", rpcRequest.getInterfaceName(),rpcRequest.getMethodName());
        }catch (InvocationTargetException | IllegalAccessException e){
            logger.error("调用或发送时有错误发生：", e);
        }
        return result;
    }
    public Object invokeTargetMethod(RpcRequest rpcRequest,Object service) throws InvocationTargetException, IllegalAccessException {
        Method method;
        try {
            method = service.getClass().getMethod(rpcRequest.getMethodName(),rpcRequest.getParamTypes());
        } catch (NoSuchMethodException e) {
            return RpcResponse.fail(ResponseCode.METHOD_NOT_FOUND);
        }
        return method.invoke(service,rpcRequest.getParameters());
    }
}
