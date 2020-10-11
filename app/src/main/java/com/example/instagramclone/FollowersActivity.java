package com.example.instagramclone;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toolbar;

import com.example.instagramclone.Adapter.CommentAdapter;
import com.example.instagramclone.Adapter.UserAdapter;
import com.example.instagramclone.Model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FollowersActivity extends Activity {

    String id,title;
    List<String> idList;

    RecyclerView recyclerView;
    UserAdapter userAdapter;
    List<User> userList;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_followers);

        Intent intent=getIntent();
        id=intent.getStringExtra("id");
        title=intent.getStringExtra("title");

        Toolbar toolbar=findViewById(R.id.toolbar);
        setActionBar(toolbar);
        getActionBar().setTitle(title);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        recyclerView=findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        userList=new ArrayList<>();
        userAdapter=new UserAdapter(this, userList,true);
        recyclerView.setAdapter(userAdapter);

        idList=new ArrayList<>();

        switch (title){
            case "likes":getLikes();
                        break;
            case "following":getFollowing();
                            break;
            case "followers":getFollowers();
                            break;
            case "views": getViews();
                        break;
        }
    }
    private void getViews() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Story").child(id)
                .child(getIntent().getStringExtra("storyid")).child("views");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                idList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    idList.add(dataSnapshot.getKey());
                }
                showUsers();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }




    private void getLikes() {
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Likes").child(id);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                idList.clear();
                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                    idList.add(dataSnapshot.getKey());
                }
                showUsers();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getFollowers() {
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Follow").child(id).child("followers");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                idList.clear();
                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                    idList.add(dataSnapshot.getKey());
                }
                showUsers();
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getFollowing(){
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Follow").child(id).child("following");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                idList.clear();
                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                    idList.add(dataSnapshot.getKey());
                }
                showUsers();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void showUsers(){
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Users ");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                    User user=dataSnapshot.getValue(User.class);
                    for(String id:idList){
                        if(user.getId().equals(id))
                            userList.add(user);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
