package com.example.routex_app.utils

object ApiEndpointsList {

    //const val BASE_URL = " http://localhost:8080/api/"
    const val BASE_URL = "http://10.0.2.2:8080/api/" //solo emulacion android studio local
    // La ruta que creamos en el controlador de C#
    const val BASE_URL_CS = "http://10.0.2.2:5198/api/"

    // Aquí puedes poner los nombres de los endpoints para no equivocarte
    const val LOGIN_ENDPOINT = "login"
    const val COMMERCIAL_DASHBOARD_ENDPOINT = "commercial/dashboard/"
    const val REJECTED_QUOTES_ENDPOINT = "commercial/ofertes/rejected/"
    const val SENT_QUOTES_ENDPOINT = "commercial/ofertes/sent/"
    const val ACCEPTED_QUOTES_ENDPOINT = "commercial/ofertes/accepted/"
    const val RESGISTER_NEW_CLIENT = "commercial/register-client"
    const val LIST_CURRENCY = "commercial/currency"
    const val LIST_INDUSTRY = "commercial/industria"


}