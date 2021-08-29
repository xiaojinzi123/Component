package com.xiaojinzi.component.error

import java.lang.RuntimeException

class RunTimeTimeoutException(message: String? = null) : RuntimeException(message) {
}