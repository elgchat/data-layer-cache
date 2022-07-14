package cn.vimor.data.layer.cache.config.base;

import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 基本的映射器
 *
 * @author Jani
 * @date 2022/06/20
 */
public interface BaseMapper<T> {

    /**
     * 插入
     *
     * @param t t
     * @return int
     */
    int insert(T t);


    /**
     * 选择通过id
     *
     * @param id id
     * @return {@link T}
     */
    T selectById(@Param("id") Long id);


    /**
     * 更新
     *
     * @param t t
     * @return int
     */
    int update(T t);


    /**
     * 选择所有id
     *
     * @param ids id
     * @return {@link List}<{@link T}>
     */
    List<T> selectAllById(@Param("ids") List<Long> ids);

}
