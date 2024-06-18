package mx.uv.carma


import android.app.DatePickerDialog
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.MotionEvent
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import mx.uv.carma.databinding.ActivityRegistroBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.util.Calendar
import kotlin.reflect.KMutableProperty0

class RegistroActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegistroBinding
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDatabase: DatabaseReference
    private var passwordActualVisible = false

    val c = Calendar.getInstance()
    val year = c.get(Calendar.YEAR)
    val month = c.get(Calendar.MONTH)
    val day = c.get(Calendar.DAY_OF_MONTH)

    private var correo = ""
    private var nombre = ""
    private var fecha = ""
    private var contra = ""




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRegistroBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        // Inicializar Firebase Auth
        mAuth = FirebaseAuth.getInstance()
        mDatabase = FirebaseDatabase.getInstance().reference

        binding.btnRegistro.setOnClickListener {
            // Recuperar la información de los EditText
            correo = binding.etCorreo.text.toString()
            nombre = binding.etNombreUsuario.text.toString()
            fecha = binding.dateText.text.toString()
            contra = binding.etContra.text.toString()

            // Validar que todos los campos estén llenados
            if (correo.isNotEmpty() && nombre.isNotEmpty() && fecha.isNotEmpty() && contra.isNotEmpty()) {
                if (contra.length >= 6) {
                    // Verificar si el correo ya existe antes de registrar
                    verificarCorreo(correo)
                } else {
                    Toast.makeText(this, "La contraseña tiene que contar con más de 6 caracteres", Toast.LENGTH_SHORT).show()
                }
            } else {
                // Mostrar un Toast si algún campo está vacío
                Toast.makeText(this, "Por favor, llena todos los campos", Toast.LENGTH_SHORT).show()
            }
        }

        binding.tvInicioMain.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }


        binding.dateText.setOnClickListener {
            mostrarDatePickerDialog()
        }

        setupPasswordVisibilityToggle(binding.etContra, ::passwordActualVisible)

    }

    private fun mostrarDatePickerDialog() {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(this,
            { _, selectedYear, selectedMonth, selectedDay ->
                val selectedCalendar = Calendar.getInstance()
                selectedCalendar.set(selectedYear, selectedMonth, selectedDay)

                val todayCalendar = Calendar.getInstance()

                if (selectedCalendar.after(todayCalendar)) {
                    // La fecha seleccionada es posterior a la fecha actual
                    Toast.makeText(this, "No puedes seleccionar fechas posteriores al día de hoy", Toast.LENGTH_SHORT).show()
                } else {
                    // La fecha seleccionada es válida, la puedes usar
                    val selectedDate = String.format("%02d/%02d/%04d", selectedDay, selectedMonth + 1, selectedYear)
                    binding.dateText.setText(selectedDate)
                }
            }, year, month, day)

        // Establecer la fecha mínima como la fecha de hoy
        datePickerDialog.datePicker.maxDate = System.currentTimeMillis()

        datePickerDialog.show()
    }

    private fun verificarCorreo(correo: String) {
        mDatabase.child("Users").orderByChild("correo").equalTo(correo)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        Toast.makeText(this@RegistroActivity, "El correo ya está registrado.", Toast.LENGTH_SHORT).show()
                    } else {
                        registrarNuevoUsuario()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e(TAG, "Error en la consulta de la base de datos: ${error.message}")
                }
            })
    }

    private fun registrarNuevoUsuario() {
        mAuth.createUserWithEmailAndPassword(correo, contra).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val id = mAuth.currentUser!!.uid

                val userMap = HashMap<String, Any>()
                userMap["correo"] = correo
                userMap["nombre"] = nombre
                userMap["fecha"] = fecha
                userMap["contra"] = contra
                userMap["uid"]= id

                Log.d(TAG, "Datos del usuario: $userMap")

                mDatabase.child("Users").child(id).setValue(userMap)
                    .addOnCompleteListener { dbTask ->
                        if (dbTask.isSuccessful) {
                            val intent = Intent(this, LoginActivity::class.java)
                            startActivity(intent)
                            finish()
                            Toast.makeText(this, "Registro exitoso", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this, "Error al guardar los datos: ${dbTask.exception?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
            } else {
                Toast.makeText(this, "No se pudo completar el registro: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
            }
        }
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


}