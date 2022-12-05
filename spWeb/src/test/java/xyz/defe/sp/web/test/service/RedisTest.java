package xyz.defe.sp.web.test.service;

import org.junit.jupiter.api.Assertions;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

@SpringBootTest
public class RedisTest {
//    @Autowired
    private RedisTemplate redisTemplate;

//    @Test
    public void saveDate() {
        String key = "test";
        String val = "for test";
        redisTemplate.opsForValue().set(key, val);
        Object object = redisTemplate.opsForValue().get(key);
        Assertions.assertEquals(val, object);
    }

}
