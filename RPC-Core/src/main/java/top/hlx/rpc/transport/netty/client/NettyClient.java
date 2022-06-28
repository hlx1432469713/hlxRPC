package top.hlx.rpc.transport.netty.client;


import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.hlx.rpc.codec.CommonDecoder;
import top.hlx.rpc.codec.CommonEncoder;
import top.hlx.rpc.enumeration.entity.RpcRequest;
import top.hlx.rpc.enumeration.entity.RpcResponse;
import top.hlx.rpc.enumeration.enumeration.RpcError;
import top.hlx.rpc.enumeration.exception.RpcException;
import top.hlx.rpc.loadBalancer.LoadBalancer;
import top.hlx.rpc.loadBalancer.RandomLoadBalancer;
import top.hlx.rpc.registry.NacosServiceDiscovery;
import top.hlx.rpc.registry.NacosServiceRegistry;
import top.hlx.rpc.registry.ServiceDiscovery;
import top.hlx.rpc.registry.ServiceRegistry;
import top.hlx.rpc.serializer.CommonSerializer;
import top.hlx.rpc.serializer.JsonSerializer;
import top.hlx.rpc.serializer.KryoSerializer;
import top.hlx.rpc.transport.RpcClient;

import java.net.InetSocketAddress;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Netty客户端---发送请求，处理响应
 */
public class NettyClient implements RpcClient {
    private static final Logger logger = LoggerFactory.getLogger(NettyClient.class);
    
    private String host;
    private int port;
    private static final Bootstrap bootstrap;
    private final CommonSerializer serializer;
    private final ServiceDiscovery serviceDiscovery;

    public NettyClient(){
        this(DEFAULT_SERIALIZER,new RandomLoadBalancer());
    }
    public NettyClient(LoadBalancer loadBalancer){
      this(DEFAULT_SERIALIZER,loadBalancer);
    }

    public NettyClient(Integer serializer){
        this(serializer,new RandomLoadBalancer());
    }

    public NettyClient(Integer serializer,LoadBalancer loadBalancer){
        this.serviceDiscovery = new NacosServiceDiscovery(loadBalancer);
        this.serializer = CommonSerializer.getByCode(serializer);
    }

    static {
        EventLoopGroup group = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE,true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        ChannelPipeline pipeline = socketChannel.pipeline();
                        pipeline.addLast(new CommonDecoder())
                                .addLast(new CommonEncoder(new KryoSerializer()))
                                .addLast(new NettyClientHandler());
                    }
                });
    }

    /**
     * 没有Nacos注册中心之前
     * @param rpcRequest
     * @return
     */
//    @Override
//    public Object sendRequest(RpcRequest rpcRequest) {
//       try {
//           ChannelFuture future = bootstrap.connect(host,port).sync();
//           logger.info("客户端连接到服务器 {}:{}", host, port);
//           Channel channel = future.channel();
//           if (channel != null){
//               channel.writeAndFlush(rpcRequest).addListener(future1 ->{
//                   if (future1.isSuccess()){
//                       logger.info(String.format("客户端发送消息: %s", rpcRequest.toString()));
//                   }else{
//                       logger.error("发送消息时有错误发生: ", future1.cause());
//                   }
//               });
//               channel.closeFuture().sync();
//               AttributeKey<RpcResponse> key = AttributeKey.valueOf("rpcResponse");
//               RpcResponse rpcResponse = channel.attr(key).get();
//               return rpcResponse.getData();
//           }
//       } catch (InterruptedException e) {
//           e.printStackTrace();
//       }
//       return null;
//    }

    /**
     * 使用Nacos注册中心后
     * @param rpcRequest
     * @return
     */
    @Override
    public Object sendRequest(RpcRequest rpcRequest) {
        if (serializer == null){
            logger.error("未设置序列化器");
            throw new RpcException(RpcError.SERIALIZER_NOT_FOUND);
        }
        AtomicReference<Object> result = new AtomicReference<>(null);
        try {
                InetSocketAddress inetSocketAddress = serviceDiscovery.lookupService(rpcRequest.getInterfaceName());
                Channel channel = ChannelProvider.get(inetSocketAddress,serializer);
                logger.info("客户端连接到服务器 {}:{}", host, port);
                if (channel != null){
                   channel.writeAndFlush(rpcRequest).addListener(future1 ->{
                       if (future1.isSuccess()){
                           logger.info(String.format("客户端发送消息: %s", rpcRequest.toString()));
                       }else{
                           logger.error("发送消息时有错误发生: ", future1.cause());
                       }
                   });
                   channel.closeFuture().sync();
                   AttributeKey<RpcResponse> key = AttributeKey.valueOf("rpcResponse");
                   RpcResponse rpcResponse = channel.attr(key).get();
                   return rpcResponse.getData();
               }
           } catch (InterruptedException e) {
               e.printStackTrace();
           }
           return null;
    }
}
