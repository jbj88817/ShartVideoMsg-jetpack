package us.bojie.shortvideomsg.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.databinding.BindingAdapter;

public class SVMImageView extends AppCompatImageView {
    public SVMImageView(Context context) {
        super(context);
    }

    public SVMImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SVMImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @BindingAdapter(value = {"image_url", "isCircle"}, requireAll = false)
    public static void setImageUrl(SVMImageView view, String imageUrl, boolean isCircle) {
        RequestBuilder<Drawable> builder = Glide.with(view).load(imageUrl);
        if (isCircle) {
            builder.transform(new CircleCrop());
        }
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        if (layoutParams != null && layoutParams.width > 0 && layoutParams.height > 0) {
            builder.override(layoutParams.width, layoutParams.height);
        }
        builder.into(view);
    }
}
