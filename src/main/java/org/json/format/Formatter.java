package org.json.format;

public interface Formatter {

    String formatObjectMember(String src);

    String formatArrayMember(String src);

    String formatObjectBegin(String src);

    String formatArrayBegin(String src);

    String formatObjectEnd(String src);

    String formatArrayEnd(String src);

    String formatComma(String src);

    String formatColon(String src);

    void nextObject();

    void finishObject();

}
