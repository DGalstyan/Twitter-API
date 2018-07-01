package twiter.ginosi.com.twiterapi.helpers;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.AttrRes;
import android.support.annotation.ColorInt;
import android.util.TypedValue;

/**
 * Created by Davit Galstyan on 6/30/18.
 */
public class ColorHelper {
    public static  @ColorInt
    int getAttributeColor(Context context, @AttrRes int colorAttr){
        TypedValue typedValue = new TypedValue();
        if (context.getTheme().resolveAttribute(colorAttr, typedValue, true))
            return typedValue.data;
        else
            return Color.BLACK;
    }

    public static void setTint(Context context, @AttrRes int colorAttr, Drawable... drawables) {
        int color = getAttributeColor(context, colorAttr);

        for(Drawable drawable : drawables){
            if(drawable != null) {
                drawable.setColorFilter(color, PorterDuff.Mode.SRC_IN);
            }
        }
    }

    public static int getContrastColor(int colorIntValue) {
        int red = Color.red(colorIntValue);
        int green = Color.green(colorIntValue);
        int blue = Color.blue(colorIntValue);
        double lum = (((0.299 * red) + ((0.587 * green) + (0.114 * blue))));
        return lum > 186 ? 0xFF000000 : 0xFFFFFFFF;
    }
}
