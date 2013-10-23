package com.itrustcambodia.pluggable.wicket.extensions.markup.html.repeater.data.table.export;

import org.apache.wicket.extensions.markup.html.repeater.data.table.DataTable;
import org.apache.wicket.model.IModel;

public class ExportToolbar extends org.apache.wicket.extensions.markup.html.repeater.data.table.export.ExportToolbar {

    /**
     * 
     */
    private static final long serialVersionUID = 2499207571533685537L;

    public ExportToolbar(DataTable<?, ?> table, IModel<String> messageModel, IModel<String> fileNameModel) {
        super(table, messageModel, fileNameModel);
    }

    public ExportToolbar(DataTable<?, ?> table, IModel<String> fileNameModel) {
        super(table, fileNameModel);
    }

    public ExportToolbar(DataTable<?, ?> table) {
        super(table);
    }

}
