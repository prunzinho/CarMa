package mx.uv.carma

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import mx.uv.carma.databinding.ActivityVentaBinding
import java.io.InputStream
import java.net.URL
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.widget.ImageView
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class VentaActivity : AppCompatActivity() {
    private lateinit var binding: ActivityVentaBinding
    private lateinit var mDatabase: DatabaseReference
    private var uid = ""
    private var nombre = ""
    private var fotoUrl = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityVentaBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        val view = binding.root
        setContentView(view)

        nombre = intent.getStringExtra("nombre")!!
        uid = intent.getStringExtra("uid")!!

        mDatabase = FirebaseDatabase.getInstance().reference

        cargarDatosUsuario()

        binding.tvNombreUsuario.text = nombre

        binding.llCrearPublicacion.setOnClickListener {
            irPantallaCrearPublicacion()
        }

        binding.llEditarPublicacion.setOnClickListener {
            irPantallaEditarPublicacion()
        }

        binding.llAgendarCita.setOnClickListener {
            irPantallaCitas()
        }

        binding.ivNavInicio.setOnClickListener {
            irPantallaInicio()
        }

        binding.ivNavBusqueda.setOnClickListener {
            irPantallaBusqueda()
        }

        binding.ivNavPerfil.setOnClickListener {
            irPantallaPerfil()
        }

        binding.ivNavPerfil.setOnClickListener {
            irPantallaPerfil()
        }

        binding.ivNavChats.setOnClickListener {
            irPantallaMisChats()
        }


    }

    private fun cargarDatosUsuario() {
        val userRef = mDatabase.child("Users").child(uid)
        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val nomb = snapshot.child("nombre").getValue(String::class.java)
                fotoUrl = snapshot.child("fotoUrl").getValue(String::class.java).orEmpty()

                binding.tvNombreUsuario.text = nombre
                if (fotoUrl.isNotEmpty()) {
                    DownloadImageTask(binding.ivImgPerfilUsuario).execute(fotoUrl)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@VentaActivity, "Error al cargar datos.", Toast.LENGTH_SHORT).show()
            }
        })
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

    //Navegacion:

    fun irPantallaInicio(){
        val intent = Intent(this@VentaActivity,InicioActivity::class.java)
        intent.putExtra("nombre", nombre)
        intent.putExtra("uid", uid)
        startActivity(intent)
    }

    fun irPantallaBusqueda(){
        val intent = Intent(this@VentaActivity,BusquedaActivity::class.java)
        intent.putExtra("nombre", nombre)
        intent.putExtra("uid", uid)
        startActivity(intent)
    }


    fun irPantallaPerfil(){
        val intent = Intent(this@VentaActivity,PerfilActivity::class.java)
        intent.putExtra("nombre", nombre)
        intent.putExtra("uid", uid)
        startActivity(intent)
    }

    fun irPantallaCitas(){
        val intent = Intent(this@VentaActivity,CitasActivity::class.java)
        intent.putExtra("nombre", nombre)
        intent.putExtra("uid", uid)
        startActivity(intent)
    }

    fun irPantallaCrearPublicacion(){
        val intent = Intent(this@VentaActivity,CrearPublicacionActivity::class.java)
        intent.putExtra("nombre", nombre)
        intent.putExtra("uid", uid)
        startActivity(intent)
    }

    fun irPantallaEditarPublicacion(){
        val intent = Intent(this@VentaActivity,EditarPublicacionActivity::class.java)
        intent.putExtra("nombre", nombre)
        intent.putExtra("uid", uid)
        startActivity(intent)
    }

    fun irPantallaMisChats(){
        val intent = Intent(this@VentaActivity,MisChatsActivity::class.java)
        intent.putExtra("nombre", nombre)
        intent.putExtra("uid", uid)
        startActivity(intent)
    }
}