package de.jwahle.xperiasstatusbartogglesremover;

import static de.jwahle.xperiasstatusbartogglesremover.Constants.EXPAND_LAUNCHER_GRID;
import static de.jwahle.xperiasstatusbartogglesremover.Constants.HIDE_STATUS_BAR_TOGGLES;
import static de.jwahle.xperiasstatusbartogglesremover.Constants.HOME;
import static de.jwahle.xperiasstatusbartogglesremover.Constants.PREFS;
import static de.jwahle.xperiasstatusbartogglesremover.Constants.SYSTEM_UI;
import android.content.res.XResources;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import de.robv.android.xposed.IXposedHookInitPackageResources;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_InitPackageResources.InitPackageResourcesParam;
import de.robv.android.xposed.callbacks.XC_LayoutInflated;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

public class XperiaHooks implements
IXposedHookLoadPackage, IXposedHookInitPackageResources {

    private final XSharedPreferences preferences = new XSharedPreferences(
            "de.jwahle.xperiasstatusbartogglesremover", PREFS);
    @Override
    public void handleLoadPackage(LoadPackageParam lpparam) throws Throwable {
        if (lpparam.packageName.equals(SYSTEM_UI) && shallHideStatusBarToggles()) {
            suppressToolsMainStart(lpparam);
        }
    }
    
    private boolean shallHideStatusBarToggles() {
        preferences.reload();
        boolean hide = preferences.getBoolean(HIDE_STATUS_BAR_TOGGLES, true); 
        XposedBridge.log("hide = " + hide);
        return hide;
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
        if (resparam.packageName.equals(SYSTEM_UI) && shallHideStatusBarToggles())
            removeTogglesAndWhiteLineFromLayout(resparam.res);
        else if (resparam.packageName.equals(HOME) && shallExpandLauncherGrid())
            expandLauncherGrid(resparam.res);
    }

    private void removeTogglesAndWhiteLineFromLayout(XResources res) {
        res.hookLayout(SYSTEM_UI, "layout", "status_bar_expanded_header",
                new RemoveTogglesAndWhiteLineHook());
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
    
    private boolean shallExpandLauncherGrid() {
        preferences.reload();
        boolean expand = preferences.getBoolean(EXPAND_LAUNCHER_GRID, false); 
        XposedBridge.log("expand = " + expand);
        return expand;
    }
    
    private void expandLauncherGrid(XResources res) {
        res.setReplacement(HOME, "integer", "desktop_grid_rows", 5);
        res.setReplacement(HOME, "dimen", "cell_height",
                new XResources.DimensionReplacement(105, TypedValue.COMPLEX_UNIT_DIP));
    }

}
