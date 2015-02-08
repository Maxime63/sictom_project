package com.savajolchauvet.isima.sictomproject.activity.navigation;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.savajolchauvet.isima.sictomproject.R;

import java.util.List;
import java.util.logging.Logger;

/**
 * Created by Maxime on 31/01/2015.
 */
public class CustomDrawerAdapter extends ArrayAdapter<DrawerItem> {
    private static final Logger logger = Logger.getLogger(CustomDrawerAdapter.class.getName());

    private Context mContext;
    private List<DrawerItem> mDrawerItemList;
    private int mLayoutResID;

    public CustomDrawerAdapter(Context context, int layoutResourceID,
                               List<DrawerItem> listItems) {
        super(context, layoutResourceID, listItems);
        this.mContext = context;
        this.mDrawerItemList = listItems;
        this.mLayoutResID = layoutResourceID;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(mLayoutResID, parent, false);
        }


        TextView itemName = (TextView) view.findViewById(R.id.drawer_itemName);
        ImageView icon = (ImageView) view.findViewById(R.id.drawer_icon);


        icon.setImageDrawable(view.getResources().getDrawable(mDrawerItemList.get(position).getImageId()));
        itemName.setText(mDrawerItemList.get(position).getTitle());


        return view;
    }
}
