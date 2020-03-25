package br.com.smarttoolsapps.mapaviral;

import android.content.Context;
import android.content.DialogInterface;
import android.util.TypedValue;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;

import java.util.Timer;
import java.util.TimerTask;

import br.com.smarttoolsapps.mapaviral.R;

public class DialogHelper {

    private Context context;

    private MaterialDialog progressDialog;
    private MaterialDialog errorDialog;
    private MaterialDialog successDialog;

    public DialogHelper(Context context) {

        this.context = context;

        progressDialog = new MaterialDialog.Builder(context)
                .content(context.getResources().getString(R.string.dialog_wait))
                .progress(true, 0)
                .cancelable(false)
                .theme(Theme.DARK)
                .showListener(new DialogInterface.OnShowListener() {

                    @Override
                    public void onShow(DialogInterface dialogInterface) {

                        ((MaterialDialog)dialogInterface).getContentView().setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
                    }
                })
                .build();


        successDialog = new MaterialDialog.Builder(context)
                .title(context.getResources().getString(R.string.dialog_success))
                .positiveText("OK")
                .theme(Theme.DARK)
                .showListener(new DialogInterface.OnShowListener() {

                    @Override
                    public void onShow(DialogInterface dialogInterface) {

                        ((MaterialDialog)dialogInterface).getContentView().setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
                    }
                })
                .build();

        errorDialog = new MaterialDialog.Builder(context)
                .title(context.getResources().getString(R.string.dialog_sorry))
                .positiveText("OK")
                .theme(Theme.DARK)
                .showListener(new DialogInterface.OnShowListener() {

                    @Override
                    public void onShow(DialogInterface dialogInterface) {

                        ((MaterialDialog)dialogInterface).getContentView().setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
                    }
                })
                .build();
    }

    public void showProgress() {

        progressDialog.show();
    }

    public void dismissProgress() {

        progressDialog.dismiss();
    }

    public void showProgressDelayed(final int millisec, final Runnable runnable) {

        showProgress();

        new Timer().schedule(new TimerTask() {

            @Override
            public void run() {

                dismissProgress();

                try {

                    runnable.run();
                }
                catch (Exception e) { }
            }
        }, millisec);
    }

    public void showSuccess(String text) {

        successDialog.setContent(text);
        successDialog.show();
    }

    public void showError(String text) {

        errorDialog.setContent(text);
        errorDialog.show();
    }

    public void showList(String title, String[] list, MaterialDialog.ListCallback listCallBack) {

        new MaterialDialog.Builder(context)
                .title(title)
                .items(list)
                .itemsCallback(listCallBack)
                .show();
    }

    public void inputDialog(String title, String text, int inputType, MaterialDialog.InputCallback inputCallback) {

        new MaterialDialog.Builder(context)
                .title(title)
                .content(text)
                .inputType(inputType)
                .input("", "", inputCallback)
                .show();
    }

    public void confirmDialog(boolean cancelable, String title, String text, String negativeText, MaterialDialog.SingleButtonCallback inputCallback) {

        new MaterialDialog.Builder(context)
                .title(title)
                .content(text)
                .positiveText("OK")
                .negativeText(negativeText)
                .onPositive(inputCallback)
                .cancelable(cancelable)
                .show();
    }
}
