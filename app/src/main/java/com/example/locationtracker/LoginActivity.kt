package com.example.locationtracker

import android.content.Intent
import android.os.Bundle
import android.telephony.PhoneNumberUtils
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.material.textfield.TextInputLayout

class LoginActivity : AppCompatActivity() {
    private val nameLiveData = MutableLiveData<String>()
    private val phoneLiveData = MutableLiveData<String>()
    private lateinit var loginButton: Button
    private lateinit var name: TextInputLayout
    private lateinit var phone: TextInputLayout
    private val isValidLiveData = MediatorLiveData<Boolean>().apply {
        this.value = false
        addSource(nameLiveData)
        { name ->
            val phone = phoneLiveData.value
            this.value = validateForm(name, phone)
        }
        addSource(phoneLiveData)
        { phone ->
            val name = nameLiveData.value
            this.value = validateForm(name, phone)
        }
    }

    private fun validateForm(name: String?, phone: String?): Boolean {
        val isNameValid = name != null && name.isNotBlank()
        val isPhoneValid =
            phone != null && phone.isNotBlank() && PhoneNumberUtils.isGlobalPhoneNumber(phone)
        return isNameValid && isPhoneValid
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        checkLoginStatus()
        loginButton = findViewById(R.id.login_button)
        name = findViewById(R.id.nameLayout)
        phone = findViewById(R.id.phoneLayout)
        name.editText?.doOnTextChanged { text, _, _, _ ->
            nameLiveData.value = text?.toString()
        }
        phone.editText?.doOnTextChanged { text, _, _, _ ->
            phoneLiveData.value = text?.toString()
        }
        isValidLiveData.observe(this) { isValid ->
            loginButton.isEnabled = isValid
        }
        loginButton.setOnClickListener {
            Pref.setBooleanValue(this, "login_key", true)
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    private fun checkLoginStatus() {
        val loginStatus = Pref.getBooleanValue(this, "login_key")
        if (loginStatus) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}