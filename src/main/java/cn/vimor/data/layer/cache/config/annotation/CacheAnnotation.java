package cn.vimor.data.layer.cache.config.annotation;

import cn.vimor.data.layer.cache.config.enums.TypeEnums;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 缓存注释
 *
 * @author jianghai
 * @date 2022/05/24
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CacheAnnotation {

    /**
     * 缓存名称
     *
     * @return {@link String}
     */
    String cacheName() default "";

    /**
     * 处理
     *
     * @return {@link TypeEnums}
     */
    TypeEnums handle();

    /**
     * 到期
     *
     * @return long
     */
    long expires() default 100000L;
}
