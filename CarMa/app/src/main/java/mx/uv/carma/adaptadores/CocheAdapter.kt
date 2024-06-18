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
import mx.uv.carma.InformacionCocheActivity
import mx.uv.carma.databinding.ItemRecyclerCochesBinding
import mx.uv.carma.poko.Coche
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

import java.text.NumberFormat
import java.util.Locale

class CocheAdapter(
    private val context: Context,
    private val coches: List<Coche>,
    private val uidUser: String,
    private val nombreUser: String
) : RecyclerView.Adapter<CocheAdapter.CocheViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CocheViewHolder {
        val inflater = LayoutInflater.from(context)
        val binding = ItemRecyclerCochesBinding.inflate(inflater, parent, false)
        return CocheViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CocheViewHolder, position: Int) {
        val coche = coches[position]
        holder.bind(coche)

        // Botón "Ver más"
        holder.itemView.findViewById<Button>(mx.uv.carma.R.id.btn_ver_mas).setOnClickListener {
            val intent = Intent(context, InformacionCocheActivity::class.java).apply {
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
                putExtra("uid", coche.uid)
                putExtra("nombre", coche.nombre)
                putExtra("uidU", uidUser)
                putExtra("nombreU", nombreUser)

            }
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = coches.size

    inner class CocheViewHolder(private val binding: ItemRecyclerCochesBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(coche: Coche) {
            // Cargar imagen de forma nativa
            DownloadImageTask(binding.ivImagenCoche).execute(coche.imagenDiagonal)
            binding.tvAnioCoche.text = coche.anio

            binding.tvTransmisionCoche.text = coche.transmision
            binding.tvCombustibleCoche.text = coche.combustible
            binding.tvModeloCoche.text = coche.modelo
            //binding.tvPrecioCoche.text = "$${coche.precio}"

            // Formatea el precio
            val formatter = NumberFormat.getNumberInstance(Locale.US)
            val precioFormateado = formatter.format(coche.precio)
            val kmFormateado = formatter.format(coche.km)

            binding.tvKmCoche.text = kmFormateado

            binding.tvPrecioCoche.text = "$$precioFormateado"


            binding.btnVerMas.setOnClickListener {
                onVerMasClicked(coche)
            }
        }
    }

    private fun onVerMasClicked(coche: Coche) {
        val intent = Intent(context, InformacionCocheActivity::class.java).apply {
            putExtra("coche", coche)
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
