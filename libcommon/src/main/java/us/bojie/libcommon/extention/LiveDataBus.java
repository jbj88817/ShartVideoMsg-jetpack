package us.bojie.libcommon.extention;

import java.util.concurrent.ConcurrentHashMap;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

public class LiveDataBus {

    //
//    Handler mHandler = new Handler(Looper.getMainLooper()){
//        @Override
//        public void handleMessage(@NonNull Message msg) {
//            super.handleMessage(msg);
//        }
//    };
//    mHandler.sendMessage(msg)

    // normal event
    // LiveData mLiveData=null;
    // mLiveData.observer(this,new Observer<User>){
    //        void onChanged(User user){
    //
    //        }
    //    }
    //mLiveData.postValue(data);

    // sticky event
//    LiveData mLiveData=null;
//    mLiveData.postValue(data);
//    mLiveData.observer(this,new Observer<User>){
//        void onChanged(User user){
//
//        }
//    }

    private static class Lazy {
        static LiveDataBus sLiveDataBus = new LiveDataBus();
    }

    public static LiveDataBus get() {
        return Lazy.sLiveDataBus;
    }

    private ConcurrentHashMap<String, StickyLiveData> mHashMap = new ConcurrentHashMap<>();

    public StickyLiveData with(String eventName) {
        StickyLiveData liveData = mHashMap.get(eventName);
        if (liveData == null) {
            liveData = new StickyLiveData(eventName);
            mHashMap.put(eventName, liveData);
        }
        return liveData;
    }

    public class StickyLiveData<T> extends LiveData<T> {
        private String mEventName;
        private T mStickyData;
        private int mVersion = 0;

        public StickyLiveData(String eventName) {
            mEventName = eventName;
        }

        @Override
        protected void setValue(T value) {
            mVersion++;
            super.setValue(value);
        }

        @Override
        protected void postValue(T value) {
            mVersion++;
            super.postValue(value);
        }

        public void setStickyData(T stickyData) {
            mStickyData = stickyData;
            setValue(stickyData);
        }

        public void postStickyData(T stickyData) {
            mStickyData = stickyData;
            postValue(stickyData);
        }

        @Override
        public void observe(@NonNull LifecycleOwner owner, @NonNull Observer<? super T> observer) {
            observerSticky(owner, observer, false);
        }

        private void observerSticky(LifecycleOwner owner, Observer<? super T> observer, boolean sticky) {
            super.observe(owner, new WrapperObserver(this, observer, sticky));
            owner.getLifecycle().addObserver(new LifecycleEventObserver() {
                @Override
                public void onStateChanged(@NonNull LifecycleOwner source, @NonNull Lifecycle.Event event) {
                    if (event == Lifecycle.Event.ON_DESTROY) {
                        mHashMap.remove(mEventName);
                    }
                }
            });
        }

        private class WrapperObserver<T> implements Observer<T> {
            private StickyLiveData<T> mLiveData;
            private Observer<T> mObserver;
            private boolean mSticky;
            private int mLastVersion;

            public WrapperObserver(StickyLiveData<T> liveData, Observer<T> observer, boolean sticky) {

                mLiveData = liveData;
                mObserver = observer;
                mSticky = sticky;
                mLastVersion = mLiveData.mVersion;
            }

            @Override
            public void onChanged(T t) {
                if (mLastVersion >= mLiveData.mVersion) {
                    if (mSticky && mLiveData.mStickyData != null) {
                        mObserver.onChanged(mLiveData.mStickyData);
                    }
                    return;
                }

                mLastVersion = mLiveData.mVersion;
                mObserver.onChanged(t);
            }
        }
    }
}
