package com.xiaojinzi.component.support

import android.os.Bundle
import android.os.Parcelable
import android.util.SparseArray
import java.io.Serializable
import java.util.ArrayList

interface IBundle<T: IBundle<T>> {

    /**
     * 这时候的 Bundle
     */
    val bundle: Bundle

    fun putAll(bundle: Bundle): T
    fun putBundle(key: String, bundle: Bundle?): T
    fun putCharSequence(key: String, value: CharSequence?): T
    fun putCharSequenceArray(key: String, value: Array<CharSequence>?): T
    fun putCharSequenceArrayList(key: String, value: ArrayList<CharSequence>?): T
    fun putByte(key: String, value: Byte): T
    fun putByteArray(key: String, value: ByteArray?): T
    fun putChar(key: String, value: Char): T
    fun putCharArray(key: String, value: CharArray?): T
    fun putBoolean(key: String, value: Boolean): T
    fun putBooleanArray(key: String, value: BooleanArray?): T
    fun putString(key: String, value: String?): T
    fun putStringArray(key: String, value: Array<String>?): T
    fun putStringArrayList(key: String, value: ArrayList<String>?): T
    fun putShort(key: String, value: Short): T
    fun putShortArray(key: String, value: ShortArray?): T
    fun putInt(key: String, value: Int): T
    fun putIntArray(key: String, value: IntArray?): T
    fun putIntegerArrayList(key: String, value: ArrayList<Int>?): T
    fun putLong(key: String, value: Long): T
    fun putLongArray(key: String, value: LongArray?): T
    fun putFloat(key: String, value: Float): T
    fun putFloatArray(key: String, value: FloatArray?): T
    fun putDouble(key: String, value: Double): T
    fun putDoubleArray(key: String, value: DoubleArray?): T
    fun putParcelable(key: String, value: Parcelable?): T
    fun putParcelableArray(key: String, value: Array<Parcelable>?): T
    fun putParcelableArrayList(key: String, value: ArrayList<out Parcelable>?): T
    fun putSparseParcelableArray(key: String, value: SparseArray<out Parcelable>?): T
    fun putSerializable(key: String, value: Serializable?): T
}

class IBundleImpl<T: IBundle<T>>(override val bundle: Bundle = Bundle()): IBundle<T> {

    override fun putAll(bundle: Bundle): T {
        Utils.checkNullPointer(bundle, "bundle")
        this.bundle.putAll(bundle)
        return this as T
    }

    override fun putBundle(key: String, bundle: Bundle?): T {
        this.bundle.putBundle(key, bundle)
        return this as T
    }

    override fun putCharSequence(key: String, value: CharSequence?): T {
        bundle.putCharSequence(key, value)
        return this as T
    }

    override fun putCharSequenceArray(key: String, value: Array<CharSequence>?): T {
        bundle.putCharSequenceArray(key, value)
        return this as T
    }

    override fun putCharSequenceArrayList(key: String, value: ArrayList<CharSequence>?): T {
        bundle.putCharSequenceArrayList(key, value)
        return this as T
    }

    override fun putByte(key: String, value: Byte): T {
        bundle.putByte(key, value)
        return this as T
    }

    override fun putByteArray(key: String, value: ByteArray?): T {
        bundle.putByteArray(key, value)
        return this as T
    }

    override fun putChar(key: String, value: Char): T {
        bundle.putChar(key, value)
        return this as T
    }

    override fun putCharArray(key: String, value: CharArray?): T {
        bundle.putCharArray(key, value)
        return this as T
    }

    override fun putBoolean(key: String, value: Boolean): T {
        bundle.putBoolean(key, value)
        return this as T
    }

    override fun putBooleanArray(key: String, value: BooleanArray?): T {
        bundle.putBooleanArray(key, value)
        return this as T
    }

    override fun putString(key: String, value: String?): T {
        bundle.putString(key, value)
        return this as T
    }

    override fun putStringArray(key: String, value: Array<String>?): T {
        bundle.putStringArray(key, value)
        return this as T
    }

    override fun putStringArrayList(key: String, value: ArrayList<String>?): T {
        bundle.putStringArrayList(key, value)
        return this as T
    }

    override fun putShort(key: String, value: Short): T {
        bundle.putShort(key, value)
        return this as T
    }

    override fun putShortArray(key: String, value: ShortArray?): T {
        bundle.putShortArray(key, value)
        return this as T
    }

    override fun putInt(key: String, value: Int): T {
        bundle.putInt(key, value)
        return this as T
    }

    override fun putIntArray(key: String, value: IntArray?): T {
        bundle.putIntArray(key, value)
        return this as T
    }

    override fun putIntegerArrayList(key: String, value: ArrayList<Int>?): T {
        bundle.putIntegerArrayList(key, value)
        return this as T
    }

    override fun putLong(key: String, value: Long): T {
        bundle.putLong(key, value)
        return this as T
    }

    override fun putLongArray(key: String, value: LongArray?): T {
        bundle.putLongArray(key, value)
        return this as T
    }

    override fun putFloat(key: String, value: Float): T {
        bundle.putFloat(key, value)
        return this as T
    }

    override fun putFloatArray(key: String, value: FloatArray?): T {
        bundle.putFloatArray(key, value)
        return this as T
    }

    override fun putDouble(key: String, value: Double): T {
        bundle.putDouble(key, value)
        return this as T
    }

    override fun putDoubleArray(key: String, value: DoubleArray?): T {
        bundle.putDoubleArray(key, value)
        return this as T
    }

    override fun putParcelable(key: String, value: Parcelable?): T {
        bundle.putParcelable(key, value)
        return this as T
    }

    override fun putParcelableArray(key: String, value: Array<Parcelable>?): T {
        bundle.putParcelableArray(key, value)
        return this as T
    }

    override fun putParcelableArrayList(key: String, value: ArrayList<out Parcelable>?): T {
        bundle.putParcelableArrayList(key, value)
        return this as T
    }

    override fun putSparseParcelableArray(key: String, value: SparseArray<out Parcelable>?): T {
        bundle.putSparseParcelableArray(key, value)
        return this as T
    }

    override fun putSerializable(key: String, value: Serializable?): T {
        bundle.putSerializable(key, value)
        return this as T
    }

}