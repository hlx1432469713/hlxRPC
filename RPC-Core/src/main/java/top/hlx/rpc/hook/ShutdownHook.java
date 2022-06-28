package top.hlx.rpc.hook;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.hlx.rpc.enumeration.factory.ThreadPoolFactory;
import top.hlx.rpc.enumeration.util.NacosUtil;


/**
 *  钩子类
 */
public class ShutdownHook {

    /**
     * 使用了单例模式创建其对象，在 addClearAllHook 中，Runtime 对象是 JVM 虚拟机的运行时环境，调用其 addShutdownHook 方法增加一个钩子函数，
     * 创建一个新线程调用 clearRegistry 方法完成注销工作。这个钩子函数会在 JVM 关闭之前被调用。
     */
    private static final Logger logger = LoggerFactory.getLogger(ShutdownHook.class);

    private static final ShutdownHook shutdownHook = new ShutdownHook();

    public static ShutdownHook getShutdownHook(){
        return shutdownHook;
    }

    public void addClearAllHook(){
        logger.info("关闭后将自动注销所有服务");
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            NacosUtil.clearRegistry();
            ThreadPoolFactory.shutDownAll();
        }));
    }
}
