/*******************************************************************************
 * Copyright (c) 2012-2017 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.journalexplorer.core.model;

public class File {

    private String connetionName;
    private String outFileName;
    private String outFileLibrary;

    public File() {
    }

    public String getConnectionName() {
        return connetionName;
    }

    public void setConnetionName(String connetionName) {
        this.connetionName = connetionName;
    }

    public String getOutFileName() {
        return outFileName;
    }

    public void setOutFileName(String outFileName) {
        this.outFileName = outFileName;
    }

    public String getOutFileLibrary() {
        return outFileLibrary;
    }

    public void setOutFileLibrary(String outFileLibrary) {
        this.outFileLibrary = outFileLibrary;
    }

}
