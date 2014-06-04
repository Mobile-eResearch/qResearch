/**
Copyright 2013-2014 Bhavesh Hirpara
Copyright 2014 Henrik Reichmann

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */

package de.eresearch.app.gui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;

import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.PauseOnScrollListener;
import com.nostra13.universalimageloader.utils.StorageUtils;

import de.eresearch.app.R;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Collections;

public class CustomGalleryActivity extends Activity {

    private GridView gridGallery;
    private Handler handler;
    private GalleryAdapter adapter;

    private ImageView imgNoMedia;
    private Button btnGalleryOk;
    private Button btnGalleryAll;
    private Button btnGalleryNothing;

    private String action;
    private ImageLoader imageLoader;

    private Bundle mExtras;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_custom_gallery);
        mExtras = getIntent().getExtras();

        action = getIntent().getAction();
        if (action == null) {
            finish();
        }
        initImageLoader();
        init();
    }

    private void initImageLoader() {
        try {
            String CACHE_DIR = Environment.getExternalStorageDirectory()
                    .getAbsolutePath() + "/.temp_tmp";
            new File(CACHE_DIR).mkdirs();

            File cacheDir = StorageUtils.getOwnCacheDirectory(getBaseContext(),
                    CACHE_DIR);

            DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                    .cacheOnDisc(true).imageScaleType(ImageScaleType.EXACTLY)
                    .bitmapConfig(Bitmap.Config.RGB_565).build();
            ImageLoaderConfiguration.Builder builder = new ImageLoaderConfiguration.Builder(
                    getBaseContext())
                    .defaultDisplayImageOptions(defaultOptions)
                    .discCache(new UnlimitedDiscCache(cacheDir))
                    .memoryCache(new WeakMemoryCache());

            ImageLoaderConfiguration config = builder.build();
            imageLoader = ImageLoader.getInstance();
            imageLoader.init(config);

        } catch (Exception e) {

        }
    }

    private void init() {

        handler = new Handler();
        gridGallery = (GridView) findViewById(R.id.gridGallery);
        gridGallery.setFastScrollEnabled(true);
        adapter = new GalleryAdapter(getApplicationContext(), imageLoader);
        PauseOnScrollListener listener = new PauseOnScrollListener(imageLoader,
                true, true);
        gridGallery.setOnScrollListener(listener);

        imgNoMedia = (ImageView) findViewById(R.id.imgNoMedia);

        btnGalleryOk = (Button) findViewById(R.id.btnGalleryOk);
        btnGalleryOk.setOnClickListener(mOkClickListener);

        btnGalleryAll = (Button) findViewById(R.id.btnGalleryAll);
        btnGalleryAll.setOnClickListener(mAllClickListener);
        btnGalleryAll.setText(R.string.gallery_select_all);
        btnGalleryAll.setBackgroundResource(R.layout.activity_custom_gallery_border);

        btnGalleryNothing = (Button) findViewById(R.id.btnGalleryNothing);
        btnGalleryNothing.setOnClickListener(mNothingClickListener);
        btnGalleryNothing.setText(R.string.gallery_select_undo);
        btnGalleryNothing.setBackgroundResource(R.layout.activity_custom_gallery_border);

        if (action.equalsIgnoreCase(Action.ACTION_MULTIPLE_PICK)) {

            findViewById(R.id.llBottomContainer).setVisibility(View.VISIBLE);
            gridGallery.setOnItemClickListener(mItemMulClickListener);
            adapter.setMultiplePick(true);
            gridGallery.setAdapter(adapter);

        }

        new Thread() {

            @Override
            public void run() {
                Looper.prepare();
                handler.post(new Runnable() {

                    @Override
                    public void run() {
                        adapter.addAll(getGalleryPhotos());
                        checkImageStatus();
                    }
                });
                Looper.loop();
            };

        }.start();

    }

    private void checkImageStatus() {
        if (adapter.isEmpty()) {
            imgNoMedia.setVisibility(View.VISIBLE);
        } else {
            imgNoMedia.setVisibility(View.GONE);
        }
    }

    View.OnClickListener mOkClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            finishGallery();
        }
    };

    AdapterView.OnItemClickListener mItemMulClickListener = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> l, View v, int position, long id) {
            adapter.changeSelection(v, position);

        }
    };

    private ArrayList<CustomGallery> getGalleryPhotos() {
        ArrayList<CustomGallery> galleryList = new ArrayList<CustomGallery>();

        String file = mExtras.getString("de.eresearch.app.gui.CustomGalleryActivity");

        final File appFolder = new File(Environment.getExternalStorageDirectory().toString()
                + "/qResearch/Pictures");

        if (!appFolder.exists()) {
            appFolder.mkdirs();
        }

        MediaScannerConnection.scanFile(this, new String[] {
                appFolder.toString()
        }, null, null);

        if (file != null && !file.isEmpty()) {

            if (file.equalsIgnoreCase(appFolder.getAbsolutePath())) {
                File dir = new File(file);

                File dirdir[] = dir.listFiles(new DirFileFilter());

                for (File fd : dirdir) {
                    File images2[] = fd.listFiles(new MediaFileFilter());

                    if (images2 != null && images2.length > 0) {
                        for (File fk : images2) {
                            CustomGallery item2 = new CustomGallery();
                            item2.sdcardPath = fk.getAbsolutePath();
                            galleryList.add(item2);
                        }
                    }

                }
            }

            File dir = new File(file);
            File images[] = dir.listFiles(new MediaFileFilter());

            if (images != null && images.length > 0) {

                for (File f : images) {
                    CustomGallery item = new CustomGallery();
                    item.sdcardPath = f.getAbsolutePath();
                    galleryList.add(item);
                }
            }

        }
        Collections.reverse(galleryList);
        return galleryList;
    }

    private void finishGallery() {
        ArrayList<CustomGallery> selected = adapter.getSelected();

        String[] allPath = new String[selected.size()];
        for (int i = 0; i < allPath.length; i++) {
            allPath[i] = selected.get(i).sdcardPath;
        }

        Intent data = new Intent().putExtra("all_path", allPath);
        setResult(RESULT_OK, data);
        finish();
    }

    @Override
    public void onBackPressed() {
        finishGallery();
    }

    // inspired by
    // https://sites.google.com/site/lapndaandroid/programing/file-filter
    public class MediaFileFilter implements FileFilter {

        private String[] extension = {
                ".JPG", ".JPEG", ".PNG"
        };

        @SuppressLint("DefaultLocale")
        public boolean accept(File pathname) {
            if (pathname.isDirectory()) {
                return false;
            }

            String name = pathname.getName().toUpperCase();
            for (String anExt : extension) {
                if (name.endsWith(anExt)) {
                    return true;
                }
            }
            return false;
        }

    }

    public class DirFileFilter implements FileFilter {

        public boolean accept(File pathname) {
            if (pathname.isDirectory()) {
                return true;
            }

            return false;
        }

    }

    View.OnClickListener mAllClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            adapter.selectAll(true);
        }
    };

    View.OnClickListener mNothingClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            adapter.selectAll(false);
        }
    };

}
