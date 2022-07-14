package cn.vimor.data.layer.cache.config;

import com.vlightv.overseas.toolkit.service.JedisService;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 缓存线程本地
 *
 * @author Jani
 * @date 2022/06/20
 */
@Component
public class CacheThreadLocal {


    /**
     * 删除保存
     */
    private final ThreadLocal<List<String>> REMOVE_SAVE = new ThreadLocal<>();

    /**
     * Jedis服务
     */
    @Resource
    private JedisService jedisService;


    /**
     * 设置删除保存
     *
     * @param keys 键
     */
    public void setRemoveSave(String... keys) {
        List<String> threadLocalList = REMOVE_SAVE.get();
        for (String key : keys) {

            if (ObjectUtils.isEmpty(threadLocalList)) {
                threadLocalList = new ArrayList<>();
            }
            threadLocalList.add(key);
        }
        REMOVE_SAVE.set(threadLocalList);
    }


    /**
     * 删除
     */
    public void remove() {
        List<String> keys = REMOVE_SAVE.get();

        for (String key : keys) {
            this.jedisService.del(key);
        }
        REMOVE_SAVE.remove();
    }
}
