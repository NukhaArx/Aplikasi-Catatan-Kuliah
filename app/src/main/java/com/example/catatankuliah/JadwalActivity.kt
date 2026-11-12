package com.example.catatankuliah

import android.app.TimePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.Locale

class JadwalActivity : AppCompatActivity() {

    private lateinit var db: DatabaseHelper
    private lateinit var adapter: JadwalAdapter
    private lateinit var tvKosong: TextView

    private val daftarHari = listOf("Senin", "Selasa", "Rabu", "Kamis", "Jumat", "Sabtu")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_jadwal)

        db = DatabaseHelper(this)
        tvKosong = findViewById(R.id.tvKosong)

        adapter = JadwalAdapter(
            mutableListOf(),
            onEdit = { showForm(it) },
            onDelete = { konfirmasiHapus(it) }
        )
        val rv = findViewById<RecyclerView>(R.id.rvJadwal)
        rv.layoutManager = LinearLayoutManager(this)
        rv.adapter = adapter

        findViewById<FloatingActionButton>(R.id.fabTambah).setOnClickListener { showForm(null) }
        muatData()
    }

    private fun muatData() {
        val list = db.getAllJadwal()
        adapter.setData(list)
        tvKosong.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
    }

    private fun konfirmasiHapus(j: Jadwal) {
        AlertDialog.Builder(this)
            .setTitle("Hapus jadwal?")
            .setMessage("Jadwal ${j.mataKuliah} (${j.hari}) akan dihapus.")
            .setPositiveButton("Hapus") { _, _ ->
                db.deleteJadwal(j.id)
                muatData()
                Toast.makeText(this, "Jadwal dihapus", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun pilihJam(target: EditText) {
        val parts = target.text.toString().split(":")
        val jam = parts.getOrNull(0)?.toIntOrNull() ?: 8
        val menit = parts.getOrNull(1)?.toIntOrNull() ?: 0
        TimePickerDialog(
            this,
            { _, h, m -> target.setText(String.format(Locale.US, "%02d:%02d", h, m)) },
            jam, menit, true
        ).show()
    }

    private fun showForm(j: Jadwal?) {
        val view = layoutInflater.inflate(R.layout.dialog_jadwal, null)
        val etMatkul = view.findViewById<EditText>(R.id.etMatkul)
        val spHari = view.findViewById<Spinner>(R.id.spHari)
        val etMulai = view.findViewById<EditText>(R.id.etJamMulai)
        val etSelesai = view.findViewById<EditText>(R.id.etJamSelesai)
        val etRuangan = view.findViewById<EditText>(R.id.etRuangan)
        val etDosen = view.findViewById<EditText>(R.id.etDosen)

        spHari.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, daftarHari)

        if (j != null) {
            etMatkul.setText(j.mataKuliah)
            spHari.setSelection(daftarHari.indexOf(j.hari).coerceAtLeast(0))
            etMulai.setText(j.jamMulai)
            etSelesai.setText(j.jamSelesai)
            etRuangan.setText(j.ruangan)
            etDosen.setText(j.dosen)
        }

        etMulai.setOnClickListener { pilihJam(etMulai) }
        etSelesai.setOnClickListener { pilihJam(etSelesai) }

        val dialog = AlertDialog.Builder(this)
            .setTitle(if (j == null) "Tambah Jadwal" else "Edit Jadwal")
            .setView(view)
            .setPositiveButton("Simpan", null)
            .setNegativeButton("Batal", null)
            .create()

        dialog.setOnShowListener {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                val matkul = etMatkul.text.toString().trim()
                val hari = spHari.selectedItem.toString()
                val mulai = etMulai.text.toString()
                val selesai = etSelesai.text.toString()
                val ruangan = etRuangan.text.toString().trim()
                val dosen = etDosen.text.toString().trim()

                if (matkul.isEmpty()) { etMatkul.error = "Mata kuliah wajib diisi"; return@setOnClickListener }
                if (mulai.isEmpty()) { etMulai.error = "Pilih jam mulai"; return@setOnClickListener }
                if (selesai.isEmpty()) { etSelesai.error = "Pilih jam selesai"; return@setOnClickListener }
                if (selesai <= mulai) { etSelesai.error = "Jam selesai harus setelah jam mulai"; return@setOnClickListener }

                if (j == null) {
                    db.insertJadwal(Jadwal(mataKuliah = matkul, hari = hari, jamMulai = mulai, jamSelesai = selesai, ruangan = ruangan, dosen = dosen))
                    Toast.makeText(this, "Jadwal ditambahkan", Toast.LENGTH_SHORT).show()
                } else {
                    db.updateJadwal(j.copy(mataKuliah = matkul, hari = hari, jamMulai = mulai, jamSelesai = selesai, ruangan = ruangan, dosen = dosen))
                    Toast.makeText(this, "Jadwal diperbarui", Toast.LENGTH_SHORT).show()
                }
                muatData()
                dialog.dismiss()
            }
        }
        dialog.show()
    }
}
