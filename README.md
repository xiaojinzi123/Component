
**`Component` 一定以最快的速度解决您的 `issue`, 提供解决方案.**

可以说到目前为止, 基于 `URI` 方面的所有路由框架中(`ARouter`、`WMRouter`、`ActivityRouter` ...) `Component` 是最强大和完善的组件化框架.

选择一个更好、更全面的、更稳定、更有发展前景的框架更是你们技术团队或者技术负责人要做的事情!

[Component VS ARouter](https://github.com/xiaojinzi123/Component/wiki/Component-%E5%92%8C-ARouter-%E6%AF%94%E8%BE%83)

**[点击查看 Component 解决了哪些开发中的痛点](https://github.com/xiaojinzi123/Component/wiki/Component-%E7%9B%B4%E5%87%BB%E5%BC%80%E5%8F%91%E4%B8%AD%E7%9A%84%E7%97%9B%E7%82%B9)**

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
    <img height=20 src="https://gitee.com/logo-black.svg" />
</a>
<a href="https://github.com/xiaojinzi123/Component">
    <img height=22 src="https://xiaojinzi.oss-cn-shanghai.aliyuncs.com/blogImages/fluidicon.png" />
</a>

## 1. Demo体验

扫码或者点击图片即可下载

<a href="https://github.com/xiaojinzi123/Component/releases/download/v1.7.6.1/app-release.apk">
    <img height=180 src="./imgs/demoApk.png" />
</a>

## 2. Hello World 文档

[最简单的 Hello World 文档](https://github.com/xiaojinzi123/Component/wiki/%E4%B8%BB%E9%A1%B5)

## 3. Component 的功能

组件化方案真的有很多,那么这个组件化方案优秀在哪里？相比于 `ARouter`、`WMRouter`、`ActivityRouter`、`CC`、`DDComponent`
等开源的组件化框架, 有哪些一样或者更加优秀的点

- [x] 支持多 `Module`
- [X] 支持 `Google App Bundle 架构` 
- [x] 支持 `Flutter`, `H5` 等混合项目
- [x] 支持 `androidx`, 几乎没有其他组件化框架支持 `androidx` 的
- [x] 整个设计贴近原生,对原生的代码入侵极少,尽最大的可能保留原生的代码
- [x] 支持依赖注入、支持目标界面的路由参数
- [X] 跨模块调用
- [x] 支持业务组件[生命周期](https://github.com/xiaojinzi123/Component/wiki/%E4%B8%9A%E5%8A%A1%E7%BB%84%E4%BB%B6%E7%9A%84%E7%94%9F%E5%91%BD%E5%91%A8%E6%9C%9F)(被加载和被卸载)
- [x] 配套的 `Idea Plugin` 方便快速浏览,持续会更新此 [插件](https://github.com/xiaojinzi123/RouterGoPlugin)
- [x] 完美支持 `RxJava2`(使用`rx`库)
- [x] 服务发现(跨模块调用)和路由分开设计
      - 其实这两块本来就是两个方面,我不清楚为什么很多方案中都柔和在一块
- [x] [服务发现装饰增强](https://github.com/xiaojinzi123/Component/wiki/%E6%9C%8D%E5%8A%A1%E5%8F%91%E7%8E%B0-%E8%A3%85%E9%A5%B0%E5%A2%9E%E5%BC%BA)
- [x] [业务模块单独运行]([https://github.com/xiaojinzi123/Component/wiki/%E4%B8%9A%E5%8A%A1%E7%BB%84%E4%BB%B6%E5%8D%95%E7%8B%AC%E8%BF%90%E8%A1%8C](https://github.com/xiaojinzi123/Component/wiki/业务组件单独运行))
- [x] 路由跳转功能
    - [x] 支持[生成文档](https://github.com/xiaojinzi123/Component/wiki/%E6%96%87%E6%A1%A3%E7%94%9F%E6%88%90)
    - [x] 支持获取目标的 [ProxyIntent](https://github.com/xiaojinzi123/Component/wiki/ProxyIntent)
    - [x] 支持标准 `URI` 的使用
    - [x] 无缝对接 `H5`
      - [x] `H5` 只需利用 `URL` 即可任意路由到任何界面(只需下面一段统一的跳转. 完全不需要关心目标界面是否需要登陆、定位、权限等.)
        ```
        @JavascriptInterface
        public void openUrl(final String url) {
            Router.with(this).url(url).forward();
        }
        ```
      - [x] `H5` 发起路由不需要关心目标界面需要做的先决条件(框架的[页面拦截器](https://github.com/xiaojinzi123/Component/wiki/%E5%90%8D%E8%AF%8D%E8%A7%A3%E9%87%8A#%E9%A1%B5%E9%9D%A2%E6%8B%A6%E6%88%AA%E5%99%A8)已经帮您做完) 
    - [x] [外部链接跳转](https://github.com/xiaojinzi123/Component/wiki/App-%E5%A4%96%E9%83%A8%E9%93%BE%E6%8E%A5%E8%B7%B3%E8%BD%AC)
    - [x] 支持原生的[跳转动画](https://github.com/xiaojinzi123/Component/wiki/%E8%B7%B3%E8%BD%AC-%E5%8A%A8%E7%94%BB)
    - [x] 支持跳转 `Fragment`(也就是跨组件获取`Fragment`)
    - [x] 支持[单 Activity 多 Fragment 架构](https://github.com/xiaojinzi123/Component/wiki/%E5%8D%95-Activity-%E5%A4%9A-Fragment)
    - [x] 路由拦截器执行线程设计是主线程, 整体是异步的(这点很多人不理解, 没有关系. 先用起来之后在慢慢理解)
      - 在路由拦截器的执行线程的设计上,考虑到用户平时书写的 `90%` 代码都是在主线程的,<br/>所以路由拦截器的执行线程也设计为主线程执行,可以让您放心的操作 `UI`、弹框等操作.<br/>同时提供 `Callback` 机制可以在拦截器中做任何耗时的任务<br/>这点绝对是压倒性的优势,不仅整体是 `异步` 的,而且拦截器中能像平常一样写实现的代码
    - [x] 路由的取消,基本上没有路由框架支持路由的取消,这也是一个很大的优势!
      - [x] 手动用代码取消某次路由
      - [x] 路由自动取消, 当发起路由的 `Fragment` 或者 `Activity` 销毁的时候会取消
    - [x] 路由拦截器,足矣满足所有业务情况(具体看 [拦截器wiki]([https://github.com/xiaojinzi123/Component/wiki/%E6%8B%A6%E6%88%AA%E5%99%A8](https://github.com/xiaojinzi123/Component/wiki/拦截器)))
      - [x] 全局拦截器(针对全部路由)
      - [x] 局部路由拦截器
        - [x] [页面拦截器](https://github.com/xiaojinzi123/Component/wiki/%E6%8B%A6%E6%88%AA%E5%99%A8#%E7%BB%99%E4%BD%A0%E7%9A%84%E6%8B%A6%E6%88%AA%E5%99%A8%E8%B5%B7%E4%B8%80%E4%B8%AA%E5%88%AB%E5%90%8D)(针对所有跳转到某一个界面的路由)
        - [x] 拦截器别名,支持跨模块使用(可以让每一个拦截器都放在自个的模块)
    - [x] 跳转
      - [x] 持标准 `URI`
      - [x] 支持自定义 `Intent`, 你可以给任意一个 `Intent` 标记路由, 这个功能很强大!
      - [x] 支持类似 `Retrofit` 接口编程式跳转
      - [x] `Idea Plugin` 强势支持跳转代码和目标界面的来回导航,也支持拦截器的代码使用和声明处的来回导航
    - [x] **0** 配置拿到目标界面返回的 `ActivityResult`, 很多框架不支持或者需要入侵 `BaseActivity`. 绝对的优势
      - [ ] 和系统的行为一样,当 `Context` 是 `Application` 或者 `Service 的 Context` 或者 `ContentProvider 的 Context `的时候, **不支持获取ActivityResult**, 如果真的有需要, 你可以使用栈顶的 `Activity` 来充当 `Context`
      - [x] 除第一点说的几个 `Context`, 其他的情况都是支持的,包括 `Dialog` 中获取到的 `Context`.
      - [x] 如果你想单独使用这个功能, 也可以单独依赖使用, [链接是这里](https://github.com/xiaojinzi123/ActivityResultHelper)
- [ ] `注解驱动器` 不支持增量更新, 暂时不知道怎么去做

## 4. 完整文档

- 依赖和配置
  - [依赖和配置](https://github.com/xiaojinzi123/Component/wiki/%E4%BE%9D%E8%B5%96%E5%92%8C%E9%85%8D%E7%BD%AE)
  - [依赖和配置 For AndroidX](https://github.com/xiaojinzi123/Component/wiki/%E4%BE%9D%E8%B5%96%E5%92%8C%E9%85%8D%E7%BD%AE-AndroidX)
- [Activity注解使用](https://github.com/xiaojinzi123/Component/wiki/RouterAnno-%E6%B3%A8%E8%A7%A3)
- [组件生命周期](https://github.com/xiaojinzi123/Component/wiki/%E4%B8%9A%E5%8A%A1%E7%BB%84%E4%BB%B6%E7%9A%84%E7%94%9F%E5%91%BD%E5%91%A8%E6%9C%9F)
- 跳转
  - [跳转 Fragment](https://github.com/xiaojinzi123/Component/wiki/%E8%B7%B3%E8%BD%AC-Fragment)
  - [跳转-接口路由的方式](https://github.com/xiaojinzi123/Component/wiki/%E8%B7%B3%E8%BD%AC-%E6%8E%A5%E5%8F%A3%E8%B7%AF%E7%94%B1%E7%9A%84%E6%96%B9%E5%BC%8F)
  - [跳转-使用代码跳转](https://github.com/xiaojinzi123/Component/wiki/%E8%B7%B3%E8%BD%AC-%E4%BD%BF%E7%94%A8%E4%BB%A3%E7%A0%81%E8%B7%B3%E8%BD%AC)
- [拦截器](https://github.com/xiaojinzi123/Component/wiki/%E6%8B%A6%E6%88%AA%E5%99%A8)
- [服务发现实现跨模块调用](https://github.com/xiaojinzi123/Component/wiki/%E8%B7%A8%E6%A8%A1%E5%9D%97%E6%9C%8D%E5%8A%A1%E8%B0%83%E7%94%A8)
- [ProxyIntent](https://github.com/xiaojinzi123/Component/wiki/ProxyIntent) 在需要使用 `Intent` 的地方使用
- [生成文档](https://github.com/xiaojinzi123/Component/wiki/%E6%96%87%E6%A1%A3%E7%94%9F%E6%88%90)
- [单独运行](https://github.com/xiaojinzi123/Component/wiki/%E4%B8%9A%E5%8A%A1%E7%BB%84%E4%BB%B6%E5%8D%95%E7%8B%AC%E8%BF%90%E8%A1%8C)
- [名词解释](https://github.com/xiaojinzi123/Component/wiki/%E5%90%8D%E8%AF%8D%E8%A7%A3%E9%87%8A)
- [基本介绍](https://github.com/xiaojinzi123/Component/wiki/%E5%9F%BA%E6%9C%AC%E4%BB%8B%E7%BB%8D%E5%92%8C%E6%9E%B6%E6%9E%84%E4%BB%8B%E7%BB%8D)
- [更新日志](./changelog.md)

**了解更多请看 [wiki](https://github.com/xiaojinzi123/Component/wiki/) 更多功能等你来发现**
**有关的文章 [到底什么是组件化](https://blog.csdn.net/u011692041/article/details/92572758) **

## 5. 配套的 `Idea Plugin`

`Android Studio` 中搜索插件名称：RouterGo, 即可下载对应的插件

[RouterGo 源码地址：帮助你快速导航到目标界面或者目标拦截器,你值得拥有!](https://github.com/xiaojinzi123/RouterGoPlugin)

## 6. `Component` 项目结构(Demo + 库源码)

- `demo` 示例代码
    - `app`                 --> 壳工程
    - `Module1`             --> `Java` 业务模块
    - `Module1run`          --> `Module1` 业务模块单独运行的 `Application` 应用
    - `Module2`             --> `Kotlin` 业务模块
    - `ModuleHelp`          --> `Help` 业务模块, 一些有关通用的或者系统相关的放这里
    - `ModuleUser`          --> `User` 业务模块
    - `ModuleBase`          --> 各个业务模块的基础模块, 上述的没一个业务模块都需要依赖
- 实现库源码
    - `ComponentApi`        --> `Api` 库
    - `ComponentCompiler`   --> 注解驱动器库
    - `ComponentImpl`       --> 实现库
    - `ComponentRxImpl`     --> 实现库 `RxJava` 的扩展
    - `ComponentPlugin`     --> 配套的 `Gradle`, 功能有两个
        - 优化初始化的方式, 优化反射加载模块为正常的 `new` 对象的方式, 利用字节码修改技术
        - 生成路由文档


## 7. 扫码进群

<div>
    <img src="./imgs/qq_group1.JPG" width="210px" height="300px" />
    <img src="./imgs/qq_group2.JPG" width="210px" height="300px" />
</div>

## 8. 如果你觉得项目不错, 就请我喝杯咖啡吧! 一块钱也是爱!

<img height=200 src="./imgs/collectQRCode.png" />

## 9. License

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
