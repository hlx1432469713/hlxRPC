package top.hlx.rpc.enumeration.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 *  Package Type：4字节（4B），表示标明这是一个调用请求还是调用响应
 */
@AllArgsConstructor
@Getter
public enum PackageType {

    REQUEST_PACK(0),
    RESPONSE_PACK(1);

    private final int code;

}
