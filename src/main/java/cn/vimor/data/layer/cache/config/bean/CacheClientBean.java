package cn.vimor.data.layer.cache.config.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 缓存客户端bean
 * 缓存客户端
 *
 * @author Jani
 * @date 2022/06/20
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CacheClientBean {

    /**
     * 包名
     */
    private String packageName;

    /**
     * 方法名称
     */
    private String methodName;

    /**
     * 参数名称
     */
    private String[] parameterNames;

    /**
     * arg游戏
     */
    private Object[] args;
}
