package com.example.submissionpertamaai

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Toast
import com.example.submissionpertamaai.Response.ResponseRegister
import com.example.submissionpertamaai.databinding.ActivityRegisterBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var mainViewModel: MainViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        playAnimation()

        binding.buttonRegister.setOnClickListener{
            if (binding.edPassword.text.toString().length>= 8 && Patterns.EMAIL_ADDRESS.matcher(binding.edEmail.text.toString()).matches() ){
                val username = binding.edUsername.text
                val email = binding.edEmail.text.toString()
                val password = binding.edPassword.text.toString()


                showLoading(true)
                val apiService = com.example.submissionpertamaai.Api.ApiConfig.getApiService()
                val login = apiService.Forregister(username.toString(),email,password)
                login.enqueue(object : Callback<ResponseRegister> {
                    override fun onResponse(
                        call: Call<ResponseRegister>,
                        response: Response<ResponseRegister>
                    ) {
                        showLoading(false)
                        if (response.isSuccessful) {
                            val responseBody = response.body()
                            if (responseBody != null && !responseBody.error!!) {
                                Toast.makeText(this@RegisterActivity, responseBody.message, Toast.LENGTH_SHORT).show()
                                val intent = Intent(this@RegisterActivity,LoginActivity::class.java)
                                startActivity(intent)
                                finish()
                            }
                        } else {
                            Toast.makeText(this@RegisterActivity, "Register Gagal Mohon Cek Ulang Data Anda", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<ResponseRegister>, t: Throwable) {
                        showLoading(false)
                        Toast.makeText(this@RegisterActivity, t.message, Toast.LENGTH_SHORT).show()

                    }

                })
            }

            else{
                Toast.makeText(this,"Email Dan Password Yang anda Masukkan Tidak Sesuai dengan ketentuan",Toast.LENGTH_SHORT).show()
            }



        }

    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.logo, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()
        val tv_username = ObjectAnimator.ofFloat(binding.tvName, View.ALPHA, 1f).setDuration(500)
        val ed_username = ObjectAnimator.ofFloat(binding.edUsername, View.ALPHA, 1f).setDuration(500)
        val tv_email = ObjectAnimator.ofFloat(binding.tvEmail, View.ALPHA, 1f).setDuration(500)
        val ed_email = ObjectAnimator.ofFloat(binding.edEmail, View.ALPHA, 1f).setDuration(500)
        val tv_password = ObjectAnimator.ofFloat(binding.textView, View.ALPHA, 1f).setDuration(500)
        val ed_password = ObjectAnimator.ofFloat(binding.edPassword, View.ALPHA, 1f).setDuration(500)
        val button_login = ObjectAnimator.ofFloat(binding.buttonRegister, View.ALPHA, 1f).setDuration(500)




        AnimatorSet().apply {
            playSequentially(tv_username,ed_username,tv_email, ed_email, tv_password,ed_password,button_login)
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