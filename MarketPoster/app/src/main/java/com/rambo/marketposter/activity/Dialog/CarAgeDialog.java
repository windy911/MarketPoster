package com.rambo.marketposter.activity.Dialog;


import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.rambo.marketposter.R;


/**
 * 汽车车龄选择
 * @author Rambo
 *
 */
public class CarAgeDialog extends BaseDialogInBottom implements
        View.OnClickListener {
    private Context context;
    private View view;
    private ListView listView;
    private TextView titleDialog;
    private static final String[] CONTENTS = {"不限", "3年以内", "5年以内"};
    private int mCurrentSelected = -1;
    private OnSelectedDisplacementListener selectedItemListener;

    public CarAgeDialog(Context context, OnSelectedDisplacementListener listener, int selected) {
        super(context);
        // TODO Auto-generated constructor stub
        this.context = context;
        setOnSelectedItem(listener);
        mCurrentSelected = selected;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        view = LayoutInflater.from(context).inflate(R.layout.volume_diaolog, null);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(view);
        listView = (ListView) view.findViewById(R.id.listView);
        titleDialog=(TextView) view.findViewById(R.id.title_dialog);
        titleDialog.setText("车龄");
        listView.setAdapter(new MyAdapter());
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (selectedItemListener != null) {
                    selectedItemListener.onSelected(position);
                    CarAgeDialog.this.dismiss();
                }
            }
        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        }
    }


    public class MyAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return CONTENTS.length;
        }

        @Override
        public Object getItem(int position) {
            return (position >= 0 && position < CONTENTS.length) ? CONTENTS[position] : null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = LayoutInflater.from(context).inflate(R.layout.volume_list_item, null);
            TextView tvContent = (TextView) convertView.findViewById(R.id.tvContent);
            ImageView imgSelect = (ImageView) convertView.findViewById(R.id.imgSelected);
            tvContent.setText(CONTENTS[position]);
            imgSelect.setVisibility(position == mCurrentSelected ? View.VISIBLE : View.GONE);
            return convertView;
        }
    }


    private void setOnSelectedItem(OnSelectedDisplacementListener callback) {
        this.selectedItemListener = callback;
    }

    public interface OnSelectedDisplacementListener {
        void onSelected(int index);
    }

    public static String getContentPosition(int index) {
        if (index >= 0 && index < CONTENTS.length) {
            return CONTENTS[index];
        } else {
            return "";
        }
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        selectedItemListener = null;
    }
}
