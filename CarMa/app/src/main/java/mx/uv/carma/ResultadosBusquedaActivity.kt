package mx.uv.carma

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.*
import mx.uv.carma.adaptadores.CocheAdapter
import mx.uv.carma.databinding.ActivityResultadosBusquedaBinding
import mx.uv.carma.poko.Coche

class ResultadosBusquedaActivity : AppCompatActivity() {
    private lateinit var binding: ActivityResultadosBusquedaBinding
    private lateinit var databaseRef: DatabaseReference

    // Variables para los filtros
    private lateinit var marcas: Set<String>
    private var anio: String? = null
    private var transmision: String? = null
    private var combustible: String? = null
    private var precioMin: Int? = null
    private var precioMax: Int? = null
    private var kmMin: Int? = null
    private var kmMax: Int? = null
    private var pais: String? = null
    private var estado: String? = null
    private var municipio: String? = null

    private var uid = ""
    private var nombre = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityResultadosBusquedaBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        val view = binding.root
        setContentView(view)

        nombre = intent.getStringExtra("nombre")!!
        uid = intent.getStringExtra("uid")!!

        // Obtener los datos de los filtros de la intención
        val intent = intent
        marcas = intent.getStringArrayListExtra("marcasSeleccionadas")?.toSet() ?: emptySet()
        anio = intent.getStringExtra("anio")
        transmision = intent.getStringExtra("transmision")
        combustible = intent.getStringExtra("combustible")
        precioMin = intent.getIntExtra("precioMin", -1).takeIf { it != -1 }
        precioMax = intent.getIntExtra("precioMax", -1).takeIf { it != -1 }
        kmMin = intent.getIntExtra("kmMin", -1).takeIf { it != -1 }
        kmMax = intent.getIntExtra("kmMax", -1).takeIf { it != -1 }
        pais = intent.getStringExtra("pais")
        estado = intent.getStringExtra("estado")
        municipio = intent.getStringExtra("municipio")

        // Log para verificar valores de filtros
        Log.i("Filtros", "Marcas: $marcas, Año: $anio, Transmisión: $transmision, Combustible: $combustible, Precio Min: $precioMin, Precio Max: $precioMax, Km Min: $kmMin, Km Max: $kmMax, País: $pais, Estado: $estado, Municipio: $municipio")

        // Configurar RecyclerView
        binding.recyclerCocheInicio.layoutManager = LinearLayoutManager(this)

        // Inicializar referencia de Firebase
        databaseRef = FirebaseDatabase.getInstance().getReference("publicaciones")

        // Cargar datos desde Firebase con filtro
        cargarDatosDesdeFirebase()

        //navegacion inferior

        //Ir a Inicio

        binding.ivNavInicio.setOnClickListener {
            irPantallaInicio()
        }

        binding.ivNavPerfil.setOnClickListener {
            irPantallaPerfil()
        }

        binding.ivNavVenta.setOnClickListener {
            irPantallaVenta()
        }

        binding.ivNavChats.setOnClickListener {
            irPantallaMisChats()
        }

        binding.ivNavBusqueda.setOnClickListener {
            irPantallaBusqueda()
        }

    }

    private fun cargarDatosDesdeFirebase() {
        databaseRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val coches = mutableListOf<Coche>()
                for (postSnapshot in snapshot.children) {
                    val coche = postSnapshot.getValue(Coche::class.java)
                    coche?.let {
                        if (filtrarCoche(it)) {
                            coches.add(it)
                        } else {
                            // Log para depurar por qué el coche no fue filtrado
                            Log.i("FiltrarCoche", "Coche no filtrado: ${it.marca} ${it.anio} ${it.transmision} ${it.combustible} ${it.precio} ${it.km} ${it.ubicacionPais} ${it.ubicacionEstado} ${it.ubicacionMunicipio}")
                        }
                    }
                }

                if (coches.isEmpty()) {
                    binding.recyclerCocheInicio.visibility = View.GONE
                    binding.tvNoPublicaciones.visibility = View.VISIBLE
                } else {
                    binding.recyclerCocheInicio.visibility = View.VISIBLE
                    binding.tvNoPublicaciones.visibility = View.GONE
                    val adapter = CocheAdapter(this@ResultadosBusquedaActivity, coches,uid,nombre)
                    binding.recyclerCocheInicio.adapter = adapter
                }


                val adapter = CocheAdapter(this@ResultadosBusquedaActivity, coches,uid,nombre)
                binding.recyclerCocheInicio.adapter = adapter
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ResultadosBusquedaActivity, "Error al cargar los datos", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun filtrarCoche(coche: Coche): Boolean {
        val resultado = (marcas.isEmpty() || marcas.contains(coche.marca)) &&
                (anio == null || anio == "Cualquiera" || anio == coche.anio.toString()) &&
                (transmision == null || transmision == "Cualquiera" || transmision == coche.transmision) &&
                (combustible == null || combustible == "Cualquiera" || combustible == coche.combustible) &&
                (precioMin == null || coche.precio >= precioMin!!) &&
                (precioMax == null || coche.precio <= precioMax!!) &&
                (kmMin == null || coche.km >= kmMin!!) &&
                (kmMax == null || coche.km <= kmMax!!) &&
                (pais == null || pais == "Cualquiera" || pais == coche.ubicacionPais) &&
                (estado == null || estado == "Cualquiera" || estado == coche.ubicacionEstado) &&
                (municipio == null || municipio == "Cualquiera" || municipio == coche.ubicacionMunicipio)

        // Log para depurar el resultado del filtrado
        Log.i("FiltrarCoche", "Coche: ${coche.marca} ${coche.anio} ${coche.transmision} ${coche.combustible} ${coche.precio} ${coche.km} ${coche.ubicacionPais} ${coche.ubicacionEstado} ${coche.ubicacionMunicipio} -> $resultado")

        return resultado
    }

    //Navegacion:

    fun irPantallaInicio(){
        val intent = Intent(this@ResultadosBusquedaActivity,InicioActivity::class.java)
        intent.putExtra("nombre", nombre)
        intent.putExtra("uid", uid)
        startActivity(intent)
    }

    fun irPantallaPerfil(){
        val intent = Intent(this@ResultadosBusquedaActivity,PerfilActivity::class.java)
        intent.putExtra("nombre", nombre)
        intent.putExtra("uid", uid)
        startActivity(intent)
    }

    fun irPantallaVenta(){
        val intent = Intent(this@ResultadosBusquedaActivity,VentaActivity::class.java)
        intent.putExtra("nombre", nombre)
        intent.putExtra("uid", uid)
        startActivity(intent)
    }

    fun irPantallaMisChats(){
        val intent = Intent(this@ResultadosBusquedaActivity,MisChatsActivity::class.java)
        intent.putExtra("nombre", nombre)
        intent.putExtra("uid", uid)
        startActivity(intent)
    }

    fun irPantallaBusqueda(){
        val intent = Intent(this@ResultadosBusquedaActivity,BusquedaActivity::class.java)
        intent.putExtra("nombre", nombre)
        intent.putExtra("uid", uid)
        startActivity(intent)
    }
}
