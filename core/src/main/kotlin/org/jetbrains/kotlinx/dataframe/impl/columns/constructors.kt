package org.jetbrains.kotlinx.dataframe.impl.columns

import org.jetbrains.kotlinx.dataframe.AnyCol
import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.AnyRow
import org.jetbrains.kotlinx.dataframe.ColumnsContainer
import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.Selector
import org.jetbrains.kotlinx.dataframe.api.AddDataRow
import org.jetbrains.kotlinx.dataframe.api.AddExpression
import org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl
import org.jetbrains.kotlinx.dataframe.api.Infer
import org.jetbrains.kotlinx.dataframe.api.PivotColumnsSelector
import org.jetbrains.kotlinx.dataframe.api.PivotDsl
import org.jetbrains.kotlinx.dataframe.api.SortColumnsSelector
import org.jetbrains.kotlinx.dataframe.api.SortDsl
import org.jetbrains.kotlinx.dataframe.api.asDataColumn
import org.jetbrains.kotlinx.dataframe.api.cast
import org.jetbrains.kotlinx.dataframe.api.concat
import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
import org.jetbrains.kotlinx.dataframe.api.indices
import org.jetbrains.kotlinx.dataframe.api.toColumnOf
import org.jetbrains.kotlinx.dataframe.api.toDataFrame
import org.jetbrains.kotlinx.dataframe.columns.ColumnResolutionContext
import org.jetbrains.kotlinx.dataframe.columns.ColumnSet
import org.jetbrains.kotlinx.dataframe.columns.ColumnWithPath
import org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver
import org.jetbrains.kotlinx.dataframe.columns.ValueColumn
import org.jetbrains.kotlinx.dataframe.columns.toColumnsSetOf
import org.jetbrains.kotlinx.dataframe.impl.DataFrameReceiver
import org.jetbrains.kotlinx.dataframe.impl.DataRowImpl
import org.jetbrains.kotlinx.dataframe.impl.asList
import org.jetbrains.kotlinx.dataframe.impl.guessValueType
import org.jetbrains.kotlinx.dataframe.impl.replaceGenericTypeParametersWithUpperbound
import org.jetbrains.kotlinx.dataframe.index
import org.jetbrains.kotlinx.dataframe.nrow
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.withNullability

// region create DataColumn

internal class AddDataRowImpl<T>(index: Int, owner: DataFrame<T>, private val container: List<*>) :
    DataRowImpl<T>(index, owner),
    AddDataRow<T> {

    override fun <C> AnyRow.newValue() = container[index] as C
}

@PublishedApi
internal fun <T, R> ColumnsContainer<T>.newColumn(
    type: KType,
    name: String = "",
    infer: Infer = Infer.Nulls,
    expression: AddExpression<T, R>,
): DataColumn<R> {
    val df = this as? DataFrame<T> ?: dataFrameOf(columns()).cast()
    val (nullable, values) = computeValues(df, expression)
    return when (infer) {
        Infer.Nulls -> DataColumn.createUnsafe(
            name = name,
            values = values,
            type = type.withNullability(nullable).replaceGenericTypeParametersWithUpperbound(),
            infer = Infer.None,
        )

        Infer.Type -> DataColumn.createWithTypeInference(
            name = name,
            values = values,
            nullable = nullable,
        )

        Infer.None -> DataColumn.createUnsafe(
            name = name,
            values = values,
            type = type.replaceGenericTypeParametersWithUpperbound(),
            infer = Infer.None,
        )
    }
}

@PublishedApi
internal fun <T, R> ColumnsContainer<T>.newColumnWithActualType(
    name: String,
    expression: AddExpression<T, R>,
): DataColumn<R> {
    val (_, values) = computeValues(this as DataFrame<T>, expression)
    return createColumnGuessingType(name, values)
}

internal fun <T, R> computeValues(df: DataFrame<T>, expression: AddExpression<T, R>): Pair<Boolean, List<R>> {
    var nullable = false
    val list = ArrayList<R>(df.nrow)
    df.indices().forEach {
        val row = AddDataRowImpl(it, df, list)
        val value = expression(row, row)
        if (value == null) nullable = true
        list.add(value)
    }
    return nullable to list
}

// endregion

// region create Columns

internal fun <C> createColumnSet(
    resolver: (context: ColumnResolutionContext) -> List<ColumnWithPath<C>>,
): ColumnSet<C> =
    object : ColumnSet<C> {
        override fun resolve(context: ColumnResolutionContext) = resolver(context)
    }

internal fun <C> createTransformableColumnSet(
    resolver: (context: ColumnResolutionContext) -> List<ColumnWithPath<C>>,
    transformResolve: (
        context: ColumnResolutionContext,
        transformer: ColumnsResolverTransformer,
    ) -> List<ColumnWithPath<C>>,
): TransformableColumnSet<C> =
    object : TransformableColumnSet<C> {
        override fun resolve(context: ColumnResolutionContext) = resolver(context)

        override fun transformResolve(
            context: ColumnResolutionContext,
            transformer: ColumnsResolverTransformer,
        ): List<ColumnWithPath<C>> = transformResolve(context, transformer)
    }

// region toColumnSet

// region DSL

internal fun <TD, T : DataFrame<TD>, C> Selector<T, ColumnsResolver<C>>.toColumnSet(
    createReceiver: (ColumnResolutionContext) -> T,
): ColumnSet<C> =
    createColumnSet {
        val receiver = createReceiver(it)
        val columnSet = this(receiver, receiver)
        columnSet.resolve(receiver, it.unresolvedColumnsPolicy)
    }

@JvmName("toColumnSetForPivot")
internal fun <T, C> PivotColumnsSelector<T, C>.toColumnSet(): ColumnSet<C> =
    toColumnSet {
        object : DataFrameReceiver<T>(it.df.cast(), it.unresolvedColumnsPolicy), PivotDsl<T> {}
    }

@JvmName("toColumnSetForSort")
internal fun <T, C> SortColumnsSelector<T, C>.toColumnSet(): ColumnSet<C> =
    toColumnSet {
        object : DataFrameReceiver<T>(it.df.cast(), it.unresolvedColumnsPolicy), SortDsl<T> {}
    }

internal fun <T, C> ColumnsSelector<T, C>.toColumnSet(): ColumnSet<C> =
    toColumnSet {
        object : DataFrameReceiver<T>(it.df.cast(), it.unresolvedColumnsPolicy), ColumnsSelectionDsl<T> {}
    }

// endregion

// endregion

// region toComparableColumns

internal fun Array<out String>.toComparableColumns() = toColumnsSetOf<Comparable<Any?>>()

internal fun String.toComparableColumn() = toColumnOf<Comparable<Any?>>()

// endregion

internal fun Array<out String>.toNumberColumns() = toColumnsSetOf<Number>()

// endregion

/**
 * Creates a new column by doing type inference on the given values and
 * some conversions to unify the values if necessary.
 *
 * @param values values to create a column from
 * @param suggestedType optional suggested type for values.
 *   If set to `null` (default) the type will be inferred.
 * @param guessTypeWithSuggestedAsUpperbound Only relevant when [suggestedType]` != null`.
 *   If `true`, type inference will happen with the given [suggestedType] as the supertype.
 * @param defaultValue optional default value for the column used when a [ValueColumn] is created.
 * @param nullable optional hint for the column nullability, used when a [ValueColumn] is created.
 * @param listifyValues if `true`, then values and nulls will be wrapped in a list if they appear among other lists.
 *   For example: `[1, null, listOf(1, 2, 3)]` will become `[[1], [], [1, 2, 3]]`.
 *   Note: this parameter is ignored if another [Collection] is present in the values.
 * @param allColsMakesColGroup if `true`, then, if all values are non-null same-sized columns,
 *   a column group will be created instead of a [DataColumn][DataColumn]`<`[AnyCol][AnyCol]`>`.
 */
@PublishedApi
internal fun <T> createColumnGuessingType(
    values: Iterable<T>,
    suggestedType: KType? = null,
    guessTypeWithSuggestedAsUpperbound: Boolean = false,
    defaultValue: T? = null,
    nullable: Boolean? = null,
    listifyValues: Boolean = false,
    allColsMakesColGroup: Boolean = false,
): DataColumn<T> =
    createColumnGuessingType(
        name = "",
        values = values,
        suggestedType = suggestedType,
        guessTypeWithSuggestedAsUpperbound = guessTypeWithSuggestedAsUpperbound,
        defaultValue = defaultValue,
        nullable = nullable,
        listifyValues = listifyValues,
        allColsMakesColGroup = allColsMakesColGroup,
    )

/**
 * @include [createColumnGuessingType]
 * @param name name for the column
 */
@PublishedApi
internal fun <T> createColumnGuessingType(
    name: String,
    values: Iterable<T>,
    suggestedType: KType? = null,
    guessTypeWithSuggestedAsUpperbound: Boolean = false,
    defaultValue: T? = null,
    nullable: Boolean? = null,
    listifyValues: Boolean = false,
    allColsMakesColGroup: Boolean = false,
): DataColumn<T> {
    val detectType = suggestedType == null || guessTypeWithSuggestedAsUpperbound
    val type = if (detectType) {
        guessValueType(
            values = values.asSequence(),
            upperBound = suggestedType,
            listifyValues = listifyValues,
            allColsMakesRow = allColsMakesColGroup,
        )
    } else {
        suggestedType!!
    }

    return when (type.classifier!! as KClass<*>) {
        DataRow::class -> {
            // guessValueType can only return DataRow if all values are AnyRow?
            // or all are AnyCol and they all have the same size
            if (values.firstOrNull() is AnyCol) {
                val df = dataFrameOf(values as Iterable<AnyCol>)
                DataColumn.createColumnGroup(name, df)
            } else {
                val df = values.map {
                    (it as AnyRow?)?.toDataFrame() ?: DataFrame.empty(1)
                }.concat()
                DataColumn.createColumnGroup(name, df)
            }.asDataColumn().cast()
        }

        DataFrame::class -> {
            val frames = values.map {
                when (it) {
                    null -> DataFrame.empty()
                    is AnyFrame -> it
                    is AnyRow -> it.toDataFrame()
                    is List<*> -> (it as List<AnyRow>).toDataFrame()
                    else -> throw IllegalStateException()
                }
            }
            DataColumn.createFrameColumn(name, frames).asDataColumn().cast()
        }

        List::class -> {
            val nullable = type.isMarkedNullable
            var isListOfRows: Boolean? = null
            val lists = values.map {
                when (it) {
                    null -> if (nullable) null else emptyList()

                    is List<*> -> {
                        if (isListOfRows != false && it.isNotEmpty()) isListOfRows = it.all { it is AnyRow }
                        it
                    }

                    else -> { // if !detectType and suggestedType is a list, we wrap the values in lists
                        if (isListOfRows != false) isListOfRows = it is AnyRow
                        listOf(it)
                    }
                }
            }
            if (isListOfRows == true) {
                val frames = lists.map {
                    if (it == null) {
                        DataFrame.empty()
                    } else {
                        (it as List<AnyRow>).concat()
                    }
                }
                DataColumn.createFrameColumn(name, frames).cast()
            } else {
                DataColumn.createValueColumn(name, lists, type, defaultValue = defaultValue).cast()
            }
        }

        else -> {
            if (nullable == null) {
                DataColumn.createValueColumn(
                    name = name,
                    values = values.asList(),
                    type = type,
                    infer = if (detectType) Infer.None else Infer.Nulls,
                    defaultValue = defaultValue,
                )
            } else {
                DataColumn.createValueColumn(
                    name = name,
                    values = values.asList(),
                    type = type.withNullability(nullable),
                    defaultValue = defaultValue,
                )
            }
        }
    }
}
