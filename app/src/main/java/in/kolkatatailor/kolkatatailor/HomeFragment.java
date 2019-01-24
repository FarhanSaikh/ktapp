package in.kolkatatailor.kolkatatailor;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class HomeFragment extends Fragment {
    RecyclerView mGrouplist;
    LinearLayoutManager mLayoutManager;
    DatabaseReference mDatabase,usersCommonInstance;
    String groupRef,username;
    Button joinButton;
    String sub_push_Key;
    String userId;
    FloatingActionButton fab;

    private String tailorId, tailorname,tailortype,tailorphotourl;

    String nameuser, email,address, phoneuser, useridU;
    // Required empty public constructor

    public HomeFragment() {
        }

        @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        }
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        mGrouplist = view.findViewById(R.id.groupsin);

        usersCommonInstance=FirebaseDatabase.getInstance().getReference();
            if (FirebaseAuth.getInstance().getCurrentUser()!=null) {
            tailorId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        } else {
            tailorId="ssdhsdsdhsd";

        }
        mDatabase = FirebaseDatabase.getInstance().getReference().child("TailorDB").child(tailorId).child("chats");
        mGrouplist.setHasFixedSize(true);
        // mpostlist.setLayoutManager(new LinearLayoutManager(PostList.this));
        mLayoutManager = new LinearLayoutManager(getContext());
        mLayoutManager.setReverseLayout(false);
        mLayoutManager.setStackFromEnd(false);
        // Now set the layout manager and the adapter to the RecyclerView
        mGrouplist.setLayoutManager(mLayoutManager);
        joinButton=view.findViewById(R.id.joingroupTv);
        joinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getContext(),TailorList.class);
                startActivity(intent);
                //getActivity().finish();
                }
        });


        //floating action buttton
        fab = view.findViewById(R.id.fab);
        return view; }

        @Override
        public void onStart() {
        super.onStart();

        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    final ProgressDialog progressDialog = new ProgressDialog(getContext());
                    progressDialog.setTitle("Loading Chats..");
                    progressDialog.setMessage("Please wait..");
                    progressDialog.setCancelable(true);
                    progressDialog.show();
                    FirebaseRecyclerAdapter<UserModel, PostViewFolder> PSVF = new FirebaseRecyclerAdapter<UserModel,HomeFragment.PostViewFolder>(
                            UserModel.class,
                            R.layout.chatlistformat,
                            HomeFragment.PostViewFolder.class,
                            mDatabase) {

                        @Override
                        protected void populateViewHolder(final HomeFragment.PostViewFolder viewHolder, final UserModel model, final int position) {
                            viewHolder.setName(model.getName());
                            viewHolder.setTime(model.getTime());
                            viewHolder.setUnread(model.getUnread());
                            progressDialog.dismiss();
                           final String groupKey = getRef(position).getKey();

                            viewHolder.mview.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    groupRef = groupKey;
                                    Intent intent=new Intent(getActivity().getBaseContext(),ChatActivity.class);
                                    intent.putExtra("customeruid",groupKey);
                                    mDatabase.child(groupKey).child("unread").setValue("0");
                                    startActivity(intent);
                                }});
                        }};
                    mGrouplist.setAdapter(PSVF);

                }

                else {

                    Toast.makeText(getContext(), "No Chats Availble now", Toast.LENGTH_SHORT).show();

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


        public void setTime(String time) {
            TextView namegroup = (TextView) mview.findViewById(R.id.date);
            namegroup.setText(time);
        }
        void setUnread(String unread) {
            TextView unredaTv = (TextView) mview.findViewById(R.id.unreadcount);
            unredaTv.setText(unread);
            if (Integer.parseInt(unread)>0){

                unredaTv.setVisibility(View.VISIBLE);

            }

        }

        }


    public void Update_Tailorinfo(){

        if (FirebaseAuth.getInstance().getCurrentUser()!=null && tailorId!=null) {

            usersCommonInstance.child("TailorDB").child(tailorId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()){
                        TailorModel tailorModel= dataSnapshot.getValue(TailorModel.class);
                        // Toast.makeText(TailorList.this, "Its working customer name is :"+userModel.getName(), Toast.LENGTH_SHORT).show();
                        tailorname=tailorModel.getName();
                        tailortype=tailorModel.getType();
                        tailorphotourl=tailorModel.getPhotourl();

                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }


}
class CircleTransform implements Transformation {
    @Override
    public Bitmap transform(Bitmap source) {
        int size = Math.min(source.getWidth(), source.getHeight());
        int x = (source.getWidth() - size) / 2;
        int y = (source.getHeight() - size) / 2;
        Bitmap squaredBitmap = Bitmap.createBitmap(source, x, y, size, size);
        if (squaredBitmap != source) {
            source.recycle();
        }
        Bitmap bitmap = Bitmap.createBitmap(size, size, source.getConfig());
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        BitmapShader shader = new BitmapShader(squaredBitmap,
                BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP);
        paint.setShader(shader);
        paint.setAntiAlias(true);
        float r = size / 2f;
        canvas.drawCircle(r, r, r, paint);
        squaredBitmap.recycle();
        return bitmap;
    }

    @Override
    public String key() {
        return "circle";
    }
}