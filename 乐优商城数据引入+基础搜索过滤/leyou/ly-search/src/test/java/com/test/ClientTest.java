package com.test;

import com.leyou.LySearchApplication;
import com.leyou.client.GoodsClient;
import com.leyou.common.utils.BeanHelper;
import com.leyou.common.vo.PageResult;
import com.leyou.item.dto.SpuDTO;
import com.leyou.pojo.Goods;
import com.leyou.respository.SearchRespository;
import com.leyou.service.SearchService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = LySearchApplication.class)
public class ClientTest {
    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;
    @Autowired
    private GoodsClient goodsClient;
    @Autowired
    private  SearchService searchService;
    @Autowired
    private SearchRespository searchRespository;
    @Test
    public void  createdgoods(){
        elasticsearchTemplate.createIndex(Goods.class);
    }
    @Test
    public void putMapping(){
        elasticsearchTemplate.putMapping(Goods.class);
    }
    @Test
    public void testGoods(){
        Integer page=1;
        while(true){
            //通过分页查询spu信息封装到pageResult中，然后在遍历出每一个spu信息
            PageResult<SpuDTO> pageResult = goodsClient.querySpuByPage(null, null, page, 50);
            if (pageResult==null){
                break;
            }
            List<SpuDTO> items = pageResult.getItems();
            page++;

//            ArrayList<Goods> goodsArrayList = new ArrayList<>();
            items.forEach(item->{
               Goods goods=searchService.buildGoods(item);
                searchRespository.save(goods);
            });

        }
    }
}
