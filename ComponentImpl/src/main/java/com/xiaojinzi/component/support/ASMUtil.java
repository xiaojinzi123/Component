package com.xiaojinzi.component.support;

import android.support.annotation.Nullable;

import com.xiaojinzi.component.anno.support.CheckClassName;
import com.xiaojinzi.component.application.IComponentHostApplication;
import com.xiaojinzi.component.fragment.IComponentHostFragment;
import com.xiaojinzi.component.impl.application.ModuleManager;
import com.xiaojinzi.component.interceptor.IComponentHostInterceptor;
import com.xiaojinzi.component.router.IComponentHostRouter;
import com.xiaojinzi.component.router.IComponentHostRouterDegrade;
import com.xiaojinzi.component.service.IComponentHostService;

/**
 * 下面的方法都是空方法, 如果用户用了 Gradele 插件去配置各个模块的名称, 那么下面的空方法都会被 Gradle 插件
 * 利用 ASM 去填写对应的代码. 让代码生效
 * <p>
 * 在 Component 框架初始化的时候,
 * 框架要求用户调用 {@link ModuleManager} 的注册相关的方法,
 * 完成指定模块的注册, 比如代码如下：
 * ModuleManager.registerArr("user","music",...);
 * 这里面做了什么事情呢？其实这里面会根据用户传递进来的每一个名称,
 * 通过 {@link com.xiaojinzi.component.ComponentUtil#genHostFragmentClassName(String)} 等方法来生成一个
 * 全类名, 然后根据反射去寻找对应的类. 寻找失败会打印日志或者忽略, 但是不会崩溃, 因为这个本来就是需要用户保证传递的
 * 名称是有效的.
 * 上面提到了, 反射去找对应的类, 其实一两次的反射我不会关心. 但是其实由于框架的设计目前有几大模块：
 * 1. 路由模块 {@link com.xiaojinzi.component.anno.RouterAnno}
 * 2. 路由降级模块 {@link com.xiaojinzi.component.anno.RouterDegradeAnno}
 * 3. Service 服务发现模块 {@link com.xiaojinzi.component.anno.ServiceAnno}
 * 4. 拦截器模块 {@link com.xiaojinzi.component.anno.InterceptorAnno}
 * 5. Fragment 模块 {@link com.xiaojinzi.component.anno.FragmentAnno}
 * 6. 以及最重要的生命周期模块 {@link com.xiaojinzi.component.anno.ModuleAppAnno}
 * 我们加载模块其实就是告知 {@link ModuleManager} 反射的时候需要找哪个生成类, 同时生命周期对应的代码中会去告知对应的
 * 其他模块目前需要加载哪个名称的组件, 然后每个组件的管理器都会去加载自己的生成类, 代码如下：
 * <img src="https://i.loli.net/2019/10/26/LU5O8IrRxdGuAbl.png" >
 * <p>
 * 所以这个整个过程用了几次反射呢？如果假设你加载的模块是 N 个, 那么总的反射次数是 N * 6 次. 耗时大概在几十毫秒.
 * 对于有些人觉得这个时间无所谓, 其实我也这么觉得. 但是就是有人受不了这么多次的反射. 那么怎么办呢？
 * <p>
 * 在所有地方进行反射之前, 都会调用此类的方法去获取目标类. 但是我们可以看到下面的代码都是空实现, 所以在你运行之后,
 * 不会起到任何的效果. 但是如果这里面的代码在编译之后对应的 Class 文件被修改了呢？换句话说, 要不走反射, 你这些方法
 * 必须返回对应的对象, 不能为空, 否则后面还是会走反射,
 * 具体代码可以参看 {@link ModuleManager#findModuleApplication(String)}.
 * 所以这个类有啥用呢？简单点说就是我们会利用插件, 在编译之后对此类的空方法填上一些代码. 让他是以正常的 new 对象
 * 的方式返回的, 而不是反射. 而抽取出这个类, 是为了字节码改动的范围尽可能的小.
 * <p>
 * 里面生成多少代码, 取决于你有多少模块被打包进来, 但是加载哪些模块还是你自己说了算的 {@link ModuleManager#registerArr(String...)}
 * 这个类只是生成了本身需要反射获取的对象而已, 加快几毫秒的启动速度
 *
 * @author xiaojinzi
 */
@CheckClassName("ComponentPlugin 插件的模块需要关注class 的名称和包名的变化")
public class ASMUtil {

    /**
     * 此方法特别重要, 为了加快启动初始化的速度, 作者写了一个 Gradle 插件, 去动态修改此方法的实现.
     * 增加对 host 参数的判断, 并且返回对应模块的实现类
     * 最终修改后的代码会像下方注释后的代码一样
     *
     * @param host 对应模块的名称, 不考虑大小写
     * @return 返回对应模块的 Application {@link IComponentHostApplication}
     */
    @Nullable
    public static IComponentHostApplication findModuleApplicationAsmImpl(String host) {
        return null;
    }

    /**
     * 获取指定模块的拦截器管理器
     *
     * @param host 对应模块的名称, 不考虑大小写
     * @return 返回对应模块的 Application {@link IComponentHostInterceptor}
     */
    @Nullable
    public static IComponentHostInterceptor findModuleInterceptorAsmImpl(String host) {
        return null;
    }

    /**
     * 获取指定模块的 Router 管理器
     *
     * @param host 对应模块的名称, 不考虑大小写
     * @return 返回对应模块的 {@link IComponentHostRouter}
     */
    @Nullable
    public static IComponentHostRouter findModuleRouterAsmImpl(String host) {
        return null;
    }

    /**
     * 获取指定模块的 RouterDegrade 管理器
     *
     * @param host 对应模块的名称, 不考虑大小写
     * @return 返回对应模块的 {@link IComponentHostRouterDegrade}
     */
    @Nullable
    public static IComponentHostRouterDegrade findModuleRouterDegradeAsmImpl(String host) {
        return null;
    }

    /**
     * 获取指定模块的 Service 管理器
     *
     * @param host 对应模块的名称, 不考虑大小写
     * @return 返回对应模块的 {@link IComponentHostService}
     */
    @Nullable
    public static IComponentHostService findModuleServiceAsmImpl(String host) {
        return null;
    }

    /**
     * 获取指定模块的 Fragment 管理器
     *
     * @param host 对应模块的名称, 不考虑大小写
     * @return 返回对应模块的 {@link IComponentHostFragment}
     */
    @Nullable
    public static IComponentHostFragment findModuleFragmentAsmImpl(String host) {
        return null;
    }

}
