# Component

![](/Users/xiaojinzi/Documents/code/github/Component/imgs/logo1.png)

**A powerful componentized framework,We pay attention to user experience,welcome to use.Please commit your issue when you have any problem. Also you can scan qrcode to join  `QQ` group and ask some questions.**

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
    <img height=22 src="https://github.com/fluidicon.png" />
</a>

## Demo Apk(scan qrcode or click the image to download)

<a href="https://files.gitee.com/group1/M00/07/F6/PaAvDFz3FnGAcMLcADcIxREinEY078.apk?attname=app-debug.apk">
    <img height=180 src="/Users/xiaojinzi/Documents/code/github/Component/imgs/demoApk.png" />
</a>

## The advantage of `Component`

There are many componentized framework for ` Android`, and what are advantages of `Component`?

- [x] Support standard `URI`.
- [x] Support `androidx`, there is almost no componentized framework can support it.
- [x] Support lifecycle of `Module`
- [x] The design of  `Component`  is close to native, less invasive for native code, minimizing the possibility of retaining native code
- [x] Support autowire of `Parameter` and `Service` 
- [x] The `Interceptor` of `Router` is running on `MainThread` 
  - The design of `Interceptor`, we think 90% code is running on `MainThread`,so the  `Interceptor` of `Router` is running on `MainThread` .Then you can operate the `UI` or `Dialog` and so on.
  - We provoder the `Callback` mechanism,then you can do any thing in `Interceptor`
  - This advantage is very useful
- [x] An `idea plugin`  can navigate to target `Activity`  and find all the use of target `Activity`, [here is the Plugin](https://github.com/xiaojinzi123/RouterGoPlugin)
- [x] Cancel routering, this advantage is very useful
  - [x] You can cancel routering
  - [x] The routering will auto canceled when `Activity` or `Fragment` destoried
- [x] `Interceptor` of router, more detail see [Interceptor Wiki](https://github.com/xiaojinzi123/Component/wiki/%E6%8B%A6%E6%88%AA%E5%99%A8)
  - [x] Global Interceptor
  - [x] Local Interceptor
    - [x] page Interceptor
    - [x] The alias name of Interceptor, you can use Interceptor by name anywhere
- [x] Routering
  - [x] Support standard `URI`
  - [x] Support custom `Intent`
  - [x] Support routering api like [Retrofit 
  - [x] `Idea Plugin`  can navigate to target `Activity`  and find all the use of target `Activity``
- [x] Perfect support `H5`
  - [x] `H5` can use `URL` jump to any `Activity` 
  - [x] Because of [page Interceptor](https://github.com/xiaojinzi123/Component/wiki/%E5%90%8D%E8%AF%8D%E8%A7%A3%E9%87%8A#%E9%A1%B5%E9%9D%A2%E6%8B%A6%E6%88%AA%E5%99%A8), when you routering from `H5`,you don't need to care whether the target `Activity` is need login or other or not
  - [x] Not Support get target `Activity` result from `H5`, this situation you have to design `Type` to do as before.
- [x] **You can get `ActivityResult` with no change of code, also don't need to change any code of `BaseActivity` **
  - [x] As the behavior of system, when `Context` is  `Application` or `Service Context` or `ContentProvider Context`, will not support.
  - [x] In addition to the above points, other situations are all sopport, include `Dialog` and so on.
- [x] The `Routering` and `Service` is divided
- [x] Perfect support `RxJava2` when you use `rx` version.
- [x] [Module run alone](https://github.com/xiaojinzi123/Component/wiki/%E4%B8%9A%E5%8A%A1%E7%BB%84%E4%BB%B6%E5%8D%95%E7%8B%AC%E8%BF%90%E8%A1%8C)

## hello world

[hello world](https://github.com/xiaojinzi123/Component/wiki)

## More usage detail

- dependence and config
  - [dependence and config](https://github.com/xiaojinzi123/Component/wiki/%E4%BE%9D%E8%B5%96%E5%92%8C%E9%85%8D%E7%BD%AE)
  - [dependence and config For AndroidX](https://github.com/xiaojinzi123/Component/wiki/%E4%BE%9D%E8%B5%96%E5%92%8C%E9%85%8D%E7%BD%AE-AndroidX)
- The usage of [RouterAnno](https://github.com/xiaojinzi123/Component/wiki/RouterAnno%E6%B3%A8%E8%A7%A3%E6%A0%87%E8%AE%B0%E7%9B%AE%E6%A0%87%E7%9A%84%E9%AB%98%E7%BA%A7%E7%94%A8%E6%B3%95)
- Routering
  - [Routering Api
  - [Routering with code](https://github.com/xiaojinzi123/Component/wiki/%E8%B7%B3%E8%BD%AC-%E4%BD%BF%E7%94%A8%E4%BB%A3%E7%A0%81%E8%B7%B3%E8%BD%AC)
- [Interceptor](https://github.com/xiaojinzi123/Component/wiki/%E6%8B%A6%E6%88%AA%E5%99%A8)
- [SPI and use](https://github.com/xiaojinzi123/Component/wiki/%E8%B7%A8%E6%A8%A1%E5%9D%97%E8%B0%83%E7%94%A8%E7%9A%84%E6%9C%8D%E5%8A%A1%E5%8F%91%E7%8E%B0%E5%8A%9F%E8%83%BD)
- [Module run alone](https://github.com/xiaojinzi123/Component/wiki/%E4%B8%9A%E5%8A%A1%E7%BB%84%E4%BB%B6%E5%8D%95%E7%8B%AC%E8%BF%90%E8%A1%8C)
- [Noun explanation](https://github.com/xiaojinzi123/Component/wiki/%E5%90%8D%E8%AF%8D%E8%A7%A3%E9%87%8A)
- [Basic introduction](https://github.com/xiaojinzi123/Component/wiki/%E5%9F%BA%E6%9C%AC%E4%BB%8B%E7%BB%8D%E5%92%8C%E6%9E%B6%E6%9E%84%E4%BB%8B%E7%BB%8D)

**if you want leran more you can see [wiki](https://github.com/xiaojinzi123/Component/wiki/) **

## Some articles

- Waht is componentized

 `Idea Plugin`

[RouterGo can navigate to target `Activity`  and find all the use of target `Activity`, you're worth it!](https://github.com/xiaojinzi123/RouterGoPlugin)

## Scan qrcode to join `QQ` group

<div>
<img src="imgs/qq_group1.JPG" width="260px" height="360px" />
<img src="imgs/qq_group2.JPG" width="260px" height="360px" />
</div>

## 

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