package mx.uv.carma

import android.content.Intent
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.MotionEvent
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

import mx.uv.carma.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.FirebaseApp
import kotlin.reflect.KMutableProperty0
import com.google.firebase.database.FirebaseDatabase

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var mAuth: FirebaseAuth
    private var passwordActualVisible = false
    var correo = ""
    var contra = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        // Inicializar Firebase Auth
        mAuth = FirebaseAuth.getInstance()

        binding.tvRecuperarContra.setOnClickListener {
            val intent = Intent(this, RecuperarContraActivity::class.java)
            startActivity(intent)
        }

        binding.tvRegistroCuenta.setOnClickListener {
            val intent = Intent(this, RegistroActivity::class.java)
            startActivity(intent)
        }

        binding.btnIngreso.setOnClickListener {
            correo = binding.etCorreo.text.toString()
            contra = binding.etContra.text.toString()

            if (correo.isNotEmpty() && contra.isNotEmpty()) {
                ingresarUsuario(correo, contra)
            } else {
                Toast.makeText(this, "Por favor, llena todos los campos", Toast.LENGTH_SHORT).show()
            }
        }


        setupPasswordVisibilityToggle(binding.etContra, ::passwordActualVisible)

    }

    private fun setupPasswordVisibilityToggle(editText: EditText, visibilityState: KMutableProperty0<Boolean>) {
        editText.setOnTouchListener { v, event ->
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


    private fun ingresarUsuario(correo: String, contra: String) {
        mAuth.signInWithEmailAndPassword(correo, contra).addOnCompleteListener { task ->
            if (task.isSuccessful) {

                val user = mAuth.currentUser
                user?.let {
                    val uid = user.uid


                    val db = FirebaseDatabase.getInstance().getReference("Users").child(uid)


                    db.get().addOnSuccessListener { snapshot ->
                        if (snapshot.exists()) {
                            val nombre = snapshot.child("nombre").value?.toString() ?: "Usuario"

                            val intent = Intent(this@LoginActivity, InicioActivity::class.java)
                            intent.putExtra("nombre", nombre)
                            intent.putExtra("uid", uid)
                            startActivity(intent)
                            finish()
                            Toast.makeText(this, "Bienvenido:  $nombre", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this, "Usuario no encontrado", Toast.LENGTH_SHORT).show()
                        }
                    }.addOnFailureListener { exception ->
                        Toast.makeText(this, "Error al obtener el nombre", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this, "Credenciales incorrectas", Toast.LENGTH_SHORT).show()
            }
        }
    }


}