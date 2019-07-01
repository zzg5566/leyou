package com.leyou.controller;

import com.leyou.item.dto.SpecParamDTO;
import com.leyou.service.SpecParamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class SpecParamController {
    @Autowired
    private SpecParamService specParamService;
    @GetMapping("spec/params")
    public ResponseEntity<List<SpecParamDTO>> querySpecParams(@RequestParam(value = "gid",required = false) Long gid,@RequestParam(value = "cid",required = false) Long cid){
        return ResponseEntity.ok(specParamService.querySpecParams(gid,cid));
    }
}
