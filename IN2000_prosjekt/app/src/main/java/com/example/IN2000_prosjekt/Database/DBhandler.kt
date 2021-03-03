package com.example.IN2000_prosjekt.Database


import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import androidx.core.content.contentValuesOf

const val DATABASE_NAME = "DATABASE"
const val TABLE_NAME = "Reiser"
const val COL_NAVN = "navn"
const val COL_Lat = "Lat"
const val COL_Lon = "Lon"

const val TABLE_NAME2 = "beskrivelser"
const val COL_NAVN2 = "navn"
const val COL_BESKRIVELSE = "rutebeskrivelse"



//https://www.youtube.com/watch?v=QjICgmk31js denne ble brukt som hjelp for å lage denne klassen
class DBhandler(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, 1) {
    //lager databasen med 2 skjemaer
    override fun onCreate(db: SQLiteDatabase?) {
        val createTable = "CREATE TABLE " + TABLE_NAME + " (" +
                COL_NAVN + " VARCHAR(256)," +
                COL_Lat + " text," +
                COL_Lon + " text )"

        db?.execSQL(createTable)
        val createTable2 = "CREATE TABLE " + TABLE_NAME2 + " (" +
                COL_NAVN2 + " VARCHAR(256)," +
                COL_BESKRIVELSE + " VARCHAR(256))"

        db?.execSQL(createTable2)
    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {
        return
    }

    //lagrer rutene
    fun insertData(point: Punkt) {

        val db = this.writableDatabase
        val cv = contentValuesOf()

        cv.put(COL_NAVN, point.navn)
        cv.put(COL_Lat, point.lat.toString())
        cv.put(COL_Lon, point.long.toString())
        val result = db.insert(TABLE_NAME, null, cv)
        if (result == (-1).toLong()) {
            print("\n\n\n\n Lagring feilet \n\n\n")
        } else {
            print("\n\n\n Lagering vellykket \n\n\n")
        }
        db.close()


    }

    //henter en hel rute basert på navnet
    fun readData(navnpaaRuta: String): MutableList<Punkt> {
        val list: MutableList<Punkt> = ArrayList()

        val db = this.readableDatabase
        val query = "Select * from $TABLE_NAME where $COL_NAVN like '$navnpaaRuta'"
        val result = db.rawQuery(query, null)

        if (result.moveToFirst()) {
            do {
                val user = Punkt()
                user.navn = result.getString(result.getColumnIndex(COL_NAVN))
                print(result.getString(result.getColumnIndex(COL_Lon)))
                result.getString(result.getColumnIndex(COL_Lat))
                user.long = result.getString(result.getColumnIndex(COL_Lon)).toDouble()
                user.lat = result.getString(result.getColumnIndex(COL_Lat)).toDouble()
                list.add(user)
            } while (result.moveToNext())
        }

        result.close()
        db.close()
        return list
    }

    //henter navn til vær unike rute
    fun readDistinct(): MutableList<String>{
        val list: MutableList<String> = ArrayList()

        val db = this.readableDatabase
        val query = "Select DISTINCT $COL_NAVN from $TABLE_NAME "
        val result = db.rawQuery(query, null)

        if (result.moveToFirst()) {
            do {
                val navn = result.getString(result.getColumnIndex(COL_NAVN))
                list.add(navn)
            } while (result.moveToNext())
        }

        result.close()
        db.close()
        return list

    }

    //setter beskrivelsene til hver
    fun insertBeskrivelse(navnpaaRuta: String,beskrivelse:String){
        val db = this.writableDatabase
        val cv = contentValuesOf()

        cv.put(COL_NAVN2, navnpaaRuta)
        cv.put(COL_BESKRIVELSE, beskrivelse)
        val result = db.insert(TABLE_NAME2, null, cv)
        if (result == (-1).toLong()) {
            print("\n\n\n\n Lagring feilet \n\n\n")
        } else {
            print("\n\n\n Lagering vellykket \n\n\n")
        }
        db.close()
    }

    //henter beskrivelsene til rutene.

    fun readDesc(navn:String): String{

        val db = this.readableDatabase
        val query = "Select $COL_BESKRIVELSE from $TABLE_NAME2 where $COL_NAVN2 like '$navn' "
        val result = db.rawQuery(query, null)
        var navnn = ""
        if (result.moveToFirst()) {
            navnn = result.getString(result.getColumnIndex(COL_BESKRIVELSE))
        }

        result.close()
        db.close()
        return navnn

    }
    //sletter rutebeskrvielsene og punktene som tilhører ett navn

    fun reomoveName(navn: String){

        print("\n\n\n\n")
        print("før sletting: ")
        print(readData(navn).size)
        print("\n\n\n\n")
        val db = this.readableDatabase
        val query = "DELETE FROM $TABLE_NAME WHERE $COL_NAVN IS '$navn'"
        val query1 = "DELETE FROM $TABLE_NAME2 WHERE $COL_NAVN2 IS '$navn'"
        db.execSQL(query)
        db.execSQL(query1)
        db.close()

    }



}