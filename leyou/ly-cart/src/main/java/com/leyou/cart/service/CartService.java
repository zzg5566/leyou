package com.leyou.cart.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.leyou.cart.Interceptor.CartInterceptor;
import com.leyou.cart.entity.Cart;
import com.leyou.common.auth.entity.UserInfo;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.utils.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CartService {
   @Autowired
   private CartInterceptor cartInterceptor;
    @Autowired
    private StringRedisTemplate redisTemplate;
    private static final String KEY_PREFIX = "ly:cart:uid:";
    public void addCart(Cart cart) {
        UserInfo info = cartInterceptor.getUser();

        BoundHashOperations<String, String, String> ops = redisTemplate.boundHashOps(KEY_PREFIX + (info.getId().toString()));
        if (ops.hasKey(cart.getSkuId().toString())){
            //代表以前就加入过
            String sku = ops.get(cart.getSkuId().toString());
            Cart cart1 = JsonUtils.nativeRead(sku, new TypeReference<Cart>() {
            });
            cart1.setNum(cart1.getNum()+cart.getNum());
            ops.put(cart1.getSkuId().toString(),JsonUtils.toString(cart1));
        }else{
            ops.put(cart.getSkuId().toString(),JsonUtils.toString(cart));
        }
    }

    public List<Cart> queryCartList() {
        UserInfo info = cartInterceptor.getUser();
        Boolean boo = this.redisTemplate.hasKey(KEY_PREFIX + (info.getId().toString()));
        if(boo == null || !boo){
            // 不存在，直接返回
            throw new LyException(ExceptionEnum.CARTS_NOT_FOUND);
        }
        BoundHashOperations<String, String, String> ops = redisTemplate.boundHashOps(KEY_PREFIX + (info.getId().toString()));
        List<String> list = ops.values();
        List<Cart> cartList=list.stream().map(skuJson->JsonUtils.toBean(skuJson,Cart.class)).collect(Collectors.toList());
        return cartList;
    }


    public void updateNum(Long id, Integer num) {
        UserInfo info = cartInterceptor.getUser();
        Boolean aBoolean = redisTemplate.hasKey(KEY_PREFIX + (info.getId().toString()));
        if (aBoolean==null||!aBoolean){
            throw new LyException(ExceptionEnum.CARTS_NOT_FOUND);
        }
        BoundHashOperations<String, String, String> ops = redisTemplate.boundHashOps(KEY_PREFIX + (info.getId().toString()));

        String s = ops.get(id.toString());

        Cart cart = JsonUtils.nativeRead(s, new TypeReference<Cart>() {
        });
        cart.setNum(num);
        ops.put(id.toString(),JsonUtils.toString(cart));
    }

    public void deleteCart(Long id) {
        UserInfo info = cartInterceptor.getUser();
        Boolean aBoolean = redisTemplate.hasKey(KEY_PREFIX + (info.getId().toString()));
        if (aBoolean==null||!aBoolean){
            throw new LyException(ExceptionEnum.CARTS_NOT_FOUND);
        }
          redisTemplate.opsForHash().delete(KEY_PREFIX + info.getId().toString(), id.toString());
    }

    public void mergeCart(List<Cart> carts) {
        carts.forEach(cart -> {
            addCart(cart);
        });
    }
}
