package com.leyou.mapper;

import com.leyou.common.mapper.BaseMapper;
import com.leyou.entity.Category;
import org.apache.ibatis.annotations.Param;



import java.util.List;

public interface CategoryMapper extends BaseMapper<Category> {
    List<Category> queryByBrandId(@Param("id") Long brandId);
}