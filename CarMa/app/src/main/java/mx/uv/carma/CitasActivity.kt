package mx.uv.carma

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import mx.uv.carma.adaptadores.CitasAdapter
import mx.uv.carma.databinding.ActivityCitasBinding
import mx.uv.carma.poko.Cita

class CitasActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCitasBinding
    private lateinit var citasAdapter: CitasAdapter
    private val citasList = mutableListOf<Cita>()

    private var uid = ""
    private var nombre = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCitasBinding.inflate(layoutInflater)
        setContentView(binding.root)

        nombre = intent.getStringExtra("nombre")!!
        uid = intent.getStringExtra("uid")!!

        setupRecyclerView()
        fetchCitasFromFirebase()

        // Navegación
        binding.ivNavInicio.setOnClickListener { irPantallaInicio() }
        binding.ivNavBusqueda.setOnClickListener { irPantallaBusqueda() }
        binding.ivNavPerfil.setOnClickListener { irPantallaPerfil() }
        binding.ivNavVenta.setOnClickListener { irPantallaVenta() }
        binding.ivNavChats.setOnClickListener { irPantallaMisChats() }

    }

    private fun setupRecyclerView() {
        citasAdapter = CitasAdapter(citasList)
        binding.recyclerCitas.layoutManager = LinearLayoutManager(this)
        binding.recyclerCitas.adapter = citasAdapter
    }

    private fun fetchCitasFromFirebase() {
        val uidUsuario = FirebaseAuth.getInstance().currentUser?.uid
        val database = FirebaseDatabase.getInstance()
        val citasRef = database.getReference("citas")

        citasRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                citasList.clear()
                for (citaSnapshot in snapshot.children) {
                    val cita = citaSnapshot.getValue(Cita::class.java)
                    if (cita != null && (cita.uidUsuario == uidUsuario || cita.uidVendedor == uidUsuario)) {
                        citasList.add(cita)
                    }
                }
                citasAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                // Manejar error
            }
        })
    }

    // Navegación
    private fun irPantallaInicio() {
        val intent = Intent(this@CitasActivity, InicioActivity::class.java)
        intent.putExtra("nombre", nombre)
        intent.putExtra("uid", uid)
        startActivity(intent)
    }

    fun irPantallaBusqueda() {
        val intent = Intent(this@CitasActivity, BusquedaActivity::class.java)
        intent.putExtra("nombre", nombre)
        intent.putExtra("uid", uid)
        startActivity(intent)
    }

    fun irPantallaPerfil() {
        val intent = Intent(this@CitasActivity, PerfilActivity::class.java)
        intent.putExtra("nombre", nombre)
        intent.putExtra("uid", uid)
        startActivity(intent)
    }

    fun irPantallaVenta() {
        val intent = Intent(this@CitasActivity, VentaActivity::class.java)
        intent.putExtra("nombre", nombre)
        intent.putExtra("uid", uid)
        startActivity(intent)
    }

    fun irPantallaMisChats(){
        val intent = Intent(this@CitasActivity,MisChatsActivity::class.java)
        intent.putExtra("nombre", nombre)
        intent.putExtra("uid", uid)
        startActivity(intent)
    }
}
