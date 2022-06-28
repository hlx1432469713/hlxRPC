package top.hlx.rpc.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import top.hlx.rpc.enumeration.entity.PackageType;
import top.hlx.rpc.enumeration.entity.RpcRequest;
import top.hlx.rpc.serializer.CommonSerializer;

/**
 * 通用编码拦截器
 * 作用：在发送的数据上加上各种《必要的数据》，形成自定义的协议，而自《动加上这个数据》就是编码器的工作
 * 自定义的协议格式
 * +---------------+---------------+-----------------+-------------+
 * |  Magic Number |  Package Type | Serializer Type | Data Length |
 * |    4 bytes    |    4 bytes    |     4 bytes     |   4 bytes   |
 * +---------------+---------------+-----------------+-------------+
 * |                          Data Bytes                           |
 * |                   Length: ${Data Length}                      |
 * +---------------------------------------------------------------+
 *  Magic Number（魔数）：4字节（4B），表示一个协议包
 *  Package Type：4字节（4B），表示标明这是一个调用请求还是调用响应
 *  Serializer Type：4字节（4B），表示标明了实际数据使用的序列化器号（int 类型占4B），这个服务端和客户端应当使用统一标准
 *  Data Length：4字节（4B），表示实际数据的长度，设置这个字段主要防止《粘包》
 *  Data Bytes ：表示经过序列化后的实际数据，可能是RpcRequest （调用请求），也可能是RpcResponse经过序列化的字节（调用响应：服务器根据请求返回相对应的响应信息），取决于Package Type
 *
 *  请求：在浏览器地址栏输入地址，点击回车请求服务器，这个过程就是一个请求过程。
 *  响应：服务器根据浏览器发送的请求，返回数据到浏览器在网页上进行显示，这个过程就称之为响应。
 */
public class CommonEncoder extends MessageToByteEncoder {
    /**
     * CommonEncoder 继承了MessageToByteEncoder 类，见名知义，就是把 Message（实际要发送的对象）转化成 Byte 数组。
     * CommonEncoder 的工作很简单，就是把 RpcRequest 或者 RpcResponse 包装成协议包。
     * 根据上面提到的协议格式，将各个字段写到管道里就可以了，这里serializer.getCode() 获取序列化器的编号，
     * 之后使用传入的序列化器将请求或响应包序列化为字节数组写入管道即可。
     */
    private static final int MAGIC_NUMBER = 0xCAFEBABE;//魔数

    private CommonSerializer serializer;//通用的序列化 和 反序列化 接口

    public CommonEncoder(CommonSerializer serializer) {
        this.serializer = serializer;
    }

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Object o, ByteBuf byteBuf) throws Exception {
        byteBuf.writeInt(MAGIC_NUMBER);// 1.Magic Number
        if(o instanceof RpcRequest){// 2.Package Type
            //RpcRequest---请求
            byteBuf.writeInt(PackageType.REQUEST_PACK.getCode());
        }else{
            //RpcResponse---响应
            byteBuf.writeInt(PackageType.RESPONSE_PACK.getCode());
        }
        byteBuf.writeInt(serializer.getCode());//3.Serializer Type
        //进行序列化
        byte[] bytes = serializer.serialize(o);
        byteBuf.writeInt(bytes.length); //4.Data Length
        byteBuf.writeBytes(bytes);//5.Data Bytes
    }
}
