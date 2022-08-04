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

public class SysUserSysRoleEntity extends BaseEntity<Long> implements Serializable {

	private static final long serialVersionUID = 1L;

    	

			/**
    * 
  	*/
		private Long userId;
	

			/**
    * 
  	*/
		private Long roleId;
	

		

		

	
}
