package biz.isphere.core.internal;

import com.ibm.as400.access.AS400;
import com.ibm.as400.data.PcmlException;
import com.ibm.as400.data.ProgramCallDocument;

import biz.isphere.base.internal.StringHelper;

public class APIProgramCallDocument extends ProgramCallDocument {
    
    private static final long serialVersionUID = 4373969780285460768L;

    public APIProgramCallDocument(AS400 paramAS400, String paramString, ClassLoader paramClassLoader) throws PcmlException {
        super(paramAS400, paramString, paramClassLoader);
    }

    public void setQualifiedObjectName(String aParameter, String aLibrary, String aName) throws PcmlException {
        setValue(aParameter, StringHelper.getFixLength(aName, 10) + StringHelper.getFixLength(aLibrary, 10));
    }

}
