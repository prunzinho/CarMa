package mx.uv.carma


import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import mx.uv.carma.databinding.ActivityRecuperarContraBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlin.random.Random

class RecuperarContraActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRecuperarContraBinding
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDatabase: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRecuperarContraBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        // Inicializar Firebase Auth y Database
        mAuth = FirebaseAuth.getInstance()
        mDatabase = FirebaseDatabase.getInstance().reference

        binding.btnRecuperarContra.setOnClickListener {
            val correo = binding.etCorreo.text.toString()

            if (correo.isNotEmpty()) {
                verificarCorreo(correo)
            } else {
                Toast.makeText(this, "Por favor, ingresa tu correo", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun verificarCorreo(correo: String) {
        mDatabase.child("Users").orderByChild("correo").equalTo(correo)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        // El correo existe en la base de datos
                        Toast.makeText(this@RecuperarContraActivity, "Correo encontrado, puedes proceder con la recuperación.", Toast.LENGTH_SHORT).show()
                        val codigo = generarCodigo()
                        enviarCodigoPorCorreo(correo, codigo)
                    } else {

                        Toast.makeText(this@RecuperarContraActivity, "Correo no encontrado, por favor verifica.", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e(TAG, "Error en la consulta de la base de datos: ${error.message}")
                }
            })
    }

    private fun generarCodigo(): String {
        val codigo = Random.nextInt(100000, 999999).toString()
        Log.d(TAG, "Código generado: $codigo")
        return codigo
    }

    private fun enviarCodigoPorCorreo(correo: String, codigo: String) {
        val subject = "Recuperación de contraseña"
        val body = "Hola,Tu código de recuperación es: $codigo"
        Toast.makeText(this@RecuperarContraActivity, body, Toast.LENGTH_SHORT).show()
        EmailUtil.sendEmail(correo, subject, body)
    }

    companion object {
        private const val TAG = "RecuperarContraActivity"
    }
}
