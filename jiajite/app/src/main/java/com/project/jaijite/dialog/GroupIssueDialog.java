package com.project.jaijite.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;

import com.project.jaijite.R;

public class GroupIssueDialog extends Dialog {
    public GroupIssueDialog(Context context) {
        super(context);
        init();
    }

    public GroupIssueDialog(Context context, int themeResId) {
        super(context, themeResId);
        init();
    }

    protected GroupIssueDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        init();
    }

    private void init() {
        setContentView(R.layout.dialog_group_issue);
        findViewById(R.id.sure).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }
}
