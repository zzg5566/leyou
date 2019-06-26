package com.leyou.item.pojo;

import lombok.Data;

@Data
public class CategoryDTO {
	private Long id;
	private String name;
	private Long parentId;
	private Boolean isParent;
	private Integer sort;
}