package isymphonyz.akotv.customview;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by Dooplus on 11/26/15 AD.
 */

public class RSUTextView extends TextView {

    Typeface tf;

    public RSUTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        tf = Typeface.createFromAsset(context.getAssets(), "fonts/rsu-light.ttf");
        this.setTypeface(tf);
    }

    public RSUTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        tf = Typeface.createFromAsset(context.getAssets(), "fonts/rsu-light.ttf");
        this.setTypeface(tf);
    }

    public RSUTextView(Context context) {
        super(context);
        tf = Typeface.createFromAsset(context.getAssets(), "fonts/rsu-light.ttf");
        this.setTypeface(tf);
    }
}
