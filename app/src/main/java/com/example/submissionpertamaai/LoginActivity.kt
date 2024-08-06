package com.example.submissionpertamaai

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.example.submissionpertamaai.DataStore.UserPreferences
import com.example.submissionpertamaai.DataStore.UserViewModel
import com.example.submissionpertamaai.DataStore.ViewModelFactory
import com.example.submissionpertamaai.Response.ResponseLogin
import com.example.submissionpertamaai.databinding.ActivityLoginBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user")
class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var mainViewModel: MainViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mainViewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserPreferences.getInstance(dataStore))
        )[MainViewModel::class.java]
        mainViewModel.logout()

        playAnimation()

        binding.button.setOnClickListener{

            if (binding.edPassword.text.toString().length >=8 && Patterns.EMAIL_ADDRESS.matcher(binding.edEmail.text.toString()).matches() ){
                val email = binding.edEmail.text.toString()
                val password = binding.edPassword.text.toString()
                showLoading(true)

                val apiService = com.example.submissionpertamaai.Api.ApiConfig.getApiService()
                val login = apiService.Forlogin(email,password)
                login.enqueue(object : Callback<ResponseLogin> {
                    override fun onResponse(
                        call: Call<ResponseLogin>,
                        response: Response<ResponseLogin>
                    ) {
                        showLoading(false)
                        if (response.isSuccessful) {
                            val responseBody = response.body()
                            if (responseBody != null && !responseBody.error!!) {
                                responseBody.loginResult?.let {

                                    val UserData = UserViewModel(it.name.toString(),it.userId.toString(), it.token.toString())
                                    mainViewModel.saveuser(UserData)
                                    val intent = Intent(this@LoginActivity, MainActivity::class.java)
                                    startActivity(intent)


                                }
                                Toast.makeText(this@LoginActivity,"Login ${responseBody.message}",Toast.LENGTH_SHORT).show()


                            }
                        } else {
                            Toast.makeText(this@LoginActivity, "Email dan Password kurang tepat", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<ResponseLogin>, t: Throwable) {
                        showLoading(false)
                        Toast.makeText(this@LoginActivity, t.message, Toast.LENGTH_SHORT).show()
                    }

                })
            }
            else{
                Toast.makeText(this,"Email Dan Password Yang anda Masukkan Tidak Sesuai dengan ketentuan",Toast.LENGTH_SHORT).show()
            }

        }

        binding.register.setOnClickListener{
            val intent = Intent(this,RegisterActivity::class.java)
            startActivity(intent)
            finish()

        }


    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.logo, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val tv_email = ObjectAnimator.ofFloat(binding.tvEmail, View.ALPHA, 1f).setDuration(500)
        val ed_email = ObjectAnimator.ofFloat(binding.edEmail, View.ALPHA, 1f).setDuration(500)
        val tv_password = ObjectAnimator.ofFloat(binding.textView, View.ALPHA, 1f).setDuration(500)
        val ed_password = ObjectAnimator.ofFloat(binding.edPassword, View.ALPHA, 1f).setDuration(500)
        val button_login = ObjectAnimator.ofFloat(binding.button, View.ALPHA, 1f).setDuration(500)
        val register = ObjectAnimator.ofFloat(binding.register, View.ALPHA, 1f).setDuration(500)



        AnimatorSet().apply {
            playSequentially(tv_email, ed_email, tv_password,ed_password,button_login,register)
            start()
        }

    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }
}