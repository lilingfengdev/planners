package com.bh.planners.core.storage

import taboolib.common.io.newFile
import taboolib.common.platform.function.getDataFolder
import taboolib.module.database.ColumnTypeSQLite
import taboolib.module.database.HostSQLite
import taboolib.module.database.Table

class StorageSqlite : StorageSQL() {

    val file = newFile(getDataFolder(), "data.db", create = true)

    val sqliteHost = HostSQLite(file)

    override val userTable = Table("planners_user", sqliteHost) {
        add("id") { id() }
        add(UUID) { type(ColumnTypeSQLite.TEXT, 36) }
        add(MANA) {
            type(ColumnTypeSQLite.REAL, 20) {
                def(0.0)
            }
        }
        add(JOB) { type(ColumnTypeSQLite.INTEGER) }
        add(DATA) {
            type(ColumnTypeSQLite.TEXT)
        }
    }

    override val jobTable = Table("planners_job", sqliteHost) {
        add("id") { id() }
        add(USER) { type(ColumnTypeSQLite.INTEGER, 10) }
        add(JOB) { type(ColumnTypeSQLite.TEXT, 30) }
        add(POINT) {
            type(ColumnTypeSQLite.INTEGER) {
                def(0)
            }
        }
        add(LEVEL) {
            type(ColumnTypeSQLite.INTEGER, 30) {
                def(1)
            }
        }
        add(EXPERIENCE) {
            type(ColumnTypeSQLite.INTEGER) {
                def(0)
            }
        }
    }

    override val skillTable = Table("planners_skill", sqliteHost) {
        add("id") { id() }
        add(USER) { type(ColumnTypeSQLite.INTEGER, 10) }
        add(JOB) { type(ColumnTypeSQLite.TEXT, 30) }
        add(SKILL) { type(ColumnTypeSQLite.TEXT, 30) }
        add(SHORTCUT_KEY) { type(ColumnTypeSQLite.TEXT, 30) }
        add(LEVEL) {
            type(ColumnTypeSQLite.INTEGER, 10) {
                def(0)
            }
        }
    }

    init {
        userTable.createTable(dataSource)
        jobTable.createTable(dataSource)
        skillTable.createTable(dataSource)
    }


}