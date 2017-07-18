/**
 * <p><owner>10208178</owner> </p>
 * <p><createdate>2017/6/29</createdate></p> 
 * <p>文件名称: JsonBeanConverter.java</p>
 * <p>文件描述: 无</p>
 * <p>版权所有: 版权所有(C)2001-2020</p>
 * <p>公司名称: 深圳市中兴通讯股份有限公司</p>
 * <p>内容摘要: 无</p>
 * <p>其他说明: 无</p>
 * <p>创建日期：2017/6/29</p>
 * <p>完成日期：2017/6/29</p>
 * <p>修改记录1: // 修改历史记录，包括修改日期、修改者及修改内容</p>
 * <pre>
 *    修改日期：
 *    版 本 号：
 *    修 改 人：
 *    修改内容：
 * </pre>
 * <p>评审记录1: // 评审历史记录，包括评审日期、评审人及评审内容</p>
 * <pre>
 *    评审日期：
 *    版 本 号：
 *    评 审 人：
 *    评审内容：
 * </pre>
 * @version 1.0
 * @author 周明
 */
package com.zte.hacker.common.json;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.type.TypeFactory;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Map;

/**
 * 功能描述:<br>
 * <p/>
 * <p/>
 * <p/>
 * Note:
 *
 * @author 10208178
 * @version 1.0
 */
public class JsonBeanConverter
{
    private static final ObjectMapper mapper = new ObjectMapper();

    private JsonBeanConverter()
    {

    }

    /**
     * 将一个JavaBean转成一个JsonString<br>
     * 这个bean的字段如果不是public，就需要getter、setter函数<br>
     * bean必须要有默认的构造函数
     *
     * @param bean
     * @return
     * @throws Exception
     */
    public static final String convertBeanToJsonStr(Object bean) throws IOException
    {
        String jsonValue = "Error to convert bean to Json String";

        jsonValue = mapper.writeValueAsString(bean);
        return jsonValue;
    }

    public static final Object convertJsonStrToBean(String jsonStr, Class<?> clazz)
        throws IOException
    {
        Object bean = null;

        bean = mapper.readValue(jsonStr, clazz);
        return bean;
    }

    public static final Object convertJsonStrToBean(String jsonStr, Type type) throws IOException
    {
        return mapper.readValue(jsonStr, TypeFactory.type(type));
    }

    /**
     * 将一个（按照Collection.class转化为json的）jsonString转为Collection<YourBean>
     *
     * @param jsonStr
     * @param collection
     * @param clazz
     * @return
     */
    public static final Object convertJsonStrToBean(String jsonStr,
        Class<? extends Collection> collection, Class<?> clazz) throws IOException
    {
        return mapper.readValue(jsonStr, TypeFactory.collectionType(collection, clazz));
    }

    /**
     * 将一个（按照Map.class转化为json的）jsonString转为map<YourKeyBean,YourValueBean>
     *
     * @param jsonStr
     * @param mapType
     * @param keyType
     * @param valueType
     * @return
     * @throws Exception
     */
    public static final Object convertJsonStrToBean(String jsonStr, Class<? extends Map> mapType,
        Class<?> keyType, Class<?> valueType) throws IOException
    {
        return mapper.readValue(jsonStr, TypeFactory.mapType(mapType, keyType, valueType));
    }
}