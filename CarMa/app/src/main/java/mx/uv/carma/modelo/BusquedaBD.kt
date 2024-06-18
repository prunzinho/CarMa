package mx.uv.carma.modelo


import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import mx.uv.carma.poko.Pais
import mx.uv.carma.poko.Estado
import mx.uv.carma.poko.Municipio

class BusquedaBD(contexto: Context) : SQLiteOpenHelper(contexto, NOMBRE_BD, null, VERSION_BD) {

    companion object {
        private const val NOMBRE_BD = "busqueda.bd"
        private const val VERSION_BD = 1

        // Tablas
        private const val TABLA_PAIS = "Pais"
        private const val TABLA_ESTADO = "Estado"
        private const val TABLA_MUNICIPIO = "Municipio"

        // Columnas País
        private const val COL_PAIS_ID = "id"
        private const val COL_PAIS_NOMBRE = "nombre"

        // Columnas Estado
        private const val COL_ESTADO_ID = "id"
        private const val COL_ESTADO_NOMBRE = "nombre"
        private const val COL_ESTADO_ID_PAIS = "idPais"

        // Columnas Municipio
        private const val COL_MUNICIPIO_ID = "id"
        private const val COL_MUNICIPIO_NOMBRE = "nombre"
        private const val COL_MUNICIPIO_ID_ESTADO = "idEstado"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createPaisTable = "CREATE TABLE $TABLA_PAIS ($COL_PAIS_ID INTEGER PRIMARY KEY AUTOINCREMENT, $COL_PAIS_NOMBRE TEXT)"
        val createEstadoTable = "CREATE TABLE $TABLA_ESTADO ($COL_ESTADO_ID INTEGER PRIMARY KEY AUTOINCREMENT, $COL_ESTADO_NOMBRE TEXT, $COL_ESTADO_ID_PAIS INTEGER, FOREIGN KEY($COL_ESTADO_ID_PAIS) REFERENCES $TABLA_PAIS($COL_PAIS_ID))"
        val createMunicipioTable = "CREATE TABLE $TABLA_MUNICIPIO ($COL_MUNICIPIO_ID INTEGER PRIMARY KEY AUTOINCREMENT, $COL_MUNICIPIO_NOMBRE TEXT, $COL_MUNICIPIO_ID_ESTADO INTEGER, FOREIGN KEY($COL_MUNICIPIO_ID_ESTADO) REFERENCES $TABLA_ESTADO($COL_ESTADO_ID))"

        db.execSQL(createPaisTable)
        db.execSQL(createEstadoTable)
        db.execSQL(createMunicipioTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLA_MUNICIPIO")
        db.execSQL("DROP TABLE IF EXISTS $TABLA_ESTADO")
        db.execSQL("DROP TABLE IF EXISTS $TABLA_PAIS")
        onCreate(db)
    }

    // Métodos para insertar datos

    fun insertarPais(pais: Pais): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COL_PAIS_NOMBRE, pais.nombre)
        }
        val id = db.insert(TABLA_PAIS, null, values)
        db.close()
        return id
    }

    fun insertarEstado(estado: Estado): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COL_ESTADO_NOMBRE, estado.nombre)
            put(COL_ESTADO_ID_PAIS, estado.idPais)
        }
        val id = db.insert(TABLA_ESTADO, null, values)
        db.close()
        return id
    }

    fun insertarMunicipio(municipio: Municipio): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COL_MUNICIPIO_NOMBRE, municipio.nombre)
            put(COL_MUNICIPIO_ID_ESTADO, municipio.idEstado)
        }
        val id = db.insert(TABLA_MUNICIPIO, null, values)
        db.close()
        return id
    }

    // Métodos para consultar datos

    fun obtenerPaises(): List<Pais> {
        val db = readableDatabase
        val cursor = db.query(TABLA_PAIS, null, null, null, null, null, null)
        val paises = mutableListOf<Pais>()
        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getLong(cursor.getColumnIndexOrThrow(COL_PAIS_ID))
                val nombre = cursor.getString(cursor.getColumnIndexOrThrow(COL_PAIS_NOMBRE))
                paises.add(Pais(id, nombre))
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return paises
    }

    fun obtenerEstadosPorPais(idPais: Long): List<Estado> {
        val db = readableDatabase
        val cursor = db.query(TABLA_ESTADO, null, "$COL_ESTADO_ID_PAIS=?", arrayOf(idPais.toString()), null, null, null)
        val estados = mutableListOf<Estado>()
        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getLong(cursor.getColumnIndexOrThrow(COL_ESTADO_ID))
                val nombre = cursor.getString(cursor.getColumnIndexOrThrow(COL_ESTADO_NOMBRE))
                estados.add(Estado(id, nombre, idPais))
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return estados
    }

    fun obtenerMunicipiosPorEstado(idEstado: Long): List<Municipio> {
        val db = readableDatabase
        val cursor = db.query(TABLA_MUNICIPIO, null, "$COL_MUNICIPIO_ID_ESTADO=?", arrayOf(idEstado.toString()), null, null, null)
        val municipios = mutableListOf<Municipio>()
        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getLong(cursor.getColumnIndexOrThrow(COL_MUNICIPIO_ID))
                val nombre = cursor.getString(cursor.getColumnIndexOrThrow(COL_MUNICIPIO_NOMBRE))
                municipios.add(Municipio(id, nombre, idEstado))
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return municipios
    }
}
