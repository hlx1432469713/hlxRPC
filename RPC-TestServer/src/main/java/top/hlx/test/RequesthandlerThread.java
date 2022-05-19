package top.hlx.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.hlx.rpc.enumeration.entity.RpcRequest;
import top.hlx.rpc.enumeration.entity.RpcResponse;
import top.hlx.rpc.handler.RequestHandler;
import top.hlx.rpc.registry.ServiceRegistry;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * 处理线程，接受对象
 */
public class RequesthandlerThread implements Runnable{
    private static final Logger logger = LoggerFactory.getLogger(RequesthandlerThread.class);

    private Socket socket;
    private ServiceRegistry serviceRegistry;
    //具体
    private RequestHandler requestHandler;

    //构造函数
    public RequesthandlerThread(Socket socket, ServiceRegistry serviceRegistry, RequestHandler requestHandler) {
        this.socket = socket;
        this.serviceRegistry = serviceRegistry;
        this.requestHandler = requestHandler;
    }

    @Override
    public void run() {
        try {
            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            RpcRequest rpcRequest = (RpcRequest) objectInputStream.readObject();
            String interfaceName = rpcRequest.getInterfaceName();
            Object service = serviceRegistry.getService(interfaceName);
            Object result = requestHandler.handle(rpcRequest,service);
            objectOutputStream.writeObject(RpcResponse.success(result));
            objectOutputStream.flush();
        } catch (IOException e) {
            logger.error("调用或发送时有错误发生：", e);
        } catch (ClassNotFoundException e) {
            logger.error("调用或发送时有错误发生：", e);
        }
    }
}
