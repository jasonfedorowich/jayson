package org.json.format;

public class DefaultFormatter implements Formatter{

    @Override
    public String formatObjectMember(String src) {
        return src;
    }

    @Override
    public String formatArrayMember(String src) {
        return src;
    }

    @Override
    public String formatObjectBegin(String src) {
        return src;
    }

    @Override
    public String formatArrayBegin(String src) {
        return src;
    }

    @Override
    public String formatObjectEnd(String src) {
        return src;
    }

    @Override
    public String formatArrayEnd(String src) {
        return src;
    }

    @Override
    public String formatComma(String src) {
        return src;
    }

    @Override
    public String formatColon(String src) {
        return src;
    }

    @Override
    public void nextObject() {

    }

    @Override
    public void finishObject() {

    }
}
