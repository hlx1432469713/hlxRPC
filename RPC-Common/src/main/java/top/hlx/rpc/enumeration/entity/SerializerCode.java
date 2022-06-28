package top.hlx.rpc.enumeration.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 字节流识别序列化和反序列化器（代表四种序列化和反序列化的方式）
 */
@AllArgsConstructor
@Getter
public enum SerializerCode {

    KRYO(0),
    JSON(1),
    HESSIAN(2),
    PROTOBUF(3);
    private final int code;
}