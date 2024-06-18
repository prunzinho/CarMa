package mx.uv.carma.adaptadores

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import mx.uv.carma.ChatActivity
import mx.uv.carma.R
import mx.uv.carma.modelo.User
import java.io.InputStream
import java.net.URL

class UserAdapter(val context: Context, val userList: ArrayList<User>,  private val uidUser: String,private val nombreUser: String) :
    RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view: View = LayoutInflater.from(context).inflate(R.layout.user_layout, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val currentUser = userList[position]
        holder.textName.text = currentUser.nombre

        // Load user image
        val fotoUrl = currentUser.fotoUrl
        if (!fotoUrl.isNullOrEmpty()) {
            DownloadImageTask(holder.profileImage).execute(fotoUrl)
        }

        holder.itemView.setOnClickListener {
            val intent = Intent(context, ChatActivity::class.java)
            intent.putExtra("name", currentUser.nombre)
            intent.putExtra("uid", currentUser.uid)
            intent.putExtra("nameU", nombreUser)
            intent.putExtra("uidU", uidUser)
            intent.putExtra("fotoUrl", currentUser.fotoUrl)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textName: TextView = itemView.findViewById(R.id.txt_name)
        val profileImage: ImageView = itemView.findViewById(R.id.iv_img_perfil_usuario)
    }

    private class DownloadImageTask(private val imageView: ImageView) : AsyncTask<String, Void, Bitmap?>() {
        override fun doInBackground(vararg urls: String): Bitmap? {
            val url = urls[0]
            var bmp: Bitmap? = null
            try {
                val input: InputStream = URL(url).openStream()
                bmp = BitmapFactory.decodeStream(input)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return bmp
        }

        override fun onPostExecute(result: Bitmap?) {
            if (result != null) {
                imageView.setImageBitmap(result)
            }
        }
    }
}
