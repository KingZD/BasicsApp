
package com.project.jaijite.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.project.jaijite.R;

public class ManualActivity extends Activity implements OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual1);
    }

    @Override
    public void onClick(View arg0) {
        finish();
    }

}
