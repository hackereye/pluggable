package com.itrustcambodia.pluggable.panel;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.panel.ComponentFeedbackPanel;

public class FormFeedback extends ComponentFeedbackPanel {

    private static final long serialVersionUID = 2606478694551646850L;

    public FormFeedback(String id, Component filter) {
        super(id, filter);
    }

}
