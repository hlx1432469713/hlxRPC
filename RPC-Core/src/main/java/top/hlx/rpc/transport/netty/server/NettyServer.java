package top.hlx.rpc.transport.netty.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.hlx.rpc.codec.CommonDecoder;
import top.hlx.rpc.codec.CommonEncoder;
import top.hlx.rpc.enumeration.enumeration.RpcError;
import top.hlx.rpc.enumeration.exception.RpcException;
import top.hlx.rpc.hook.ShutdownHook;
import top.hlx.rpc.provider.ServiceProviderImpl;
import top.hlx.rpc.registry.NacosServiceRegistry;
import top.hlx.rpc.serializer.CommonSerializer;
import top.hlx.rpc.serializer.KryoSerializer;
import top.hlx.rpc.transport.AbstractRpcServer;

import java.net.InetSocketAddress;


/**
 * Netty服务器端
 */
public class NettyServer extends AbstractRpcServer {
    /**
     * 了解过 Netty 的同学可能知道，Netty 中有一个很重要的设计模式——责任链模式，责任链上有多个处理器，
     * 每个处理器都会对数据进行加工，并将处理后的数据传给下一个处理器。
     * 代码中的 CommonEncoder、CommonDecoder和NettyServerHandler 分别就是编码器，解码器和数据处理器。
     * 因为数据从外部传入时需要解码，而传出时需要编码，类似计算机网络的分层模型，每一层向下层传递数据时都要加上该层的信息，
     * 而向上层传递时则需要对本层信息进行解码。

     */
    private static final Logger logger = LoggerFactory.getLogger(NettyServer.class);

    private final CommonSerializer serializer;

    public NettyServer(String host,int port){
        this(host,port,DEFAULT_SERIALIZER);
    }

    public NettyServer(String host,int port,Integer serializer){
        this.host = host;
        this.port = port;
        serviceProvider = new ServiceProviderImpl();
        serviceRegistry = new NacosServiceRegistry();
        this.serializer = CommonSerializer.getByCode(serializer);
        ScanServices();
    }
    @Override
    public void start() {
        //这个主要是用来注册钩子-----用于在关闭服务端之后，注销Nacos中的所有服务的注册信息
        ShutdownHook.getShutdownHook().addClearAllHook();
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup,workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .option(ChannelOption.SO_BACKLOG,256)
                    .option(ChannelOption.SO_KEEPALIVE,true)
                    .childOption(ChannelOption.TCP_NODELAY,true)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            ChannelPipeline pipeline = socketChannel.pipeline();
                            pipeline.addLast(new CommonEncoder(new KryoSerializer()));
                            pipeline.addLast(new CommonDecoder());
                            pipeline.addLast(new NettyServerHandler());
                        }
                    });
                ChannelFuture future = serverBootstrap.bind(port).sync();
                future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
           logger.error("启动服务器时有错误发生：",e);
        }finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    /**
     * publishService 需要将服务保存在本地的注册表，同时注册到 Nacos 上。
     * 实现是注册完一个服务后直接调用 start() 方法，这是个不太好的实现……
     * 导致一个服务端只能注册一个服务，之后可以多注册几个然后再手动调用 start() 方法。
     * @param service
     * @param serviceClass
     * @param <T>
     */
    @Override
    public <T> void publishService(Object service, Class<T> serviceClass) {
        if(serializer == null){
            logger.error("未设置序列化器");
            throw new RpcException(RpcError.SERIALIZER_NOT_FOUND);
        }
        //将服务保存在本地的注册表上
        serviceProvider.addServiceProvider(service,serviceClass.getCanonicalName());
        //将服务注册到Nacos服务注册中心中
        serviceRegistry.register(serviceClass.getCanonicalName(),new InetSocketAddress(host,port));
        start();

    }
}
