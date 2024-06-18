package mx.uv.carma

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import mx.uv.carma.databinding.ActivityCrearPublicacionBinding
import mx.uv.carma.modelo.BusquedaBD
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import android.view.View
import androidx.core.content.ContextCompat

class CrearPublicacionActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCrearPublicacionBinding
    private lateinit var db: BusquedaBD

    private var uid = ""
    private var nombre = ""

    private var marcaCoche = ""


    private val REQUEST_IMAGE_CAPTURE = 1
    private val REQUEST_IMAGE_SELECT = 2

    private var currentImagePath: String? = null
    private var imageUriDiagonal: Uri? = null
    private var imageUriFrente: Uri? = null
    private var imageUriLadoDerecho: Uri? = null
    private var imageUriLadoIzquierdo: Uri? = null
    private var imageUriAtras: Uri? = null
    private var currentPhotoPath: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityCrearPublicacionBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        val view = binding.root
        setContentView(view)

        db = BusquedaBD(this)

        nombre = intent.getStringExtra("nombre")!!
        uid = intent.getStringExtra("uid")!!


        // Navegación
        binding.ivNavInicio.setOnClickListener { irPantallaInicio() }
        binding.ivNavBusqueda.setOnClickListener { irPantallaBusqueda() }
        binding.ivNavPerfil.setOnClickListener { irPantallaPerfil() }
        binding.ivNavVenta.setOnClickListener { irPantallaVenta() }
        binding.ivNavChats.setOnClickListener { irPantallaMisChats() }

        binding.llMarcaVw.setOnClickListener {
            marcaCoche = "Volkswagen"
            val drawableRes = R.drawable.rounded_corners_l_primary
            binding.llMarcaVw.setBackgroundResource(drawableRes)

            val colorRes = R.color.l_accent
            binding.tvMarcaVw.setTextColor(ContextCompat.getColor(this@CrearPublicacionActivity, colorRes))

            val drawableResAccent = R.drawable.rounded_corners_busqueda_marca
            val colorResDark = R.color.l_primary_dark

            binding.llMarcaChevrolet.setBackgroundResource(drawableResAccent)
            binding.tvMarcaChevrolet.setTextColor(ContextCompat.getColor(this@CrearPublicacionActivity, colorResDark))

            binding.llMarcaFord.setBackgroundResource(drawableResAccent)
            binding.tvMarcaFord.setTextColor(ContextCompat.getColor(this@CrearPublicacionActivity, colorResDark))

            binding.llMarcaNissan.setBackgroundResource(drawableResAccent)
            binding.tvMarcaNissan.setTextColor(ContextCompat.getColor(this@CrearPublicacionActivity, colorResDark))
        }

        binding.llMarcaChevrolet.setOnClickListener {
            marcaCoche = "Chevrolet"

            val drawableRes = R.drawable.rounded_corners_l_primary
            binding.llMarcaChevrolet.setBackgroundResource(drawableRes)

            val colorRes = R.color.l_accent
            binding.tvMarcaChevrolet.setTextColor(ContextCompat.getColor(this@CrearPublicacionActivity, colorRes))

            val drawableResAccent = R.drawable.rounded_corners_busqueda_marca
            val colorResDark = R.color.l_primary_dark

            binding.llMarcaVw.setBackgroundResource(drawableResAccent)
            binding.tvMarcaVw.setTextColor(ContextCompat.getColor(this@CrearPublicacionActivity, colorResDark))

            binding.llMarcaFord.setBackgroundResource(drawableResAccent)
            binding.tvMarcaFord.setTextColor(ContextCompat.getColor(this@CrearPublicacionActivity, colorResDark))

            binding.llMarcaNissan.setBackgroundResource(drawableResAccent)
            binding.tvMarcaNissan.setTextColor(ContextCompat.getColor(this@CrearPublicacionActivity, colorResDark))
        }

        binding.llMarcaFord.setOnClickListener {
            marcaCoche = "Ford"

            val drawableRes = R.drawable.rounded_corners_l_primary
            binding.llMarcaFord.setBackgroundResource(drawableRes)

            val colorRes = R.color.l_accent
            binding.tvMarcaFord.setTextColor(ContextCompat.getColor(this@CrearPublicacionActivity, colorRes))

            val drawableResAccent = R.drawable.rounded_corners_busqueda_marca
            val colorResDark = R.color.l_primary_dark

            binding.llMarcaChevrolet.setBackgroundResource(drawableResAccent)
            binding.tvMarcaChevrolet.setTextColor(ContextCompat.getColor(this@CrearPublicacionActivity, colorResDark))

            binding.llMarcaVw.setBackgroundResource(drawableResAccent)
            binding.tvMarcaVw.setTextColor(ContextCompat.getColor(this@CrearPublicacionActivity, colorResDark))

            binding.llMarcaNissan.setBackgroundResource(drawableResAccent)
            binding.tvMarcaNissan.setTextColor(ContextCompat.getColor(this@CrearPublicacionActivity, colorResDark))
        }

        binding.llMarcaNissan.setOnClickListener {
            marcaCoche = "Nissan"

            val drawableRes = R.drawable.rounded_corners_l_primary
            binding.llMarcaNissan.setBackgroundResource(drawableRes)

            val colorRes = R.color.l_accent
            binding.tvMarcaNissan.setTextColor(ContextCompat.getColor(this@CrearPublicacionActivity, colorRes))

            val drawableResAccent = R.drawable.rounded_corners_busqueda_marca
            val colorResDark = R.color.l_primary_dark

            binding.llMarcaChevrolet.setBackgroundResource(drawableResAccent)
            binding.tvMarcaChevrolet.setTextColor(ContextCompat.getColor(this@CrearPublicacionActivity, colorResDark))

            binding.llMarcaFord.setBackgroundResource(drawableResAccent)
            binding.tvMarcaFord.setTextColor(ContextCompat.getColor(this@CrearPublicacionActivity, colorResDark))

            binding.llMarcaVw.setBackgroundResource(drawableResAccent)
            binding.tvMarcaVw.setTextColor(ContextCompat.getColor(this@CrearPublicacionActivity, colorResDark))
        }





        val spinnerPais = binding.spinnerPais
        val spinnerEstado = binding.spinnerEstado
        val spinnerMunicipio = binding.spinnerMunicipio

        // Llenar spinner de países
        val paises = db.obtenerPaises()
        val adapterPaises = ArrayAdapter(this, android.R.layout.simple_spinner_item, paises.map { it.nombre })
        spinnerPais.adapter = adapterPaises

        spinnerPais.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val paisSeleccionado = paises[position]
                val estados = db.obtenerEstadosPorPais(paisSeleccionado.id)
                val adapterEstados = ArrayAdapter(this@CrearPublicacionActivity, android.R.layout.simple_spinner_item, estados.map { it.nombre })
                spinnerEstado.adapter = adapterEstados

                spinnerEstado.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                        val estadoSeleccionado = estados[position]
                        val municipios = db.obtenerMunicipiosPorEstado(estadoSeleccionado.id)
                        val adapterMunicipios = ArrayAdapter(this@CrearPublicacionActivity, android.R.layout.simple_spinner_item, municipios.map { it.nombre })
                        spinnerMunicipio.adapter = adapterMunicipios
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {}
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        configurarSpinnerAnios()
        configurarSpinnerArregloTransmision()
        configurarSpinnerArregloCombustible()

        // Configurar los listeners para los LinearLayout de fotos
        binding.llFotografiaDiagonal.setOnClickListener { selectImage("diagonal") }
        binding.llFotografiaFrente.setOnClickListener { selectImage("frente") }
        binding.llFotografiaLadoDerecho.setOnClickListener { selectImage("lado_derecho") }
        binding.llFotografiaLadoIzquierdo.setOnClickListener { selectImage("lado_izquierdo") }
        binding.llFotografiaAtras.setOnClickListener { selectImage("atras") }

        // Configurar botón para guardar la publicación
        binding.btnCrearPublicacion.setOnClickListener { guardarPublicacion() }
    }

    fun configurarSpinnerAnios() {
        // Crear una lista de años (de 2024 a 1980)
        val opcionesIntent = mutableListOf<String>()
        for (i in 2024 downTo 1980) {
            opcionesIntent.add(i.toString())
        }

        val adapterArray = ArrayAdapter(this@CrearPublicacionActivity,
            android.R.layout.simple_spinner_item, opcionesIntent)

        adapterArray.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerAnio.adapter = adapterArray
    }

    fun configurarSpinnerArregloTransmision(){
        val opcionesIntent = arrayOf("Manual", "Automática")
        val adapterArray = ArrayAdapter(this@CrearPublicacionActivity,
            android.R.layout.simple_spinner_item, opcionesIntent)
        adapterArray.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerTransmision.adapter= adapterArray
    }

    fun configurarSpinnerArregloCombustible(){
        val opcionesIntent = arrayOf("Gasolina", "Diésel", "Electricidad")
        val adapterArray = ArrayAdapter(this@CrearPublicacionActivity,
            android.R.layout.simple_spinner_item, opcionesIntent)
        adapterArray.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerCombustible.adapter= adapterArray
    }

    // Navegación
    private fun irPantallaInicio() {
        val intent = Intent(this@CrearPublicacionActivity, InicioActivity::class.java)
        intent.putExtra("nombre", nombre)
        intent.putExtra("uid", uid)
        startActivity(intent)
    }

    fun irPantallaBusqueda() {
        val intent = Intent(this@CrearPublicacionActivity, BusquedaActivity::class.java)
        intent.putExtra("nombre", nombre)
        intent.putExtra("uid", uid)
        startActivity(intent)
    }

    fun irPantallaPerfil() {
        val intent = Intent(this@CrearPublicacionActivity, PerfilActivity::class.java)
        intent.putExtra("nombre", nombre)
        intent.putExtra("uid", uid)
        startActivity(intent)
    }

    fun irPantallaVenta() {
        val intent = Intent(this@CrearPublicacionActivity, VentaActivity::class.java)
        intent.putExtra("nombre", nombre)
        intent.putExtra("uid", uid)
        startActivity(intent)
    }

    fun irPantallaMisChats(){
        val intent = Intent(this@CrearPublicacionActivity,MisChatsActivity::class.java)
        intent.putExtra("nombre", nombre)
        intent.putExtra("uid", uid)
        startActivity(intent)
    }

    private fun selectImage(imageType: String) {
        val options = arrayOf<CharSequence>("Seleccionar desde galería", "Cancelar")
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Agregar Foto")
        builder.setItems(options) { dialog, item ->
            when (options[item]) {
                "Seleccionar desde galería" -> dispatchSelectPictureIntent(imageType)
                "Cancelar" -> dialog.dismiss()
            }
        }
        builder.show()
    }


    private fun dispatchSelectPictureIntent(imageType: String) {
        val selectPictureIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        selectPictureIntent.type = "image/*"
        startActivityForResult(selectPictureIntent, REQUEST_IMAGE_SELECT)
        currentImagePath = imageType
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_IMAGE_CAPTURE -> {
                    when (currentImagePath) {
                        "diagonal" -> imageUriDiagonal = Uri.fromFile(File(currentPhotoPath))
                        "frente" -> imageUriFrente = Uri.fromFile(File(currentPhotoPath))
                        "lado_derecho" -> imageUriLadoDerecho = Uri.fromFile(File(currentPhotoPath))
                        "lado_izquierdo" -> imageUriLadoIzquierdo = Uri.fromFile(File(currentPhotoPath))
                        "atras" -> imageUriAtras = Uri.fromFile(File(currentPhotoPath))
                    }
                }
                REQUEST_IMAGE_SELECT -> {
                    data?.data?.let { uri ->
                        when (currentImagePath) {
                            "diagonal" -> imageUriDiagonal = uri
                            "frente" -> imageUriFrente = uri
                            "lado_derecho" -> imageUriLadoDerecho = uri
                            "lado_izquierdo" -> imageUriLadoIzquierdo = uri
                            "atras" -> imageUriAtras = uri
                        }
                    }
                }
            }
        }
    }

    private fun guardarPublicacion() {
        val nombre = intent.getStringExtra("nombre")!!
        val uid = intent.getStringExtra("uid")!!

        val modelo = binding.etModeloCarro.text.toString()
        val marca = marcaCoche
        val anio = binding.spinnerAnio.selectedItem.toString()
        val transmision = binding.spinnerTransmision.selectedItem.toString()
        val combustible = binding.spinnerCombustible.selectedItem.toString()
        val precio = binding.etPrecioCarro.text.toString().toDoubleOrNull() ?: 0.0
        val km = binding.etKmCarro.text.toString().toDoubleOrNull() ?: 0.0

        val ubicacionPais = binding.spinnerPais.selectedItem.toString()
        val ubicacionEstado = binding.spinnerEstado.selectedItem.toString()
        val ubicacionMunicipio = binding.spinnerMunicipio.selectedItem.toString()

        // Generar un ID único para la publicación
        val postId = UUID.randomUUID().toString()

        // Crear mapa mutable con los datos de la publicación incluyendo postId
        val postData = mutableMapOf(
            "postId" to postId, // Aquí se agrega el postId
            "nombre" to nombre,
            "uid" to uid,
            "modelo" to modelo,
            "marca" to marca,
            "anio" to anio,
            "transmision" to transmision,
            "combustible" to combustible,
            "precio" to precio,
            "km" to km,
            "ubicacionPais" to ubicacionPais,
            "ubicacionEstado" to ubicacionEstado,
            "ubicacionMunicipio" to ubicacionMunicipio
            // Agregar más campos si es necesario, como las imágenes
        )

        // Referencia a la base de datos Firebase
        val databaseRef = FirebaseDatabase.getInstance().getReference("publicaciones").child(postId)

        // Guardar los datos en Firebase
        databaseRef.setValue(postData).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                uploadImages(postId) // Subir las imágenes después de guardar los datos
            } else {
                Toast.makeText(this, "Error al guardar la publicación", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun uploadImages(postId: String) {
        val storageRef = FirebaseStorage.getInstance().reference.child("publicaciones/$postId")

        val imageRefs = listOf(
            Pair(storageRef.child("diagonal.jpg"), imageUriDiagonal),
            Pair(storageRef.child("frente.jpg"), imageUriFrente),
            Pair(storageRef.child("lado_derecho.jpg"), imageUriLadoDerecho),
            Pair(storageRef.child("lado_izquierdo.jpg"), imageUriLadoIzquierdo),
            Pair(storageRef.child("atras.jpg"), imageUriAtras)
        )

        val imageUrls = mutableMapOf<String, String>()

        imageRefs.forEach { (ref, uri) ->
            uri?.let {
                ref.putFile(it).addOnSuccessListener { taskSnapshot ->
                    ref.downloadUrl.addOnSuccessListener { url ->
                        when (ref.name) {
                            "diagonal.jpg" -> imageUrls["imagenDiagonal"] = url.toString()
                            "frente.jpg" -> imageUrls["imagenFrente"] = url.toString()
                            "lado_derecho.jpg" -> imageUrls["imagenLado_derecho"] = url.toString()
                            "lado_izquierdo.jpg" -> imageUrls["imagenLado_izquierdo"] = url.toString()
                            "atras.jpg" -> imageUrls["imagenAtras"] = url.toString()
                        }

                        if (imageUrls.size == imageRefs.size) {
                            val databaseRef = FirebaseDatabase.getInstance().getReference("publicaciones").child(postId)
                            databaseRef.updateChildren(imageUrls as Map<String, Any>).addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    Toast.makeText(this, "Publicación guardada exitosamente", Toast.LENGTH_SHORT).show()

                                } else {
                                    Toast.makeText(this, "Error al guardar las URLs de las imágenes", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }
                }.addOnFailureListener { exception ->
                    Toast.makeText(this, "Error al subir la imagen ${ref.name}: ${exception.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
