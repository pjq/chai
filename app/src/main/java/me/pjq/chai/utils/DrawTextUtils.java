package me.pjq.chai.utils;

/**
 * Created by pjq on 11/9/13.
 */

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.*;
import me.pjq.chai.MyApplication;
import me.pjq.chai.R;

public class DrawTextUtils {

    /**
     * @param activity
     */
    public static boolean drawTextToBitmap(Activity activity, String filePath, String text, boolean withLogo) {
        Bitmap bitmap = null;
        if (withLogo) {
            bitmap = text2BitmapWithLogo(activity, text);
        } else {
            bitmap = text2Bitmap(activity, text);
        }

        return ScreenshotUtils.savePic(bitmap, filePath);
    }

    public static Bitmap drawTextToBitmap(Context context, int backgroundResId, String text) {
        Resources resources = context.getResources();
        float scale = resources.getDisplayMetrics().density;
        Bitmap bitmap =
                BitmapFactory.decodeResource(resources, backgroundResId);

        Bitmap.Config bitmapConfig =
                bitmap.getConfig();
        // set default bitmap config if none
        if (bitmapConfig == null) {
            bitmapConfig = Bitmap.Config.ARGB_8888;
        }
        // resource bitmaps are imutable,
        // so we need to convert it to mutable one
        bitmap = bitmap.copy(bitmapConfig, true);

        Canvas canvas = new Canvas(bitmap);
        // new antialised Paint
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        // text color - #3D3D3D
        paint.setColor(Color.rgb(61, 61, 61));
        // text size in pixels
        paint.setTextSize((int) (14 * scale));
        // text shadow
        paint.setShadowLayer(1f, 0f, 1f, Color.WHITE);

        // draw text to the Canvas center
        Rect bounds = new Rect();
        paint.getTextBounds(text, 0, text.length(), bounds);
        int x = 10;
        int y = bounds.height();

        canvas.drawText(text, x, y, paint);

        return bitmap;
    }

    public static Bitmap drawText(Context context, int backgroundResId, String text) {
        Resources resources = context.getResources();
        Bitmap bitmap =
                BitmapFactory.decodeResource(resources, backgroundResId);

        Bitmap.Config bitmapConfig =
                bitmap.getConfig();
        // set default bitmap config if none
        if (bitmapConfig == null) {
            bitmapConfig = Bitmap.Config.ARGB_8888;
        }
        // resource bitmaps are imutable,
        // so we need to convert it to mutable one
        bitmap = bitmap.copy(bitmapConfig, true);

        Canvas canvas = new Canvas(bitmap);
        // new antialised Paint
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        // text color - #3D3D3D
        paint.setColor(resources.getColor(R.color.text_black_color));
        // text size in pixels
        float fontSize = Utils.dpToPixels(MyApplication.getContext(), 14);

        paint.setTextSize((int) (fontSize));
        Rect bounds = new Rect();
        bounds.set(0, 0, bitmap.getWidth() - 15, bitmap.getHeight() - 10);

        drawMultilineText(text, 10, 30, paint, canvas, (int) fontSize, bounds);

        return bitmap;
    }

    public static Bitmap text2BitmapWithLogo(Context context, String text) {
        return text2Bitmap(context, text, R.drawable.pjq_me_144);
    }

    public static Bitmap text2Bitmap(Context context, String text) {
        return text2Bitmap(context, text, -1);
    }

    public static Bitmap text2Bitmap(Context context, String text, int logoResId) {
        Resources resources = context.getResources();
        Bitmap logo = null;
        if (logoResId <= 0) {

        } else {
            logo = BitmapFactory.decodeResource(resources, logoResId);
        }

        // new antialised Paint
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(resources.getColor(R.color.text_black_color));
        float fontSize = Utils.dpToPixels(MyApplication.getContext(), 14);

        paint.setTextSize((int) (fontSize));
        Rect bounds = new Rect();
        int width = (int) (MyApplication.mScreenWidth * 0.9f);
        int bottomPadding = 15;
        int topPadding = 70;
        if (null != logo) {
            bottomPadding = logo.getHeight();
        }
        int height = calculateMultilineTextHeight(text, 10, topPadding, paint, (int) fontSize, width) + bottomPadding;
        bounds.set(0, 0, width - 15, height - 10);
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(resources.getColor(R.color.bg_color_white));

        drawMultilineText(text, 10, topPadding, paint, canvas, (int) fontSize, bounds);

        if (null != logo) {
            drawLogo(context, logo, canvas, paint);
        }

        return bitmap;
    }

    public static void drawLogo(Context context, Bitmap logo, Canvas canvas, Paint paint) {
        canvas.drawBitmap(logo, canvas.getWidth() - logo.getWidth() - 10, canvas.getHeight() - logo.getHeight() - 10, paint);
    }

    private static void drawMultilineText(String str, int x, int y, Paint paint, Canvas canvas, int fontSize, Rect drawSpace) {
        int lineHeight = 0;
        int yoffset = 0;
        String[] lines = str.split("");

        // set height of each line (height of text + 20%)
        float scale = 1.1f;
        lineHeight = (int) (calculateHeightFromFontSize(str, fontSize) * scale);
        // draw each line
        String line = "";
        for (int i = 0; i < lines.length; ++i) {
            if (lines[i].equalsIgnoreCase("\n")) {
                canvas.drawText(line, x, y + yoffset, paint);
                yoffset = yoffset + lineHeight;
                line = "";
                continue;
            }

            if (calculateWidthFromFontSize(line + "" + lines[i], fontSize) > drawSpace.width() || (lines[i].equalsIgnoreCase("\n"))) {
                canvas.drawText(line, x, y + yoffset, paint);
                yoffset = yoffset + lineHeight;
                line = lines[i];
            } else {
                line = line + "" + lines[i];
            }

//            if (calculateWidthFromFontSize(line + "" + lines[i], fontSize) <= drawSpace.width()||!(lines[i].equalsIgnoreCase("\n"))) {
//                line = line + "" + lines[i];
//            } else {
//                canvas.drawText(line, x, y + yoffset, paint);
//                yoffset = yoffset + lineHeight;
//                line = lines[i];
//            }
        }
        canvas.drawText(line, x, y + yoffset, paint);
    }

    private static int calculateMultilineTextHeight(String str, int x, int y, Paint paint, int fontSize, int width) {
        int lineHeight = 0;
        int yoffset = 0;
        String[] lines = str.split("");

        // set height of each line (height of text + 20%)
        float scale = 1.1f;
        lineHeight = (int) (calculateHeightFromFontSize(str, fontSize) * scale);
        // draw each line
        String line = "";
        for (int i = 0; i < lines.length; ++i) {
            if (lines[i].equalsIgnoreCase("\n")) {
                yoffset = yoffset + lineHeight;
                line = "";
                continue;
            }

            if (calculateWidthFromFontSize(line + "" + lines[i], fontSize) > width) {
                yoffset = yoffset + lineHeight;
                line = lines[i];
            } else {
                line = line + "" + lines[i];
            }
        }

        return y + yoffset + lineHeight * 2;
    }

    private static int calculateWidthFromFontSize(String testString, int currentSize) {
        Rect bounds = new Rect();
        Paint paint = new Paint();
        paint.setTextSize(currentSize);
        paint.getTextBounds(testString, 0, testString.length(), bounds);
        int value = (int) Math.ceil(bounds.width());

        return value;
    }

    private static int calculateHeightFromFontSize(String testString, int currentSize) {
        Rect bounds = new Rect();
        Paint paint = new Paint();
        paint.setTextSize(currentSize);
        paint.getTextBounds(testString, 0, testString.length(), bounds);

        return (int) Math.ceil(bounds.height());
    }
}
