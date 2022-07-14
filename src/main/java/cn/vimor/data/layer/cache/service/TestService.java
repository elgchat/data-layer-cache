package cn.vimor.data.layer.cache.service;

import cn.vimor.data.layer.cache.entity.UserInfoEntity;

import java.util.List;

/**
 * 测试服务
 *
 * @author Jani
 * @date 2022/06/14
 */
public interface TestService {

    /**
     * 插入
     *
     * @return {@link String}
     */
    String insert();

    /**
     * 找到所有
     *
     * @return {@link List}<{@link UserInfoEntity}>
     */
    List<UserInfoEntity> findAll();

    /**
     * 找到一个
     *
     * @param id id
     * @return {@link UserInfoEntity}
     */
    UserInfoEntity findOne(Long id);


    /**
     * 更新
     *
     * @param username 用户名
     * @param id       id
     */
    void update(String username, Long id);

    /**
     * 发现按年龄
     *
     * @param age 年龄
     * @return {@link List}<{@link UserInfoEntity}>
     */
    List<UserInfoEntity> findByAge(Integer age);

    Object testNoCache(Integer age);
}
