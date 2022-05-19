package com.bh.planners.util

import taboolib.common.platform.function.getDataFolder
import taboolib.common.platform.function.releaseResourceFile
import java.io.File


fun files(path: String, defs: List<String>, callback: (File) -> Unit) {
    defs.forEach {
        releaseResourceFile("$path/$it")
    }
    getFiles(File(getDataFolder(), path)).forEach {
        callback(it)
    }
}

fun getFiles(file: File): List<File> {
    val listOf = mutableListOf<File>()
    when (file.isDirectory) {
        true -> listOf += file.listFiles().flatMap { getFiles(it) }
        false -> {
            if (file.name.endsWith(".yml")) {
                listOf += file
            }
        }
    }
    return listOf
}
