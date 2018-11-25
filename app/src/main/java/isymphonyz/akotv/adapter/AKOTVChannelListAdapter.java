package isymphonyz.akotv.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.ArrayList;

import isymphonyz.akotv.R;
import isymphonyz.akotv.customview.RSUTextView;

/**
 * Created by Dooplus on 12/5/15 AD.
 */
public class AKOTVChannelListAdapter extends BaseAdapter {
    private Activity activity;
    private static LayoutInflater inflater=null;
    //ImageLoader imageLoader;
    Typeface tf;

    private ArrayList<Integer> logoList = null;
    private ArrayList<String> nameList = null;
    private ArrayList<String> titleList = null;
    private ArrayList<Boolean> isFavoriteList = null;

    //public LazyAdapter(Activity a, String[] d) {
    public AKOTVChannelListAdapter(Activity a) {
        activity = a;
        //imageLoader = new ImageLoader(activity);
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //tf = Typeface.createFromAsset(activity.getAssets(), "fonts/rsu-light.ttf");
    }

    public void setLogoList(ArrayList<Integer> logoList) {
        this.logoList = logoList;
    }
    public void setNameList(ArrayList<String> nameList) {
        this.nameList = nameList;
    }
    public void setTitleList(ArrayList<String> titleList) {
        this.titleList = titleList;
    }

    public void setIsFavoriteList(ArrayList<Boolean> isFavoriteList) {
        this.isFavoriteList = isFavoriteList;
    }

    public int getCount() {
        //return data.length;
        return nameList.size();
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    public static class ViewHolder{
        public RelativeLayout layout;
        public ImageView imgLogo;
        public ImageView btnFavorite;
        public RSUTextView txtName;
        public RSUTextView txtTitle;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        View vi=convertView;
        ViewHolder holder;
        if(convertView==null){
            vi = inflater.inflate(R.layout.home_channel_list_item, null);

            int margin = convertDpToPx(8);

            holder=new ViewHolder();
            //holder.layout = (RelativeLayout) vi.findViewById(R.id.layout);
            holder.imgLogo = (ImageView) vi.findViewById(R.id.imgLogo);
            holder.btnFavorite = (ImageView) vi.findViewById(R.id.btnFavorite);
            holder.txtName = (RSUTextView) vi.findViewById(R.id.txtName);
            holder.txtTitle = (RSUTextView) vi.findViewById(R.id.txtTitle);

            vi.setTag(holder);
        }
        else
            holder=(ViewHolder)vi.getTag();

        holder.imgLogo.setImageResource(logoList.get(position));
        holder.txtName.setText(nameList.get(position));
        holder.txtTitle.setText(titleList.get(position));

        if(isFavoriteList.get(position)) {
            holder.btnFavorite.setImageResource(R.mipmap.ic_drawer_01);
        } else {
            holder.btnFavorite.setImageResource(R.mipmap.ic_launcher);
        }

        return vi;
    }

    private int convertDpToPx(int dp){
        return Math.round(dp*(activity.getResources().getDisplayMetrics().xdpi/ DisplayMetrics.DENSITY_DEFAULT));
    }
}
