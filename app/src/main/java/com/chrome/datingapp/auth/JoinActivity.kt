package com.chrome.datingapp.auth

import android.app.Instrumentation.ActivityResult
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import com.chrome.datingapp.MainActivity
import com.chrome.datingapp.R
import com.chrome.datingapp.utils.FirebaseAuthUtils
import com.chrome.datingapp.utils.FirebaseRef
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.storage.ktx.storage
import java.io.ByteArrayOutputStream

class JoinActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    private var nickname = ""
    private var gender = ""
    private var region = ""
    private var age = ""
    private var uid = ""

    lateinit var profileImage : ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_join)

        auth = Firebase.auth
        profileImage = findViewById(R.id.profile)

        val getAction = registerForActivityResult(
            ActivityResultContracts.GetContent(),
            ActivityResultCallback { uri ->
                profileImage.setImageURI(uri)
            }
        )

        profileImage.setOnClickListener {
            getAction.launch("image/*")
        }

        val joinBtn = findViewById<Button>(R.id.join)
        joinBtn.setOnClickListener {
            val email = findViewById<TextInputEditText>(R.id.email)
            val password = findViewById<TextInputEditText>(R.id.password)
            val passwordChk = findViewById<TextInputEditText>(R.id.passwordChk)

            if(email.text.toString().isEmpty()) {
                Toast.makeText(this, "이메일을 입력해주세요", Toast.LENGTH_SHORT).show()
            }

            else if(password.text.toString().isEmpty()) {
                Toast.makeText(this, "비밀번호를 입력해주세요", Toast.LENGTH_SHORT).show()
            }

            else if(password.text.toString().length > 12 || password.text.toString().length < 6) {
                Toast.makeText(this, "비밀번호는 6~12자만 입력할 수 있습니다.", Toast.LENGTH_SHORT).show()
            }

            else if(password.text.toString() != passwordChk.text.toString()) {
                Toast.makeText(this, "비밀번호와 비밀번호 확인의 입력 값이 다릅니다.", Toast.LENGTH_SHORT).show()
            }

            else {
                try {
                    nickname = findViewById<TextInputEditText>(R.id.nickname).text.toString()
                    gender = findViewById<TextInputEditText>(R.id.gender).text.toString()
                    region = findViewById<TextInputEditText>(R.id.region).text.toString()
                    age = findViewById<TextInputEditText>(R.id.age).text.toString()

                    if(nickname.isEmpty()) {
                        Toast.makeText(this, "닉네임을 입력해주세요", Toast.LENGTH_SHORT).show()
                    }

                    else if(gender.isEmpty()) {
                        Toast.makeText(this, "성별을 입력해주세요", Toast.LENGTH_SHORT).show()
                    }

                    else if(region.isEmpty()) {
                        Toast.makeText(this, "지역을 입력해주세요", Toast.LENGTH_SHORT).show()
                    }

                    else if(age.isEmpty()) {
                        Toast.makeText(this, "나이를 입력해주세요", Toast.LENGTH_SHORT).show()
                    }

                    else {
                        age.toInt() // 나이에 숫자 값이 들어왔는지 확인
                        if(profileImage.drawable != null) {
                            Log.d("JoinActivity", profileImage.toString())
                            auth.createUserWithEmailAndPassword(email.text.toString(), password.text.toString())
                                .addOnCompleteListener(this) { task ->
                                    if (task.isSuccessful) {
                                        Log.d("JoinActivity","회원가입 완료")
                                        uid = FirebaseAuthUtils.getUid()

                                        FirebaseMessaging.getInstance().token.addOnCompleteListener(
                                            OnCompleteListener { task ->
                                            if (!task.isSuccessful) {
                                                Log.w("MyToken", "Fetching FCM registration token failed", task.exception)
                                                return@OnCompleteListener
                                            }
                                            val token = task.result
                                            val userInfo = UserInfo(uid, nickname, gender, region, age, token)
                                            FirebaseRef.userInfo.child(uid).setValue(userInfo)
                                            uploadImage(uid)

                                            val intent = Intent(this, MainActivity::class.java)
                                            startActivity(intent)
                                        })
                                    } else {
                                        Toast.makeText(this, "이메일 형식이 잘못되었습니다.", Toast.LENGTH_SHORT).show()
                                    }
                                }
                        }
                        else {
                            Toast.makeText(this, "프로필 이미지를 등록해주세요.", Toast.LENGTH_SHORT).show()
                        }
                    }
                } catch (e: NumberFormatException) {
                    Toast.makeText(this, "나이 입력 칸에는 숫자만 입력할 수 있습니다.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun uploadImage(uid: String) {

        val storage = Firebase.storage
        val storageRef = storage.reference.child(uid + ".jpeg")

        profileImage.isDuplicateParentStateEnabled = true
        profileImage.buildDrawingCache()
        val bitmap = (profileImage.drawable as BitmapDrawable).bitmap
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()

        var uploadTask = storageRef.putBytes(data)
        uploadTask.addOnFailureListener{

        }.addOnSuccessListener { taskSnapshot ->

        }
    }
}