package com.example.itzmeanjan.weatherz

import androidx.room.*

@Entity(tableName = "cityNames")
data class CityNames(@PrimaryKey var cityId: String,
                     @ColumnInfo(name = "cityName") var cityName: String,
                     @ColumnInfo(name = "countryCode") var countryCode: String,
                     @ColumnInfo(name = "lon") var lon: String,
                     @ColumnInfo(name = "lat") var lat: String)

@Dao
interface CityNamesDao {
    @Insert
    fun insertData(vararg cityNames: CityNames)

    @Query("select * from cityNames")
    fun getCityNames(): List<CityNames>

    @Query("select * from cityNames where cityId = :cityId")
    fun getCityById(cityId: String): CityNames

    @Query("select * from cityNames where cityName like :cityName")
    fun getCityByName(cityName: String): List<CityNames>

    @Query("select * from cityNames where countryCode = :countryCode")
    fun getCityByCountryCode(countryCode: String): List<CityNames>
}

@Database(entities = [CityNames::class], version = 1)
abstract class CityNamesDataBase: RoomDatabase(){
    abstract fun getCityNamesDao(): CityNamesDao
}
