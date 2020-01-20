package com.welo.pagerecycleview.app;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Amy on 2019-12-24
 */

public class AppUtility {

    public static Single<List<AppInfo>> getAppInfoList(Context context) {
        return getPackageNameList(context).flatMap(packageNameList -> getAppIcon(context, packageNameList));
    }

    private static Single<List<String>> getPackageNameList(Context context) {
        return Single.defer(() -> {
                    List<String> packageNameList = getInstalledApps(context);
                    return Single.just(packageNameList);
                }
        ).subscribeOn(Schedulers.io());
    }

    private static Single<List<AppInfo>> getAppIcon(Context context, List<String> packageNameList) {
        PackageManager packageManager = context.getPackageManager();
        ArrayList<AppInfo> appInfos = new ArrayList<>();
        return Single.fromCallable(() -> {
            for (String packageName : packageNameList) {
                appInfos.add(getAppIconDrawable(packageName, packageManager));
            }
            return appInfos;
        });
    }

    private static AppInfo getAppIconDrawable(String packageName, PackageManager packageManager) {
        Drawable drawable = null;
        String appName = "";
        try {
            ApplicationInfo app = packageManager.getApplicationInfo(packageName, 0);
            //drawable = packageManager.getApplicationIcon(app);
            appName = packageManager.getApplicationLabel(app).toString();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        String imageName = getIconName(packageName).trim();
        AppInfo appInfo = AppInfo.newBuilder()
                .imageName(imageName)
                .appName(appName)
                .packageName(packageName).build();

        return appInfo;
    }

    private static Bitmap drawableToBitmap(Drawable drawable) {
        int w = drawable.getIntrinsicWidth();
        int h = drawable.getIntrinsicHeight();
        Bitmap.Config config =
                drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                        : Bitmap.Config.RGB_565;
        Bitmap bitmap = Bitmap.createBitmap(w, h, config);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, w, h);
        drawable.draw(canvas);

        return bitmap;
    }

    private static List<String> getInstalledApps(Context context) {
        List<String> packageNameList = new ArrayList<>();
        PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> packageInfoList = packageManager.getInstalledPackages(PackageManager.COMPONENT_ENABLED_STATE_DEFAULT);
        for (PackageInfo packageInfo : packageInfoList) {
            String packageName = packageInfo.packageName;
            String name = (String) packageInfo.applicationInfo.loadLabel(packageManager);
            if (TextUtils.isEmpty(packageName) || TextUtils.isEmpty(name) || packageName.getBytes().length > 255 || name.getBytes().length > 255 || packageName.equals(context.getPackageName()))
                continue;

            Intent intent = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.CUPCAKE) {
                intent = packageManager.getLaunchIntentForPackage(packageName);
            }
            if (intent == null)
                continue;

            packageNameList.add(packageName);
        }

        return packageNameList;
    }

    private static String getIconName(String iconName) {
        return "images/" + iconName.replace(".", "_").split("---")[0] + ".png";
    }





}
