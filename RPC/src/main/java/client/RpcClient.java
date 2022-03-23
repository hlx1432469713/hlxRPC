package client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import server.agreement.RpcRequest;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * 客户端
 */
public class RpcClient {
    private static final Logger logger = LoggerFactory.getLogger(RpcClient.class);

    /**
     *
     * 客户端发送请求至指定的服务器，并且接收返回信息
     *
     * @param rpcRequest 传输请求的类
     * @param host  服务器的IP地址
     * @param port  服务器的端口号
     * @return
     */
    public Object sendRequest(RpcRequest rpcRequest,String host ,int port)   {
        Socket socket = null;
        try {
            socket = new Socket(host,port);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
            objectOutputStream.writeObject(rpcRequest);
            objectOutputStream.flush();
            System.out.println("2222222");
            return objectInputStream.readObject();
        } catch (IOException e) {
            logger.error("调用时有错误发生：", e);
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            logger.error("调用时有错误发生：", e);
            e.printStackTrace();
        }
        return null;
    }

}
