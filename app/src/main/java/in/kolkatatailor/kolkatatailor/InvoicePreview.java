package in.kolkatatailor.kolkatatailor;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;

public class InvoicePreview extends AppCompatActivity {

    String customerUid;
    DatabaseReference invoicedblocation;

    RecyclerView mGrouplist;
    LinearLayoutManager mLayoutManager;
    int pricetotal;
    TextView totalamount, invoice_date_TV,bill_to_tv;
    private static final int PERMISSION_REQUEST_CODE = 1000;

    Button generateInvoiceBUTTON;
    ImageView companyLogo;


    // Storage Permissions

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {

            case PERMISSION_REQUEST_CODE: {

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();

                } else {

                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();

                }

            }
            break;

        }
    }
    @RequiresApi(api = Build.VERSION_CODES.M)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(Build.VERSION.SDK_INT>=24){
            try{
                Method m = StrictMode.class.getMethod("disableDeathOnFileUriExposure");
                m.invoke(null);
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        setContentView(R.layout.activity_invoice_preview);


       getSupportActionBar().hide();
       mGrouplist = findViewById(R.id.item_list);
       totalamount=findViewById(R.id.total_amount);
       invoice_date_TV=findViewById(R.id.date_invoice);
       bill_to_tv=findViewById(R.id.bill_to);
        companyLogo=findViewById(R.id.company_logo);

        //set logo into invoice

       //set invoice date
        long date = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        SimpleDateFormat timeonly = new SimpleDateFormat("hh:mm");
        String dateString = sdf.format(date);
        String timeString = timeonly.format(date);
        //show present date
        invoice_date_TV.setText("INVOICE  \n \n"+dateString);

        //show present time
        //TextView.setText(timeString);



        generateInvoiceBUTTON=findViewById(R.id.generate_invoice);
        if (ActivityCompat.checkSelfPermission(InvoicePreview.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            requestPermissions(new String[]{
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, PERMISSION_REQUEST_CODE);


       generateInvoiceBUTTON.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {



                       if (ActivityCompat.checkSelfPermission(InvoicePreview.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)

                       {
                           Toast.makeText(InvoicePreview.this, "You should Grant Permission", Toast.LENGTH_SHORT).show();
                           requestPermissions(new String[]{
                                   Manifest.permission.WRITE_EXTERNAL_STORAGE
                           }, PERMISSION_REQUEST_CODE);

                       }
                       else {

                           generateInvoiceBUTTON.setVisibility(View.GONE);
                           takeScreenshot();




                       }

                       }
       });


        // mCheckUser = FirebaseDatabase.getInstance().getReference().child("Users").child("AllUsers").child(userId).child("stclass");
        //mGrouplist.setHasFixedSize(true);
         //mGrouplist.setLayoutManager(new LinearLayoutManager(InvoicePreview.this));

        mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setReverseLayout(false);
        mLayoutManager.setStackFromEnd(false);
        // Now set the layout manager and the adapter to the RecyclerView
        mGrouplist.setLayoutManager(mLayoutManager);
        //floating action buttton
    }

    @Override
    public void onStart() {
        super.onStart();
        customerUid=getIntent().getStringExtra("customeruid");
        invoicedblocation=FirebaseDatabase.getInstance().getReference().child("TemporaryInvoice").child(customerUid);

        invoicedblocation.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){

                    for (DataSnapshot pricedatasnapshot: dataSnapshot.getChildren()){
                    int   getEachTotal=pricedatasnapshot.child("total").getValue(Integer.class);


                    pricetotal=pricetotal+getEachTotal;
                    totalamount.setText("Total:                     ₹"+pricetotal);

                    }


                    final ProgressDialog progressDialog = new ProgressDialog(InvoicePreview.this);
                    progressDialog.setTitle("Loading  List..");
                    progressDialog.setMessage("Please wait..");
                    progressDialog.setCancelable(true);
                    progressDialog.show();
                    FirebaseRecyclerAdapter<ServiceModel, InvoicePreview.PostViewFolder> PSVF = new FirebaseRecyclerAdapter<ServiceModel,InvoicePreview.PostViewFolder>(
                            ServiceModel.class,
                            R.layout.itemlayout,
                            InvoicePreview.PostViewFolder.class,
                            invoicedblocation) {
                        @Override
                        protected void populateViewHolder(final InvoicePreview.PostViewFolder viewHolder, final ServiceModel model, final int position) {


                            viewHolder.setServicename(model.getServicename());


                            viewHolder.setQuantity(model.getQuantity());
                            viewHolder.setTotal(model.getTotal());
                            viewHolder.setPrice(model.getPrice());
                            progressDialog.cancel();
                            // final String groupKey = getRef(position).getKey();
                            viewHolder.mview.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {


                                }
                            });
                        }
                    };
                    mGrouplist.setAdapter(PSVF);

                }

                else {

                    Toast.makeText(InvoicePreview.this, "wrong", Toast.LENGTH_SHORT).show();
                }
            }



            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    public static class PostViewFolder extends RecyclerView.ViewHolder {
        View mview;

        public PostViewFolder(View itemView) {
            super(itemView);
            mview = itemView;
        }

        void setServicename(String servicename) {
            TextView namegroup = (TextView) mview.findViewById(R.id.service_name1);
            namegroup.setText(servicename);
        }
        void setPrice(int price) {

            TextView pricetv = (TextView) mview.findViewById(R.id.service_price1);
            pricetv.setText("₹"+price);

        }

        public void setQuantity(int quantity) {
            TextView quanity = (TextView) mview.findViewById(R.id.service_quantity1);
            quanity.setText(""+quantity);

        }
        public void setTotal(int total) {
            TextView quanity = (TextView) mview.findViewById(R.id.service_total_price1);
            quanity.setText("₹"+total);

        }


    }

    private void takeScreenshot() {
        Date now = new Date();
        android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss", now);

        try {
            // image naming and path  to include sd card  appending name you choose for file
            String mPath = Environment.getExternalStorageDirectory().toString() + "/" + now + ".jpg";

            // create bitmap screen capture
            View v1 = getWindow().getDecorView().getRootView();
            v1.setDrawingCacheEnabled(true);
            Bitmap bitmap = Bitmap.createBitmap(v1.getDrawingCache());
            v1.setDrawingCacheEnabled(false);

            File imageFile = new File(mPath);

            FileOutputStream outputStream = new FileOutputStream(imageFile);
            int quality = 100;
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
            outputStream.flush();
            outputStream.close();
            openScreenshot(imageFile);
            Toast.makeText(InvoicePreview.this, "Invoice saved to gallery ", Toast.LENGTH_LONG).show();

        } catch (Throwable e) {
            // Several error may come out with file handling or DOM
            e.printStackTrace();
        }
    }

    private void openScreenshot(File imageFile) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        Uri uri = Uri.fromFile(imageFile);
        intent.setDataAndType(uri, "image/*");
        startActivity(intent);
    }


    public void updateCustomeraddress(){


    }

    }

