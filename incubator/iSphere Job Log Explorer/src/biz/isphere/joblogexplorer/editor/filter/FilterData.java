/*******************************************************************************
 * Copyright (c) 2012-2016 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.joblogexplorer.editor.filter;

public class FilterData {

    private String EMPTY = ""; //$NON-NLS-1$

    public String id;
    public String type;
    public String severity;

    public FilterData() {
        this.id = EMPTY;
        this.type = EMPTY;
        this.severity = EMPTY;
    }

}
