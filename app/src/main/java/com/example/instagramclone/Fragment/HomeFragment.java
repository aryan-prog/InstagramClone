package com.example.instagramclone.Fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.example.instagramclone.Adapter.PostAdapter;
import com.example.instagramclone.Adapter.StoryAdapter;
import com.example.instagramclone.Model.Post;
import com.example.instagramclone.Model.Story;
import com.example.instagramclone.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private PostAdapter postAdapter;
    private List<Post> postLists;
    private List<String> followingList;

    private RecyclerView recyclerView_story;
    private StoryAdapter storyAdapter;
    private List<Story> storyList;


    ProgressBar progressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_home, container, false);

        recyclerView= view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        postLists=new ArrayList<>();
        postAdapter =new PostAdapter(getContext(),postLists);
        recyclerView.setAdapter(postAdapter);

        recyclerView_story= view.findViewById(R.id.recycler_view_story);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager1=new LinearLayoutManager(getContext(),
                LinearLayoutManager.HORIZONTAL,false);
        recyclerView_story.setLayoutManager(linearLayoutManager1);
        storyList=new ArrayList<>();
        storyAdapter =new StoryAdapter(getContext(),storyList);
        recyclerView_story.setAdapter(storyAdapter);

        progressBar=view.findViewById(R.id.progress_circular);


        checkFollowing();

        return view;
    }
    private void checkFollowing(){
        followingList =new ArrayList<>();

        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Follow")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Following");

        reference.addValueEventListener((new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                followingList.clear();
                for (DataSnapshot snapshot1:snapshot.getChildren())
                    followingList.add(snapshot1.getKey());
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        }));
        readPosts();
        readStory();
    }
    private void readPosts(){
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Posts");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postLists.clear();
                for(DataSnapshot snapshot1:snapshot.getChildren()){
                    Post post=snapshot1.getValue(Post.class);
                    for(String id:followingList){
                        if(post.getPublisher().equals(id))
                            postLists.add(post);
                    }
                }
                postAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void readStory(){
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Story");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                long timecurrent = System.currentTimeMillis();
                storyList.clear();
                storyList.add(new Story("",0,0,"",
                        FirebaseAuth.getInstance().getCurrentUser().getUid()));
                for(String id:followingList) {
                    int countStory=0;
                    Story story=null;

                    for (DataSnapshot dataSnapshot : snapshot.child(id).getChildren()) {
                         story = dataSnapshot.getValue(Story.class);
                        if (timecurrent > story.getTimestart() && timecurrent < story.getTimeend())
                            countStory++;
                    }
                    if (countStory>0) {
                        storyList.add(story);
                    }
                }
                storyAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}
