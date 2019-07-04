package com.leyou.mapper;

import com.leyou.common.mapper.BaseMapper;
import com.leyou.entity.Brand;
import org.apache.ibatis.annotations.Param;

import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface BrandMapper extends BaseMapper<Brand> {
    int insertCategoryBrand(@Param("id") Long id,@Param("cids") List<Long> cids);


    int deleteCategoryBrand(@Param("id") Long id);

    int selectBrandId(@Param("id") Long id);

    List<Brand> selectBrandByCategoryId(@Param("id") Long id);
}