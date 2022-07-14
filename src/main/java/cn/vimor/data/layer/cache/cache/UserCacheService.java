package cn.vimor.data.layer.cache.cache;

import cn.vimor.data.layer.cache.config.base.BaseCacheService;
import cn.vimor.data.layer.cache.entity.UserInfoEntity;

import java.util.List;

/**
 * 用户缓存服务
 *
 * @author Jani
 * @date 2022/06/15
 */
public interface UserCacheService extends BaseCacheService<UserInfoEntity> {


    /**
     * 找到所有年龄
     *
     * @param age 年龄
     * @return {@link List}<{@link Integer}>
     */
    List<UserInfoEntity> findByAge(Integer age);


    /**
     * 找到所有
     *
     * @return {@link List}<{@link UserInfoEntity}>
     */
    List<UserInfoEntity> findAll();

}
