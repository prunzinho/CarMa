package mx.uv.carma.adaptadores

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import mx.uv.carma.databinding.ItemRecyclerCitasBinding
import mx.uv.carma.poko.Cita

class CitasAdapter(private val citasList: List<Cita>) : RecyclerView.Adapter<CitasAdapter.CitaViewHolder>() {

    private val database: DatabaseReference = FirebaseDatabase.getInstance().getReference("Users")

    inner class CitaViewHolder(private val binding: ItemRecyclerCitasBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(cita: Cita) {
            Log.i("Cita", "ID usuario: ${cita.uidUsuario}  ID Vendedor: ${cita.uidVendedor}")

            fetchUserName(cita.uidUsuario) { nombreUsuario ->
                binding.tvNombreUsuario.text = "Citador: $nombreUsuario"
            }

            fetchUserName(cita.uidVendedor) { nombreVendedor ->
                binding.tvNombreVendedor.text = "Citado: $nombreVendedor"
            }

            binding.tvFechaCita.text = "Fecha: ${cita.fechaCita}"
            binding.tvHoraCita.text = "Hora: ${cita.horaCita}"
            binding.tvLugar.text = "Lugar: ${cita.lugar}"
            binding.tvDescripcion.text = "Descripción: ${cita.descripcion}"
        }

        private fun fetchUserName(uid: String?, callback: (String) -> Unit) {
            if (uid == null) {
                callback("Desconocido")
                return
            }

            database.child(uid).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val nombre = snapshot.child("nombre").getValue(String::class.java)
                    if (nombre != null) {
                        callback(nombre)
                    } else {
                        callback("Desconocido")
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("Cita", "Error fetching user data: ${error.message}")
                    callback("Error")
                }
            })
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CitaViewHolder {
        val binding = ItemRecyclerCitasBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CitaViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CitaViewHolder, position: Int) {
        holder.bind(citasList[position])
    }

    override fun getItemCount(): Int = citasList.size
}