## 组件化的使用

### 依赖

在主工程的 build.gradle 中添加 maven 地址：

```
maven { url 'http://xiaojinzi.tpddns.cn:18081/repository/maven-releases/' }
```

在基础工程 BaseModule 中添加依赖：(版本号强烈建议你可以先写+,拉下来之后再写死)

```java
api "com.xiaojinzi.component:impl:+"
```
或者 RxJava2的实现
```java
api "com.xiaojinzi.component:impl-rx:+"
```
强烈建议使用 Rx 版本,基础版本很多功能不及 Rx版本,强烈建议
各个业务组件会依赖 BaseModule,所以自动会有上述的依赖

然后在各个业务组件中添加注解驱动器
```java
annotationProcessor "com.xiaojinzi.component:compiler:+"
```
这个会生成各个业务组件的 Application 管理类和路由表

### 在壳工程初始化

```java
ComponentConfig.init(this,true);
```

如果依赖了 Rx版本的实现,请调用 
```java
EHiRxRouter.tryErrorCatch(); // 这个可以帮助你在路由拿目标界面数据的时候或者整个路由过程中出现的异常可以被忽略,Rxjava2 的错误默认不处理会崩溃哒！！！
```

### 在壳工程注册业务模块
```java
EHiModuleManager.getInstance().register("component1");
EHiModuleManager.getInstance().register("component2");
EHiModuleManager.getInstance().register("user");
EHiModuleManager.getInstance().register("help");
```
同样你也可以反注册业务模块,让某一个业务模块下架,比如下架 业务组件1
```java
EHiModuleManager.getInstance().unregister("component1");
```
但是我们通常不是这样子用的,通常都是

### 每一个业务组件的配置

- 配置 业务组件的 Host 名称

![image.png](https://i.loli.net/2018/12/06/5c08e291917e4.jpg)

- 编写业务组件的 Application,继承 IComponentApplication 接口实现创建和销毁的逻辑
	- 创建一个Application 实现 IComponentApplication接口,并且使用 @EHiModuleApp() 标记
	- 在创建和销毁方法中注册(反注册)自己的路由表,暴露(不暴露)自己的功能给其他的业务组件


```java
@EHiModuleAppAnno()
public class Component1Application implements IComponentApplication {
    @Override
    public void onCreate(@NonNull final Application app) {
        // 你可以做一些当前业务模块的一些初始化
    }

    @Override
    public void onDestory() {
        // 你可以销毁有关当前业务模块的东西
    }
}
```

### Activity 的配置

这里展示了一个业务组件1的一个 Activity 的使用
- 使用 **@EHiRouterAnno** 注解, ** host** 表示模块,**value**表示**path**,
- **interceptors** 和 **interceptorNames** 表示进入这个页面需要执行的拦截器,两者的区别就是一个是填写Class属性,另一种填写的是一个字符串,他俩的区别或者使用场景我后面再叙述
- 使用 **QueryParameterSupport** 帮助类获取 Uri 中传过来的 **query** 参数
- 其他参数的获取和原生一样,原先的代码或者习惯无需更改!!!!

```java
@EHiRouterAnno(
        host = "component1",
        value = "test",
        interceptors = {Component1Interceptor1.class, Component1Interceptor2.class},
        interceptorNames = "user.login",
        desc = "业务组件1的测试界面"
)
public class Component1TestAct extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.component1_test_act);

        TextView tv_data = findViewById(R.id.tv_data);
        tv_data.setText(QueryParameterSupport.getString(getIntent(), "data"));

        String data = getIntent().getStringExtra("data");

        if (data != null) {
            tv_data.setText(tv_data.getText() + "\n" + data);
        }

    }

    public void returnData(View view) {
        Intent intent = new Intent();
        intent.putExtra("data", "this is the return data");
        setResult(RESULT_OK, intent);
        finish();
    }

}

```

### 路由到其他界面

#### 普通跳转

这段就跳转到上面我们写的那个例子的界面中了,并且我们还传了两个 query 值过去哦
```java
EHiRouter
                .with(this)
                .host("component1")
                .path("main")
                .query("name", "cxj")
                .query("pass", "123")
                .navigate();
```

#### 普通跳转Bundle带值

这段就跳转到上面我们写的那个例子的界面中了,我们传了两个 query 值,并且额外存了两个值到 Bundle 中携带过去,和我们普通的 传值效果是一样的,唯一的区别是 路由的 putXXX 方式利用方法名区别传不同的值, Intent 靠重载方法来传不同的值,但是我更倾向于利用方法区分,因为一旦你入参的参数类型有改动的时候,方法就会报错,而 Intent 不会
Intent intent = new Intent(Context,XXX.class);
intent.putExtra(key,value);
```java
EHiRouter
                .with(this)
                .host("component1")
                .path("main")
                .query("name", "cxj")
                .query("pass", "123")
				.putString("name", "cxj1")
                .putInt("age", 25)
                .navigate();
```

#### 普通跳转判断是否跳转成功

```java
EHiRouter.with(this)
               .host("component1")
               .path("main")
               .query("name", "cxj")
               .query("pass", "123")
               .navigate(new EHiCallbackAdapter() {
                        @Override
                        public void onSuccess(@NonNull EHiRouterResult result) {
                                // 跳转成功,参数是 request 对象
                        }

                        @Override
                        public void onError(@NonNull Exception error) {
                                // 跳转失败,参数是 Exception 对象
                        }
                });
```

#### 普通跳转成功和失败

```java
EHiRouter.with(this)
               .host("component1")
               .path("main")
               .query("name", "cxj")
               .query("pass", "123")
               .navigate(new EHiCallbackAdapter() {
                        @Override
                        public void onEvent(@Nullable EHiRouterResult routerResult, @Nullable Exception error) {
                                // 跳转成功,参数是 request 对象
                        }
                });
```

这种就是成功和失败都能从一个方法中拿到

#### 普通跳转拿数据

```java
EHiRouter
                .with(this)
                .host("component1")
                .path("main")
                .query("name", "cxj")
                .query("pass", "123")
                .requestCode(456) // requestCode
                .navigate();
```

你就可以在 onActivityResult 方法中拿到数据啦,这种方式和原生的写法没什么差别,骚操作且看下面

#### 路由请求的自动取消(支持Activity和Fragment)

```java
EHiRouter
                .with(this)
                .host(ModuleConfig.System.NAME)
                .path(ModuleConfig.System.CALL_PHONE)
                .interceptors(DialogShowInterceptor.class)
                .navigate(new EHiCallbackAdapter() {
                    @Override
                    public void onEvent(@Nullable EHiRouterResult result, @Nullable Exception error) {
                        super.onEvent(result, error);
                    }

                    @Override
                    public void onCancel() {
                        super.onCancel();
                    }
                });

        finish();
```
当销毁这个Actiivty的时候,路由请求如果还没有跳转过去,比如因为中间的拦截器等因素,那么就会自动取消这个路由请求
```java
EHiRxRouter
                .withFragment(this)
                .host(ModuleConfig.System.NAME)
                .path(ModuleConfig.System.CALL_PHONE)
                .interceptors(DialogShowInterceptor.class)
                .navigate(new EHiCallbackAdapter(){
                    @Override
                    public void onEvent(@Nullable EHiRouterResult result, @Nullable Exception error) {
                        super.onEvent(result, error);
                    }

                    @Override
                    public void onCancel() {
                        super.onCancel();
                    }
                });

        getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
```

当销毁这个Fragment的时候,路由请求如果还没有跳转过去,比如因为中间的拦截器等因素,那么就会自动取消这个路由请求

#### 跳转前后可以执行的Action

```java
EHiRouter
                .with(this)
                .host(ModuleConfig.System.NAME)
                .path(ModuleConfig.System.CALL_PHONE)
                .onBeforJump(new com.ehi.component.support.Action() {
                    @Override
                    public void run() throws Exception {
                        Toast.makeText(mContext, "startActivity之前", Toast.LENGTH_SHORT).show();
                    }
                })
                .onAfterJump(new com.ehi.component.support.Action() {
                    @Override
                    public void run() throws Exception {
                        Toast.makeText(mContext, "startActivity之后", Toast.LENGTH_SHORT).show();
                    }
                })
                .navigate();
```

#### Rx方式跳转拿数据

```java
EHiRxRouter
                .with(this)
                .host("component1")
                .path("main")
                .query("name", "cxj")
                .query("pass", "123")
                .requestCode(456)
                .newIntentCall()
                .subscribe(new Consumer<Intent>() {
                    @Override
                    public void accept(Intent intent) throws Exception {
                        // intent 参数中拿数据
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        // 你可以自己处理错误,如果不传入 错误的回调接口,默认忽略
                    }
                });
```
这种方式可以说很**明显减少了平常写的代码的量,并且让你摆脱了 onActivityResult**,所以这可以在** Adapter 或者 任何一个有 Context(其实是一个Activity)的地方去使用**
上述的写法是要处理错误,如果你不想处理错误,可以这样写

```java
EHiRxRouter
                .with(this)
                .host("component1")
                .path("main")
                .query("name", "cxj")
                .query("pass", "123")
                .requestCode(456)
                .newIntentCall()
                .subscribe(new Consumer<Intent>() {
                    @Override
                    public void accept(Intent intent) throws Exception {
                        // intent 参数中拿数据
                    }
                });
```

#### Rx 方式和其他操作组合

因为上面的使用你都是拿到一个 Single Observable,所以你可以拿着这个 Observable 和其他的任何流程进行结合使用
完美的嵌入到一个流程中,不会被 onActivityResult 等方式打断你的流程

#### 自定义 Intent 和自定义跳转

**从新版本开始,支持自定义跳转,这个功能很重要,意味着你可以跳转到任何地方(包括第三方和系统),享受这个组件化框架带来的所有的功能,下面就是使用的范例,路由 @EHiRouterAnno 支持标记在静态方法上,支持返回Intent或者直接你自行跳转,这一点是CC组件化框架所不能的**

```java
public class CustomerRouterImpl {

    @Nullable
    @EHiRouterAnno(
            host = ModuleConfig.System.NAME, value = ModuleConfig.System.CALL_PHONE,
            interceptorNames = InterceptorConfig.HELP_CALLPHOEPERMISION
    )
    public static Intent callPhoneIntent(@NonNull EHiRouterRequest request) {
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + "15857913627"));
        if (true) {
            throw new NullPointerException();
        }
        return intent;
    }

    /**
     * 系统 App 详情
     *
     * @param request
     * @return
     */
    @EHiRouterAnno(host = ModuleConfig.System.NAME, value = ModuleConfig.System.SYSTEM_APP_DETAIL)
    public static void appDetail(@NonNull EHiRouterRequest request) {
        Activity act = request.getActivity();
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + request.getRawContext().getPackageName()));
        if (request.requestCode == null) {
            if (act == null) {
                request.fragment.startActivity(intent);
            }else {
                act.startActivity(intent);
            }
        } else {
            if (act == null) {
                request.fragment.startActivityForResult(intent, request.requestCode);
            }else {
                act.startActivityForResult(intent, request.requestCode);
            }
        }
    }

}
```

### 服务的提供和使用

在依赖库中有一个设计好的服务容器类

**EHiService.class**

#### 定义服务接口

当你想提供**业务组件1**的功能出去的时候,你在**基础库(BaseModule)**的  **service** 包下面新建一个接口文件,这个接口会被所有的业务模块引用

![image.png](https://i.loli.net/2018/12/06/5c08e2bb49281.jpg)

```java
public interface Component1Service {
    Fragment getFragment();
	void xxx();
	int count();
	......
}
```

#### 注册服务到服务容器

##### 非单例服务

```java
@EHiServiceAnno(value = {Component1Service.class},singleTon = false)
public class Component1ServiceImpl implements Component1Service {

    private Context context;

    public Component1ServiceImpl(@NonNull Application app) {
        context = app;
        Toast.makeText(app, "创建了 Component1Service 服务", Toast.LENGTH_SHORT).show();
    }

    @Override
    public Fragment getFragment() {
        return new Component1Fragment();
    }


}
```

##### 单例服务

```java
@EHiServiceAnno(value = {Component1Service.class},singleTon = true)
public class Component1ServiceImpl implements Component1Service {

    private Context context;

    public Component1ServiceImpl(@NonNull Application app) {
        context = app;
        Toast.makeText(app, "创建了 Component1Service 服务", Toast.LENGTH_SHORT).show();
    }

    @Override
    public Fragment getFragment() {
        return new Component1Fragment();
    }


}
```

#### 服务的使用

然后其他任何一个地方就可以这样子使用啦

![image.png](https://i.loli.net/2018/12/06/5c08e2f8b1f24.jpg)

**或者使用增强版本的RxService,可以省去如果服务未找到等情况的判断,并且一些同步的方法调用错误也会自动给你过滤假如你不想处理它,但是像返回 ObServable 这种,当你订阅它然后出现的问题需要您自行处理**

![image.png](https://i.loli.net/2019/01/11/5c3825917a1fb.jpg)

## 组件化的几个重点

组件化的方式很多,但是每一个组件化都必须有这三点,我们做组件化只要保证做到这三点其实就是做了组件化了

- 业务组件之间的隔离                    
```
项目拆分成多个 module
```
- 业务组件之间的服务的提供
```
某个业务组件中的服务如何让别人使用
也可以理解为平行没有依赖关系的模块A和B之间,A如何向B提供A业务模块的功能
```
- 业务组件之间的跳转
```
路由
```
- 基础组件的下沉                          
 ```
比如：(实体对象,网络请求模块,数据库,本地存储.....)
```
- 业务组件加载和卸载的生命周期    
```
业务组件的生命周期(为每一个业务模块带去类似于Application的概念)
```

```
业务组件的动态的加载和卸载,比如像大公司的 App,后台能下发指令,下架某一个模块,比如 滴滴 在顺风车出现问题的情况下,下架手机上的顺风车的功能
这就是 '业务组件加载和卸载的生命周期' 可以做到的事情
```

## 示例代码

github地址：[组件化示例项目](https://github.com/xiaojinzi123/ComponentDemo)

这个项目里面实现了组件化的方案,并且使用组件化方案写了一个 Demo,整个项目架构如下:

![image.png](https://i.loli.net/2019/02/02/5c555e58d0c3e.jpg)

- app 
```
壳工程
```
- ModuleBase
```
基础库工程
```
- Module1 
```
业务组件1
```
- Module2 
```
业务组件2
```
- ModuleUser
```
用户业务模块
```
- ModuleHelp
```
帮助业务模块
```
- ComponentApi 
```
组件化的注解Api
```
- ComponentCompiler 
```
组件化的注解Api注解驱动器
```
- ComponentImpl 
```
组件化的基础实现
```
- ComponentRxImpl 
```
组件化结合Rxjava2的实现,这个ComponentRxImpl实现中会有一个很好用的功能,基于ComponentApiImpl扩展
```

## **特别注意**

任何一个组件化框架,在跳转这件事情上,都需要额外的小心,下面是示例代码：

### 注意运行线程

由于框架在路由的开始的时候可能需要操作 Fragment,所以必须在主线程上执行,所以这点还请注意
本来没有返回值的话还能在子线程上走,我帮您切换到主线程上,但是还有一个返回值,所以没办法,只能在主线程了

### 没组件化之前跳转

```java
Intent intent = new Intent(this.XXX.class);
startActivity(intent);
finish();
```

### 使用组件化之后

```java
EHiRouter.with(context)
        .host("host")
        .path("path")
        .navigate();
finish();
```

前一种写法是没有问题的,因为跳转的信号已经发送到系统,然后紧接着又发送了结束当前Activity的信号,系统会依次执行这两个信号要执行的代码
后一种写法使用了路由跳转就会有问题,这个问题在任何一个框架中都会存在

因为使用了路由跳转,中间有可能会经过若干拦截器,所以最终的跳转的代码 startActivity(Intent) 都会晚于 finish(); 代码,所以就会导致界面先销毁了然后再跳转,而一个销毁了的 Activity 是不可以打开另一个 Activity 的,所以这种写法一定要注意,这是错的

正确的写法

```java
EHiRouter.with(context)
        .host("host")
        .path("path")
        .navigate(new EHiCallbackAdapter(){
                @Override
                public void onEvent(@Nullable EHiRouterResult result, @Nullable Exception error) {
                        super.onEvent(result, error);
                        finish();
                }
        });
```

这样子就在这个路由结束之后执行的, onEvent 失败和成功都会执行,onError 会在失败的时候执行, onSuccess 会成功的时候执行,你可以自由的选择,如果你想在成功的里面执行还有一种方式就是上面介绍的 onAfterJump ,也可以实现

## 总结

### 拦截器

#### 拦截器类型

- 全局拦截器
- 局部拦截器

#### 拦截器使用

**拦截器需要实现 EHiRouterInterceptor 接口,构造函数提供了无参 和 一个参数 (Application) 的**

##### 全局拦截器

**如何使用：**全局拦截器需要使用 @EHiGlobalInterceptorAnno(priority = 4) 注解标记,这个拦截器会在业务组件加载的时候自动加载到框架中,会拦截到每一个路由请求.参数 priority 是指这个拦截器的优先级,数值越大,优先级越大

**使用场景：** 比如您需要对所有的路由做处理、做分析等等

```java
@EHiGlobalInterceptorAnno(priority = 4)
public class Component1Interceptor2 implements EHiRouterInterceptor {

    public Component1Interceptor2(Application application) {
    }

    @Override
    public void intercept(Chain chain) throws Exception {
        chain.proceed(chain.request());
    }

}
```

##### 局部拦截器

**如何使用：**局部拦截器需要使用 @EHiInterceptorAnno(value="user.login") **注解标记或者不使用注解标记**,这种拦截器不会在业务模块被加载的时候加载,这种拦截器更贴切的可以比作业务拦截器

示例代码：

```java
@EHiGlobalInterceptorAnno(priority = 4)
public class Component1Interceptor2 implements EHiRouterInterceptor {

    public Component1Interceptor2(Application application) {
    }

    @Override
    public void intercept(Chain chain) throws Exception {
        chain.proceed(chain.request());
    }

}
```

**使用场景1：** A 模块需要使用User模块的登录拦截器,使用这个拦截器就可以实现自动登录

**在User模块声明登录拦截器**

```java
@EHiInterceptorAnno("user.login")
public class UserLoginInterceptor implements EHiRouterInterceptor {

    public UserLoginInterceptor(Context app) {
    }

    @Override
    public void intercept(final Chain chain) throws Exception {
            // 实现登录的代码.........
    }
}
```

在 B 模块的界面上使用

```java
@EHiRouterAnno(
        host = ModuleConfig.Component1.NAME,
        value = ModuleConfig.Component1.TEST_LOGIN,
        interceptorNames = "user.login"
)
public class Component1TestLoginAct extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.component1_test_login_act);
        getSupportActionBar().setTitle("测试登录");
    }

}
```

**这样子我们就跨组件的使用了别的模块提供的拦截器**

**使用场景2：** A 界面是一个订单确认界面,好多入口都可以跳转到这里下订单,但是这个界面需要做一个事情,就是跳转过来之前你需要做一下参数的检查,那么这段代码你放在前面的界面实现肯定不靠谱,因为这样子每一个界面都要做这件事情,而且以后新加一个入口你都要注意这个事情.假如你放到下个界面做,那么已经跳转过来了,用户已经看到确认订单的界面了,然后检测不通过的话界面又销毁掉,明显这样子给用户的体验又不好了,所以这种就这个界面会使用的拦截器,你可以这样子来做

声明订单检测的拦截器,不需要使用注解标记

```java
public class CheckBeforOrderInterceptor implements EHiRouterInterceptor {

        @Override
        public void intercept(EHiRouterInterceptor.Chain chain) throws Exception {
                // 去做检测的事情
        }
}
```
使用这个拦截器,参数为 Class 类型

```java
@EHiRouterAnno(
        host = ModuleConfig.Main.NAME,
        value = ModuleConfig.Main.MAIN_ORDER_CONFIRM,
        interceptors = {CheckBeforOrderInterceptor.class}
)
public class OrderConfirmAct extends Activity {
}
```

当然这里的拦截器你完全可以使用 @EHiInterceptorAnno 注解然后起一个名字,但是这种不对外的拦截器,这样子使用更加的简单

### 小结

到此组件化的方案介绍完毕,里面有很多基于得到的经验,也有很多的调整改进和优化
如果有兴趣想更清楚内部的实现,请详细的去琢磨以下三个 module 中的代码吧,不明白的可以来和我交流
![image.png](https://i.loli.net/2019/01/11/5c380e9ba68b7.jpg)






