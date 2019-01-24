package in.kolkatatailor.kolkatatailor;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

public class SplashScreen extends AppCompatActivity {

    FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        getSupportActionBar().hide();
        mAuth=FirebaseAuth.getInstance();
        DatabaseReference alluserDb = FirebaseDatabase.getInstance().getReference("Users");
        String device_token=FirebaseInstanceId.getInstance().getToken();


        if (mAuth.getCurrentUser()!=null){
            String userid = mAuth.getCurrentUser().getUid();
            alluserDb.child(userid).child("device_token").setValue(device_token);
            Intent intent=new Intent(SplashScreen.this,MainActivity.class);
            startActivity(intent);
            finish();
            }

        else
            {
                Intent intent=new Intent(SplashScreen.this,FrontScreenActivity.class);
            startActivity(intent);
            finish();
            }

    }
}
