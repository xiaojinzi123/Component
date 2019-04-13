Component
-------

这个组件化方案参考了 ARouter,WMRouter,ActivityRouter,CC,DDComponent等开源的组件化框架
结合了其中的一些优点并且扩展了几个很好用的特性：

- 路由可手动取消
- 路由自动取消,当发起Fragment或者Activity销毁
- 零配置实现任何地方拿到目标界面返回的需要在onActivityResult拿的Intent
- 用户自定义的拦截器的实现方法和Callback回调方法都是在主线程,并且以Callback机制,让你可以在拦截器中做任何事情
- 把服务发现和Router跳转分开,Arouter,WMRouter等都是一起的,我个人认为分开更好
- 采取了WMRouter等框架设计的页面拦截器(具体怎么回事看wiki)
- Rx扩展库扩展完美结合了 RxJava2

RouterGoPlugin
-------

[一个帮助您识别路由和拦截器的库,减少您查找的时间,配合组件化使用](https://github.com/xiaojinzi123/RouterGoPlugin)

如何使用见 wiki 
-------

[usage](https://github.com/xiaojinzi123/Component/wiki/how-to-use)

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