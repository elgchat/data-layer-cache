package cn.vimor.data.layer.cache.config.base;

import cn.vimor.data.layer.cache.config.CacheClient;
import cn.vimor.data.layer.cache.config.annotation.CacheAnnotation;
import cn.vimor.data.layer.cache.config.enums.TypeEnums;
import cn.vimor.data.layer.cache.util.ApplicationContextUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * 基本的缓存服务
 *
 * @author Jani
 * @date 2022/06/15
 */
@Service
public class BaseCacheServiceImpl<T, M extends BaseMapper<T>, A> implements BaseCacheService<T> {


    /**
     * 缓存客户端
     */
    @Resource
    CacheClient<T> cacheClient;
    /**
     * 映射器
     */
    @Autowired
    private M mapper;

    /**
     * 插入
     *
     * @param t t
     * @return {@link Integer}
     */
    @CacheAnnotation(handle = TypeEnums.CREATE)
    @Transactional(rollbackFor = Exception.class)
    public Integer insert(T t) {
        return mapper.insert(t);
    }

    /**
     * 找到一个
     *
     * @param id id
     * @return {@link T}
     */
    @CacheAnnotation(handle = TypeEnums.FIND)
    public T findOne(Long id) {
        return this.mapper.selectById(id);
    }

    /**
     * 找到所有id
     *
     * @param id id
     * @return {@link List}<{@link T}>
     */
    @CacheAnnotation(handle = TypeEnums.FIND_ALL)
    public List<T> findAllById(List<Long> id) {
        return this.mapper.selectAllById(id);
    }


    /**
     * 更新
     *
     * @param t t
     * @return int
     */
    @CacheAnnotation(handle = TypeEnums.UPDATE)
    public int update(T t) {
        return this.mapper.update(t);
    }

    /**
     * 得到目标
     *
     * @return {@link BaseCacheServiceImpl}<{@link T}, {@link M}, {@link A}>
     */
    protected BaseCacheServiceImpl<T, M, A> getTarget() {
        return ApplicationContextUtil.getBean(this.getClass());
    }


    /**
     * 组群
     *
     * @param ageResult 年龄结果
     * @param cacheKey  缓存key
     * @param groupKeys 组密钥
     */
    public void setGroup(List<Long> ageResult, String cacheKey, String... groupKeys) {
        cacheClient.saveCacheAndSetGroup(ageResult, cacheKey, groupKeys);
    }

    /**
     * 获取缓存
     *
     * @param cacheName 缓存名称
     * @return {@link List}<{@link Integer}>
     */
    public List<Long> getCache(String cacheName) {
        return this.cacheClient.getCache(cacheName);
    }

    /**
     * 删除组
     *
     * @param groupKeys 组密钥
     */
    public void removeGroup(String...
                                    groupKeys) {
        this.cacheClient.removeGroup(groupKeys);
    }

    /**
     * 根据组获取缓存
     *
     * @param groupKeys 组密钥
     * @return {@link List}<{@link String}>
     */
    public List<String> getCacheByGroup(String groupKeys) {
        return this.cacheClient.getCacheByGroup(groupKeys);
    }
}
