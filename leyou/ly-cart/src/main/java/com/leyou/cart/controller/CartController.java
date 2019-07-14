package com.leyou.cart.controller;

import com.leyou.cart.entity.Cart;
import com.leyou.cart.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
public class CartController {
    @Autowired
    private CartService cartService;
    @PostMapping
    public ResponseEntity<Void> addCart(@RequestBody Cart cart){
        this.cartService.addCart(cart);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
    @GetMapping("list")
    public ResponseEntity<List<Cart>> queryCartList(){
       return  ResponseEntity.ok(this.cartService.queryCartList());
    }
    @PutMapping
    public ResponseEntity<Void> updateNum(@RequestParam("id") Long id,@RequestParam("num")Integer num){
        this.cartService.updateNum(id,num);
        return  ResponseEntity.status(HttpStatus.OK).build();
    }
    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteCart(@PathVariable("id")Long id){
        this.cartService.deleteCart(id);
        return ResponseEntity.ok().build();
    }
    @PostMapping("list")
    public ResponseEntity<Void> mergeCart(@RequestBody List<Cart> carts){
        this.cartService.mergeCart(carts);
        return  ResponseEntity.ok().build();
    }
}
