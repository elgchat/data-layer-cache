package cn.vimor.data.layer.cache.config;


import cn.vimor.data.layer.cache.config.annotation.CacheAnnotation;
import cn.vimor.data.layer.cache.config.base.BaseEntity;
import cn.vimor.data.layer.cache.config.bean.CacheClientBean;
import cn.vimor.data.layer.cache.config.bean.LocalCache;
import cn.vimor.data.layer.cache.config.enums.TypeEnums;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.ObjectUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * 缓存拦截器
 *
 * @author jianghai
 * @date 2022/05/24
 */
@Aspect
@Component
public class CacheAspect {

    @Resource
    private CacheClient<?> cacheClient;

    @Resource
    private CacheThreadLocal cacheThreadLocal;


    @Pointcut(value = "execution(* cn.vimor.data.layer.cache.cache.*.*(..))")
    private void pointCut() {
    }

    @Pointcut("@annotation(cn.vimor.data.layer.cache.config.annotation.CacheAnnotation)")
    private void pointCutAnnotation() {
    }

    @Pointcut(value = "execution(* cn.vimor.data.layer.cache.config.base.BaseCacheServiceImpl.*(..))")
    private void pointCutAnnotation1() {
    }


    @Around("pointCut() || pointCutAnnotation() || pointCutAnnotation1()")
    public Object before(ProceedingJoinPoint joinPoint) throws Throwable {

        //获取切面参数
        Object[] args = joinPoint.getArgs();
        //获取切面方法
        String methodName = joinPoint.getSignature().getName();
        //获取切面方法类
        String packageName = joinPoint.getSignature().getDeclaringType().getName();
        //获取方法信息
        MethodSignature sign = (MethodSignature) joinPoint.getSignature();
        //获取所有参数名称
        String[] parameterNames = sign.getParameterNames();
        Method method = sign.getMethod();
        //获取方法上的注解
        CacheAnnotation annotation = method.getAnnotation(CacheAnnotation.class);
        //cacheClient
        CacheClientBean cacheClientBean = new CacheClientBean(packageName, methodName, parameterNames, args);

        Object result;
        //存在注解逻辑
        if (ObjectUtils.isNotEmpty(annotation)) {
            result = annotationProcessor(joinPoint, args, parameterNames, annotation, cacheClientBean);
        } else {
            result = joinPoint.proceed(args);
        }

        return result;
    }

    /**
     * 注解处理器
     *
     * @param joinPoint       连接点
     * @param args            arg游戏
     * @param parameterNames  参数名称
     * @param annotation      注释
     * @param cacheClientBean 缓存客户端bean
     * @return {@link Object}
     * @throws Throwable throwable
     */
    private Object annotationProcessor(ProceedingJoinPoint joinPoint, Object[] args, String[] parameterNames, CacheAnnotation annotation, CacheClientBean cacheClientBean) throws Throwable {

        //查找
        if (annotation.handle() == TypeEnums.FIND) {
            return findAction(joinPoint, args, cacheClientBean);
        }

        //查找
        if (annotation.handle() == TypeEnums.FIND_ALL) {
            return findAllAction(joinPoint, args, cacheClientBean);
        }

        // 更新或删除操作
        if (annotation.handle() == TypeEnums.UPDATE || annotation.handle() == TypeEnums.DELETE) {
            return operationProcessing(joinPoint, args, parameterNames, cacheClientBean);
        }
        return joinPoint.proceed(args);
    }


    /**
     * 操作处理
     *
     * @param joinPoint       连接点
     * @param args            arg游戏
     * @param parameterNames  参数名称
     * @param cacheClientBean 缓存客户端bean
     * @return {@link Object}
     * @throws Throwable throwable
     */
    private Object operationProcessing(ProceedingJoinPoint joinPoint, Object[] args, String[] parameterNames, CacheClientBean cacheClientBean) throws Throwable {


        Object arg = null;
        for (int i = 0; i < parameterNames.length; i++) {
            if ("id".equals(parameterNames[i])) {
                arg = args[i];
            }
        }

        if (ObjectUtils.isEmpty(arg)) {
            for (Object object : args) {
                if (object.getClass().getSuperclass() == BaseEntity.class) {
                    Field[] declaredFields = object.getClass().getDeclaredFields();

                    for (Field declaredField : declaredFields) {
                        declaredField.setAccessible(true);
                        if ("id".equals(declaredField.getName())) {
                            arg = declaredField.get(object);
                        }
                    }
                }
            }
        }

        if (ObjectUtils.isNotEmpty(arg)) {

            Object proceed = joinPoint.proceed(args);

            boolean synchronizationActive = TransactionSynchronizationManager.isSynchronizationActive();
            if (synchronizationActive) {

                //事物结束后删除
                this.cacheClient.tagDelCache(cacheClientBean);
                TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                    @Override
                    public void afterCommit() {
                        TransactionSynchronization.super.afterCommit();
                        cacheThreadLocal.remove();
                    }
                });
            } else {
                //立即删除
                this.cacheClient.delCache(cacheClientBean);
            }
            return proceed;
        }
        throw new RuntimeException("操作类找不到id!!!");
    }

    /**
     * 找到行动
     *
     * @param joinPoint       连接点
     * @param args            arg游戏
     * @param cacheClientBean 缓存客户端bean
     * @return {@link Object}
     * @throws Throwable throwable
     */
    private Object findAction(ProceedingJoinPoint joinPoint, Object[] args, CacheClientBean cacheClientBean) throws Throwable {

        if (args.length != 1) {
            throw new RuntimeException("根据id获取数据方法异常！");
        }

        LocalCache cache = this.cacheClient.getCache(cacheClientBean);

        if (ObjectUtils.isNotEmpty(cache)) {
            return JSONObject.parseObject(cache.getValue(), cache.getConversionClass());
        } else {
            Object result = joinPoint.proceed(args);
            LocalCache localCache = new LocalCache(JSONObject.parseObject(JSONObject.toJSONString(result)).getLong("id"), result.getClass(), result);
            this.cacheClient.saveCache(cacheClientBean, localCache);
            return result;
        }
    }

    /**
     * 找到所有行动
     *
     * @param joinPoint       连接点
     * @param args            arg游戏
     * @param cacheClientBean 缓存客户端bean
     * @return {@link Object}
     */
    private Object findAllAction(ProceedingJoinPoint joinPoint, Object[] args, CacheClientBean cacheClientBean) throws Throwable {

        if (args.length != 1) {
            throw new RuntimeException("根据id获取数据方法异常！");
        }

        //参数接收
        List<Long> acceptArgs = (List<Long>) args[0];
        if (ObjectUtils.isEmpty(acceptArgs)) {
            throw new RuntimeException("params is null");
        }

        List<String> cacheResult = cacheClient.batchGetCache(cacheClientBean, acceptArgs);

        //验证是否为空
        if (ObjectUtils.isNotEmpty(cacheResult)) {
            List<Object> resultData = new ArrayList<>();
            for (String cache : cacheResult) {
                LocalCache localCache = JSONObject.parseObject(cache, LocalCache.class);
                resultData.add(JSONObject.parseObject(localCache.getValue(), localCache.getConversionClass()));
            }

            if (cacheResult.size() < acceptArgs.size()) {
                List<Long> existCacheIds = new ArrayList<>();
                for (Long acceptArg : acceptArgs) {
                    for (String cache : cacheResult) {
                        LocalCache localCache = JSONObject.parseObject(cache, LocalCache.class);
                        if (acceptArg.equals(localCache.getId())) {
                            existCacheIds.add(localCache.getId());
                        }
                    }
                }
                acceptArgs.removeAll(existCacheIds);
                Object result = joinPoint.proceed(new Object[]{acceptArgs});

                if (result instanceof ArrayList) {
                    ((ArrayList<?>) result).forEach(e -> {
                        saveCache(cacheClientBean, e);
                        resultData.add(e);
                    });
                }
            }
            return resultData;
        } else {
            Object result = joinPoint.proceed(args);
            if (result instanceof ArrayList) {
                ((ArrayList<?>) result).forEach(e -> saveCache(cacheClientBean, e));
            }
            return result;
        }
    }

    /**
     * 保存缓存
     *
     * @param cacheClientBean 缓存客户端bean
     * @param object          object
     */
    private void saveCache(CacheClientBean cacheClientBean, Object object) {
        Long id = JSONObject.parseObject(JSONObject.toJSONString(object)).getLong("id");
        LocalCache localCache = new LocalCache(id, object.getClass(), object);
        cacheClientBean.setArgs(new Object[]{id});
        this.cacheClient.saveCache(cacheClientBean, localCache);
    }
}
