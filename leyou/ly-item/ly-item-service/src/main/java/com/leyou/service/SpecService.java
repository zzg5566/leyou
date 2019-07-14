package com.leyou.service;

import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.utils.BeanHelper;
import com.leyou.entity.Spec;
import com.leyou.item.dto.SpecDTO;
import com.leyou.item.dto.SpecParamDTO;
import com.leyou.mapper.SpecMapper;
import com.leyou.mapper.SpecParamMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
@Service
public class SpecService {
    @Autowired
    private SpecMapper specMapper;
    @Autowired
    private SpecParamService specParamService;
    public List<SpecDTO> queryGroupByCategory(Long id) {
        Spec spec = new Spec();
        spec.setCid(id);
        List<Spec> specList = specMapper.select(spec);
        List<SpecDTO> specDTOList = BeanHelper.copyWithCollection(specList, SpecDTO.class);
        // 查询分类下所有规格参数
        List<SpecParamDTO> params =specParamService.querySpecParams(null, id, null);
        // 将规格参数按照groupId进行分组，得到每个group下的param的集合
        Map<Long, List<SpecParamDTO>> paramMap = params.stream()
                .collect(Collectors.groupingBy(SpecParamDTO::getGroupId));
        // 填写到group中
        for (SpecDTO specDTO : specDTOList) {
            specDTO.setParams(paramMap.get(specDTO.getId()));
        }
        return specDTOList;
    }

}
