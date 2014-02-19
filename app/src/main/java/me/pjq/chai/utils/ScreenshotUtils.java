package me.pjq.chai.utils;

/**
 * Created by pjq on 11/9/13.
 */

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.*;
import android.view.View;
import me.pjq.chai.LocalPathResolver;
import me.pjq.chai.MyApplication;
import me.pjq.chai.R;
import net.sourceforge.simcpux.Util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class ScreenshotUtils {
    /**
     * @param pActivity
     * @return bitmap
     */
    public static Bitmap takeScreenShot(Activity pActivity, int excludeTop) {
        Bitmap bitmap = null;
        View view = pActivity.getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        bitmap = view.getDrawingCache();

        Rect frame = new Rect();
        view.getWindowVisibleDisplayFrame(frame);

        int stautsHeight = excludeTop;
        if (excludeTop <= 0) {
            stautsHeight = frame.top;
        }

        int width = pActivity.getWindowManager().getDefaultDisplay().getWidth();
        int height = pActivity.getWindowManager().getDefaultDisplay().getHeight();
        bitmap = Bitmap.createBitmap(bitmap, 0, stautsHeight, width, height - stautsHeight);
        view.setDrawingCacheEnabled(false);
        return bitmap;
    }

    public static Bitmap takeScreenShot(Activity pActivity) {
        return takeScreenShot(pActivity, 0);
    }


    /**
     * @param pBitmap
     */
    public static boolean savePic(Bitmap pBitmap, String strName) {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(strName);
            if (null != fos) {
                pBitmap.compress(Bitmap.CompressFormat.PNG, 90, fos);
                fos.flush();
                fos.close();
                return true;
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static String getshotFilePath() {
        String imagePath = LocalPathResolver.getCachePath("images");

        File file = new File(imagePath);
        if (!file.exists()) {
            file.mkdirs();
        }

        return imagePath + System.currentTimeMillis() + ".png";
    }

    public static String getshotFilePathByDay() {
        String imagePath = LocalPathResolver.getCachePath("images");

        File file = new File(imagePath);
        if (!file.exists()) {
            file.mkdirs();
        }

        return imagePath + Utils.time2DateKey("" + System.currentTimeMillis()) + ".png";
    }

    /**
     * @param pActivity
     */
    public static boolean shotBitmap(Activity pActivity, String filePath, int excludeTopHeight) {
        return ScreenshotUtils.savePic(takeScreenShot(pActivity, excludeTopHeight), filePath);
    }

    public static Bitmap shotBitmap2(Activity pActivity, String filePath) {
        Bitmap bitmap = takeScreenShot(pActivity);
        ScreenshotUtils.savePic(bitmap, filePath);
        return bitmap;
    }


    /**
     * @param pActivity
     */
    public static boolean drawTextToBitmap(Activity pActivity, String filePath, String text) {
        //return ScreenshotUtils.savePic(drawTextToBitmap(pActivity, R.drawable.bg, text), filePath);
        //return ScreenshotUtils.savePic(drawText(pActivity, R.drawable.bg_grey, text), filePath);
        return ScreenshotUtils.savePic(text2Bitmap(pActivity, text), filePath);
    }

    public static Bitmap drawTextToBitmap(Context gContext,
                                          int gResId,
                                          String gText) {
        Resources resources = gContext.getResources();
        float scale = resources.getDisplayMetrics().density;
        Bitmap bitmap =
                BitmapFactory.decodeResource(resources, gResId);

        android.graphics.Bitmap.Config bitmapConfig =
                bitmap.getConfig();
        // set default bitmap config if none
        if (bitmapConfig == null) {
            bitmapConfig = android.graphics.Bitmap.Config.ARGB_8888;
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
        paint.getTextBounds(gText, 0, gText.length(), bounds);
        int x = 10;
        int y = bounds.height();

        canvas.drawText(gText, x, y, paint);

        return bitmap;
    }

    public static Bitmap drawText(Context gContext, int gResId, String gText) {
        Resources resources = gContext.getResources();
        Bitmap bitmap =
                BitmapFactory.decodeResource(resources, gResId);

        android.graphics.Bitmap.Config bitmapConfig =
                bitmap.getConfig();
        // set default bitmap config if none
        if (bitmapConfig == null) {
            bitmapConfig = android.graphics.Bitmap.Config.ARGB_8888;
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

        drawMultilineText(gText, 10, 30, paint, canvas, (int) fontSize, bounds);

        return bitmap;
    }

    public static Bitmap text2Bitmap(Context gContext, String gText) {
        Resources resources = gContext.getResources();

        // new antialised Paint
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(resources.getColor(R.color.text_black_color));
        float fontSize = Utils.dpToPixels(MyApplication.getContext(), 14);

        paint.setTextSize((int) (fontSize));
        Rect bounds = new Rect();
        int width = (int) (MyApplication.mScreenWidth * 0.7f);
        int bottomPadding = 15;
        int topPadding = 40;
        int height = getMultilineTextHeight(gText, 10, topPadding, paint, (int) fontSize, width) + bottomPadding;
        bounds.set(0, 0, width - 15, height - 10);
        Bitmap bitmap = Bitmap.createBitmap(width, height, android.graphics.Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(resources.getColor(R.color.holo_green_light));

        drawMultilineText(gText, 10, topPadding, paint, canvas, (int) fontSize, bounds);

        return bitmap;
    }

    private static void drawMultilineText(String str, int x, int y, Paint paint, Canvas canvas, int fontSize, Rect drawSpace) {
        int lineHeight = 0;
        int yoffset = 0;
®        String[] lines = str.split("");

        // set height of each line (height of text + 20%)
        float scale = 1.1f;
        lineHeight = (int) (calculateHeightFromFontSize(str, fontSize) * scale);
        // draw each line
        String line = "";
        for (int i = 0; i < lines.length; ++i) {

            if (calculateWidthFromFontSize(line + "" + lines[i], fontSize) <= drawSpace.width()) {
                line = line + "" + lines[i];
            } else {
                canvas.drawText(line, x, y + yoffset, paint);
                yoffset = yoffset + lineHeight;
                line = lines[i];
            }
        }
        canvas.drawText(line, x, y + yoffset, paint);
    }

    private static int getMultilineTextHeight(String str, int x, int y, Paint paint, int fontSize, int width) {
        int lineHeight = 0;
        int yoffset = 0;
        String[] lines = str.split("");

        // set height of each line (height of text + 20%)
        float scale = 1.1f;
        lineHeight = (int) (calculateHeightFromFontSize(str, fontSize) * scale);
        // draw each line
        String line = "";
        for (int i = 0; i < lines.length; ++i) {
            if (calculateWidthFromFontSize(line + "" + lines[i], fontSize) <= width) {
                line = line + "" + lines[i];
            } else {
                yoffset = yoffset + lineHeight;
                line = lines[i];
            }
        }

        return y + yoffset + lineHeight;
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
