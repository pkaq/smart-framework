# Smart Framework

## 简介

### 1. 它是一款轻量级 Java Web 框架

- 内置 IOC、AOP、ORM、DAO、MVC 等特性
- 基于 Servlet 3.0 规范
- 使用 Java 注解取代 XML 配置

### 2. 它使应用充分做到“前后端分离”

- 客户端可使用 HTML 或 JSP 作为视图模板
- 服务端可发布 REST 服务（使用 REST 插件）
- 客户端通过 AJAX 获取服务端数据并进行界面渲染

### 3. 它可提高应用程序的开发效率

- 面向基于 Web 的中小规模的应用程序
- 新手能在较短时间内入门
- 核心具有良好的定制性且插件易于扩展

![架构](http://i.imgur.com/NrH99Zu.png)

## 入门

### 1. 创建一个 Maven Web 工程

整个工程的目录结构如下：

```
smart-sample/
　　┗ src/
　　　　┗ main/
　　　　　　┗ java/
　　　　　　┗ resources/
　　　　　　┗ webapp/
　　┗ pom.xml
```

在 `java` 目录下，创建以下包名目录结构：

```
org/
　　┗ smart4j/
　　　　┗ sample/
　　　　　　┗ action/
　　　　　　┗ entity/
　　　　　　┗ service/
```

可见，基础包名为：org.smart4j.sample，下面的配置中会用到它。

### 2. 配置 Maven 依赖

编辑 `pom.xml` 文件，添加 `smart-framework` 依赖：

```xml
<dependency>
    <groupId>org.smart4j</groupId>
    <artifactId>smart-framework</artifactId>
    <version>[版本号]</version>
</dependency>
```

> 提示：需要指定具体的版本号。若使用相关 Smart 插件，则需分别配置。

### 3. 编写 Smart 配置

在 `resources` 目录下，创建一个名为 `smart.properties` 的文件，内容如下：

```
smart.framework.app.base_package=org.smart4j.sample
smart.framework.app.home_page=/users

smart.framework.jdbc.driver=com.mysql.jdbc.Driver
smart.framework.jdbc.url=jdbc:mysql://localhost:3306/smart-sample
smart.framework.jdbc.username=root
smart.framework.jdbc.password=root
```

> 提示：需根据实际情况修改以上配置。

### 4. 编写 Entity 类

```java
package org.smart4j.sample.entity;

import org.smart4j.framework.orm.annotation.Entity;

@Entity
public class User {

    private long id;

    private String username;

    private String password;

    // getter/setter
}
```

### 5. 编写 Service 接口及其实现

Service 接口

```java
package org.smart4j.sample.service;

import java.util.List;
import java.util.Map;
import org.smart4j.sample.entity.User;

public interface UserService {

    List<User> findUserList();

    User findUser(long id);

    boolean saveUser(Map<String, Object> fieldMap);

    boolean updateUser(long id, Map<String, Object> fieldMap);

    boolean deleteUser(long id);
}
```

Service 实现

```java
package org.smart4j.sample.service.impl;

import java.util.List;
import java.util.Map;
import org.smart4j.framework.orm.DataSet;
import org.smart4j.framework.tx.annotation.Service;
import org.smart4j.framework.tx.annotation.Transaction;
import org.smart4j.sample.entity.User;
import org.smart4j.sample.service.UserService;

@Service
public class UserServiceImpl implements UserService {

    @Override
    public List<User> findUserList() {
        return DataSet.selectList(User.class);
    }

    @Override
    public User findUser(long id) {
        return DataSet.select(User.class, "id = ?", id);
    }

    @Override
    @Transaction
    public boolean saveUser(Map<String, Object> fieldMap) {
        return DataSet.insert(User.class, fieldMap);
    }

    @Override
    @Transaction
    public boolean updateUser(long id, Map<String, Object> fieldMap) {
        return DataSet.update(User.class, fieldMap, "id = ?", id);
    }

    @Override
    @Transaction
    public boolean deleteUser(long id) {
        return DataSet.delete(User.class, "id = ?", id);
    }
}
```

### 5. 编写 Action 类

```java
package org.smart4j.sample.action;

import java.util.List;
import java.util.Map;
import org.smart4j.framework.ioc.annotation.Inject;
import org.smart4j.framework.mvc.DataContext;
import org.smart4j.framework.mvc.annotation.Action;
import org.smart4j.framework.mvc.annotation.Request;
import org.smart4j.framework.mvc.bean.Params;
import org.smart4j.framework.mvc.bean.Result;
import org.smart4j.framework.mvc.bean.View;
import org.smart4j.sample.entity.User;
import org.smart4j.sample.service.UserService;

@Action
public class UserAction {

    @Inject
    private UserService userService;

    @Request.Get("/users")
    public View index() {
        List<User> userList = userService.findUserList();
        DataContext.Request.put("userList", userList);
        return new View("user.jsp");
    }

    @Request.Get("/user")
    public View create() {
        return new View("user_create.jsp");
    }

    @Request.Post("/user")
    public Result save(Params params) {
        Map<String, Object> fieldMap = params.getFieldMap();
        boolean result = userService.saveUser(fieldMap);
        return new Result(result);
    }

    @Request.Get("/user/{id}")
    public View edit(long id) {
        User user = userService.findUser(id);
        DataContext.Request.put("user", user);
        return new View("user_edit.jsp");
    }

    @Request.Put("/user/{id}")
    public Result update(long id, Params params) {
        Map<String, Object> fieldMap = params.getFieldMap();
        boolean result = userService.updateUser(id, fieldMap);
        return new Result(result);
    }

    @Request.Delete("/user/{id}")
    public Result delete(long id) {
        boolean result = userService.deleteUser(id);
        return new Result(result);
    }
}
```

### 6. 编写视图

在 Action 中使用了 JSP 作为视图展现技术，需要编写以下 JSP 文件：

- user.jsp
- user_list.jsp
- user_create.jsp
- user_edit.jsp

> 提示：更多相关细节，请参考 [Smart Sample](http://git.oschina.net/huangyong/smart-sample) 示例。

## 提高

TODO

## 示例

- Smart Sample：http://git.oschina.net/huangyong/smart-sample
- Smart Bootstrap：http://git.oschina.net/huangyong/smart-bootstrap
- Smart REST Server：http://git.oschina.net/huangyong/smart-rest-server
- Smart REST Client：http://git.oschina.net/huangyong/smart-rest-client

## 附录

### 相关插件

> 注意：插件依赖于框架，不能独立使用。

- [smart-plugin-security](http://git.oschina.net/huangyong/smart-plugin-security) -- 基于 [Apache Shiro](http://shiro.apache.org/) 的安全控制插件
- [smart-plugin-cache](http://git.oschina.net/huangyong/smart-plugin-cache) -- 基于注解的 Cache 插件
- [smart-plugin-i18n](http://git.oschina.net/huangyong/smart-plugin-i18n) -- 通用的 I18N 插件
- [smart-plugin-mail](http://git.oschina.net/huangyong/smart-plugin-mail) -- 基于 [Apache Commons Email](http://commons.apache.org/proper/commons-email/) 的邮件收发插件
- [smart-plugin-template](http://git.oschina.net/huangyong/smart-plugin-template) -- 基于 [Apache Velocity](http://velocity.apache.org/) 的模板引擎插件
- [smart-plugin-job](http://git.oschina.net/huangyong/smart-plugin-job) -- 基于 [Quartz](http://www.quartz-scheduler.org/) 的作业调度插件
- [smart-plugin-soap](http://git.oschina.net/huangyong/smart-plugin-soap) -- 基于 [Apache CXF](http://cxf.apache.org/) 的 SOAP Web Service 插件
- [smart-plugin-rest](http://git.oschina.net/huangyong/smart-plugin-rest) -- 基于 [Apache CXF](http://cxf.apache.org/) 的 REST Web Service 插件
- [smart-plugin-hessian](http://git.oschina.net/huangyong/smart-plugin-hessian) -- 基于 [Hessian](http://hessian.caucho.com/) 的 RMI 插件
- [smart-plugin-xmlrpc](http://git.oschina.net/huangyong/smart-plugin-xmlrpc) -- 基于 [Apache XML-RPC](http://ws.apache.org/xmlrpc/) 的 XML-RPC 插件
- [smart-plugin-search](http://git.oschina.net/huangyong/smart-plugin-search) -- 基于 [Apache Lucene](http://lucene.apache.org/) 的搜索引擎插件
- [smart-plugin-mybatis](http://git.oschina.net/free/smart-plugin-mybatis) -- 基于 [MyBatis](http://mybatis.github.io/mybatis-3/) 的数据持久层插件
- [smart-plugin-args](http://git.oschina.net/free/smart-plugin-args) -- 强大的 Action 方法参数绑定的插件
- [smart-plugin-c3p0](http://git.oschina.net/huangyong/smart-plugin-c3p0) -- 基于 [C3P0](http://sourceforge.net/projects/c3p0/) 的连接池插件
- [smart-plugin-druid](http://git.oschina.net/huangyong/smart-plugin-druid) -- 基于 [Druid](https://github.com/alibaba/druid) 的连接池插件

### 相关模块

> 注意：模块不依赖于框架，可以独立使用。

- [smart-sso](http://git.oschina.net/huangyong/smart-sso) -- 基于 [Jasig CAS](http://www.jasig.org/cas) 的 SSO 模块
- [smart-cache](http://git.oschina.net/huangyong/smart-cache) -- 通用的 Cache 模块与基于内存的实现
- [smart-cache-ehcache](http://git.oschina.net/huangyong/smart-cache-ehcache) -- 基于 [EhCache](http://www.ehcache.org/) 的 Cache 模块
- [smart-cache-redis](http://git.oschina.net/lujianing/smart-cache-redis) -- 基于 [Jedis](https://github.com/xetorthio/jedis/) 的 Cache 模块

### 参考资料

- Smart 系列博文：http://my.oschina.net/huangyong/blog/158380
- Maven 那点事儿：http://my.oschina.net/huangyong/blog/194583