package com.xiaojinzi.component.error

import java.lang.RuntimeException

class NotSupportException(message: String?) : RuntimeException(message)