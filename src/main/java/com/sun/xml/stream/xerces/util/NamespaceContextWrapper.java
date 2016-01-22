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

package com.sun.xml.stream.xerces.util;

import java.util.Enumeration;
import java.util.Vector;
import javax.xml.namespace.NamespaceContext ;

/**
 * Writing a wrapper to re-use most of the namespace functionality 
 * already provided by NamespaceSupport, which implements NamespaceContext
 * from XNI. It would be good if we can change the XNI NamespaceContext 
 * interface to implement the JAXP NamespaceContext interface.
 *
 * Note that NamespaceSupport assumes the use of symbols. Since this class
 * can be exposed to the application, we must intern all Strings before
 * calling NamespaceSupport methods.
 *
 * @author  Neeraj Bajaj, Sun Microsystems, inc.
 * @author Santiago.PericasGeertsen@sun.com
 *
 */
public class NamespaceContextWrapper implements NamespaceContext {
    
    private com.sun.xml.stream.xerces.xni.NamespaceContext fNamespaceContext;
    
    public NamespaceContextWrapper(com.sun.xml.stream.xerces.xni.NamespaceContext namespaceContext) {
        fNamespaceContext = namespaceContext ;
    }
    
    public String getNamespaceURI(String prefix) {
        if (prefix == null) {
            throw new IllegalArgumentException("Prefix can't be null");
        }
        return fNamespaceContext.getURI(prefix.intern());
    }
    
    public String getPrefix(String namespaceURI) {
        if (namespaceURI == null) {
            throw new IllegalArgumentException("URI can't be null");
        }
        return fNamespaceContext.getPrefix(namespaceURI.intern());
    }
    
    /**
     * TODO: Namespace doesn't give information giving multiple prefixes for
     * the same namespaceURI.
     */
    public java.util.Iterator getPrefixes(String namespaceURI) {
        if (namespaceURI == null) {
            throw new IllegalArgumentException("URI can't be null");
        } 
        else {
            Vector vector = 
                ((NamespaceSupport) fNamespaceContext).getPrefixes(namespaceURI.intern());
            return vector.iterator();
        }
    }
    
    /**
     * This method supports all functions in the NamespaceContext utility class
     */
    public com.sun.xml.stream.xerces.xni.NamespaceContext getNamespaceContext() {
        return fNamespaceContext;
    }
}

