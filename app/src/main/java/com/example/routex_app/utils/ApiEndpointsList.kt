package com.example.routex_app.utils

object ApiEndpointsList {

    //const val BASE_URL = " http://localhost:8080/api/"
    const val BASE_URL = "http://10.0.2.2:8080/api/" //solo emulacion android studio local
    // La ruta que creamos en el controlador de C#
    //const val BASE_URL_CS = "http://10.0.2.2:5198/api/"

    const val BASE_URL_PHP = "https://approutex.josepguiudev.tech/api/"
    const val BASE_URL_CS = "http://routex-apicsharp-6bahrx-f06c64-51-83-192-177.traefik.me/api/"
    // ruta al servido de Springboot en docker
    //const val BASE_URL_SERVER = "http://10.0.2.2:5199/"



    // Aquí puedes poner los nombres de los endpoints para no equivocarte
    const val LOGIN_ENDPOINT = "login"
    const val COMMERCIAL_DASHBOARD_ENDPOINT = "commercial/dashboard/"
    const val REJECTED_QUOTES_ENDPOINT = "commercial/ofertes/rejected/"
    const val SENT_QUOTES_ENDPOINT = "commercial/ofertes/sent/"
    const val ACCEPTED_QUOTES_ENDPOINT = "commercial/ofertes/accepted/"
    const val RESGISTER_NEW_CLIENT = "commercial/register-client"
    const val LIST_CURRENCY = "commercial/currency"
    const val LIST_INDUSTRY = "commercial/industria"

    const val DNI_UPLOAD = "dni/upload/"
    const val DNI_DOWNLOAD = "dni/download/"

    const val LISTADO_OFERRTAS = "commercial/envios/activos/"
    const val DETALLE_OFERRTAS = "commercial/envios/detalle/"
    const val LSITADO_CLIENTES = "commercial/active-clients/"
    const val PERFIL_COMMERCIAL = "commercial/profile/"

    const val CLIENTE_OFERTAS  = "Ofertes"
    const val CLIENTE_DASHBOARD = "client/dashboard/"
    const val PORT = "Ports"

}