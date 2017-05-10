package ru.coyul.packages;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.List;

public class PackagesAdapter extends BaseAdapter {

    private final List<PackageInfo> packages;

    public PackagesAdapter(List<PackageInfo> packages) {
        this.packages = Collections.unmodifiableList(packages);
    }

    @Override
    public int getCount() {
        return packages.size();
    }

    @Override
    public PackageInfo getItem(int position) {
        return packages.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            view = inflater.inflate(R.layout.package_list_item, parent, false);

            ViewHolder holder = new ViewHolder();
            holder.icon = (ImageView) view.findViewById(R.id.package_icon);
            holder.title = (TextView) view.findViewById(R.id.package_main_text);
            holder.subTitle = (TextView) view.findViewById(R.id.package_add_text);

            view.setTag(holder);
        }

        ViewHolder holder = (ViewHolder) view.getTag();
        PackageInfo packageInfo = getItem(position);

        PackageManager pm = parent.getContext().getPackageManager();

        if (packageInfo.applicationInfo != null) {
            LoadInfoTask loadInfoTask = new LoadInfoTask(packageInfo, pm, holder.icon, holder.title);
            loadInfoTask.execute();
        }


        holder.subTitle.setText(packageInfo.packageName);

        return view;


    }

    private static class ViewHolder {
        ImageView icon;
        TextView title;
        TextView subTitle;
    }

    //class to load package images and titles faster (as it extends AsyncTask) with mistakes preventing (weak references)
    private static class LoadInfoTask extends AsyncTask<Void, Void, ProccessInfo> {

        private PackageInfo packageInfo;
        private PackageManager packageManager;
        private WeakReference<ImageView> imageRef;
        private WeakReference<TextView> textRef;

        public LoadInfoTask(PackageInfo packageInfo, PackageManager packageManager, ImageView imageTarget, TextView textTarget) {
            this.packageInfo = packageInfo;
            this.packageManager = packageManager;
            imageRef = new WeakReference<ImageView>(imageTarget);
            textRef = new WeakReference<TextView>(textTarget);

            imageTarget.setTag(packageInfo.packageName);
        }

        @Override
        protected ProccessInfo doInBackground(Void... params) {

            Drawable icon = packageManager.getApplicationIcon(packageInfo.applicationInfo);
            CharSequence label = packageInfo.applicationInfo.loadLabel(packageManager);

            return new ProccessInfo(icon, label);
        }

        @Override
        protected void onPostExecute(ProccessInfo proccessInfo) {
            ImageView imageView = imageRef.get();
            TextView textView = textRef.get();

            if (imageView == null || textView == null || !packageInfo.packageName.equals(imageView.getTag().toString())) {
                return;
            }

            imageView.setImageDrawable(proccessInfo.getIcon());
            textView.setText(proccessInfo.getTitle());
        }
    }

    static class ProccessInfo {
        private Drawable icon;
        private CharSequence title;

        public ProccessInfo(Drawable icon, CharSequence title) {
            this.icon = icon;
            this.title = title;
        }

        public Drawable getIcon() {
            return icon;
        }

        public CharSequence getTitle() {
            return title;
        }
    }
}
