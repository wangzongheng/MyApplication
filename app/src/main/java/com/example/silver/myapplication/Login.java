package com.example.silver.myapplication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;


public class Login extends AppCompatActivity {
    ImageView checkImage;
    RequestQueue queue=null;
    EditText et_username,et_password,checkText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        queue = Volley.newRequestQueue(this);
        et_username = (EditText)findViewById(R.id.username);
        et_password = (EditText)findViewById(R.id.password);
        checkImage = (ImageView) findViewById(R.id.checkImage);
        checkText = (EditText)findViewById(R.id.checkText);
        SendImageRequest();
    }



    public void loginClick(View view) {

        String url = "http://www.neuedu.cn/m/mobileLogin!loginNeu.action";
        final String userName = et_username.getText().toString();
        final String passWord = et_password.getText().toString();
        final String code = checkText.getText().toString();

        StringRequest request = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        Gson gson = new Gson();
                        Root root = gson.fromJson(s, Root.class);
                        LoginReturn loginReturn = root.getLoginReturn();
                        if (loginReturn.getLoginFlag() == 1) {
                            Toast.makeText(Login.this, loginReturn.getMsg(), Toast.LENGTH_LONG).show();
                            startActivity(new Intent(Login.this, MainActivity.class));
                            finish();
                        } else {
                            Toast.makeText(Login.this, loginReturn.getMsg(), Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(Login.this, "请检测网络！", Toast.LENGTH_SHORT).show();
                    }
                }) {

            //写入Cookie
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> localHashMap = new HashMap<>();
                String cookie = SharedPreferenceHelper.getCookie(Login.this);
                localHashMap.put("Cookie", cookie);
                return localHashMap;
            }

            //参数提交
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                map.put("username", userName);
                map.put("pwd", passWord);
                map.put("imgcode", code);
                return map;
            }

            //重写方法获取Cookie
            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                Map<String, String> responseHeaders = response.headers;
                String rawCookies = responseHeaders.get("Set-Cookie");
                if (rawCookies != null) {
                    String cookie = rawCookies.split(";")[0];
                    SharedPreferenceHelper.saveCookie(Login.this, cookie);
                }
                return super.parseNetworkResponse(response);
            }
        };
        queue.add(request);
    }

    public void SendImageRequest() {
        String url = "http://www.neuedu.cn/imgcode";
        ImageRequest imageRequest = new ImageRequest(
                url,
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap response) {
                        checkImage.setImageBitmap(response);
                    }
                }, 0, 0, Bitmap.Config.RGB_565, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                checkImage.setImageResource(R.mipmap.ic_launcher);
            }
        }) {
            @Override
            protected Response<Bitmap> parseNetworkResponse(NetworkResponse response) {
                Map<String, String> responseHeaders = response.headers;
                String rawCookies = responseHeaders.get("Set-Cookie");
                String cookie = rawCookies.split(";")[0];
                SharedPreferenceHelper.saveCookie(Login.this, cookie);
                return super.parseNetworkResponse(response);
            }
        };
        queue.add(imageRequest);
    }
    public void RefreshCodeImage(View view){
        SendImageRequest();
    }

}
