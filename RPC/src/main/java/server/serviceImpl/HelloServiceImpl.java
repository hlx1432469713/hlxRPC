package server.serviceImpl;

import object.HelloObject;
import server.service.HelloService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *   HelloService的实现类
 */
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
