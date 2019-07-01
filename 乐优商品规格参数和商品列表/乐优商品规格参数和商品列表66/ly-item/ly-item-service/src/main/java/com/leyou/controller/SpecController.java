package com.leyou.controller;

import com.leyou.item.dto.SpecDTO;
import com.leyou.service.SpecService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/spec/groups")
public class SpecController {
    @Autowired
    private SpecService specService;
    @GetMapping("/of/category")
    public ResponseEntity<List<SpecDTO>> queryGroupByCategory(@RequestParam("id") Long id){
        return ResponseEntity.ok(specService.queryGroupByCategory(id));
    }
}
