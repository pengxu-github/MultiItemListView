package com.pengxu.test3rdjars;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

public class TestHttpUrlConnecttion extends Activity implements OnClickListener {

    private static final String TAG = "TestPicaso";
    private ListView lvPic;
    private Button refresh, bt;
    private ImageView mImageView;
    private ArrayList<String> dataList;
    private Runnable mRunnable;
    private Bitmap mBitmap;

    private static final String imageUrl = "http://www.51ps.com/upfile/2007/11/200711234317140356694.jpg";

    private static final int SET_IMAGE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_picaso);
        lvPic = (ListView) findViewById(R.id.lv_test_picaso);
        refresh = (Button) findViewById(R.id.refresh);
        bt = (Button) findViewById(R.id.bt);
        mImageView = (ImageView) findViewById(R.id.ig);

        refresh.setOnClickListener(this);
        bt.setOnClickListener(this);
        dataList = new ArrayList<String>();
        initData();
        lvPic.setVisibility(View.GONE);
        // lvPic.setAdapter(null);
        mRunnable = new Runnable() {
            @Override
            public void run() {
                try {
                    long startTime = System.currentTimeMillis();
                    URL url = new URL(imageUrl);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setDoInput(true);
                    conn.connect();
                    long connTime = System.currentTimeMillis();
                    Log.d(TAG, "connect time: " + (connTime - startTime) + "ms");
                    InputStream is = conn.getInputStream();
                    long getTime = System.currentTimeMillis();
                    Log.d(TAG, "get inputStream time: " + (getTime - connTime) + "ms");
                    Bitmap bitmap = BitmapFactory.decodeStream(is);
                    is.close();
                    Log.d(TAG, "decode time: " + (System.currentTimeMillis() - getTime) + "ms");
                    Message msg = new Message();
                    msg.what = SET_IMAGE;
                    msg.obj = bitmap;
                    mHandler.sendMessage(msg);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        new Thread(mRunnable).start();
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int id = msg.what;
            switch (id) {
                case SET_IMAGE:
                    setBitmap((Bitmap) msg.obj);
                    break;
                default:
                    break;
            }
        }
    };

    private void initData() {
        for (int i = 0; i < 10; i++) {
            dataList.add("item:\t" + i);
        }
    }

    private void setBitmap(Bitmap bitmap) {
        if (mImageView != null) {
            mBitmap = bitmap;
            mImageView.setImageBitmap(mBitmap);
            int size = mBitmap.getByteCount() / 1024;
            Log.d(TAG, "bitmap size: " + size + "kb, hight: " + mBitmap.getHeight() + ", width: " + mBitmap.getWidth());
        } else
            Log.d(TAG, "the mImageView is null");
    }

    private void refresh() {
        new Thread(mRunnable).start();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.bt:
                Log.d(TAG, "button is clicked");
                break;
            case R.id.refresh:
                refresh();
                break;
            default:
                break;
        }
    }
}
