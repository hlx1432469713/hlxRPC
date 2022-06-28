package top.hlx.test;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.hlx.rpc.HelloObject;
import top.hlx.rpc.HelloService;
import top.hlx.rpc.annotation.Service;

/**
 *   HelloService的实现类，该类只存在于服务端中，客户端只能通过去调用服务端中的实现类才能获取结果
 */
@Service
public class HelloServiceImpl implements HelloService {
    /**
     * static修饰的属性强调它们只有一个，
     * final修饰的属性表明是一个常数（创建后不能被修改）。
     * static final修饰的属性表示一旦给值，就不可修改，并且可以通过类名访问。
     */
    private static final Logger logger = LoggerFactory.getLogger(HelloServiceImpl.class);
    @Override
    public String hello(HelloObject helloObject) {
        logger.info("接收到：{}",helloObject.getMessage());
        return "调用成功，返回Id = " + helloObject.getId();
    }
}
