package com.example.catatankuliah

import java.text.SimpleDateFormat
import java.util.Locale

data class Tugas(
    val id: Int = 0,
    val judul: String,
    val mataKuliah: String,
    val deskripsi: String,
    val deadline: String, // format yyyy-MM-dd
    val selesai: Boolean = false
)

data class Jadwal(
    val id: Int = 0,
    val mataKuliah: String,
    val hari: String,
    val jamMulai: String,   // format HH:mm
    val jamSelesai: String, // format HH:mm
    val ruangan: String,
    val dosen: String
)

// Ubah "2026-11-20" menjadi "20 Nov 2026"
fun formatTanggal(iso: String): String = try {
    val tgl = SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(iso)!!
    SimpleDateFormat("dd MMM yyyy", Locale("id", "ID")).format(tgl)
} catch (e: Exception) {
    iso
}
