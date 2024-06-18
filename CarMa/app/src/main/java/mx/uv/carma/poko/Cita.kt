package mx.uv.carma.poko

data class Cita(
    var uidUsuario: String? = "",
    var uidVendedor: String? = "",
    var nombreUsuario: String = "",
    var nombreVendedor: String? = "",
    var fechaCita: String = "",
    var horaCita: String = "",
    var lugar: String = "",
    var descripcion: String = ""
) {
    // Constructor sin argumentos necesario para Firebase
    constructor() : this("", "", "", "", "", "", "", "")
}