package in.kolkatatailor.kolkatatailor;

import android.content.Intent;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class GenerateInvoice extends AppCompatActivity {

    EditText servicenameEd,servicePriceEd,servicequantityEd;
   Button addServiceButton;

   DatabaseReference invoicedblocation;
   TextView previewandsendButton;

   String customerUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate_invoice);
        getSupportActionBar().setTitle("Create Invoice");


        customerUid=getIntent().getStringExtra("customeruid");

        //need to change

        //customerUid=FirebaseAuth.getInstance().getCurrentUser().getUid();

        //get customer uid for the purpose of address

        invoicedblocation=FirebaseDatabase.getInstance().getReference().child("TemporaryInvoice").child(customerUid);
        servicenameEd=findViewById(R.id.service_name);
        servicePriceEd=findViewById(R.id.service_price);
        servicequantityEd=findViewById(R.id.service_quantity);
        addServiceButton=findViewById(R.id.add_button);
        previewandsendButton=findViewById(R.id.preview_send);


        addServiceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //check feilds are vaild or not

                   String servicename=servicenameEd.getText().toString();
                   int price=Integer.parseInt(servicePriceEd.getText().toString());
                   int quantity=Integer.parseInt(servicequantityEd.getText().toString());



                if (servicenameEd.getText().toString().equals("")){
                    servicenameEd.setError("Required");

                    }
                else if (servicePriceEd.getText().toString().equals("")){
                    servicePriceEd.setError("Required");
                    }
                else if (servicequantityEd.getText().toString().equals("")){
                    servicequantityEd.setError("Required");
                    }
                    else {

                    int total=quantity*price;
                    ServiceModel serviceModel= new ServiceModel(servicename,price,quantity,total);
                    invoicedblocation.push().setValue(serviceModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            Toast.makeText(GenerateInvoice.this, "Saved", Toast.LENGTH_SHORT).show();

                            servicenameEd.setText("");
                            servicePriceEd.setText("");
                            servicequantityEd.setText("");

                            previewandsendButton.setVisibility(View.VISIBLE);




                        }
                    });

                    }
                }
        });


        previewandsendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(GenerateInvoice.this,InvoicePreview.class);

                intent.putExtra("customeruid",customerUid);

                startActivity(intent);



            }
        });

    }







}
