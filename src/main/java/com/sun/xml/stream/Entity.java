/*
 * $Id: Entity.java,v 1.4 2007-07-19 22:33:12 ofung Exp $
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
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 1999-2002 The Apache Software Foundation.
 * All rights reserved.
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
 */
/*
 * Entity.java
 *
 * Created on December 2, 2002, 3:10 PM
 */

package com.sun.xml.stream;

import com.sun.xml.stream.util.BufferAllocator;
import com.sun.xml.stream.util.ThreadLocalBufferAllocator;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

import com.sun.xml.stream.xerces.xni.XMLResourceIdentifier;


/**
 * Entity information.
 *
 * @author
 */
public abstract class Entity {
    
    //
    // Data
    //
    
    //xxx why dont we declare the type of entities, like assign integer for external/ internal etc..
    
    /** Entity name. */
    public String name;
    
    // whether this entity's declaration was found in the internal
    // or external subset
    public boolean inExternalSubset;
    
    //
    // Constructors
    //
    
    /** Default constructor. */
    public Entity() {
        clear();
    } // <init>()
    
    /** Constructs an entity. */
    public Entity(String name, boolean inExternalSubset) {
        this.name = name;
        this.inExternalSubset = inExternalSubset;
    } // <init>(String)
    
    //
    // Public methods
    //
    
    /** Returns true if this entity was declared in the external subset. */
    public boolean isEntityDeclInExternalSubset() {
        return inExternalSubset;
    }
    
    /** Returns true if this is an external entity. */
    public abstract boolean isExternal();
    
    /** Returns true if this is an unparsed entity. */
    public abstract boolean isUnparsed();
    
    /** Clears the entity. */
    public void clear() {
        name = null;
        inExternalSubset = false;
    } // clear()
    
    /** Sets the values of the entity. */
    public void setValues(Entity entity) {
        name = entity.name;
        inExternalSubset = entity.inExternalSubset;
    } // setValues(Entity)
    
    
    /**
     * Internal entity.
     *
     * @author nb131165
     */
    public static class InternalEntity
    extends Entity {
        
        //
        // Data
        //
        
        /** Text value of entity. */
        public String text;
        
        //
        // Constructors
        //
        
        /** Default constructor. */
        public InternalEntity() {
            clear();
        } // <init>()
        
        /** Constructs an internal entity. */
        public InternalEntity(String name, String text, boolean inExternalSubset) {
            super(name,inExternalSubset);
            this.text = text;
        } // <init>(String,String)
        
        //
        // Entity methods
        //
        
        /** Returns true if this is an external entity. */
        public final boolean isExternal() {
            return false;
        } // isExternal():boolean
        
        /** Returns true if this is an unparsed entity. */
        public final boolean isUnparsed() {
            return false;
        } // isUnparsed():boolean
        
        /** Clears the entity. */
        public void clear() {
            super.clear();
            text = null;
        } // clear()
        
        /** Sets the values of the entity. */
        public void setValues(Entity entity) {
            super.setValues(entity);
            text = null;
        } // setValues(Entity)
        
        /** Sets the values of the entity. */
        public void setValues(InternalEntity entity) {
            super.setValues(entity);
            text = entity.text;
        } // setValues(InternalEntity)
        
    } // class InternalEntity
    
    /**
     * External entity.
     *
     * @author nb131165
     */
    public  static class ExternalEntity
    extends Entity {
        
        //
        // Data
        //
        
        /** container for all relevant entity location information. */
        public XMLResourceIdentifier entityLocation;
        
        /** Notation name for unparsed entity. */
        public String notation;
        
        //
        // Constructors
        //
        
        /** Default constructor. */
        public ExternalEntity() {
            clear();
        } // <init>()
        
        /** Constructs an internal entity. */
        public ExternalEntity(String name, XMLResourceIdentifier entityLocation,
        String notation, boolean inExternalSubset) {
            super(name,inExternalSubset);
            this.entityLocation = entityLocation;
            this.notation = notation;
        } // <init>(String,XMLResourceIdentifier, String)
        
        //
        // Entity methods
        //
        
        /** Returns true if this is an external entity. */
        public final boolean isExternal() {
            return true;
        } // isExternal():boolean
        
        /** Returns true if this is an unparsed entity. */
        public final boolean isUnparsed() {
            return notation != null;
        } // isUnparsed():boolean
        
        /** Clears the entity. */
        public void clear() {
            super.clear();
            entityLocation = null;
            notation = null;
        } // clear()
        
        /** Sets the values of the entity. */
        public void setValues(Entity entity) {
            super.setValues(entity);
            entityLocation = null;
            notation = null;
        } // setValues(Entity)
        
        /** Sets the values of the entity. */
        public void setValues(ExternalEntity entity) {
            super.setValues(entity);
            entityLocation = entity.entityLocation;
            notation = entity.notation;
        } // setValues(ExternalEntity)
        
    } // class ExternalEntity
    
    /**
     * Entity state.
     *
     * @author nb131165
     */
    public static class ScannedEntity
    extends Entity {
        
        
        /** Default buffer size (4096). */
        public static final int DEFAULT_BUFFER_SIZE = 8192;
        //4096;
        
        /**
         * Buffer size. We get this value from a property. The default size
         * is used if the input buffer size property is not specified.
         * REVISIT: do we need a property for internal entity buffer size?
         */
        public int fBufferSize = DEFAULT_BUFFER_SIZE;
        
        /** Default buffer size before we've finished with the XMLDecl:  */
        public static final int DEFAULT_XMLDECL_BUFFER_SIZE = 64;
        
        /** Default internal entity buffer size (1024). */
        public static final int DEFAULT_INTERNAL_BUFFER_SIZE = 1024;
        
        //
        // Data
        //
        
        // i/o
        
        /** XXX let these field remain public right now, though we have defined methods for them.
         * Input stream. */
        public InputStream stream;
        
        /** XXX let these field remain public right now, though we have defined methods for them.
         * Reader. */
        public Reader reader;
        
        // locator information
        
        /** entity location information */
        public XMLResourceIdentifier entityLocation;
        
        // encoding
        
        /** Auto-detected encoding. */
        public String encoding;
        
        // status
        
        /** True if in a literal.  */
        public boolean literal;
        
        // whether this is an external or internal scanned entity
        public boolean isExternal;
        
        //each 'external' parsed entity may have xml/text declaration containing version information
        public String  version ;
        
        // buffer
        
        /** Character buffer. */
        public char[] ch = null;
        
        /** Position in character buffer at any point of time. */
        public int position;
        
        /** Count of characters present in buffer. */
        public int count;
        
        public int lineNumber = 1 ;
        public int columnNumber = 1;
    
        /** This variable is used to calculate the current position in the XML stream.
            Note that fCurrentEntity.position maintains the position relative to 
            the buffer. 
         *  At any point of time absolute position in the XML stream can be calculated
         *  as fTotalCountTillLastLoad + fCurrentEntity.position
        */
        public int fTotalCountTillLastLoad ;

        /** This variable stores the number of characters read during the load() 
         * operation. It is used to calculate fTotalCountTillLastLoad 
         */
        public  int fLastCount ;
        
        // to allow the reader/inputStream to behave efficiently:
        public boolean mayReadChunks;
        
        /** returns the name of the current encoding
         *  @return current encoding name
         */
        public String getEncodingName(){
            return encoding ;
        }
        
        /**each 'external' parsed entity may have xml/text declaration containing version information
         * @return String version of the enity, for an internal entity version would be null
         */
        public String getEntityVersion(){
            return version ;
        }
        
        /** each 'external' parsed entity may have xml/text declaration containing version information
         * @param String version of the external parsed entity
         */
        public void setEntityVersion(String version){
            this.version = version ;
        }
        
        /**  Returns the java.io.Reader associated with this entity.Readers are used
         * to read from the file. Readers wrap any particular  InputStream that was
         * used to open the entity.
         * @return java.io.Reader Reader associated with this entity
         */
        public Reader getEntityReader(){
            return reader;
        }
        
        
        /** if entity was opened using the stream, return the associated inputstream
         * with this entity
         *@return java.io.InputStream InputStream associated with this entity
         */
        public InputStream getEntityInputStream(){
            return stream;
        }
        
        //
        // Constructors
        //
        
        /** Constructs a scanned entity. */
        public ScannedEntity(String name,
        XMLResourceIdentifier entityLocation,
        InputStream stream, Reader reader,
        String encoding, boolean literal, boolean mayReadChunks, boolean isExternal) {
            this.name = name ;
            this.entityLocation = entityLocation;
            this.stream = stream;
            this.reader = reader;
            this.encoding = encoding;
            this.literal = literal;
            this.mayReadChunks = mayReadChunks;
            this.isExternal = isExternal;
            final int size = isExternal ? fBufferSize : DEFAULT_INTERNAL_BUFFER_SIZE;
            BufferAllocator ba = ThreadLocalBufferAllocator.getBufferAllocator();
            ch = ba.getCharBuffer(size);
            if (ch == null) {
                this.ch = new char[size];
            }
        } // <init>(StringXMLResourceIdentifier,InputStream,Reader,String,boolean, boolean)
        
        /**
         * Release any resources associated with this entity.
         */
        public void close() throws IOException {
            BufferAllocator ba = ThreadLocalBufferAllocator.getBufferAllocator();
            ba.returnCharBuffer(ch);
            ch = null;
            reader.close();                       
        }
        
        //
        // Entity methods
        //
        
        /** Returns true if this is an external entity. */
        public final boolean isExternal() {
            return isExternal;
        } // isExternal():boolean
        
        /** Returns true if this is an unparsed entity. */
        public final boolean isUnparsed() {
            return false;
        } // isUnparsed():boolean
        
        //
        // Object methods
        //
        
        /** Returns a string representation of this object. */
        public String toString() {
            
            StringBuffer str = new StringBuffer();
            str.append("name=\""+name+'"');
            str.append(",ch="+ new String(ch));
            str.append(",position="+position);
            str.append(",count="+count);
            return str.toString();
            
        } // toString():String
        
    } // class ScannedEntity
    
} // class Entity

