package cz.cuni.mff.umlift.core.pom;

public enum MavenPackaging {
    JAR("jar"),
    WAR("war"),
    EAR("ear"),
    POM("pom"),
    MAVEN_PLUGIN("maven-plugin"),
    BUNDLE("bundle"),
    EJB("ejb"),
    HPI("hpi"),
    ATLASSIAN_PLUGIN("atlassian-plugin"),
    APK("apk"),
    NBM("nbm");

    private final String value;

    MavenPackaging(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static MavenPackaging fromString(String text) {
        for (MavenPackaging type : MavenPackaging.values()) {
            if (type.value.equalsIgnoreCase(text)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown packaging type: " + text);
    }
}
