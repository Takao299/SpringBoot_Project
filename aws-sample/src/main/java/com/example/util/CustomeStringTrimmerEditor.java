package com.example.util;

import java.beans.PropertyEditorSupport;

import org.apache.commons.lang3.StringUtils;

public class CustomeStringTrimmerEditor extends PropertyEditorSupport {

    private final boolean emptyAsNull;

    public CustomeStringTrimmerEditor(boolean emptyAsNull) {
        this.emptyAsNull = emptyAsNull;
    }

    @Override
    public String getAsText() {
        Object value = this.getValue();
        return value != null ? value.toString() : "";
    }

    @Override
    public void setAsText(String text) {
        if (text == null) {
            this.setValue((Object) null);
        } else {
            String value = StringUtils.strip(text);

            if (this.emptyAsNull && "".equals(value)) {
                this.setValue((Object) null);
            } else {
                this.setValue(value);
            }
        }
    }
}