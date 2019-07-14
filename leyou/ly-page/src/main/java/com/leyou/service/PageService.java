package com.leyou.service;

import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.item.ItemClient;
import com.leyou.item.dto.BrandDTO;
import com.leyou.item.dto.CategoryDTO;
import com.leyou.item.dto.SpecDTO;
import com.leyou.item.dto.SpuDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Service
@Slf4j
public class PageService {
    @Autowired
    private  ItemClient itemClient;
    @Autowired
    private TemplateEngine templateEngine;

    @Value("${ly.static.itemDir}")
    private String itemDir;
    @Value("${ly.static.itemTemplate}")
    private String itemTemplate;


        public Map<String, Object> loadItemData(Long id) {
            // 查询spu
            SpuDTO spu = itemClient.querySpuById(id);
            // 查询分类集合
            List<CategoryDTO> categories = itemClient.queryCategoryByIds(spu.getCategoryIds());
            // 查询品牌
            BrandDTO brand = itemClient.queryById(spu.getBrandId());
            // 查询规格
            List<SpecDTO> specs = itemClient.queryGroupByCategory(spu.getCid3());
            // 封装数据
            Map<String, Object> data = new HashMap<>();
            data.put("categories", categories);
            data.put("brand", brand);
            data.put("spuName", spu.getName());
            data.put("subTitle", spu.getSubTitle());
            data.put("skus", spu.getSkus());
            data.put("detail", spu.getSpuDetail());
            data.put("specs", specs);
            return data;
    }
    public void createItemHtml(Long id) {
        // 上下文，准备模型数据
        Context context = new Context();
        // 调用之前写好的加载数据方法
        context.setVariables(loadItemData(id));
        // 准备文件路径
        File dir = new File(itemDir);
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                // 创建失败，抛出异常
                log.error("【静态页服务】创建静态页目录失败，目录地址：{}", dir.getAbsolutePath());
                throw new LyException(ExceptionEnum.DIRECTORY_WRITER_ERROR);
            }
        }
        File filePath = new File(dir, id + ".html");
        // 准备输出流
        try (PrintWriter writer = new PrintWriter(filePath, "UTF-8")) {
            templateEngine.process(itemTemplate, context, writer);
        } catch (IOException e) {
            log.error("【静态页服务】静态页生成失败，商品id：{}", id, e);
            throw new LyException(ExceptionEnum.FILE_WRITER_ERROR);
        }
    }
    public void deleteItemHtml(Long id) {
        File file = new File(itemDir, id + ".html");
        if(file.exists()){
            if (!file.delete()) {
                log.error("【静态页服务】静态页删除失败，商品id：{}", id);
                throw new LyException(ExceptionEnum.FILE_WRITER_ERROR);
            }
        }
    }
}
