package org.jetbrains.kotlinx.dataframe.plugin.impl.api

import org.jetbrains.kotlin.fir.types.ConeClassLikeType
import org.jetbrains.kotlin.fir.types.ConeKotlinType
import org.jetbrains.kotlin.fir.types.ConeNullability
import org.jetbrains.kotlin.fir.types.constructClassLikeType
import org.jetbrains.kotlin.fir.types.isSubtypeOf
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators.Aggregator
import org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators.Aggregators
import org.jetbrains.kotlinx.dataframe.plugin.impl.AbstractSchemaModificationInterpreter
import org.jetbrains.kotlinx.dataframe.plugin.impl.Arguments
import org.jetbrains.kotlinx.dataframe.plugin.impl.PluginDataFrameSchema
import org.jetbrains.kotlinx.dataframe.plugin.impl.SimpleCol
import org.jetbrains.kotlinx.dataframe.plugin.impl.SimpleDataColumn
import org.jetbrains.kotlinx.dataframe.plugin.impl.dataFrame
import org.jetbrains.kotlinx.dataframe.plugin.impl.ignore
import org.jetbrains.kotlinx.dataframe.plugin.impl.simpleColumnOf
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.createType

private object PrimitiveClassIds {
    const val INT = "kotlin/Int"
    const val LONG = "kotlin/Long"
    const val DOUBLE = "kotlin/Double"
    const val FLOAT = "kotlin/Float"
    const val SHORT = "kotlin/Short"
    const val BYTE = "kotlin/Byte"
}

private fun KClass<*>.toClassId(): ClassId? = when (this) {
    Int::class -> ClassId.fromString(PrimitiveClassIds.INT)
    Long::class -> ClassId.fromString(PrimitiveClassIds.LONG)
    Double::class -> ClassId.fromString(PrimitiveClassIds.DOUBLE)
    Float::class -> ClassId.fromString(PrimitiveClassIds.FLOAT)
    Short::class -> ClassId.fromString(PrimitiveClassIds.SHORT)
    Byte::class -> ClassId.fromString(PrimitiveClassIds.BYTE)
    else -> null
}

private val primitiveTypeMap = mapOf(
    PrimitiveClassIds.INT to Int::class,
    PrimitiveClassIds.LONG to Long::class,
    PrimitiveClassIds.DOUBLE to Double::class,
    PrimitiveClassIds.FLOAT to Float::class,
    PrimitiveClassIds.SHORT to Short::class,
    PrimitiveClassIds.BYTE to Byte::class
)

fun ConeKotlinType.toKType(): KType? {
    return (this as? ConeClassLikeType)?.let { coneType ->
        val nullable = coneType.nullability == ConeNullability.NULLABLE
        primitiveTypeMap[coneType.lookupTag.classId.asString()]
            ?.createType(nullable = nullable)
    }
}

fun KType.toConeKotlinType(): ConeKotlinType? {
    val kClass = this.classifier as? KClass<*> ?: return null
    val classId = kClass.toClassId() ?: return null

    return classId.constructClassLikeType(
        typeArguments = emptyArray(),
        isNullable = this.isMarkedNullable
    )
}

internal fun Arguments.generateStatisticResultColumns(
    statisticAggregator: Aggregator<*, *>,
    inputColumns: List<SimpleDataColumn>
): List<SimpleCol> {
    return inputColumns.map { col -> createUpdatedColumn(col, statisticAggregator) }
}

private fun Arguments.createUpdatedColumn(
    column: SimpleDataColumn,
    statisticAggregator: Aggregator<*, *>
): SimpleCol {
    val originalType = column.type.type
    val inputKType = originalType.toKType()
    val resultKType = inputKType?.let { statisticAggregator.calculateReturnType(it, emptyInput = true) }
    val updatedType = resultKType?.toConeKotlinType() ?: originalType
    return simpleColumnOf(column.name, updatedType)
}

internal val skipNaN = true
internal val ddofDefault: Int = 1
internal val percentileArg: Double = 30.0
internal val sum = Aggregators.sum(skipNaN)
internal val mean = Aggregators.mean(skipNaN)
internal val std = Aggregators.std(skipNaN, ddofDefault)
internal val median = Aggregators.median(skipNaN)
internal val min = Aggregators.min<Double>(skipNaN)
internal val max = Aggregators.max<Double>(skipNaN)
internal val percentile = Aggregators.percentile(percentileArg, skipNaN)

/** Adds to the schema only numerical columns. */
abstract class Aggregator0(val aggregator: Aggregator<*, *>) : AbstractSchemaModificationInterpreter() {
    private val Arguments.receiver: PluginDataFrameSchema by dataFrame()

    override fun Arguments.interpret(): PluginDataFrameSchema {
        val resolvedColumns = receiver.columns()
            .filterIsInstance<SimpleDataColumn>()
            .filter { it.type.type.isSubtypeOf(session.builtinTypes.numberType.type, session) }

        val newColumns = generateStatisticResultColumns(aggregator, resolvedColumns)

        return PluginDataFrameSchema(newColumns)
    }
}

class Sum0 : Aggregator0(sum)

class Mean0 : Aggregator0(mean)

class Std0 : Aggregator0(std)

/** Adds to the schema only numerical columns. */
abstract class AggregatorIntraComparable0(val aggregator: Aggregator<*, *>) : AbstractSchemaModificationInterpreter() {
    private val Arguments.receiver: PluginDataFrameSchema by dataFrame()

    override fun Arguments.interpret(): PluginDataFrameSchema {
        val resolvedColumns = receiver.columns()
            .filterIsInstance<SimpleDataColumn>()
            .filter { isIntraComparable(it, session) }

        val newColumns = generateStatisticResultColumns(aggregator, resolvedColumns)

        return PluginDataFrameSchema(newColumns)
    }
}

class Median0 : AggregatorIntraComparable0(median)

class Max0 : AggregatorIntraComparable0(max)

class Min0 : AggregatorIntraComparable0(min)

class Percentile0 : AggregatorIntraComparable0(percentile) {
    val Arguments.percentile by ignore()
}

/** Adds to the schema all resolved columns. */
abstract class Aggregator1 (val aggregator: Aggregator<*, *>) : AbstractSchemaModificationInterpreter() {
    private val Arguments.receiver: PluginDataFrameSchema by dataFrame()
    private val Arguments.columns: ColumnsResolver by arg()

    override fun Arguments.interpret(): PluginDataFrameSchema {
        val resolvedColumns = columns.resolve(receiver).map { it.column }.filterIsInstance<SimpleDataColumn>().toList()

        val newColumns = generateStatisticResultColumns(aggregator, resolvedColumns)

        return PluginDataFrameSchema(newColumns)
    }
}

class Sum1 : Aggregator1(sum)

class Mean1 : Aggregator1(mean)

class Std1 : Aggregator1(std)

class Median1 : Aggregator1(median)

class Max1 : Aggregator1(max)

class Min1 : Aggregator1(min)

class Percentile1 : Aggregator1(percentile) {
    val Arguments.percentile by ignore()
}
