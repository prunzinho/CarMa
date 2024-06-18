package mx.uv.carma.poko

import java.io.Serializable

data class Coche(
    val postId: String = "",
    val modelo: String = "",
    val marca: String = "",
    val anio: String = "",
    val transmision: String = "",
    val combustible: String = "",
    val precio: Double = 0.0,
    val km: Double = 0.0,
    val ubicacionPais: String = "",
    val ubicacionEstado: String = "",
    val ubicacionMunicipio: String = "",
    val uid: String = "",
    val nombre: String = "",
    val imagenAtras: String = "",
    val imagenDiagonal: String = "",
    val imagenFrente: String = "",
    val imagenLado_derecho: String = "",
    val imagenLado_izquierdo: String = ""
) : Serializable
