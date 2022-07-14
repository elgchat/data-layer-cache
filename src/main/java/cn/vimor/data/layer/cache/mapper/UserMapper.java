package cn.vimor.data.layer.cache.mapper;

import cn.vimor.data.layer.cache.config.base.BaseMapper;
import cn.vimor.data.layer.cache.entity.UserInfoEntity;
import org.apache.ibatis.annotations.Param;
import org.mybatis.spring.annotation.MapperScan;

import java.util.List;


/**
 * 用户映射器
 *
 * @author Jani
 * @date 2022/06/14
 */
@MapperScan
public interface UserMapper extends BaseMapper<UserInfoEntity> {

    /**
     * 选择所有
     *
     * @return {@link List}<{@link UserInfoEntity}>
     */
    List<Long> selectAll();


    /**
     * 发现按年龄
     *
     * @param age 年龄
     * @return {@link List}<{@link Integer}>
     */
    List<Long> findByAge(@Param("age") Integer age);

}
