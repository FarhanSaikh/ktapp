package in.kolkatatailor.kolkatatailor;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;

import java.util.UUID;

import dmax.dialog.SpotsDialog;

public class SingleImageActivity extends AppCompatActivity {
    ImageView img;
    String urlimg;
    private static final int PERMISSION_REQUEST_CODE = 1000;
   Button dowload,sharebutton;


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
        setContentView(R.layout.activity_single_image);


        dowload = findViewById(R.id.download);
        sharebutton = findViewById(R.id.share);

        img=(ImageView) findViewById(R.id.singleImg);
        urlimg=getIntent().getExtras().getString("imgUrl");
        Loadimg();





        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            requestPermissions(new String[]{
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, PERMISSION_REQUEST_CODE);




        dowload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(SingleImageActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)

                {
                    Toast.makeText(SingleImageActivity.this, "You should Grant Permission", Toast.LENGTH_SHORT).show();
                    requestPermissions(new String[]{
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                    }, PERMISSION_REQUEST_CODE);

                }
                else {

                    AlertDialog dialog= new SpotsDialog(SingleImageActivity.this);

                    dialog.show();
                    dialog.setMessage("Downloading...");
                    String filename= UUID.randomUUID().toString()+".jpg";
                    Picasso
                            .with(getBaseContext())
                            .load(urlimg)
                            .into(new SaveImageHelper(getBaseContext(),
                                    dialog,
                                    getApplicationContext().getContentResolver(),
                                    filename,
                                    "Download"));

                    // Toast.makeText(SingleImageActivity.this, "Image Saved", Toast.LENGTH_SHORT).show();

                }
            }

        });
        sharebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(SingleImageActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)

                {
                    Toast.makeText(SingleImageActivity.this, "You should Grant Permission", Toast.LENGTH_SHORT).show();
                    requestPermissions(new String[]{
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                    }, PERMISSION_REQUEST_CODE);

                }
                else {

                    AlertDialog dialog= new SpotsDialog(SingleImageActivity.this);

                    dialog.show();
                    dialog.setMessage("Please wait...");
                    String filename= UUID.randomUUID().toString()+".jpg";
                    Picasso
                            .with(getBaseContext())
                            .load(urlimg)
                            .into(new SaveImageHelper(getBaseContext(),
                                    dialog,
                                    getApplicationContext().getContentResolver(),
                                    filename,
                                    "Share"));

                    // Toast.makeText(SingleImageActivity.this, "Image Saved", Toast.LENGTH_SHORT).show();

                }
            }

        });

    }

    public void Loadimg(){
        ProgressDialog progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.show();
        Glide.with(img.getContext())
                .load(urlimg)
                .placeholder(R.drawable.ic_photo_black_24dp)

                .into(img);
        progressDialog.cancel();
    }

    }

