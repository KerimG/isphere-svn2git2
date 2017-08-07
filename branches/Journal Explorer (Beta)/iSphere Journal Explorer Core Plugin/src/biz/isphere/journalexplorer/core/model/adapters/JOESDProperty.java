/*******************************************************************************
 * Copyright (c) 2012-2017 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Initial idea and development: Isaac Ramirez Herrera
 * Continued and adopted to iSphere: iSphere Project Team
 *******************************************************************************/

package biz.isphere.journalexplorer.core.model.adapters;

import java.util.ArrayList;

import biz.isphere.journalexplorer.core.internals.JoesdParser;
import biz.isphere.journalexplorer.core.model.JournalEntry;
import biz.isphere.journalexplorer.core.model.MetaColumn;
import biz.isphere.journalexplorer.core.model.MetaDataCache;
import biz.isphere.journalexplorer.core.model.MetaTable;

import com.ibm.as400.access.Record;

public class JOESDProperty extends JournalProperty {

    private JournalEntry journal;

    private MetaTable metatable;

    private ArrayList<JournalProperty> specificProperties;

    private Record parsedJOESD;

    private boolean errorParsing;

    public JOESDProperty(String name, Object value, Object parent, JournalEntry journal) {

        super(name, "", parent);
        this.journal = journal;
        this.errorParsing = false;

        this.executeParsing();
    }

    public void executeParsing() {
        try {
            this.initialize();
            this.parseJOESD();
        } catch (Exception exception) {
            this.value = exception.getMessage();
            this.errorParsing = true;
        }
    }

    private void initialize() throws Exception {

        this.errorParsing = false;
        this.value = "";

        this.metatable = null;

        this.parsedJOESD = null;

        if (this.specificProperties != null) {
            this.specificProperties.clear();
        } else {
            this.specificProperties = new ArrayList<JournalProperty>();
        }
    }

    private void parseJOESD() throws Exception {

        String columnName;

        this.metatable = MetaDataCache.INSTANCE.retrieveMetaData(this.journal);

        this.parsedJOESD = new JoesdParser(this.metatable).execute(this.journal);

        for (MetaColumn column : this.metatable.getColumns()) {
            columnName = column.getName().trim();
            if (column.getColumnText() != null && column.getColumnText().trim() != "") {
                columnName += " (" + column.getColumnText().trim() + ")";
            }

            this.specificProperties.add(new JournalProperty(columnName, this.parsedJOESD.getField(column.getName()).toString(), this));
        }
    }

    public Object[] toPropertyArray() {
        if (this.specificProperties != null) {
            return this.specificProperties.toArray();
        } else {
            return null;
        }
    }

    @Override
    public int compareTo(JournalProperty comparable) {

        if (comparable instanceof JOESDProperty) {
            JOESDProperty joesdSpecificProperty = (JOESDProperty)comparable;

            if (joesdSpecificProperty.parsedJOESD.getNumberOfFields() != this.parsedJOESD.getNumberOfFields()) {
                this.highlighted = comparable.highlighted = true;
                return -1;

            } else {
                int status = 0;

                for (int i = 0; i < this.specificProperties.size(); i++) {

                    if (this.specificProperties.get(i).compareTo(joesdSpecificProperty.specificProperties.get(i)) != 0) {
                        status = -1;
                    }
                }
                return status;
            }
        } else {
            return -1;
        }
    }

    public boolean isErrorParsing() {
        return this.errorParsing;
    }
}
