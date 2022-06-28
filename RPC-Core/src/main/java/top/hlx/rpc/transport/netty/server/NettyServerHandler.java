package top.hlx.rpc.transport.netty.server;

import io.netty.channel.*;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.hlx.rpc.enumeration.entity.RpcRequest;
import top.hlx.rpc.enumeration.entity.RpcResponse;
import top.hlx.rpc.handler.RequestHandler;
import top.hlx.rpc.provider.ServiceProviderImpl;
import top.hlx.rpc.provider.ServiceProvider;


/**
 * Netty服务器端---处理客户端传来的调用请求
 */
public class NettyServerHandler extends SimpleChannelInboundHandler<RpcRequest> {
    /**
     *  NettyServerHandler 和 NettyClientHandler 都分别位于服务器端和客户端责任链的尾部，
     *  直接和 RpcServer 对象或 RpcClient 对象打交道，而无需关心字节序列的情况。
     *
     * NettyServerhandler 用于接收 RpcRequest，并且执行调用，将调用结果返回封装成 RpcResponse 发送出去。
     */
    private static final Logger logger = LoggerFactory.getLogger(NettyServerHandler.class);
    private static RequestHandler requestHandler;
    private static ServiceProvider serviceProvider;

    static {
        requestHandler = new RequestHandler();
        serviceProvider = new ServiceProviderImpl();
    }



    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcRequest rpcRequest) throws Exception {
       try {
           logger.info("服务器接收到请求：{}",rpcRequest);
           //接口名称
           String interfaceName = rpcRequest.getInterfaceName();
           Object service = serviceProvider.getServiceProvider(interfaceName);
           Object result = requestHandler.handle(rpcRequest,service);
           ChannelFuture future = channelHandlerContext.writeAndFlush(RpcResponse.success(result));
           future.addListener(ChannelFutureListener.CLOSE);
       }finally {
           ReferenceCountUtil.release(rpcRequest);
       }
    }
}
