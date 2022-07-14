# data-layer-cache
数据层缓存实现

## 缓存使用说明
1. 创建 XXXCacheService extends BaseCacheService<实体类>
2. 创建XXXCacheServiceImpl extends BaseCacheServiceImpl<实体类, 实体对应mapper, 当前类> implements UserCacheService
3. mybaits生成的mapper中需要有以下方法
    * insert
    * selectById
    * selectAllById
    * update
4. mybaits中自定义的代码均要返回id
5. 在对应的XXXCacheServiceImpl中自定义业务逻辑中要需要根据id查询等使用getTarget().xxxx(*) 方式调用内置方法
6. 获取自定义缓存的key 方式  getTarget().getCache(“key”);
7. 设定key及组方式  getTarget().setGroup(数据, “自定义缓存key”, “组key（可多个）”);
8. 删除组 getTarget().removeGroup();
