package mx.uv.carma

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.FirebaseDatabase
import mx.uv.carma.databinding.ActivityAgendarCitaBinding
import mx.uv.carma.poko.Cita
import java.util.Calendar

class AgendarCitaActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAgendarCitaBinding
    private var uid = ""
    private var nombre = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityAgendarCitaBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        val view = binding.root
        setContentView(view)

        nombre = intent.getStringExtra("nombre")!!
        uid = intent.getStringExtra("uid")!!

        val uidUsuario = intent.getStringExtra("uidUsuario")
        val uidVendedor = intent.getStringExtra("uidVendedor")
        val nombreVendedor = intent.getStringExtra("vendedorNombre")

        binding.etFechaCita.setOnClickListener {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(this,
                { _, selectedYear, selectedMonth, selectedDay ->
                    val selectedDate = String.format("%02d/%02d/%04d", selectedDay, selectedMonth + 1, selectedYear)
                    binding.etFechaCita.setText(selectedDate)
                }, year, month, day)

            // Restringir selección a fechas futuras o actuales
            datePickerDialog.datePicker.minDate = c.timeInMillis
            datePickerDialog.show()
        }

        binding.etHoraCita.setOnClickListener {
            val c = Calendar.getInstance()
            val hour = c.get(Calendar.HOUR_OF_DAY)
            val minute = c.get(Calendar.MINUTE)

            val timePickerDialog = TimePickerDialog(this,
                { _, selectedHour, selectedMinute ->
                    val selectedTime = String.format("%02d:%02d", selectedHour, selectedMinute)
                    binding.etHoraCita.setText(selectedTime)
                }, hour, minute, true)
            timePickerDialog.show()
        }

        binding.btnAgendarCita.setOnClickListener {
            val nombreUsuario = binding.etNombreUsuario.text.toString()
            val fechaCita = binding.etFechaCita.text.toString()
            val horaCita = binding.etHoraCita.text.toString()
            val lugar = binding.etLugar.text.toString()
            val descripcion = binding.etDescripcionCita.text.toString()

            if (nombreUsuario.isNotEmpty() && fechaCita.isNotEmpty() && horaCita.isNotEmpty() && lugar.isNotEmpty() && descripcion.isNotEmpty()) {
                guardarCita(uidUsuario, uidVendedor, nombreUsuario, nombreVendedor, fechaCita, horaCita, lugar, descripcion)
                Toast.makeText(this@AgendarCitaActivity, "Cita guadada exitosamente.", Toast.LENGTH_SHORT).show()

            } else {
                Toast.makeText(this@AgendarCitaActivity, "Introduce todos los campos.", Toast.LENGTH_SHORT).show()
            }
        }

        binding.ivNavInicio.setOnClickListener {
            irPantallaInicio()
        }

        binding.ivNavBusqueda.setOnClickListener {
            irPantallaBusqueda()
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
    }

    private fun guardarCita(uidUsuario: String?, uidVendedor: String?, nombreUsuario: String, nombreVendedor: String?, fechaCita: String, horaCita: String, lugar: String, descripcion: String) {
        val database = FirebaseDatabase.getInstance()
        val citasRef = database.getReference("citas")

        val citaId = citasRef.push().key
        val cita = Cita(uidUsuario, uidVendedor, nombreUsuario, nombreVendedor, fechaCita, horaCita, lugar, descripcion)

        if (citaId != null) {
            citasRef.child(citaId).setValue(cita)
                .addOnSuccessListener {
                    // Cita guardada exitosamente
                }
                .addOnFailureListener {
                    // Error al guardar la cita
                }
        }
    }

    // Navegación:
    fun irPantallaInicio() {
        val intent = Intent(this@AgendarCitaActivity, InicioActivity::class.java)
        intent.putExtra("nombre", nombre)
        intent.putExtra("uid", uid)
        startActivity(intent)
    }

    fun irPantallaBusqueda() {
        val intent = Intent(this@AgendarCitaActivity, BusquedaActivity::class.java)
        intent.putExtra("nombre", nombre)
        intent.putExtra("uid", uid)
        startActivity(intent)
    }

    fun irPantallaPerfil() {
        val intent = Intent(this@AgendarCitaActivity, PerfilActivity::class.java)
        intent.putExtra("nombre", nombre)
        intent.putExtra("uid", uid)
        startActivity(intent)
    }

    fun irPantallaVenta() {
        val intent = Intent(this@AgendarCitaActivity, VentaActivity::class.java)
        intent.putExtra("nombre", nombre)
        intent.putExtra("uid", uid)
        startActivity(intent)
    }

    fun irPantallaMisChats(){
        val intent = Intent(this@AgendarCitaActivity,MisChatsActivity::class.java)
        intent.putExtra("nombre", nombre)
        intent.putExtra("uid", uid)
        startActivity(intent)
    }
}