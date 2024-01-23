package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.Grammar
import org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.Grammar.InfixName
import org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.Grammar.Name
import org.jetbrains.kotlinx.dataframe.columns.ColumnPath
import org.jetbrains.kotlinx.dataframe.columns.ColumnReference
import org.jetbrains.kotlinx.dataframe.columns.ColumnSet
import org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver
import org.jetbrains.kotlinx.dataframe.columns.SingleColumn
import org.jetbrains.kotlinx.dataframe.documentation.AccessApiLink
import org.jetbrains.kotlinx.dataframe.documentation.DoubleIndent
import org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate
import org.jetbrains.kotlinx.dataframe.documentation.Indent
import org.jetbrains.kotlinx.dataframe.documentation.LineBreak
import org.jetbrains.kotlinx.dataframe.impl.columns.ColumnsList
import kotlin.reflect.KProperty

// region ColumnsSelectionDsl

/**
 * See [Grammar]
 */
public interface AndColumnsSelectionDsl {

    /**
     * ## And Operator Grammar
     *
     * @include [DslGrammarTemplate]
     * {@setArg [DslGrammarTemplate.DefinitionsArg]
     *  {@include [DslGrammarTemplate.ColumnSetDef]}
     *  {@include [LineBreak]}
     *  {@include [DslGrammarTemplate.ColumnGroupDef]}
     *  {@include [LineBreak]}
     *  {@include [DslGrammarTemplate.ColumnDef]}
     *  {@include [LineBreak]}
     *  {@include [DslGrammarTemplate.ColumnOrColumnSetDef]}
     * }
     *
     * {@setArg [DslGrammarTemplate.PlainDslFunctionsArg]
     *  {@include [DslGrammarTemplate.ColumnOrColumnSetRef]} {@include [InfixName]}` [ `**`{`**` ] `{@include [DslGrammarTemplate.ColumnOrColumnSetRef]}` [ `**`\\}`**` ] `
     *
     *  `| `{@include [DslGrammarTemplate.ColumnOrColumnSetRef]}{@include [Name]} **`(`**`|`**`{ `**{@include [DslGrammarTemplate.ColumnOrColumnSetRef]}**` \\}`**`|`**`)`**
     * }
     *
     * {@setArg [DslGrammarTemplate.ColumnSetFunctionsArg]
     *  {@include [Indent]}{@include [Name]} **`(`**`|`**`{ `**{@include [DslGrammarTemplate.ColumnOrColumnSetRef]}**` \\}`**`|`**`)`**
     * }
     *
     * {@setArg [DslGrammarTemplate.ColumnGroupFunctionsArg]
     *  {@include [Indent]}{@include [Name]} **`(`**`|`**`{ `**{@include [DslGrammarTemplate.ColumnOrColumnSetRef]}**` \\}`**`|`**`)`**
     * }
     */
    public interface Grammar {

        /** [**and**][ColumnsSelectionDsl.and] */
        public interface InfixName

        /** .[**and**][ColumnsSelectionDsl.and] */
        public interface Name
    }

    /**
     * ## And Operator
     * The [and] operator allows you to combine selections of columns or simply select multiple columns at once.
     *
     * You can even mix and match any {@include [AccessApiLink]}!
     *
     * ### Check out: [Grammar]
     *
     * #### Examples:
     *
     * `df.`[groupBy][DataFrame.groupBy]` { "colA" `[and][String.and]` colB }`
     *
     * `df.`[select][DataFrame.select]` {`
     *
     * {@include [Indent]}[colsOf][SingleColumn.colsOf]`<`[String][String]`>() `[and][ColumnSet.and]` {`
     *
     * {@include [DoubleIndent]}[colsAtAnyDepth][ColumnsSelectionDsl.colsAtAnyDepth]` { "price" `[in][String.contains]` it.`[name][DataColumn.name]` }`
     *
     * {@include [Indent]}`}`
     *
     * `}`
     *
     * `df.`[select][DataFrame.select]` { "colC" `[and][String.and]` Type::colB `[and][KProperty.and]` "pathTo"["colC"] `[and][ColumnPath.and]` colD }`
     *
     * #### Example for this overload:
     *
     * {@getArg [CommonAndDocs.ExampleArg]}
     *
     * @return A [ColumnSet] that contains all the columns from the [ColumnsResolvers][ColumnsResolver] on the left
     *   and right side of the [and] operator.
     */
    private interface CommonAndDocs {

        interface ExampleArg
    }

    // region ColumnsResolver

    /**
     * @include [CommonAndDocs]
     * @setArg [CommonAndDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { `[cols][ColumnsSelectionDsl.cols]` { ... } `[and][ColumnsResolver.and] {@getArg [ColumnsResolverAndDocs.Argument]}` }`
     */
    private interface ColumnsResolverAndDocs {

        interface Argument
    }

    /** @include [ColumnsResolverAndDocs] {@setArg [ColumnsResolverAndDocs.Argument] [colsOf][SingleColumn.colsOf]`<`[Int][Int]`>()} */
    public infix fun <C> ColumnsResolver<C>.and(other: ColumnsResolver<C>): ColumnSet<C> = ColumnsList(this, other)

    /** @include [ColumnsResolverAndDocs] {@setArg [ColumnsResolverAndDocs.Argument] `{ colA `[/][DataColumn.div]` 2.0 `[named][ColumnReference.named]` "half colA" } `} */
    public infix fun <C> ColumnsResolver<C>.and(other: () -> ColumnsResolver<C>): ColumnSet<C> = this and other()

    /** @include [ColumnsResolverAndDocs] {@setArg [ColumnsResolverAndDocs.Argument] "colB"} */
    public infix fun <C> ColumnsResolver<C>.and(other: String): ColumnSet<*> = this and other.toColumnAccessor()

    /** @include [ColumnsResolverAndDocs] {@setArg [ColumnsResolverAndDocs.Argument] Type::colB} */
    public infix fun <C> ColumnsResolver<C>.and(other: KProperty<C>): ColumnSet<C> = this and other.toColumnAccessor()

    // endregion

    // region String

    /**
     * @include [CommonAndDocs]
     * @setArg [CommonAndDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { "colA" `[and][String.and] {@getArg [StringAndDocs.Argument]}` }`
     */
    private interface StringAndDocs {

        interface Argument
    }

    /** @include [StringAndDocs] {@setArg [StringAndDocs.Argument] [colsOf][SingleColumn.colsOf]`<`[Int][Int]`>()} */
    public infix fun <C> String.and(other: ColumnsResolver<C>): ColumnSet<*> = toColumnAccessor() and other

    /** @include [StringAndDocs] {@setArg [StringAndDocs.Argument] `{ colA `[/][DataColumn.div]` 2.0 `[named][ColumnReference.named]` "half colA" } `} */
    public infix fun <C> String.and(other: () -> ColumnsResolver<C>): ColumnSet<*> = toColumnAccessor() and other()

    /** @include [StringAndDocs] {@setArg [StringAndDocs.Argument] "colB"} */
    public infix fun String.and(other: String): ColumnSet<*> = toColumnAccessor() and other.toColumnAccessor()

    /** @include [StringAndDocs] {@setArg [StringAndDocs.Argument] Type::colB} */
    public infix fun <C> String.and(other: KProperty<C>): ColumnSet<*> = toColumnAccessor() and other

    // endregion

    // region KProperty

    /**
     * @include [CommonAndDocs]
     * @setArg [CommonAndDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { Type::colA `[and][KProperty.and] {@getArg [KPropertyAndDocs.Argument]}` }`
     */
    private interface KPropertyAndDocs {

        interface Argument
    }

    /** @include [KPropertyAndDocs] {@setArg [KPropertyAndDocs.Argument] [colsOf][SingleColumn.colsOf]`<`[Int][Int]`>()} */
    public infix fun <C> KProperty<C>.and(other: ColumnsResolver<C>): ColumnSet<C> = toColumnAccessor() and other

    /** @include [KPropertyAndDocs] {@setArg [KPropertyAndDocs.Argument] `{ colA `[/][DataColumn.div]` 2.0 `[named][ColumnReference.named]` "half colA" } `} */
    public infix fun <C> KProperty<C>.and(other: () -> ColumnsResolver<C>): ColumnSet<C> =
        toColumnAccessor() and other()

    /** @include [KPropertyAndDocs] {@setArg [KPropertyAndDocs.Argument] "colB"} */
    public infix fun <C> KProperty<C>.and(other: String): ColumnSet<*> = toColumnAccessor() and other

    /** @include [KPropertyAndDocs] {@setArg [KPropertyAndDocs.Argument] Type::colB} */
    public infix fun <C> KProperty<C>.and(other: KProperty<C>): ColumnSet<C> =
        toColumnAccessor() and other.toColumnAccessor()

    // endregion
}

// endregion
