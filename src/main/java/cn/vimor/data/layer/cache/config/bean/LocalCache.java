package cn.vimor.data.layer.cache.config.bean;

import com.alibaba.fastjson.JSONObject;

import java.io.Serializable;

/**
 * 本地缓存
 *
 * @author Jani
 * @date 2022/06/20
 */
public class LocalCache implements Serializable {

    private static final long serialVersionUID = -5068741196032673365L;

    /**
     * 时间戳
     */
    private final long timestamp = System.currentTimeMillis();

    /**
     * id
     */
    private Long id;
    /**
     * 转换类
     */
    private Class<?> conversionClass;
    /**
     * 值
     */
    private String value;

    public LocalCache() {
    }

    public LocalCache(Long id, Class<?> conversionClass, Object value) {
        this.id = id;
        this.conversionClass = conversionClass;
        this.value = JSONObject.toJSONString(value);
    }

    public long getTimestamp() {
        return timestamp;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Class<?> getConversionClass() {
        return conversionClass;
    }

    public void setConversionClass(Class<?> conversionClass) {
        this.conversionClass = conversionClass;
    }
}
