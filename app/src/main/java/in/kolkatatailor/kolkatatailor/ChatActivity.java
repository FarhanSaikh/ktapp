package in.kolkatatailor.kolkatatailor;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    public static final String ANONYMOUS = "anonymous";
    public static final int DEFAULT_MSG_LENGTH_LIMIT = 1000;
    public static final String MESSAGE_LENGTH_KEY = "message_length";
    public static final int RC_SIGN_IN = 1;
    private static final int RC_PHOTO_PICKER = 2;
    private ListView mMessageListView;
    private MessageAdapter mMessageAdapter;
    private ProgressBar mProgressBar;
    private ImageButton mPhotoPickerButton, mSendButton;
    private EditText mMessageEditText;
    private String mUsername;
    private TextView tailornameTv,tailorTypeTv;
    private ImageView tailorDp;
   private String tailorId, tailorname,tailortype,tailorphotourl;
    //fab
    private LinearLayout groupbutton;
    //Firebase Instance Variables
    private FirebaseDatabase database;
    private DatabaseReference mFirebaseReference,notificationDb;
    private ChildEventListener mChildEventListener;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private FirebaseStorage mFirebaseStorage;
    private StorageReference mChatPhotosReference;
    String nameuser, email,address, phoneuser, useridU;
    String customerid;

     String status;
    TextView helpbtton;
    int unreadcount=1;
   // String tailorname, tailor
    //String userId;
    DatabaseReference usersCommonInstance, userOnlineStat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        getSupportActionBar().hide();

        //Initializing Firebase Object
        database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mFirebaseStorage = FirebaseStorage.getInstance();
        if (FirebaseAuth.getInstance().getCurrentUser()!=null) {
            //TODO for customer app , we need to get tailor id from intent and for tailor app we need to get customer id from intent


            tailorId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        } else {
            tailorId="ssdhsdsdhsd";
        }
        usersCommonInstance=FirebaseDatabase.getInstance().getReference();
        userOnlineStat=FirebaseDatabase.getInstance().getReference();
        // TODO this need be change for tailor admin app
        //getting from Update user info function





            //to user id
        //mRemoteConfig = FirebaseRemoteConfig.getInstance();
        // Initialize references to views
        tailorDp=findViewById(R.id.tailorimage);
        tailornameTv=findViewById(R.id.tailorname);
        tailorTypeTv=findViewById(R.id.tailortype);

        helpbtton=findViewById(R.id.helpbutton);
        helpbtton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ChatActivity.this, GenerateInvoice.class);
                intent.putExtra("customeruid", customerid);
                startActivity(intent);

            }
        });

        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mMessageListView = (ListView) findViewById(R.id.messageListView);
        mPhotoPickerButton = (ImageButton) findViewById(R.id.photoPickerButton);
        mMessageEditText = (EditText) findViewById(R.id.messageEditText);
        mSendButton = (ImageButton) findViewById(R.id.sendButton);

        // Initialize message ListView and its adapter
        final List<FriendlyMessage> friendlyMessages = new ArrayList<>();
        mMessageAdapter = new MessageAdapter(this, R.layout.item_message, friendlyMessages);
        mMessageListView.setAdapter(mMessageAdapter);
        mMessageListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Boolean isPhoto = mMessageAdapter.getItem(position).getPhotoUrl() != null;
                if (isPhoto) {
                    String imgURL = mMessageAdapter.getItem(position).getPhotoUrl();
                    //Toast.makeText(MainActivity.this, "This a photo",Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(ChatActivity.this, SingleImageActivity.class);
                    intent.putExtra("imgUrl", imgURL);
                    startActivity(intent);
                }
            }
        });

        // Initialize progress bar
        mProgressBar.setVisibility(ProgressBar.INVISIBLE);
        // ImagePickerButton shows an image picker to upload a image for a message
        mPhotoPickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.setType("image/jpeg");
                i.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                startActivityForResult(Intent.createChooser(i, "Complete action using"), RC_PHOTO_PICKER);
            }
        });


        // Enable Send button when there's text to send
        mMessageEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() > 0) {
                    mSendButton.setEnabled(true);
                    mSendButton.setVisibility(View.VISIBLE);
                } else {
                    mSendButton.setEnabled(false);
                    mSendButton.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        mMessageEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(DEFAULT_MSG_LENGTH_LIMIT)});
        // Send button sends a message and clears the EditText
        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userid = mAuth.getCurrentUser().getUid();
                //String date = (DateFormat.format("dd-MM,hh:mm", new java.util.Date()).toString());
                String mydate = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
                FriendlyMessage message = new FriendlyMessage(mMessageEditText.getText().toString(), mUsername, null, userid, mydate);

                //Todo , need to check if recever is offline then save here or else dont send here

                mFirebaseReference.push().setValue(message);

                //check if user online then do not set notification message
                //if offline then only create notification message

                Map<String, String> notification = new HashMap<String, String>();
                notification.put("from",tailorId);
                notification.put("type","message request");

                if(status.equals("Offline")) {

                        notificationDb.push().setValue(notification);
                      //set badge
                    usersCommonInstance.child("UserDb").child(customerid).child("chats").child(tailorId).child("unread").setValue(String.valueOf(unreadcount));
                    unreadcount=unreadcount+1;

                }

                        // Clear input box
                mMessageEditText.setText("");
            }
        });

        //Authenticating Users
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    //user signed-in
                    OnSignedIn(mUsername);
                    //Toast.makeText(MainActivity.this, "You are Signed-in.Welcome to Friendly Chat App.",Toast.LENGTH_SHORT).show();
                }
            }
        };
        }
    @Override
    protected void onStart() {
        super.onStart();
        customerid=getIntent().getStringExtra("customeruid");
        if (customerid==null){
            customerid="KPtfsmNBlPVJQPlU3RR1WGkPyqiiyj";
            Intent intent = new Intent(ChatActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
        mFirebaseReference = FirebaseDatabase.getInstance().getReference().child("TailorDB").child(tailorId).child("chats").child(customerid).child("chats").child("message");
        notificationDb=FirebaseDatabase.getInstance().getReference().child("notifications").child(customerid);
        mChatPhotosReference = mFirebaseStorage.getReference().child("TailorDB").child(tailorId).child("chats").child(customerid).child("photos");
        userOnlineStat.child("Users").child(tailorId).child("status").setValue("Online");

        //Tailor Db ref where message will be saved.TODO Need to be changed for tailor app.

        Update_userinfo();
        Update_Tailorinfo();
        UpdateCustomerStat();




    }

 @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_PHOTO_PICKER && resultCode == RESULT_OK) {
            Uri imageUri = data.getData();
            final ProgressDialog progressDialog = new ProgressDialog(ChatActivity.this);
            progressDialog.setTitle("Sending...");
            progressDialog.show();
            progressDialog.setCancelable(false);
            //get the reference to stored file at database
            final StorageReference photoReference = mChatPhotosReference.child(imageUri.getLastPathSegment());

            //upload file to firebase
            photoReference.putFile(imageUri).addOnSuccessListener(ChatActivity.this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    photoReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                         Uri downloadUrl=uri;

                         //Uri downloadUrl = taskSnapshot.getDownloadUrl();
                            String userid = mAuth.getCurrentUser().getUid();
                            //String date = (DateFormat.format("dd-MM,hh:mm", new java.util.Date()).toString());
                            String mydate = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
                            FriendlyMessage message = new FriendlyMessage(null, mUsername, downloadUrl.toString(), userid, mydate);
                            mFirebaseReference.push().setValue(message);
                            Toast.makeText(ChatActivity.this, "Sent", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }  });

                }


            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(ChatActivity.this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                            progressDialog.setMessage("Uploaded " + (int) progress + "%");
                        }
                    });
        }
    }

    protected void OnSignedIn(String userName) {
        mUsername = userName;
        attachDatabaseReadListener();
    }

    @Override
    public void onResume() {
        super.onResume();
        mAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();

        userOnlineStat.child("Users").child(tailorId).child("status").setValue("Offline");
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mAuthStateListener != null) {
            mAuth.removeAuthStateListener(mAuthStateListener);
        }
        detachDatabaseReadListener();
        mMessageAdapter.clear();
    }

    protected void attachDatabaseReadListener() {
        if (mChildEventListener == null) {
            mChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    FriendlyMessage friendlyMessage = dataSnapshot.getValue(FriendlyMessage.class);
                    mMessageAdapter.add(friendlyMessage);
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            };
            mFirebaseReference.addChildEventListener(mChildEventListener);
        }
    }

    protected void detachDatabaseReadListener() {
        if (mChildEventListener != null) {
            mFirebaseReference.removeEventListener(mChildEventListener);
            mChildEventListener = null;
        }
    }
    public void Update_userinfo(){
        if (FirebaseAuth.getInstance().getCurrentUser()!=null && customerid!=null) {
            usersCommonInstance.child("UserDb").child(customerid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()){
                        UserModel userModel= dataSnapshot.getValue(UserModel.class);
                        // Toast.makeText(TailorList.this, "Its working customer name is :"+userModel.getName(), Toast.LENGTH_SHORT).show();
                        nameuser=userModel.getName();
                        tailornameTv.setText(nameuser);
                        email=userModel.getEmailid();
                        phoneuser=userModel.getPhonenumber();
                        address=userModel.getAddress();
                        useridU=userModel.getUserid();
                        //mUsername=nameuser;
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }


    public void Update_Tailorinfo(){

        if (FirebaseAuth.getInstance().getCurrentUser()!=null && tailorId!=null) {

            usersCommonInstance.child("TailorDB").child(tailorId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()){
                        TailorModel tailorModel= dataSnapshot.getValue(TailorModel.class);
                        // Toast.makeText(TailorList.this, "Its working customer name is :"+userModel.getName(), Toast.LENGTH_SHORT).show();
                        tailorname=tailorModel.getName();
                        mUsername=tailorname;
                        tailortype=tailorModel.getType();
                        tailorphotourl=tailorModel.getPhotourl();

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }


    public void UpdateCustomerStat(){

          userOnlineStat.child("Users").child(customerid).child("status").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if (dataSnapshot.exists()){

                        status=dataSnapshot.getValue(String.class);

                        }
                    }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }

    }






