package com.welo.pagerecycleview.app;

import android.graphics.Bitmap;

import java.io.Serializable;

/**
 * Created by Amy on 2019/4/2
 */

public class AppInfo implements Serializable {
    private String imageName;
    private String packageName;
    private String appName;

    private AppInfo(Builder builder) {
        imageName = builder.imageName;
        packageName = builder.packageName;
        appName = builder.appName;
    }

    public static Builder newBuilder() {
        return new Builder();
    }


    public String getImageName() {
        return imageName;
    }

    public String getPackageName() {
        return packageName;
    }

    public String getAppName() {
        return appName;
    }


    public static final class Builder {
        private String imageName;
        private String packageName;
        private String appName;

        private Builder() {
        }

        public Builder imageName(String val) {
            imageName = val;
            return this;
        }

        public Builder packageName(String val) {
            packageName = val;
            return this;
        }

        public Builder appName(String val) {
            appName = val;
            return this;
        }

        public AppInfo build() {
            return new AppInfo(this);
        }
    }

    @Override
    public String toString() {
        return "AppInfo{" +
                "imageName='" + imageName + '\'' +
                ", packageName='" + packageName + '\'' +
                ", appName='" + appName + '\'' +
                '}';
    }
}
