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

/**
 * Created by Maxime on 31/01/2015.
 */
public class CustomDrawerAdapter extends ArrayAdapter<DrawerItem> {
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
        // TODO Auto-generated method stub

        DrawerItemHolder drawerHolder;
        View view = convertView;

        if (view == null) {
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            drawerHolder = new DrawerItemHolder();

            view = inflater.inflate(mLayoutResID, parent, false);
            drawerHolder.ItemName = (TextView) view
                    .findViewById(R.id.drawer_itemName);
            drawerHolder.icon = (ImageView) view.findViewById(R.id.drawer_icon);

            view.setTag(drawerHolder);

        } else {
            drawerHolder = (DrawerItemHolder) view.getTag();

        }

        DrawerItem dItem = (DrawerItem) this.mDrawerItemList.get(position);

        drawerHolder.icon.setImageDrawable(view.getResources().getDrawable(
                dItem.getImageId()));
        drawerHolder.ItemName.setText(dItem.getTitle());

        return view;
    }

    private static class DrawerItemHolder {
        TextView ItemName;
        ImageView icon;
    }
}
