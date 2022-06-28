package top.hlx.rpc.serializer;

/**
 * 通用的序列化 和 反序列化 接口
 */
public interface CommonSerializer {

    Integer KRYO_SERIALIZER = 0;
    Integer JSON_SERIALIZER = 1;
    Integer HESSIAN_SERIALIZER = 2;
    Integer PROTOBUF_SERIALIZER = 3;

    Integer DEFAULT_SERIALIZER = KRYO_SERIALIZER;
    /**
     * 序列化
     * @param object
     * @return
     */
    byte[] serialize(Object object);

    /**
     * 反序列化
     * @param bytes
     * @param clazz
     * @return
     */
    Object deserialize(byte[] bytes,Class<?> clazz);

    /**
     * 获得该序列化器的编号
     * @return
     */
    int getCode();


    /**
     * 根据编号获取序列化器
     * @param code
     * @return
     */
    static CommonSerializer getByCode(int code){
        switch (code){
            case 0 :
                return new KryoSerializer();
            case 1 :
                return new JsonSerializer();
            default:
                return new KryoSerializer();
        }
    }

}
