package mx.uv.carma

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.*
import mx.uv.carma.adaptadores.CocheAdapter
import mx.uv.carma.databinding.ActivityInicioBinding
import mx.uv.carma.poko.Coche
import android.view.View

class InicioActivity : AppCompatActivity() {
    private lateinit var binding: ActivityInicioBinding
    private lateinit var databaseRef: DatabaseReference
    private var uid = ""
    private var nombre = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityInicioBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        val view = binding.root
        setContentView(view)

        nombre = intent.getStringExtra("nombre")!!
        uid = intent.getStringExtra("uid")!!

        // Navegación inferior
        binding.ivNavBusqueda.setOnClickListener { irPantallaBusqueda() }
        binding.ivNavPerfil.setOnClickListener { irPantallaPerfil() }
        binding.ivNavChats.setOnClickListener { irPantallaMisChats() }
        binding.ivNavVenta.setOnClickListener { irPantallaVenta() }

        // Configurar RecyclerView
        binding.recyclerCocheInicio.layoutManager = LinearLayoutManager(this)

        // Inicializar referencia de Firebase
        databaseRef = FirebaseDatabase.getInstance().getReference("publicaciones")

        // Cargar datos desde Firebase
        cargarDatosDesdeFirebase()
    }

    private fun cargarDatosDesdeFirebase() {
        databaseRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val coches = mutableListOf<Coche>()
                for (postSnapshot in snapshot.children) {
                    val coche = postSnapshot.getValue(Coche::class.java)
                    coche?.let { coches.add(it) }
                }

                if (coches.isEmpty()) {
                    binding.recyclerCocheInicio.visibility = View.GONE
                    binding.tvNoPublicaciones.visibility = View.VISIBLE
                } else {
                    binding.recyclerCocheInicio.visibility = View.VISIBLE
                    binding.tvNoPublicaciones.visibility = View.GONE
                    val adapter = CocheAdapter(this@InicioActivity, coches,uid,nombre)
                    binding.recyclerCocheInicio.adapter = adapter
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@InicioActivity, "Error al cargar los datos", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun irPantallaBusqueda() {
        val intent = Intent(this@InicioActivity, BusquedaActivity::class.java)
        intent.putExtra("nombre", nombre)
        intent.putExtra("uid", uid)
        startActivity(intent)
    }

    private fun irPantallaPerfil() {
        val intent = Intent(this@InicioActivity, PerfilActivity::class.java)
        intent.putExtra("nombre", nombre)
        intent.putExtra("uid", uid)
        startActivity(intent)
    }

    private fun irPantallaVenta() {
        val intent = Intent(this@InicioActivity, VentaActivity::class.java)
        intent.putExtra("nombre", nombre)
        intent.putExtra("uid", uid)
        startActivity(intent)
    }

    fun irPantallaMisChats(){
        val intent = Intent(this@InicioActivity,MisChatsActivity::class.java)
        intent.putExtra("nombre", nombre)
        intent.putExtra("uid", uid)
        startActivity(intent)
    }

}
