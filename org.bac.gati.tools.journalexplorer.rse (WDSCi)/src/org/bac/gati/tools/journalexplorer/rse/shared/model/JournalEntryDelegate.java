/*******************************************************************************
 * Copyright (c) 2012-2017 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package org.bac.gati.tools.journalexplorer.rse.shared.model;

import java.sql.Time;
import java.util.Date;

public class JournalEntryDelegate {

    public static Date getDate(String date, int dateFormat) {

        // TODO: fix it
        // AS400Date as400date = new
        // AS400Date(Calendar.getInstance().getTimeZone(), dateFormat, null);
        // java.sql.Date dateObject = as400date.parse(date);

        // return new Date(dateObject.getTime());
        return new Date(1,1,1970);
    }

    public static Time getTime(int time) {

        // TODO: fix it
        // AS400Time as400time = new
        // AS400Time(Calendar.getInstance().getTimeZone(), AS400Time.FORMAT_HMS,
        // null);
        // Time timeObject = as400time.parse(Integer.toString(time));

        // return new Time(timeObject.getTime());
        return new Time(0,0,0);
    }
}
