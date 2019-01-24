package in.kolkatatailor.kolkatatailor;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
public class SignupActivity extends AppCompatActivity {
    private static final String TAG = "RegisterActivity";
    //Firebase
    private FirebaseAuth.AuthStateListener mAuthListener;
    //widgets
    private EditText mEmail, mName,refercodegiven;
    private Button mRegister;
    private ProgressBar mProgressBar;
    String promocode;
    //vars
    private Context mContext;
    private String email, name, password,refercode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        mRegister = (Button) findViewById(R.id.btn_register);
        mEmail = (EditText) findViewById(R.id.input_email);
        mName = (EditText) findViewById(R.id.input_name);
        mContext = SignupActivity.this;
        refercodegiven=findViewById(R.id.refercode);
        Log.d(TAG, "onCreate: started");
        hideSoftKeyboard();
        initProgressBar();
        //TODO need to call the function addetails, need to change user db with "Users" in the refererence

        mRegister.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
       email=mEmail.getText().toString();
       name=mName.getText().toString();

       if (email.equals("") || name.equals(""))
       {
           Toast.makeText(mContext, "Fill name and Email", Toast.LENGTH_SHORT).show();
           }
       else {
        addetails();
        Intent intent=new Intent(mContext,MainActivity.class);
        startActivity(intent);
        finish();
        }
    }
});

    }
    public void addetails(){
        final ProgressDialog progressDialog = new ProgressDialog(SignupActivity.this);
        progressDialog.setMessage("Please wait..");
        progressDialog.getActionBar();
        progressDialog.setCancelable(false);
        progressDialog.show();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null) {
            String userid = mAuth.getCurrentUser().getUid();

            //String emailid = mAuth.getCurrentUser().getEmail();
            int index = email.indexOf("@");
            String resultpromo = email.substring(0, index);
            resultpromo = resultpromo.replaceAll("[.]", "");
            DatabaseReference current_user_db = FirebaseDatabase.getInstance().getReference("UserDb");
            DatabaseReference alluserDb = FirebaseDatabase.getInstance().getReference("Users");

            DatabaseReference createpromo = FirebaseDatabase.getInstance().getReference("reffercode");
            String joindate = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());

            int walletcoin = 50;
            promocode = resultpromo;
            long millis=System.currentTimeMillis();
            long hour=(millis/3600000);
            Map<String, String> newPost = new HashMap<String, String>();
            newPost.put("name", name);
            newPost.put("email", email);
            newPost.put("reffered", refercode);
            newPost.put("walletcoin", String.valueOf(walletcoin));
            newPost.put("refferal", "notcredited");
            newPost.put("promocode",promocode);
            newPost.put("userid",userid);
            newPost.put("joindate",joindate);
            newPost.put("lastcheckout",String.valueOf(hour));
            current_user_db.child(userid).setValue(newPost);
            String device_token=FirebaseInstanceId.getInstance().getToken();

            //for notification sending purpose.

            Map<String, String> userinfo = new HashMap<String, String>();

            userinfo.put("name",name);
            userinfo.put("device_token",device_token);
            alluserDb.child(userid).setValue(userinfo);



            createpromo.child(promocode).setValue(userid);
            progressDialog.cancel();
            checkreffercode();
            }
            else
        {
            Toast.makeText(SignupActivity.this, "Please Login Again", Toast.LENGTH_LONG).show();

            }
            }
    public void checkreffercode() {

        final String refferalcode = refercodegiven.getText().toString();
        if (refferalcode.equals("") || refferalcode.equals(promocode)) {
            Toast.makeText(SignupActivity.this, "Referral code is empty or not valid", Toast.LENGTH_LONG).show();
            } else {
            final DatabaseReference reffercoderef = FirebaseDatabase.getInstance().getReference().child("reffercode");
            final DatabaseReference agentwallet = FirebaseDatabase.getInstance().getReference();
            final DatabaseReference userwalletstat = FirebaseDatabase.getInstance().getReference("UserDb").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("refferal");
            final DatabaseReference userwallet = FirebaseDatabase.getInstance().getReference("UserDb").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("walletcoin");
            userwalletstat.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String statous = dataSnapshot.getValue(String.class);
                    if (statous == null) {

                        Toast.makeText(SignupActivity.this, "data is null in Refferal", Toast.LENGTH_LONG).show();


                    } else if (statous.equals("notcredited")) {
                        //here check all the feild....
                        reffercoderef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.child(refferalcode).exists()) {
                                    final String Uid = dataSnapshot.child(refferalcode).getValue(String.class);
                                    userwallet.setValue(String.valueOf(100));
                                    agentwallet.child("UserDb").child(Uid).child("walletcoin").addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            String agentbalance = dataSnapshot.getValue(String.class);
                                            Integer agentbalinint = Integer.parseInt(agentbalance);
                                            Integer updatedbalu = (agentbalinint + 100);
                                            agentwallet.child("UserDb").child(Uid).child("walletcoin").setValue(String.valueOf(updatedbalu));
                                            userwalletstat.setValue("credited");
                                            }
                                            @Override
                                        public void onCancelled(DatabaseError databaseError) {
                                            Toast.makeText(SignupActivity.this, "error on updating agent balance", Toast.LENGTH_LONG).show();
                                            }
                                    });
                                    } else {

                                    Toast.makeText(SignupActivity.this, "Refferal code not found", Toast.LENGTH_LONG).show();
                                    } }
                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Toast.makeText(SignupActivity.this, "Database error on checking refferal code", Toast.LENGTH_LONG).show();
                                }
                        });
                        //last
                    } else {
                        Toast.makeText(SignupActivity.this, "Both wallet has been credited", Toast.LENGTH_LONG).show();
                        } }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(SignupActivity.this, "Database error on checking account status", Toast.LENGTH_LONG).show();
                    }
            });
            } }
    private void initProgressBar(){
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mProgressBar.setVisibility(View.INVISIBLE);
    }
    private void hideSoftKeyboard(){
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }
}
