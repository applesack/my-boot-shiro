# **my-boot-shiro**

**以`bootshiro`为模板，做为学习`shiro`框架的参考。**  
**对原项目的一些部分做了改造和重写。**

## **1.基本信息**

基于`shiro`实现的web权限管理系统。
- 开发环境: jdk8+, maven, mysql, redis
- 主要技术栈: springboot, springmvc, mybatis, shiro

## **2.功能的实现**

整个项目只有5个controller，总共可请求的路径约20多个，对于其中一些主要的功能写一些个人的理解。
所有的权限管理按照bootshiro中的过滤器来分有两种，一种负责过滤登陆注册请求，另外一种验证jwt的有效性。

### *2.1注册*

**注册流程**
1. 由客户端发起向服务器获取`userKey`和`tokenKey`的请求。
2. 服务端在过滤器中拦截了索要userKey和tokenKey的请求，  
   生成了userKey和tokenKey并把tokenKey存储在`redis`中，然后把这两个信息返回给客户端。
3. 客户端收到响应，使用tokenKey加密了要注册的密码，并在提交注册请求的时候，把userKey带上。
4. 服务端中的过滤器收到了注册请求，确认注册请求中包含了注册所需的数据后，放行到controller层，  
   由controller根据请求中的userKey去redis中查找对应的tokenKey，对客户端提交的密码进行解密，  
   假如这个过程没有出错，则将用户数据写入数据库，并告诉客户端注册成功。

整个注册流程需要客户端请求两次，为了确保用户密码在传输过程中是可解密的加密内容，  
所以服务端需要准备一个密钥，并在返回给客户端时在redis中做一个备份用于解密。

另外，在将用户数据存储到数据库的时候，会先产生一个salt，将salt与用户密码加在一起再次md5加密，得到的结果进行存储。  
并且，每个新注册的用户，默认分配一个guest访客角色。

```text
伪代码 
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
   
### *2.2登陆*

与注册的流程相似，也是需要两次请求，同样需要在传输过程中加密密码，区别在于登陆流程中会校验客户端提交的用户名和密码是否与数据库中的数据一致。 
在验证密码是否一致的时候，由于存在salt，所以会把用户提交的密码进行同样的处理，再进行比对。

```text
一步到三步参考注册流程，此时client已经准备好了用户名和加密的密码，
        还有server传来的userKey，准备发送给server，而server端也在redis中存有userKey对应的tokenKey
server: receive request, then
        password = decode(password);
        user = database.loadBy(username);
        if user.username == username and user.password == password:
            generator jwt;
            return jwt;
        else
            return false;        
```
