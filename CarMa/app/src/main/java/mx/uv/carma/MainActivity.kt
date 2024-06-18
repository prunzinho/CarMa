package mx.uv.carma

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import mx.uv.carma.databinding.ActivityMainBinding
import mx.uv.carma.modelo.BusquedaBD
import mx.uv.carma.poko.Pais
import mx.uv.carma.poko.Estado
import mx.uv.carma.poko.Municipio

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val db = BusquedaBD(this)
        if (db.obtenerPaises().isEmpty()) { // Verificar si la base de datos ya tiene datos
            cargarDatosIniciales(db)
        }



        irPantallaBusqueda()



    }

    private fun cargarDatosIniciales(db: BusquedaBD) {
        val estadosConMunicipios = mapOf(
            "Aguascalientes" to listOf("Aguascalientes", "Asientos", "Calvillo", "Cosío", "Jesús María"),
            "Baja California" to listOf("Ensenada", "Mexicali", "Tecate", "Tijuana", "Playas de Rosarito"),
            "Baja California Sur" to listOf("La Paz", "Los Cabos", "Loreto", "Mulegé", "Comondú"),
            "Campeche" to listOf("Campeche", "Carmen", "Champotón", "Escárcega", "Hecelchakán"),
            "Chiapas" to listOf("Tuxtla Gutiérrez", "San Cristóbal de las Casas", "Tapachula", "Comitán", "Palenque"),
            "Chihuahua" to listOf("Chihuahua", "Juárez", "Delicias", "Parral", "Cuauhtémoc"),
            "Ciudad de México" to listOf("Álvaro Obregón", "Coyoacán", "Cuajimalpa de Morelos", "Gustavo A. Madero", "Iztacalco"),
            "Coahuila" to listOf("Saltillo", "Torreón", "Monclova", "Piedras Negras", "Acuña"),
            "Colima" to listOf("Colima", "Manzanillo", "Tecomán", "Villa de Álvarez", "Armería"),
            "Durango" to listOf("Durango", "Gómez Palacio", "Lerdo", "Ciudad Lerdo", "Santiago Papasquiaro"),
            "Guanajuato" to listOf("León", "Irapuato", "Celaya", "Guanajuato", "San Miguel de Allende"),
            "Guerrero" to listOf("Acapulco", "Chilpancingo", "Iguala", "Zihuatanejo", "Taxco"),
            "Hidalgo" to listOf("Pachuca", "Tulancingo", "Tula de Allende", "Huejutla de Reyes", "Tepeji del Río"),
            "Jalisco" to listOf("Guadalajara", "Zapopan", "Tlaquepaque", "Tonalá", "Puerto Vallarta"),
            "Estado de México" to listOf("Toluca", "Ecatepec", "Naucalpan", "Tlalnepantla", "Chimalhuacán"),
            "Michoacán" to listOf("Morelia", "Uruapan", "Zamora", "Lázaro Cárdenas", "Zitácuaro"),
            "Morelos" to listOf("Cuernavaca", "Jiutepec", "Cuautla", "Temixco", "Emiliano Zapata"),
            "Nayarit" to listOf("Tepic", "Bahía de Banderas", "Xalisco", "San Blas", "Compostela"),
            "Nuevo León" to listOf("Monterrey", "Guadalupe", "San Nicolás de los Garza", "Apodaca", "Santa Catarina"),
            "Oaxaca" to listOf("Oaxaca de Juárez", "Salina Cruz", "Juchitán de Zaragoza", "Tuxtepec", "San Juan Bautista Tuxtepec"),
            "Puebla" to listOf("Puebla", "Tehuacán", "San Martín Texmelucan", "Atlixco", "Huauchinango"),
            "Querétaro" to listOf("Querétaro", "San Juan del Río", "El Marqués", "Corregidora", "Colón"),
            "Quintana Roo" to listOf("Cancún", "Playa del Carmen", "Chetumal", "Cozumel", "Tulum"),
            "San Luis Potosí" to listOf("San Luis Potosí", "Ciudad Valles", "Matehuala", "Soledad de Graciano Sánchez", "Rioverde"),
            "Sinaloa" to listOf("Culiacán", "Mazatlán", "Los Mochis", "Guasave", "Navolato"),
            "Sonora" to listOf("Hermosillo", "Ciudad Obregón", "Nogales", "San Luis Río Colorado", "Guaymas"),
            "Tabasco" to listOf("Villahermosa", "Cárdenas", "Comalcalco", "Macuspana", "Paraíso"),
            "Tamaulipas" to listOf("Reynosa", "Matamoros", "Nuevo Laredo", "Tampico", "Ciudad Victoria"),
            "Tlaxcala" to listOf("Tlaxcala", "Apizaco", "Huamantla", "San Pablo del Monte", "Chiautempan"),
            "Veracruz" to listOf("Veracruz", "Xalapa", "Coatzacoalcos", "Orizaba", "Poza Rica"),
            "Yucatán" to listOf("Mérida", "Valladolid", "Tizimín", "Progreso", "Tekax"),
            "Zacatecas" to listOf("Zacatecas", "Fresnillo", "Guadalupe", "Jerez", "Río Grande")
        )

        val paisId = db.insertarPais(Pais(0, "México"))

        for ((estado, municipios) in estadosConMunicipios) {
            val estadoId = db.insertarEstado(Estado(0, estado, paisId))
            for (municipio in municipios) {
                db.insertarMunicipio(Municipio(0, municipio, estadoId))
            }
        }
    }

    fun irPantallaBusqueda(){


        Handler().postDelayed({
            val intent = Intent(this@MainActivity,LoginActivity::class.java)
            startActivity(intent)
            finish()
        }, 3000)



    }
}