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
public class AKOTVHomeMenuListAdapter extends BaseAdapter {
    private Activity activity;
    private static LayoutInflater inflater=null;
    //ImageLoader imageLoader;
    Typeface tf;

    private ArrayList<Integer> logoList = null;
    private ArrayList<String> nameList = null;

    //public LazyAdapter(Activity a, String[] d) {
    public AKOTVHomeMenuListAdapter(Activity a) {
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
            vi = inflater.inflate(R.layout.home_menu_list_item, null);

            int margin = convertDpToPx(8);

            holder=new ViewHolder();
            //holder.layout = (RelativeLayout) vi.findViewById(R.id.layout);
            holder.imgLogo = (ImageView) vi.findViewById(R.id.imgLogo);
            holder.txtName = (RSUTextView) vi.findViewById(R.id.txtName);

            vi.setTag(holder);
        }
        else
            holder=(ViewHolder)vi.getTag();

        holder.imgLogo.setImageResource(logoList.get(position));
        holder.txtName.setText(nameList.get(position));

        return vi;
    }

    private int convertDpToPx(int dp){
        return Math.round(dp*(activity.getResources().getDisplayMetrics().xdpi/ DisplayMetrics.DENSITY_DEFAULT));
    }
}
