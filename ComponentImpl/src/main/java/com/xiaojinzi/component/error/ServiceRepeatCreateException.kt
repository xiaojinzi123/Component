package com.xiaojinzi.component.error

import java.lang.RuntimeException

class ServiceRepeatCreateException(message: String?) : RuntimeException(message)