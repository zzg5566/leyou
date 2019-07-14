package com.leyou.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.utils.BeanHelper;
import com.leyou.common.utils.JsonUtils;
import com.leyou.common.vo.PageResult;
import com.leyou.dto.GoodsDTO;
import com.leyou.item.ItemClient;
import com.leyou.item.dto.*;
import com.leyou.pojo.Goods;
import com.leyou.respository.SearchRespository;
import com.leyou.utils.SearchRequest;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.LongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilterBuilder;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class SearchService {
    @Autowired
    private ItemClient itemClient;

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;
    public Goods buildGoods(SpuDTO spuDTO){
        Goods goods = BeanHelper.copyProperties(spuDTO, Goods.class);
         //all
        String name = spuDTO.getName();
        String categoryNames=itemClient.queryCategoryByIds(spuDTO.getCategoryIds()).stream().map(CategoryDTO::getName).collect(Collectors.joining(","));
        BrandDTO brandDTO = itemClient.queryById(spuDTO.getBrandId());
        String brandDTOName = brandDTO.getName();
        goods.setAll(name+" "+categoryNames+" "+brandDTOName);
        //categoryId
        goods.setCategoryId(spuDTO.getCid3());
        //createTime
        goods.setCreateTime(spuDTO.getCreateTime().getTime());

        //specs
        List<SpecParamDTO> specParamDTOS = itemClient.querySpecParams(null, spuDTO.getCid3(), true);
        SpuDetailDTO spuDetailDTO = itemClient.querySpuDetailById(spuDTO.getId());
        Map<String, Object> specs=new HashMap<>();
       Map<Long,Object> genericSpec = JsonUtils.nativeRead(spuDetailDTO.getGenericSpec(), new TypeReference<Map<Long,Object>>() {
        });
       Map<Long,List<String>> specialSpec=JsonUtils.nativeRead(spuDetailDTO.getSpecialSpec(), new TypeReference<Map<Long,List<String>>>() {
       });
        for (SpecParamDTO specParamDTO : specParamDTOS) {
            String key = specParamDTO.getName();
            Long id = specParamDTO.getId();
            Object value=null;
            if (specParamDTO.getGeneric()){
                value = genericSpec.get(id);
            }else{
                value=specialSpec.get(id);
            }
            if (specParamDTO.getNumeric()){
                value=chooseSegment(value,specParamDTO);
            }
            specs.put(key,value);
        }

        goods.setSpecs(specs);
        //skus
        List<SkuDTO> skuDTOS = itemClient.querySkuBySpuId(spuDTO.getId());
        List<Map<String,Object>> skuList=new ArrayList<>();
        skuDTOS.forEach(skuDTO -> {
            HashMap<String, Object> map = new HashMap<>();
            map.put("id",skuDTO.getId());
            map.put("price",skuDTO.getPrice());
            map.put("title",skuDTO.getTitle());
            map.put("image",StringUtils.substringBefore(skuDTO.getImages(),","));
            skuList.add(map);
        });
       goods.setSkus(JsonUtils.toString(skuList));
        //price
       Set<Long> price= skuDTOS.stream().map(SkuDTO::getPrice).collect(Collectors.toSet());
       goods.setPrice(price);
        return goods;
    }
    private String chooseSegment(Object value, SpecParamDTO p) {
        if (value == null || StringUtils.isBlank(value.toString())) {
            return "其它";
        }
        double val = parseDouble(value.toString());
        String result = "其它";
        // 保存数值段
        for (String segment : p.getSegments().split(",")) {
            String[] segs = segment.split("-");
            // 获取数值范围
            double begin = parseDouble(segs[0]);
            double end = Double.MAX_VALUE;
            if (segs.length == 2) {
                end = parseDouble(segs[1]);
            }
            // 判断是否在范围内
            if (val >= begin && val < end) {
                if (segs.length == 1) {
                    result = segs[0] + p.getUnit() + "以上";
                } else if (begin == 0) {
                    result = segs[1] + p.getUnit() + "以下";
                } else {
                    result = segment + p.getUnit();
                }
                break;
            }
        }
        return result;
    }

    private double parseDouble(String str) {
        try {
            return Double.parseDouble(str);
        } catch (Exception e) {
            return 0;
        }
    }

    public PageResult<GoodsDTO> search(SearchRequest searchRequest) {
        String key = searchRequest.getKey();
        if (StringUtils.isBlank(key)){
            throw new LyException(ExceptionEnum.INVALID_PARAM_ERROR);
        }
        NativeSearchQueryBuilder searchQueryBuilder=new NativeSearchQueryBuilder();
        searchQueryBuilder.withSourceFilter(new FetchSourceFilter(new String[]{"id","skus","subTitle"},null));
       searchQueryBuilder.withQuery(buildBasicQuery(searchRequest));
        searchQueryBuilder.withPageable(PageRequest.of(searchRequest.getPage()-1,searchRequest.getSize()));

        AggregatedPage<Goods> aggregatedPage = elasticsearchTemplate.queryForPage(searchQueryBuilder.build(), Goods.class);
        List<Goods> content = aggregatedPage.getContent();
        return new PageResult<>(aggregatedPage.getTotalElements(),aggregatedPage.getTotalPages(),BeanHelper.copyWithCollection(content,GoodsDTO.class));
    }

    public Map<String, List<?>> queryFilters(SearchRequest searchRequest) {
         NativeSearchQueryBuilder searchQueryBuilder=new NativeSearchQueryBuilder();
         searchQueryBuilder.withSourceFilter(new FetchSourceFilter(new String[]{""},null));
         searchQueryBuilder.withPageable(PageRequest.of(0,1));
        QueryBuilder queryBuilder = buildBasicQuery(searchRequest);
        searchQueryBuilder.withQuery(queryBuilder);
         searchQueryBuilder.addAggregation(AggregationBuilders.terms("categoryAgg").field("categoryId"));
         searchQueryBuilder.addAggregation(AggregationBuilders.terms("brandAgg").field("brandId"));
        AggregatedPage<Goods> goods = elasticsearchTemplate.queryForPage(searchQueryBuilder.build(), Goods.class);
        Aggregations aggregations = goods.getAggregations();
       LongTerms cAgg = aggregations.get("categoryAgg");
        LongTerms bAgg = aggregations.get("brandAgg");
        List<Long> list = cAgg.getBuckets().stream().map(LongTerms.Bucket::getKeyAsNumber).map(Number::longValue).collect(Collectors.toList());
        List<Long> bList = bAgg.getBuckets().stream().map(LongTerms.Bucket::getKeyAsNumber).map(Number::longValue).collect(Collectors.toList());
        Map<String,List<?>> map=new HashMap<>();
        List<CategoryDTO> categoryDTOS = itemClient.queryCategoryByIds(list);
        List<BrandDTO> brandDTOS=itemClient.queryBrandByIds(bList);
        if (list!=null&&list.size()==1){
            handelSpecAgg(list.get(0),queryBuilder,map);
        }
        map.put("分类",categoryDTOS);
        map.put("品牌",brandDTOS);
        return map ;

    }

    private void handelSpecAgg(Long id, QueryBuilder queryBuilder , Map<String,List<?>> map) {
        List<SpecParamDTO> specParamDTOS = itemClient.querySpecParams(null, id, true);
        NativeSearchQueryBuilder searchQueryBuilder = new NativeSearchQueryBuilder();
        searchQueryBuilder.withPageable(PageRequest.of(0,1));

        searchQueryBuilder.withSourceFilter(new FetchSourceFilterBuilder().build());
        searchQueryBuilder.withQuery(queryBuilder);
        for (SpecParamDTO specParamDTO : specParamDTOS) {
            searchQueryBuilder.addAggregation(AggregationBuilders.terms(specParamDTO.getName()).field("specs."+specParamDTO.getName()+".keyword"));
        }
        AggregatedPage<Goods> goods = elasticsearchTemplate.queryForPage(searchQueryBuilder.build(), Goods.class);
        Aggregations aggregations = goods.getAggregations();
        for (SpecParamDTO specParamDTO : specParamDTOS) {
            String name = specParamDTO.getName();
            StringTerms stringTerms = aggregations.get(name);
            map.put(name,stringTerms.getBuckets().stream()
                    .map(StringTerms.Bucket::getKeyAsString).collect(Collectors.toList()));
        }
    }
    @Autowired
    private SearchRespository searchRespository;
    public QueryBuilder buildBasicQuery(SearchRequest searchRequest){
        BoolQueryBuilder boolQueryBuilder=QueryBuilders.boolQuery();
        boolQueryBuilder.must(QueryBuilders.matchQuery("all",searchRequest.getKey()).operator(Operator.AND));
        if (searchRequest.getFilter()==null) {
            throw new LyException(ExceptionEnum.DATA_INSETR_ERROR);
        }
            Set<Map.Entry<String, String>> entries = searchRequest.getFilter().entrySet();
            for (Map.Entry<String, String> entry : entries) {
                String key = entry.getKey();
                if ("分类".equals(key)) {
                    key = "categoryId";
                } else if ("品牌".equals(key)) {
                    key = "brandId";
                } else {
                    key = "specs." + key + ".keyword";
                }
                boolQueryBuilder.filter(QueryBuilders.termQuery(key, entry.getValue()));

            }

            return boolQueryBuilder;

    }
    public void createIndex(Long id){
        // 查询spu
        SpuDTO spu = itemClient.querySpuById(id);
        // 构建成goods对象
        Goods goods = buildGoods(spu);
        // 保存数据到索引库
        searchRespository.save(goods);
    }

    public void deleteById(Long id) {
        searchRespository.deleteById(id);
    }

}
