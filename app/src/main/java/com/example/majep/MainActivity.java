package com.example.majep;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.majep.Common.Common;
import com.facebook.accountkit.AccessToken;
import com.facebook.accountkit.AccountKit;
import com.facebook.accountkit.AccountKitLoginResult;
import com.facebook.accountkit.ui.AccountKitActivity;
import com.facebook.accountkit.ui.AccountKitConfiguration;
import com.facebook.accountkit.ui.LoginType;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {
private static final int APP_REQUEST_CODE=7117;

@BindView(R.id.btn_login)
Button btn_login;

    @BindView(R.id.txt_skip)
    TextView txt_skip;

    @OnClick(R.id.btn_login)
    void loginUser()
    {
        final Intent intent=new Intent(this, AccountKitActivity.class);
        AccountKitConfiguration.AccountKitConfigurationBuilder configurtionBuilder=
                new AccountKitConfiguration.AccountKitConfigurationBuilder(LoginType.PHONE,AccountKitActivity.ResponseType.TOKEN);
        intent.putExtra(AccountKitActivity.ACCOUNT_KIT_ACTIVITY_CONFIGURATION,configurtionBuilder.build());
        startActivityForResult(intent,APP_REQUEST_CODE);

    }

    @OnClick(R.id.txt_skip)
    void skipLoginJustGoHome()
    {
        Intent intent=new Intent(this,HomeActivity.class);
        intent.putExtra(Common.IS_LOGIN,false);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==APP_REQUEST_CODE)
        {
            AccountKitLoginResult loginResult=data.getParcelableExtra(AccountKitLoginResult.RESULT_KEY);
            if(loginResult.getError()!=null)
            {
                Toast.makeText(this,""+loginResult.getError().getErrorType().getMessage(),Toast.LENGTH_SHORT).show();

            }
            else if(loginResult.wasCancelled())
            {
                Toast.makeText(this,"Login cancelled",Toast.LENGTH_SHORT).show();
            }
            else
            {
                Intent intent=new Intent(this,HomeActivity.class);
                intent.putExtra(Common.IS_LOGIN,true);
                startActivity(intent);
                finish();

            }

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AccessToken accessToken= AccountKit.getCurrentAccessToken();
        if(accessToken!=null)
        {
            Intent intent=new Intent(this,HomeActivity.class);
            intent.putExtra(Common.IS_LOGIN,true);
            startActivity(intent);
            finish();
        }
        else
        {
            setContentView(R.layout.activity_main);
            ButterKnife.bind(MainActivity.this);
        }

        printKeyHash();
    }

    private void printKeyHash() {
        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo(
                    getPackageName(),
                    PackageManager.GET_SIGNATURES
            );

            for (Signature signature : packageInfo.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KEYHASH", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        }catch (PackageManager.NameNotFoundException e){
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }
}
