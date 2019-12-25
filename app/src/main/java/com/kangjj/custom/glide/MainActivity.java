package com.kangjj.custom.glide;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.kangjj.custom.glide.library.Glide;

public class MainActivity extends AppCompatActivity {
    public static final String PATH  = "https://cn.bing.com/sa/simg/hpb/LaDigue_EN-CA1115245085_1920x1080.jpg";

    private ImageView imageView1, imageView2, imageView3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView1 = findViewById(R.id.image1);
        imageView2 = findViewById(R.id.image2);
        imageView3 = findViewById(R.id.image3);
    }
    public void t1(View view) {
        Glide.with(this).load(PATH).into(imageView1);
    }
    public void t2(View view) {
        Glide.with(this).load(PATH).into(imageView2);
    }
    public void t3(View view) {
        Glide.with(this).load(PATH).into(imageView3);
    }

}
