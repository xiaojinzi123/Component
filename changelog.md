#### [v1.9.0-beta2](https://github.com/xiaojinzi123/Component/releases/tag/v1.9.0-beta2)
- 自动注入支持了 Kotlin 文件的属性了. 不必再使用 @JvmField 注解标记了
- @ServiceAnno 增加了 autoInit 属性, 这个属性可以让 Service 自动初始化在模块加载之后. 每一个模块加载/卸载都会触发

#### [v1.9.0-beta1](https://github.com/xiaojinzi123/Component/releases/tag/v1.9.0-beta1)
- 支持了 Service 的装饰功能. 使用 @ServiceDecoratorAnno 注解标记一个类.
- Service 装饰功能支持 Condition.
- 修复之前的版本卸载模块但是路由卸载不掉的问题

#### [v1.8.8](https://github.com/xiaojinzi123/Component/releases/tag/v1.8.8)
- kotlin 的协程模块上线啦
- 这个版本及其之前的版本. 会有模块卸载的时候, 路由表卸载不了的问题
    - 由于真实项目中不会有模块卸载的情况. 所以这个版本暂时未修复. 之前的版本会修复
    - 至于为什么真实项目中不会卸载. 原因是：卸载之后会有无法预期的问题. 正确的做法是在启动的时候只加载你需要加载的模块. 而不是后续去卸载!
- 修复 Utils 中的 isCauseBy 方法的 bug

#### [v1.8.6](https://github.com/xiaojinzi123/Component/releases/tag/v1.8.6)
- Service 支持了多实现. 一个接口. 可以有问个实现类. 但是需要用 name 区分

#### [v1.8.3.6](https://github.com/xiaojinzi123/Component/releases/tag/v1.8.3.5)
- [#79](https://github.com/xiaojinzi123/Component/issues/79) 为了解决死锁. 放弃了 Fragment 和 服务发现的创建一定保证主线程的逻辑. 避免死锁发生. 
- host 的配置支持了任意的字符串, 比如: www.baidu.com, www.module.user. 没有任何限制了

#### [v1.8.3.5](https://github.com/xiaojinzi123/Component/releases/tag/v1.8.3.5)
- 增加当发生死锁的时候, 错误信息的报错, 方便用户排查. 具体可以看 [#79](https://github.com/xiaojinzi123/Component/issues/79) 这个 issue

#### [v1.8.3.4](https://github.com/xiaojinzi123/Component/releases/tag/v1.8.3.4)
- 增加一个可选的 IModuleNotifyChanged 接口, 用来接收模块加载的变化

#### [v1.8.3.3](https://github.com/xiaojinzi123/Component/releases/tag/v1.8.3.3)
- 删除 1.8.3.1 增加的 Service 生命周期的功能. 实践得知, 这功能并不正确
- ServiceManager 的反注册方法改为没有返回值.
- @MainThread 注解全都改为 @UiThread
- afterAction 的执行用的 request 修复为 finalRequest, 而不是 originRequest
- 增加一些方法的 @NonNull 注解
- 修复 RouterRequest的bundle变为RouterRequest.Builder.Bundle 地址传递的问题, 应该为值传递
- 增加模块加载失败时候的 url 链接, 可以帮助排查错误
- 调整 Logo 输出的位置
- 修复生成的路由表的 ArrayList 的泛型警告问题
- 修改 componnet 拼写错误的问题.
- 增加 component_asm_disable 配置, 可以让 Gradle 插件禁用 ASM 的功能. 因为有些人就只用 RouterDoc 的功能

#### [v1.8.3.2](https://github.com/xiaojinzi123/Component/releases/tag/v1.8.3.2)
- 修复 void method(Context context, Callback callback); 这中 Api 接口方法生成代码失败的问题

#### [v1.8.3.1](https://github.com/xiaojinzi123/Component/releases/tag/v1.8.3.1)
- 增加 java8 的发布维度
- 支持属性注入多个属性. 主要用于兼容如果想变动 key 的问题.
- ~~支持 @ServiceAnno 标记的服务实现类, 可以可选的实现 IServiceLifecycle 接口来接受模块的生命周期的回调~~ 此功能将被删除.
- 用户自定义的模块生命周期类, 需要实现 IApplicationLifecycle 接口, 而不是老的 IComponentApplication 接口, 虽说老的目前也支持, 但是若干版本之后会删除
- 内部优化类结构. 需要更新新的[混淆配置](https://github.com/xiaojinzi123/Component/wiki/%E4%BE%9D%E8%B5%96%E5%92%8C%E9%85%8D%E7%BD%AE-AndroidX#%E6%B7%B7%E6%B7%86%E9%85%8D%E7%BD%AE)

#### [v1.8.2.3](https://github.com/xiaojinzi123/Component/releases/tag/v1.8.2.3)
- 优化了 Host 名称中不可以配置 _ 的问题. 但是一定不是开头哦
- 修复 ServiceManager.get() 或者 FragmentManager.get() 方法在子线程中调用的时候, 如果没有实现类就会死循环的问题

#### [v1.8.2.2](https://github.com/xiaojinzi123/Component/releases/tag/v1.8.2.2)
- 当不使用框架的 ActivityResult, 可以填写 requestCode 使用普通的 startActivityResult

#### [v1.8.2.1](https://github.com/xiaojinzi123/Component/releases/tag/v1.8.2.1)

- 修复之前修改 `impl` 模块的包名引起的 `ProxtActivity` 失效的问题
- `router` 文档增加控制是否生成文档的 `boolean` 的开关. `component_router_doc_enable`
    - 配置方式和 `componnet_router_doc_folder` 一致. 比如 ext.component_router_doc_enable = true
- 同时注解驱动器的 `RouterDoc` 属性配置改为 `RouterDocFolder`
- 注解驱动器也增加一个文档是否生成的开关 `RouterDocEable`, 一般 `RouterDocEable` 这个值随着 `component_router_doc_enable` 变化即可

#### [v1.8.2](https://github.com/xiaojinzi123/Component/releases/tag/v1.8.2)

- 增加 Androidx 配置和 非Androidx 选择错误的时候的提示
- 当页面拦截器更改 `Uri` 的 `host` 和 `path` 这部分信息之后重新加载新的目标的页面拦截器
  - 因为页面拦截器是和目标 `Uri` 绑定的. 如果在页面拦截器中更改了 `Uri`. 那么最终跳转的 `Intent` 可能和页面拦截器不是一对的
- 增加页面拦截器的执行优先级 `interceptorPriorities` 和 `interceptorNamePriorities`
- 修复一个界面同时使用两个跳转拿 `ActivityResult` 出现的其中一个回调问题.
  - 原因是 `commitAllowingStateLoss` 不是立马提交的, 需要使用 `commitNowAllowingStateLoss`. 基本只在测试情况出现, 线上使用基本没有同一个界面同时跳转拿 `ActivityResult` 的
- 优化获取 `ActivityResult` 的时候因为 `requestCode` 重复导致的日志输出一个取消和错误的情况.

#### [v1.8.1.1](https://github.com/xiaojinzi123/Component/releases/tag/v1.8.1.1)

- 修复之前更新版本出现的 `Uri` 中的 `Query` 同步到 `Bundle` 中的问题
  - 之前版本表现为拦截器中没法从 `Bundle` 中获取到 `Query` 中的参数

#### [v1.8.1](https://github.com/xiaojinzi123/Component/releases/tag/v1.8.1)

- beforJumpAction 全部更名为 beforAction
- afterJumpAction 全部更名为 afterAction
- 增加 beforStartAction, 此 Action 和 startActivity 方法是紧紧连着的代码
- 增加 afterStartAction, 此 Action 和 startActivity 方法是紧紧连着的代码
  - 可用来转场动画

#### [v1.8.0.2](https://github.com/xiaojinzi123/Component/releases/tag/v1.8.0.2)

- 对路由 `Fragment` 支持了常用的 `putXXX` 方法

#### [v1.8.0.1](https://github.com/xiaojinzi123/Component/releases/tag/v1.8.0.1)

- 类型 ArrayList<? extends Serializable> RouterApi 中的支持和 Autowire 的支持. 此类型判断为 Serializable 类型
- 修复生成文档的 `componnet_router_doc_folder` 拼写错误的错误. 改为 `component_router_doc_folder`

#### [v1.8.0](https://github.com/xiaojinzi123/Component/releases/tag/v1.8.0)

- 修复 `Uri` 中的 `userInfo` 为空的时候的处理
- 增加针对单个路由是否启用重复检查的拦截器的方法. Navigator.useRouteRepeatCheck(boolean) 默认是全局设置的值. 全局设置的值默认是 `true`
- 增加了 [生成文档](https://github.com/xiaojinzi123/Component/wiki/%E6%96%87%E6%A1%A3%E7%94%9F%E6%88%90) 的功能, 对应 [issue](https://github.com/xiaojinzi123/Component/issues/15)

#### [v1.7.9](https://github.com/xiaojinzi123/Component/releases/tag/v1.7.9)

- `ModuleManager` 类中增加 `autoRegister()` 方法, 可以自动加载所有的模块
    - 前提是你使用 Gradle 插件, 并且配置中 optimizeInit 开关已经打开
- 配置中增加开关, 控制是否在初始化的时候自动加载所有模块. 这样子可以进一步省略配置的代码量
- 上面两个描述不支持 `Google` 的 `App Bundle`. 还请特别留意
- `ParameterSupport` 增加对 `Uri` 的获取. 
- 跳转支持 `Uri` 中的 `UserInfo`. 接口的路由跳转中同样也支持了, 使用 `@UserInfoAnno` 即可
- 修复 `@FragmentAnno` 标记在类上的时候的传值问题, 详见 `issue` [#51](https://github.com/xiaojinzi123/Component/issues/51)
- Router 中的方法 `haveProxyIntent` 改名为 `isProxyIntentExist`
- 修复自动注入 ArrayList<T extends Parcelable> 类型的参数错误的问题
- 支持注入 Uri 中的数组的值
- 自动注入支持 SparseArray<? extends Parcelable> 类型
- ParameterSupport 增加 getSparseParcelableArray 方法

#### v1.7.8(不兼容版本更新)

- 所有的配置整改. 统一使用 `Config` 类来配置. 初始化方式改变了. `Component.init(boolean, Config);`
    - 配置中增加 tipWhenUseApplication(boolean) 方法, 用来提醒使用者当使用者使用了 Application 作为 Context, 默认为 true
    - 配置中增加 useRouteRepeatCheckInterceptor(boolean) 让使用者来控制是否使用内置的放抖动的跳转重复检查, 默认为 true
    - Config 类为建造者模式, 整理了以前几个配置, 同时也为以后的配置增多提供了很好的扩展
- `Router.with(Context).withProxyBundle` 更名为 `Router.with(Context).proxyBundle`
- OnRouterCancel 和 OnRouterError 的包名更改为 com.xiaojinzi.component.support
- com.xiaojinzi.component.condition.Condition 更名为 com.xiaojinzi.component.support.Condition
- com.xiaojinzi.component.bean.CustomerIntentCall 更名为 com.xiaojinzi.component.support.CustomerIntentCall
- 注入属性和 `Service` 不兼容优化. 目的是注入属性的值和注入 Service 两个功能更加的明确. 并且提供聚合和分开的方法供用户自由选择
    - Component.inject(Object) 功能不变
    - Component.injectFromIntent(Object, Intent) 更名为 injectAttrValueFromIntent(Object, Intent). 内部去除了注入 `Service` 的功能
    - Component.injectFromBundle(Object, Intent) 更名为 injectAttrValueFromBundle(Object, Intent). 内部去除了注入 `Service` 的功能
    - 新增方法 Component.injectService(Object), 用来注入 `Service`
- 注解 @FieldValueAutowiredAnno 更名为 @AttrValueAutowiredAnno. 不兼容的更新
- 更新若干库到最新
    - RxJava 版本升级到 2.2.17

#### v1.7.7.3(功能+优化)

- 为了满足有些场景是需要先拿到 `Intent` 的, 特别设计了创建代理 `Intent` 的功能. 具体[点击查看](https://github.com/xiaojinzi123/Component/wiki/ProxyIntent)
- 修复 `Router` 跳转拿 `ActivityResult` 和 `Router` 跳转只填写 `requestCode` 产生的 `requestCode` 混乱的问题.ps: 之前的版本你们别混用就不会出问题
- 修复 `FieldAutowiredAnno` 正确拼写, 之前为：`FiledAutowiredAnno`
- 修复 `Gradle` 插件中复制 `Jar` 的 `bug`

#### v1.7.7.2(优化)

- 计划取消 `FragmentAnno` 注解中的 `singleTon` 属性. 考虑到平常使用 `Fragment` 都是创建一个新的然后使用.
- 框架永远不会帮你隐式调用 `requestCodeRandom()` 的. 有多人反馈当跳转获取 `ActivityResult` 的时候, 既然可以调用 `requestCodeRandom()` 方法表示随机生成一个 `requestCode`, 那么为何不直接当用户是这种行为的时候, 框架自动生成一个, 不用用户手动调用. 下面几点是我的说明.
    - 其实是这样的, 跳转获取 `ActivityResult` 对应着 `startActivityForResult()` 方法和 `onActivityResult` 方法的一整个过程. 
    - 那么作为框架的设计, 我当然可以做到上述的操作. 但是我希望的是用户能明白本质上这是一个 `startActivityForResult()`, 需要 `requestCode`. 所以我这边需要用户关心一下
    - `requestCodeRandom()` 是一个便捷的方式, 但是不能省略, 因为我想让用户知道其实这里是需要 `requestCode` 的. 显示的一个标志
    - 所以总结就是, 框架设计上不会默认帮你调用 `requestCodeRandom()` 方法. 大家不要想了.
- 当跳转使用 `Application` 的时候, 新增日志的提醒使用用户, 告知他们使用的是 `Application`, 并且告知不推荐使用 `Application`
    - 增加 `Component.closeLogWhenUseApplication();` 方法来关闭警告的日志
- 优化 `ServiceManager.get(Class)` 内部的获取 `Service` 对象的代码. 让用户自定义的对象肯定在主线程中被创建. 贯彻初期的设计(用户接触的所有地方都是主线程)
- 优化拦截器可能在其他线程初始化的问题
    - 当你在第一次在子线程中执行跳转的时候, 会有可能让拦截器的创建在子线程
- 内部所有方法都添加线程的注解, 标记方法的线程范围.
- 增加对 `Fragment` 的名称的全局唯一性的检测. 当你全局有同名的, 启动的时候调用 `Component.check()` 会报错. 帮助你检查出重名问题.

#### v1.7.7.1

- 所有生成的类上都增加 @Keep 注解
- 错误回调整理(优化如下的问题)
    - 当获取目标界面 `ActiivtyResult` 的时候, 如果前置或者后置方法出错了, 那么错误信息不会被打印 

#### v1.7.7
- `Component` 的配置类中增加一个方法 `optimizeInit(boolean)`, 默认是 `false`, 如果设置为 `true`, 初始化的实现就会变成使用 `ASM` 技术实现的, 反之使用反射
    - 其实两者性能几乎无差别, 不是极致优化的情况下, 我建议还是别用
- `Gradle Plugin`
    - 在之前版本的基础上, 增加一个 `Gradle Plugin`, 用来加快启动的速度. 具体问题请看 [issue](https://github.com/xiaojinzi123/Component/issues/26)
    - `Component` 默认采用反射进行初始化, 但是优化后和优化前的时间相差的很小, 只有几毫秒. 所以并不推荐使用 `Gradle Plugin` 
    - 当然了使用 `Gradle` 插件也会带来一定的未知的风险, 当有任何问题产生的时候, 请第一时间禁用此插件来排查是否是此插件引起的. 
    - 如果是插件引起的问题, 请您禁用此插件, 请放心, 不会对你代码造成任何的影响. 可以的话把问题反馈给我. 谢谢
- `Idea Plugin` 修复对 `RxRouter` 的图标显示问题
- `Idea Plugin` 不在支持 `AS3.4`, 最低支持 `AS3.5`

#### v1.7.6.2
- 全部的 `navigate` 方法都增加了 `@CheckResult` 注解, 提示使用者此方法是有返回值的, 不要返回值你可以使用对应的 `forward` 方法

#### v1.7.6.1
- 增加全套的 `forward` 方法, 没有 `NavigationDisposable` 返回值

#### v1.7.6
- 增加"路由" `Fragment` 的功能(其实就是针对`Fragment`做的一个更简单的获取方式)
    - 任意一个 `Fragment` 使用 `@FragmentAnno` 标记即可
    - 如何使用请看, 路由的 `wiki` [跳转 Fragment](https://github.com/xiaojinzi123/Component/wiki/%E8%B7%B3%E8%BD%AC-Fragment)
- 增加 `RxRouter` 对路由 `Fragment` 的支持, 返回的是 `Single<Fragment>`
- 重命名 `RxService` 为 `RxServiceManager`
- 重命名 `Service` 为 `ServiceManager`

#### v1.7.5.1 
- 属性注入增加方法 `Component.inject(Object target, Intent intent)` 以便在 `onNewIntent` 方法中使用
- 升级 `RxJava` 版本到 `2.2.13`
- 升级其他库的版本至最新

#### v1.7.5`
- 纠正接口 `IComponentApplication` 中 `onDestory` 方法的拼写错误, 正确为 `onDestroy`
- 新增 `afterEventAction`, 表示跳转成功或者失败之后的回调, 不允许抛出异常, 会导致奔溃
- 新增 `afterErrorAction`, 表示跳转失败之后的回调, 不允许抛出异常, 会导致奔溃
- 修改内置的同样的路由请求在一秒内只能启动一次的错误日志的输出形式
- 重构几个 `Action` 这块用户自定义的这块的执行顺序
- 路由 `Api` 增加 `AfterErrorActionAnno` 和 `AfterEventActionAnno` 注解

#### v1.7.4
- `Router.with()` 方法支持空参数的形式了, 这种形式默认会使用 `Application` 作为 `Context`, 作者虽然支持了这种形式, 但是不建议平时使用的时候故意使用 `Application`. 因为当你没有用路由框架的之前, 你通常也会使用当前 `Activity Context` 的, 所以作者呼吁大家, 在有 `Activity Context` 的时候, 建议传入 `Activity` 的 `Context`

#### v1.7.3.2
- 路由跳转的进阶版路由 `Api` 支持返回 `RxJava` 的 `Observable` 啦, 支持 `Single`, `Completeable`

#### v1.7.3.1
- 修复 `Fragment` 无法自动注入的 `Bug`
- 类 `ComponentConfig` 重命名为 `Component`
- 源码中增加单独运行模块 `Module1` 的范例,通过新建了一个 `Module1Run` 的 `Module` 去运行

#### v1.7.3.0
- 支持属性的注入, 使用 `@FieldAutowiredAnno` 注解
- 支持 `Service` 的注入, 使用 `@ServiceAutowireAnno` 注解
- 使用 `Component.inject(this)` 注入属性和 `Service`

#### v1.7.2.3
- 支持每一个业务 `Module` 可选的创建生命周期的实现类, 有些模块可能不需要, 那么这个对于用户来说又可以少一个配置

#### v1.7.2.2
- 增加 `@RouterAnno` 注解的 `hostAndPath` 的属性

#### v1.7.2.1
- 仓库变为 `jitpack`,一个可以提供更加稳定的依赖仓库

#### v1.7.2
- 支持路由 Api 中 `Activity Options` 的使用, 使用 `@OptionsAnno` 标记参数即可

#### v1.7.1
- 完善支持路由 `api` 的使用方式
- 删除自定义跳转返回 `void` 的功能
- 跳转增加支持 `flag` 和 `category` 的支持

#### v1.7.0
- 取消支持自定义跳转的时候方法的参数可以自定义的功能
- 支持了全部属性的界面注入功能,通过 `Component.inject(this)` 即可完成注入

#### v1.6.1
- 修复无法支持 `requestCode` 的问题

#### v1.6.0
- 支持了类似 `Retrofit` 的路由接口 `Api`, 详细的请看源码中的示例代码. 全局搜索 `@RouterApiAnno` 注解标记的类, 那些都是范例

#### v1.6.0 之前的版本
之前的版本就不再追溯了, 之后的每次更新我都会详细记录更新的日志