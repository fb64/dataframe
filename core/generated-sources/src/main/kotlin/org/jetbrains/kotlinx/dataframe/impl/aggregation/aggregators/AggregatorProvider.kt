package org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators

import kotlin.reflect.KProperty

/**
 * Common interface for providers or "factory" objects that create anything of type [T].
 *
 * When implemented, this allows the object to be created using the `by` delegate, to give it a name, like:
 * ```kt
 * val myNamedValue by MyFactory
 * ```
 */
public fun interface Provider<out T> {

    public fun create(name: String): T
}

internal operator fun <T> Provider<T>.getValue(obj: Any?, property: KProperty<*>): T = create(property.name)

/**
 * Common interface for providers of [Aggregators][Aggregator] or "factory" objects that create aggregators.
 *
 * When implemented, this allows an aggregator to be created using the `by` delegate, to give it a name, like:
 * ```kt
 * val myAggregator by MyAggregator.Factory
 * ```
 */
public fun interface AggregatorProvider<in Value : Any, out Return : Any?> :
    Provider<Aggregator<Value, @UnsafeVariance Return>>
