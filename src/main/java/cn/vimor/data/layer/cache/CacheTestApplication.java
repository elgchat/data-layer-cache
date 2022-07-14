package cn.vimor.data.layer.cache;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 测试应用程序缓存
 *
 * @author Jani
 * @date 2022/06/14
 */
@SpringBootApplication
@MapperScan({"jani.demo.cache.test.mapper"})
public class CacheTestApplication {

    public static void main(String[] args) {
        SpringApplication.run(CacheTestApplication.class, args);
    }


}
