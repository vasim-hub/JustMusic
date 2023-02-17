/*
Copyright 2022 Vasim Mansuri

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */
package com.justmusic.cache.utils.base

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Update
import java.util.Date


/**
 * Base Entity Dao for creating and managing common operation for Insert, Update and Delete operation for
 * local DB
 */
@Suppress("unused")
@Dao
abstract class AbstractBaseEntityDao<T : BaseEntity?> {
    @Insert
    abstract fun actualInsert(t: T): Long

    fun insert(t: T): Long {
        t!!.createdAt = Date()
        t.updatedAt = Date()
        return actualInsert(t)
    }

    @Insert
    abstract fun actualInsertAll(ts: List<T>?): List<Long>

    fun insertAll(ts: List<T>?): List<Long> {
        if (ts != null) {
            for (t in ts) {
                t?.createdAt = Date()
                t?.updatedAt = Date()
            }
        }
        return actualInsertAll(ts)
    }

    @Update
    abstract fun actualUpdate(t: T)

    fun update(t: T) {
        t!!.updatedAt = Date()
        actualUpdate(t)
    }

    @Update
    abstract fun actualUpdateAll(ts: List<T>?)

    fun updateAll(ts: List<T>?) {
        if (ts != null) {
            for (t in ts) {
                t!!.updatedAt = Date()
            }
        }
        actualUpdateAll(ts)
    }

    @Delete
    abstract fun delete(t: T)

    @Delete
    abstract fun deleteAll(ts: List<T>?)
}