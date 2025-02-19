package com.example.page_app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.example.page_app.databinding.ActivityMainBinding;
import com.example.page_app.fragment.AddressBookFragment;
import com.example.page_app.fragment.DiscoverFragment;
import com.example.page_app.fragment.HomeFragment;
import com.example.page_app.fragment.MeFragment;
import com.example.page_app.user.LoginActivity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    List<Fragment> list;

    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initView();
        checkToken();
    }

    private void initView(){
        list = new ArrayList<>();
        list.add(new HomeFragment());
        list.add(new DiscoverFragment());
        list.add(new AddressBookFragment());
        list.add(new MeFragment());

        switchFragment(list.get(0));

        binding.navigationBar.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.menu_message:
                    switchFragment(list.get(0));
                    break;
                case R.id.menu_discovery:
                    switchFragment(list.get(1));
                    break;
                case R.id.menu_address_book:
                    switchFragment(list.get(2));
                    break;
                case R.id.menu_me:
                    switchFragment(list.get(3));
                    break;
            }
            return true;
        });
    }

    private void switchFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, fragment)
                .commit();
    }

    private void checkToken(){
        SharedPreferences sp = getSharedPreferences("user", MODE_PRIVATE);
        String token = sp.getString("token", "");
        if (token.isEmpty()){
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            Toast.makeText(this, "请先登录", Toast.LENGTH_SHORT).show();
        }
    }

}