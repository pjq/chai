package me.pjq.chai;

import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;

/**
 * Created by pjq on 12/8/13.
 */
public class BaseActionBarActivity extends ActionBarActivity {

    protected ActionBar getActionBarImpl(){
        return getSupportActionBar();
    }
}
