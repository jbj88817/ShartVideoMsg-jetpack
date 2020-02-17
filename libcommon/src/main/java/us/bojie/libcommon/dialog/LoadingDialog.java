package us.bojie.libcommon.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import us.bojie.libcommon.R;

public class LoadingDialog extends AlertDialog {

    private TextView loadingText;

    public LoadingDialog(Context context) {
        super(context);
    }

    protected LoadingDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    protected LoadingDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    public void setLoadingText(String loadingText) {
        if (this.loadingText != null) {
            this.loadingText.setText(loadingText);
        }
    }

    @Override
    public void show() {
        super.show();
        setContentView(R.layout.layout_loading_view);
        loadingText = findViewById(R.id.loading_text);
        Window window = getWindow();
        WindowManager.LayoutParams attributes = window.getAttributes();
        attributes.width = WindowManager.LayoutParams.MATCH_PARENT;
        attributes.height = WindowManager.LayoutParams.MATCH_PARENT;
        attributes.gravity = Gravity.CENTER;
        attributes.dimAmount = 0.35f;
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        window.setAttributes(attributes);
    }
}
