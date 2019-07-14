package com.leyou.item.dto;

import lombok.Data;
import tk.mybatis.mapper.annotation.KeySql;

import javax.persistence.Id;
import java.util.Date;
import java.util.List;

@Data
public class SpecDTO {

        private Long id;
        private  Long cid;
        private  String name;
        private List<SpecParamDTO> params;

}
