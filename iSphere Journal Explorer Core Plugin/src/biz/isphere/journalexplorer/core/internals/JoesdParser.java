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

package biz.isphere.journalexplorer.core.internals;

import biz.isphere.journalexplorer.base.interfaces.IJoesdParserDelegate;
import biz.isphere.journalexplorer.core.Messages;
import biz.isphere.journalexplorer.core.model.JournalEntry;
import biz.isphere.journalexplorer.core.model.MetaColumn;
import biz.isphere.journalexplorer.core.model.MetaTable;
import biz.isphere.journalexplorer.rse.shared.model.JoesdParserDelegate;

import com.ibm.as400.access.AS400Bin2;
import com.ibm.as400.access.AS400Bin4;
import com.ibm.as400.access.AS400Bin8;
import com.ibm.as400.access.AS400Float4;
import com.ibm.as400.access.AS400Float8;
import com.ibm.as400.access.AS400PackedDecimal;
import com.ibm.as400.access.AS400Text;
import com.ibm.as400.access.AS400ZonedDecimal;
import com.ibm.as400.access.BinaryFieldDescription;
import com.ibm.as400.access.CharacterFieldDescription;
import com.ibm.as400.access.FloatFieldDescription;
import com.ibm.as400.access.PackedDecimalFieldDescription;
import com.ibm.as400.access.Record;
import com.ibm.as400.access.RecordFormat;
import com.ibm.as400.access.ZonedDecimalFieldDescription;

public class JoesdParser {
    private static final int AJUSTE_VARCHAR = 2;

    private MetaTable metadata;

    private RecordFormat formatoJoesd;

    private IJoesdParserDelegate joesdParserDelegate = new JoesdParserDelegate();

    public JoesdParser(MetaTable metadata) throws Exception {
        this.metadata = metadata;
        this.inicializar();
    }

    private void inicializar() throws Exception {
        this.formatoJoesd = new RecordFormat();

        for (MetaColumn columna : this.metadata.getColumns()) {
            switch (columna.getDataType()) {
            case BIGINT:
                this.formatoJoesd.addFieldDescription(new BinaryFieldDescription(new AS400Bin8(), columna.getName()));
                break;

            case CHAR:
                this.formatoJoesd.addFieldDescription(new CharacterFieldDescription(new AS400Text(columna.getSize()), columna.getName()));
                break;

            case CLOB:
                throw new Exception(Messages.JoesdParser_CLOBNotSupported);

            case DATE:
                this.formatoJoesd.addFieldDescription(joesdParserDelegate.getDateFieldDescription(columna.getName()));
                break;

            case DECIMAL:
                this.formatoJoesd.addFieldDescription(new PackedDecimalFieldDescription(new AS400PackedDecimal(columna.getSize(), columna
                    .getPrecision()), columna.getName()));
                break;

            case DOUBLE:
                this.formatoJoesd.addFieldDescription(new FloatFieldDescription(new AS400Float8(), columna.getName()));
                break;

            case INTEGER:
                this.formatoJoesd.addFieldDescription(new BinaryFieldDescription(new AS400Bin4(), columna.getName()));
                break;

            case NUMERIC:
                this.formatoJoesd.addFieldDescription(new ZonedDecimalFieldDescription(new AS400ZonedDecimal(columna.getSize(), columna
                    .getPrecision()), columna.getName()));
                break;

            case REAL:
                this.formatoJoesd.addFieldDescription(new FloatFieldDescription(new AS400Float4(), columna.getName()));
                break;

            case SMALLINT:
                this.formatoJoesd.addFieldDescription(new BinaryFieldDescription(new AS400Bin2(), columna.getName()));
                break;

            case TIME:
                this.formatoJoesd.addFieldDescription(joesdParserDelegate.getTimeFieldDescription(columna.getName()));
                break;

            case TIMESTMP:
                this.formatoJoesd.addFieldDescription(joesdParserDelegate.getTimestampFieldDescription(columna.getName()));
                break;

            case VARCHAR:
                this.formatoJoesd.addFieldDescription(new CharacterFieldDescription(new AS400Text(columna.getSize() + AJUSTE_VARCHAR), columna
                    .getName()));
                break;
            }
        }
    }

    public Record procesar(JournalEntry journal) throws Exception {
        if (this.verificarObjetos(journal))
            return this.getFormatoJoesd().getNewRecord(journal.getSpecificData(), this.metadata.getParsingOffset());
        else
            throw new Exception(Messages.JoesdParser_TableMetadataDontMatchEntry);
    }

    private boolean verificarObjetos(JournalEntry datos) {
        return this.metadata.getName().equals(datos.getObjectName()) && this.metadata.getLibrary().equals(datos.getObjectLibrary());
    }

    /**
     * @return the formatoJoesd
     */
    public RecordFormat getFormatoJoesd() {
        return formatoJoesd;
    }
}
