package mx.uv.carma

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import mx.uv.carma.databinding.ActivityInformacionCocheBinding
import java.io.InputStream
import java.net.URL
import java.text.NumberFormat
import java.util.Locale

class InformacionCocheActivity : AppCompatActivity() {
    private lateinit var binding: ActivityInformacionCocheBinding
    private var uid = ""
    private var nombre = ""
    private var idVendedor = ""
    private var nombreV = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityInformacionCocheBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        val view = binding.root
        setContentView(view)

        nombre = intent.getStringExtra("nombreU")!!
        uid = intent.getStringExtra("uidU")!!



        binding.btnContactar.setOnClickListener{
            irPantallaChat()
        }

        binding.ivNavInicio.setOnClickListener {
            irPantallaInicio()
        }

        binding.ivNavPerfil.setOnClickListener {
            irPantallaPerfil()
        }

        binding.ivNavBusqueda.setOnClickListener {
            irPantallaBusqueda()
        }

        binding.ivNavVenta.setOnClickListener {
            irPantallaVenta()
        }

        binding.ivNavChats.setOnClickListener {
            irPantallaMisChats()
        }

        // Recibir datos del intent
        val modelo = intent.getStringExtra("modelo")
        val marca = intent.getStringExtra("marca")
        val anio = intent.getStringExtra("anio")
        val transmision = intent.getStringExtra("transmision")
        val combustible = intent.getStringExtra("combustible")
        val precio = intent.getStringExtra("precio")
        val km = intent.getStringExtra("km")
        val ubicacionPais = intent.getStringExtra("ubicacionPais")
        val ubicacionEstado = intent.getStringExtra("ubicacionEstado")
        val ubicacionMunicipio = intent.getStringExtra("ubicacionMunicipio")
        val nombreVendedor = intent.getStringExtra("nombre")
        val idVendedorCoche = intent.getStringExtra("uid")

        idVendedor = idVendedorCoche.toString()
        nombreV = nombreVendedor.toString()

        val imagenFrente = intent.getStringExtra("imagenFrente")
        val imagenDiagonal = intent.getStringExtra("imagenDiagonal")
        val imagenLadoDerecho = intent.getStringExtra("imagenLado_derecho")
        val imagenLadoIzquierdo = intent.getStringExtra("imagenLado_izquierdo")
        val imagenAtras = intent.getStringExtra("imagenAtras")

        // Actualizar TextViews con los datos recibidos
        binding.tvModeloCoche.text = modelo
        binding.tvMarcaCoche.text = marca
        binding.tvAnioCoche.text = anio
        binding.tvTransmisionCoche.text = transmision
        binding.tvCombustibleCoche.text = combustible
        // Formatea los valores
        val formattedPrecio = precio?.let { formatNumber(it) } ?: ""
        val formattedKm = km?.let { formatNumber(it) } ?: ""

        // Asigna los valores formateados a los TextViews
        binding.tvPrecioCoche.text = formattedPrecio
        binding.tvKmCoche.text = formattedKm
        binding.tvUbicacionCochePais.text = ubicacionPais
        binding.tvUbicacionCocheEstado.text = ubicacionEstado
        binding.tvUbicacionCocheMunicipio.text = ubicacionMunicipio


        DownloadImageTask(binding.ivImgCochePreview).execute(imagenDiagonal)
        DownloadImageTask(binding.tvImgCocheDiagonal).execute(imagenDiagonal)
        DownloadImageTask(binding.tvImgCocheFrente).execute(imagenFrente)
        DownloadImageTask(binding.ivImgCocheLadoDerecho).execute(imagenLadoDerecho)
        DownloadImageTask(binding.tvImgCocheLadoIzquierdo).execute(imagenLadoIzquierdo)
        DownloadImageTask(binding.tvImgCocheAtras).execute(imagenAtras)

        binding.tvNombreVendedor.text = nombreVendedor



        // Configurar listeners para cambiar la imagen de vista previa
        binding.llImgCocheFrente.setOnClickListener {
            DownloadImageTask(binding.ivImgCochePreview).execute(imagenFrente)
        }

        binding.llImgCocheDiagonal.setOnClickListener {
            DownloadImageTask(binding.ivImgCochePreview).execute(imagenDiagonal)
        }

        binding.llImgCocheLadoDerecho.setOnClickListener {
            DownloadImageTask(binding.ivImgCochePreview).execute(imagenLadoDerecho)
        }

        binding.llImgCocheLadoIzquierdo.setOnClickListener {
            DownloadImageTask(binding.ivImgCochePreview).execute(imagenLadoIzquierdo)
        }

        binding.llImgCocheAtras.setOnClickListener {
            DownloadImageTask(binding.ivImgCochePreview).execute(imagenAtras)
        }
    }

    fun formatNumber(number: String): String {
        val numberFormat = NumberFormat.getNumberInstance(Locale.US)
        val parsedNumber = number.toDoubleOrNull()
        return parsedNumber?.let { numberFormat.format(it) } ?: number
    }

    // Navegacion:
    fun irPantallaInicio() {
        val intent = Intent(this@InformacionCocheActivity, InicioActivity::class.java)
        intent.putExtra("nombre", nombre)
        intent.putExtra("uid", uid)
        startActivity(intent)
    }

    fun irPantallaBusqueda(){
        val intent = Intent(this@InformacionCocheActivity,BusquedaActivity::class.java)
        intent.putExtra("nombre", nombre)
        intent.putExtra("uid", uid)
        startActivity(intent)
    }

    fun irPantallaPerfil(){
        val intent = Intent(this@InformacionCocheActivity,PerfilActivity::class.java)
        intent.putExtra("nombre", nombre)
        intent.putExtra("uid", uid)
        startActivity(intent)
    }

    fun irPantallaVenta(){
        val intent = Intent(this@InformacionCocheActivity,VentaActivity::class.java)
        intent.putExtra("nombre", nombre)
        intent.putExtra("uid", uid)
        startActivity(intent)
    }

    fun irPantallaChat(){
        val intent = Intent(this@InformacionCocheActivity,ChatActivity::class.java)
        intent.putExtra("nameU", nombre)
        intent.putExtra("uidU", uid)
        intent.putExtra("uid", idVendedor)
        intent.putExtra("name",nombreV)
        startActivity(intent)
    }

    fun irPantallaMisChats(){
        val intent = Intent(this@InformacionCocheActivity,MisChatsActivity::class.java)
        intent.putExtra("nombre", nombre)
        intent.putExtra("uid", uid)
        startActivity(intent)
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
            if (result != null) {
                imageView.setImageBitmap(result)
            }
        }
    }
}
