package mx.uv.carma

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import mx.uv.carma.databinding.ActivityPerfilBinding
import java.io.InputStream
import java.net.URL
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import com.google.firebase.auth.FirebaseAuth

class PerfilActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPerfilBinding
    private lateinit var mDatabase: DatabaseReference
    private var uid = ""
    private var nombre = ""
    private var fotoUrl = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityPerfilBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        val view = binding.root
        setContentView(view)

        nombre = intent.getStringExtra("nombre")!!
        uid = intent.getStringExtra("uid")!!

        mDatabase = FirebaseDatabase.getInstance().reference

        cargarDatosUsuario()

        binding.tvNombreUsuario.text = nombre

        binding.ivNavInicio.setOnClickListener {
            irPantallaInicio()
        }

        binding.ivNavBusqueda.setOnClickListener {
            irPantallaBusqueda()
        }

        binding.ivNavVenta.setOnClickListener {
            irPantallaVenta()
        }

        binding.ivNavChats.setOnClickListener {
            irPantallaChats()
        }

        binding.llEditarPerfil.setOnClickListener {
            irPantallaEditarPerfil()
        }

        binding.llCerrarSesion.setOnClickListener{
            cerrarSesion()
        }
    }

    private fun cargarDatosUsuario() {
        val userRef = mDatabase.child("Users").child(uid)
        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val nombre = snapshot.child("nombre").getValue(String::class.java)
                fotoUrl = snapshot.child("fotoUrl").getValue(String::class.java).orEmpty()

                binding.tvNombreUsuario.text = nombre
                if (fotoUrl.isNotEmpty()) {
                    DownloadImageTask(binding.ivImgPerfilUsuario).execute(fotoUrl)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@PerfilActivity, "Error al cargar datos.", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // Navegación
    private fun irPantallaInicio() {
        val intent = Intent(this@PerfilActivity, InicioActivity::class.java)
        intent.putExtra("nombre", nombre)
        intent.putExtra("uid", uid)
        startActivity(intent)
    }

    private fun irPantallaBusqueda() {
        val intent = Intent(this@PerfilActivity, BusquedaActivity::class.java)
        intent.putExtra("nombre", nombre)
        intent.putExtra("uid", uid)
        startActivity(intent)
    }

    private fun irPantallaEditarPerfil() {
        val intent = Intent(this@PerfilActivity, EditarPerfilActivity::class.java)
        intent.putExtra("nombre", nombre)
        intent.putExtra("uid", uid)
        startActivity(intent)
    }

    private fun irPantallaVenta() {
        val intent = Intent(this@PerfilActivity, VentaActivity::class.java)
        intent.putExtra("nombre", nombre)
        intent.putExtra("uid", uid)
        startActivity(intent)
    }

    private fun irPantallaChats() {
        val intent = Intent(this@PerfilActivity, MisChatsActivity::class.java)
        intent.putExtra("nombre", nombre)
        intent.putExtra("uid", uid)
        startActivity(intent)
    }

    private fun cerrarSesion() {
        FirebaseAuth.getInstance().signOut()
        val intent = Intent(this@PerfilActivity, MainActivity::class.java)
        // Limpiar la pila de actividades
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
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