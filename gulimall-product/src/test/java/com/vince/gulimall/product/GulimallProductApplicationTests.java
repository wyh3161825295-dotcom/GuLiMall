package com.vince.gulimall.product;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.vince.gulimall.product.dao.AttrGroupDao;
import com.vince.gulimall.product.dao.SkuSaleAttrValueDao;
import com.vince.gulimall.product.service.BrandService;
import com.vince.gulimall.product.service.CategoryService;
import com.vince.gulimall.product.vo.SkuItemSaleAttrVo;
import com.vince.gulimall.product.vo.SpuItemAttrGroupVo;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class GulimallProductApplicationTests {

    @Resource
    private BrandService brandService;

    @Resource
    private CategoryService categoryService;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private RedissonClient redissonClient;

    @Resource
    private AttrGroupDao attrGroupDao;

    @Resource
    private SkuSaleAttrValueDao skuSaleAttrValueDao;
    @Test
    public void testThread() {
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                System.out.println("线程。。"+Thread.currentThread());
            }
        });
    }

    @Test
    public void test1() {
        List<SkuItemSaleAttrVo> saleAttrBySpuId = skuSaleAttrValueDao.getSaleAttrBySpuId(13L);
        saleAttrBySpuId.forEach(System.out::println);
    }

    @Test
    public void test() {
        List<SpuItemAttrGroupVo> attrGroupWithAttrsBySpuId = attrGroupDao.getAttrGroupWithAttrsBySpuId(130L, 225L);
        attrGroupWithAttrsBySpuId.forEach(System.out::println);
    }

    @Test
    public void testRedisson() {
        System.out.println(redissonClient);
    }

    @Test
    public void testStringRedis() {
        ValueOperations<String, String> ops = stringRedisTemplate.opsForValue();

        //保存
        ops.set("hello","world_" + UUID.randomUUID().toString());

        //查询
        String hello = ops.get("hello");
        System.out.println("之前保存的数据:"+hello);
    }

    @Test
    public void testFindPath() {
        Long[] catelogPath = categoryService.findCatelogPath(225l);

        log.info("完整路径catelogPath={}", Arrays.asList(catelogPath));
    }


    @Test
    public void contextLoads() {
        System.out.println(Integer.MAX_VALUE);

    }

}
