package mx.uv.carma

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import mx.uv.carma.databinding.ActivityChatBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import mx.uv.carma.adaptadores.MessageAdapter
import mx.uv.carma.modelo.Message

class ChatActivity : AppCompatActivity() {


    private lateinit var binding:ActivityChatBinding
    private lateinit var chatRecyclerView: RecyclerView
    private lateinit var messageBox:EditText
    private lateinit var sendButton:ImageView
    private lateinit var messageAdapter: MessageAdapter
    private lateinit var messageList: ArrayList<Message>
    private lateinit var mDbRef:DatabaseReference

    private var uid = ""
    private var nombre = ""

    var receiverRoom:String?=null
    var senderRoom:String?=null

    var idUsuario = ""
    var idVendedor = ""

    var nombreU = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityChatBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)



        val name = intent.getStringExtra("name")
        val receiverUid = intent.getStringExtra("uid")

        nombre = intent.getStringExtra("nameU")!!
        uid = intent.getStringExtra("uidU")!!

        nombreU = name.toString()

        val senderUid = FirebaseAuth.getInstance().currentUser?.uid
        mDbRef = FirebaseDatabase.getInstance().getReference()

        idUsuario = senderUid.toString()
        idVendedor = receiverUid.toString()

        senderRoom = receiverUid + senderUid
        receiverRoom = senderUid + receiverUid

        supportActionBar?.title = name

        chatRecyclerView = binding.chatRecyclerView
        messageBox = binding.messageBox
        sendButton = binding.sendButton
        messageList = ArrayList()
        messageAdapter= MessageAdapter(this,messageList)

        chatRecyclerView.layoutManager = LinearLayoutManager(this)
        chatRecyclerView.adapter = messageAdapter

        //logica para el recycle
        mDbRef.child("chats").child(senderRoom!!).child("messages")
            .addValueEventListener(object:ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {

                    messageList.clear()

                    for(postSnapshot in snapshot.children){

                        val message = postSnapshot.getValue(Message::class.java)
                        messageList.add(message!!)


                    }

                    messageAdapter.notifyDataSetChanged()

                }

                override fun onCancelled(error: DatabaseError) {

                }

            })

        // añadir el mensaje a la base
        sendButton.setOnClickListener{
            val message = messageBox.text.toString()
            val messageObject = Message(message, senderUid)

            mDbRef.child("chats").child(senderRoom!!).child("messages").push()
                .setValue(messageObject).addOnSuccessListener {
                    mDbRef.child("chats").child(receiverRoom!!).child("messages").push()
                        .setValue(messageObject)
                }

            // Añadir los uids a la lista de chats
            mDbRef.child("UsersChats").child(senderUid!!).child(receiverUid!!).setValue(true)
            mDbRef.child("UsersChats").child(receiverUid!!).child(senderUid!!).setValue(true)

            messageBox.setText("")
        }

        binding.agendaLinearLayout.setOnClickListener {

            irPantallaAgendarCita()

        }

        // Navegación
        binding.ivNavInicio.setOnClickListener { irPantallaInicio() }
        binding.ivNavBusqueda.setOnClickListener { irPantallaBusqueda() }
        binding.ivNavPerfil.setOnClickListener { irPantallaPerfil() }
        binding.ivNavVenta.setOnClickListener { irPantallaVenta() }
        binding.ivNavChats.setOnClickListener { irPantallaMisChats() }

    }

    // Navegación
    private fun irPantallaInicio() {
        val intent = Intent(this@ChatActivity, InicioActivity::class.java)
        intent.putExtra("nombre", nombre)
        intent.putExtra("uid", uid)
        startActivity(intent)
    }

    fun irPantallaBusqueda() {
        val intent = Intent(this@ChatActivity, BusquedaActivity::class.java)
        intent.putExtra("nombre", nombre)
        intent.putExtra("uid", uid)
        startActivity(intent)
    }

    fun irPantallaPerfil() {
        val intent = Intent(this@ChatActivity, PerfilActivity::class.java)
        intent.putExtra("nombre", nombre)
        intent.putExtra("uid", uid)
        startActivity(intent)
    }

    fun irPantallaVenta() {
        val intent = Intent(this@ChatActivity, VentaActivity::class.java)
        intent.putExtra("nombre", nombre)
        intent.putExtra("uid", uid)
        startActivity(intent)
    }

    fun irPantallaMisChats(){
        val intent = Intent(this@ChatActivity,MisChatsActivity::class.java)
        intent.putExtra("nombre", nombre)
        intent.putExtra("uid", uid)
        startActivity(intent)
    }

    fun irPantallaAgendarCita(){
        val intent = Intent(this@ChatActivity,AgendarCitaActivity::class.java)

        intent.putExtra("nombre", nombre)
        intent.putExtra("uid", uid)

        intent.putExtra("uidUsuario", idUsuario)
        intent.putExtra("uidVendedor", idVendedor)

        intent.putExtra("uid", idUsuario)
        startActivity(intent)
    }

}