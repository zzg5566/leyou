package com.leyou.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.utils.BeanHelper;
import com.leyou.common.vo.PageResult;
import com.leyou.entity.Brand;
import com.leyou.entity.Sku;
import com.leyou.entity.Spu;
import com.leyou.entity.SpuDetail;
import com.leyou.item.dto.*;
import com.leyou.mapper.*;
import com.thoughtworks.xstream.io.xml.BEAStaxDriver;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Service
public class GoodsService {
    @Autowired
    private GoodsMapper goodsMapper;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private BrandService brandService;
    @Autowired
    private DetailMapper detailMapper;
    @Autowired
    private SkuMapper skuMapper;
    public PageResult<SpuDTO> querySpuByPage(String key, Boolean saleable, Integer page, Integer rows) {
        PageHelper.startPage(page,rows);
        Example example = new Example(Spu.class);
        Example.Criteria criteria = example.createCriteria();
        if (StringUtils.isNotBlank(key)){
            criteria.andLike("name","%"+key+"%");
        }
        if (saleable!=null){
            criteria.andEqualTo("saleable",saleable);
        }
        List<Spu> spus = goodsMapper.selectByExample(example);
        if (CollectionUtils.isEmpty(spus)){
            throw new LyException(ExceptionEnum.GOODS_NOT_FOUND);
        }

        PageInfo<Spu> spuPageInfo = new PageInfo<>(spus);
        List<SpuDTO> spuDTOS = BeanHelper.copyWithCollection(spus, SpuDTO.class);
        spuDTOS.forEach(spuDTO->{
            String categoryName = categoryService.queryCategoryByIds(spuDTO.getCategoryIds()).stream()
                    .map(CategoryDTO::getName).collect(Collectors.joining("/"));
            spuDTO.setCategoryName(categoryName);
            BrandDTO brandDTO = brandService.queryById(spuDTO.getBrandId());
            spuDTO.setBrandName(brandDTO.getName());
        });
        PageResult<SpuDTO> pageResult = new PageResult<>(spuPageInfo.getTotal(), spuPageInfo.getPages(), spuDTOS);
        return  pageResult;
    }
  @Transactional
    public void saveGoods(SpuDTO spuDTO) {
        Spu spu = BeanHelper.copyProperties(spuDTO, Spu.class);
        spu.setId(null);
        spu.setCreateTime(new Date());
        spu.setSaleable(null);
        int i = goodsMapper.insertSelective(spu);
        if (i!=1){
            throw new LyException(ExceptionEnum.DATA_INSETR_ERROR);
        }
        SpuDetailDTO spuDetailDto = spuDTO.getSpuDetail();
        spuDetailDto.setSpuId(spu.getId());
        SpuDetail spuDetail = BeanHelper.copyProperties(spuDetailDto, SpuDetail.class);
        int i1 = detailMapper.insertSelective(spuDetail);
        if (i1!=1){
            throw new LyException(ExceptionEnum.DATA_INSETR_ERROR);
        }
        List<SkuDTO> skus = spuDTO.getSkus();
        skus.forEach(skuDTO -> {
            skuDTO.setSpuId(spu.getId());
            Sku sku = BeanHelper.copyProperties(skuDTO, Sku.class);
            skuMapper.insertSelective(sku);
        });
    }
@Transactional
    public void updateSpuSaleable(Long id, Boolean saleable) {
        Spu spu = new Spu();
        spu.setId(id);
        spu.setSaleable(saleable);
        int i = goodsMapper.updateByPrimaryKeySelective(spu);
        if (i!=1){
            throw new LyException(ExceptionEnum.Update_Spu_FAILL);
        }
        Sku sku = new Sku();
        sku.setEnable(saleable);
        Example example = new Example(Sku.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("spuId",id);
        int count = skuMapper.updateByExampleSelective(sku, example);
        if (count!=1){
            throw new LyException(ExceptionEnum.Update_SKU_FAILL);
        }
    }

    public SpuDetailDTO querySpuDetailById(Long id) {

        SpuDetail spuDetail= detailMapper.selectByPrimaryKey(id);
      if (spuDetail==null){
          throw new LyException(ExceptionEnum.GOODS_NOT_FOUND);
      }
     return  BeanHelper.copyProperties(spuDetail,SpuDetailDTO.class);

    }

    public List<SkuDTO> querySkuBySpuId(Long id) {
        Sku sku = new Sku();
        sku.setSpuId(id);
        List<Sku> skus = skuMapper.select(sku);
        if (CollectionUtils.isEmpty(skus)){
            throw new LyException(ExceptionEnum.GOODS_NOT_FOUND);
        }
        return BeanHelper.copyWithCollection(skus,SkuDTO.class);
    }

    public void updateGoods(SpuDTO spuDTO) {
        Spu spu = BeanHelper.copyProperties(spuDTO, Spu.class);
       spu.setSaleable(null);
        int i = goodsMapper.updateByPrimaryKeySelective(spu);
        if (i!=1){
            throw new LyException(ExceptionEnum.Update_Spu_FAILL);
        }
        SpuDetailDTO spuDetailDTO = spuDTO.getSpuDetail();
        spuDetailDTO.setSpuId(spuDTO.getId());
        SpuDetail spuDetail = BeanHelper.copyProperties(spuDetailDTO, SpuDetail.class);
        int i1 = detailMapper.updateByPrimaryKeySelective(spuDetail);
        if (i1!=1){
            throw new LyException(ExceptionEnum.Update_SpuDetail_FAILL);
        }
        Sku sku = new Sku();
        sku.setSpuId(spuDTO.getId());
        int i2 = skuMapper.selectCount(sku);
        int delete = skuMapper.delete(sku);
        if (i2!=delete){
            throw new LyException(ExceptionEnum.DELETE_SKU_FAILL);
        }
        List<SkuDTO> skus = spuDTO.getSkus();
        skus.forEach(skuDTO -> {
            skuDTO.setSpuId(spuDTO.getId());
            skuDTO.setEnable(false);
            Sku sku1 = BeanHelper.copyProperties(skuDTO, Sku.class);
            skuMapper.insertSelective(sku1);
        });
    }
}
