package cn.vimor.data.layer.cache.service;

import cn.vimor.data.layer.cache.cache.UserCacheService;
import cn.vimor.data.layer.cache.entity.UserInfoEntity;
import cn.vimor.data.layer.cache.mapper.UserMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 测试服务实现类
 *
 * @author Jani
 * @date 2022/06/14
 */
@Service
public class TestServiceImpl implements TestService {

    @Resource
    private UserCacheService userCacheService;

    @Resource
    private UserMapper userMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String insert() {
        UserInfoEntity userInfoEntity = new UserInfoEntity();
        userInfoEntity.setAge(1);
        userInfoEntity.setUsername("1111");
        int insert = this.userCacheService.insert(userInfoEntity);
        return String.valueOf(insert);
    }

    @Override
    public List<UserInfoEntity> findAll() {

        return this.userCacheService.findAll();
    }

    @Override
    public UserInfoEntity findOne(Long id) {
        return this.userCacheService.findOne(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(String username, Long id) {

        UserInfoEntity userInfoEntity = new UserInfoEntity();
        userInfoEntity.setId(id);
        userInfoEntity.setUsername(username);
        this.userCacheService.update(userInfoEntity);
    }

    @Override
    public List<UserInfoEntity> findByAge(Integer age) {

        return this.userCacheService.findByAge(age);
    }

    @Override
    public Object testNoCache(Integer age) {

        long l = System.currentTimeMillis();
        System.out.println("开始时间" + l);

        List<Long> longs = this.userMapper.findByAge(age);

        List<Object> list = new ArrayList<>();
        List<UserInfoEntity> allById = this.userMapper.selectAllById(longs);
        for (Long id : longs) {
            for (UserInfoEntity userInfoEntity : allById) {
                if (id.equals(userInfoEntity.getId())) {
                    list.add(userInfoEntity);
                }
            }
        }
        System.out.println("结束时间" + System.currentTimeMillis());
        System.out.println("结束时间差" + (System.currentTimeMillis() - l));
        return list;
    }
}
