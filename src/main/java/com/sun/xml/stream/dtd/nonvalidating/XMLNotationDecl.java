/*
 * $Id: XMLNotationDecl.java,v 1.3 2007-07-19 22:33:13 ofung Exp $
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

/*
 * Copyright 1999-2002,2004 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sun.xml.stream.dtd.nonvalidating;

/**
 * @version $Id: XMLNotationDecl.java,v 1.3 2007-07-19 22:33:13 ofung Exp $
 */
public class XMLNotationDecl {
    
    //
    // Data
    //
    
    /** name */
    public String name;
    
    /** publicId */
    public String publicId;
    
    /** systemId */
    public String systemId;
    
    /** base systemId */
    public String baseSystemId;
    
    //
    // Methods
    //
    
    /**
     * setValues
     *
     * @param name
     * @param publicId
     * @param systemId
     */
    public void setValues(String name, String publicId, String systemId, String baseSystemId) {
        this.name     =   name;
        this.publicId = publicId;
        this.systemId = systemId;
        this.baseSystemId = baseSystemId;
    } // setValues
    
    /**
     * clear
     */
    public void clear() {
        this.name     = null;
        this.publicId = null;
        this.systemId = null;
        this.baseSystemId = null;
    } // clear
    
} // class XMLNotationDecl
