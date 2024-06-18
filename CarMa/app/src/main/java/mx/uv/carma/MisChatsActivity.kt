package mx.uv.carma


import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import mx.uv.carma.databinding.ActivityMisChatsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import mx.uv.carma.adaptadores.UserAdapter
import mx.uv.carma.modelo.User

class MisChatsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMisChatsBinding
    private lateinit var userRecyclerView: RecyclerView
    private lateinit var userList: ArrayList<User>
    private lateinit var adapter: UserAdapter
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDbRef: DatabaseReference
    private var uid = ""
    private var nombre = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        nombre = intent.getStringExtra("nombre")!!
        uid = intent.getStringExtra("uid")!!

        mAuth = FirebaseAuth.getInstance()
        mDbRef = FirebaseDatabase.getInstance().getReference()

        binding = ActivityMisChatsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        userList = ArrayList()
        adapter = UserAdapter(this, userList,uid,nombre)

        userRecyclerView = findViewById(R.id.userRecyclerView)

        userRecyclerView.layoutManager = LinearLayoutManager(this)
        userRecyclerView.adapter = adapter

        // Navegación
        binding.ivNavInicio.setOnClickListener { irPantallaInicio() }
        binding.ivNavBusqueda.setOnClickListener { irPantallaBusqueda() }
        binding.ivNavPerfil.setOnClickListener { irPantallaPerfil() }
        binding.ivNavVenta.setOnClickListener { irPantallaVenta() }

        val currentUserUid = mAuth.currentUser?.uid

        if (currentUserUid != null) {
            mDbRef.child("UsersChats").child(currentUserUid).addValueEventListener(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    userList.clear()
                    for (postSnapshot in snapshot.children) {
                        val chatUserUid = postSnapshot.key
                        mDbRef.child("Users").child(chatUserUid!!).addListenerForSingleValueEvent(object: ValueEventListener {
                            override fun onDataChange(userSnapshot: DataSnapshot) {
                                val chatUser = userSnapshot.getValue(User::class.java)
                                if (chatUser != null) {
                                    userList.add(chatUser)
                                    adapter.notifyDataSetChanged()
                                }

                                /*if(userList.isEmpty()){
                                    binding.tvNoChats.visibility = View.VISIBLE
                                    binding.userRecyclerView.visibility = View.GONE
                                }else{
                                    binding.tvNoChats.visibility = View.GONE
                                    binding.userRecyclerView.visibility = View.VISIBLE
                                }*/

                            }

                            override fun onCancelled(error: DatabaseError) {

                            }
                        })
                    }


                }

                override fun onCancelled(error: DatabaseError) {

                }
            })
        }
    }

    // Navegación
    private fun irPantallaInicio() {
        val intent = Intent(this@MisChatsActivity, InicioActivity::class.java)
        intent.putExtra("nombre", nombre)
        intent.putExtra("uid", uid)
        startActivity(intent)
    }

    fun irPantallaBusqueda() {
        val intent = Intent(this@MisChatsActivity, BusquedaActivity::class.java)
        intent.putExtra("nombre", nombre)
        intent.putExtra("uid", uid)
        startActivity(intent)
    }

    fun irPantallaPerfil() {
        val intent = Intent(this@MisChatsActivity, PerfilActivity::class.java)
        intent.putExtra("nombre", nombre)
        intent.putExtra("uid", uid)
        startActivity(intent)
    }

    fun irPantallaVenta() {
        val intent = Intent(this@MisChatsActivity, VentaActivity::class.java)
        intent.putExtra("nombre", nombre)
        intent.putExtra("uid", uid)
        startActivity(intent)
    }



}
