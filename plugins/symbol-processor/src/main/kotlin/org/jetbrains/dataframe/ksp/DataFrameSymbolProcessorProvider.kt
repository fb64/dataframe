package org.jetbrains.dataframe.ksp

import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider

class DataFrameSymbolProcessorProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        val resolutionDir = environment.options["dataframe.resolutionDir"]
        return DataFrameSymbolProcessor(environment.codeGenerator, environment.logger, resolutionDir)
    }
}
