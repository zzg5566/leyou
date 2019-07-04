package com.leyou.Controller;

import com.leyou.common.vo.PageResult;
import com.leyou.dto.GoodsDTO;
import com.leyou.service.SearchService;
import com.leyou.utils.SearchRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class searchController {
    @Autowired
    private SearchService searchService;
    @PostMapping("page")
    public ResponseEntity<PageResult<GoodsDTO>> search(@RequestBody SearchRequest searchRequest){
        return ResponseEntity.ok(this.searchService.search(searchRequest));
    }
    @PostMapping("filter")
    public ResponseEntity<Map<String,List<?>>> queryFilters(@RequestBody SearchRequest searchRequest){
        return  ResponseEntity.ok(this.searchService.queryFilters(searchRequest));
    }
}
