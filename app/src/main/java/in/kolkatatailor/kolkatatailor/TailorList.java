package in.kolkatatailor.kolkatatailor;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.Calendar;

public class TailorList extends AppCompatActivity {
    RecyclerView mGrouplist;
    LinearLayoutManager mLayoutManager;
    DatabaseReference mDatabase,mCheckUser;
    String groupRef,username;
    Button joinButton, loginbutton;
    String sub_push_Key;
    DatabaseReference usersCommonInstance;
    String nameuser, email,address, phoneuser, useridU;
    String userId;
    FloatingActionButton fab;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tailor_list);
        loginbutton=findViewById(R.id.login);

        mGrouplist = findViewById(R.id.tailorlist);
        if (FirebaseAuth.getInstance().getCurrentUser()!=null) {
            userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        } else {

            userId="ssdhsdsdhsd";

            }
            usersCommonInstance=FirebaseDatabase.getInstance().getReference();

          Update_userinfo();

        mDatabase = FirebaseDatabase.getInstance().getReference().child("TailorDB");
        // mCheckUser = FirebaseDatabase.getInstance().getReference().child("Users").child("AllUsers").child(userId).child("stclass");
        mGrouplist.setHasFixedSize(true);
        // mpostlist.setLayoutManager(new LinearLayoutManager(PostList.this));
        mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setReverseLayout(false);
        mLayoutManager.setStackFromEnd(false);
        // Now set the layout manager and the adapter to the RecyclerView
        mGrouplist.setLayoutManager(mLayoutManager);
        //floating action buttton
        fab = findViewById(R.id.fab);

        loginbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent =new Intent(TailorList.this, FrontScreenActivity.class);
                startActivity(intent);
                finish(); }
        });

        }

    @Override
    public void onStart() {
        super.onStart();

        if (FirebaseAuth.getInstance().getCurrentUser()==null){
            loginbutton.setVisibility(View.VISIBLE);

        }


        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    final ProgressDialog progressDialog = new ProgressDialog(TailorList.this);
                    progressDialog.setTitle("Loading Tailor List..");
                    progressDialog.setMessage("Please wait..");
                    progressDialog.setCancelable(true);
                    progressDialog.show();
                    FirebaseRecyclerAdapter<TailorModel, TailorList.PostViewFolder> PSVF = new FirebaseRecyclerAdapter<TailorModel,TailorList.PostViewFolder>(
                            TailorModel.class,
                            R.layout.chatlistformat,
                            TailorList.PostViewFolder.class,
                            mDatabase) {
                        @Override
                        protected void populateViewHolder(final TailorList.PostViewFolder viewHolder, final TailorModel model, final int position) {
                            viewHolder.setName(model.getName());
                            viewHolder.setTime(model.getTime());
                            viewHolder.setPhotourl(TailorList.this,model.getPhotourl());
                            progressDialog.cancel();
                            // final String groupKey = getRef(position).getKey();
                            viewHolder.mview.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    if (FirebaseAuth.getInstance().getCurrentUser() == null) {

                                        UseAuthStat(model.getName());



                                    } else {

                                        //Chat dialoge before confirming it.
                                        ConfirmChat(model.getUserid(),model.getName(), model.getPhotourl(),model.getTailorcontact(),model.getType());





                                    }

                                }
                            });
                        }
                    };
                    mGrouplist.setAdapter(PSVF);

                }

                else

                    {

                    Toast.makeText(TailorList.this,"Welcome",Toast.LENGTH_LONG).show();
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

        public void setName(String name) {
            TextView namegroup = (TextView) mview.findViewById(R.id.name);
            namegroup.setText(name);
        }
        public void setPhotourl(Context ctx, String photourl) {
            ImageView profilepic = mview.findViewById(R.id.groupimage);
            Picasso.with(ctx).load(photourl).transform(new CircleTransform()).into(profilepic);

        }

        public void setTime(String time) {
            TextView namegroup = (TextView) mview.findViewById(R.id.date);
            namegroup.setText(time);
        }

    }

    public void ConfirmChat(final String uid ,
                            final String name ,
                            final String  photourl,
                            final String number,
                            final String type )
    {

        AlertDialog.Builder builder = new AlertDialog.Builder(TailorList.this);
            builder.setTitle("Confirmation");
            builder.setIcon(R.drawable.ic_schedule_black_24dp);

              final DatabaseReference UserDbRefforchat =usersCommonInstance.child("UserDb").child(userId).child("chats");

            builder.setMessage("Let's Chat with, "+name)
                    .setPositiveButton("Proceed", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                            String joindate = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());

                            //Step 1
                            // Create chat shortcut in user's db ,under chats. unique id

                            TailorModel tailorModel=new TailorModel(uid,name,photourl,joindate,number,type);
                           //note: under user db with tailors unique id.
                            UserDbRefforchat.child(uid).setValue(tailorModel);

                            //step 2
                            //create chat in Tailor's db.tailorid.usersid, SO on entering chat activty chat activity requires tailors unique id and user unique id
                            //this is under tailor db, User data going to tailor db and taiilos data going to customer db

                            UserModel userModel=new UserModel(nameuser,useridU,phoneuser,address,joindate,email,null);
                            mDatabase.child(uid).child("chats").child(userId).setValue(userModel);

                            //step 3
                            // Go to Chat activity with user id and tailor unique id to start chats or to fetch chats.


                            Intent intent =new Intent(TailorList.this,ChatActivity.class);
                            intent.putExtra("tailorid",uid);
                            intent.putExtra("customerid",userId);
                            intent.putExtra("tailorname",name);
                            intent.putExtra("tailorphotourl",photourl);
                            intent.putExtra("customername",nameuser);
                            startActivity(intent);


                        }

                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();

                        }
                    });


            builder.show();


    }

    public void UseAuthStat( String name  ){
        AlertDialog.Builder builder = new AlertDialog.Builder(TailorList.this);
        builder.setTitle("Please login to proceed");

        builder.setIcon(R.drawable.ic_schedule_black_24dp);
        builder.setMessage("Please login/Sign up to chat with selected Tailor, "+name)
                .setPositiveButton("Login / Sign Up", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();

                        Intent intent =new Intent(TailorList.this, FrontScreenActivity.class);
                        startActivity(intent);
                        finish();

                    }

                })
                .setNegativeButton("No, Thanks", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();

                    }
                });

        builder.show();
        }
    public void Update_userinfo(){

        if (FirebaseAuth.getInstance().getCurrentUser()!=null) {
            usersCommonInstance.child("UserDb").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()){
                        UserModel userModel= dataSnapshot.getValue(UserModel.class);
                       // Toast.makeText(TailorList.this, "Its working customer name is :"+userModel.getName(), Toast.LENGTH_SHORT).show();
                        nameuser=userModel.getName();
                         email=userModel.getEmailid();
                         phoneuser=userModel.getPhonenumber();
                         address=userModel.getAddress();
                         useridU=userModel.getUserid();

                    }
                    }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            }
        }

}
