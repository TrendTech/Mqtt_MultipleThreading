package com.trend.core.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * vo与字节数组的相互转换器
 * @author lbf
 *
 * @param <T>
 */
class ClazzConvertor<T> {
	private static final Logger logger = Logger.getLogger(ClazzConvertor.class);
	
	private Class<T> clazz;
	private FieldConvertor[] fieldConvertors;

	public ClazzConvertor(Class<T> clazz) {
		this.clazz = clazz;
		initFieldConvertors();
	}
	/**
	 * 创建各个域的转换器
	 */
	private void initFieldConvertors(){
		Field[] fields = clazz.getDeclaredFields();
		fieldConvertors = new FieldConvertor[fields.length];
		for (int i = 0; i < fields.length; i++) {
			Field field = fields[i];
			JSONField jsonFieldAnnotation = field.getAnnotation(JSONField.class);
			if (jsonFieldAnnotation == null) {
				continue;
			}
			int index = jsonFieldAnnotation.ordinal();
			
			Class<?> fieldType = field.getType();
			if (String.class == fieldType) {
				fieldConvertors[index] = new StringFieldConvertor(field);
				continue;
			}
			if (Integer.TYPE == fieldType||Integer.class.equals(fieldType)) {
				fieldConvertors[index] = new IntFieldConvertor(field);
				continue;
			}
			if (Short.TYPE == fieldType||Short.class.equals(fieldType)) {
				fieldConvertors[index] = new ShortFieldConvertor(field);
				continue;
			}
			if (Long.TYPE == fieldType||Long.class.equals(fieldType)) {
				fieldConvertors[index] = new LongFieldConvertor(field);
				continue;
			}
			if (Float.TYPE == fieldType) {
				fieldConvertors[index] = new FloatFieldConvertor(field);
				continue;
			}
			if (BigDecimal.class == fieldType) {
				fieldConvertors[index] = new BigDecimalFieldConvertor(field);
				continue;
			}
			if (Date.class == fieldType) {
				fieldConvertors[index] = new DateFieldConvertor(field);
				continue;
			}
			if (List.class == fieldType) {
				fieldConvertors[index] = new ListFieldConvertor(field);
				continue;
			}
			if(BigString.class.equals(fieldType)){
				fieldConvertors[index] = new BigStringFieldConvertor(field);
				continue;
			}
			
			logger.error(String.format("类（%s）中的域（%s）类型(%s)不符合规范", clazz.getName(),field.getName(),fieldType.getName()));
		}
		
	}
	/**
	 * 字节数组转换成vo对象
	 * @param bytes 字节数组
	 * @param from 游标起点
	 * @return ConvertResult 转换结果，包含已转换的vo和当前字节数组的游标位置
	 */
	public ConvertResult<T> bytesToVo(byte[] bytes,int from){
		T t = null;
		try {
			t = clazz.newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		for (int i = 0; i < fieldConvertors.length; i++) {
			FieldConvertor fieldConvertor = fieldConvertors[i];
			if (fieldConvertor == null) {
				continue;
			}
			from = fieldConvertor.bytesToVo(bytes, from, t);
		}
		return new ConvertResult<T>(from, t);
	}
	/**
	 * vo转成字节数组
	 * @param t 待转vo对象
	 * @return
	 */
	public byte[] voToBytes(T t){
		List<Byte> byteList = new ArrayList<Byte>();
		for (int i = 0; i < fieldConvertors.length; i++) {
			FieldConvertor fieldConvertor = fieldConvertors[i];
			if (fieldConvertor == null) {
				continue;
			}
			fieldConvertor.voToBytes(byteList, t);
		}
		//List<Byte>转成byte[]
		byte[] bytes = new byte[byteList.size()];
		for (int i = 0; i < bytes.length; i++) {
			bytes[i] = byteList.get(i);
		}
		return bytes;
	}
//==================================================================	
	/**
	 * 域转换器 接口
	 * @author lbf
	 *
	 */
	public interface FieldConvertor{
		/**
		 * 从字节数组中获取字节数据并转换为相应域的类型，注入对象相应的域中
		 * @param bytes 字节数组
		 * @param from 游标起点
		 * @param t 被注入的对象
		 * @return 下一个游标起点
		 */
		public int bytesToVo(byte[] bytes, int from, Object t);
		/**
		 * 获取对象中域的值并转换为字节，插入到字节list中
		 * @param bytes 字节list
		 * @param t 数据对象
		 */
		public void voToBytes(List<Byte> bytes, Object t);
	}
//=======================================================
	/**
	 * 域转换器基础抽象类
	 * @author lbf
	 *
	 */
	public static abstract class AbstractFieldConvertor implements FieldConvertor{
		private static final Logger logger = Logger.getLogger(AbstractFieldConvertor.class);
		protected final Field field;
		protected final Method reader;
		protected final Method writer;
		public AbstractFieldConvertor(Field field){
			this.field = field;
			this.reader = ReflectUtil.getReader(field);
			this.writer = ReflectUtil.getWriter(field);
		}
		/**
		 * 读取给定对象中该域的值
		 * @param t
		 * @return
		 */
		protected Object readFieldValue(Object t){
			Object fieldValueObject = null;
			try {
				fieldValueObject = reader.invoke(t);
			} catch (Exception e) {
				logger.error(String.format("读取(%s)的域(%s)出现错误", field.getDeclaringClass().getName(),field.getName()));
			}
			return fieldValueObject;
		}
		/**
		 * 将值写入给定对象相应的域中
		 * @param t
		 * @param fieldValue
		 */
		protected void writeFieldValue(Object t,Object fieldValue){
			try {
				writer.invoke(t, fieldValue);
			} catch (Exception e) {
				logger.error(String.format("写入(%s)的域(%s)出现错误", field.getDeclaringClass().getName(),field.getName()));
			}
		}
	}
//============================================================================
	
	public static class IntFieldConvertor extends AbstractFieldConvertor implements FieldConvertor{

		public IntFieldConvertor( Field field) {
			super(field);
		}

		@Override
		public int bytesToVo(byte[] bytes, int from, Object t) {
			int redult = ByteUtils.readInt(bytes, from);
			super.writeFieldValue(t, redult);
			return from + 4;
		}

		@Override
		public void voToBytes(List<Byte> bytes, Object t) {
			Object fieldValue = super.readFieldValue(t);
			if(fieldValue==null){
				fieldValue=0;
			}
			byte[] resultBytes = ByteUtils.writeInt(Integer.valueOf(fieldValue.toString()));
			for (int i = 0; i < resultBytes.length; i++) {
				bytes.add(resultBytes[i]);
			}
		}
	}
	
	public static class ShortFieldConvertor extends AbstractFieldConvertor implements FieldConvertor{

		public ShortFieldConvertor(Field field) {
			super(field);
		}

		@Override
		public int bytesToVo(byte[] bytes, int from, Object t) {
			short result = ByteUtils.readShort(bytes, from);
			super.writeFieldValue(t,result);
			return from + 2;
		}

		@Override
		public void voToBytes(List<Byte> bytes, Object t) {
			Object fieldValue = super.readFieldValue(t);
			if(fieldValue==null){
				fieldValue=0;
			}
			byte[] resultBytes = ByteUtils.writeShort(Short.valueOf(fieldValue.toString()));
			for (int i = 0; i < resultBytes.length; i++) {
				bytes.add(resultBytes[i]);
			}
		}
		
	}
	
	public static class LongFieldConvertor extends AbstractFieldConvertor implements FieldConvertor{
		public LongFieldConvertor(Field field) {
			super(field);
		}

		@Override
		public int bytesToVo(byte[] bytes, int from, Object t) {
			long result = ByteUtils.readLong(bytes, from);
			super.writeFieldValue(t,result);
			return from + 8;
		}
		
		@Override
		public void voToBytes(List<Byte> bytes, Object t) {
			Object fieldValue = super.readFieldValue(t);
			if(fieldValue==null){
				fieldValue=0;
			}
			byte[] resultBytes = ByteUtils.writeLong(Long.valueOf(fieldValue.toString()));
			for (int i = 0; i < resultBytes.length; i++) {
				bytes.add(resultBytes[i]);
			}
		}
		
	}
	
	public static class FloatFieldConvertor extends AbstractFieldConvertor implements FieldConvertor{
		public FloatFieldConvertor(Field field) {
			super(field);
		}

		@Override
		public int bytesToVo(byte[] bytes, int from, Object t) {
			float result = ByteUtils.readFloat(bytes, from);
			super.writeFieldValue(t,result);
			return from + 4;
		}

		@Override
		public void voToBytes(List<Byte> bytes, Object t) {
			Object fieldValue = super.readFieldValue(t);
			byte[] resultBytes = ByteUtils.writeFloat(Float.valueOf(fieldValue.toString()));
			for (int i = 0; i < resultBytes.length; i++) {
				bytes.add(resultBytes[i]);
			}
		}
	}
	
	public static class BigDecimalFieldConvertor extends AbstractFieldConvertor implements FieldConvertor{
		public BigDecimalFieldConvertor(Field field) {
			super(field);
		}
		
		@Override
		public int bytesToVo(byte[] bytes, int from, Object t) {
			float result = ByteUtils.readFloat(bytes, from);
			super.writeFieldValue(t,new BigDecimal(String.valueOf(result)));
			return from + 4;
		}
		
		@Override
		public void voToBytes(List<Byte> bytes, Object t) {
			Object fieldValue = super.readFieldValue(t);
			fieldValue = fieldValue == null ? "0" : fieldValue;
			byte[] resultBytes = ByteUtils.writeFloat(Float.valueOf(fieldValue.toString()));
			for (int i = 0; i < resultBytes.length; i++) {
				bytes.add(resultBytes[i]);
			}
		}
	}
	
	public static class StringFieldConvertor extends AbstractFieldConvertor implements FieldConvertor{
		
		public StringFieldConvertor(Field field) {
			super(field);
		}

		@Override
		public int bytesToVo(byte[] bytes, int from, Object t) {
			short length = ByteUtils.readShort(bytes, from);
			from += 2;
			if (length == 0) {
				return from;
			}
			String result = ByteUtils.readString(bytes, from, length);
			super.writeFieldValue(t,result);
			return from + length;
		}
		
		@Override
		public void voToBytes(List<Byte> bytes, Object t) {
			Object fieldValue = super.readFieldValue(t);
			if (fieldValue == null) {
				byte[] lengthBytes = ByteUtils.writeShort((short)0);
				for (int i = 0; i < lengthBytes.length; i++) {
					bytes.add(lengthBytes[i]);
				}
				return;
			}
			byte[] resultBytes = ByteUtils.writeString(String.valueOf(fieldValue));
			byte[] lengthBytes = ByteUtils.writeShort((short)resultBytes.length);
			for (int i = 0; i < lengthBytes.length; i++) {
				bytes.add(lengthBytes[i]);
			}
			for (int i = 0; i < resultBytes.length; i++) {
				bytes.add(resultBytes[i]);
			}
		}
		
	}
	
	public static class DateFieldConvertor extends AbstractFieldConvertor implements FieldConvertor{
		
		public DateFieldConvertor(Field field) {
			super(field);
		}

		@Override
		public int bytesToVo(byte[] bytes, int from, Object t) {
			long result = ByteUtils.readLong(bytes, from);
			Date date = new Date(result);
			super.writeFieldValue(t, date);
			return from + 8;
		}
		
		@Override
		public void voToBytes(List<Byte> bytes, Object t) {
			Object fieldValue = super.readFieldValue(t);
			Date date = (Date) fieldValue;
			//如果日期为null则写入0
			long milliseconds  = date == null ? 0L : date.getTime();
			byte[] resultBytes = ByteUtils.writeLong(milliseconds);
			for (int i = 0; i < resultBytes.length; i++) {
				bytes.add(resultBytes[i]);
			}
		}
		
	}
	
	public static class ListFieldConvertor extends AbstractFieldConvertor implements FieldConvertor{
		private static final Logger logger = Logger.getLogger(ListFieldConvertor.class);
		public ListFieldConvertor(Field field) {
			super(field);
		}

		@Override
		public int bytesToVo(byte[] bytes, int from, Object t) {
			short listLength = ByteUtils.readShort(bytes, from);
			from += 2;
			if (listLength == 0) {
				return from;
			}
			Type type = field.getGenericType();
			if (!(type instanceof ParameterizedType)) {
				logger.error(String.format("类（%s）的域（%s）为List类型必须定义泛型", field.getDeclaringClass().getName(),field.getName()));
				throw new RuntimeException(String.format("类（%s）的域（%s）为List类型必须定义泛型", field.getDeclaringClass().getName(),field.getName()));
			}
			ParameterizedType pt = (ParameterizedType) type;
			Class<?> genericClazz = (Class<?>)pt.getActualTypeArguments()[0];
			List<Object> fieldValue = new ArrayList<>();
			for (int i = 0; i < listLength; i++) {
				ConvertResult<?> convertResult = ConvertUtil.bytesToConvertResult(bytes, from, genericClazz);
				fieldValue.add(convertResult.getVo());
				from = convertResult.getFrom();
			}
			super.writeFieldValue(t, fieldValue);
			return from;
		}
		
		@Override
		public void voToBytes(List<Byte> bytes, Object t) {
			Object fieldValue = super.readFieldValue(t);
			int length = 0;
			List<?> list = null;
			if (fieldValue != null) {
				list = (List<?>)fieldValue;
				length = list.size();
			}
			
			byte[] lengthBytes = ByteUtils.writeShort((short) length);
			for (int i = 0; i < lengthBytes.length; i++) {
				bytes.add(lengthBytes[i]);
			}
			//list为null或者list.size()==0 说明没有具体数据内容，直接返回
			if (length == 0) {
				return;
			}
			for (int i = 0; i < list.size(); i++) {
				Object ele = list.get(i);
				byte[] eleBytes = ConvertUtil.voToBytes(ele);
				for (int j = 0; j < eleBytes.length; j++) {
					bytes.add(eleBytes[j]);
				}
			}
		}
		
	}
	
public static class BigStringFieldConvertor extends AbstractFieldConvertor implements FieldConvertor{
		
		public BigStringFieldConvertor(Field field) {
			super(field);
		}

		@Override
		public int bytesToVo(byte[] bytes, int from, Object t) {
			int length = ByteUtils.readInt(bytes, from);
			from += 4;
			if (length == 0) {
				return from;
			}
			byte[] result = ByteUtils.readByteArray(bytes, from, length);
			((BigString)t).setContent(new String(result));
			//super.writeFieldValue(t,result);
			return from + length;
		}
		
		@Override
		public void voToBytes(List<Byte> bytes, Object t) {
			Object fieldValue = super.readFieldValue(t);
			if (fieldValue == null||fieldValue.toString()==null||fieldValue.equals("")) {
				byte[] lengthBytes = ByteUtils.writeInt(0);
				for (int i = 0; i < lengthBytes.length; i++) {
					bytes.add(lengthBytes[i]);
				}
				return;
			}
			byte[] resultBytes = ((BigString)fieldValue).compress();
			byte[] lengthBytes = ByteUtils.writeInt(resultBytes.length);
			for (int i = 0; i < lengthBytes.length; i++) {
				bytes.add(lengthBytes[i]);
			}
			for (int i = 0; i < resultBytes.length; i++) {
				bytes.add(resultBytes[i]);
			}
		}
	}
	
}
