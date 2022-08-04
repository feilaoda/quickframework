package com.quickpaas.shop.system.entity;

import com.baomidou.mybatisplus.annotation.TableField;
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

public class SysRoleEntity extends BaseEntity<Long> implements Serializable {

	private static final long serialVersionUID = 1L;

    	

			/**
    * 
  	*/
		private String name;
	

			/**
    * 
  	*/
		private Integer idx;
	

			/**
    * 
  	*/
		private String tips;
	

			/**
    * 
  	*/
		private Integer version;
	

		

		

	
}
