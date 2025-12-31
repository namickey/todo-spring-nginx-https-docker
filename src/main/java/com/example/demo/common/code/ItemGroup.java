package com.example.demo.common.code;

/**
 * コードクラス：item分類
 */
public enum ItemGroup {

    BUNGU("CD-A01", "文具"),
    OTHER("CD-A02", "その他");

    private String code;
    private String label;
    
    private ItemGroup(String code, String label) {
        this.code = code;
        this.label = label;
    }

    public String getCode() {
        return this.code;
    }

    public String getLabel() {
        return this.label;
    }
    
    public static String getLabel(String code) {
        for (ItemGroup group : ItemGroup.values()) {
            if (group.getCode().equals(code)) {
                return group.getLabel();
            }
        }
        return null;
    }
}
