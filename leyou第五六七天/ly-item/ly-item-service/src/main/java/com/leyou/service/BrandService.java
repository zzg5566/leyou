package com.leyou.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.utils.BeanHelper;
import com.leyou.common.vo.PageResult;
import com.leyou.entity.Brand;
import com.leyou.item.dto.BrandDTO;
import com.leyou.mapper.BrandMapper;
import com.leyou.mapper.CategoryMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import tk.mybatis.mapper.entity.Example;

import java.util.Collections;
import java.util.List;
@Service
public class BrandService {
    @Autowired
    private BrandMapper brandMapper;

    public  PageResult<BrandDTO> queryPage(Integer page, Integer rows, String sortBy, Boolean desc, String search) {
        PageHelper.startPage(page,rows);
        Example example = new Example(Brand.class);
        Example.Criteria criteria = example.createCriteria();
        if (StringUtils.isNotBlank(search)){
            criteria.orLike("name","%"+search+"%").orEqualTo("letter",search.toUpperCase());
        }
        if (StringUtils.isNotBlank(sortBy)){
           example.setOrderByClause(sortBy+(desc?" DESC":" ASC"));
        }
        List<Brand> brands = brandMapper.selectByExample(example);

        if (CollectionUtils.isEmpty(brands)){
            throw new LyException(ExceptionEnum.BRAND_NOT_FOND);
        }
        PageInfo<Brand> pageInfo = new PageInfo<>(brands);
        List<BrandDTO> brandDTOS = BeanHelper.copyWithCollection(brands, BrandDTO.class);
        return new PageResult<>(pageInfo.getTotal(),pageInfo.getPages(), brandDTOS);
    }
  @Transactional
    public void addBrand(BrandDTO brandDTO, List<Long> cids) {
        Brand brand = BeanHelper.copyProperties(brandDTO, Brand.class);
        int num = brandMapper.insertSelective(brand);
        if (num!=1){
            throw new LyException(ExceptionEnum.ADD_BRAND_FAILL);
        }
        num=brandMapper.insertCategoryBrand(brand.getId(),cids);
        if (num!=cids.size()){
            throw  new LyException(ExceptionEnum.ADD_BRAND_FAILL);
        }

    }
  @Transactional
    public void updateBrand(BrandDTO brandDTO, List<Long> cids) {
        Brand brand = BeanHelper.copyProperties(brandDTO, Brand.class);
        int num = brandMapper.updateByPrimaryKeySelective(brand);
        if (num!=1){
            throw new LyException(ExceptionEnum.Update_BRAND_FAILL);
    }
        int count=brandMapper.selectBrandId(brand.getId());
       int i=brandMapper.deleteCategoryBrand(brand.getId());
       if (count!=i){
           throw new LyException(ExceptionEnum.BRANDID_NUM_FAILL);
       }
      num= brandMapper.insertCategoryBrand(brand.getId(),cids);
      if (num!=cids.size()){
          throw new LyException(ExceptionEnum.Update_BRAND_FAILL);
      }
    }
   public BrandDTO queryById(Long id){
       Brand brand = brandMapper.selectByPrimaryKey(id);
       if (brand==null){
           throw new LyException(ExceptionEnum.BRAND_NOT_FOND);
       }
       return  BeanHelper.copyProperties(brand,BrandDTO.class);
   }

    public List<BrandDTO> queryByCategoryId(Long id) {
        List<Brand> brand=brandMapper.selectBrandByCategoryId(id);
        if (brand==null){
            throw new LyException(ExceptionEnum.BRAND_NOT_FOND);
        }
        return  BeanHelper.copyWithCollection(brand,BrandDTO.class);
    }
}
