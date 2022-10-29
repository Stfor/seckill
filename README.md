
许奥 032002244


# 工程简介

# 延伸阅读



## 需要解决的问题

1. 超卖问题
2. 降低QPS







# validated自定义注解

service当中经常会做重复得校验，这样得话我们就可以自定义注解来实现参数校验得过程

1. 先添加上依赖

   ```
           <!--        validation组件-->
           <dependency>
               <groupId>org.springframework.boot</groupId>
               <artifactId>spring-boot-starter-validation</artifactId>
           </dependency>
   ```

   

2. 在方法传入得实体类上加上@Valid注解，才能在使用方法时进行校验过程

3. 在传入方法得实体类当中得字段上添加上类似@NotBlank等判断注解，当然这里是自带我们可以自定义一个出来

   ## 自定义注解得过程

   - 首先先创建出一个注解并复制上最基本得一些参数

   - ```java
     @Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER, ElementType.TYPE_USE})
     @Retention(RetentionPolicy.RUNTIME)
     @Documented
     @Constraint(
             validatedBy = {IsMobileValidator.class} //这里是要注意的地方，添加上校验方法
     )
     public @interface IsMobile {
         boolean requied() default true; //参数是否必填 这个不是必要得自己添加
     
         String message() default "{手机号码格式错误}";
     
         Class<?>[] groups() default {};
     
         Class<? extends Payload>[] payload() default {};
     }
     ```

     校验方法

     ```java
     public class IsMobileValidator implements ConstraintValidator<IsMobile,String> {
         private boolean required = false;
         //这个是初始化得参数，获取是否是必填得参数等等
         @Override
         public void initialize(IsMobile constraintAnnotation) {
             required = constraintAnnotation.requied();
             ConstraintValidator.super.initialize(constraintAnnotation);
         }
     
     //真正的校验方法
         @Override
         public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
             if (required){
                 return ValidatorUtil.isMobile(s);
             }else {
                 if (StringUtils.isEmpty(s)){
                     return true;
                 }else {
                     return ValidatorUtil.isMobile(s);
                 }
             }
         }
     }
     ```

     其中的ValidatorUtil是自己写的判断手机号是否符合格式的方法也可以直接写在该校验方法中

     ```java
     public class ValidatorUtil {
     
         private static final Pattern mobile_patten = Pattern.compile("[1]([3-9])[0-9]{9}$");
     
         /**
          * 手机号码校验
          * @author LC
          * @operation add
          * @date 2:19 下午 2022/3/2
          * @param mobile
          * @return boolean
          **/
         public static boolean isMobile(String mobile) {
             if (StringUtils.isEmpty(mobile)) {
                 return false;
             }
             Matcher matcher = mobile_patten.matcher(mobile);
             return matcher.matches();
         }
     }
     ```





## 分布式session 的问题

之前的代码在我们之后一台应用系统，所有操作都在一台Tomcat上，没有什么问题。当我们部署多台 系统，配合Nginx的时候会出现用户登录的问题 

原因 由于 Nginx 使用默认负载均衡策略（轮询），请求将会按照时间顺序逐一分发到后端应用上。 也就是说刚开始我们在 Tomcat1 登录之后，用户信息放在 Tomcat1 的 Session 里。过了一会，请求 又被 Nginx 分发到了 Tomcat2 上，这时 Tomcat2 上 Session 里还没有用户信息，于是又要登录 

解决方法：

1. tomcat复制
   - 优点： 
     - 无需修改代码，只需要修改Tomcat配置
   - 缺点: 
     - session同步传输占用内网带宽
     - 多台Tomcat同步性能指数级下降
     - session占用内存，无法有效水平扩展
2. 前端存储
   - 优点：
     - 不占用服务端内存
   - 缺点：
     - 存在安全风险
     - 数据大小受到cookie的限制
     - 占用外网带宽
3. session粘滞
   - 优点：
     - 无需修改代码
     - 服务端可以水平扩展
   - 缺点：
     - 增加新机器，会重新Hash，导致重新登录
     - 应用重启，需要重新登录
4. 后端集中存储
   - 优点：
     - 安全
     - 容易水平扩展
   - 缺点：
     - 增加复杂度
     - 需要修改代码



1.使用springseesion进行解决，springsession配合上redis，首先就是配置好redis并引入redis依赖以及springsession的依赖，会自动将session放入redis当中

```
<!--Spring Boot Redis -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-redis</artifactId>
</dependency>

<!--对象池依赖-->
<dependency>
    <groupId>org.apache.commons</groupId>
    <artifactId>commons-pool2</artifactId>
</dependency>
<!--springsession依赖-->
<dependency>
    <groupId>org.springframework.session</groupId>
    <artifactId>spring-session-data-redis</artifactId>
</dependency>
```

2.使用redis来存储对象的方式，记得将上面的data-redis的依赖注释，key是cookie

在登录的时候，由后端使用UUID生成一个cookie，以cookie为key，查询到的user用户为value存入redis中，在controller返回user数据的时候再根据cookie的值从redis查出，如果对应的cookie有数据则说明已经登录



**每个controller方法上都加上一个User传参，但是这个传参又不是前端传递的，是我们后端自己通过cookie或者token从redis当中查询。所以这个时候我们一般会通过继承**HandlerMethodArgumentResolver**方法，查得到user的话就说明该用户已经登录——单点登录问题**

**其次就是需要一个配置类实现WebMvcConfigurer** 

```
@Configuration
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer {
    @Autowired
    private Argument userArgumentResolver;
//    @Autowired
//    private AccessLimitInterceptor accessLimitInterceptor;

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
//        WebMvcConfigurer.super.addArgumentResolvers(resolvers);
        resolvers.add(userArgumentResolver);
    }
}
```

**注意需要加注解**

supportsParameter 方法是进行判断哪些方法需要使用当当前的ArgumentResolver

resolveArgument方法是进行查询参数的方法，直接返回需要传递的对象



## 对登录的优化

说明：在index页面登录之后，在其余的后面的操作其他的页面我们都需要对用户是否登录做一个判断，如果在每个controller上都写上相应的方法就造成代码冗余

解决方法：拦截器等

#### 拦截器

#### MVC配置类 ArgumentResolver

- 首先	





## 异常处理

我们知道，系统中异常包括:编译时异常和运行时异常RuntimeException，前者通过捕获异常从而获取异常信息，后者主要通过规范代码开发、测试通过手段减少运行时异常的发生。在开发中，不管是dao层、service层还是controller层，都有可能抛出异常，在Springmvc中，能将所有类型的异常处理从各处理过程解耦出来，既保证了相关处理过程的功能较单一，也实现了异常信息的统一处理和维护。



springboot的全局异常处方式主要有两种

使用`@ControllerAdvice`和`@ExceptionHandler` 注解。

使用`**ErrorController类**`来实现



区别：

1. `@ControllerAdvice`方式只能处理控制器抛出的异常。此时请求已经进入控制器中。
2. `ErrorController`类方式可以处理所有的异常，包括未进入控制器的错误，比如404,401等错误
3. 如果应用中两者共同存在，则`@ControllerAdvice`方式处理控制器抛出的异常，`ErrorController类`方式处理未进入控制器的异常。
4. `@ControllerAdvice`方式可以定义多个拦截方法，拦截不同的异常类，并且可以获取抛出的异常信息，自由度更大



## 压测的使用Jmeter

因为以后的项目基本都是放在linux系统中运行，而window系统和linux系统是有区别的一般linux系统的QPS会比较低一些

![1660376299960](C:\Users\XovY\AppData\Local\Temp\1660376299960.png)

其中“商品列表” “用户信息” “秒杀”是对某个接口的测试

“查看结果树” “聚合报告” “用表格查看结果” 是对压力测试结果的查看

HTTP Cookie管理器是对访问时需要cookie的管理，其中的值时CSV数据文件当中的值![1660376386301](C:\Users\XovY\AppData\Local\Temp\1660376386301.png)

有时候需要大量的用户进行访问，则可以将用户对应cookie值写在文件当中使用CSV读入，生成多个用户访问的效果



linux要使用jmeter的话就需要安装java环境，因为jmeter是用java开发的，将jmeter的jxm文件保存以及CSV所用的文件传入linux上进行运行 jmeter -n -t /root/test.jmx -l /root/result.jtl & （注意要先在环境变量中配置jmeter）

测试完后将result.jtl结果文件传入window在聚合报告中打开就可以看到linux上的测试结果



# 优化方案

## 页面缓存 URL缓存 

QPS的最大瓶颈就是数据库的IO，有时候我们可以把对数据库的操作放到缓存中来就可以提高QPS

[为什么要使用页面缓存技术 - 范仁义 - 博客园 (cnblogs.com)](https://www.cnblogs.com/Renyi-Fan/p/10907658.html) 

页面缓存存在的问题也是只能缓存那些大量访问且不经常变更的页面，比如如果列表有100页不可能会全部缓存，一般用户都只会经常访问前几页，我们也只要缓存前几页就够了

且注意页面的时效性，我们也要根据情况来设置页面在redis当中的一个失效时间



**使用Ttemplate来手动渲染页面**



## 对象缓存

就比如将用户user存入redis当中，每次在接口调用的时候需要用到user就不用都去数据库中查询直接在缓存redis中获取，减少IO的次数，提高性能

比如：每次在接口调用的时候都要获取用户的信息，可以选择从redis中获取而不从数据库中查

但是需要注意的是，数据库当中的user变化的时候同时也要变更redis当中的数据

**注意的是，我们从redis当中获取到的html，肯定是要进行一个手动的渲染**

（如何进行手动的渲染前端页面）



## 商品详情页面静态化

不是使用model来传输数据了，所以需要自定义一个VO来传输所需要的数据



## 超卖问题

事务的注解

一般就是加锁 乐观锁 悲观锁 分布式锁

1.在减库存得时间进行判断是否大于0

2.添加索引（同一个用户只能秒杀一个商品，通过将userid和goodsid进行绑定 一样）

3.使用乐观锁

4.使用同步代码块（但是效率较低）



## 接口优化

还可以进行对数据库的优化，比如数据库的分表分库 mycat等等

此前还是有很多访问数据库的操作，比如在秒杀的时候进行查询库存以及减去库存的操作，我们可以通过**redis来预减库存**来达到减少访问数据库的操作提高QPS，这样的话就要经常和redis进行交互所以一般redis会放在一个单独的服务器上（这样又要经常和redis服务器进行网络通信--我们还可以通过**内存标记去减少redis的访问**）



下单直接找数据库仍然扛不住这样的高并发，所以可以用到队列，下单之后先让请求进队列，通过**队列进行异步下单**增强用户体验（rabbit mq）（这样的消息队列具有流量削峰的作用--在大并发的时候都加入队列快速处理，后面再慢慢处理每个请求）



- **预减库存**
  - 将controller实现InitializingBean，进行系统启动时的配置将数据加载到redis当中(了解bean的生命周期)
  - 减先在redis当中进行减法
- **内存标记**
  - 问题出现时：当redis中的库存已经显示为0的时候，还有大量的并发需要进行访问redis服务器，这样的话相比之下还能优化，就是在代码中加入一个Map<Long,Boolean>其中Long记录的是秒杀商品的id因为可能会有许多的商品同时进行秒杀，Boolean记录的是是否还有库存
  - 在InitializingBean当中进行对map的设置先设置为false当预减库存为0的时间就可以设置为true就不用在和redis的服务器进行访问，提高速度
- **rabbitmq异步下单**
  - 在进行redis的预减操作的时候，同时使用mq进行下单，这样能够立马给用户返回，比如正在排队的信息，在高并发的时候达到流量削峰的作用，因为mq是按序进行处理所以应该不会出现高并发的问题
  - 在放入mq的时候返回给前端一个排队中，然后前端需要轮询后端的数据查看是否产生订单，如果有的话就说明秒杀成功，则前端展示秒杀成功



##### redis的分布式锁

锁就是一个占位的过程，占为之后其他的访问就放弃或者重试，等到占用过程结束之后进行锁的删除

1.使用redistemplate

2.使用redission（推荐）

![1660571588321](C:\Users\XovY\AppData\Local\Temp\1660571588321.png)

- 这里的setIfAbsent是进行假设加锁的一个过程，如果存在k1的键就不添加返回0，不存在添加返回1
- 我们假设redis中存在k1则说明有人再占用锁
- 但是可能存在的问题就是因为宕机导致锁没有删除，就会出现死锁的现象，所以我们可以通过加上redis的定时消失的时间来解决
- 但是如果加上了定时的时间，只是因为网络故障的问题导致线程再代码块中停留的时间超过了Timeout的时间就会导致这个线程再删除的时候可能会删除到其他的线程的锁，这样的话我就可以设置一个UUID在删除锁的时候进行一个判断(就像乐观锁那样)
- 但是如果添加了像乐观锁的操作就会多了，加锁，获取锁，比较锁。这三个不是原子性的操作
- 为了解决不是原子性的问题，我们可以选择使用lua脚本，redis有自带的lua方法，他可以让多个redis命令原子性执行





### 使用rabbitmq

四个模式 finout direct topic headers h几乎不用，通常都是topic

1.引入依赖

```java
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-amqp</artifactId>
</dependency>
```

2.写配置文件

```
#RabbitMQ
   spring：
    rabbitmq:
      #服务器
      host: 192.168.153.138
      #用户名
      username: guest
      #密码
      password: guest
      #虚拟主机
      virtual-host: /
      #端口
      port: 5672
      listener:
        simple:
          #消费者最小数量
          concurrency: 10
          #消费者最大数量
          max-concurrency: 10
          #限制消费者每次只能处理一条消息，处理完在继续下一条消息
          prefetch: 1
          #启动是默认启动容器
          auto-startup: true
          #被拒绝时重新进入队列
          default-requeue-rejected: true
      template:
        retry:
          #发布重试，默认false
          enabled: true
          #重试时间，默认1000ms
          initial-interval: 1000ms
          #重试最大次数，默认3次
          max-attempts: 3
          #最大重试间隔时间
          max-interval: 10000ms
          #重试的间隔乘数，比如配2。0  第一等10s 第二次等20s 第三次等40s
          multiplier: 1
```

3.填写配置文件（配置交换机，队列 以及绑定关系）

4.写生产者以及消费者 消费者使用注解@RabbitListener啥的绑定队列进行接受





## 安全化

1.为了防止黄牛使用计算机进行秒杀，所以我们对秒杀接口进行一个隐藏，开始秒杀的时候才开放接口

通过秒杀的接口来获取地址

其中接口用过地址进行拼接，拼接规则由自己定，加上一个UUID然后将UUID放入redis中，当用户进行访问的时候需要从redis中的地址进行校验

2.为了防止黄牛的lua脚本，使用验证码进行隔离

3.接口的一个限流 控制QPS保护服务器的压力 ，可以使用redis设置每个用户的登入次数（计数器）有各种的限流算法 漏桶算法 令牌头算法自己了解

