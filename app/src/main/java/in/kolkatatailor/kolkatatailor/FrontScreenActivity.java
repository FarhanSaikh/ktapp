package in.kolkatatailor.kolkatatailor;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class FrontScreenActivity extends AppCompatActivity {
    EditText code,number;
    Button continuebtton;
    TextView skipbtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_front_screen);
         hideSoftKeyboard();

         code=findViewById(R.id.countrycode);
        number=findViewById(R.id.editTextPhone);
        continuebtton=findViewById(R.id.buttonContinue);
        skipbtn=findViewById(R.id.skipbutton);
        code.setText(String.format("+%s", GetCountryZipCode()));

        //skip button
        skipbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(FrontScreenActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        continuebtton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String countycode = code.getText().toString().trim();
                String mobileno = number.getText().toString().trim();
                if(mobileno.isEmpty() || mobileno.length() < 10) {
                    number.setError("Valid number is required");
                    number.requestFocus();
                    //return;
                    }

                else {
                    String phoneNumber = countycode + mobileno;
                    Intent intent=new Intent(FrontScreenActivity.this,VerifyPhoneActivity.class);
                    intent.putExtra("phonenumber", phoneNumber);
                    intent.putExtra("comingfrom","loginactivty");
                    startActivity(intent);
                    finish();
                    } }});
        }


        public String GetCountryZipCode(){
        String CountryID="";
        String CountryZipCode="";
        TelephonyManager manager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        //getNetworkCountryIso
        CountryID= manager.getSimCountryIso().toUpperCase();
        String[] rl=this.getResources().getStringArray(R.array.CountryCodes);
        for(int i=0;i<rl.length;i++){
            String[] g=rl[i].split(",");
            if(g[1].trim().equals(CountryID.trim())){
                CountryZipCode=g[0];
                break;
            }
        }

        return CountryZipCode;
    }


    private void hideSoftKeyboard(){
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }
}
