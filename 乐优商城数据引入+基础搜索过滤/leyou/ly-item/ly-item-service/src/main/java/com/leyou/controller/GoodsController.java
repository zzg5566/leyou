package com.leyou.controller;

import com.leyou.common.vo.PageResult;
import com.leyou.item.dto.SkuDTO;
import com.leyou.item.dto.SpuDTO;
import com.leyou.item.dto.SpuDetailDTO;
import com.leyou.service.GoodsService;
import com.netflix.ribbon.proxy.annotation.Http;
import jdk.nashorn.internal.objects.annotations.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController

public class GoodsController {
    @Autowired
    private GoodsService goodsService;
    @GetMapping("spu/page")
    public ResponseEntity<PageResult<SpuDTO>> querySpuByPage(
            @RequestParam(value = "key",required = false) String key,
            @RequestParam(value = "saleable",required = false) Boolean saleable,
            @RequestParam(value = "page",defaultValue = "1") Integer page,
            @RequestParam(value = "rows",defaultValue = "5")Integer rows){
       PageResult<SpuDTO> pageResult= goodsService.querySpuByPage(key,saleable,page,rows);
       return  ResponseEntity.ok(pageResult);
    }
    @PostMapping("goods")
    public ResponseEntity<Void>  saveGoods(@RequestBody SpuDTO spuDTO){
        goodsService.saveGoods(spuDTO);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
    @PutMapping("spu/saleable")
    public ResponseEntity<Void> updateSpuSaleable(@RequestParam("id")Long id,@RequestParam("saleable")Boolean saleable){
        this.goodsService.updateSpuSaleable(id,saleable);
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }
    @GetMapping("spu/detail")
    public ResponseEntity<SpuDetailDTO> querySpuDetailById(@RequestParam("id")Long id){
        return ResponseEntity.ok(this.goodsService.querySpuDetailById(id));
    }
    @GetMapping("sku/of/spu")
    public  ResponseEntity<List<SkuDTO>> querySkuBySpuId(@RequestParam("id") Long id){
        return  ResponseEntity.ok(this.goodsService.querySkuBySpuId(id));
    }
    @PutMapping("goods")
    public ResponseEntity<Void> updateGoods(@RequestBody SpuDTO spuDTO){
        this.goodsService.updateGoods(spuDTO);
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }
}
