package mx.uv.carma

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.AsyncTask
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
import mx.uv.carma.modelo.BusquedaBD
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import android.view.View
import android.widget.ImageView
import androidx.core.content.ContextCompat
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import mx.uv.carma.databinding.ActivityEditarPublicacionCocheBinding
import java.io.InputStream


class EditarPublicacionCocheActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditarPublicacionCocheBinding
    private lateinit var db: BusquedaBD
    private var uid = ""
    private var nombre = ""
    private var postId = ""
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
        binding = ActivityEditarPublicacionCocheBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        val view = binding.root
        setContentView(view)

        db = BusquedaBD(this)

        nombre = intent.getStringExtra("nombreUser")!!
        uid = intent.getStringExtra("uidUser")!!
        postId = intent.getStringExtra("postId")!!




        binding.llMarcaVw.setOnClickListener {
            marcaCoche = "Volkswagen"
            val drawableRes = R.drawable.rounded_corners_l_primary
            binding.llMarcaVw.setBackgroundResource(drawableRes)

            val colorRes = R.color.l_accent
            binding.tvMarcaVw.setTextColor(ContextCompat.getColor(this@EditarPublicacionCocheActivity, colorRes))

            val drawableResAccent = R.drawable.rounded_corners_busqueda_marca
            val colorResDark = R.color.l_primary_dark

            binding.llMarcaChevrolet.setBackgroundResource(drawableResAccent)
            binding.tvMarcaChevrolet.setTextColor(ContextCompat.getColor(this@EditarPublicacionCocheActivity, colorResDark))

            binding.llMarcaFord.setBackgroundResource(drawableResAccent)
            binding.tvMarcaFord.setTextColor(ContextCompat.getColor(this@EditarPublicacionCocheActivity, colorResDark))

            binding.llMarcaNissan.setBackgroundResource(drawableResAccent)
            binding.tvMarcaNissan.setTextColor(ContextCompat.getColor(this@EditarPublicacionCocheActivity, colorResDark))
        }

        binding.llMarcaChevrolet.setOnClickListener {
            marcaCoche = "Chevrolet"

            val drawableRes = R.drawable.rounded_corners_l_primary
            binding.llMarcaChevrolet.setBackgroundResource(drawableRes)

            val colorRes = R.color.l_accent
            binding.tvMarcaChevrolet.setTextColor(ContextCompat.getColor(this@EditarPublicacionCocheActivity, colorRes))

            val drawableResAccent = R.drawable.rounded_corners_busqueda_marca
            val colorResDark = R.color.l_primary_dark

            binding.llMarcaVw.setBackgroundResource(drawableResAccent)
            binding.tvMarcaVw.setTextColor(ContextCompat.getColor(this@EditarPublicacionCocheActivity, colorResDark))

            binding.llMarcaFord.setBackgroundResource(drawableResAccent)
            binding.tvMarcaFord.setTextColor(ContextCompat.getColor(this@EditarPublicacionCocheActivity, colorResDark))

            binding.llMarcaNissan.setBackgroundResource(drawableResAccent)
            binding.tvMarcaNissan.setTextColor(ContextCompat.getColor(this@EditarPublicacionCocheActivity, colorResDark))
        }

        binding.llMarcaFord.setOnClickListener {
            marcaCoche = "Ford"

            val drawableRes = R.drawable.rounded_corners_l_primary
            binding.llMarcaFord.setBackgroundResource(drawableRes)

            val colorRes = R.color.l_accent
            binding.tvMarcaFord.setTextColor(ContextCompat.getColor(this@EditarPublicacionCocheActivity, colorRes))

            val drawableResAccent = R.drawable.rounded_corners_busqueda_marca
            val colorResDark = R.color.l_primary_dark

            binding.llMarcaChevrolet.setBackgroundResource(drawableResAccent)
            binding.tvMarcaChevrolet.setTextColor(ContextCompat.getColor(this@EditarPublicacionCocheActivity, colorResDark))

            binding.llMarcaVw.setBackgroundResource(drawableResAccent)
            binding.tvMarcaVw.setTextColor(ContextCompat.getColor(this@EditarPublicacionCocheActivity, colorResDark))

            binding.llMarcaNissan.setBackgroundResource(drawableResAccent)
            binding.tvMarcaNissan.setTextColor(ContextCompat.getColor(this@EditarPublicacionCocheActivity, colorResDark))
        }

        binding.llMarcaNissan.setOnClickListener {
            marcaCoche = "Nissan"

            val drawableRes = R.drawable.rounded_corners_l_primary
            binding.llMarcaNissan.setBackgroundResource(drawableRes)

            val colorRes = R.color.l_accent
            binding.tvMarcaNissan.setTextColor(ContextCompat.getColor(this@EditarPublicacionCocheActivity, colorRes))

            val drawableResAccent = R.drawable.rounded_corners_busqueda_marca
            val colorResDark = R.color.l_primary_dark

            binding.llMarcaChevrolet.setBackgroundResource(drawableResAccent)
            binding.tvMarcaChevrolet.setTextColor(ContextCompat.getColor(this@EditarPublicacionCocheActivity, colorResDark))

            binding.llMarcaFord.setBackgroundResource(drawableResAccent)
            binding.tvMarcaFord.setTextColor(ContextCompat.getColor(this@EditarPublicacionCocheActivity, colorResDark))

            binding.llMarcaVw.setBackgroundResource(drawableResAccent)
            binding.tvMarcaVw.setTextColor(ContextCompat.getColor(this@EditarPublicacionCocheActivity, colorResDark))
        }


        configurarSpinners()
        configurarSpinnerAnios()
        configurarSpinnerArregloTransmision()
        configurarSpinnerArregloCombustible()


        // Configurar los listeners para los LinearLayout de fotos
        binding.llFotografiaDiagonal.setOnClickListener { selectImage("diagonal") }
        binding.llFotografiaFrente.setOnClickListener { selectImage("frente") }
        binding.llFotografiaLadoDerecho.setOnClickListener { selectImage("lado_derecho") }
        binding.llFotografiaLadoIzquierdo.setOnClickListener { selectImage("lado_izquierdo") }
        binding.llFotografiaAtras.setOnClickListener { selectImage("atras") }

        cargarDatosUsuario()

        // Configurar botón para guardar la publicación
        binding.btnCrearPublicacion.setOnClickListener { actualizarPublicacion() }

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

        binding.ivNavPerfil.setOnClickListener {
            irPantallaPerfil()
        }

    }

    // Navegación
    private fun irPantallaInicio() {
        val intent = Intent(this@EditarPublicacionCocheActivity, InicioActivity::class.java)
        intent.putExtra("nombre", nombre)
        intent.putExtra("uid", uid)
        startActivity(intent)
    }

    private fun irPantallaBusqueda() {
        val intent = Intent(this@EditarPublicacionCocheActivity, BusquedaActivity::class.java)
        intent.putExtra("nombre", nombre)
        intent.putExtra("uid", uid)
        startActivity(intent)
    }


    private fun irPantallaVenta() {
        val intent = Intent(this@EditarPublicacionCocheActivity, VentaActivity::class.java)
        intent.putExtra("nombre", nombre)
        intent.putExtra("uid", uid)
        startActivity(intent)
    }

    private fun irPantallaChats() {
        val intent = Intent(this@EditarPublicacionCocheActivity, MisChatsActivity::class.java)
        intent.putExtra("nombre", nombre)
        intent.putExtra("uid", uid)
        startActivity(intent)
    }

    private fun irPantallaPerfil() {
        val intent = Intent(this@EditarPublicacionCocheActivity, PerfilActivity::class.java)
        intent.putExtra("nombre", nombre)
        intent.putExtra("uid", uid)
        startActivity(intent)
    }



    private fun configurarSpinners() {


        val spinnerPais = binding.spinnerPais
        val spinnerEstado = binding.spinnerEstado
        val spinnerMunicipio = binding.spinnerMunicipio

// Obtener datos de ubicación del Intent
        val ubicacionPais = intent.getStringExtra("ubicacionPais")
        val ubicacionEstado = intent.getStringExtra("ubicacionEstado")
        val ubicacionMunicipio = intent.getStringExtra("ubicacionMunicipio")

// Llenar spinner de países
        val paises = db.obtenerPaises()
        val adapterPaises = ArrayAdapter(this, android.R.layout.simple_spinner_item, paises.map { it.nombre })
        spinnerPais.adapter = adapterPaises

// Seleccionar país inicial si existe
        ubicacionPais?.let {
            val posicionPais = paises.indexOfFirst { it.nombre == ubicacionPais }
            if (posicionPais != -1) {
                spinnerPais.setSelection(posicionPais)
            }
        }

        spinnerPais.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val paisSeleccionado = paises[position]
                val estados = db.obtenerEstadosPorPais(paisSeleccionado.id)
                val adapterEstados = ArrayAdapter(this@EditarPublicacionCocheActivity, android.R.layout.simple_spinner_item, estados.map { it.nombre })
                spinnerEstado.adapter = adapterEstados

                // Seleccionar estado inicial si existe
                ubicacionEstado?.let {
                    val posicionEstado = estados.indexOfFirst { it.nombre == ubicacionEstado }
                    if (posicionEstado != -1) {
                        spinnerEstado.setSelection(posicionEstado)
                    }
                }

                spinnerEstado.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                        val estadoSeleccionado = estados[position]
                        val municipios = db.obtenerMunicipiosPorEstado(estadoSeleccionado.id)
                        val adapterMunicipios = ArrayAdapter(this@EditarPublicacionCocheActivity, android.R.layout.simple_spinner_item, municipios.map { it.nombre })
                        spinnerMunicipio.adapter = adapterMunicipios

                        // Seleccionar municipio inicial si existe
                        ubicacionMunicipio?.let {
                            val posicionMunicipio = municipios.indexOfFirst { it.nombre == ubicacionMunicipio }
                            if (posicionMunicipio != -1) {
                                spinnerMunicipio.setSelection(posicionMunicipio)
                            }
                        }
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {}
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }


    }


    fun configurarSpinnerAnios() {
        // Crear una lista de años (de 2024 a 1980)
        val opcionesIntent = mutableListOf<String>()
        for (i in 2024 downTo 1980) {
            opcionesIntent.add(i.toString())
        }

        val adapterArray = ArrayAdapter(this@EditarPublicacionCocheActivity,
            android.R.layout.simple_spinner_item, opcionesIntent)

        adapterArray.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerAnio.adapter = adapterArray
    }

    fun configurarSpinnerArregloTransmision(){
        val opcionesIntent = arrayOf("Manual", "Automática")
        val adapterArray = ArrayAdapter(this@EditarPublicacionCocheActivity,
            android.R.layout.simple_spinner_item, opcionesIntent)
        adapterArray.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerTransmision.adapter= adapterArray
    }

    fun configurarSpinnerArregloCombustible(){
        val opcionesIntent = arrayOf("Gasolina", "Diésel", "Electricidad")
        val adapterArray = ArrayAdapter(this@EditarPublicacionCocheActivity,
            android.R.layout.simple_spinner_item, opcionesIntent)
        adapterArray.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerCombustible.adapter= adapterArray
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

    private fun cargarDatosUsuario() {

        // Obtener datos del intent
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
        val imagenAtras = intent.getStringExtra("imagenAtras")
        val imagenDiagonal = intent.getStringExtra("imagenDiagonal")
        val imagenFrente = intent.getStringExtra("imagenFrente")
        val imagenLadoDerecho = intent.getStringExtra("imagenLado_derecho")
        val imagenLadoIzquierdo = intent.getStringExtra("imagenLado_izquierdo")
        val uid = intent.getStringExtra("uid")
        val nombre = intent.getStringExtra("nombre")

        //Imagenes

        if (!imagenDiagonal.isNullOrEmpty()) {
            DownloadImageTask(binding.ivFotografiaDiagonal).execute(imagenDiagonal)
        }

        if (!imagenFrente.isNullOrEmpty()) {
            DownloadImageTask(binding.ivFotografiaFrente).execute(imagenFrente)
        }

        if (!imagenLadoDerecho.isNullOrEmpty()) {
            DownloadImageTask(binding.ivFotografiaLadoDerecho).execute(imagenLadoDerecho)
        }

        if (!imagenLadoIzquierdo.isNullOrEmpty()) {
            DownloadImageTask(binding.ivFotografiaLadoIzquierdo).execute(imagenLadoIzquierdo)
        }

        if (!imagenAtras.isNullOrEmpty()) {
            DownloadImageTask(binding.ivFotografiaAtras).execute(imagenAtras)
        }

        if(marca == "Volkswagen"){

            marcaCoche = marca

            val drawableRes = R.drawable.rounded_corners_l_primary
            binding.llMarcaVw.setBackgroundResource(drawableRes)

            val colorRes = R.color.l_accent
            binding.tvMarcaVw.setTextColor(ContextCompat.getColor(this@EditarPublicacionCocheActivity, colorRes))

            val drawableResAccent = R.drawable.rounded_corners_busqueda_marca
            val colorResDark = R.color.l_primary_dark

            binding.llMarcaChevrolet.setBackgroundResource(drawableResAccent)
            binding.tvMarcaChevrolet.setTextColor(ContextCompat.getColor(this@EditarPublicacionCocheActivity, colorResDark))

            binding.llMarcaFord.setBackgroundResource(drawableResAccent)
            binding.tvMarcaFord.setTextColor(ContextCompat.getColor(this@EditarPublicacionCocheActivity, colorResDark))

            binding.llMarcaNissan.setBackgroundResource(drawableResAccent)
            binding.tvMarcaNissan.setTextColor(ContextCompat.getColor(this@EditarPublicacionCocheActivity, colorResDark))

        }else{
            if(marca == "Chevrolet"){

                marcaCoche = marca

                val drawableRes = R.drawable.rounded_corners_l_primary
                binding.llMarcaChevrolet.setBackgroundResource(drawableRes)

                val colorRes = R.color.l_accent
                binding.tvMarcaChevrolet.setTextColor(ContextCompat.getColor(this@EditarPublicacionCocheActivity, colorRes))

                val drawableResAccent = R.drawable.rounded_corners_busqueda_marca
                val colorResDark = R.color.l_primary_dark

                binding.llMarcaVw.setBackgroundResource(drawableResAccent)
                binding.tvMarcaVw.setTextColor(ContextCompat.getColor(this@EditarPublicacionCocheActivity, colorResDark))

                binding.llMarcaFord.setBackgroundResource(drawableResAccent)
                binding.tvMarcaFord.setTextColor(ContextCompat.getColor(this@EditarPublicacionCocheActivity, colorResDark))

                binding.llMarcaNissan.setBackgroundResource(drawableResAccent)
                binding.tvMarcaNissan.setTextColor(ContextCompat.getColor(this@EditarPublicacionCocheActivity, colorResDark))

            }else{
                if(marca == "Ford"){

                    marcaCoche = marca

                    val drawableRes = R.drawable.rounded_corners_l_primary
                    binding.llMarcaFord.setBackgroundResource(drawableRes)

                    val colorRes = R.color.l_accent
                    binding.tvMarcaFord.setTextColor(ContextCompat.getColor(this@EditarPublicacionCocheActivity, colorRes))

                    val drawableResAccent = R.drawable.rounded_corners_busqueda_marca
                    val colorResDark = R.color.l_primary_dark

                    binding.llMarcaChevrolet.setBackgroundResource(drawableResAccent)
                    binding.tvMarcaChevrolet.setTextColor(ContextCompat.getColor(this@EditarPublicacionCocheActivity, colorResDark))

                    binding.llMarcaVw.setBackgroundResource(drawableResAccent)
                    binding.tvMarcaVw.setTextColor(ContextCompat.getColor(this@EditarPublicacionCocheActivity, colorResDark))

                    binding.llMarcaNissan.setBackgroundResource(drawableResAccent)
                    binding.tvMarcaNissan.setTextColor(ContextCompat.getColor(this@EditarPublicacionCocheActivity, colorResDark))

                }else{
                    if(marca == "Nissan"){

                        marcaCoche = marca

                        val drawableRes = R.drawable.rounded_corners_l_primary
                        binding.llMarcaNissan.setBackgroundResource(drawableRes)

                        val colorRes = R.color.l_accent
                        binding.tvMarcaNissan.setTextColor(ContextCompat.getColor(this@EditarPublicacionCocheActivity, colorRes))

                        val drawableResAccent = R.drawable.rounded_corners_busqueda_marca
                        val colorResDark = R.color.l_primary_dark

                        binding.llMarcaChevrolet.setBackgroundResource(drawableResAccent)
                        binding.tvMarcaChevrolet.setTextColor(ContextCompat.getColor(this@EditarPublicacionCocheActivity, colorResDark))

                        binding.llMarcaFord.setBackgroundResource(drawableResAccent)
                        binding.tvMarcaFord.setTextColor(ContextCompat.getColor(this@EditarPublicacionCocheActivity, colorResDark))

                        binding.llMarcaVw.setBackgroundResource(drawableResAccent)
                        binding.tvMarcaVw.setTextColor(ContextCompat.getColor(this@EditarPublicacionCocheActivity, colorResDark))

                    }
                }
            }
        }

        binding.etModeloCarro.setText(modelo)
        binding.etPrecioCarro.setText(precio)
        binding.etKmCarro.setText(km)

        // Asignar valor a los Spinners
        anio?.let {
            val anioPosition = (binding.spinnerAnio.adapter as ArrayAdapter<String>).getPosition(it)
            binding.spinnerAnio.setSelection(anioPosition)
        }

        transmision?.let {
            val transmisionPosition = (binding.spinnerTransmision.adapter as ArrayAdapter<String>).getPosition(it)
            binding.spinnerTransmision.setSelection(transmisionPosition)
        }

        combustible?.let {
            val combustiblePosition = (binding.spinnerCombustible.adapter as ArrayAdapter<String>).getPosition(it)
            binding.spinnerCombustible.setSelection(combustiblePosition)
        }


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

    private fun actualizarPublicacion() {
        // Obtener los valores editados del formulario
        val modelo = binding.etModeloCarro.text.toString().trim()
        val precioStr = binding.etPrecioCarro.text.toString().trim()
        val kmStr = binding.etKmCarro.text.toString().trim()
        val anio = binding.spinnerAnio.selectedItem.toString()
        val transmision = binding.spinnerTransmision.selectedItem.toString()
        val combustible = binding.spinnerCombustible.selectedItem.toString()
        val ubicacionPais = binding.spinnerPais.selectedItem.toString()
        val ubicacionEstado = binding.spinnerEstado.selectedItem.toString()
        val ubicacionMunicipio = binding.spinnerMunicipio.selectedItem.toString()


        // Verificar si hay alguna imagen nueva seleccionada
        val hayNuevaImagenSeleccionada = imageUriDiagonal != null || imageUriFrente != null ||
                imageUriLadoDerecho != null || imageUriLadoIzquierdo != null ||
                imageUriAtras != null

        // Si no hay ninguna imagen nueva seleccionada y no se ha modificado nada más, salir sin hacer nada
        if (!hayNuevaImagenSeleccionada && modelo.isEmpty() && marcaCoche.isEmpty() && precioStr.isEmpty() && kmStr.isEmpty() &&
            anio.isEmpty() && transmision.isEmpty() && combustible.isEmpty() && ubicacionPais.isEmpty() &&
            ubicacionEstado.isEmpty() && ubicacionMunicipio.isEmpty()
        ) {
            Toast.makeText(this, "No se han realizado cambios", Toast.LENGTH_SHORT).show()
            return
        }

        // Actualizar los datos en la base de datos si hay cambios
        val databaseRef = FirebaseDatabase.getInstance().getReference("publicaciones").child(postId)
        val publicacionActualizada = hashMapOf<String, Any>()

        // Agregar solo los campos que se han modificado
        if (modelo.isNotEmpty()) {
            publicacionActualizada["modelo"] = modelo
        }

        if (modelo.isNotEmpty()) {
            publicacionActualizada["marca"] = marcaCoche
        }

        if (precioStr.isNotEmpty()) {
            val precio = precioStr.toDoubleOrNull() ?: 0.0 // Convertir a Double o usar 0.0 si no se puede convertir
            publicacionActualizada["precio"] = precio
        }
        if (kmStr.isNotEmpty()) {
            val km = kmStr.toDoubleOrNull() ?: 0.0 // Convertir a Double o usar 0.0 si no se puede convertir
            publicacionActualizada["km"] = km
        }
        if (anio.isNotEmpty()) {
            publicacionActualizada["anio"] = anio
        }
        if (transmision.isNotEmpty()) {
            publicacionActualizada["transmision"] = transmision
        }
        if (combustible.isNotEmpty()) {
            publicacionActualizada["combustible"] = combustible
        }
        if (ubicacionPais.isNotEmpty()) {
            publicacionActualizada["ubicacionPais"] = ubicacionPais
        }
        if (ubicacionEstado.isNotEmpty()) {
            publicacionActualizada["ubicacionEstado"] = ubicacionEstado
        }
        if (ubicacionMunicipio.isNotEmpty()) {
            publicacionActualizada["ubicacionMunicipio"] = ubicacionMunicipio
        }

        // Si se han realizado cambios en los datos, actualizar en la base de datos
        if (publicacionActualizada.isNotEmpty()) {
            databaseRef.updateChildren(publicacionActualizada).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Datos actualizados exitosamente", Toast.LENGTH_SHORT).show()
                    // Si se han cambiado las imágenes, subirlas
                    if (hayNuevaImagenSeleccionada) {
                        uploadImages(postId)
                    }
                } else {
                    Toast.makeText(this, "Error al actualizar la publicación", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            // Si no se han realizado cambios en los datos
            Toast.makeText(this, "No se han realizado cambios", Toast.LENGTH_SHORT).show()
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

    private class DownloadImageTask(private val imageView: ImageView) : AsyncTask<String, Void, Bitmap>() {
        override fun doInBackground(vararg urls: String): Bitmap? {
            val url = urls[0]
            return try {
                val `in`: InputStream = java.net.URL(url).openStream()
                BitmapFactory.decodeStream(`in`)
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }

        override fun onPostExecute(result: Bitmap?) {
            result?.let { imageView.setImageBitmap(it) }
        }
    }
}
