package club.asynclab.asyncauth.misc

import kotlin.reflect.KProperty

class SimpleDelegator<T : Any>(private val initializer: (String) -> T, private val post: (T) -> Unit) {
    private lateinit var v: T

    operator fun provideDelegate(thisRef: Any?, prop: KProperty<*>): SimpleDelegator<T> {
        this.v = initializer(prop.name)
        post(this.v)
        return this
    }

    operator fun getValue(thisRef: Any?, prop: KProperty<*>): T {
        return this.v
    }

    operator fun setValue(thisRef: Any?, prop: KProperty<*>, value: T) {
        this.v = value
    }
}