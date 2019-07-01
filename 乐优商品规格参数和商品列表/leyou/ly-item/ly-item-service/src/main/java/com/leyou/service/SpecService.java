package com.leyou.service;

import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.utils.BeanHelper;
import com.leyou.entity.Spec;
import com.leyou.item.dto.SpecDTO;
import com.leyou.mapper.SpecMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
@Service
public class SpecService {
    @Autowired
    private SpecMapper specMapper;
    public List<SpecDTO> queryGroupByCategory(Long id) {
        Spec spec = new Spec();
        spec.setCid(id);
        List<Spec> specs = specMapper.select(spec);
        if (CollectionUtils.isEmpty(specs)){
            throw new LyException(ExceptionEnum.Spec_NOT_FOUND);
        }
       return  BeanHelper.copyWithCollection(specs,SpecDTO.class);
    }
}
