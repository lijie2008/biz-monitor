package com.huntkey.rx.sceo.monitor.commom.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * 
 * ClassName: SerializeUtils
 * Date: 2017年8月3日 上午9:42:12
 * @author lijie
 * @version
 */
public class SerializeUtils {

   /**
    * 
    * deserialize: 反序列化
    * @author lijie
    * @param bytes
    * @return
    * @throws Exception
    */
    public static Object deserialize(byte[] bytes) throws Exception{
        ByteArrayInputStream byteInt=new ByteArrayInputStream(bytes);
        ObjectInputStream objInt=new ObjectInputStream(byteInt);
        return objInt.readObject();
    }

   /**
    * 
    * serialize:序列化
    * @author lijie
    * @param o
    * @return
    * @throws Exception
    */
    public static byte[] serialize(Object o) throws Exception{
        ByteArrayOutputStream byt=new ByteArrayOutputStream();
        ObjectOutputStream obj=new ObjectOutputStream(byt);
        obj.writeObject(o);
        return byt.toByteArray();
    }
}
