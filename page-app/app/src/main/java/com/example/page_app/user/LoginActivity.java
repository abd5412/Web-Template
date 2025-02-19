package com.example.page_app.user;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.page_app.MainActivity;
import com.example.page_app.R;
import com.example.page_app.databinding.ActivityLoginBinding;
import com.example.page_app.utils.HttpClientUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    ActivityLoginBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initView();

    }

    private void initView() {
        clearToken();
        binding.loginBtn.setOnClickListener(v -> {
            String username = binding.loginNameEd.getText().toString();
            String password = binding.loginPwdEd.getText().toString();
            Log.d("LoginActivity", "initView: " + username + " " + password);
            if (username.isEmpty() || password.isEmpty()){
                Toast.makeText(this, "请输入用户名或密码", Toast.LENGTH_SHORT).show();
                return;
            }
            // 验证用户名密码
            HttpClientUtil httpClientUtil = new HttpClientUtil(this);
            Map<String, Object> param = new HashMap<>();
            param.put("username", username);
            param.put("password", password);
            httpClientUtil.post("/user/login", new HttpClientUtil.CallBack() {
                @Override
                public void onSuccess(JSONObject res) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                if (res.getInt("code") == 0){
                                    Log.d("LoginActivity", "onSuccess: " + res.toString());
                                    String token = res.getString("token");
                                    getSharedPreferences("user", MODE_PRIVATE).edit().putString("token", token).apply();
                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                }else {
                                    Toast.makeText(LoginActivity.this, "用户名或密码错误", Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    });
                }

                @Override
                public void onFailure(Exception e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.d("LoginActivity", "onFailure: " + e.getMessage());
                            Toast.makeText(LoginActivity.this, "网络错误", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }, param);
        });
    }


    private void clearToken() {
        Log.d("LoginActivity", "token clear: ");
        getSharedPreferences("user", MODE_PRIVATE).edit().clear().apply();
    }




}