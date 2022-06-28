package top.hlx.rpc.serializer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.hlx.rpc.enumeration.entity.RpcRequest;
import top.hlx.rpc.enumeration.entity.SerializerCode;

import java.io.IOException;


/**
 * JSON序列化器-----序列化器之一
 *
 * 基于 JSON 的序列化器有一个毛病，就是在某个类的属性反序列化时，如果属性声明为 Object 的，
 * 就会造成反序列化出错，通常会把 Object 属性直接反序列化成 String 类型，就需要其他参数辅助序列化。
 * 并且，JSON 序列化器是基于字符串（JSON 串）的，占用空间较大且速度较慢。
 */
public class JsonSerializer implements CommonSerializer{
    private Logger logger = LoggerFactory.getLogger(JsonSerializer.class);

    /**
     * ObjectMapper类是Jackson的主要类，它可以帮助我们快速的进行各个类型和Json类型的相互转换。
     */
    private ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 序列化 : 把对象翻译成字节数组
     * @param object
     * @return
     */
    @Override
    public byte[] serialize(Object object) {
        try {
            return objectMapper.writeValueAsBytes(object);
        } catch (JsonProcessingException e) {
            logger.error("序列化时有错误发生：{}",e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 反序列化：根据字节数组和Class反序列为对象
     * @param bytes
     * @param clazz
     * @return
     */
    @Override
    public Object deserialize(byte[] bytes, Class<?> clazz) {
        try {
            Object obj = objectMapper.readValue(bytes,clazz);
            //isAssignableFrom()方法是从类继承的角度去判断，instanceof关键字是从实例继承的角度去判断。
            //isAssignableFrom()方法是判断是否为某个类的父类，instanceof关键字是判断是否某个类的子类。
            if(obj instanceof RpcRequest){
                obj = handleRequest(obj);
            }
            return obj;
        } catch (IOException e) {
            logger.error("反序列化有错误发生 ：{}",e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public int getCode() {
        return SerializerCode.JSON.getCode();
    }

    /**
     * 。这里有一个需要注意的点，就是在 RpcRequest 反序列化时，由于其中有一个字段是 Object 数组，
     * 在反序列化时序列化器会根据字段类型进行反序列化，而 Object 就是一个十分模糊的类型，会出现反序列化失败的现象，
     * 这时就需要 RpcRequest 中的另一个字段 ParamTypes 来获取到 Object 数组中的每个实例的实际类，辅助反序列化，
     * 这就是 handleRequest() 方法的作用。
     *
     * 上面提到的这种情况不会在其他序列化方式中出现，因为其他序列化方式是转换成字节数组，会记录对象的信息，
     * 而 JSON 方式本质上只是转换成 JSON 字符串，会丢失对象的类型信息。
     */
    private Object handleRequest(Object obj) throws IOException {
        RpcRequest rpcRequest = (RpcRequest) obj;
        for (int i = 0; i < rpcRequest.getParamTypes().length; i++) {
            Class<?> clazz = rpcRequest.getParamTypes()[i];
            //isAssignableFrom()方法是从类继承的角度去判断，instanceof关键字是从实例继承的角度去判断。
            //isAssignableFrom()方法是判断是否为某个类的父类，instanceof关键字是判断是否某个类的子类。
            //如果此时参数类型（类）和参数名称（类）不匹配--对参数名称（类）重新进行序列化和反序列
            if(!clazz.isAssignableFrom(rpcRequest.getParameters()[i].getClass())){
                //序列化
                byte[] bytes = objectMapper.writeValueAsBytes(rpcRequest.getParameters()[i]);
                rpcRequest.getParameters()[i] = objectMapper.readValue(bytes,clazz);
            }
        }
        return rpcRequest;
    }
}
