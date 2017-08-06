package com.example.qwexo.menubasket;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

/**
 * Created by qwexo on 2017-07-24.
 */

public class ImageGlide {
    StorageReference storageReference;
    public void getImage(final Context context, String userID, final String menu, final ImageView view){
        storageReference = FirebaseStorage.getInstance().getReference().child("market").child(userID).child("menu").child(menu + ".jpg");
        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(context)
                        .load(uri)
                        .into(view);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, "서버 연결 실패", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        });
    }
}
