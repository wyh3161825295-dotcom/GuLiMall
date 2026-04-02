package com.vince.gulimall.product.fallback;

import com.vince.common.exception.BizCodeEnum;
import com.vince.common.utils.R;
import com.vince.gulimall.product.feign.SeckillFeignService;
import org.springframework.stereotype.Component;



@Component
public class SeckillFeignServiceFallBack implements SeckillFeignService {
    @Override
    public R getSkuSeckilInfo(Long skuId) {
        return R.error(BizCodeEnum.TO_MANY_REQUEST.getCode(),BizCodeEnum.TO_MANY_REQUEST.getMessage());
    }
}
