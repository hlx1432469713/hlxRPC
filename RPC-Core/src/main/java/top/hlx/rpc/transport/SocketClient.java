package top.hlx.rpc.transport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.hlx.rpc.enumeration.entity.RpcRequest;


import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * 客户端 ---主要目的是将对象发送到服务器，并接收返回值
 */
public class SocketClient implements RpcClient {
    private static final Logger logger = LoggerFactory.getLogger(SocketClient.class);

    /**
     *
     * 客户端发送请求至指定的服务器，并且接收返回信息
     *
     * @param rpcRequest 传输请求的类(需要发送对象)
     * @param host  服务器的IP地址
     * @param port  服务器的端口号
     * @return
     */
    public Object sendRequest(RpcRequest rpcRequest, String host , int port)   {
        Socket socket = null;
        try {
            socket = new Socket(host,port);
            //ObjectOutputStream（序列化的关键类） : 可以将一个对象转换成二进制流
            //ObjectInputStream（反序列化的关键类） ： 可以将二进制流还原成对象
            //可以实现readObject、writeObject方法实现自己的序列化策略，
            //flush()方法用于刷新此流，并将任何缓冲输出的字节立即写入基础流。
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
            objectOutputStream.writeObject(rpcRequest);
            objectOutputStream.flush();
            System.out.println("2222222");
            return objectInputStream.readObject();
        } catch (IOException e) {
            logger.error("调用时有错误发生1：", e);
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            logger.error("调用时有错误发生2：", e);
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Object sendRequest(RpcRequest rpcRequest) {
        return null;
    }
}
