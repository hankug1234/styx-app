package org.androidtown.login;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class customFragment extends Fragment {

    public static customFragment makeInstance(String s)
    {
        customFragment re = new customFragment();
        Bundle b = new Bundle();
        b.putString("title",s);
        re.setArguments(b);

        return re;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragmentlayout,container,false);
        TextView t = v.findViewById(R.id.fra);
        t.setText(getArguments().getString("title"));


        return v;

    }
}
