package cn.vimor.data.layer.cache.entity;

import cn.vimor.data.layer.cache.config.base.BaseEntity;

import java.io.Serializable;

/**
 * 用户信息实体
 *
 * @author Jani
 * @date 2022/06/08
 */
public class UserInfoEntity extends BaseEntity implements Serializable {

    private Long id;

    private String username;

    private int age;

    public UserInfoEntity() {
    }

    public UserInfoEntity(Long id, String username, int age) {
        this.id = id;
        this.username = username;
        this.age = age;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
}
