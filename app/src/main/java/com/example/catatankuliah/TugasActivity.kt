package com.example.catatankuliah

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class TugasActivity : AppCompatActivity() {

    private lateinit var db: DatabaseHelper
    private lateinit var adapter: TugasAdapter
    private lateinit var tvKosong: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tugas)

        db = DatabaseHelper(this)
        tvKosong = findViewById(R.id.tvKosong)

        adapter = TugasAdapter(
            mutableListOf(),
            onToggle = { t, selesai -> db.setSelesai(t.id, selesai); muatData() },
            onEdit = { showForm(it) },
            onDelete = { konfirmasiHapus(it) }
        )
        val rv = findViewById<RecyclerView>(R.id.rvTugas)
        rv.layoutManager = LinearLayoutManager(this)
        rv.adapter = adapter

        findViewById<FloatingActionButton>(R.id.fabTambah).setOnClickListener { showForm(null) }
        muatData()
    }

    private fun muatData() {
        val list = db.getAllTugas()
        adapter.setData(list)
        tvKosong.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
    }

    private fun konfirmasiHapus(t: Tugas) {
        AlertDialog.Builder(this)
            .setTitle("Hapus tugas?")
            .setMessage("\"${t.judul}\" akan dihapus permanen.")
            .setPositiveButton("Hapus") { _, _ ->
                db.deleteTugas(t.id)
                muatData()
                Toast.makeText(this, "Tugas dihapus", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    // t == null -> tambah baru, t != null -> edit
    private fun showForm(t: Tugas?) {
        val view = layoutInflater.inflate(R.layout.dialog_tugas, null)
        val etJudul = view.findViewById<EditText>(R.id.etJudul)
        val etMatkul = view.findViewById<EditText>(R.id.etMatkul)
        val etDeskripsi = view.findViewById<EditText>(R.id.etDeskripsi)
        val etDeadline = view.findViewById<EditText>(R.id.etDeadline)

        var deadline = t?.deadline ?: ""
        if (t != null) {
            etJudul.setText(t.judul)
            etMatkul.setText(t.mataKuliah)
            etDeskripsi.setText(t.deskripsi)
            etDeadline.setText(formatTanggal(t.deadline))
        }

        etDeadline.setOnClickListener {
            val cal = Calendar.getInstance()
            if (deadline.isNotEmpty()) {
                try {
                    cal.time = SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(deadline)!!
                } catch (_: Exception) { }
            }
            DatePickerDialog(
                this,
                { _, y, m, d ->
                    deadline = String.format(Locale.US, "%04d-%02d-%02d", y, m + 1, d)
                    etDeadline.setText(formatTanggal(deadline))
                },
                cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        val dialog = AlertDialog.Builder(this)
            .setTitle(if (t == null) "Tambah Tugas" else "Edit Tugas")
            .setView(view)
            .setPositiveButton("Simpan", null) // di-override supaya tidak otomatis tertutup
            .setNegativeButton("Batal", null)
            .create()

        dialog.setOnShowListener {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                val judul = etJudul.text.toString().trim()
                val matkul = etMatkul.text.toString().trim()
                val deskripsi = etDeskripsi.text.toString().trim()

                if (judul.isEmpty()) { etJudul.error = "Judul wajib diisi"; return@setOnClickListener }
                if (matkul.isEmpty()) { etMatkul.error = "Mata kuliah wajib diisi"; return@setOnClickListener }
                if (deadline.isEmpty()) { etDeadline.error = "Pilih deadline"; return@setOnClickListener }

                if (t == null) {
                    db.insertTugas(Tugas(judul = judul, mataKuliah = matkul, deskripsi = deskripsi, deadline = deadline))
                    Toast.makeText(this, "Tugas ditambahkan", Toast.LENGTH_SHORT).show()
                } else {
                    db.updateTugas(t.copy(judul = judul, mataKuliah = matkul, deskripsi = deskripsi, deadline = deadline))
                    Toast.makeText(this, "Tugas diperbarui", Toast.LENGTH_SHORT).show()
                }
                muatData()
                dialog.dismiss()
            }
        }
        dialog.show()
    }
}
