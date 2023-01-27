package com.example.stepcountpoc.database

import androidx.room.*

@Dao
interface StepsInfoDao {
    @Query("SELECT * FROM step_count_details")
    fun getAll(): List<StepsInfo>

   /* @Query("SELECT * FROM step_count_details WHERE uid IN (:userIds)")
    fun loadAllByIds(userIds: IntArray): List<Student>*/

//    @Query("SELECT * FROM step_count_details WHERE roll_no LIKE :roll LIMIT 1")
//    suspend fun findByRoll(roll: Int): StepsInfo

    @Insert(onConflict = OnConflictStrategy.IGNORE)
     fun insert(stepsInfo: StepsInfo)

    @Delete
     fun delete(stepsInfo: StepsInfo)

    @Query("DELETE FROM step_count_details")
     fun deleteAll()
}