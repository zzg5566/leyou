package com.leyou.service;

import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.utils.BeanHelper;
import com.leyou.entity.SpecParam;
import com.leyou.item.dto.SpecParamDTO;
import com.leyou.mapper.SpecParamMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
@Service
public class SpecParamService {
    @Autowired
    private SpecParamMapper specParamMapper;
    public List<SpecParamDTO> querySpecParams(Long gid,Long cid,Boolean search ) {
        SpecParam specParam = new SpecParam();
            specParam.setGroupId(gid);
           specParam.setCid(cid);
           specParam.setSearching(search);

        List<SpecParam> specParams = specParamMapper.select(specParam);
        if (CollectionUtils.isEmpty(specParams)){
            throw new LyException(ExceptionEnum.Params_NOT_FOUND);
        }
          return BeanHelper.copyWithCollection(specParams,SpecParamDTO.class);
    }
}
