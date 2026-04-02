package com.vince.gulimall.cart.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.vince.gulimall.cart.interceptor.CartInterceptor;
import com.vince.gulimall.cart.vo.CartItemVo;
import com.vince.gulimall.cart.vo.CartVo;
import com.vince.gulimall.cart.vo.SkuInfoVo;
import com.vince.common.utils.R;
import com.vince.gulimall.cart.exception.CartExceptionHandler;
import com.vince.gulimall.cart.feign.ProductFeignService;
import com.vince.gulimall.cart.service.CartService;
import com.vince.gulimall.cart.to.UserInfoTo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

import static com.vince.common.constant.CartConstant.CART_PREFIX;


@Slf4j
@Service("cartService")
public class CartServiceImpl implements CartService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private ProductFeignService productFeignService;

    @Autowired
    private ThreadPoolExecutor executor;

    @Override
    public CartItemVo addToCart(Long skuId, Integer num) throws ExecutionException, InterruptedException {

        //1.拿到要操作的购物车redis操作器信息
        BoundHashOperations<String, Object, Object> cartOps = getCartOps();

        //2.判断Redis是否有该商品的信息
        String productRedisValue = (String) cartOps.get(skuId.toString());

        if (StringUtils.isEmpty(productRedisValue)) {
//2.1如果没有就添加数据
            //添加新的商品到购物车(redis)
            CartItemVo cartItemVo = new CartItemVo();
            //开启第一个异步任务，存商品基本信息
            CompletableFuture<Void> getSkuInfoFuture = CompletableFuture.runAsync(() -> {
                //远程调用商品模块查询当前要添加商品的sku信息
                R productSkuInfo = productFeignService.getInfo(skuId);
                SkuInfoVo skuInfo = productSkuInfo.getData("skuInfo", new TypeReference<SkuInfoVo>() {});
                //sku信息数据赋值给单个CartItem
                cartItemVo.setSkuId(skuInfo.getSkuId());
                cartItemVo.setTitle(skuInfo.getSkuTitle());
                cartItemVo.setImage(skuInfo.getSkuDefaultImg());
                cartItemVo.setPrice(skuInfo.getPrice());
                cartItemVo.setCount(num);
            }, executor);

            //开启第二个异步任务，存商品属性信息
            CompletableFuture<Void> getSkuAttrValuesFuture = CompletableFuture.runAsync(() -> {
                //2、远程查询skuAttrValues组合信息
                List<String> skuSaleAttrValues = productFeignService.getSkuSaleAttrValues(skuId);
                cartItemVo.setSkuAttrValues(skuSaleAttrValues);
            }, executor);

            //等待所有的异步任务全部完成
            CompletableFuture.allOf(getSkuInfoFuture, getSkuAttrValuesFuture).get();

            String cartItemJson = JSON.toJSONString(cartItemVo);
            cartOps.put(skuId.toString(), cartItemJson);

            return cartItemVo;
        } else {
//2.2 购物车有此商品，修改数量即可
            CartItemVo cartItemVo = JSON.parseObject(productRedisValue, CartItemVo.class);
            cartItemVo.setCount(cartItemVo.getCount() + num);
            //修改redis的数据
            String cartItemJson = JSON.toJSONString(cartItemVo);
            cartOps.put(skuId.toString(),cartItemJson);

            return cartItemVo;
        }
    }

    @Override
    public CartItemVo getCartItem(Long skuId) {
        //拿到要操作的购物车信息
        BoundHashOperations<String, Object, Object> cartOps = getCartOps();

        String redisValue = (String) cartOps.get(skuId.toString());

        CartItemVo cartItemVo = JSON.parseObject(redisValue, CartItemVo.class);

        return cartItemVo;
    }

    /**
     * 获取用户登录或者未登录购物车里所有的数据
     * @return
     * @throws ExecutionException
     * @throws InterruptedException
     */
    @Override
    public CartVo getCart() throws ExecutionException, InterruptedException {

        CartVo cartVo = new CartVo();
        UserInfoTo userInfoTo = CartInterceptor.toThreadLocal.get();
        if (userInfoTo.getUserId() != null) {
            //1、登录
            String cartKey = CART_PREFIX + userInfoTo.getUserId();
            //临时购物车的键
            String temptCartKey = CART_PREFIX + userInfoTo.getUserKey();

            //2、如果临时购物车的数据还未进行合并
            List<CartItemVo> tempCartItems = getCartItems(temptCartKey);
            if (tempCartItems != null) {
                //临时购物车有数据需要进行合并操作
                for (CartItemVo item : tempCartItems) {
                    addToCart(item.getSkuId(),item.getCount());
                }
                //清除临时购物车的数据
                clearCartInfo(temptCartKey);
            }

            //3、获取登录后的购物车数据【包含合并过来的临时购物车的数据和登录后购物车的数据】
            List<CartItemVo> cartItems = getCartItems(cartKey);
            cartVo.setItems(cartItems);

        } else {
            //没登录
            String cartKey = CART_PREFIX + userInfoTo.getUserKey();
            //获取临时购物车里面的所有购物项
            List<CartItemVo> cartItems = getCartItems(cartKey);
            cartVo.setItems(cartItems);
        }

        return cartVo;
    }

    /**
     * 获取到我们要操作的购物车的redis
     * @return
     */
    private BoundHashOperations<String, Object, Object> getCartOps() {
        //先从拦截器的ThreadLocal得到当前用户信息
        UserInfoTo userInfoTo = CartInterceptor.toThreadLocal.get();
        // 1、判断用户有没有登录
        String cartKey = ""; //购物车名，例如"gulimall:cart:用户ID"或"gulimall:cart:临时用户userKey"
        if (userInfoTo.getUserId() != null) {
            // 用户已登录，则存储在Redis中的key 是 用户的Id
            cartKey = CART_PREFIX + userInfoTo.getUserId(); //CART_PREFIX = "gulimall:cart:"
        } else {
            // 用户没有登录即临时用户状态，则存在Redis中的key 是 临时用户对应的 `user-key`
            cartKey = CART_PREFIX + userInfoTo.getUserKey();
        }

        //绑定一个hash操作，以后所有redis增删改查都是针对这个key的增删改查。
        // 例如operations.put("xx","xx")是往这个cartKey的哈希键里添加数据
        BoundHashOperations<String, Object, Object> operations = redisTemplate.boundHashOps(cartKey);

        return operations;
    }


    /**
     * 获取购物车里面的数据
     * @param cartKey
     * @return
     */
    private List<CartItemVo> getCartItems(String cartKey) {
        //获取购物车里面的所有商品
        BoundHashOperations<String, Object, Object> operations = redisTemplate.boundHashOps(cartKey);
        List<Object> values = operations.values();
        if (values != null && values.size() > 0) {
            List<CartItemVo> cartItemVoStream = values.stream().map((obj) -> {
                String str = (String) obj;
                CartItemVo cartItem = JSON.parseObject(str, CartItemVo.class);
                return cartItem;
            }).collect(Collectors.toList());
            return cartItemVoStream;
        }
        return null;

    }


    @Override
    public void clearCartInfo(String cartKey) {
        redisTemplate.delete(cartKey);
    }

    @Override
    public void checkItem(Long skuId, Integer check) {

        //查询购物车里面的商品
        CartItemVo cartItem = getCartItem(skuId);
        //修改商品状态
        cartItem.setCheck(check == 1?true:false);

        //序列化存入redis中
        String redisValue = JSON.toJSONString(cartItem);

        BoundHashOperations<String, Object, Object> cartOps = getCartOps();
        cartOps.put(skuId.toString(),redisValue);

    }

    /**
     * 修改购物项数量
     * @param skuId
     * @param num
     */
    @Override
    public void changeItemCount(Long skuId, Integer num) {

        //查询购物车里面的商品
        CartItemVo cartItem = getCartItem(skuId);
        cartItem.setCount(num);

        BoundHashOperations<String, Object, Object> cartOps = getCartOps();
        //序列化存入redis中
        String redisValue = JSON.toJSONString(cartItem);
        cartOps.put(skuId.toString(),redisValue);
    }


    /**
     * 删除购物项
     * @param skuId
     */
    @Override
    public void deleteIdCartInfo(Integer skuId) {

        BoundHashOperations<String, Object, Object> cartOps = getCartOps();
        cartOps.delete(skuId.toString());
    }

    @Override
    public List<CartItemVo> getUserCartItems() {

        List<CartItemVo> cartItemVoList = new ArrayList<>();
        //获取当前用户登录的信息
        UserInfoTo userInfoTo = CartInterceptor.toThreadLocal.get();
        //如果用户未登录直接返回null
        if (userInfoTo.getUserId() == null) {
            return null;
        } else {
            //获取购物车项
            String cartKey = CART_PREFIX + userInfoTo.getUserId();
            //获取所有的
            List<CartItemVo> cartItems = getCartItems(cartKey);
            if (cartItems == null) {
                throw new CartExceptionHandler();
            }
            //筛选出选中的
            cartItemVoList = cartItems.stream()
                    .filter(items -> items.getCheck())
                    .map(item -> {
                        //更新为最新的价格（查询数据库）
                        BigDecimal price = productFeignService.getPrice(item.getSkuId());
                        item.setPrice(price);
                        return item;
                    })
                    .collect(Collectors.toList());
        }

        return cartItemVoList;
    }
}
