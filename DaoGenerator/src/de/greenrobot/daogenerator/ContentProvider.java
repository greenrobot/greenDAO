package de.greenrobot.daogenerator;

public class ContentProvider {
    private final Entity entity;
    private String authority;
    private String basePath;
    private String className;
    private String javaPackage;
    private boolean readOnly;

    public ContentProvider(Entity entity) {
        this.entity = entity;
    }

    public String getAuthority() {
        return authority;
    }

    public void setAuthority(String authority) {
        this.authority = authority;
    }

    public String getBasePath() {
        return basePath;
    }

    public void setBasePath(String basePath) {
        this.basePath = basePath;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getJavaPackage() {
        return javaPackage;
    }

    public void setJavaPackage(String javaPackage) {
        this.javaPackage = javaPackage;
    }

    public boolean isReadOnly() {
        return readOnly;
    }

    public void readOnly() {
        this.readOnly = true;
    }

    public void init2ndPass() {
        if (authority == null) {
            authority = entity.getJavaPackage();
        }
        if (basePath == null) {
            basePath = entity.getClassName();
        }
        if (className == null) {
            className = entity.getClassName() + "ContentProvider";
        }
        if (javaPackage == null) {
            javaPackage = entity.getJavaPackageDao();
        }

    }

}
