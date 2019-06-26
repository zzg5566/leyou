package com.leyou.controller;

import com.leyou.item.pojo.CategoryDTO;
import com.leyou.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;
    /**
     * 根据父节点查询商品类目
     *
     * @param pid
     * @return
     */
    @GetMapping("/of/parent")
    public ResponseEntity<List<CategoryDTO>> queryByParentId(
            @RequestParam(value = "pid", defaultValue = "0") Long pid) {
        return ResponseEntity.ok(this.categoryService.queryListByParent(pid));
    }
}