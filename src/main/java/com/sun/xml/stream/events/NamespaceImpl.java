/*
 * $Id: NamespaceImpl.java,v 1.3 2007-07-19 22:33:14 ofung Exp $
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

import javax.xml.stream.events.Namespace;
import javax.xml.stream.events.XMLEvent;
import javax.xml.namespace.QName;

import javax.xml.XMLConstants;
/**
 *
 * @author  Neeraj Bajaj,K.Venugopal@sun.com  Sun Microsystems.
 */
public class NamespaceImpl extends AttributeImpl implements Namespace{
    
    public NamespaceImpl( ) {
        init();
    }
    
    /** Creates a new instance of NamespaceImpl */
    public NamespaceImpl(String namespaceURI) {
        super(XMLConstants.XMLNS_ATTRIBUTE,XMLConstants.XMLNS_ATTRIBUTE_NS_URI,XMLConstants.DEFAULT_NS_PREFIX,namespaceURI,null);
        init();
    }
    
    public NamespaceImpl(String prefix, String namespaceURI){
        super(XMLConstants.XMLNS_ATTRIBUTE,XMLConstants.XMLNS_ATTRIBUTE_NS_URI,prefix,namespaceURI,null);
        init();
    }
    
    public boolean isDefaultNamespaceDeclaration() {
        QName name = this.getName();
        
        if(name != null && (name.getLocalPart().equals(XMLConstants.DEFAULT_NS_PREFIX)))
            return true;
        return false;
    }
    
    void setPrefix(String prefix){
        if(prefix == null)
            setName(new QName(XMLConstants.XMLNS_ATTRIBUTE_NS_URI,XMLConstants.DEFAULT_NS_PREFIX,XMLConstants.XMLNS_ATTRIBUTE));
        else// new QName(uri, localpart, prefix)
            setName(new QName(XMLConstants.XMLNS_ATTRIBUTE_NS_URI,prefix,XMLConstants.XMLNS_ATTRIBUTE));
    }
    
    public String getPrefix() {
        //for a namespace declaration xmlns:prefix="uri" to get the prefix we have to get the
        //local name if this declaration is stored as QName.
        QName name = this.getName();
        if(name != null)
            return name.getLocalPart();
        return null;
    }
    
    public String getNamespaceURI() {
        //we are treating namespace declaration as attribute -- so URI is stored as value
        //xmlns:prefix="Value"
        return this.getValue();
    }
    
    void setNamespaceURI(String uri) {
        //we are treating namespace declaration as attribute -- so URI is stored as value
        //xmlns:prefix="Value"
        this.setValue(uri);
    }
    
    protected void init(){
        setEventType(XMLEvent.NAMESPACE);
    }
    
    public int getEventType(){
        return XMLEvent.NAMESPACE;
    }
    
    public boolean isNamespace(){
        return true;
    }
}
