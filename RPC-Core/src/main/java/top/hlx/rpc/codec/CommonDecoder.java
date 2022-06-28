package top.hlx.rpc.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.hlx.rpc.enumeration.entity.PackageType;
import top.hlx.rpc.enumeration.entity.RpcRequest;
import top.hlx.rpc.enumeration.entity.RpcResponse;
import top.hlx.rpc.enumeration.enumeration.RpcError;
import top.hlx.rpc.enumeration.exception.RpcException;
import top.hlx.rpc.serializer.CommonSerializer;

import java.util.List;

/**
 * 通用的解码拦截器
 * 作用：解析数据获得《原始数据》就是解码器的工作。
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
public class CommonDecoder extends ReplayingDecoder {
    /**
     * CommonDecoder 继承自 ReplayingDecoder ，与 MessageToByteEncoder 相反，
     * 它用于将收到的字节序列还原为实际对象。主要就是一些字段的校验，比较重要的就是取出序列化器的编号，以获得正确的反序列化方式，
     * 并且读入 length 字段来确定数据包的长度（防止粘包），最后读入正确大小的字节数组，反序列化成对应的对象。

     */
    private static final Logger logger = LoggerFactory.getLogger(CommonDecoder.class);
    private static final int MAGIC_NUMBER = 0xCAFEBABE;//魔数
    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        int magic = byteBuf.readInt();
        if(magic != MAGIC_NUMBER){
            logger.error("不识别的协议包：{}",magic);
            throw new RpcException(RpcError.UNKNOWN_PROTOCOL);
        }
        int packageCode = byteBuf.readInt();
        Class<?> packageClass;
        if(packageCode == PackageType.REQUEST_PACK.getCode()){
            packageClass = RpcRequest.class;
        }else if (packageCode == PackageType.RESPONSE_PACK.getCode()){
            packageClass = RpcResponse.class;
        }else{
            logger.error("不识别的数据包：{}" ,packageCode);
            throw new RpcException(RpcError.UNKNOWN_PACKAGE_TYPE);
        }
        int serializerCode = byteBuf.readInt();
        CommonSerializer commonSerializer = CommonSerializer.getByCode(serializerCode);
        if (commonSerializer == null){
            logger.error("不识别的反序列化器：{}" ,packageCode);
            throw new RpcException(RpcError.UNKNOWN_SERIALIZER);
        }
        int length = byteBuf.readInt();
        byte[] bytes = new byte[length];
        byteBuf.readBytes(bytes);
        //对数据进行反序列化
        Object obj = commonSerializer.deserialize(bytes,packageClass);
        list.add(obj);
    }
}
