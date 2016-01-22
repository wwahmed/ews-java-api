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

/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2000-2002 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Xerces" and "Apache Software Foundation" must
 *    not be used to endorse or promote products derived from this
 *    software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation and was
 * originally based on software copyright (c) 1999, International
 * Business Machines, Inc., http://www.apache.org.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */

package com.sun.xml.stream.xerces.xni;

/**
 * A structure that holds the components of an XML Namespaces qualified
 * name.
 * <p>
 * To be used correctly, the strings must be identical references for
 * equal strings. Within the parser, these values are considered symbols
 * and should always be retrieved from the <code>SymbolTable</code>.
 *
 * @see <a href="../../../../../xerces2/com/sun/xml/stream/xerces/util/SymbolTable.html">com.sun.xml.stream.xerces.util.SymbolTable</a>
 *
 * @author Andy Clark, IBM
 *
 * @version $Id: QName.java,v 1.3 2007-07-19 22:33:16 ofung Exp $
 */
public class QName
implements Cloneable {
    
    //rawname
    public char [] characters = null;
    //
    // Data
    //
    
    /**
     * The qname prefix. For example, the prefix for the qname "a:foo"
     * is "a".
     */
    public String prefix;
    
    /**
     * The qname localpart. For example, the localpart for the qname "a:foo"
     * is "foo".
     */
    public String localpart;
    
    /**
     * The qname rawname. For example, the rawname for the qname "a:foo"
     * is "a:foo".
     */
    public String rawname;
    
    /**
     * The URI to which the qname prefix is bound. This binding must be
     * performed by a XML Namespaces aware processor.
     */
    public String uri;
    
    //
    // Constructors
    //
    
    /** Default constructor. */
    public QName() {
        clear();
    } // <init>()
    
    /** Constructs a QName with the specified values. */
    public QName(String prefix, String localpart, String rawname, String uri) {
        setValues(prefix, localpart, rawname, uri);
    } // <init>(String,String,String,String)
    
    /** Constructs a copy of the specified QName. */
    public QName(QName qname) {
        setValues(qname);
    } // <init>(QName)
    
    //
    // Public methods
    //
    
    /**
     * Convenience method to set the values of the qname components.
     *
     * @param QName The qualified name to be copied.
     */
    public void setValues(QName qname) {
        prefix = qname.prefix;
        localpart = qname.localpart;
        rawname = qname.rawname;
        uri = qname.uri;
        characters=qname.characters;
    } // setValues(QName)
    
    /**
     * Convenience method to set the values of the qname components.
     *
     * @param prefix    The qname prefix. (e.g. "a")
     * @param localpart The qname localpart. (e.g. "foo")
     * @param rawname   The qname rawname. (e.g. "a:foo")
     * @param uri       The URI binding. (e.g. "http://foo.com/mybinding")
     */
    public void setValues(String prefix, String localpart, String rawname,
    String uri) {
        this.prefix = prefix;
        this.localpart = localpart;
        this.rawname = rawname;
        this.uri = uri;
    } // setValues(String,String,String,String)
    
    /** Clears the values of the qname components. */
    public void clear() {
        prefix = null;
        localpart = null;
        rawname = null;
        uri = null;
    } // clear()
    
    //
    // Cloneable methods
    //
    
    /** Returns a clone of this object. */
    public Object clone() {
        return new QName(this);
    } // clone():Object
    
    //
    // Object methods
    //
    
    /** Returns the hashcode for this object. */
    public int hashCode() {
        if (uri != null) {
            return uri.hashCode() + localpart.hashCode();
        }
        return rawname.hashCode();
    } // hashCode():int
    
    /** Returns true if the two objects are equal. */
    public boolean equals(Object object) {
        if (object instanceof QName) {
            QName qname = (QName)object;
            if (qname.uri != null) {
                return uri == qname.uri && localpart == qname.localpart;
            }
            else if (uri == null) {
                return rawname == qname.rawname;
            }
            // fall through and return not equal
        }
        return false;
    } // equals(Object):boolean
    
    /** Returns a string representation of this object. */
    public String toString() {
        
        StringBuffer str = new StringBuffer();
        boolean comma = false;
        if (prefix != null) {
            str.append("prefix=\""+prefix+'"');
            comma = true;
        }
        if (localpart != null) {
            if (comma) {
                str.append(',');
            }
            str.append("localpart=\""+localpart+'"');
            comma = true;
        }
        if (rawname != null) {
            if (comma) {
                str.append(',');
            }
            str.append("rawname=\""+rawname+'"');
            comma = true;
        }
        if (uri != null) {
            if (comma) {
                str.append(',');
            }
            str.append("uri=\""+uri+'"');
        }
        return str.toString();
        
    } // toString():String
    
} // class QName
