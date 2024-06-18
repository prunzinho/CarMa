package mx.uv.carma

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.*
import mx.uv.carma.adaptadores.EditarCocheAdapter
import mx.uv.carma.databinding.ActivityEditarPublicacionBinding
import mx.uv.carma.poko.Coche

class EditarPublicacionActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditarPublicacionBinding
    private lateinit var databaseRef: DatabaseReference
    private var uid = ""
    private var nombre = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityEditarPublicacionBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        val view = binding.root
        setContentView(view)

        nombre = intent.getStringExtra("nombre")!!
        uid = intent.getStringExtra("uid")!!

        // Configurar RecyclerView
        binding.recyclerEditarCoche.layoutManager = LinearLayoutManager(this)

        // Inicializar referencia de Firebase
        databaseRef = FirebaseDatabase.getInstance().getReference("publicaciones")

        // Cargar datos desde Firebase
        cargarDatosDesdeFirebase()

        // Navegación inferior
        binding.ivNavInicio.setOnClickListener { irPantallaInicio() }
        binding.ivNavBusqueda.setOnClickListener { irPantallaBusqueda() }
        binding.ivNavPerfil.setOnClickListener { irPantallaPerfil() }
        binding.ivNavVenta.setOnClickListener { irPantallaVenta() }
        binding.ivNavChats.setOnClickListener { irPantallaMisChats() }
    }

    private fun cargarDatosDesdeFirebase() {
        databaseRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val coches = mutableListOf<Coche>()
                for (postSnapshot in snapshot.children) {
                    val coche = postSnapshot.getValue(Coche::class.java)
                    // Filtrar los coches por uid
                    if (coche?.uid == uid) {
                        coche?.let { coches.add(it) }
                    }
                }
                val adapter = EditarCocheAdapter(this@EditarPublicacionActivity, coches, uid, nombre)
                binding.recyclerEditarCoche.adapter = adapter
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@EditarPublicacionActivity, "Error al cargar los datos", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // Navegación:
    fun irPantallaInicio() {
        val intent = Intent(this@EditarPublicacionActivity, InicioActivity::class.java)
        intent.putExtra("nombre", nombre)
        intent.putExtra("uid", uid)
        startActivity(intent)
    }

    fun irPantallaBusqueda() {
        val intent = Intent(this@EditarPublicacionActivity, BusquedaActivity::class.java)
        intent.putExtra("nombre", nombre)
        intent.putExtra("uid", uid)
        startActivity(intent)
    }

    fun irPantallaPerfil() {
        val intent = Intent(this@EditarPublicacionActivity, PerfilActivity::class.java)
        intent.putExtra("nombre", nombre)
        intent.putExtra("uid", uid)
        startActivity(intent)
    }

    private fun irPantallaVenta() {
        val intent = Intent(this@EditarPublicacionActivity, VentaActivity::class.java)
        intent.putExtra("nombre", nombre)
        intent.putExtra("uid", uid)
        startActivity(intent)
    }

    fun irPantallaMisChats(){
        val intent = Intent(this@EditarPublicacionActivity,MisChatsActivity::class.java)
        intent.putExtra("nombre", nombre)
        intent.putExtra("uid", uid)
        startActivity(intent)
    }
}
