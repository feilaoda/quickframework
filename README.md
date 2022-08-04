QuickFramework 快速开发框架





```java
@TableName("sys_user")
@Data
public class SysUser extends BaseEntity<Long>{
    private Long id;
    private String name;
    private Long departmentId;

    @ManyToMany(joinEntity=SysUserSysRole.class, leftMappedBy="userId", rightMappedBy="roleId")
    @TableField(exist = false)    
    private List<SysRole> roles;

    @ManyToOne(mappedBy="departmentId")
    @TableField(exist = false)
    private SysDepartment department;
}

@TableName("sys_role")
@Data
public class SysRole extends BaseEntity<Long>{
    private Long id;
    private String name;
    
}

@TableName("sys_department")
@Data
public class SysDepartment extends BaseEntity<Long>{
    private Long id;
    private String name;
}

@TableName("sys_user_role")
@Data
public class SysUserSysRole extends BaseEntity<Long>{
    private Long id;
    private Long userId;
    private Long roleId;
}


public interface SysUserService extends BaseService<SysUser> {

}

public class SysUserController extends BaseShopController<SysUser> {
    
}

```



查询User列表(不带roles, department属性)

```java
{
    "where": {},
    "fields":[],
    "page":{
        "pageSize": 10,
        "current": 1
    }
}
```

查询User列表(带roles, department属性)

```java
{
    "fields":[
        "roles",
        "department"
    ],
    "page":{
        "pageSize":10,
        "current":1
    }
}
```

查询角色是admin的User列表(带roles, department属性)

```java
{
    "where":{
        "roles.name":"admin"
    },
    "fields":[
        "roles",
        "department"
    ],
    "page":{
        "pageSize":10,
        "current":1
    }
}
```

查询角色是admin或guest，部门id为1的User列表(带roles, department属性)

```java
{
    "where":{
        "department.id":1,
        "roles.name":{"@IN": ["admin","guest"]}
    },
    "fields":[
        "roles",
        "department"
    ],
    "page":{
        "pageSize":10,
        "current":1
    }
}
```

```json
{
    "code": 0,
    "message": null,
    "data": [
        {
            "id": "1473593908553347074",
            "createdAt": "2021-12-22 17:59:00",
            "updatedAt": "2022-01-11 15:09:39",
            "account": "test14",
            "avatar": null,
            "departmentId": "1001",
            "email": null,
            "name": "test14",
            "phone": "12345",
            "salt": null,
            "gender": 1,
            "status": 1,
            "version": null,
            "department": {
                "id": "1001",
                "createdAt": null,
                "updatedAt": "2022-01-13 13:25:14",
                "name": "部门22",
                "title": "部门2",
                "num": 1,
                "parentId": 1000,
                "parentIds": null,
                "tips": null,
                "version": null,
                "createdBy": null,
                "updatedBy": null,
                "parent": null,
                "users": null
            },
            "roles": [
                {
                    "id": "1",
                    "createdAt": "2019-01-13 14:18:21",
                    "updatedAt": "2022-07-29 16:31:02",
                    "name": "admin",
                    "idx": 1,
                    "tips": "administrator",
                    "version": 1,
                    "permissions": null,
                    "menus": null
                },
                {
                    "id": "2",
                    "createdAt": "2019-01-13 14:18:21",
                    "updatedAt": "2022-01-19 14:13:14",
                    "name": "test1a",
                    "idx": 1,
                    "tips": "developer",
                    "version": null,
                    "permissions": null,
                    "menus": null
                },
            ]
        },
        {
            "id": "2",
            "createdAt": "2021-12-14 11:15:38",
            "updatedAt": "2022-08-02 12:34:00",
            "account": "test",
            "avatar": null,
            "departmentId": "1001",
            "email": null,
            "name": "test",
            "phone": "133",
            "salt": null,
            "gender": 0,
            "status": 1,
            "version": null,
            "department": {
                "id": "1001",
                "createdAt": null,
                "updatedAt": "2022-01-13 13:25:14",
                "name": "部门22",
                "title": "部门2",
                "num": 1,
                "parentId": 1000,
                "parentIds": null,
                "tips": null,
                "version": null,
                "createdBy": null,
                "updatedBy": null,
                "parent": null,
                "users": null
            },
            "roles": [
                {
                    "id": "1",
                    "createdAt": "2019-01-13 14:18:21",
                    "updatedAt": "2022-07-29 16:31:02",
                    "name": "admin",
                    "idx": 1,
                    "tips": "administrator",
                    "version": 1,
                    "permissions": null,
                    "menus": null
                },
                {
                    "id": "2",
                    "createdAt": "2019-01-13 14:18:21",
                    "updatedAt": "2022-01-19 14:13:14",
                    "name": "test1a",
                    "idx": 1,
                    "tips": "developer",
                    "version": null,
                    "permissions": null,
                    "menus": null
                },
                
            ]
        }
    ],
    "page": {
        "pages": 2,
        "total": 4,
        "current": 1,
        "size": 2,
        "records": null
    },
    "success": true
}
```
支持的查询操作符有

```sql
@EQ("=")等于
@GT(">")大于
@GE(">=")大于等于
@LE("<=")小于等于
@NE("<>")不等于
@LT("<")小于
@CONTAINS("like")模糊查询
@IN("in")
@NOTNULL("is not null")
@ISNULL("is null")
```


