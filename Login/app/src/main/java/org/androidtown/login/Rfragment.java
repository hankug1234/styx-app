package org.androidtown.login;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class Rfragment extends Fragment {
    ArrayAdapter MyAdater;
    ArrayList<String> list ;
    ListView listView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        ViewGroup re = (ViewGroup)inflater.inflate(R.layout.rfragment,container,false);
        list = new ArrayList<String>();
        loadDB(list);
        listView = re.findViewById(R.id.Rlistview);
        MyAdater = new ArrayAdapter(getActivity(),android.R.layout.simple_list_item_1,list);
        listView.setAdapter(MyAdater);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(parent.getContext(),"i am working",Toast.LENGTH_SHORT).show();
            }
        });


        return re;
    }

    public void loadDB(ArrayList<String> a) // 추후에 데이터 베이스와 연동해서 군현
    {
        a.add("i am work");
    }

}
