package com.leyou.entity;

import lombok.Data;
import tk.mybatis.mapper.annotation.KeySql;

import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Table(name = "tb_spec_group")
@Data
public class Spec {
    @Id
    @KeySql(useGeneratedKeys = true)
    private Long id;
    private  Long cid;
    private  String name;
    private Date create_time;
    private Date update_time;
}
