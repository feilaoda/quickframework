package com.quickpaas.shop.system.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.quickpaas.framework.domain.BaseEntity;
import lombok.Data;

import java.io.Serializable;

/**
 * Description:
 *
 * @author feilaoda
 * @email 
 * 
 */
@Data

public class SysMenuEntity extends BaseEntity<Long> implements Serializable {

	private static final long serialVersionUID = 1L;

    	

			/**
    * 编号
  	*/
		private String code;
	

			/**
    * 名称
  	*/
		private String name;
	

			/**
    * 是否隐藏
  	*/
		private Integer hidden;
	

			/**
    * 父菜单编号
  	*/
		private String parentCode;
	

			/**
    * 图标
  	*/
		private String icon;
	

			/**
    * 是否是菜单1:菜单,0:按钮
  	*/
		private Integer isMenu;
	

			/**
    * 是否默认打开1:是,0:否
  	*/
		private Integer isOpen;
	

			/**
    * 级别
  	*/
		private Integer levels;
	

			/**
    * 顺序
  	*/
		private Integer sort;
	

			/**
    * 状态1:启用,0:禁用
  	*/
		private Integer status;
	

			/**
    * 链接
  	*/
		private String url;
	

		

		

	
}
