package com.xiaojinzi.component.bean

import android.content.Intent
import com.xiaojinzi.component.error.ignore.ActivityResultException

/**
 * activity result 的返回对象,[android.app.Activity.onActivityResult]
 * time   : 2018/12/04
 *
 * @author : xiaojinzi
 */
data class
ActivityResult(
        val requestCode: Int,
        val resultCode: Int,
        val data: Intent?
) {

    @Throws(ActivityResultException::class)
    fun intentCheckAndGet(): Intent {
        if (data == null) {
            throw ActivityResultException("the intent result data is null")
        }
        return data
    }

    @Throws(ActivityResultException::class)
    fun intentWithResultCodeCheckAndGet(expectedResultCode: Int): Intent {
        if (data == null) {
            throw ActivityResultException("the intent result data is null")
        }
        if (expectedResultCode != resultCode) {
            throw ActivityResultException("the resultCode is not matching $expectedResultCode")
        }
        return data
    }

}