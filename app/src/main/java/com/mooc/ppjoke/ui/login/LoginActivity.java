package com.mooc.ppjoke.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.tencent.connect.UserInfo;
import com.tencent.connect.auth.QQToken;
import com.tencent.connect.common.Constants;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import org.json.JSONException;
import org.json.JSONObject;

import us.bojie.libnetwork.ApiResponse;
import us.bojie.libnetwork.ApiService;
import us.bojie.libnetwork.JsonCallback;
import com.mooc.ppjoke.R;
import com.mooc.ppjoke.model.User;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private View actionClose;
    private View actionLogin;
    private Tencent tencent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        actionClose = findViewById(R.id.action_close);
        actionLogin = findViewById(R.id.action_login);

        actionClose.setOnClickListener(this);
        actionLogin.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.action_close) {
            finish();
        } else if (v.getId() == R.id.action_login) {
            login();
        }
    }

    private void login() {
        if (tencent == null) {
            tencent = Tencent.createInstance("101794421", getApplicationContext());
        }

        tencent.login(this, "all", loginListener);
    }

    IUiListener loginListener = new IUiListener() {
        @Override
        public void onComplete(Object o) {
            JSONObject response = (JSONObject) o;
            try {
                String openid = response.getString("openid");
                String access_token = response.getString("access_token");
                String expires_in = response.getString("expires_in");
                long expires_time = response.getLong("expires_time");

                tencent.setAccessToken(access_token, expires_in);
                tencent.setOpenId(openid);
                QQToken qqToken = tencent.getQQToken();
                getUserInfo(qqToken, expires_time, openid);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onError(UiError uiError) {
            Toast.makeText(getApplicationContext(), "Login failed " + uiError.toString(), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCancel() {
            Toast.makeText(getApplicationContext(), "Login cancelled", Toast.LENGTH_SHORT).show();
        }
    };

    private void getUserInfo(QQToken qqToken, long expiresTime, String openid) {
        UserInfo userInfo = new UserInfo(getApplicationContext(), qqToken);
        userInfo.getUserInfo(new IUiListener() {
            @Override
            public void onComplete(Object o) {
                JSONObject response = (JSONObject) o;
                try {
                    String nickname = response.getString("nickname");
                    String figureurl2 = response.getString("figureurl_2");

                    save(nickname, figureurl2, openid, expiresTime);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(UiError uiError) {
                Toast.makeText(getApplicationContext(), "Get user's QQ info failed " + uiError.toString(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancel() {
                Toast.makeText(getApplicationContext(), "Get user's QQ info cancelled", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void save(String nickname, String avatar, String openid, long expiresTime) {
        ApiService.get("/user/insert")
                .addParam("name", nickname)
                .addParam("avatar", avatar)
                .addParam("qqOpenId", openid)
                .addParam("expire_time", expiresTime)
                .execute(new JsonCallback<User>() {
                    @Override
                    public void onSuccess(ApiResponse<User> response) {
                        if (response.body != null) {
                            com.mooc.ppjoke.ui.login.UserManager.get().save(response.body);
                        } else {
                            runOnUiThread(() -> Toast.makeText(getApplicationContext(), "login failed", Toast.LENGTH_SHORT).show());
                        }
                    }

                    @Override
                    public void onError(ApiResponse<User> response) {
                        runOnUiThread(() -> Toast.makeText(getApplicationContext(), "login failed " + response.message, Toast.LENGTH_SHORT).show());
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.REQUEST_LOGIN) {
            Tencent.onActivityResultData(requestCode, resultCode, data, loginListener);
        }
    }
}
