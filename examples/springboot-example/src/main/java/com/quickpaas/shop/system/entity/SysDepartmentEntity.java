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

public class SysDepartmentEntity extends BaseEntity<Long> implements Serializable {

	private static final long serialVersionUID = 1L;

    	

			/**
    * 
  	*/
		private String name;
	

			/**
    * 
  	*/
		private String title;
	

			/**
    * 
  	*/
		private Integer num;
	

			/**
    * 
  	*/
		private Long parentId;
	

			/**
    * 
  	*/
		private String parentIds;
	

			/**
    * 
  	*/
		private String tips;
	

			/**
    * 
  	*/
		private Integer version;
	

		

		

			/**
    * 创建人
  	*/
		private Long createdBy;
	

			/**
    * 最后更新人
  	*/
		private Long updatedBy;
	

	
}
