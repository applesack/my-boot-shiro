# **`My-Boot-Shiro`**

**以`bootshiro`为模板，做为学习`shiro`框架的参考。**  
**对原项目的一些部分做了改造和重写。**

## **基本信息**

> `bootshiro`是基于`springboot+shiro+jwt`的真正`restful URL`资源无状态认证权限管理系统的后端项目。
>
> 此项目的特点在于提供可配置式的、动态的 `restful api` 安全管理支持。
>
> 数据传输动态秘钥加密,`jwt`过期刷新,用户操作监控等，加固应用安全。

- 开发环境: `jdk14`, `maven`, `mysql`, `redis`
- 主要技术栈: `springboot`, `springmvc`, `mybatis`, `shiro`

---

## 线上演示

- [前端界面](http://121.5.60.31:4100/)
- [Swagger文档](http://121.5.60.31:8080/doc.html)



---

## **一些功能实现的细节**

整个项目只有5个可供请求的controller，其中一个用于用户管理账户操作(注册、登陆等)，其他主要负责提供视图显示需要的数据。

按照`shiro`中的过滤器划分，所有的资源的权限管理有两种实现方式。

- 一个是关于账户的操作，在系统内会被`PasswordFilter`过滤。由于账户操作在前后端交互过程中，会涉及到一些敏感数据，例如用户密码，为了保证密码的安全性，在表单传输的过程中进行加密操作。
- 另一个负责过滤其他所有路径的过滤器，由`BonJwtFilter`处理，对于其他的路径，存在权限问题的，这个过滤器将拦截没有指定权限的用户访问某资源，另外管理`jwt`的过期重新签发。



###  *注册流程*

1. 由客户端发起向服务器获取`userKey`和`tokenKey`的请求。

2. 服务端在过滤器中拦截了索要`userKey`和`tokenKey`的请求，  
   生成了`userKey`和`tokenKey`并把`tokenKey`存储在`redis`中，然后把这两个信息返回给客户端，其中

    - `tokenKey`: 随机生成的一个字符串，用于`Aes`加解密。

    - `userKey`：是一个6位长度的随机字符串，做为正在执行注册/登陆操作的用户的标识，持有`userKey`则可以表示一个用户，

      在`redis`中存储token的key是当前执行此操作的用户的ip，只有ip和`userKey`对上了才可以取出一个用户的数据

3. 客户端收到响应，使用`tokenKey`加密了密码，并在提交注册请求的时候，把`userKey`带上。

4. 服务端中的过滤器收到了注册请求，确认注册请求中包含了注册所需的数据后，放行到controller层，  
   由controller根据请求中的`userKey`去`redis`中查找对应的`tokenKey`，对客户端提交的密码进行解密，  
   假如这个过程没有出错，则将用户数据写入数据库，并告诉客户端注册成功。

整个注册流程需要客户端请求两次，为了确保用户密码在传输过程中是可解密的加密内容。

另外，在将用户数据存储到数据库的时候，会先产生一个salt，将salt与用户密码加在一起再次`md5`加密，得到的结果进行存储。  
并且，每个新注册的用户，默认分配一个guest访客角色。

```bash
# 伪代码 
client: GET /register?tokenKey=get; 
server: receive request, then
        generate userKey="QAZWSX", tokenKey="EDCRFVTGB";
        redis.set(userKey, tokenKey);
        return {userKey, tokenKey};
client: receive response, then
        password=encode(password, tokenKey)
        POST /register request_body={userKey, username, password}
server: receive request, then
        check(request);
        tokenKey=redis.get(request.userKey);
        password=decode(password, tokenKey);
        databases.save(new user());
        return success
over.        
```



### *登陆流程*

与注册的流程相似，也是需要两次请求，同样需要在传输过程中加密密码，区别在于登陆流程中会校验客户端提交的用户名和密码是否与数据库中的数据一致。
在验证密码是否一致的时候，由于存在salt，所以会把用户提交的密码进行同样的处理，再进行比对。

```bash
# 一步到三步参考注册流程，此时client已经准备好了用户名和加密的密码，
#        还有server传来的userKey，准备发送给server，而server端也在redis中存有userKey对应的tokenKey
server: receive request, then
        password = decode(password);
        user = database.loadBy(username);
        if user.username == username and user.password == password:
            generator jwt;
            return jwt;
        else
            return false;        
```



### `JWT`签发及处理

当用户登陆时，系统会在登陆消息的响应体内存放`jwt`返回给客户端，这个`jwt`内的信息包括用户标识(*用户名*)，一个过期时间(*5小时*)，还有这个用户具有的角色，客户端每次请求服务器需要在请求头上携带`jwt`信息。

登陆成功后，前台跳转到主页面，这个时候获取资源的路径都是`/log`，`/user`，`/resource`等，这样的路径就是`BonJwtFilter`的过滤范畴了。对于访问这些路径的请求，过滤器会检查请求头中的必备属性，例如`auth`和`appId`，假如没有就拦截该请求。然后尝试给当前访问此路径的用户进行登陆操作，登陆通过后当前用户的subject对象会带有权限信息，然后检查当前用户是否有获取此资源的权限，假如没有则拦截，有则放行。

前面提到了默认一个`jwt`的过期时间是5个小时，这个`jwt`同时在`redis`数据库中有一个副本，在`redis`中副本的有效期是10个小时，这样做的意义是，从登陆起开始计算(签发第一个`jwt`的时间点)，5小时后存在请求头中的`jwt`失效，服务端查询`redis`还能查询到用户的`jwt`信息，则对用户的`jwt`进行一个刷新操作，刷新过期时间同时更新`redis`数据库中的内容，用户的操作不会被打断。但是到了10小时以后，请求头和`redis`中的`jwt`都已经失效，则用户需要重新登陆(例如用户使用浏览器的数据直接访问就有可能出现`jwt`过期的问题)。



### 资源权限管理

> 在`Shiro`体系中存在几个概念，先过一下本项目中的`Shiro`配置。

- `SecurityManager` ==> `DefaultWebSecurityManager`

    - `Authenticator`(认证器): 重写，由原来遍历所有`realm`逐一调用，改为只调用支持此`token`的`realm`
    - `realms`(领域类)：两个，一个用于处理账号密码，一个用于处理`jwt`
    - `filter`：两个，分别对应两个`realm`
    - `matcher`：两个，分别对应两个`realm`
    - `SessionStorageEvaluator`：设置为不存储`session`



> 再过一下权限处理流程。

`request`  ==> `filter` ==> `controller`

1. 服务端接收请求

2. `Shiro`接管请求，并根据请求分配一个过滤链

3. 过滤链开始工作，处理请求。对于一个路径，可以有多个过滤器，每个过滤器都会接收一个参数表示当前路径需要具备的权限。过滤器中对当前用户执行`login`操作，然后判断当前用户是否具有权限，其中

    - token: 由过滤器根据当前用户的信息创建

    - info: 对当前用户执行`login`后`Shiro`调用对应的`Realm`创建Info

    - matcher:  执行`login`后生成的token和info由matcher进行匹配，匹配成功则登陆成功，反之亦然。

4. 假如步骤3没return false或者没抛异常，则这里就放行到controller层了，用户即可访问资源。

> 如何实现`RESTFul`风格的资源权限管理？

`RestFul`要求一个资源可以有多种请求方式，在这个项目中，请求资源所需要具备的条件都存储在数据库中，这样的一行记录包括资源路径(`ant`风格，支持通配符)，请求方法，需要的角色等。其中资源进行加工就变成了例如`/account/**==POST`这样的格式，假如这条记录的访问要求角色是`Anon`，则表示访问`/account/`下的路径使用`POST`方式不需要任何角色，使用这种方式实现了对路径的权限管理每种方式都可以有不同的规则。

另外，这个项目重写了`PathMatchingFilterChainResolver`类的`getChain`方法，对于上面的思路给予代码实现

1. `Shiro`每收到一个请求时，就会调用`getChain`方法，将这条请求映射到对应的过滤链上
2. 这个方法首先获取请求的`uri`，然后遍历项目中的所有资源访问条件(这些访问条件就是上文提到了数据库中的内容，在项目启动时就会将数据库中的记录保存到集合)，找到与这条请求的`uri`相匹配的访问条件(路径和请求方法都和访问条件对上)，假如找到了这个访问条件，则将这条请求映射到之前配置好的过滤链，假如没有，就直接放行，所以假如数据库中没有记录某条资源的信息，那么这个资源就不能被`Shiro`保护。

虽然没看`Shiro`的源码，但是从`debug`的经验来说，能大致推测`Shiro`的运行原理。刚开始上手的时候有几个容易弄混的概念。

- `filter`： 所有过滤器都继承自`NameableFilter`(从类继承意义上讲)，都可以给过滤器命名，例如项目中`PasswordFilter`命名为`auth`，覆盖了`Shiro`默认的`auth`过滤器。
- `FilterChains`：过滤链，过滤链由一个`LinkedHashMap`组成，在这个项目中，键是资源及访问方式，值是此种方法访问需要具备的权限，权限是一个字符串，例如`auth[role1,role2]`，它具有一个前缀，然后后面的中括号里就是角色的列表。
- 前面提到“重写`getChain`方法，自定义映射规则”中，这个映射，应该是把这个这个请求的资源对应的权限交给对应的过滤器，写点伪代码表达这个意思。

```bash
# 现在有两个过滤器，并且都已经注入到shiro中
filtersMap.put("auth", passwordFilter)
filtersMap.put("jwt", jwtFilter)
shiro.setFilters(filtersMap)

# 现在对于“/account/**”和“/user/**”路径有一个访问规则，并且访问规则也注入到了shiro中
chainsMap.put("/account/**==POST", "auth")
chainsMap.put("/user/**==GET", "jwt[user]")
shiro.setChains(chainsMap)

# 现在有一个请求
request = "/account/login", method=POST
# 重写了shiro的getChain方法中获取这条请求对应的访问规则的实现，实现逻辑为
# 遍历shiro中所有的匹配规则
for pattern in shiro.getChains():
    # 找到与当前路径相匹配的访问规则
    if match(pattern, request.uri):
        # 这条请求最终与"/account/**==POST"这个规则匹配
        chain = chainsMap.get(pattren)
        # 这个规则对应的资源的权限是"auth"，与之对应的是passwordFilter
        filter = filtersMap.get(chain.key)
        # passwordFilter处理了这个请求，同时把这个过滤规则需要的权限一同交给这个过滤器
        filter.isAccessAllowed(request, chain.value)
        return

# 现在又来的一个请求
request = "/user/info", method=GET
# 下面省略了，由上面的逻辑可知，这条请求是被jwtFilter进行处理的

# 假如一条请求是这样的
request = "/non", method=PUT
# 因为并没有与之对应的访问规则，所以遍历完所有的访问规则后都没有找到，结果是null
```

> 资源权限动态刷新

这个功能的实现还算是比较简单的，首先，关于资源及权限的信息都存储在数据库中，而数据库中的内容可用在应用启动后修改，要动态刷新资源权限，只要先修改数据库数据，再让`Shiro`重新加载过滤链就行了。

在项目中的做法是，专门用一个类管理过滤链的操作，这个类保留了`ShiroFilterFactoryBean`的引用，在需要刷新的时候，清空`FilterChainsMap`的数据，重新从载入数据库中的内容，此后，权限的规则就改变了。



----

## 部署

目前部署这个项目采用的方式的使用`docker`，另外使用的是`docker`的`Dockerfile`功能，外部依赖不是很多，部署也不是很困难(并不)

准备工作

- 一个`liunx`服务器
- 安装`vsftpd`和`docker`，只说一下大致流程，具体的细节百度上都有
- 项目打包好的`jar`包，还有一个写好的`Dockerfile`

连接上服务器后习惯将一些比较长比较重要的命令保存起来，下次用的时候直接粘贴或者小改一下，比较方便。这个项目主要依赖两个中间件，`redis`和`mysql`，将这两个中间件的镜像拉取下来，然后运行这两个镜像生成容器，运行命令稍微需要注意一下，因为`mysql`中存储的数据比较重要，所以不应该将数据留在容器中，通常的做法是将`mysql`的容器存储数据的路径与宿主机器的路径做一个映射，这样当容器重新启动或者重做容器数据不会丢失，假如`redis`需要做持久化也可以做这个操作。

- ```bash
  docker pull mysql:latest
  ```

- ```bash
  docker pull redis:latest
  ```

- ```bash
  docker run -itd --name redis -p 7373:6379 redis
  ```

- ```bash
  docker run --name mysql -p4406:3306 -v /data/bootshiro/mysql:/var/lib/mysql -e MYSQL_ROOT_PASSWORD={password} -d mysql
  ```

数据库准备好后，本地连接上远程服务器，把`sql`文件运行到数据库中，至此中间件算是准备好了。



然后是项目的源码，已经打包成`jar`包了，在`IDEA`或者`VsCode`中有`docker`的插件，可以直接把`Dockerfile`部署到远程服务器，这个提前需要在服务器的`docker`的服务文件中更改设置启用远程登陆。但是用插件的方式部署经常遇见各种棘手的问题，对于我这种初学者，我的解决方案是把写好的`Dockerfile`和`jar`包一同用`ftp`传到服务器上，在服务器上执行构建镜像的命令(这两个文件都在当前目录下)。

- ```bash
  docker build -t bootshiro .
  ```

这条命令会生成一个名为`bootshiro`的镜像，可以用`docker images`查看到，接下来运行这个镜像，命令:

- ```
  docker run -dit --name bootshiro --restart=always -p 8080:8080 -v /data/bootshiro/bootshiro/:/data/bootshiro bootshiro
  ```

因为这个`jar`包会在同目录产生日志，为了方便管理这些日志，也将存储`jar`包的路径与宿主机器做了映射，在实际部署过程中，我的`Dockerfile`内是没有`ADD`命令将`jar`包加入容器的，因为这个路径，也就是宿主机器的此位置已经有的jar包，这个容器只是在此位置运行`java -jar`的命令，后续假如需要发布更改后的`jar`包，停止并移除这个容器，将新的jar包替换旧的`jar`包后重新运行容器。
