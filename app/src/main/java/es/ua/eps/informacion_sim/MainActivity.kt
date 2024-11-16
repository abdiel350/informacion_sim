package es.ua.eps.informacion_sim

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.telephony.TelephonyManager
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {

    private val REQUEST_CODE_PERMISSION = 101
    private lateinit var infoTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Inicializar el TextView
        infoTextView = findViewById(R.id.infoTextView)

        // Solicitar permisos si es necesario
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.READ_PHONE_STATE), REQUEST_CODE_PERMISSION)
        } else {
            getSimInfo()
        }
    }

    // Obtener información de la SIM
    private fun getSimInfo() {
        val telephonyManager = getSystemService(TELEPHONY_SERVICE) as TelephonyManager

        // Nombre del operador
        val operatorName = telephonyManager.networkOperatorName

        // Tipo de red (LTE, 3G, etc.)
        val networkType = when (telephonyManager.networkType) {
            TelephonyManager.NETWORK_TYPE_LTE -> "LTE"
            TelephonyManager.NETWORK_TYPE_UMTS -> "3G"
            TelephonyManager.NETWORK_TYPE_GPRS -> "2G"
            TelephonyManager.NETWORK_TYPE_EDGE -> "2G"
            TelephonyManager.NETWORK_TYPE_HSPA -> "3G"
            else -> "Desconocido"
        }

        // Código del país y del operador
        val countryCode = telephonyManager.networkCountryIso
        val mccMnc = telephonyManager.networkOperator

        // Estado de la SIM (puede ser null si no está disponible)
        val simState = when (telephonyManager.simState) {
            TelephonyManager.SIM_STATE_READY -> "SIM lista"
            TelephonyManager.SIM_STATE_ABSENT -> "SIM ausente"
            TelephonyManager.SIM_STATE_PIN_REQUIRED -> "PIN requerido"
            TelephonyManager.SIM_STATE_PUK_REQUIRED -> "PUK requerido"
            TelephonyManager.SIM_STATE_NETWORK_LOCKED -> "Red bloqueada"
            else -> "Estado desconocido"
        }

        // Actualizar el contenido del TextView con la información
        infoTextView.text = """
            Operador: $operatorName
            Tipo de Red: $networkType
            Código de País: $countryCode
            MCC/MNC: $mccMnc
            Estado SIM: $simState
        """.trimIndent()
    }

    // Manejar el resultado de la solicitud de permisos
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permiso otorgado, obtener la información de la SIM
                getSimInfo()
            } else {
                Toast.makeText(this, "Permiso no otorgado", Toast.LENGTH_SHORT).show()
            }
        }
    }
}