package com.example.catatankuliah

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {

    companion object {
        const val DB_NAME = "kuliah.db"
        const val DB_VERSION = 1

        const val T_TUGAS = "tugas"
        const val T_JADWAL = "jadwal"
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """CREATE TABLE $T_TUGAS (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                judul TEXT NOT NULL,
                mata_kuliah TEXT NOT NULL,
                deskripsi TEXT,
                deadline TEXT NOT NULL,
                selesai INTEGER NOT NULL DEFAULT 0
            )"""
        )
        db.execSQL(
            """CREATE TABLE $T_JADWAL (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                mata_kuliah TEXT NOT NULL,
                hari TEXT NOT NULL,
                jam_mulai TEXT NOT NULL,
                jam_selesai TEXT NOT NULL,
                ruangan TEXT,
                dosen TEXT
            )"""
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $T_TUGAS")
        db.execSQL("DROP TABLE IF EXISTS $T_JADWAL")
        onCreate(db)
    }

    // ===================== TUGAS =====================

    fun insertTugas(t: Tugas): Long {
        val cv = ContentValues().apply {
            put("judul", t.judul)
            put("mata_kuliah", t.mataKuliah)
            put("deskripsi", t.deskripsi)
            put("deadline", t.deadline)
            put("selesai", if (t.selesai) 1 else 0)
        }
        return writableDatabase.insert(T_TUGAS, null, cv)
    }

    fun getAllTugas(): MutableList<Tugas> {
        val list = mutableListOf<Tugas>()
        // belum selesai di atas, lalu urut deadline terdekat
        val c = readableDatabase.rawQuery(
            "SELECT id, judul, mata_kuliah, deskripsi, deadline, selesai " +
                    "FROM $T_TUGAS ORDER BY selesai ASC, deadline ASC", null
        )
        c.use {
            while (it.moveToNext()) {
                list.add(
                    Tugas(
                        id = it.getInt(0),
                        judul = it.getString(1),
                        mataKuliah = it.getString(2),
                        deskripsi = it.getString(3) ?: "",
                        deadline = it.getString(4),
                        selesai = it.getInt(5) == 1
                    )
                )
            }
        }
        return list
    }

    fun updateTugas(t: Tugas): Int {
        val cv = ContentValues().apply {
            put("judul", t.judul)
            put("mata_kuliah", t.mataKuliah)
            put("deskripsi", t.deskripsi)
            put("deadline", t.deadline)
        }
        return writableDatabase.update(T_TUGAS, cv, "id = ?", arrayOf(t.id.toString()))
    }

    fun setSelesai(id: Int, selesai: Boolean): Int {
        val cv = ContentValues().apply { put("selesai", if (selesai) 1 else 0) }
        return writableDatabase.update(T_TUGAS, cv, "id = ?", arrayOf(id.toString()))
    }

    fun deleteTugas(id: Int): Int =
        writableDatabase.delete(T_TUGAS, "id = ?", arrayOf(id.toString()))

    fun countTugasBelumSelesai(): Int {
        val c = readableDatabase.rawQuery(
            "SELECT COUNT(*) FROM $T_TUGAS WHERE selesai = 0", null
        )
        c.use { return if (it.moveToFirst()) it.getInt(0) else 0 }
    }

    // ===================== JADWAL =====================

    fun insertJadwal(j: Jadwal): Long {
        val cv = ContentValues().apply {
            put("mata_kuliah", j.mataKuliah)
            put("hari", j.hari)
            put("jam_mulai", j.jamMulai)
            put("jam_selesai", j.jamSelesai)
            put("ruangan", j.ruangan)
            put("dosen", j.dosen)
        }
        return writableDatabase.insert(T_JADWAL, null, cv)
    }

    fun getAllJadwal(): MutableList<Jadwal> {
        val list = mutableListOf<Jadwal>()
        val c = readableDatabase.rawQuery(
            """SELECT id, mata_kuliah, hari, jam_mulai, jam_selesai, ruangan, dosen
               FROM $T_JADWAL
               ORDER BY CASE hari
                   WHEN 'Senin' THEN 1 WHEN 'Selasa' THEN 2 WHEN 'Rabu' THEN 3
                   WHEN 'Kamis' THEN 4 WHEN 'Jumat' THEN 5 WHEN 'Sabtu' THEN 6
                   ELSE 7 END, jam_mulai ASC""", null
        )
        c.use {
            while (it.moveToNext()) {
                list.add(
                    Jadwal(
                        id = it.getInt(0),
                        mataKuliah = it.getString(1),
                        hari = it.getString(2),
                        jamMulai = it.getString(3),
                        jamSelesai = it.getString(4),
                        ruangan = it.getString(5) ?: "",
                        dosen = it.getString(6) ?: ""
                    )
                )
            }
        }
        return list
    }

    fun updateJadwal(j: Jadwal): Int {
        val cv = ContentValues().apply {
            put("mata_kuliah", j.mataKuliah)
            put("hari", j.hari)
            put("jam_mulai", j.jamMulai)
            put("jam_selesai", j.jamSelesai)
            put("ruangan", j.ruangan)
            put("dosen", j.dosen)
        }
        return writableDatabase.update(T_JADWAL, cv, "id = ?", arrayOf(j.id.toString()))
    }

    fun deleteJadwal(id: Int): Int =
        writableDatabase.delete(T_JADWAL, "id = ?", arrayOf(id.toString()))

    fun countJadwal(): Int {
        val c = readableDatabase.rawQuery("SELECT COUNT(*) FROM $T_JADWAL", null)
        c.use { return if (it.moveToFirst()) it.getInt(0) else 0 }
    }
}
