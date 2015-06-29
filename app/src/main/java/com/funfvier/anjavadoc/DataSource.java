package com.funfvier.anjavadoc;

import android.app.Activity;

import com.funfvier.anjavadoc.entity.EClass;
import com.funfvier.anjavadoc.entity.EMember;
import com.funfvier.anjavadoc.entity.EPackage;

/**
 * Created by lshi on 25.06.2015.
 */
public class DataSource {
    Activity activity;

    public final EClass[] classes;

    public final EMember[] members;

    public DataSource(Activity activity) {
        this.activity = activity;

        classes = new EClass[] {new EClass(1, "java.applet.AppletContext", activity.getString(R.string.java_applet_AppletContext_shortDescription), activity.getString(R.string.java_applet_AppletContext_longDescription), 1),
                new EClass(2, "java.applet.Applet", activity.getString(R.string.java_applet_Applet_shortDescription), activity.getString(R.string.java_applet_Applet_longDescription), 1)};

        members = new EMember[] {new EMember(1, "void destroy()", activity.getString(R.string.java_applet_Applet_destroy_shortDescription), activity.getString(R.string.java_applet_Applet_destroy_longDescription), 2)};
    }
}
