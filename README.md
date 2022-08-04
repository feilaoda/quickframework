QuickFramework 快速开发框架





```java
@TableName("sys_user")
@Data
public class SysUser implements BaseDoamin{
    private Long id;
    private String name;
    private Long departmentId;

    @ManyToMany(middle=SysUserSysRole.class, leftMappedBy="userId", rightMappedBy="roleId")
    @TableField(exist = false)    
    private List<SysRole> roles;

    @ManyToOne(mappedBy="departmentId")
    @TableField(exist = false)
    private SysDepartment department;
}

@TableName("sys_role")
@Data
public class SysRole implements BaseDoamin{
    private Long id;
    private String name;
    
}

@TableName("sys_department")
@Data
public class SysDepartment implements BaseDoamin{
    private Long id;
    private String name;
}

@TableName("sys_user_role")
@Data
public class SysUserSysRole implements BaseDoamin{
    private Long id;
    private Long userId;
    private Long roleId;
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


