package mx.uv.carma.modelo

class User {
    var nombre:String? = null
    var correo: String? = null
    var uid:String? = null
    var contra:String? = null
    var fecha:String? = null
    var fotoUrl:String?=null

    constructor(){}

    constructor(nombre:String?,correo:String?,uid:String?,contra:String?,fecha:String?,fotoUrl:String?){
        this.nombre = nombre
        this.correo= correo
        this.uid = uid
        this.contra = contra
        this.fecha = fecha
        this.fotoUrl = fotoUrl
    }
}