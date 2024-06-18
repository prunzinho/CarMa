package mx.uv.carma.adaptadores

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import mx.uv.carma.EditarPublicacionCocheActivity
import mx.uv.carma.databinding.ItemRecyclerEditarCochesBinding
import mx.uv.carma.poko.Coche
import java.io.InputStream
import java.net.URL
import java.text.NumberFormat
import java.util.Locale

class EditarCocheAdapter(
    private val context: Context,
    private val coches: List<Coche>,
    private val uidUser: String, // Añadir uid aquí
    private val nombreUser: String // Añadir nombre aquí
) : RecyclerView.Adapter<EditarCocheAdapter.CocheViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CocheViewHolder {
        val inflater = LayoutInflater.from(context)
        val binding = ItemRecyclerEditarCochesBinding.inflate(inflater, parent, false)

        return CocheViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CocheViewHolder, position: Int) {
        val coche = coches[position]
        holder.bind(coche)

        // Botón "Editar"
        holder.itemView.findViewById<Button>(mx.uv.carma.R.id.btn_editar).setOnClickListener {
            val intent = Intent(context, EditarPublicacionCocheActivity::class.java).apply {
                putExtra("postId", coche.postId) // Agregar postId
                putExtra("modelo", coche.modelo)
                putExtra("marca", coche.marca)
                putExtra("anio", coche.anio)
                putExtra("transmision", coche.transmision)
                putExtra("combustible", coche.combustible)
                putExtra("precio", coche.precio.toString())
                putExtra("km", coche.km.toString())
                putExtra("ubicacionPais", coche.ubicacionPais)
                putExtra("ubicacionEstado", coche.ubicacionEstado)
                putExtra("ubicacionMunicipio", coche.ubicacionMunicipio)
                putExtra("imagenAtras", coche.imagenAtras)
                putExtra("imagenDiagonal", coche.imagenDiagonal)
                putExtra("imagenFrente", coche.imagenFrente)
                putExtra("imagenLado_derecho", coche.imagenLado_derecho)
                putExtra("imagenLado_izquierdo", coche.imagenLado_izquierdo)
                putExtra("uidCoche", coche.uid) // Añadir uid aquí
                putExtra("nombreCocheUser", coche.nombre) // Añadir nombre aquí
                putExtra("uidUser", uidUser) // Pasar uid del usuario
                putExtra("nombreUser", nombreUser) // Pasar nombre del usuario
            }
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = coches.size

    inner class CocheViewHolder(private val binding: ItemRecyclerEditarCochesBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(coche: Coche) {
            // Cargar imagen de forma nativa
            DownloadImageTask(binding.ivImagenCoche).execute(coche.imagenDiagonal)
            binding.tvAnioCoche.text = coche.anio
            binding.tvTransmisionCoche.text = coche.transmision
            binding.tvCombustibleCoche.text = coche.combustible
            binding.tvModeloCoche.text = coche.modelo

            // Formatea el precio
            val formatter = NumberFormat.getNumberInstance(Locale.US)
            val precioFormateado = formatter.format(coche.precio)
            val kmFormateado = formatter.format(coche.km)

            binding.tvKmCoche.text = kmFormateado
            binding.tvPrecioCoche.text = "$$precioFormateado"

            binding.btnEditar.setOnClickListener {
                onEditarClicked(coche)
            }
        }
    }

    private fun onEditarClicked(coche: Coche) {
        val intent = Intent(context, EditarPublicacionCocheActivity::class.java).apply {
            putExtra("postId", coche.postId) // Agregar postId
            putExtra("modelo", coche.modelo)
            putExtra("marca", coche.marca)
            putExtra("anio", coche.anio)
            putExtra("transmision", coche.transmision)
            putExtra("combustible", coche.combustible)
            putExtra("precio", coche.precio.toString())
            putExtra("km", coche.km.toString())
            putExtra("ubicacionPais", coche.ubicacionPais)
            putExtra("ubicacionEstado", coche.ubicacionEstado)
            putExtra("ubicacionMunicipio", coche.ubicacionMunicipio)
            putExtra("imagenAtras", coche.imagenAtras)
            putExtra("imagenDiagonal", coche.imagenDiagonal)
            putExtra("imagenFrente", coche.imagenFrente)
            putExtra("imagenLado_derecho", coche.imagenLado_derecho)
            putExtra("imagenLado_izquierdo", coche.imagenLado_izquierdo)
            //putExtra("uid", uid) // Añadir uid aquí
            //putExtra("nombre", nombre) // Añadir nombre aquí
        }
        context.startActivity(intent)
    }

    private class DownloadImageTask(private val imageView: ImageView) : AsyncTask<String, Void, Bitmap?>() {
        override fun doInBackground(vararg urls: String): Bitmap? {
            val url = urls[0]
            var bmp: Bitmap? = null
            try {
                val input: InputStream = URL(url).openStream()
                bmp = BitmapFactory.decodeStream(input)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return bmp
        }

        override fun onPostExecute(result: Bitmap?) {
            imageView.setImageBitmap(result)
        }
    }
}
