package com.example.catatankuliah

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class JadwalAdapter(
    private var data: MutableList<Jadwal>,
    private val onEdit: (Jadwal) -> Unit,
    private val onDelete: (Jadwal) -> Unit
) : RecyclerView.Adapter<JadwalAdapter.VH>() {

    class VH(v: View) : RecyclerView.ViewHolder(v) {
        val tvHari: TextView = v.findViewById(R.id.tvHari)
        val tvMatkul: TextView = v.findViewById(R.id.tvMatkul)
        val tvWaktu: TextView = v.findViewById(R.id.tvWaktu)
        val tvInfo: TextView = v.findViewById(R.id.tvInfo)
        val btnEdit: ImageButton = v.findViewById(R.id.btnEdit)
        val btnHapus: ImageButton = v.findViewById(R.id.btnHapus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_jadwal, parent, false)
        return VH(v)
    }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(h: VH, position: Int) {
        val j = data[position]
        h.tvHari.text = j.hari
        h.tvMatkul.text = j.mataKuliah
        h.tvWaktu.text = "${j.jamMulai} - ${j.jamSelesai}"
        val info = listOf(j.ruangan, j.dosen).filter { it.isNotBlank() }.joinToString(" • ")
        h.tvInfo.text = info
        h.tvInfo.visibility = if (info.isBlank()) View.GONE else View.VISIBLE
        h.btnEdit.setOnClickListener { onEdit(j) }
        h.btnHapus.setOnClickListener { onDelete(j) }
    }

    fun setData(newData: MutableList<Jadwal>) {
        data = newData
        notifyDataSetChanged()
    }
}
