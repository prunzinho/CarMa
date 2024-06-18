package mx.uv.carma

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.provider.MediaStore
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.MotionEvent
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import mx.uv.carma.databinding.ActivityEditarPerfilBinding
import java.io.InputStream
import java.util.Calendar
import kotlin.reflect.KMutableProperty0

class EditarPerfilActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditarPerfilBinding
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDatabase: DatabaseReference
    private lateinit var mStorage: FirebaseStorage
    private var uid = ""
    private var nombreActual = ""
    private var fechaActual = ""
    private lateinit var contrasenia: String
    private var selectedImageUri: Uri? = null

    private var passwordActualVisible = false
    private var passwordNuevaVisible = false
    private var passwordConfirmarVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditarPerfilBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mAuth = FirebaseAuth.getInstance()
        mDatabase = FirebaseDatabase.getInstance().reference
        mStorage = FirebaseStorage.getInstance()
        uid = intent.getStringExtra("uid")!!

        cargarDatosUsuario()

        binding.etFechaNacimiento.setOnClickListener {
            mostrarDatePickerDialog()
        }

        setupPasswordVisibilityToggle(binding.etPasswordActual, ::passwordActualVisible)
        setupPasswordVisibilityToggle(binding.etPasswordNueva, ::passwordNuevaVisible)
        setupPasswordVisibilityToggle(binding.etPasswordConfirmar, ::passwordConfirmarVisible)

        binding.tvEditarFoto.setOnClickListener {
            seleccionarImagen()
        }

        binding.btnEditarPerfil.setOnClickListener {
            actualizarPerfil()
        }

        // Navegación
        binding.ivNavInicio.setOnClickListener { irPantallaInicio() }
        binding.ivNavBusqueda.setOnClickListener { irPantallaBusqueda() }
        binding.ivNavPerfil.setOnClickListener { irPantallaPerfil() }
        binding.ivNavVenta.setOnClickListener { irPantallaVenta() }
        binding.ivNavChats.setOnClickListener { irPantallaMisChats() }
    }

    private fun cargarDatosUsuario() {
        val userRef = mDatabase.child("Users").child(uid)
        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                nombreActual = snapshot.child("nombre").getValue(String::class.java).toString()
                fechaActual = snapshot.child("fecha").getValue(String::class.java).toString()
                val correo = snapshot.child("correo").getValue(String::class.java)
                contrasenia = snapshot.child("contra").getValue(String::class.java).toString()
                val fotoUrl = snapshot.child("fotoUrl").getValue(String::class.java)
                binding.etNombreUsuario.setText(nombreActual)
                binding.etFechaNacimiento.setText(fechaActual)

                if (!fotoUrl.isNullOrEmpty()) {
                    DownloadImageTask(binding.ivImgPerfilUsuario).execute(fotoUrl)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@EditarPerfilActivity, "Error al cargar datos.", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun mostrarDatePickerDialog() {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(this,
            { _, selectedYear, selectedMonth, selectedDay ->
                val selectedDate = String.format("%02d/%02d/%04d", selectedDay, selectedMonth + 1, selectedYear)
                binding.etFechaNacimiento.setText(selectedDate)
            }, year, month, day)
        datePickerDialog.show()
    }

    private fun setupPasswordVisibilityToggle(editText: EditText, visibilityState: KMutableProperty0<Boolean>) {
        editText.setOnTouchListener { _, event ->
            val DRAWABLE_END = 2
            if (event.action == MotionEvent.ACTION_UP) {
                if (event.rawX >= (editText.right - editText.compoundDrawables[DRAWABLE_END].bounds.width())) {
                    visibilityState.set(!visibilityState.get())
                    togglePasswordVisibility(editText, visibilityState.get())
                    return@setOnTouchListener true
                }
            }
            false
        }
    }

    private fun togglePasswordVisibility(editText: EditText, isVisible: Boolean) {
        if (isVisible) {
            editText.transformationMethod = HideReturnsTransformationMethod.getInstance()
            editText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_ojo, 0)
        } else {
            editText.transformationMethod = PasswordTransformationMethod.getInstance()
            editText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_ojo_cerrado, 0)
        }
        editText.setSelection(editText.text.length)
    }

    private fun seleccionarImagen() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, 1)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == Activity.RESULT_OK && data != null) {
            selectedImageUri = data.data
            binding.ivImgPerfilUsuario.setImageURI(selectedImageUri)
        }
    }

    private fun actualizarPerfil() {
        val nombreNuevo = binding.etNombreUsuario.text.toString()
        val fechaNueva = binding.etFechaNacimiento.text.toString()
        val passwordActual = binding.etPasswordActual.text.toString()
        val passwordNueva = binding.etPasswordNueva.text.toString()
        val passwordConfirmar = binding.etPasswordConfirmar.text.toString()

        var cambiosRealizados = false

        // Crear un mapa para almacenar solo los campos que se van a actualizar
        val updates = mutableMapOf<String, Any>()

        // Verificar y añadir cambios de la foto de perfil
        if (selectedImageUri != null) {
            val ref = mStorage.reference.child("profile_images/$uid.jpg")
            ref.putFile(selectedImageUri!!).addOnSuccessListener {
                ref.downloadUrl.addOnSuccessListener { downloadUrl ->
                    val imageUrl = downloadUrl.toString()
                    updates["fotoUrl"] = imageUrl
                    // Llamar a la función para actualizar los datos después de subir la imagen
                    actualizarDatosUsuario(updates)
                }
            }.addOnFailureListener {
                Toast.makeText(this, "Error al subir la imagen.", Toast.LENGTH_SHORT).show()
            }
            cambiosRealizados = true
        }

        // Verificar y añadir cambios del nombre
        if (nombreNuevo.isNotEmpty() && nombreNuevo != nombreActual) {
            updates["nombre"] = nombreNuevo
            cambiosRealizados = true
        }

        // Verificar y añadir cambios de la fecha de nacimiento
        if (fechaNueva.isNotEmpty() && fechaNueva != fechaActual) {
            updates["fecha"] = fechaNueva
            cambiosRealizados = true
        }

        // Verificar y añadir cambios de la contraseña
        if (passwordActual.isNotEmpty() && passwordNueva.isNotEmpty() && passwordConfirmar.isNotEmpty()) {
            if (passwordActual == contrasenia) {
                if (passwordNueva == passwordConfirmar) {
                    val user = mAuth.currentUser!!
                    val credential = EmailAuthProvider.getCredential(user.email!!, passwordActual)

                    user.reauthenticate(credential).addOnCompleteListener {
                        if (it.isSuccessful) {
                            user.updatePassword(passwordNueva).addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    updates["contra"] = passwordNueva
                                    actualizarDatosUsuario(updates)
                                    Toast.makeText(this, "Se han actualizado los datos correctamente", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(this, "Error al actualizar la contraseña.", Toast.LENGTH_SHORT).show()
                                }
                            }
                        } else {
                            Toast.makeText(this, "Autenticación fallida. Verifica tu contraseña actual.", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(this, "Las contraseñas nuevas no coinciden.", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Contraseña actual incorrecta.", Toast.LENGTH_SHORT).show()
            }
            cambiosRealizados = true
        }

        // Si hubo algún cambio y no es de contraseña, actualizar datos
        if (cambiosRealizados && updates.isNotEmpty() && passwordActual.isEmpty() && passwordNueva.isEmpty() && passwordConfirmar.isEmpty()) {
            actualizarDatosUsuario(updates)
        }

        if (!cambiosRealizados) {
            Toast.makeText(this, "No se han realizado cambios.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun actualizarDatosUsuario(updates: Map<String, Any>) {
        mDatabase.child("Users").child(uid).updateChildren(updates).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(this, "Perfil actualizado correctamente.", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Error al actualizar el perfil.", Toast.LENGTH_SHORT).show()
            }
        }

        irPantallaPerfil()
    }

    // Navegación
    private fun irPantallaInicio() {
        val intent = Intent(this@EditarPerfilActivity, InicioActivity::class.java)
        intent.putExtra("nombre", nombreActual)
        intent.putExtra("uid", uid)
        startActivity(intent)
    }

    fun irPantallaBusqueda() {
        val intent = Intent(this@EditarPerfilActivity, BusquedaActivity::class.java)
        intent.putExtra("nombre", nombreActual)
        intent.putExtra("uid", uid)
        startActivity(intent)
    }

    fun irPantallaPerfil() {
        val intent = Intent(this@EditarPerfilActivity, PerfilActivity::class.java)
        intent.putExtra("nombre", nombreActual)
        intent.putExtra("uid", uid)
        startActivity(intent)
    }

    fun irPantallaVenta() {
        val intent = Intent(this@EditarPerfilActivity, VentaActivity::class.java)
        intent.putExtra("nombre", nombreActual)
        intent.putExtra("uid", uid)
        startActivity(intent)
    }

    fun irPantallaMisChats(){
        val intent = Intent(this@EditarPerfilActivity,MisChatsActivity::class.java)
        intent.putExtra("nombre", nombreActual)
        intent.putExtra("uid", uid)
        startActivity(intent)
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
