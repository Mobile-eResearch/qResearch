
package de.eresearch.app.gui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import de.eresearch.app.R;
import de.eresearch.app.gui.adapter.StudyEditPictureListArrayAdapter;
import de.eresearch.app.gui.dialogs.StudyEditPictureStatementDialog;
import de.eresearch.app.logic.model.Item;
import de.eresearch.app.logic.model.Picture;

import java.io.File;
import java.io.FileFilter;

/**
 * Provides Fragment for edit/create meta data this (frame on the right side,
 * next to the {@link StudyEditListFragment})
 * 
 * @author thg (edit by Henrik)
 */
public class StudyEditItemsFragment extends Fragment {

    private StudyEditPictureListArrayAdapter mListAdapter;
    private ListView mItemList;
    private ImageView mAddButton;
    private TextView mCount, mStatusText;

    private StudyEditContainer sec;

    private ImageLoader imageLoader;

    /**
     * Name of debug log tag. *
     */
    private static final String LOG_TAG = "StudyEditItemsFragment";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sec = StudyEditContainer.getStudyEditContainer();
        mListAdapter = new StudyEditPictureListArrayAdapter(getActivity(),
                R.layout.list_study_edit_picture, sec.getStudy().getItems(), this);

        initImageLoader();
    }

    /**
     * Callback for fragment lifecycle onCreateView()
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(LOG_TAG, "onCreateView");

        View rootView = inflater.inflate(R.layout.fragment_study_edit_items,
                container, false);
        mAddButton = (ImageView) rootView.findViewById(R.id.study_edit_item_addButton);
        mAddButton.setOnClickListener(clickAddButton);

        mStatusText = (TextView) rootView.findViewById(R.id.textViewStatus);

        mCount = (TextView) rootView.findViewById(R.id.textViewCount);

        status();

        mItemList = (ListView) rootView.findViewById(R.id.listView_items);
        mItemList.setAdapter(mListAdapter);

        mItemList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                    int position, long id) {

                StudyEditPictureStatementDialog newFragment = new StudyEditPictureStatementDialog();
                newFragment.setItem(sec.getStudy().getItems().get(position));
                newFragment.show(getFragmentManager(), "StudyEditItemStatementDialog");

            }

        });

        return rootView;
    }

    /**
     * Update edit status
     */
    private void status() {

        mCount.setText("(" + sec.getStudy().getItems().size() + "/"
                + sec.getStudy().getPyramid().getSize() + ")");
        if (sec.getStudy().getItems().size() < sec.getStudy().getPyramid().getSize()
                || sec.getStudy().getItems().size() > sec.getStudy().getPyramid().getSize()) {
            mCount.setTextColor(getActivity().getResources()
                    .getColor(android.R.color.holo_red_dark));
            mStatusText.setVisibility(View.VISIBLE);
            ((StudyEditListFragment) getFragmentManager().findFragmentById(
                    R.id.fragment_study_edit_list))
                    .setMenuStatus(StudyEditListFragment.MENU_ENTRY.Items, false);
        }
        else {
            mCount.setTextColor(getActivity().getResources().getColor(android.R.color.black));
            mStatusText.setVisibility(View.INVISIBLE);
            ((StudyEditListFragment) getFragmentManager().findFragmentById(
                    R.id.fragment_study_edit_list))
                    .setMenuStatus(StudyEditListFragment.MENU_ENTRY.Items, true);
        }
    }

    public void onResume() {
        super.onResume();
        Log.d(LOG_TAG, "onResume");
        mListAdapter.notifyDataSetChanged();
        status();
    }

    OnClickListener clickAddButton = new OnClickListener() {
        @Override
        public void onClick(View v) {
            final AlertDialog ad = new AlertDialog.Builder(getActivity())
                    .create();
            ad.setCancelable(true);
            ad.setTitle(R.string.gallery_folders);

            ScrollView scroll = new ScrollView(getActivity());
            scroll.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
                    LayoutParams.MATCH_PARENT));
            
            @SuppressWarnings("static-access")
            LayoutInflater layoutInflater = (LayoutInflater) getActivity().getSystemService(
                    getActivity().LAYOUT_INFLATER_SERVICE);
            View view = layoutInflater.inflate(R.layout.dialog_gallery, null);

            TableLayout layout = (TableLayout) view.findViewById(R.id.dialog_gallery_table);
            layout.setColumnShrinkable(1, true);
            layout.setColumnShrinkable(2, true);
            layout.setColumnShrinkable(3, true);

            TableRow row = new TableRow(getActivity());
            row.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
                    LayoutParams.WRAP_CONTENT));

            final File appFolder = new File(Environment.getExternalStorageDirectory().toString()
                    + "/qResearch/Pictures");

            if (!appFolder.exists()) {
                appFolder.mkdirs();
            }

            MediaScannerConnection.scanFile(getActivity(), new String[] {
                    appFolder.toString()
            }, null, null);

            File dir[] = appFolder.listFiles(new MediaFileFilter());
            Button t1 = new Button(getActivity());
            t1.setBackgroundColor(Color.WHITE);
            t1.setText(R.string.gallery_root);

            t1.setBackgroundResource(R.layout.activity_costum_gallery_border2);
            t1.setOnClickListener((new OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    startGallery(appFolder.getAbsolutePath());
                    ad.dismiss();
                }
            }));

            row.addView(t1);

            int i = 1;
            if (dir.length == 0) {
                layout.addView(row);
            } else {
                for (File f : dir) {
                    Button t2 = new Button(getActivity());
                    t2.setBackgroundColor(Color.WHITE);
                    t2.setBackgroundResource(R.layout.activity_costum_gallery_border2);
                    t2.setText(f.getName());
                    final String folder = f.getAbsolutePath();
                    t2.setOnClickListener((new OnClickListener() {
                        @Override
                        public void onClick(View arg0) {
                            startGallery(folder);
                            ad.dismiss();
                        }
                    }));
                    row.addView(t2);
                    i++;
                    if (i % 3 == 0) {
                        layout.addView(row);
                        row = new TableRow(getActivity());
                        i = 0;
                    }
                }
                if (i != 0 && i <= 2) {
                    layout.addView(row);
                }
            }
            
            TextView path = (TextView) view.findViewById(R.id.path_gallery);
            path.setText(appFolder.getAbsolutePath());
            
            scroll.addView(view);
            ad.setView(scroll);
            ad.show();
        }

    };

    /**
     * Source:
     * http://stackoverflow.com/questions/13209494/how-to-get-the-full-file
     * -path-from-uri
     * 
     * @param contentUri
     * @return
     */
    public String getRealPathFromURI(Uri contentUri)
    {
        String[] proj = {
                MediaStore.Audio.Media.DATA
        };
        Cursor cursor = getActivity().getContentResolver()
                .query(contentUri, proj, null, null, null); // Since manageQuery
                                                            // is deprecated
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    /**
     * Callback to delete item in study item list and refresh ListView
     * 
     * @param item
     */
    public void onListDeleteClick(Item item) {
        sec.getStudy().getItems().remove(item);
        // sec.getStudy().removeItem(item.getId());
        mListAdapter.notifyDataSetChanged();
        sec.isChanged = true;
        status();
    }

    /**
     * Callback to move a item on position up in ListView
     * 
     * @param item
     */
    public void onListUpClick(Item item) {
        int index = sec.getStudy().getItems().indexOf(item);
        sec.getStudy().getItems().remove(index);
        sec.getStudy().getItems().add(index - 1, item);
        mListAdapter.notifyDataSetChanged();
        sec.isChanged = true;

    }

    /**
     * Callback to move a item on position down in ListView
     * 
     * @param item
     */
    public void onListDownClick(Item item) {
        int index = sec.getStudy().getItems().indexOf(item);
        sec.getStudy().getItems().remove(index);
        sec.getStudy().getItems().add(index + 1, item);
        mListAdapter.notifyDataSetChanged();
        sec.isChanged = true;

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 200 && resultCode == Activity.RESULT_OK) {

            String[] all_path = data.getStringArrayExtra("all_path");

            sec = StudyEditContainer.getStudyEditContainer();

            for (String string : all_path) {

                Picture pic = new Picture(-1);
                pic.setStatement("");
                pic.setFilePath(string);
                sec.getStudy().getItems().add(pic);
                sec.isChanged = true;
            }

        } else {
            Toast.makeText(getActivity(), getString(R.string.error_only_images), Toast.LENGTH_SHORT)
                    .show();
        }
    }

    private void initImageLoader() {
        @SuppressWarnings("deprecation")
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheOnDisc().imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
                .bitmapConfig(Bitmap.Config.RGB_565).build();
        ImageLoaderConfiguration.Builder builder = new ImageLoaderConfiguration.Builder(
                this.getActivity()).defaultDisplayImageOptions(defaultOptions).memoryCache(
                new WeakMemoryCache());

        ImageLoaderConfiguration config = builder.build();
        imageLoader = ImageLoader.getInstance();
        imageLoader.init(config);
    }

    public class MediaFileFilter implements FileFilter {

        public boolean accept(File pathname) {
            if (pathname.isDirectory()) {
                return true;
            } else {
                return false;
            }

        }

    }

    private void startGallery(String dir) {
        Intent pickPhoto = new Intent(Action.ACTION_MULTIPLE_PICK);
        pickPhoto.putExtra("de.eresearch.app.gui.CustomGalleryActivity", dir);
        startActivityForResult(pickPhoto, 200);
    }

}
