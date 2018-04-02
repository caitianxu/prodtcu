package com.cjsz.tech.enums;

import java.util.Locale;

/**
 * 语言枚举定义
 */
public enum Language {

    zh_CN("zh_CN", "zh", "cn", Locale.CHINA),
    zh_TW("zh_TW", "zh", "tw", Locale.TAIWAN),
    en_US("en_US", "en", "en", Locale.US),
    ja_JP("ja_JP", "ja", "jp", Locale.JAPAN),
    ko_KR("ko_KR", "ko", "kr", Locale.KOREA),
    fr_FR("fr_FR", "fr", "fr", Locale.FRANCE);

    private String code;
    private String code2;
    private String abbr;
    private Locale locale;

    Language(String code, String code2, String abbr, Locale locale) {
        this.code = code;
        this.code2 = code2;
        this.abbr = abbr;
        this.locale = locale;
    }

    public boolean isZhCn() {
        return this == zh_CN;
    }

    public boolean isEnglish() {
        return this == en_US;
    }

    public String getCode() {
        return code;
    }

    public String getCode2() {
        return code2;
    }

    public String getAbbr() {
        return abbr;
    }

    public Locale getLocale() {
        return locale;
    }

    public static Language fromCode(String code) {
        for (Language language : Language.values()) {
            if (language.getCode().equals(code)) {
                return language;
            }
        }
        return null;
    }

    public static Language fromAbbr(String abbr) {
        for (Language language : Language.values()) {
            if (language.getAbbr().equals(abbr)) {
                return language;
            }
        }
        return null;
    }
}