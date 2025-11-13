package org.json.format;

public class BeautifyFormatter implements Formatter {

    private final StringBuilder tabStack = new StringBuilder();


    @Override
    public String formatObjectMember(String src) {
        return "\n" + tabStack + src;
    }

    @Override
    public String formatArrayMember(String src) {
        return "\n" + tabStack + src;
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
        StringBuilder temp = new StringBuilder(tabStack);
        temp.deleteCharAt(temp.length() - 1);
        return "\n" + temp + src;
    }

    @Override
    public String formatArrayEnd(String src) {
        StringBuilder temp = new StringBuilder(tabStack);
        temp.deleteCharAt(temp.length() - 1);
        return "\n" + temp + src;
    }

    @Override
    public String formatComma(String src) {
        return src;
    }

    @Override
    public String formatColon(String src) {
        return src + " ";
    }

    @Override
    public void nextObject() {
        tabStack.append('\t');
    }

    @Override
    public void finishObject() {
        tabStack.deleteCharAt(tabStack.length() - 1);
    }
}
