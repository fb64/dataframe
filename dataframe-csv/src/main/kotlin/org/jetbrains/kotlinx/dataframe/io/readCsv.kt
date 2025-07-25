@file:JvmName("ReadCsvDeephavenKt")

package org.jetbrains.kotlinx.dataframe.io

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.ParserOptions
import org.jetbrains.kotlinx.dataframe.documentationCsv.CommonReadDelimDocs
import org.jetbrains.kotlinx.dataframe.documentationCsv.DelimParams
import org.jetbrains.kotlinx.dataframe.documentationCsv.DelimParams.ADJUST_CSV_SPECS
import org.jetbrains.kotlinx.dataframe.documentationCsv.DelimParams.ALLOW_MISSING_COLUMNS
import org.jetbrains.kotlinx.dataframe.documentationCsv.DelimParams.COL_TYPES
import org.jetbrains.kotlinx.dataframe.documentationCsv.DelimParams.COMPRESSION
import org.jetbrains.kotlinx.dataframe.documentationCsv.DelimParams.CSV_DELIMITER
import org.jetbrains.kotlinx.dataframe.documentationCsv.DelimParams.FILE_OR_URL_READ
import org.jetbrains.kotlinx.dataframe.documentationCsv.DelimParams.FILE_READ
import org.jetbrains.kotlinx.dataframe.documentationCsv.DelimParams.FIXED_COLUMN_WIDTHS
import org.jetbrains.kotlinx.dataframe.documentationCsv.DelimParams.HAS_FIXED_WIDTH_COLUMNS
import org.jetbrains.kotlinx.dataframe.documentationCsv.DelimParams.HEADER
import org.jetbrains.kotlinx.dataframe.documentationCsv.DelimParams.IGNORE_EMPTY_LINES
import org.jetbrains.kotlinx.dataframe.documentationCsv.DelimParams.IGNORE_EXCESS_COLUMNS
import org.jetbrains.kotlinx.dataframe.documentationCsv.DelimParams.IGNORE_SURROUNDING_SPACES
import org.jetbrains.kotlinx.dataframe.documentationCsv.DelimParams.INPUT_STREAM_READ
import org.jetbrains.kotlinx.dataframe.documentationCsv.DelimParams.PARSER_OPTIONS
import org.jetbrains.kotlinx.dataframe.documentationCsv.DelimParams.PARSE_PARALLEL
import org.jetbrains.kotlinx.dataframe.documentationCsv.DelimParams.PATH_READ
import org.jetbrains.kotlinx.dataframe.documentationCsv.DelimParams.QUOTE
import org.jetbrains.kotlinx.dataframe.documentationCsv.DelimParams.READ_LINES
import org.jetbrains.kotlinx.dataframe.documentationCsv.DelimParams.SKIP_LINES
import org.jetbrains.kotlinx.dataframe.documentationCsv.DelimParams.TRIM_INSIDE_QUOTED
import org.jetbrains.kotlinx.dataframe.impl.io.readDelimImpl
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.net.URL
import java.nio.file.Path
import kotlin.io.path.inputStream

/**
 * @include [CommonReadDelimDocs.CsvDocs]
 * @set [CommonReadDelimDocs.DATA_TITLE] File
 * @set [CommonReadDelimDocs.DATA] file
 * @include [PATH_READ]
 * @include [CSV_DELIMITER]
 * @include [COMPRESSION]
 * @include [CommonReadDelimDocs.CommonReadParams]
 */
public fun DataFrame.Companion.readCsv(
    path: Path,
    delimiter: Char = CSV_DELIMITER,
    header: List<String> = HEADER,
    hasFixedWidthColumns: Boolean = HAS_FIXED_WIDTH_COLUMNS,
    fixedColumnWidths: List<Int> = FIXED_COLUMN_WIDTHS,
    colTypes: Map<String, ColType> = COL_TYPES,
    skipLines: Long = SKIP_LINES,
    readLines: Long? = READ_LINES,
    parserOptions: ParserOptions? = PARSER_OPTIONS,
    ignoreEmptyLines: Boolean = IGNORE_EMPTY_LINES,
    allowMissingColumns: Boolean = ALLOW_MISSING_COLUMNS,
    ignoreExcessColumns: Boolean = IGNORE_EXCESS_COLUMNS,
    quote: Char = QUOTE,
    ignoreSurroundingSpaces: Boolean = IGNORE_SURROUNDING_SPACES,
    trimInsideQuoted: Boolean = TRIM_INSIDE_QUOTED,
    parseParallel: Boolean = PARSE_PARALLEL,
    compression: Compression<*> = Compression.of(path),
): DataFrame<*> =
    path.inputStream().use {
        readDelimImpl(
            inputStream = it,
            delimiter = delimiter,
            header = header,
            hasFixedWidthColumns = hasFixedWidthColumns,
            fixedColumnWidths = fixedColumnWidths,
            colTypes = colTypes,
            skipLines = skipLines,
            readLines = readLines,
            parserOptions = parserOptions,
            ignoreEmptyLines = ignoreEmptyLines,
            allowMissingColumns = allowMissingColumns,
            ignoreExcessColumns = ignoreExcessColumns,
            quote = quote,
            ignoreSurroundingSpaces = ignoreSurroundingSpaces,
            trimInsideQuoted = trimInsideQuoted,
            parseParallel = parseParallel,
            compression = compression,
            adjustCsvSpecs = ADJUST_CSV_SPECS,
        )
    }

/**
 * @include [CommonReadDelimDocs.CsvDocs]
 * @set [CommonReadDelimDocs.DATA_TITLE] File
 * @set [CommonReadDelimDocs.DATA] file
 * @include [FILE_READ]
 * @include [CSV_DELIMITER]
 * @include [COMPRESSION]
 * @include [CommonReadDelimDocs.CommonReadParams]
 */
public fun DataFrame.Companion.readCsv(
    file: File,
    delimiter: Char = CSV_DELIMITER,
    header: List<String> = HEADER,
    hasFixedWidthColumns: Boolean = HAS_FIXED_WIDTH_COLUMNS,
    fixedColumnWidths: List<Int> = FIXED_COLUMN_WIDTHS,
    colTypes: Map<String, ColType> = COL_TYPES,
    skipLines: Long = SKIP_LINES,
    readLines: Long? = READ_LINES,
    parserOptions: ParserOptions? = PARSER_OPTIONS,
    ignoreEmptyLines: Boolean = IGNORE_EMPTY_LINES,
    allowMissingColumns: Boolean = ALLOW_MISSING_COLUMNS,
    ignoreExcessColumns: Boolean = IGNORE_EXCESS_COLUMNS,
    quote: Char = QUOTE,
    ignoreSurroundingSpaces: Boolean = IGNORE_SURROUNDING_SPACES,
    trimInsideQuoted: Boolean = TRIM_INSIDE_QUOTED,
    parseParallel: Boolean = PARSE_PARALLEL,
    compression: Compression<*> = Compression.of(file),
): DataFrame<*> =
    FileInputStream(file).use {
        readDelimImpl(
            inputStream = it,
            delimiter = delimiter,
            header = header,
            hasFixedWidthColumns = hasFixedWidthColumns,
            fixedColumnWidths = fixedColumnWidths,
            colTypes = colTypes,
            skipLines = skipLines,
            readLines = readLines,
            parserOptions = parserOptions,
            ignoreEmptyLines = ignoreEmptyLines,
            allowMissingColumns = allowMissingColumns,
            ignoreExcessColumns = ignoreExcessColumns,
            quote = quote,
            ignoreSurroundingSpaces = ignoreSurroundingSpaces,
            trimInsideQuoted = trimInsideQuoted,
            parseParallel = parseParallel,
            compression = compression,
            adjustCsvSpecs = ADJUST_CSV_SPECS,
        )
    }

/**
 * @include [CommonReadDelimDocs.CsvDocs]
 * @set [CommonReadDelimDocs.DATA_TITLE] Url
 * @set [CommonReadDelimDocs.DATA] url
 * @include [DelimParams.URL_READ]
 * @include [CSV_DELIMITER]
 * @include [COMPRESSION]
 * @include [CommonReadDelimDocs.CommonReadParams]
 */
public fun DataFrame.Companion.readCsv(
    url: URL,
    delimiter: Char = CSV_DELIMITER,
    header: List<String> = HEADER,
    hasFixedWidthColumns: Boolean = HAS_FIXED_WIDTH_COLUMNS,
    fixedColumnWidths: List<Int> = FIXED_COLUMN_WIDTHS,
    colTypes: Map<String, ColType> = COL_TYPES,
    skipLines: Long = SKIP_LINES,
    readLines: Long? = READ_LINES,
    parserOptions: ParserOptions? = PARSER_OPTIONS,
    ignoreEmptyLines: Boolean = IGNORE_EMPTY_LINES,
    allowMissingColumns: Boolean = ALLOW_MISSING_COLUMNS,
    ignoreExcessColumns: Boolean = IGNORE_EXCESS_COLUMNS,
    quote: Char = QUOTE,
    ignoreSurroundingSpaces: Boolean = IGNORE_SURROUNDING_SPACES,
    trimInsideQuoted: Boolean = TRIM_INSIDE_QUOTED,
    parseParallel: Boolean = PARSE_PARALLEL,
    compression: Compression<*> = Compression.of(url),
): DataFrame<*> =
    catchHttpResponse(url) {
        readDelimImpl(
            inputStream = it,
            delimiter = delimiter,
            header = header,
            hasFixedWidthColumns = hasFixedWidthColumns,
            fixedColumnWidths = fixedColumnWidths,
            colTypes = colTypes,
            skipLines = skipLines,
            readLines = readLines,
            parserOptions = parserOptions,
            ignoreEmptyLines = ignoreEmptyLines,
            allowMissingColumns = allowMissingColumns,
            ignoreExcessColumns = ignoreExcessColumns,
            quote = quote,
            ignoreSurroundingSpaces = ignoreSurroundingSpaces,
            trimInsideQuoted = trimInsideQuoted,
            parseParallel = parseParallel,
            compression = compression,
            adjustCsvSpecs = ADJUST_CSV_SPECS,
        )
    }

/**
 * @include [CommonReadDelimDocs.CsvDocs]
 * @set [CommonReadDelimDocs.DATA_TITLE] File or URL
 * @set [CommonReadDelimDocs.DATA] file or url
 * @include [FILE_OR_URL_READ]
 * @include [CSV_DELIMITER]
 * @include [COMPRESSION]
 * @include [CommonReadDelimDocs.CommonReadParams]
 */
public fun DataFrame.Companion.readCsv(
    fileOrUrl: String,
    delimiter: Char = CSV_DELIMITER,
    header: List<String> = HEADER,
    hasFixedWidthColumns: Boolean = HAS_FIXED_WIDTH_COLUMNS,
    fixedColumnWidths: List<Int> = FIXED_COLUMN_WIDTHS,
    colTypes: Map<String, ColType> = COL_TYPES,
    skipLines: Long = SKIP_LINES,
    readLines: Long? = READ_LINES,
    parserOptions: ParserOptions? = PARSER_OPTIONS,
    ignoreEmptyLines: Boolean = IGNORE_EMPTY_LINES,
    allowMissingColumns: Boolean = ALLOW_MISSING_COLUMNS,
    ignoreExcessColumns: Boolean = IGNORE_EXCESS_COLUMNS,
    quote: Char = QUOTE,
    ignoreSurroundingSpaces: Boolean = IGNORE_SURROUNDING_SPACES,
    trimInsideQuoted: Boolean = TRIM_INSIDE_QUOTED,
    parseParallel: Boolean = PARSE_PARALLEL,
    compression: Compression<*> = Compression.of(fileOrUrl),
): DataFrame<*> =
    catchHttpResponse(asUrl(fileOrUrl = fileOrUrl)) {
        readDelimImpl(
            inputStream = it,
            delimiter = delimiter,
            header = header,
            hasFixedWidthColumns = hasFixedWidthColumns,
            fixedColumnWidths = fixedColumnWidths,
            colTypes = colTypes,
            skipLines = skipLines,
            readLines = readLines,
            parserOptions = parserOptions,
            ignoreEmptyLines = ignoreEmptyLines,
            allowMissingColumns = allowMissingColumns,
            ignoreExcessColumns = ignoreExcessColumns,
            quote = quote,
            ignoreSurroundingSpaces = ignoreSurroundingSpaces,
            trimInsideQuoted = trimInsideQuoted,
            parseParallel = parseParallel,
            compression = compression,
            adjustCsvSpecs = ADJUST_CSV_SPECS,
        )
    }

/**
 * {@comment the only one with adjustCsvSpecs}
 * @include [CommonReadDelimDocs.CsvDocs]
 * @set [CommonReadDelimDocs.DATA_TITLE] InputStream
 * @set [CommonReadDelimDocs.DATA] input stream
 * @include [INPUT_STREAM_READ]
 * @include [CSV_DELIMITER]
 * @include [COMPRESSION]
 * @include [CommonReadDelimDocs.CommonReadParams]
 * @include [ADJUST_CSV_SPECS]
 */
public fun DataFrame.Companion.readCsv(
    inputStream: InputStream,
    delimiter: Char = CSV_DELIMITER,
    header: List<String> = HEADER,
    hasFixedWidthColumns: Boolean = HAS_FIXED_WIDTH_COLUMNS,
    fixedColumnWidths: List<Int> = FIXED_COLUMN_WIDTHS,
    colTypes: Map<String, ColType> = COL_TYPES,
    skipLines: Long = SKIP_LINES,
    readLines: Long? = READ_LINES,
    parserOptions: ParserOptions? = PARSER_OPTIONS,
    ignoreEmptyLines: Boolean = IGNORE_EMPTY_LINES,
    allowMissingColumns: Boolean = ALLOW_MISSING_COLUMNS,
    ignoreExcessColumns: Boolean = IGNORE_EXCESS_COLUMNS,
    quote: Char = QUOTE,
    ignoreSurroundingSpaces: Boolean = IGNORE_SURROUNDING_SPACES,
    trimInsideQuoted: Boolean = TRIM_INSIDE_QUOTED,
    parseParallel: Boolean = PARSE_PARALLEL,
    compression: Compression<*> = COMPRESSION,
    adjustCsvSpecs: AdjustCsvSpecs = ADJUST_CSV_SPECS,
): DataFrame<*> =
    readDelimImpl(
        inputStream = inputStream,
        delimiter = delimiter,
        header = header,
        hasFixedWidthColumns = hasFixedWidthColumns,
        fixedColumnWidths = fixedColumnWidths,
        colTypes = colTypes,
        skipLines = skipLines,
        readLines = readLines,
        parserOptions = parserOptions,
        ignoreEmptyLines = ignoreEmptyLines,
        allowMissingColumns = allowMissingColumns,
        ignoreExcessColumns = ignoreExcessColumns,
        quote = quote,
        ignoreSurroundingSpaces = ignoreSurroundingSpaces,
        trimInsideQuoted = trimInsideQuoted,
        parseParallel = parseParallel,
        compression = compression,
        adjustCsvSpecs = adjustCsvSpecs,
    )
