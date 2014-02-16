package me.pjq.chai;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
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
import me.pjq.chai.utils.ScreenshotUtils;
import me.pjq.chai.utils.Utils;
import me.pjq.chai.utils.WeChatUtils;

public class MainActivity extends ActionBarActivity implements View.OnClickListener, IWXAPIEventHandler {
    WeChatUtils weChatUtils;

    DashboardFragment dashboardFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            dashboardFragment = new DashboardFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, dashboardFragment, DashboardFragment.TAG)
                    .commit();
        } else {
            dashboardFragment = (DashboardFragment) getSupportFragmentManager().findFragmentByTag(DashboardFragment.TAG);
        }

        initWeChat();
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
        weChatUtils.getIWXAPI().handleIntent(intent, this);
    }

    @Override
    public void onReq(BaseReq baseReq) {

    }

    @Override
    public void onResp(BaseResp baseResp) {

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

        public DashboardFragment() {
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

            convert.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String outputString = getConvertedText();

                    result.setText(outputString);
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
    }


    private String shareFileName = ScreenshotUtils.getshotFilePathByDay();

    private void showShare() {
        final String text = dashboardFragment.getConvertedText();
        takeScreenshot();
        Utils.share(MainActivity.this, MainActivity.this.getString(R.string.app_name), text, shareFileName);
    }

    private void showShareWeChat() {
        final String text = dashboardFragment.getConvertedText();
        Bitmap bitmap = ScreenshotUtils.shotBitmap2(this, shareFileName);
        weChatUtils.createAppendReq(bitmap, this.getString(R.string.app_name), text, shareFileName);

        Intent intent = new Intent();
        intent.setClass(this, me.pjq.chai.activity.SendToWXActivity.class);
        startActivity(intent);

        Utils.overridePendingTransitionRight2Left(this);
    }

    private void sendToWeChat() {
        final String text = dashboardFragment.getConvertedText();
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
                Button convert = dashboardFragment.getConvertButton();
                int y = convert.getTop() + convert.getHeight();

//                ScreenshotUtils.shotBitmap(MainActivity.this, shareFileName, (int)y);
                ScreenshotUtils.drawTextToBitmap(MainActivity.this, shareFileName, dashboardFragment.getConvertedText());
            }
        });
    }
}
