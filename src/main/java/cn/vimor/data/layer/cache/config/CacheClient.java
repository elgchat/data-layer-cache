package cn.vimor.data.layer.cache.config;

import cn.vimor.data.layer.cache.config.bean.CacheClientBean;
import cn.vimor.data.layer.cache.config.bean.LocalCache;
import cn.vimor.data.layer.cache.config.constant.CacheMethodPrefixConstants;
import cn.vimor.toolkit.jedis.service.JedisService;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 缓存客户端
 *
 * @author Jani
 * @date 2022/06/20
 */
@Component
public class CacheClient<T> {

    @Resource
    private JedisService jedisService;

    @Resource
    private CacheThreadLocal cacheThreadLocal;


    /**
     * 保存缓存
     *
     * @param cacheClientBean 缓存客户端bean
     * @param localCache      本地缓存
     */
    public void saveCache(CacheClientBean cacheClientBean, LocalCache localCache) {

        this.jedisService.existsKey("aa");
        String key = generateKey(cacheClientBean);
        this.jedisService.opsForValue().set(key, JSONObject.toJSONString(localCache));
    }

    /**
     * 获取缓存
     *
     * @param cacheClientBean 缓存客户端bean
     * @return {@link LocalCache}
     */
    public LocalCache getCache(CacheClientBean cacheClientBean) {

        String key = generateKey(cacheClientBean);
        String cacheData = this.jedisService.opsForValue().get(key);
        return JSONObject.parseObject(cacheData, LocalCache.class);
    }


    /**
     * 生成key
     *
     * @param cacheClientBean 缓存客户端bean
     * @return {@link String}
     */
    public String generateKey(CacheClientBean cacheClientBean) {

        StringBuilder key = new StringBuilder(CacheMethodPrefixConstants.CACHE_PREFIX);
        key.append(cacheClientBean.getPackageName());
        key.append(CacheMethodPrefixConstants.CONNECTOR);
        key.append(cacheClientBean.getMethodName());
        key.append(CacheMethodPrefixConstants.CONNECTOR);

        Object[] args = cacheClientBean.getArgs();
        String[] parameterNames = cacheClientBean.getParameterNames();

        for (int i = 0; i < args.length; i++) {
            key.append(parameterNames[i]).append(args[i]);
        }
        return key.toString();
    }

    /**
     * 删除缓存
     *
     * @param cacheClientBean 缓存客户端bean
     */
    public void delCache(CacheClientBean cacheClientBean) {
        cacheClientBean.setMethodName("findOne");
        String key = generateKey(cacheClientBean);
        this.jedisService.del(key);
    }

    /**
     * 标记删除
     *
     * @param cacheClientBean 缓存客户端bean
     */
    public void tagDelCache(CacheClientBean cacheClientBean) {
        cacheClientBean.setMethodName("findOne");
        String key = generateKey(cacheClientBean);
        cacheThreadLocal.setRemoveSave(key);
    }

    /**
     * 保存缓存和集组
     *
     * @param ageResult 年龄结果
     * @param cacheKey  缓存key
     * @param groupKeys 组密钥
     */
    public void saveCacheAndSetGroup(List<Long> ageResult, String cacheKey, String... groupKeys) {

        String key = CacheMethodPrefixConstants.CUSTOM_CACHE_PREFIX + cacheKey;

        String resultData = this.jedisService.opsForValue().get(key);
        if (ObjectUtils.isEmpty(resultData)) {
            this.jedisService.opsForValue().set(key, JSONObject.toJSONString(ageResult));
        }

        key = CacheMethodPrefixConstants.CUSTOM_CACHE_PREFIX_GROUP;
        for (String groupKey : groupKeys) {
            this.jedisService.opsForSet().add(key + groupKey, cacheKey);
        }

    }

    /**
     * 获取缓存
     *
     * @param cacheName 缓存名称
     * @return {@link List}<{@link Integer}>
     */
    public List<Long> getCache(String cacheName) {

        String key = CacheMethodPrefixConstants.CUSTOM_CACHE_PREFIX + cacheName;
        String cacheResult = this.jedisService.opsForValue().get(key);

        JSONArray objects = JSONArray.parseArray(cacheResult);
        if (ObjectUtils.isEmpty(objects)) {
            return null;
        }
        return objects.stream().map(e -> Long.valueOf(JSONObject.toJSONString(e))).collect(Collectors.toList());
    }

    /**
     * 删除组
     *
     * @param groupKeys 组密钥
     */
    public void removeGroup(String[] groupKeys) {

        groupKeys = Arrays.stream(groupKeys).map(e -> CacheMethodPrefixConstants.CUSTOM_CACHE_PREFIX_GROUP + e).toArray(String[]::new);

        List<String> delGroupKey = new ArrayList<>(Arrays.asList(groupKeys));
        for (String groupKey : groupKeys) {
            List<String> cacheByGroup = getCacheByGroup(groupKey);
            if (ObjectUtils.isNotEmpty(cacheByGroup)) {
                delGroupKey.addAll(cacheByGroup);
            }
        }

        groupKeys = delGroupKey.toArray(new String[0]);

        boolean synchronizationActive = TransactionSynchronizationManager.isSynchronizationActive();
        if (synchronizationActive) {

            //事物结束后删除
            cacheThreadLocal.setRemoveSave(groupKeys);
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    TransactionSynchronization.super.afterCommit();
                    cacheThreadLocal.remove();
                }
            });
        } else {
            //立即删除
            for (String groupKey : groupKeys) {
                this.jedisService.del(groupKey);
            }
        }
    }

    /**
     * 被组缓存
     *
     * @param groupKeys 组密钥
     * @return {@link List}<{@link String}>
     */
    public List<String> getCacheByGroup(String groupKeys) {

        Set<String> members = this.jedisService.opsForSet().members(groupKeys);
        if (ObjectUtils.isEmpty(members)) {
            return null;
        }

        return new ArrayList<>(members);
    }


    /**
     * 批处理得到缓存
     *
     * @param cacheClientBean 缓存客户端bean
     * @param acceptArgs      接受参数
     * @return {@link List}<{@link String}>
     */
    public List<String> batchGetCache(CacheClientBean cacheClientBean, List<Long> acceptArgs) {
        //获取redis缓存数据

        long l = System.currentTimeMillis();

        cacheClientBean.setMethodName("findOne");
        List<String> collect = acceptArgs.stream().map(e -> {
            cacheClientBean.setArgs(new Long[]{e});
            return generateKey(cacheClientBean);
        }).collect(Collectors.toList());
        List<String> cacheResult = this.jedisService.opsForValue().piplineGet(collect);
        cacheResult.removeIf("null"::equals);
        return cacheResult;
    }
}
