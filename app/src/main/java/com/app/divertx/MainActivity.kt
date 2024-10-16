package com.app.divertx

import android.Manifest
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.telephony.TelephonyManager
import android.widget.EditText
import android.widget.ToggleButton
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {

    private lateinit var alwaysDivertNumberInput: EditText
    private lateinit var dcwbInput: EditText
    private lateinit var dcwnrInput: EditText
    private lateinit var dcwhioofcInput: EditText
    private lateinit var toggleAlwaysDivert: ToggleButton
    private lateinit var toggleBusy: ToggleButton
    private lateinit var toggleNoResponse: ToggleButton
    private lateinit var toggleOutOfCoverage: ToggleButton

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize EditText and ToggleButton
        alwaysDivertNumberInput = findViewById(R.id.alwaysDivertnumberinput)
        dcwbInput = findViewById(R.id.dcwb)
        dcwnrInput = findViewById(R.id.dcwnr)
        dcwhioofcInput = findViewById(R.id.dcwhioofc)
        toggleAlwaysDivert = findViewById(R.id.toggleButton)
        toggleBusy = findViewById(R.id.toggleButton2)
        toggleNoResponse = findViewById(R.id.toggleButton3)
        toggleOutOfCoverage = findViewById(R.id.toggleButton4)


        sharedPreferences = getSharedPreferences("DivertXPrefs", MODE_PRIVATE)


        loadSavedNumbers()



        checkPermissions()


        setupToggleListeners()
    }

    private fun loadSavedNumbers() {

        alwaysDivertNumberInput.setText(sharedPreferences.getString("alwaysDivertNumber", ""))
        dcwbInput.setText(sharedPreferences.getString("dcwbNumber", ""))
        dcwnrInput.setText(sharedPreferences.getString("dcwnrNumber", ""))
        dcwhioofcInput.setText(sharedPreferences.getString("dcwhioofcNumber", ""))
    }

    private fun setupToggleListeners() {
        toggleAlwaysDivert.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                divertCall("*21*", alwaysDivertNumberInput.text.toString())
            } else {
                disableDivertCall("21")
            }
        }

        toggleBusy.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                divertCall("*67*", dcwbInput.text.toString())
            } else {
                disableDivertCall("67")
            }
        }

        toggleNoResponse.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                divertCall("*61*", dcwnrInput.text.toString())
            } else {
                disableDivertCall("61")
            }
        }

        toggleOutOfCoverage.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                divertCall("*62*", dcwhioofcInput.text.toString())
            } else {
                disableDivertCall("62")
            }
        }
    }

    private fun divertCall(code: String, phoneNumber: String) {
        val ussdCode = code + phoneNumber + Uri.encode("#")
        val intent = Intent(Intent.ACTION_CALL)
        intent.data = Uri.parse("tel:$ussdCode")
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            startActivity(intent)
        }
    }


    private fun disableDivertCall(code: String) {
        val ussdCode =  Uri.encode("#") + code + Uri.encode("#")
        val intent = Intent(Intent.ACTION_CALL)
        intent.data = Uri.parse("tel:$ussdCode")
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            startActivity(intent)
        }
    }

    private fun checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CALL_PHONE), 1)
        }
    }

    override fun onPause() {
        super.onPause()

        saveNumbers()
    }

    private fun saveNumbers() {
        val editor = sharedPreferences.edit()
        editor.putString("alwaysDivertNumber", alwaysDivertNumberInput.text.toString())
        editor.putString("dcwbNumber", dcwbInput.text.toString())
        editor.putString("dcwnrNumber", dcwnrInput.text.toString())
        editor.putString("dcwhioofcNumber", dcwhioofcInput.text.toString())
        editor.apply()
    }
}
