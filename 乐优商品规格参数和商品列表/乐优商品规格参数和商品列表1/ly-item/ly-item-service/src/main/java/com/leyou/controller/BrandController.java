package com.leyou.controller;

import com.leyou.common.vo.PageResult;
import com.leyou.item.dto.BrandDTO;
import com.leyou.service.BrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("brand")
public class BrandController {
    @Autowired
    private BrandService brandService;
    @GetMapping("page")
    public ResponseEntity<PageResult<BrandDTO>> queryPage(
            @RequestParam(value = "page",defaultValue ="1") Integer page,
            @RequestParam(value = "rows",defaultValue = "5")Integer rows,
            @RequestParam(value = "sortBy",required = false)String sortBy,
            @RequestParam(value = "desc",required = false)Boolean desc,
            @RequestParam(value = "search",required = false)String search
    ){
        return ResponseEntity.ok(brandService.queryPage(page,rows,sortBy,desc,search));
    }
    @PostMapping
    public ResponseEntity<Void> addBrand(
            BrandDTO brandDTO,@RequestParam("cids")List<Long>cids
    ){
        brandService.addBrand(brandDTO,cids);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
    @PutMapping
    public ResponseEntity<Void> updateBrand(BrandDTO brandDTO,@RequestParam("cids")List<Long>cids){
        brandService.updateBrand(brandDTO,cids);
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }
    @GetMapping("of/category")
    public ResponseEntity<List<BrandDTO>> queryByCategoryId(@RequestParam("id")Long id){
        return ResponseEntity.ok(brandService.queryByCategoryId(id));
    }
}
