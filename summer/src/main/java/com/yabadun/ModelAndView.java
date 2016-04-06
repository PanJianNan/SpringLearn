package com.yabadun;

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * ModelAndView
 *
 * @author panjn
 * @date 2016/4/6
 */
public class ModelAndView {
    private String view;
    private Map<String, Object> model;

    public ModelAndView() {
    }

    public ModelAndView(String view) {
        this.view = view;
    }

    public String getView() {
        return view;
    }

    public void setView(String view) {
        this.view = view;
    }

    public Map<String, Object> getModel() {
        if (this.model == null) {
            this.model = new HashMap<String, Object>();
        }
        return model;
    }

    public void setModel(Map model) {
        this.model = model;
    }

    public ModelAndView addObject(String attributeName, Object attributeValue) {
        if (StringUtils.isBlank(attributeName)) {
            throw new IllegalArgumentException("Model attribute name must not be null");
        }

        this.getModel().put(attributeName, attributeValue);
        return this;
    }
}
