package com.leyou.client;

import com.leyou.common.vo.PageResult;
import com.leyou.item.dto.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient("item-service")
public interface GoodsClient {
    @GetMapping("spu/page")
   PageResult<SpuDTO> querySpuByPage(
            @RequestParam(value = "key",required = false) String key,
            @RequestParam(value = "saleable",required = false) Boolean saleable,
            @RequestParam(value = "page",defaultValue = "1") Integer page,
            @RequestParam(value = "rows",defaultValue = "5")Integer rows);
    @GetMapping("brand/{id}")
   BrandDTO queryById(@PathVariable("id")Long id);
    @GetMapping("/category/list")
   List<CategoryDTO>  queryCategoryByIds(@RequestParam("ids") List<Long> ids);
    @GetMapping("sku/of/spu")
    List<SkuDTO> querySkuBySpuId(@RequestParam("id") Long id);
    @GetMapping("spec/params")
  List<SpecParamDTO> querySpecParams(
            @RequestParam(value = "gid",required = false) Long gid,
            @RequestParam(value = "cid",required = false) Long cid,
            @RequestParam(value = "searching",required = false)Boolean search);
    @GetMapping("spu/detail")
    SpuDetailDTO querySpuDetailById(@RequestParam("id")Long id);
   @GetMapping("brand/list")
    List<BrandDTO> queryBrandByIds(@RequestParam("ids") List<Long> ids);
}
