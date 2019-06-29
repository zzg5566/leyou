package com.leyou.mapper;

import com.leyou.entity.Brand;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface BrandMapper extends Mapper<Brand> {
    int insertCategoryBrand(@Param("id") Long id,@Param("cids") List<Long> cids);
}