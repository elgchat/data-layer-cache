package cn.vimor.data.layer.cache.cache.impl;

import cn.vimor.data.layer.cache.cache.UserCacheService;
import cn.vimor.data.layer.cache.config.base.BaseCacheServiceImpl;
import cn.vimor.data.layer.cache.entity.UserInfoEntity;
import cn.vimor.data.layer.cache.mapper.UserMapper;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 用户缓存服务实现类
 *
 * @author Jani
 * @date 2022/06/15
 */
@Service
public class UserCacheServiceImpl extends BaseCacheServiceImpl<UserInfoEntity, UserMapper, UserCacheServiceImpl> implements UserCacheService {

    @Resource
    private UserMapper userMapper;

    @Override
    public List<UserInfoEntity> findAll() {

        List<Long> integers = this.userMapper.selectAll();
        List<UserInfoEntity> result = new ArrayList<>();

        List<UserInfoEntity> allById = getTarget().findAllById(integers);
        for (Long id : integers) {
            for (UserInfoEntity userInfoEntity : allById) {
                if (id.equals(userInfoEntity.getId())) {
                    result.add(userInfoEntity);
                }
            }
        }


        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<UserInfoEntity> findByAge(Integer age) {

        List<Long> ageResult = getTarget().getCache("page2:" + age);

        if (ObjectUtils.isEmpty(ageResult)) {
            ageResult = this.userMapper.findByAge(age);
            getTarget().setGroup(ageResult, "page2:" + age, "AAA", "BBB");
        }

        List<UserInfoEntity> allById = getTarget().findAllById(ageResult);

        List<UserInfoEntity> result = new ArrayList<>();
        for (Long id : ageResult) {
            for (UserInfoEntity userInfoEntity : allById) {
                if (id.equals(userInfoEntity.getId())) {
                    result.add(userInfoEntity);
                }
            }
        }

        return result;
    }
}
