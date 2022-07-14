package cn.vimor.data.layer.cache.controller;

import cn.vimor.data.layer.cache.service.TestService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;


/**
 * 测试控制器
 *
 * @author Jani
 * @date 2022/06/14
 */
@RestController
public class TestController {

    @Resource
    private TestService testService;

    /**
     * 插入
     *
     * @return {@link String}
     */
    @GetMapping("insert")
    public String insert() {

        this.testService.insert();
        return null;
    }

    /**
     * 找到所有
     *
     * @return {@link Object}
     */
    @GetMapping("findall")
    public Object findAll() {
        return this.testService.findAll();
    }

    /**
     * 找到一个
     *
     * @param id id
     * @return {@link Object}
     */
    @GetMapping("findOne")
    public Object findOne(Long id) {
        return this.testService.findOne(id);
    }

    /**
     * 更新
     *
     * @param username 用户名
     * @param id       id
     * @return {@link Object}
     */
    @GetMapping("update")
    public Object update(String username, Long id) {
        this.testService.update(username, id);
        return "success";
    }

    /**
     * 发现按年龄
     *
     * @param age 年龄
     * @return {@link Object}
     */
    @GetMapping("findByAge")
    public Object findByAge(Integer age) {

        return this.testService.findByAge(age);
    }

    /**
     * 测试没有缓存
     *
     * @param age 年龄
     * @return {@link Object}
     */
    @GetMapping("testNoCache")
    public Object testNoCache(Integer age) {

        return this.testService.testNoCache(age);
    }
}
