package cn.vimor.data.layer.cache.config.base;

import cn.vimor.data.layer.cache.entity.UserInfoEntity;

import java.util.List;

/**
 * 基本的缓存服务
 *
 * @author Jani
 * @date 2022/06/15
 */
public interface BaseCacheService<T> {

    /**
     * 插入
     *
     * @param t t
     * @return {@link Integer}
     */
    Integer insert(T t);


    /**
     * 找到一个
     *
     * @param id id
     * @return {@link UserInfoEntity}
     */
    T findOne(Long id);


    /**
     * 更新
     *
     * @param t t
     * @return int
     */
    int update(T t);


    /**
     * 找到所有id
     *
     * @param id id
     * @return {@link List}<{@link T}>
     */
    List<T> findAllById(List<Long> id);

}
