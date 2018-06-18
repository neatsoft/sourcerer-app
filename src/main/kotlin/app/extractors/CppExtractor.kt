// Copyright 2017 Sourcerer Inc. All Rights Reserved.
// Author: Liubov Yaronskaya (lyaronskaya@sourcerer.io)
// Author: Anatoly Kislov (anatoly@sourcerer.io)

package app.extractors

import app.model.CommitStats
import app.model.DiffFile

class CppExtractor : ExtractorInterface {
    companion object {
        const val LANGUAGE_NAME = Lang.CPP
        val importRegex = Regex("""^([^\n]*#include)\s[^\n]*""")
        val commentRegex = Regex("""^([^\n]*//)[^\n]*""")
        val extractImportRegex = Regex("""#include\s+["<](\w+)[/\w+]*(\.\w+)?[">]""")
    }

    override fun extract(files: List<DiffFile>): List<CommitStats> {
        files.map { file -> file.language = LANGUAGE_NAME }
        return super.extract(files)
    }

    override fun extractImports(fileContent: List<String>): List<String> {
        val imports = mutableSetOf<String>()

        fileContent.forEach {
            val res = extractImportRegex.find(it)
            if (res != null) {
                val lineLib = res.groupValues
                                 .last { !it.startsWith(".") && it != ""}
                imports.add(lineLib)
            }
        }
        return imports.toSet().toList()
    }

    override fun tokenize(line: String): List<String> {
        var newLine = importRegex.replace(line, "")
        newLine = commentRegex.replace(newLine, "")
        return super.tokenize(newLine)
    }

    override fun getLineLibraries(line: String,
                                  fileLibraries: List<String>): List<String> {
        return super.getLineLibraries(line, fileLibraries, LANGUAGE_NAME)
    }
}
