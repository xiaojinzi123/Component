package com.xiaojinzi.component.error.ignore

import java.lang.RuntimeException

/**
 * 表示目标界面没有找到
 *
 * time   : 2018/11/11
 *
 * @author : xiaojinzi
 */
class TargetActivityNotFoundException(message: String? = null) : RuntimeException(message)