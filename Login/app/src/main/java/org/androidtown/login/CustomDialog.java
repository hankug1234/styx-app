package org.androidtown.login;

import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class CustomDialog {

    private Context context;
    String phone;
    int nowValue;
    public CustomDialog(Context context,String phone) {
        this.context = context;
        this.phone = phone;
    }

    public void callFunction() {

        DatabaseReference life = FirebaseDatabase.getInstance().getReference("userlist");
        Query lifePoint = life.orderByChild("phone").equalTo(phone);
        final Dialog dlg = new Dialog(context);

        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);

        dlg.setContentView(R.layout.custom_dialog);
        dlg.show();
        final Button okButton = (Button) dlg.findViewById(R.id.okButton);
        final Button cancelButton = (Button) dlg.findViewById(R.id.cancelButton);
        final TextView add = (TextView)dlg.findViewById(R.id.title);
        final TextView point = (TextView)dlg.findViewById(R.id.current);
        final android.widget.ProgressBar bar = (android.widget.ProgressBar)dlg.findViewById(R.id.progressBar);

         lifePoint.addChildEventListener(new ChildEventListener() {
             @Override
             public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                 if(dataSnapshot.hasChild("point"))
                 {
                     nowValue = dataSnapshot.child("point/").getValue(int.class);
                     point.setText(Integer.toString(nowValue));
                     bar.setProgress(nowValue);
                 }
                 else
                 {
                     point.setText(Integer.toString(100));
                     bar.setProgress(100);
                 }
             }

             @Override
             public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                 if(dataSnapshot.hasChild("point"))
                 {
                     nowValue = dataSnapshot.child("point/").getValue(int.class);
                     point.setText(Integer.toString(nowValue));
                     bar.setProgress(nowValue);
                 }
                 else
                 {
                     point.setText(Integer.toString(100));
                     bar.setProgress(100);
                 }

             }

             @Override
             public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

             }

             @Override
             public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

             }

             @Override
             public void onCancelled(@NonNull DatabaseError databaseError) {

             }
         });





        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dlg.dismiss();
            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "취소", Toast.LENGTH_SHORT).show();
                dlg.dismiss();
            }
        });
    }
}
