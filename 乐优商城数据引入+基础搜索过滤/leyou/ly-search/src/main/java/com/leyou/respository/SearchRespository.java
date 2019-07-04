package com.leyou.respository;

import com.leyou.pojo.Goods;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface SearchRespository extends ElasticsearchRepository<Goods,Long> {
}
