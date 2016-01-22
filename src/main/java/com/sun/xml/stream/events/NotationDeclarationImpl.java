/*
 * $Id: NotationDeclarationImpl.java,v 1.4 2007-10-10 00:06:32 joehw Exp $
 */

/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 * 
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License. You can obtain
 * a copy of the License at https://glassfish.dev.java.net/public/CDDL+GPL.html
 * or glassfish/bootstrap/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 * 
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at glassfish/bootstrap/legal/LICENSE.txt.
 * Sun designates this particular file as subject to the "Classpath" exception
 * as provided by Sun in the GPL Version 2 section of the License file that
 * accompanied this code.  If applicable, add the following below the License
 * Header, with the fields enclosed by brackets [] replaced by your own
 * identifying information: "Portions Copyrighted [year]
 * [name of copyright owner]"
 * 
 * Contributor(s):
 * 
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */

package com.sun.xml.stream.events;

import javax.xml.stream.events.NotationDeclaration;
import javax.xml.stream.events.XMLEvent;
import com.sun.xml.stream.dtd.nonvalidating.XMLNotationDecl;

/**
 * Implementation of NotationDeclaration event.
 *
 * @author k.venugopal@sun.com
 */
public class NotationDeclarationImpl extends DummyEvent implements NotationDeclaration {
    
    String fName = null;
    String fPublicId = null;
    String fSystemId = null;
    
    /** Creates a new instance of NotationDeclarationImpl */
    public NotationDeclarationImpl() {
        setEventType(XMLEvent.NOTATION_DECLARATION);
    }
    
    public NotationDeclarationImpl(String name,String publicId,String systemId){
        this.fName = name;
        this.fPublicId = publicId;
        this.fSystemId = systemId;
        setEventType(XMLEvent.NOTATION_DECLARATION);
    }
    
    public NotationDeclarationImpl(XMLNotationDecl notation){
        this.fName = notation.name;
        this.fPublicId = notation.publicId;
        this.fSystemId = notation.systemId;
        setEventType(XMLEvent.NOTATION_DECLARATION);
    }
    
    public String getName() {
        return fName;
    }
    
    public String getPublicId() {
        return fPublicId;
    }
    
    public String getSystemId() {
        return fSystemId;
    }
    
    void setPublicId(String publicId){
        this.fPublicId = publicId;
    }
    
    void setSystemId(String systemId){
        this.fSystemId = systemId;
    }
    
    void setName(String name){
        this.fName = name;
    }
    
    protected void writeAsEncodedUnicodeEx(java.io.Writer writer) 
    throws java.io.IOException
    {
        writer.write("<!NOTATION ");
        writer.write(getName());
        if (fPublicId != null) {
            writer.write(" PUBLIC \"");
            writer.write(fPublicId);
            writer.write("\"");
        } else if (fSystemId != null) {
            writer.write(" SYSTEM");
            writer.write(" \"");
            writer.write(fSystemId);
            writer.write("\"");
        }
        writer.write('>');
    }
}
