**相信 Component 能成为最优秀的那个框架!**

从 **17** 年下旬就用在公司的产品上了. 虽然不像淘宝那样庞大. 但是日活也有好几万, Activity 界面也有 200+, 
经过这么久的洗礼, Component 早就度过了初期的艰难阶段. 现在 Component 越来越稳定也越来越好. 
同时作者也希望 Componeng 能一直保持活力, 本着解决用户 Bug 放在首位, 为用户服务为己任. 相信 Component
会持续在组件化方面保持领先. 并且之后也会不断发现用户的需求, 然后迭代. 希望你们能积极的提 issue, 
告诉我你们在组件化方面的困恼!

很多选型的人有这么个疑问: 有了 `ARouter` 为啥要有 `Component`. 
我这里在这里做一个回答.

`ARouter` 作为最早出现的组件化的一个实现方案,毋庸置疑有一个比较深的底蕴. 很多人看见 `ARouter` star 的数量和阿里出的框架就会产生莫名的信任.基本都会优先选择 `ARouter`.我的建议是组件化方案不同于一个简单的 `UI` 库,你应该做一个比较深入的了解,然后选择!

如果 `ARouter` 真的优秀到没有不选择它的余地,那么其实根本没有我们这些新型框架的出现.而这里我要说的是,`ARouter` 虽然出现的早,但它不是最好.有一个朋友说 `ARouter` 只关注于路由方面,不做多余的事情,
但是在我看来, `ARouter` 正是因为做的不够和一些不合理的地方,才有我们这些框架的出现.

选型是你们团队的要做的事情,但是选择更好、更全面的、更稳定、更有发展前景的框架更是你们技术团队或者技术负责人要做的事情. Component 是目前组件化方面所有框架中最优秀的框架, 不接受反驳.

[English Doc](https://github.com/xiaojinzi123/Component/blob/develop/README_en.md) | [中文文档](https://github.com/xiaojinzi123/Component)

[Component VS ARouter](https://github.com/xiaojinzi123/Component/wiki/Component-%E5%92%8C-ARouter-%E6%AF%94%E8%BE%83)

# 痛点

我们团队在整个开发过程中, 会有几个很恶心的痛点, 代码不能不写, 但是写了有恶心. 所以在组件化过程中, 发现这些是可以被解决的的. 
于是 `Component` 从最早的设计上就考虑了一些痛点

- 你有没有因为在 `Adater` 或者 `Dialog` 或者其他没有 `Activity` 的地方需要 `startActivityForResult`. [痛点解决方式,0入侵](https://github.com/xiaojinzi123/Component/wiki/%E8%B7%B3%E8%BD%AC-%E4%BD%BF%E7%94%A8%E4%BB%A3%E7%A0%81%E8%B7%B3%E8%BD%AC#%E8%B7%B3%E8%BD%AC%E6%8B%BF-activityresult-%E6%95%B0%E6%8D%AE)
- 你有没有因为进入一些界面有前置条件而写一些恶心代码. 比如 A 界面需要定位成功才能进去. 你可能有以下两种方式去解决. [痛点解决：Component 页面拦截器](https://github.com/xiaojinzi123/Component/wiki/%E6%8B%A6%E6%88%AA%E5%99%A8#%E7%BB%99%E4%BD%A0%E7%9A%84%E6%8B%A6%E6%88%AA%E5%99%A8%E8%B5%B7%E4%B8%80%E4%B8%AA%E5%88%AB%E5%90%8D)
    - 你不得不在任何一个跳转到 A 界面的地方去判断当前是否定位成功, 处理完毕之后再跳转. 这种方式恶心至极, 每次都要写一些固定代码
    - 在目标界面去处理定位的事情. 这个方式相对来说比较好, 但是它毕竟已经进入了 A 界面, A 界面需要额外考虑这些, 并且还可能要展示定位失败的视图等等
    - 你使用 `ARouter` 的 extra 做, 这个是一一种方式, 但是有两个问题:
        - `ARouter` 的路由拦截器设计在子线程, 而定位这些漫长的, 通常你希望弹个框卡住用户操作, 你会发现写起来贼恶心. `Component` 设计在主线程的, 但是整个跳转是 `"异步的"`
        - `ARouter` 的每个拦截器都是全局拦截器, 你每多一个这种拦截器, 都会加长整个路由的流程
- 整合 `RxJava`, `Component` 本身完美支持 `RxJava`, 这样子你的跳转就可以嵌入你任何一个其他 `RxJava` 流程中去了. 不会像之前一样整个流程被打断
- 你以为你写的界面, 你可以路由到就已经 ok 了吗? 其实不然, 每一个你写的界面、系统界面、第三方界面都应该是一个可路由的目标, 其他框架不能给任意一个界面标记路由. 这个也是一个痛点. [解决方案](https://github.com/xiaojinzi123/Component/wiki/RouterAnno-注解#标记在静态方法上方法允许抛出任何-exception)
    - 比如拨打电话界面, 是需要申请权限才能进入的. 如果你能给拨打界面标记一个路由, 并且结合上面的页面拦截器. 你会发现这件事做起来舒服至极！
    - 比如拍照界面, 需要申请相机权限. 同样可以这么处理.
    - 当然了, 适用于任何一个你觉得可以标记的界面. 并结合页面拦截器会有奇效!
- 有些界面参数较多, 跳转入口也较多, 你会发现维护这些跳转的参数其实会比较头疼. 这时候你可以使用 [类似 `Retrofit` 接口编程式跳转](https://github.com/xiaojinzi123/Component/wiki/%E8%B7%B3%E8%BD%AC-%E6%8E%A5%E5%8F%A3%E8%B7%AF%E7%94%B1%E7%9A%84%E6%96%B9%E5%BC%8F)

# Component

![](./imgs/logo1.png)

**一个功能强大的组件化框架,极度注重用户体验,带给你使用上不一样的享受.欢迎大家使用,在使用的过程中发现任何问题,欢迎下方的 `QQ群` 里问或者提 `issue` 给我**

[![](https://img.shields.io/github/release/xiaojinzi123/Component.svg?label=JitPack&color=%233fcd12)](https://jitpack.io/#xiaojinzi123/Component)
[![](https://img.shields.io/github/release/xiaojinzi123/Component.svg?label=JitPack-AndroidX&color=%233fcd12)](https://jitpack.io/#xiaojinzi123/Component)
[![](https://img.shields.io/github/release/xiaojinzi123/Component.svg?label=Release)](https://github.com/xiaojinzi123/Component/releases)
[![](https://img.shields.io/github/tag/xiaojinzi123/Component.svg?label=Tag)](https://github.com/xiaojinzi123/Component/releases)
![](https://img.shields.io/github/last-commit/xiaojinzi123/Component/develop.svg?label=Last%20Commit)
![](https://img.shields.io/github/repo-size/xiaojinzi123/Component.svg)
![](https://img.shields.io/github/languages/code-size/xiaojinzi123/Component.svg)
![](https://img.shields.io/github/license/xiaojinzi123/Component.svg)
<a href="https://gitee.com/xiaojinziCoder/Component" >
    <img height=20 src="https://gitee.com/logo-black.svg" /></a>
<a href="https://github.com/xiaojinzi123/Component">
    <img height=22 src="https://xiaojinzi.oss-cn-shanghai.aliyuncs.com/blogImages/fluidicon.png" /></a>

## Demo体验(扫码或者点击图片即可下载)

<a href="https://github.com/xiaojinzi123/Component/releases/download/v1.7.4/app-release.apk">
    <img height=180 src="./imgs/demoApk.png" />
</a>

## Component 的优势

组件化方案真的有很多,那么这个组件化方案优秀在哪里？相比于 `ARouter`、`WMRouter`、`ActivityRouter`、`CC`、`DDComponent`
等开源的组件化框架,有哪些一样或者更加优秀的点

- [x] 支持多 `Module`

- [x] 支持标准 `URI` 的使用

- [x] 支持跳转 `Fragment`(也就是跨组件获取`Fragment`)

- [x] 支持 `androidx`,几乎没有其他组件化框架支持 `androidx` 的

- [x] 支持业务组件生命周期(被加载和被卸载)

- [x] 整个设计贴近原生,对原生的代码入侵极少,尽最大的可能保留原生的代码

- [x] 支持依赖注入、支持目标界面的路由参数

- [x] 路由拦截器执行线程设计是主线程

  - 在路由拦截器的执行线程的设计上,考虑到用户平时书写的 `90%` 代码都是在主线程的,<br/>所以路由拦截器的执行线程也设计为主线程执行,可以让您放心的操作 `UI`、弹框等操作.<br/>同时提供 `Callback` 机制可以在拦截器中做任何耗时的任务<br/>这点绝对是压倒性的优势,不仅整体是 `异步` 的,而且拦截器中能像平常一样写实现的代码

- [x] 配套的 `Idea Plugin` 方便快速浏览,持续会更新此 [插件](https://github.com/xiaojinzi123/RouterGoPlugin)

- [x] 路由的取消,基本上没有路由框架支持路由的取消,这也是一个很大的优势!

  - [x] 手动用代码取消某次路由

  - [x] 路由自动取消, 当发起路由的 `Fragment` 或者 `Activity` 销毁的时候会取消

- [x] 拦截器,足矣满足所有业务情况(具体看 [拦截器wiki]([https://github.com/xiaojinzi123/Component/wiki/%E6%8B%A6%E6%88%AA%E5%99%A8](https://github.com/xiaojinzi123/Component/wiki/拦截器)))
  - [x] 全局拦截器(针对全部路由)
  - [x] 局部路由拦截器
    - [x] 页面拦截器(针对所有跳转到某一个界面的路由)
    - [x] 拦截器别名,支持跨模块使用(可以让每一个拦截器都放在自个的模块)

- [x] 跳转
  - [x] 持标准 `URI`
  - [x] 支持自定义 `Intent`, 你可以给任意一个 `Intent` 标记路由, 这个功能很强大!
  - [x] 支持类似 `Retrofit` 接口编程式跳转
  - [x] `Idea Plugin` 强势支持跳转代码和目标界面的来回导航,也支持拦截器的代码使用和声明处的来回导航

- [x] 无缝对接 `H5`
  - [x] `H5` 只需利用 `URL` 即可任意路由到任何界面
  - [x] `H5` 发起路由不需要关心目标界面需要做的前期工作(框架的[页面拦截器]([https://github.com/xiaojinzi123/Component/wiki/%E5%90%8D%E8%AF%8D%E8%A7%A3%E9%87%8A#%E9%A1%B5%E9%9D%A2%E6%8B%A6%E6%88%AA%E5%99%A8](https://github.com/xiaojinzi123/Component/wiki/名词解释#页面拦截器))已经帮您做完) 

- [x] **0** 配置拿到目标界面返回的 `ActivityResult`, 很多框架不支持或者需要入侵 `BaseActivity`. 绝对的优势
  - [ ] 和系统的行为一样,当 `Context` 是 `Application` 或者 `Service 的 Context` 或者 `ContentProvider 的 Context `的时候, **不支持获取ActivityResult**, 如果真的有需要, 你可以使用栈顶的 `Activity` 来充当 `Context`
  - [x] 除第一点说的几个 `Context`, 其他的情况都是支持的,包括 `Dialog` 中获取到的 `Context`.

- [x] 服务发现和路由分开设计
  - 其实这两块本来就是两个方面,我不清楚为什么很多方案中都柔和在一块

- [x] 完美支持 `RxJava2`(使用`rx`库)

- [x] [业务模块单独运行]([https://github.com/xiaojinzi123/Component/wiki/%E4%B8%9A%E5%8A%A1%E7%BB%84%E4%BB%B6%E5%8D%95%E7%8B%AC%E8%BF%90%E8%A1%8C](https://github.com/xiaojinzi123/Component/wiki/业务组件单独运行))

## hello world

[最简单的 hello world](https://github.com/xiaojinzi123/Component/wiki)

## 更详细的使用

- 依赖和配置
  - [依赖和配置](https://github.com/xiaojinzi123/Component/wiki/%E4%BE%9D%E8%B5%96%E5%92%8C%E9%85%8D%E7%BD%AE)
  - [依赖和配置 For AndroidX](https://github.com/xiaojinzi123/Component/wiki/%E4%BE%9D%E8%B5%96%E5%92%8C%E9%85%8D%E7%BD%AE-AndroidX)
- [Activity注解使用](https://github.com/xiaojinzi123/Component/wiki/RouterAnno-%E6%B3%A8%E8%A7%A3)
- 跳转
  - [跳转 Fragment](https://github.com/xiaojinzi123/Component/wiki/%E8%B7%B3%E8%BD%AC-Fragment)
  - [跳转-接口路由的方式](https://github.com/xiaojinzi123/Component/wiki/%E8%B7%B3%E8%BD%AC-%E6%8E%A5%E5%8F%A3%E8%B7%AF%E7%94%B1%E7%9A%84%E6%96%B9%E5%BC%8F)
  - [跳转-使用代码跳转](https://github.com/xiaojinzi123/Component/wiki/%E8%B7%B3%E8%BD%AC-%E4%BD%BF%E7%94%A8%E4%BB%A3%E7%A0%81%E8%B7%B3%E8%BD%AC)
- [拦截器](https://github.com/xiaojinzi123/Component/wiki/%E6%8B%A6%E6%88%AA%E5%99%A8)
- [服务发现实现跨模块调用](https://github.com/xiaojinzi123/Component/wiki/%E8%B7%A8%E6%A8%A1%E5%9D%97%E6%9C%8D%E5%8A%A1%E8%B0%83%E7%94%A8)
- [单独运行](https://github.com/xiaojinzi123/Component/wiki/%E4%B8%9A%E5%8A%A1%E7%BB%84%E4%BB%B6%E5%8D%95%E7%8B%AC%E8%BF%90%E8%A1%8C)
- [名词解释](https://github.com/xiaojinzi123/Component/wiki/%E5%90%8D%E8%AF%8D%E8%A7%A3%E9%87%8A)
- [基本介绍](https://github.com/xiaojinzi123/Component/wiki/%E5%9F%BA%E6%9C%AC%E4%BB%8B%E7%BB%8D%E5%92%8C%E6%9E%B6%E6%9E%84%E4%BB%8B%E7%BB%8D)

**了解更多请看 [wiki](https://github.com/xiaojinzi123/Component/wiki/) 更多功能等你来发现**

## 有关的文章

- [到底什么是组件化](https://blog.csdn.net/u011692041/article/details/92572758)

配套的 `Idea Plugin`

[RouterGo 帮助你快速导航到目标界面或者目标拦截器,你值得拥有!](https://github.com/xiaojinzi123/RouterGoPlugin)

## 扫码进群

<div>
    <img src="./imgs/qq_group1.JPG" width="260px" height="360px" />
    <img src="./imgs/qq_group2.JPG" width="260px" height="360px" />
</div>

## 如果你觉得项目不错, 就请我喝杯咖啡吧! 一块钱也是爱!

<img height=200 src="./imgs/collectQRCode.png" />

## 版本更新日志

#### v1.7.6

- 增加"路由" `Fragment` 的功能(其实就是针对`Fragment`做的一个更简单的获取方式)
    - 任意一个 `Fragment` 使用 `@FragmentAnno` 标记即可
    - 如何使用请看, 路由的 wiki [跳转 Fragment](https://github.com/xiaojinzi123/Component/wiki/%E8%B7%B3%E8%BD%AC-Fragment)
- 增加 RxRouter 对路由 Fragment 的支持, 返回的是 Single<Fragment>
- 重命名 RxService 为 RxServiceManager
- 重命名 Service 为 ServiceManager

#### v1.7.5.1 
- 属性注入增加方法 `Component.inject(Object target, Intent intent)` 以便在 `onNewIntent` 方法中使用
- 升级 RxJava 版本到 2.2.13
- 升级其他库的版本至最新

#### v1.7.5
- 纠正接口 `IComponentApplication` 中 `onDestory` 方法的拼写错误, 正确为 `onDestroy`
- 新增 afterEventAction, 表示跳转成功或者失败之后的回调, 不允许抛出异常, 会导致奔溃
- 新增 afterErrorAction, 表示跳转失败之后的回调, 不允许抛出异常, 会导致奔溃
- 修改内置的同样的路由请求在一秒内只能启动一次的错误日志的输出形式
- 重构几个 `Action` 这块用户自定义的这块的执行顺序
- 路由 `Api` 增加 `AfterErrorActionAnno` 和 `AfterEventActionAnno` 注解

#### v1.7.4
- Router.with() 方法支持空参数的形式了, 这种形式默认会使用 `Application` 作为 `Context`, 作者虽然支持了这种形式, 但是不建议平时使用的时候故意使用 `Application`. 因为当你没有用路由框架的之前, 你通常也会使用当前 `Activity Context` 的, 所以作者呼吁大家, 在有 `Activity Context` 的时候, 建议传入 `Activity` 的 `Context`

#### v1.7.3.2
- 路由跳转的进阶版路由 Api 支持返回 RxJava 的 Observable 啦, 支持 `Single`, `Completeable`

#### v1.7.3.1
- 修复 `Fragment` 无法自动注入的 `Bug`
- 类 `ComponentConfig` 重命名为 `Component`
- 源码中增加单独运行模块 `Module1` 的范例,通过新建了一个 `Module1Run` 的 `Module` 去运行

#### v1.7.3.0
- 支持属性的注入, 使用 `@FiledAutowireAnno` 注解
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
- 完善支持路由 api 的使用方式
- 删除自定义跳转返回 void 的功能
- 跳转增加支持 flag 和 category 的支持

#### v1.7.0
- 取消支持自定义跳转的时候方法的参数可以自定义的功能
- 支持了全部属性的界面注入功能,通过 ParameterSupport.inject(this) 即可完成注入

#### v1.6.1
- 修复无法支持 `requestCode` 的问题

#### v1.6.0
- 支持了类似 `Retrofit` 的路由接口 `Api`, 详细的请看源码中的示例代码. 全局搜索 `@RouterApiAnno` 注解标记的类, 那些都是范例

#### v1.6.0 之前的版本
之前的版本就不再追溯了, 之后的每次更新我都会详细记录更新的日志

## License

```
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
