package top.hlx.rpc;

import lombok.AllArgsConstructor;
import lombok.Data;


import java.io.Serializable;

/**
 * @author hlx
 * @time 2022-3-22
 * message ： 需要实现Serializable接口，因为该类在  调用过程中   从客户端传递给服务端
 *   @Data ： 注在类上，提供类的get、set、equals、hashCode、canEqual、toString方法
 *   @AllArgsConstructor: 使用后添加一个构造函数，该构造函数含有所有已声明字段属性参数
 *   @NoArgsConstructor ： 注在类上，提供类的无参构造
 */
@Data
@AllArgsConstructor
public class HelloObject implements Serializable {
    private Integer id;
    private String message;
}
