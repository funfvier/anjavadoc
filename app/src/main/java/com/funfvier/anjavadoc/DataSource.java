package com.funfvier.anjavadoc;

import android.app.Activity;

import com.funfvier.anjavadoc.entity.EClass;
import com.funfvier.anjavadoc.entity.EPackage;

/**
 * Created by lshi on 25.06.2015.
 */
public class DataSource {
    Activity activity;
    public final EPackage[] packages;

    public final EClass[] classes;

    public DataSource(Activity activity) {
        this.activity = activity;

        packages = new EPackage[] {new EPackage(1, "java.applet", "Provides the classes necessary to create an applet and the classes an applet uses to communicate with its applet context."),
                new EPackage(2, "java.awt", "Contains all of the classes for creating user interfaces and for painting graphics and images."),
                new EPackage(3, "java.awt.color", "Provides classes for color spaces."),
                new EPackage(4, "java.awt.datatransfer", "Provides interfaces and classes for transferring data between and within applications.")};

        classes = new EClass[] {new EClass(1, "java.applet.AppletContext", activity.getString(R.string.java_applet_AppletContext_shortDescription), activity.getString(R.string.java_applet_AppletContext_longDescription), 1),
                new EClass(2, "java.applet.Applet", activity.getString(R.string.java_applet_Applet_shortDescription), activity.getString(R.string.java_applet_Applet_longDescription), 1)};
    }
}
