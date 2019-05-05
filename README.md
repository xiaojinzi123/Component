Component
-------

**直接拿到 onActivityResult 的酸爽**

<img src="imgs/rxGetData.png" width="640px" height="360px"/>

**网页无缝调用的酸爽**

<img src="imgs/componentDesc.gif" width="250px" height="400px"/>

**属性自动注入的酸爽**

<img src="imgs/fieldInject.png" width="600px" height="360px"/>

这个组件化方案参考了 ARouter,WMRouter,ActivityRouter,CC,DDComponent等开源的组件化框架
结合了其中的一些优点并且扩展了几个很好用的特性：

- 路由可手动取消
- 路由自动取消,当发起Fragment或者Activity销毁
- 零配置实现任何地方拿到目标界面返回的需要在onActivityResult拿的Intent
- 用户自定义的拦截器的实现方法和Callback回调方法都是在主线程,并且以Callback机制,让你可以在拦截器中做任何事情
- 把服务发现和Router跳转分开,Arouter,WMRouter等都是一起的,我个人认为分开更好
- 采取了WMRouter等框架设计的页面拦截器(具体怎么回事看wiki)
- Rx扩展库扩展完美结合了 RxJava2

**更多功能等你来发现**

RouterGoPlugin
-------

[一个帮助您识别路由和拦截器的库,减少您查找的时间,配合组件化使用](https://github.com/xiaojinzi123/RouterGoPlugin)

如何使用见wiki 
-------

[wiki使用介绍](https://github.com/xiaojinzi123/Component/wiki/)

- [名词解释](https://github.com/xiaojinzi123/Component/wiki/%E5%90%8D%E8%AF%8D%E8%A7%A3%E9%87%8A)
- [基本介绍](https://github.com/xiaojinzi123/Component/wiki/%E5%9F%BA%E6%9C%AC%E4%BB%8B%E7%BB%8D%E5%92%8C%E6%9E%B6%E6%9E%84%E4%BB%8B%E7%BB%8D)
- [依赖和配置](https://github.com/xiaojinzi123/Component/wiki/%E4%BE%9D%E8%B5%96%E5%92%8C%E9%85%8D%E7%BD%AE)
- [跳转](https://github.com/xiaojinzi123/Component/wiki/%E8%B7%B3%E8%BD%AC)
- [跳转目标注解标记](https://github.com/xiaojinzi123/Component/wiki/RouterAnno%E6%B3%A8%E8%A7%A3%E6%A0%87%E8%AE%B0%E7%9B%AE%E6%A0%87%E7%9A%84%E9%AB%98%E7%BA%A7%E7%94%A8%E6%B3%95)
- [拦截器](https://github.com/xiaojinzi123/Component/wiki/%E6%8B%A6%E6%88%AA%E5%99%A8)
- [服务发现实现跨模块调用](https://github.com/xiaojinzi123/Component/wiki/%E8%B7%A8%E6%A8%A1%E5%9D%97%E8%B0%83%E7%94%A8%E7%9A%84%E6%9C%8D%E5%8A%A1%E5%8F%91%E7%8E%B0%E5%8A%9F%E8%83%BD)

为了更好的能交流,这里新建了一个QQ群：870981195
或者扫描二维码来进群

<div>
<img src="imgs/qq_group1.JPG" width="260px" height="360px" />
<img src="imgs/qq_group2.JPG" width="260px" height="360px" />
</div>

License
-------

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
