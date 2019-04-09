package com.trend.core.util;
 

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;

import com.jcraft.jzlib.JZlib;
import com.jcraft.jzlib.ZInputStream;
import com.jcraft.jzlib.ZOutputStream;

 
public class ByteUtils
{
    /**
     * 读取输入流中指定字节的长度
     * <p/>
     * 输入流
     *
     * @param length 指定长度
     * @return 指定长度的字节数组
     */
    public static byte[] readByteArray(byte[] bytes, int from, int length)
    {
        byte[] sub = new byte[length];
        for (int i = 0; i < length; i++)
        {
            sub[i] = bytes[from + i];
        }
        return sub;
    }

    /**
     * byte[]转int
     *
     * @param bytes 报文
     * @return Integer
     */
    public static int readInt(byte[] bytes, int from)
    {
        return (int) ((((bytes[from + 3] & 0xff) << 24)
                    | ((bytes[from + 2] & 0xff) << 16)
                    | ((bytes[from + 1] & 0xff) << 8)
                    | ((bytes[from + 0] & 0xff) << 0)));
    }

    /**
     * byte[]转short
     *
     * @param bytes 报文
     * @return Integer
     */
    public static short readShort(byte[] bytes, int from)
    {
        return (short) (((bytes[from + 1] << 8) | bytes[from + 0] & 0xff));
    }

    // 从byte数组的index处的连续4个字节获得一个float
    public static float readFloat(byte[] bytes, int from)
    {
        int l;
        l = bytes[from + 0];
        l &= 0xff;
        l |= ((long) bytes[from + 1] << 8);
        l &= 0xffff;
        l |= ((long) bytes[from + 2] << 16);
        l &= 0xffffff;
        l |= ((long) bytes[from + 3] << 24);

        return Float.intBitsToFloat(l);
    }

    // 从byte数组的index处的连续8个字节获得一个long
    public static long readLong(byte[] bytes, int from) {
        return ((((long) bytes[from + 7] & 0xff) << 56)
                | (((long) bytes[from + 6] & 0xff) << 48)
                | (((long) bytes[from + 5] & 0xff) << 40)
                | (((long) bytes[from + 4] & 0xff) << 32)
                | (((long) bytes[from + 3] & 0xff) << 24)
                | (((long) bytes[from + 2] & 0xff) << 16)
                | (((long) bytes[from + 1] & 0xff) << 8)
                | (((long) bytes[from + 0] & 0xff) << 0));
    }

    // 从byte数组的index处的连续8个字节获得一个double
    public static double readDouble(byte[] bytes, int from)
    {
        long l;
        l = bytes[0 + from];
        l &= 0xff;
        l |= ((long) bytes[1 + from] << 8);
        l &= 0xffff;
        l |= ((long) bytes[2 + from] << 16);
        l &= 0xffffff;
        l |= ((long) bytes[3 + from] << 24);
        l &= 0xffffffffl;
        l |= ((long) bytes[4 + from] << 32);
        l &= 0xffffffffffl;
        l |= ((long) bytes[5 + from] << 40);
        l &= 0xffffffffffffl;
        l |= ((long) bytes[6 + from] << 48);
        l &= 0xffffffffffffffl;
        l |= ((long) bytes[7 + from] << 56);
        return Double.longBitsToDouble(l);
    }

    /**
     * 字节转字符串
     * @param bytes 报文
     */
    public  static String readString(byte[] bytes, int from , int length)
    {
        return new String(bytes, from, length);
    }
    
    
    public static byte[] double2Bytes(double d) {  
        long value = Double.doubleToRawLongBits(d);  
        byte[] byteRet = new byte[8];  
        for (int i = 0; i < 8; i++) {  
            byteRet[i] = (byte) ((value >> 8 * i) & 0xff);  
        }  
        return byteRet;  
    }  
    
    
    
    public static double bytes2Double(byte[] arr) {  
        long value = 0;  
        for (int i = 0; i < 8; i++) {  
            value |= ((long) (arr[i] & 0xff)) << (8 * i);  
        }  
        return Double.longBitsToDouble(value);  
    } 
    
    
    
    
    public  static String readString(byte[] bytes) throws UnsupportedEncodingException
    {
        return new String(bytes ,"utf-8");
    }

    /*
    * 从byte数组的index处的连续两个字节获得一个char
    */
    public static char readChar(byte[] bytes, int from)
    {
        char c = (char) (((bytes[from] & 0xFF) << 8) | (bytes[from + 1] & 0xFF));

        return c;
    }

     
    /**
     * short转换为byte[2]数组
     * @param intValue
     * @return
     * @author xcc
     */
    public static byte[] writeInt(int intValue)
    {
        byte[] bytes = new byte[4];

        bytes[3] = (byte) (intValue >>> 24);
        bytes[2] = (byte) (intValue >>> 16);
        bytes[1] = (byte) (intValue >>> 8);
        bytes[0] = (byte) (intValue);

        return bytes;
    }

     
    /**
     * short转换为byte[2]数组
     * @param shortValue
     * @return
     * @author xcc
     */
    public static byte[] writeShort(short shortValue)
    {
        byte[] bytes = new byte[2];

        bytes[1] = (byte) (shortValue >> 8);
        bytes[0] = (byte) (shortValue);

        return bytes;
    }

    /**
     * float转换为byte[4]数组
     * @param floatValue
     * @return
     * @author xcc
     */
    public static byte[] writeFloat(float floatValue)
    {
        byte[] bytes = new byte[4];

        int l = Float.floatToIntBits(floatValue);
        for (int i = 0; i < 4; i++) {
            bytes[i] = new Integer(l).byteValue();
            l = l >> 8;
        }

        return bytes;
    }

    
    /**
     * long转换为byte[8]数组
     * @param longValue
     * @return
     * @author xcc
     */
    public static byte[] writeLong(long longValue)
    {
        byte[] bytes = new byte[8];

        bytes[7] = (byte) (longValue >> 56);
        bytes[6] = (byte) (longValue >> 48);
        bytes[5] = (byte) (longValue >> 40);
        bytes[4] = (byte) (longValue >> 32);
        bytes[3] = (byte) (longValue >> 24);
        bytes[2] = (byte) (longValue >> 16);
        bytes[1] = (byte) (longValue >> 8);
        bytes[0] = (byte) (longValue >> 0);

        return bytes;
    }

    
    /**
     * double转换为byte[8]数组
     * @param doubleValue
     * @return
     * @author xcc
     */
    public static byte[] writeDouble(double doubleValue)
    {
        byte[] bytes = new byte[8];

        long l = Double.doubleToLongBits(doubleValue);
        for (int i = 0; i < 4; i++) {
            bytes[i] = new Long(l).byteValue();
            l = l >> 8;
        }

        return bytes;
    }

   
    /**
     * 将string 转换成 byte数组
     * @param s
     * @return
     * @author xcc
     */
    public static byte[] writeString(String s)
    {
        return s.getBytes();
    }

     
    /**
     * char转换为byte[2]数组
     * @param c
     * @return
     * @author xcc
     */
    public static byte[] writeChar(char c)
    {
        byte[] bytes = new byte[2];

        int temp = (int) c;
        for (int i = 0; i < 2; i ++ ) {
            bytes[i] = new Integer(temp & 0xff).byteValue(); // 将最高位保存在最低位
            temp = temp >> 8; // 向右移8位
        }

        return bytes;
    }

    /**
     * 拼接 byte 数组 
     * @param bytes
     * @return
     */
    public static byte[] combineByte(byte[] ... bytes)
    {
        int length = 0;
        for (byte[] b : bytes) {
            length+=b.length;
        }
        byte[] allByte=new byte[length];
        int pos=0;
        for (byte[] b : bytes) {
            System.arraycopy(b, 0, allByte, pos, b.length);
            pos+=b.length;
        }
        return allByte;
    }

    /**
     *
     * @description     将byte 数组压缩
     * @param bt
     * @return
     */
    public static byte[] compress(byte[] bt)
    {
        //将byte数据读入文件流
        byte[] bytes = bt;

        try
        {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ZOutputStream zOut = new ZOutputStream(out,
                    JZlib.Z_DEFAULT_COMPRESSION);
            DataOutputStream objOut = new DataOutputStream(zOut);
            objOut.write(bt);
            objOut.flush();
            zOut.close();
            bytes = out.toByteArray();
            out.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return bytes;
    }

    /**
     *
     * @description     解压缩byte数组
     * @param bt
     * @return
     */
    public static byte[] unCompress(byte[] bt)
    {
        byte[] bytes = bt;

        try
        {
            ByteArrayInputStream in = new ByteArrayInputStream(bt);
            ZInputStream zIn = new ZInputStream(in);
            byte[] buf = new byte[1024];
            int num = -1;
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            while ((num = zIn.read(buf, 0, buf.length)) != -1) {
                baos.write(buf, 0, num);
            }
            bytes = baos.toByteArray();
            baos.flush();
            baos.close();
            zIn.close();
            in.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return bytes;
    }

    public static void decode(byte[] bytes)
    {
        if(bytes != null){
            for(int i = 0; i<bytes.length; i++)
            {
                bytes[i] ^= 0xFF;
            }
        }
    }

    public static void encode(byte[] bytes)
    {
        if(bytes!=null)
        {
            for(int i = 0; i<bytes.length; i++)
            {
                bytes[i] ^= 0xFF;
            }
        }
    }
    
    /**
     * byte数组转成 string
     * @param buffer
     * @return
     * @author xcc
     */
    public static String byte2hex(byte [] buffer){  
        String h = "";  
          
        for(int i = 0; i < buffer.length; i++){  
            String temp = Integer.toHexString(buffer[i] & 0xFF);  
            if(temp.length() == 1){  
                temp = "0" + temp;  
            }  
            h = h + " "+ temp;  
        }  
          
        return h;  
          
    }
}
