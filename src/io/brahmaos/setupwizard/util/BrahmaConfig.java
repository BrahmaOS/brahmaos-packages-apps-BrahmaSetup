package io.brahmaos.setupwizard.util;

import android.content.Context;

import java.util.Locale;


/**
 * the project common config
 */

public class BrahmaConfig {

    public static String getServiceTermsUrl() {
        String languageLocale = Locale.getDefault().getLanguage();
        String serviceUrl = BrahmaConst.SERVICE_PATH_EN;
        if (languageLocale.equals(BrahmaConst.LANGUAGE_CHINESE)) {
            serviceUrl = BrahmaConst.SERVICE_PATH_ZH;
        }
        return serviceUrl;
    }

    public static String getPrivacyUrl() {
        String languageLocale = Locale.getDefault().getLanguage();
        String serviceUrl = BrahmaConst.PRIVACY_POLICY_PATH_EN;
        if (languageLocale.equals(BrahmaConst.LANGUAGE_CHINESE)) {
            serviceUrl = BrahmaConst.PRIVACY_POLICY_PATH_ZH;
        }
        return serviceUrl;
    }
}
