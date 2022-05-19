package top.hlx.rpc;

/**
 * HelloService是一个通用的接口，服务端和客户端都能访问的到
 */
public interface HelloService {
    String hello(HelloObject helloObject);
}
