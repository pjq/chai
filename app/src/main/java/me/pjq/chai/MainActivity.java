package me.pjq.chai;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.tencent.mm.sdk.openapi.BaseReq;
import com.tencent.mm.sdk.openapi.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import me.pjq.chai.service.DictionService;
import me.pjq.chai.utils.DrawTextUtils;
import me.pjq.chai.utils.ScreenshotUtils;
import me.pjq.chai.utils.Utils;
import me.pjq.chai.utils.WeChatUtils;

import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends ActionBarActivity implements View.OnClickListener, IWXAPIEventHandler {
    private static final String TAG = MainActivity.class.getSimpleName();

    private WeChatUtils weChatUtils;

    private DashboardFragment dashboardFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            dashboardFragment = DashboardFragment.createFragment("");
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, dashboardFragment, DashboardFragment.TAG)
                    .commit();
        } else {
            dashboardFragment = (DashboardFragment) getSupportFragmentManager().findFragmentByTag(DashboardFragment.TAG);
        }

        initWeChat();
    }

    @Override
    protected void onStart() {
        super.onStart();

        handleExtraIntent();
    }

    private void handleExtraIntent() {
        // Get intent, action and MIME type
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if ("text/plain".equals(type)) {
                handleSendText(intent); // Handle text being sent
            } else if (type.startsWith("image/")) {
                handleSendImage(intent); // Handle single image being sent
            }
        } else if (Intent.ACTION_SEND_MULTIPLE.equals(action) && type != null) {
            if (type.startsWith("image/")) {
                handleSendMultipleImages(intent); // Handle multiple images being sent
            }
        } else {
            // Handle other intents, such as being started from the home screen
        }
    }

    void handleSendText(Intent intent) {
        String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
        if (sharedText != null) {
            // Update UI to reflect text being shared
            EditText input = dashboardFragment.getInputEditText();

            input.setText(sharedText);
        }
    }

    void handleSendImage(Intent intent) {
        Uri imageUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
        if (imageUri != null) {
            // Update UI to reflect image being shared
        }
    }

    void handleSendMultipleImages(Intent intent) {
        ArrayList<Uri> imageUris = intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM);
        if (imageUris != null) {
            // Update UI to reflect multiple images being shared
        }
    }

    private void initWeChat() {
        weChatUtils = WeChatUtils.getInstance(this);
        if (weChatUtils.getIWXAPI().isWXAppInstalled()) {
            weChatUtils.register();
            weChatUtils.getIWXAPI().handleIntent(getIntent(), this);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        if (weChatUtils.getIWXAPI().isWXAppInstalled()) {
            getMenuInflater().inflate(R.menu.main_wechat, menu);
        } else {
            getMenuInflater().inflate(R.menu.main, menu);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_about) {
            Utils.showAbout(this);

            if (ApplicationConfig.INSTANCE.DEBUG()) {
                DictionService.getInstance().updateFromServer();
            }
        } else if (id == R.id.action_share) {
            showShare();
        } else if (id == R.id.action_share_wechat) {
            showShareWeChat();
        } else if (id == R.id.action_helper) {
            Utils.showUserGuard(this, 0);
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        setIntent(intent);
        handleExtraIntent();

        weChatUtils.getIWXAPI().handleIntent(intent, this);
    }

    @Override
    public void onReq(BaseReq baseReq) {
        EFLogger.i(TAG, baseReq.toString());
    }

    @Override
    public void onResp(BaseResp baseResp) {
        EFLogger.i(TAG, baseResp.toString());

    }

    @Override
    public void onClick(View v) {

    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class DashboardFragment extends Fragment {
        public static final String TAG = DashboardFragment.class.getSimpleName();

        private EditText input;
        private Button convert;
        private TextView result;
        private boolean converted = false;

        public static DashboardFragment createFragment(String string) {
            DashboardFragment fragment = new DashboardFragment();

            Bundle bundle = new Bundle();
            bundle.putString("TEXT", string);
            fragment.setArguments(bundle);

            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            initView(rootView);

            return rootView;
        }

        private void initView(View container) {
            input = (EditText) container.findViewById(R.id.input);
            convert = (Button) container.findViewById(R.id.convert);
            result = (TextView) container.findViewById(R.id.result);

            Bundle bundle = getArguments();
            String text = bundle.getString("TEXT");
            input.setText(text);

            convert.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (!converted) {
                        String outputString = getConvertedText();
                        result.setText(outputString);
                        convert.setText(getString(R.string.unconvert));
                    } else {
                        result.setText(getText());
                        convert.setText(getString(R.string.convert));

                    }

                    converted = !converted;
                }
            });
        }

        public Button getConvertButton() {
            return convert;
        }

        public String getText() {
            return input.getText().toString();
        }

        public String getConvertedText() {
            String inputString = input.getText().toString();
            String outputString = DictionService.getInstance().convert(inputString);
            return outputString;
        }

        public String getResultText() {
            String text = result.getText().toString();

            if (TextUtils.isEmpty(text)) {
                text = getText();
            }

            return text;
        }

        public EditText getInputEditText() {
            return input;
        }
    }


    private String shareFileName = ScreenshotUtils.getshotFilePathByDay();

    private void showShare() {
        final String text = dashboardFragment.getResultText();
        takeScreenshot();
        Utils.share(MainActivity.this, MainActivity.this.getString(R.string.app_name), text, shareFileName);
    }

    private void showShareWeChat() {
        final String text = dashboardFragment.getResultText();
//        Bitmap bitmap = ScreenshotUtils.shotBitmap2(this, shareFileName);
        Bitmap bitmap = DrawTextUtils.text2BitmapWithLogo(getApplicationContext(), text);
        ScreenshotUtils.savePic(bitmap, shareFileName);

        weChatUtils.createAppendReq(bitmap, this.getString(R.string.app_name), text, shareFileName);

        Intent intent = new Intent();
        intent.setClass(this, me.pjq.chai.activity.SendToWXActivity.class);
        startActivity(intent);

        Utils.overridePendingTransitionRight2Left(this);
    }

    private void sendToWeChat() {
        final String text = dashboardFragment.getResultText();
        ScreenshotUtils.shotBitmap2(this, shareFileName);
        weChatUtils.createAppendReq2(this.getString(R.string.app_name), text, shareFileName);

        Intent intent = new Intent();
        intent.setClass(this, me.pjq.chai.activity.SendToWXActivity.class);
        startActivity(intent);

        Utils.overridePendingTransitionRight2Left(this);
    }

    private void takeScreenshot() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                DrawTextUtils.drawTextToBitmap(MainActivity.this, shareFileName, dashboardFragment.getResultText(), true);
            }
        });
    }
}
