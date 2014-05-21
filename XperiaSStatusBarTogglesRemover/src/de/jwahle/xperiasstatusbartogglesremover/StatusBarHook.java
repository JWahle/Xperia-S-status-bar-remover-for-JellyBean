package de.jwahle.xperiasstatusbartogglesremover;

import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import de.robv.android.xposed.IXposedHookInitPackageResources;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_InitPackageResources.InitPackageResourcesParam;
import de.robv.android.xposed.callbacks.XC_LayoutInflated;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

public class StatusBarHook
implements IXposedHookLoadPackage, IXposedHookInitPackageResources {

    @Override
    public void handleLoadPackage(LoadPackageParam lpparam) throws Throwable {
        if (!lpparam.packageName.equals("com.android.systemui"))
            return;
        suppressToolsMainStart(lpparam);
    }

    private void suppressToolsMainStart(LoadPackageParam lpparam) {
        XposedHelpers.findAndHookMethod(
                "com.sonymobile.systemui.statusbar.tools.ToolsMain",
                lpparam.classLoader, "start", new NullMethodReplacement());
    }

    private final class NullMethodReplacement extends XC_MethodReplacement {
        @Override
        protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
            return null;
        }
    }
    
    @Override
    public void handleInitPackageResources(InitPackageResourcesParam resparam)
            throws Throwable {
        if (!resparam.packageName.equals("com.android.systemui"))
            return;
        removeTogglesAndWhiteLineFromLayout(resparam);
    }

    private void removeTogglesAndWhiteLineFromLayout(InitPackageResourcesParam rp) {
        rp.res.hookLayout("com.android.systemui", "layout",
                "status_bar_expanded_header", new RemoveTogglesAndWhiteLineHook());
    }

    private final class RemoveTogglesAndWhiteLineHook extends XC_LayoutInflated {
        @Override
        public void handleLayoutInflated(LayoutInflatedParam lip) throws Throwable {
            ViewGroup header = (ViewGroup) lip.view;
            View toggles = header.getChildAt(0);
            View whiteLine = header.getChildAt(1);
            LayoutParams headerParams = header.getLayoutParams();
            LayoutParams togglesParams = toggles.getLayoutParams();
            LayoutParams whiteLineParams = whiteLine.getLayoutParams();
            headerParams.height -= togglesParams.height;
            headerParams.height -= whiteLineParams.height;
            togglesParams.height = 0;
            whiteLineParams.height = 0;
        }
    }

}
