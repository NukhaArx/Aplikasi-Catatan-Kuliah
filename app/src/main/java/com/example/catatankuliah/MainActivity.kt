package com.example.catatankuliah

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var db: DatabaseHelper
    private lateinit var tvRingkasan: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        db = DatabaseHelper(this)
        tvRingkasan = findViewById(R.id.tvRingkasan)

        findViewById<Button>(R.id.btnTugas).setOnClickListener {
            startActivity(Intent(this, TugasActivity::class.java))
        }
        findViewById<Button>(R.id.btnJadwal).setOnClickListener {
            startActivity(Intent(this, JadwalActivity::class.java))
        }
    }

    // refresh ringkasan setiap kembali dari layar lain
    override fun onResume() {
        super.onResume()
        tvRingkasan.text = "Tugas belum selesai: ${db.countTugasBelumSelesai()}\n" +
                "Jadwal kuliah tersimpan: ${db.countJadwal()}"
    }
}
