package com.ncst.gulimall.product;

import com.ncst.gulimall.product.dao.BrandDao;
import com.ncst.gulimall.product.entity.BrandEntity;
import com.ncst.gulimall.product.service.BrandService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import javax.sql.DataSource;

@SpringBootTest
class GulimallProductApplicationTests {

    @Autowired
    BrandService brandService;

    @Test
    void contextLoads() {

        BrandEntity brandEntity = new BrandEntity();
        brandEntity.setDescript("");
        brandEntity.setName("苹果");
        brandService.save(brandEntity);
        System.out.println("添加成功");
    }

}
