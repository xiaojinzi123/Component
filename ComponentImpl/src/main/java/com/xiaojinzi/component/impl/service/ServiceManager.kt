package com.xiaojinzi.component.impl.service

import androidx.annotation.AnyThread
import androidx.annotation.WorkerThread
import com.xiaojinzi.component.anno.support.CheckClassNameAnno
import com.xiaojinzi.component.anno.support.NotAppUseAnno
import com.xiaojinzi.component.error.ServiceRepeatCreateException
import com.xiaojinzi.component.support.Callable
import com.xiaojinzi.component.support.DecoratorCallable
import com.xiaojinzi.component.support.SingletonCallable
import com.xiaojinzi.component.support.Utils
import java.lang.RuntimeException
import java.util.*

/**
 * 服务的容器,使用这个服务容器你需要判断获取到的服务是否为空,对于使用者来说还是比较不方便的
 * 建议使用 Service 扩展的版本 RxService
 *
 * @author xiaojinzi
 */
@CheckClassNameAnno
object ServiceManager {

    private const val DEFAULT_NAME = ""

    /**
     * Service 的集合. 线程不安全的
     */
    private val serviceMap: MutableMap<Class<*>, HashMap<String, Callable<*>>> = mutableMapOf()

    /**
     * Service 装饰者的集合. 线程不安全的
     */
    private val serviceDecoratorMap: MutableMap<Class<*>, HashMap<String, DecoratorCallable<*>>> = mutableMapOf()

    private val uniqueServiceSet: MutableSet<String> = HashSet()

    /**
     * 需要自动初始化的 Service 的 class
     */
    private val autoInitMap: MutableMap<Class<*>, String?> = mutableMapOf()

    /**
     * 注册自动注册的 Service Class
     */
    @AnyThread
    @JvmStatic
    @NotAppUseAnno
    fun <T> registerAutoInit(tClass: Class<T>) {
        registerAutoInit(tClass, null)
    }

    /**
     * 注册自动注册的 Service Class
     */
    @AnyThread
    @JvmStatic
    @NotAppUseAnno
    fun <T> registerAutoInit(tClass: Class<T>, name: String?) {
        Utils.checkNullPointer(tClass, "tClass")
        autoInitMap[tClass] = name
    }

    /**
     * 反注册自动注册的 Service Class
     */
    @AnyThread
    @JvmStatic
    @NotAppUseAnno
    fun <T> unregisterAutoInit(tClass: Class<T>) {
        Utils.checkNullPointer(tClass, "tClass")
        autoInitMap.remove(tClass)
    }

    /**
     *
     * 初始化那些需要自动初始化的 Service
     */
    @JvmStatic
    @WorkerThread
    @NotAppUseAnno
    fun autoInitService() {
        for ((key, value) in autoInitMap) {
            if (value == null) {
                // 初始化实现类
                get(tClass = key)
            } else {
                get(tClass = key, name = value)
            }
        }
    }

    /**
     * 注册一个装饰者
     *
     * @param tClass   装饰目标的接口
     * @param uid      注册的这个装饰者的唯一的标记
     * @param callable 装饰者的对象获取者
     * @param <T>      装饰目标
    </T> */
    @AnyThread
    @JvmStatic
    @NotAppUseAnno
    fun <T> registerDecorator(tClass: Class<T>,
                              uid: String,
                              callable: DecoratorCallable<out T>) {
        Utils.checkNullPointer(tClass, "tClass")
        Utils.checkNullPointer(uid, "uid")
        Utils.checkNullPointer(callable, "callable")
        synchronized(serviceDecoratorMap) {
            var map = serviceDecoratorMap[tClass]
            if (map == null) {
                map = HashMap()
                serviceDecoratorMap[tClass] = map
            }
            if (serviceDecoratorMap.containsKey(uid)) {
                throw RuntimeException(tClass.simpleName + " the key of '" + uid + "' is exist")
            }
            map.put(uid, callable)
        }
    }

    /**
     * 注册一个装饰者
     *
     * @param tClass 装饰目标的接口
     * @param uid    注册的这个装饰者的唯一的标记
     * @param <T>    装饰目标
    </T> */
    @AnyThread
    @JvmStatic
    @NotAppUseAnno
    fun <T> unregisterDecorator(tClass: Class<T>, uid: String) {
        Utils.checkNullPointer(tClass, "tClass")
        Utils.checkNullPointer(uid, "uid")
        synchronized(serviceDecoratorMap) {
            val map = serviceDecoratorMap[tClass]
            map?.remove(uid)
        }
    }

    @AnyThread
    @JvmStatic
    @NotAppUseAnno
    fun <T> register(tClass: Class<T>, callable: Callable<out T>) {
        register(tClass, DEFAULT_NAME, callable)
    }

    /**
     * 你可以注册一个服务,服务的初始化可以是懒加载的
     * 注册的时候, 不会初始化目标 Service 的
     * [.get] 方法内部才会初始化目标 Service
     */
    @AnyThread
    @JvmStatic
    @NotAppUseAnno
    fun <T> register(tClass: Class<T>, name: String, callable: Callable<out T>) {
        Utils.checkNullPointer(tClass, "tClass")
        Utils.checkNullPointer(name, "name")
        Utils.checkNullPointer(callable, "callable")
        synchronized(serviceMap) {
            var implServiceMap = serviceMap[tClass]
            if (implServiceMap == null) {
                implServiceMap = HashMap()
                serviceMap[tClass] = implServiceMap
            }
            if (implServiceMap.containsKey(name)) {
                throw RuntimeException(tClass.simpleName + " the key of '" + name + "' is exist")
            }
            implServiceMap.put(name, callable)
        }
    }

    @AnyThread
    @JvmStatic
    @NotAppUseAnno
    fun <T> unregister(tClass: Class<T>) {
        unregister(tClass, DEFAULT_NAME)
    }

    @AnyThread
    @JvmStatic
    @NotAppUseAnno
    fun <T> unregister(tClass: Class<T>, name: String) {
        Utils.checkNullPointer(tClass, "tClass")
        Utils.checkNullPointer(name, "name")
        synchronized(serviceMap) {
            val implServiceMap = serviceMap[tClass]
            if (implServiceMap != null) {
                val callable = implServiceMap.remove(name) ?: return
                if (callable is SingletonCallable<*>) {
                    if ((callable as SingletonCallable<Any?>).isInit) {
                        callable.destroy()
                    }
                }
            }
        }
    }

    /**
     * 装饰某一个 Service
     *
     * @param tClass   目标 Service class
     * @param target   目标对象
     * @return 返回一个增强的目标对象的装饰者
     */
    @AnyThread
    @JvmStatic
    @NotAppUseAnno
    fun <T> decorate(tClass: Class<T>, target: T): T {
        Utils.checkNullPointer(tClass, "tClass")
        Utils.checkNullPointer(target, "target")
        var result = target
        synchronized(serviceDecoratorMap) {
            val map = serviceDecoratorMap[tClass]
            if (map != null) {
                val values: Collection<DecoratorCallable<*>> = map.values
                if (values != null) {
                    // 排序
                    val list: List<DecoratorCallable<*>> = ArrayList(values)
                    Collections.sort(list) { o1, o2 -> o1.priority() - o2.priority() }
                    for (callable in list) {
                        val realCallable = callable as DecoratorCallable<T>
                        result = realCallable.get(result)
                    }
                }
            }
        }
        return result
    }

    /**
     * Service 的创建可能不是在主线程. 所以Service 初始化的时候请注意这一点.
     * 内部会保证创建的过程是线程安全的
     *
     * @param tClass 目标对象的 Class 对象
     * @param <T>    目标对象的实例对象
     * @return 目标对象的实例对象
    </T> */
    @AnyThread
    @JvmStatic
    @JvmOverloads
    fun <T> get(tClass: Class<T>, name: String = DEFAULT_NAME): T? {
        Utils.checkNullPointer(tClass, "tClass")
        Utils.checkNullPointer(name, "name")
        synchronized(serviceMap) {
            val uniqueName = tClass.name + ":" + name
            if (uniqueServiceSet.contains(uniqueName)) {
                throw ServiceRepeatCreateException("className is " + tClass.name + ", serviceName is '" + name + "'")
            }
            uniqueServiceSet.add(uniqueName)
            var result: T? = null
            val implServiceMap = serviceMap[tClass]
            if (implServiceMap != null) {
                val callable = implServiceMap[name]
                if (callable != null) {
                    // 如果没创建, 这时候会创建了目标 service 对象
                    val t = Utils.checkNullPointer(callable.get()) as T
                    result = decorate(tClass, t)
                }
            }
            uniqueServiceSet.remove(uniqueName)
            return result
        }
    }

    /**
     * @return 肯定不会为 null
     * @see .get
     */
    @AnyThread
    @JvmStatic
    fun <T> requiredGet(tClass: Class<T>): T {
        return requiredGet(tClass, DEFAULT_NAME)
    }

    /**
     * @return 肯定不会为 null
     * @see .get
     */
    @AnyThread
    @JvmStatic
    fun <T> requiredGet(tClass: Class<T>, name: String): T {
        return Utils.checkNullPointer(get(tClass, name))
    }

}