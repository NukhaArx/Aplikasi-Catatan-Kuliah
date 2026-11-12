package com.example.catatankuliah

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TugasAdapter(
    private var data: MutableList<Tugas>,
    private val onToggle: (Tugas, Boolean) -> Unit,
    private val onEdit: (Tugas) -> Unit,
    private val onDelete: (Tugas) -> Unit
) : RecyclerView.Adapter<TugasAdapter.VH>() {

    class VH(v: View) : RecyclerView.ViewHolder(v) {
        val cb: CheckBox = v.findViewById(R.id.cbSelesai)
        val tvJudul: TextView = v.findViewById(R.id.tvJudul)
        val tvMatkul: TextView = v.findViewById(R.id.tvMatkul)
        val tvDeadline: TextView = v.findViewById(R.id.tvDeadline)
        val tvDeskripsi: TextView = v.findViewById(R.id.tvDeskripsi)
        val btnEdit: ImageButton = v.findViewById(R.id.btnEdit)
        val btnHapus: ImageButton = v.findViewById(R.id.btnHapus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_tugas, parent, false)
        return VH(v)
    }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(h: VH, position: Int) {
        val t = data[position]
        h.tvJudul.text = t.judul
        h.tvMatkul.text = t.mataKuliah
        h.tvDeadline.text = "Deadline: ${formatTanggal(t.deadline)}"
        h.tvDeskripsi.text = t.deskripsi
        h.tvDeskripsi.visibility = if (t.deskripsi.isBlank()) View.GONE else View.VISIBLE

        h.cb.isChecked = t.selesai
        // coret judul kalau sudah selesai
        h.tvJudul.paintFlags = if (t.selesai)
            h.tvJudul.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        else
            h.tvJudul.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
        h.itemView.alpha = if (t.selesai) 0.55f else 1f

        h.cb.setOnClickListener { onToggle(t, h.cb.isChecked) }
        h.btnEdit.setOnClickListener { onEdit(t) }
        h.btnHapus.setOnClickListener { onDelete(t) }
    }

    fun setData(newData: MutableList<Tugas>) {
        data = newData
        notifyDataSetChanged()
    }
}
