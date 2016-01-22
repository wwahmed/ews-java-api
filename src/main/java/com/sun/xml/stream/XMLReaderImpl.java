/*
 * $Id: XMLReaderImpl.java,v 1.22 2009-11-13 08:34:40 joehw Exp $
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

package com.sun.xml.stream;

import com.sun.xml.stream.events.EntityDeclarationImpl;
import com.sun.xml.stream.events.NotationDeclarationImpl;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.CharArrayReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import javax.xml.namespace.NamespaceContext;
import com.sun.xml.stream.xerces.xni.XNIException;
import com.sun.xml.stream.xerces.xni.parser.XMLInputSource;
import javax.xml.namespace.QName;
import javax.xml.stream.Location;
import javax.xml.stream.events.XMLEvent;
import com.sun.xml.stream.xerces.util.NamespaceContextWrapper;
import com.sun.xml.stream.xerces.util.SymbolTable;
import com.sun.xml.stream.dtd.nonvalidating.XMLNotationDecl;
import com.sun.xml.stream.dtd.nonvalidating.DTDGrammar;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import com.sun.xml.stream.xerces.impl.msg.XMLMessageFormatter;
import com.sun.xml.stream.xerces.util.XMLChar;
import com.sun.xml.stream.xerces.util.XMLStringBuffer;
import org.xml.sax.InputSource;
import javax.xml.XMLConstants;



/** This class implements javax.xml.stream.XMLStreamReader. It makes use of XML*Scanner classes to
 * derive most of its functionality. If desired, Application can reuse this instance by calling
 * reset() and setInputSource().
 *
 * @author Neeraj Bajaj Sun Microsystems,Inc.
 * @author K.Venugopal Sun Microsystems,Inc.
 */
public class XMLReaderImpl implements javax.xml.stream.XMLStreamReader {
    
    /** Property identifier: entity manager. */
    protected static final String ENTITY_MANAGER =
            Constants.XERCES_PROPERTY_PREFIX + Constants.ENTITY_MANAGER_PROPERTY;
    
    /** Property identifier: Error Reporter. */
    protected static final String ERROR_REPORTER =
            Constants.XERCES_PROPERTY_PREFIX + Constants.ERROR_REPORTER_PROPERTY;
    
    /** Property identifier: Symbol table. */
    protected static final String SYMBOL_TABLE =
            Constants.XERCES_PROPERTY_PREFIX + Constants.SYMBOL_TABLE_PROPERTY;
    
    private SymbolTable fSymbolTable = new SymbolTable();
    
    /** Document scanner. */
    protected XMLNSDocumentScannerImpl fScanner = new XMLNSDocumentScannerImpl();
    
    //make Global NamespaceContextWrapper object,  fScanner.getNamespaceContext() is dynamic object and ita value changes
    //as per the state of the parser.
    protected NamespaceContextWrapper fNamespaceContextWrapper = new NamespaceContextWrapper(fScanner.getNamespaceContext()) ;
    protected XMLEntityManager fEntityManager = new XMLEntityManager();
    protected StaxErrorReporter fErrorReporter = new StaxErrorReporter();
    
    
    /** Entity scanner, this alwasy works on last entity that was opened. */
    protected XMLEntityReaderImpl fEntityScanner = null;
    
    /** Input Source */
    protected XMLInputSource fInputSource = null;
    /** Store properties*/
    protected PropertyManager fPropertyManager = null ;
    
    /** current event type */
    private int fEventType ;
    /** debug flag*/
    static final boolean DEBUG = false ;
    /** more to scan */
    private boolean fReuse = true;
    private boolean fBindNamespaces = true;
    private String fDTDDecl = null;
    
    /**
     * @param inputStream
     * @param props
     * @throws XMLStreamException
     */
    public XMLReaderImpl(InputStream inputStream, PropertyManager props) throws  XMLStreamException {
        init(props);
        //publicId, systemid, baseSystemId, inputStream, enocding
        XMLInputSource inputSource = new XMLInputSource(null,null,null,inputStream,null);
        //pass the input source to document scanner impl.
        setInputSource(inputSource);
    }
    
    /**
     * @param systemid
     * @param props
     * @throws XMLStreamException
     */
    public XMLReaderImpl(String systemid, PropertyManager props) throws  XMLStreamException {
        init(props);
        //publicId, systemid, baseSystemId, inputStream, enocding
        XMLInputSource inputSource = new XMLInputSource(null,systemid,null);
        //pass the input source to document scanner impl.
        setInputSource(inputSource);
    }
    
    
    /**
     * @param inputStream
     * @param encoding
     * @param props
     * @throws XMLStreamException
     */
    public XMLReaderImpl(InputStream inputStream, String encoding, PropertyManager props ) throws  XMLStreamException {
        init(props);
        //publicId, systemid, baseSystemId, inputStream, enocding
        XMLInputSource inputSource = new XMLInputSource(null,null,null, new BufferedInputStream(inputStream),encoding );
        //pass the input source to document scanner impl.
        setInputSource(inputSource);
    }
    
    /**
     * @param reader
     * @param props
     * @throws XMLStreamException
     */
    public XMLReaderImpl(Reader reader, PropertyManager props) throws  XMLStreamException {
        init(props);
        //publicId, systemid, baseSystemId, inputStream, enocding
        //xxx: Using buffered reader
        XMLInputSource inputSource = new XMLInputSource(null,null,null,new BufferedReader(reader),null);
        //pass the input source to document scanner impl.
        setInputSource(inputSource);
    }
    
    /**
     * @param inputSource
     * @param props
     * @throws XMLStreamException
     */
    public XMLReaderImpl(XMLInputSource inputSource, PropertyManager props) throws  XMLStreamException {
        init(props);
        //pass the input source to document scanner impl.
        setInputSource(inputSource);
    }
    
    /**
     * @param inputSource
     * @throws XMLStreamException
     */
    public void setInputSource(XMLInputSource inputSource ) throws XMLStreamException {
        //once setInputSource() is called this instance is busy parsing the inputsource supplied
        //this instances is free for reuse if application has explicitly called close()
        fReuse = false;
        
        try{
            
            fScanner.setInputSource(inputSource) ;
            fEventType = fScanner.next();
        }catch(IOException ex){
            throw new XMLStreamException2(ex);
        } catch(XNIException ex){ //Issue 56 XNIException not caught
            throw new XMLStreamException2(ex.getMessage(), getLocation(), ex.getException());
        }
    }//setInputSource
    
    /** Set the InputSource on this instance.
     *
     * @param InputSource InputSource encapsulating information about XML Source.
     * @throws XMLStreamException Throws XMLStreamException if there is a problem setting InputSource
     */
    public void setInputSource(InputSource inputSource) throws XMLStreamException{
       setInputSource(convertSAXInputSource2XMLInputSource(inputSource)); 
    }
    
    /** Convert SAX InputSource to XMLInputSource.
     *
     * @return XMLInputSource XNI XMLInputSource.
     */    
    XMLInputSource convertSAXInputSource2XMLInputSource(InputSource inputSource){
        XMLInputSource xmlInputSource =
            new XMLInputSource(inputSource.getPublicId(),
            inputSource.getSystemId(),
            null);
            InputStream inputStream = inputSource.getByteStream();
            // If necessary, buffer input stream
            if (inputStream != null && inputStream instanceof ByteArrayInputStream == false &&
                inputStream instanceof BufferedInputStream == false)  {
                inputStream = new BufferedInputStream(inputStream);
            }
            xmlInputSource.setByteStream(inputStream);
            Reader reader = inputSource.getCharacterStream();
            // If necessary, buffer reader
            if (reader != null && reader instanceof BufferedReader == false &&
                reader instanceof CharArrayReader == false &&
                reader instanceof StringReader == false) {
                reader = new BufferedReader(reader);
            }
            xmlInputSource.setCharacterStream(reader);
            xmlInputSource.setEncoding(inputSource.getEncoding());
            
        return xmlInputSource; 
    }        
    
    void init(PropertyManager propertyManager) throws XMLStreamException {
        fPropertyManager = propertyManager;
        //set zephyr internal properties -- Note that these instances are being created in XMLReaderImpl.
        //1.SymbolTable
        //2.XMLMessageFormatter
        //3.XMLEntityManager
        //4. call reset()
        //1.
        propertyManager.setProperty(SYMBOL_TABLE,  fSymbolTable ) ;
        //2.
        propertyManager.setProperty(ERROR_REPORTER,  fErrorReporter ) ;
        //3.
        propertyManager.setProperty(ENTITY_MANAGER, fEntityManager);
        //4.
        reset();
    }
    
    /** This function tells if this instances is available for reuse.
     * One must call reset() and setInputSource() to be able to reuse
     * this instance.
     */
    public boolean canReuse(){
        if(DEBUG){
            System.out.println("fReuse = " + fReuse);
            System.out.println("fEventType = " + getEventTypeString(fEventType) );
        }
        //when parsing begins, fReuse is set to false
        //fReuse is set to 'true' when application calls close()
        return fReuse ;
    }
    
    /**
     * Resets this instance so that this instance is ready for reuse.
     */
    public void reset(){
        fReuse = true;
        fEventType = 0 ;
        //reset entity manager
        fEntityManager.reset(fPropertyManager);
        //reset the scanner
        fScanner.reset(fPropertyManager);
        //REVISIT:this is too ugly -- we are getting XMLEntityManager and XMLEntityReader from
        //property manager, it should be only XMLEntityManager
        fDTDDecl = null;
        fEntityScanner = (XMLEntityReaderImpl)fEntityManager.getEntityReader()  ;
        //default value for this property is true. However, this should be false when using XMLEventReader... Ugh..
        //because XMLEventReader should not have defined state.
        fBindNamespaces = ((Boolean)fPropertyManager.getProperty(XMLInputFactory.IS_NAMESPACE_AWARE)).booleanValue();
    }
    
    
    /** Frees any resources associated with this Reader. This method does not close the underlying input source.
     * @throws XMLStreamException if there are errors freeing associated resources
     */
    public void close() throws XMLStreamException {
        //xxx: Check what this function is intended to do.
        //reset();
        fReuse = true ;
    }
    
    
    /** Returns the character encoding declared on the xml declaration Returns null if none was declared
     * @return the encoding declared in the document or null
     */
    public String getCharacterEncodingScheme() {
        return fScanner.getCharacterEncodingScheme();
        
    }
    
    
    /**
     * @return
     */
    public int getColumnNumber() {
        return fEntityScanner.getColumnNumber();
    }//getColumnNumber
    
    /** Return input encoding if known or null if unknown.
     * @return the encoding of this instance or null
     */
    public String getEncoding() {
        return fEntityScanner.getEncoding();
    }//getEncoding
    
    /** Returns the current value of the parse event as a string, this returns the string value of a CHARACTERS event, returns the value of a COMMENT, the replacement value for an ENTITY_REFERENCE, the string value of a CDATA section, the string value for a SPACE event, or the String value of the internal subset of the DTD. If an ENTITY_REFERENCE has been resolved, any character data will be reported as CHARACTERS events.
     * @return the current text or null
     */
    public int getEventType() {
        return fEventType ;
    }//getEventType
    
    /**
     * @return
     */
    public int getLineNumber() {
        return fEntityScanner.getLineNumber() ;
    }//getLineNumber
    
    public String getLocalName() {
        if(fEventType == XMLEvent.START_ELEMENT || fEventType == XMLEvent.END_ELEMENT){
            //xxx check whats the value of fCurrentElement
            return fScanner.getElementQName().localpart ;
        } else if(fEventType == XMLEvent.ENTITY_REFERENCE){
            return fScanner.getEntityName();
        }
        throw new IllegalStateException("Method getLocalName() cannot be called for " +
                getEventTypeString(fEventType) + " event.");
    }//getLocalName()
    
    /**
     * @return
     */
    public String getNamespaceURI() {
        //doesn't take care of Attribute as separte event
        if(fEventType == XMLEvent.START_ELEMENT || fEventType == XMLEvent.END_ELEMENT){
            return fScanner.getElementQName().uri ;
        }
        return null ;
    }//getNamespaceURI
    
    /** Get the data section of a processing instruction
     * @return the data or null
     */
    
    public String getPIData() {
        if( fEventType == XMLEvent.PROCESSING_INSTRUCTION){
            return fScanner.getPIData().toString();
        } else throw new IllegalStateException("Current state of the parser is " + getEventTypeString(fEventType) +
                " But expected state is " + getEventTypeString(XMLEvent.PROCESSING_INSTRUCTION)  ) ;
    }//getPIData
    
    
    /** Get the target of a processing instruction
     * @return the target or null
     */
    public String getPITarget() {
        if( fEventType == XMLEvent.PROCESSING_INSTRUCTION){
            return fScanner.getPITarget();
        } else throw new IllegalStateException("Current state of the parser is " + getEventTypeString(fEventType) +
                " But expected state is " + getEventTypeString(XMLEvent.PROCESSING_INSTRUCTION)  ) ;
        
    }//getPITarget
    
    
    /**
     * Returns the prefix of the current event, or null if the event does 
     * not have a prefix. For START_ELEMENT and END_ELEMENT, return 
     * XMLConstants.DEFAULT_NS_PREFIX when no prefix is available.
     */
    public String getPrefix() {
        if(fEventType == XMLEvent.START_ELEMENT || fEventType == XMLEvent.END_ELEMENT){
            String prefix = fScanner.getElementQName().prefix;
            return prefix == null ? XMLConstants.DEFAULT_NS_PREFIX : prefix;
        }
        return null ;
    }//getPrefix()
    
    
    
    /**
     * @return
     */
    public char[] getTextCharacters() {
        if( fEventType == XMLEvent.CHARACTERS || fEventType == XMLEvent.COMMENT
                || fEventType == XMLEvent.CDATA || fEventType == XMLEvent.SPACE){
            return fScanner.getCharacterData().ch;
        } else{
            throw new IllegalStateException("Current state = " + getEventTypeString(fEventType)
            + " is not among the states " + getEventTypeString(XMLEvent.CHARACTERS) + " , "
                    + getEventTypeString(XMLEvent.COMMENT) + " , " + getEventTypeString(XMLEvent.CDATA)
                    + " , " + getEventTypeString(XMLEvent.SPACE) +" valid for getTextCharacters() " ) ;
        }
    }
    
    /**
     * @return
     */
    public int getTextLength() {
        if( fEventType == XMLEvent.CHARACTERS || fEventType == XMLEvent.COMMENT
                || fEventType == XMLEvent.CDATA || fEventType == XMLEvent.SPACE){
            return fScanner.getCharacterData().length;
        } else{
            throw new IllegalStateException("Current state = " + getEventTypeString(fEventType) 
            + " is not among the states " + getEventTypeString(XMLEvent.CHARACTERS) + " , " 
                    + getEventTypeString(XMLEvent.COMMENT) + " , " + getEventTypeString(XMLEvent.CDATA) 
                    + " , " + getEventTypeString(XMLEvent.SPACE) +" valid for getTextLength() " ) ;
        }
    }
    
    /**
     * @return
     */
    public int getTextStart() {
        if( fEventType == XMLEvent.CHARACTERS || fEventType == XMLEvent.COMMENT || fEventType == XMLEvent.CDATA || fEventType == XMLEvent.SPACE){
            return  fScanner.getCharacterData().offset;
        } else{
            throw new IllegalStateException("Current state = " + getEventTypeString(fEventType)
            + " is not among the states " + getEventTypeString(XMLEvent.CHARACTERS) + " , "
                    + getEventTypeString(XMLEvent.COMMENT) + " , " + getEventTypeString(XMLEvent.CDATA)
                    + " , " + getEventTypeString(XMLEvent.SPACE) +" valid for getTextStart() " ) ;
        }
    }
    
    /**
     * @return
     */
    public String getValue() {
        if(fEventType == XMLEvent.PROCESSING_INSTRUCTION){
            return fScanner.getPIData().toString();
        } else if(fEventType == XMLEvent.COMMENT){
            return fScanner.getComment();
        } else if(fEventType == XMLEvent.START_ELEMENT || fEventType == XMLEvent.END_ELEMENT){
            return fScanner.getElementQName().localpart ;
        } else if(fEventType == XMLEvent.CHARACTERS){
            return fScanner.getCharacterData().toString();
        }
        return null;
    }//getValue()
    
    /** Get the XML language version of the current document being parsed */
    public String getVersion() {
        return fEntityScanner.getVersion();
    }
    
    /**
     * @return
     */
    public boolean hasAttributes() {
        return fScanner.getAttributeIterator().getLength() > 0 ? true : false ;
    }
    
    /** this Funtion returns true if the current event has name */
    public boolean hasName() {
        if(fEventType == XMLEvent.START_ELEMENT || fEventType == XMLEvent.END_ELEMENT) {
            return true;
        } else {
            return false;
        }
    }//hasName()
    
    /**
     * @throws XMLStreamException
     * @return
     */
    public boolean hasNext() throws XMLStreamException {
        //the scanner returns -1 when it detects a broken stream
        if (fEventType == -1) return false;
        //we can check in scanners if the scanner state is not set to 
        //terminating, we still have more events.
        return fEventType != XMLEvent.END_DOCUMENT;
    }
    
    /**
     * @return
     */
    public boolean hasValue() {
        if(fEventType == XMLEvent.START_ELEMENT || fEventType == XMLEvent.END_ELEMENT
                || fEventType == XMLEvent.ENTITY_REFERENCE || fEventType == XMLEvent.PROCESSING_INSTRUCTION
                || fEventType == XMLEvent.COMMENT || fEventType == XMLEvent.CHARACTERS) {
            return true;
        } else {
            return false;
        }
        
    }
    
    /**
     * @return
     */
    public boolean isEndElement() {
        return fEventType == XMLEvent.END_ELEMENT;
    }
    
    /**
     * @return
     */
    public boolean isStandalone() {
        return fScanner.isStandAlone();
    }
    
    /**
     * @return
     */
    public boolean isStartElement() {
        return fEventType == XMLEvent.START_ELEMENT;
    }
    
    /**
     *  Returns true if the cursor points to a character data event that consists of all whitespace
     *  Application calling this method needs to cache the value and avoid calling this method again
     *  for the same event.
     * @return
     */
    public boolean isWhiteSpace() {
        if(isCharacters() || (fEventType == XMLStreamConstants.CDATA)){
            char [] ch = this.getTextCharacters();
            int start = this.getTextStart();
            int end = start + this.getTextLength();
            for (int i = start; i < end; i++){
                if(!XMLChar.isSpace(ch[i])){
                    return false;
                }
            }
            return true;
        }
        return false;
    }
    
    
    
    /**
     * @throws XMLStreamException
     * @return
     */
    public int next() throws XMLStreamException {
        if( !hasNext() ) {
            if (fEventType != -1) {
                throw new java.util.NoSuchElementException( "END_DOCUMENT reached: no more elements on the stream." );
            } else { 
                throw new XMLStreamException( "Error processing input source. The input stream is not complete." ); 
            }
        }
        try {
            return fEventType = fScanner.next();
        } catch (IOException ex) {
            // if this error occured trying to resolve the external DTD subset
            // and IS_VALIDATING == false, then this is not an XML error
            if (fScanner.fScannerState == fScanner.SCANNER_STATE_DTD_EXTERNAL) {
                Boolean isValidating = (Boolean) fPropertyManager.getProperty(
                        XMLInputFactory.IS_VALIDATING);
                if (isValidating != null
                        && !isValidating.booleanValue()) {
                    // ignore the error, set scanner to known state                    
                    fEventType = XMLEvent.DTD;
                    fScanner.setScannerState(fScanner.SCANNER_STATE_PROLOG);
                    fScanner.setDriver(fScanner.fPrologDriver);
                    if (fDTDDecl == null
                            || fDTDDecl.length() == 0) {
                        fDTDDecl = "<!-- "
                                + "Exception scanning External DTD Subset.  "
                                + "True contents of DTD cannot be determined.  "
                                + "Processing will continue as XMLInputFactory.IS_VALIDATING == false."
                                + " -->";
                    }
                    return XMLEvent.DTD;
                }
            }

            // else real error
            throw new XMLStreamException2(ex.getMessage(), getLocation(), ex);
        } catch (XNIException ex) {
            throw new XMLStreamException2(ex.getMessage(), getLocation(), ex.getException());
        }
    } //next()    
    
    
    final static String getEventTypeString(int eventType) {
        switch (eventType){
            case XMLEvent.START_ELEMENT:
                return "START_ELEMENT";
            case XMLEvent.END_ELEMENT:
                return "END_ELEMENT";
            case XMLEvent.PROCESSING_INSTRUCTION:
                return "PROCESSING_INSTRUCTION";
            case XMLEvent.CHARACTERS:
                return "CHARACTERS";
            case XMLEvent.COMMENT:
                return "COMMENT";
            case XMLEvent.START_DOCUMENT:
                return "START_DOCUMENT";
            case XMLEvent.END_DOCUMENT:
                return "END_DOCUMENT";
            case XMLEvent.ENTITY_REFERENCE:
                return "ENTITY_REFERENCE";
            case XMLEvent.ATTRIBUTE:
                return "ATTRIBUTE";
            case XMLEvent.DTD:
                return "DTD";
            case XMLEvent.CDATA:
                return "CDATA";
            case XMLEvent.SPACE:
                return "SPACE";
        }
        return "UNKNOWN_EVENT_TYPE , " + String.valueOf(eventType);
    }
    
    /** Returns the count of attributes on this START_ELEMENT,
     * this method is only valid on a START_ELEMENT or ATTRIBUTE.  This
     * count excludes namespace definitions.  Attribute indices are
     * zero-based.
     * @return returns the number of attributes
     * @throws IllegalStateException if this is not a START_ELEMENT or ATTRIBUTE
     */
    public int getAttributeCount() {
        //xxx: recognize SAX properties namespace, namespace-prefix to get XML Namespace declarations
        //does length includes namespace declarations ?
        
        //State should be either START_ELEMENT or ATTRIBUTE
        if( fEventType == XMLEvent.START_ELEMENT || fEventType == XMLEvent.ATTRIBUTE) {
            return fScanner.getAttributeIterator().getLength() ;
        } else{
            throw new IllegalStateException("Current state is not among the states "
                    + getEventTypeString(XMLEvent.START_ELEMENT) + " , " 
                    + getEventTypeString(XMLEvent.ATTRIBUTE)
                    + "valid for getAttributeCount()") ;
        }
    }//getAttributeCount
    
    /** Returns the localName of the attribute at the provided
     * index
     * @param index the position of the attribute
     * @return the localName of the attribute
     * @throws IllegalStateException if this is not a START_ELEMENT or ATTRIBUTE
     */
    public QName getAttributeName(int index) {
        //State should be either START_ELEMENT or ATTRIBUTE
        if( fEventType == XMLEvent.START_ELEMENT || fEventType == XMLEvent.ATTRIBUTE) {
            return convertXNIQNametoJavaxQName(fScanner.getAttributeIterator().getQualifiedName(index));
        } else{
            throw new IllegalStateException("Current state is not among the states "
                    + getEventTypeString(XMLEvent.START_ELEMENT) + " , " 
                    + getEventTypeString(XMLEvent.ATTRIBUTE)
                    + "valid for getAttributeName()") ;
        }
    }//getAttributeName
    
    /**
     * @param index
     * @return
     */
    public String getAttributeLocalName(int index) {
        //State should be either START_ELEMENT or ATTRIBUTE
        if( fEventType == XMLEvent.START_ELEMENT || fEventType == XMLEvent.ATTRIBUTE) {
            return fScanner.getAttributeIterator().getLocalName(index) ;
        } else{
            throw new IllegalStateException("Current state is not among the states "
                    + getEventTypeString(XMLEvent.START_ELEMENT) + " , " 
                    + getEventTypeString(XMLEvent.ATTRIBUTE)
                    + "valid for getAttributeLocalName()") ;
        }
    }//getAttributeName
    
    /** Returns the namespace of the attribute at the provided
     * index
     * @param index the position of the attribute
     * @return the namespace URI (can be null)
     * @throws IllegalStateException if this is not a START_ELEMENT or ATTRIBUTE
     */
    public String getAttributeNamespace(int index) {
        //State should be either START_ELEMENT or ATTRIBUTE
        if( fEventType == XMLEvent.START_ELEMENT || fEventType == XMLEvent.ATTRIBUTE) {
            return fScanner.getAttributeIterator().getURI(index);
        } else{
            throw new IllegalStateException("Current state is not among the states "
                    + getEventTypeString(XMLEvent.START_ELEMENT) + " , " 
                    + getEventTypeString(XMLEvent.ATTRIBUTE)
                    + "valid for getAttributeNamespace()") ;
        }
        
    }//getAttributeNamespace
    
    /** Returns the prefix of this attribute at the
     * provided index
     * @param index the position of the attribute
     * @return the prefix of the attribute
     * @throws IllegalStateException if this is not a START_ELEMENT or ATTRIBUTE
     */
    public String getAttributePrefix(int index) {
        //State should be either START_ELEMENT or ATTRIBUTE
        if( fEventType == XMLEvent.START_ELEMENT || fEventType == XMLEvent.ATTRIBUTE) {
            return fScanner.getAttributeIterator().getPrefix(index);
        } else{
            throw new IllegalStateException("Current state is not among the states "
                    + getEventTypeString(XMLEvent.START_ELEMENT) + " , " 
                    + getEventTypeString(XMLEvent.ATTRIBUTE)
                    + "valid for getAttributePrefix()") ;
        }
    }//getAttributePrefix
    
    /** Returns the qname of the attribute at the provided index
     *
     * @param index the position of the attribute
     * @return the QName of the attribute
     * @throws IllegalStateException if this is not a START_ELEMENT or ATTRIBUTE
     */
    public QName getAttributeQName(int index) {
        //State should be either START_ELEMENT or ATTRIBUTE
        if( fEventType == XMLEvent.START_ELEMENT || fEventType == XMLEvent.ATTRIBUTE) {
            // create new object at runtime..
            String localName = fScanner.getAttributeIterator().getLocalName(index) ;
            String uri = fScanner.getAttributeIterator().getURI(index) ;
            return new QName(uri, localName) ;
        } else{
            throw new IllegalStateException("Current state is not among the states "
                    + getEventTypeString(XMLEvent.START_ELEMENT) + " , " 
                    + getEventTypeString(XMLEvent.ATTRIBUTE)
                    + "valid for getAttributeQName()") ;
        }
    }//getAttributeQName
    
    /** Returns the XML type of the attribute at the provided
     * index
     * @param index the position of the attribute
     * @return the XML type of the attribute
     * @throws IllegalStateException if this is not a START_ELEMENT or ATTRIBUTE
     */
    public String getAttributeType(int index) {
        //State should be either START_ELEMENT or ATTRIBUTE
        if( fEventType == XMLEvent.START_ELEMENT || fEventType == XMLEvent.ATTRIBUTE) {
            return fScanner.getAttributeIterator().getType(index) ;
        } else{
            throw new IllegalStateException("Current state is not among the states "
                    + getEventTypeString(XMLEvent.START_ELEMENT) + " , " 
                    + getEventTypeString(XMLEvent.ATTRIBUTE)
                    + "valid for getAttributeType()") ;
        }
        
    }//getAttributeType
    
    /** Returns the value of the attribute at the
     * index
     * @param index the position of the attribute
     * @return the attribute value
     * @throws IllegalStateException if this is not a START_ELEMENT or ATTRIBUTE
     */
    public String getAttributeValue(int index) {
        //State should be either START_ELEMENT or ATTRIBUTE
        if( fEventType == XMLEvent.START_ELEMENT || fEventType == XMLEvent.ATTRIBUTE) {
            return fScanner.getAttributeIterator().getValue(index) ;
        } else{
            throw new IllegalStateException("Current state is not among the states "
                    + getEventTypeString(XMLEvent.START_ELEMENT) + " , " 
                    + getEventTypeString(XMLEvent.ATTRIBUTE)
                    + "valid for getAttributeValue()") ;
        }
        
    }//getAttributeValue
    
    /**
     * @param namespaceURI
     * @param localName
     * @return
     */
    public String getAttributeValue(String namespaceURI, String localName) {
        //State should be either START_ELEMENT or ATTRIBUTE
        if( fEventType == XMLEvent.START_ELEMENT || fEventType == XMLEvent.ATTRIBUTE) {
            return fScanner.getAttributeIterator().getValue(namespaceURI, localName) ;
        } else{
            throw new IllegalStateException("Current state is not among the states "
                    + getEventTypeString(XMLEvent.START_ELEMENT) + " , " 
                    + getEventTypeString(XMLEvent.ATTRIBUTE)
                    + "valid for getAttributeValue()") ;
        }
        
    }
    
    /** Reads the content of a text-only element. Precondition:
     * the current event is START_ELEMENT. Postcondition:
     * The current event is the corresponding END_ELEMENT.
     * @throws XMLStreamException if the current event is not a START_ELEMENT or if
     * a non text element is encountered
     */
    public String getElementText() throws XMLStreamException {
        if(getEventType() != XMLStreamConstants.START_ELEMENT) {
            throw new XMLStreamException2(
            "parser must be on START_ELEMENT to read next text", getLocation());
        }
        int eventType = next();
        StringBuffer content = new StringBuffer();
        while(eventType != XMLStreamConstants.END_ELEMENT ) {
            if(eventType == XMLStreamConstants.CHARACTERS
            || eventType == XMLStreamConstants.CDATA
            || eventType == XMLStreamConstants.SPACE
            || eventType == XMLStreamConstants.ENTITY_REFERENCE) {
                content.append(getText());
            } else if(eventType == XMLStreamConstants.PROCESSING_INSTRUCTION
            || eventType == XMLStreamConstants.COMMENT) {
                // skipping
            } else if(eventType == XMLStreamConstants.END_DOCUMENT) {
                throw new XMLStreamException2("unexpected end of document when reading element text content");
            } else if(eventType == XMLStreamConstants.START_ELEMENT) {
                throw new XMLStreamException2(
                "elementGetText() function expects text only elment but START_ELEMENT was encountered.", getLocation());
            } else {
                throw new XMLStreamException2(
                "Unexpected event type "+ eventType, getLocation());
            }
            eventType = next();
        }
        return content.toString();
    }
    
    /** Return the current location of the processor.
     * If the Location is unknown the processor should return
     * an implementation of Location that returns -1 for the
     * location and null for the publicId and systemId.
     * The location information is only valid until next() is
     * called.
     */
    public Location getLocation() {
        return new Location() {
            String _systemId = fEntityScanner.getExpandedSystemId();
            String _publicId = fEntityScanner.getPublicId();
            int _offset = fEntityScanner.getCharacterOffset();
            int _columnNumber = fEntityScanner.getColumnNumber();
            int _lineNumber = fEntityScanner.getLineNumber(); 
            public String getLocationURI(){
                return _systemId;
            }
            
            public int getCharacterOffset(){
                return _offset;
            }
            
            public int getColumnNumber() {
                return _columnNumber;
            }
            
            public int getLineNumber(){
                return _lineNumber;
            }
            
            public String getPublicId(){
                return _publicId;
            }
            
            public String getSystemId(){
                return _systemId;
            }
            
            public String toString(){
                StringBuffer sbuffer = new StringBuffer() ;
                sbuffer.append("Line number = " + getLineNumber());
                sbuffer.append("\n") ;
                sbuffer.append("Column number = " + getColumnNumber());
                sbuffer.append("\n") ;
                sbuffer.append("System Id = " + getSystemId());
                sbuffer.append("\n") ;
                sbuffer.append("Public Id = " + getPublicId());
                sbuffer.append("\n") ;
                sbuffer.append("Location Uri= " + getLocationURI());
                sbuffer.append("\n") ;
                sbuffer.append("CharacterOffset = " + getCharacterOffset());
                sbuffer.append("\n") ;
                return sbuffer.toString();
            }
        } ;
        
    }
    
    /** Returns a QName for the current START_ELEMENT or END_ELEMENT event
     * @return the QName for the current START_ELEMENT or END_ELEMENT event
     */
    public QName getName() {
        if(fEventType == XMLEvent.START_ELEMENT || fEventType == XMLEvent.END_ELEMENT)
            return convertXNIQNametoJavaxQName(fScanner.getElementQName());
        else
            throw new IllegalStateException("Illegal to call getName() "+
                    "when event type is " + getEventTypeString(fEventType) + "."
                    + " Valid states are " + getEventTypeString(XMLEvent.START_ELEMENT) + ", "
                    + getEventTypeString(XMLEvent.END_ELEMENT));
    }
    
    /** Returns a read only namespace context for the current
     * position.  The context is transient and only valid until
     * a call to next() changes the state of the reader.
     * @return return a namespace context
     */
    public NamespaceContext getNamespaceContext() {
        return fNamespaceContextWrapper ;
    }
    
    /** Returns the count of namespaces declared on this START_ELEMENT or END_ELEMENT,
     * this method is only valid on a START_ELEMENT, END_ELEMENT or NAMESPACE. On
     * an END_ELEMENT the count is of the namespaces that are about to go
     * out of scope.  This is the equivalent of the information reported
     * by SAX callback for an end element event.
     * @return returns the number of namespace declarations on this specific element
     * @throws IllegalStateException if this is not a START_ELEMENT, END_ELEMENT or NAMESPACE
     */
    public int getNamespaceCount() {
        //namespaceContext is dynamic object.
        //REVISIT: check if it specifies all conditions mentioned in the javadoc
        if(fEventType == XMLEvent.START_ELEMENT || fEventType == XMLEvent.END_ELEMENT || 
                fEventType == XMLEvent.NAMESPACE){
            return fScanner.getNamespaceContext().getDeclaredPrefixCount() ;
        } else{
            throw new IllegalStateException("Current state " + getEventTypeString(fEventType)
            + " is not among the states " + getEventTypeString(XMLEvent.START_ELEMENT)
            + ", " + getEventTypeString(XMLEvent.END_ELEMENT) + ", "
                    + getEventTypeString(XMLEvent.NAMESPACE)
            + " valid for getNamespaceCount().");
        }
    }
    
    /** Returns the prefix for the namespace declared at the
     * index.  Returns null if this is the default namespace
     * declaration
     *
     * @param index the position of the namespace declaration
     * @return returns the namespace prefix
     * @throws IllegalStateException if this is not a START_ELEMENT, END_ELEMENT or NAMESPACE
     */
    public String getNamespacePrefix(int index) {
        if(fEventType == XMLEvent.START_ELEMENT || fEventType == XMLEvent.END_ELEMENT || fEventType == XMLEvent.NAMESPACE){
            //namespaceContext is dynamic object.
            String prefix = fScanner.getNamespaceContext().getDeclaredPrefixAt(index) ;
            return prefix.equals("") ? null : prefix ;
        } else{
            throw new IllegalStateException("Current state " + getEventTypeString(fEventType)
            + " is not among the states " + getEventTypeString(XMLEvent.START_ELEMENT)
            + ", " + getEventTypeString(XMLEvent.END_ELEMENT) + ", "
                    + getEventTypeString(XMLEvent.NAMESPACE)
            + " valid for getNamespacePrefix().");
        }
    }
    
    /** Returns the uri for the namespace declared at the
     * index.
     *
     * @param index the position of the namespace declaration
     * @return returns the namespace uri
     * @throws IllegalStateException if this is not a START_ELEMENT, END_ELEMENT or NAMESPACE
     */
    public String getNamespaceURI(int index) {
        if(fEventType == XMLEvent.START_ELEMENT || fEventType == XMLEvent.END_ELEMENT || fEventType == XMLEvent.NAMESPACE){
            //namespaceContext is dynamic object.
            return fScanner.getNamespaceContext().getURI(fScanner.getNamespaceContext().getDeclaredPrefixAt(index));
        } else{
            throw new IllegalStateException("Current state " + getEventTypeString(fEventType)
            + " is not among the states " + getEventTypeString(XMLEvent.START_ELEMENT)
            + ", " + getEventTypeString(XMLEvent.END_ELEMENT) + ", "
                    + getEventTypeString(XMLEvent.NAMESPACE)
            + " valid for getNamespaceURI().");
        }
        
    }
    
    /** Get the value of a feature/property from the underlying implementation
     * @param name The name of the property, may not be null
     * @return The value of the property
     * @throws IllegalArgumentException if name is null
     */
    public Object getProperty(String name) throws IllegalArgumentException {
        if(name == null) throw new IllegalArgumentException() ;
        if (fPropertyManager != null ){
            if(name.equals(fPropertyManager.STAX_NOTATIONS)){
                return getNotationDecls();
            }else if(name.equals(fPropertyManager.STAX_ENTITIES)){
                return getEntityDecls();
            }else
                return fPropertyManager.getProperty(name);
        }
        return null;
    }
    
    /** Returns the current value of the parse event as a string,
     * this returns the string value of a CHARACTERS event,
     * returns the value of a COMMENT, the replacement value
     * for an ENTITY_REFERENCE,
     * or the String value of the DTD
     * @return the current text or null
     * @throws IllegalStateException if this state is not
     * a valid text state.
     */
    public String getText() {
        if( fEventType == XMLEvent.CHARACTERS || fEventType == XMLEvent.COMMENT 
                || fEventType == XMLEvent.CDATA || fEventType == XMLEvent.SPACE){
            //this requires creation of new string
            //fEventType == XMLEvent.ENTITY_REFERENCE
            return fScanner.getCharacterData().toString() ;
        } else if(fEventType == XMLEvent.ENTITY_REFERENCE){
            String name = fScanner.getEntityName();
            if(name != null){
                if(fScanner.foundBuiltInRefs)
                    return fScanner.getCharacterData().toString();
                
                XMLEntityStorage entityStore = fEntityManager.getEntityStore();
                Hashtable ht = entityStore.getDeclaredEntities();
                Entity en = (Entity)ht.get(name);
                if(en == null)
                    return null;
                if(en.isExternal())
                    return ((Entity.ExternalEntity)en).entityLocation.getExpandedSystemId();
                else
                    return ((Entity.InternalEntity)en).text;
            }else
                return null;
        } else
            if(fEventType == XMLEvent.DTD){
                if(fDTDDecl != null){
                    return fDTDDecl;
                }
                XMLStringBuffer tmpBuffer = fScanner.getDTDDecl();
                fDTDDecl = tmpBuffer.toString();
                return fDTDDecl;
            } else{
                throw new IllegalStateException("Current state " + getEventTypeString(fEventType) 
                    + " is not among the states" + getEventTypeString(XMLEvent.CHARACTERS) + ", "
                    + getEventTypeString(XMLEvent.COMMENT) + ", "
                    + getEventTypeString(XMLEvent.CDATA) + ", "
                    + getEventTypeString(XMLEvent.SPACE) + ", "
                    + getEventTypeString(XMLEvent.ENTITY_REFERENCE) + ", "
                    + getEventTypeString(XMLEvent.DTD) + " valid for getText() " ) ;
            }
    }//getText
    
    
    /** Test if the current event is of the given type and if the namespace and name match the current namespace and name of the current event.
     * If the namespaceURI is null it is not checked for equality, if the localName is null it is not checked for equality.
     * @param type the event type
     * @param namespaceURI the uri of the event, may be null
     * @param localName the localName of the event, may be null
     * @throws XMLStreamException if the required values are not matched.
     */
    public void require(int type, String namespaceURI, String localName) throws XMLStreamException {
        if( type != fEventType)
            throw new XMLStreamException2("Event type " + getEventTypeString(type) + " specified did " +
                    "not match with current parser event " + getEventTypeString(fEventType));
        if( namespaceURI != null && !namespaceURI.equals(getNamespaceURI()) )
            throw new XMLStreamException2("Namespace URI " + namespaceURI +" specified did not match " +
                    "with current namespace URI");
        if(localName != null && !localName.equals(getLocalName()))
            throw new XMLStreamException2("LocalName " + localName +" specified did not match with " +
                    "current local name");
        
        return;
    }
    
    /** Gets the the text associated with a CHARACTERS, SPACE or CDATA event.
     * Text starting a "sourceStart" is copied into "destination" starting at "targetStart".
     * Up to "length" characters are copied.  The number of characters actually copied is returned.
     *
     * The "sourceStart" argument must be greater or equal to 0 and less than or equal to
     * the number of characters associated with the event.  Usually, one requests text starting at a "sourceStart" of 0.
     * If the number of characters actually copied is less than the "length", then there is no more text.
     * Otherwise, subsequent calls need to be made until all text has been retrieved. For example:
     *
     * <code>
     * int length = 1024;
     * char[] myBuffer = new char[ length ];
     *
     * for ( int sourceStart = 0 ; ; sourceStart += length )
     * {
     *    int nCopied = stream.getTextCharacters( sourceStart, myBuffer, 0, length );
     *
     *   if (nCopied < length)
     *       break;
     * }
     * </code>
     * XMLStreamException may be thrown if there are any XML errors in the underlying source.
     * The "targetStart" argument must be greater than or equal to 0 and less than the length of "target",
     * Length must be greater than 0 and "targetStart + length" must be less than or equal to length of "target".
     *
     * @param sourceStart the index of the first character in the source array to copy
     * @param target the destination array
     * @param targetStart the start offset in the target array
     * @param length the number of characters to copy
     * @return the number of characters actually copied
     * @throws XMLStreamException if the underlying XML source is not well-formed
     * @throws IndexOutOfBoundsException if targetStart < 0 or > than the length of target
     * @throws IndexOutOfBoundwhile(isCharacters()) ;sException if length < 0 or targetStart + length > length of target
     * @throws UnsupportedOperationException if this method is not supported
     * @throws NullPointerException is if target is null
     */
    public int getTextCharacters(int sourceStart, char[] target, int targetStart, int length) throws XMLStreamException {
        
        if(target == null){
            throw new NullPointerException("target char array can't be null") ;
        }
        
        if(targetStart < 0 || length < 0 || sourceStart < 0 || targetStart >= target.length ||
                (targetStart + length ) > target.length) {
            throw new IndexOutOfBoundsException();
        }
        
        //getTextStart() + sourceStart should not be greater than the lenght of number of characters
        //present
        int copiedLength = 0;
        //int presentDataLen = getTextLength() - (getTextStart()+sourceStart);
        int available = getTextLength() - sourceStart;
        if(available < 0){
            throw new IndexOutOfBoundsException("sourceStart is greater than" +
                    "number of characters associated with this event");
        }
        if(available < length){
            copiedLength = available;
        } else{
            copiedLength = length;
        }
        
        System.arraycopy(getTextCharacters(), getTextStart() + sourceStart , target, targetStart, copiedLength);
        return copiedLength;
    }
    
    /** Return true if the current event has text, false otherwise
     * The following events have text:
     * CHARACTERS,DTD ,ENTITY_REFERENCE, COMMENT
     */
    public boolean hasText() {
        if(DEBUG) pr("XMLReaderImpl#EVENT TYPE = " + fEventType ) ;
        if( fEventType == XMLEvent.CHARACTERS || fEventType == XMLEvent.COMMENT || fEventType == XMLEvent.CDATA) {
            return fScanner.getCharacterData().length > 0 ? true : false;
        } else if(fEventType == XMLEvent.ENTITY_REFERENCE) {
            String name = fScanner.getEntityName();
            if(name != null){
                if(fScanner.foundBuiltInRefs)
                    return true;
                
                XMLEntityStorage entityStore = fEntityManager.getEntityStore();
                Hashtable ht = entityStore.getDeclaredEntities();
                Entity en =(Entity)ht.get(name);
                if(en == null)
                    return false;
                if(en.isExternal()){
                    return ((Entity.ExternalEntity)en).entityLocation.getExpandedSystemId() != null ? true : false;
                } else{
                    return ((Entity.InternalEntity)en).text != null ? true : false ;
                }
            }else
                return false;
        } else {
            if(fEventType == XMLEvent.DTD)
                return fScanner.fSeenDoctypeDecl;
        }
        return false;
    }
    
    /** Returns a boolean which indicates if this
     * attribute was created by default
     * @param index the position of the attribute
     * @return true if this is a default attribute
     * @throws IllegalStateException if this is not a START_ELEMENT or ATTRIBUTE
     */
    public boolean isAttributeSpecified(int index) {
        //check that current state should be either START_ELEMENT or ATTRIBUTE
        if( (fEventType == XMLEvent.START_ELEMENT) || (fEventType == XMLEvent.ATTRIBUTE)){
            return fScanner.getAttributeIterator().isSpecified(index) ;
        } else{
            throw new IllegalStateException("Current state is not among the states " 
                    + getEventTypeString(XMLEvent.START_ELEMENT) + " , " 
                    + getEventTypeString(XMLEvent.ATTRIBUTE)
                    + "valid for isAttributeSpecified()")  ;
        }
    }
    
    /** Returns true if the cursor points to a character data event
     * @return true if the cursor points to character data, false otherwise
     */
    public boolean isCharacters() {
        return fEventType == XMLEvent.CHARACTERS ;
    }
    
    /** Skips any insignificant events (COMMENT and PROCESSING_INSTRUCTION)
     * until a START_ELEMENT or
     * END_ELEMENT is reached. If other than space characters are
     * encountered, an exception is thrown. This method should
     * be used when processing element-only content because
     * the parser is not able to recognize ignorable whitespace if
     * then DTD is missing or not interpreted.
     * @return the event type of the element read
     * @throws XMLStreamException if the current event is not white space
     */
    public int nextTag() throws XMLStreamException {
        
        int eventType = next();
        while((eventType == XMLStreamConstants.CHARACTERS && isWhiteSpace()) // skip whitespace
        || (eventType == XMLStreamConstants.CDATA && isWhiteSpace())
        // skip whitespace
        || eventType == XMLStreamConstants.SPACE
                || eventType == XMLStreamConstants.PROCESSING_INSTRUCTION
                || eventType == XMLStreamConstants.COMMENT
                ) {
            eventType = next();
        }
        if (eventType != XMLStreamConstants.START_ELEMENT && eventType != XMLStreamConstants.END_ELEMENT) {
            throw new XMLStreamException2("expected start or end tag", getLocation());
        }
        return eventType;
        
    }
    
    /** Checks if standalone was set in the document
     * @return true if standalone was set in the document, or false otherwise
     */
    public boolean standaloneSet() {
        return fScanner.standaloneSet() ;
    }
    
    /**
     * @param qname
     * @return
     */
    public QName convertXNIQNametoJavaxQName(com.sun.xml.stream.xerces.xni.QName qname){
        if (qname == null) return null;
        //xxx: prefix definition ?
        if(qname.prefix == null){
            return new QName(qname.uri, qname.localpart) ;
        } else{
            return new QName(qname.uri, qname.localpart, qname.prefix) ;
        }
    }
    
    /** Return the uri for the given prefix.
     * The uri returned depends on the current state of the processor.
     *
     * <p><strong>NOTE:</strong>The 'xml' prefix is bound as defined in
     * <a href="http://www.w3.org/TR/REC-xml-names/#ns-using">Namespaces in XML</a>
     * specification to "http://www.w3.org/XML/1998/namespace".
     *
     * <p><strong>NOTE:</strong> The 'xmlns' prefix must be resolved to following namespace
     * <a href="http://www.w3.org/2000/xmlns/">http://www.w3.org/2000/xmlns/</a>
     * @return the uri bound to the given prefix or null if it is not bound
     * @param prefix The prefix to lookup, may not be null
     * @throws IllegalStateException if the prefix is null
     */
    public String getNamespaceURI(String prefix) {
        if(prefix == null) throw new IllegalArgumentException("getNamespaceURI(String prefix) is called with a null prefix.") ;

        //first add the string to symbol table.. since internally identity comparisons are done.
        return fScanner.getNamespaceContext().getURI(fSymbolTable.addSymbol(prefix)) ;
    }
    
    //xxx: this function is not being used.
    protected void setPropertyManager(PropertyManager propertyManager){
        fPropertyManager = propertyManager ;
        //REVISIT: we were supplying hashmap ealier
        fScanner.setProperty("stax-properties",propertyManager);
        fScanner.setPropertyManager(propertyManager) ;
    }
    
    /**
     * @return returns the reference to property manager.
     */
    protected PropertyManager getPropertyManager(){
        return fPropertyManager ;
    }
    
    static void pr(String str) {
        System.out.println(str) ;
    }
    
    protected List getEntityDecls(){
        if(fEventType == XMLStreamConstants.DTD){
            XMLEntityStorage entityStore = fEntityManager.getEntityStore();
            Hashtable ht = entityStore.getDeclaredEntities();
            ArrayList list = null;
            if(ht != null){
                EntityDeclarationImpl decl = null;
                list = new ArrayList(ht.size());
                Enumeration enu = ht.keys();
                while(enu.hasMoreElements()){
                    String key = (String)enu.nextElement();
                    Entity en = (Entity)ht.get(key);
                    decl = new EntityDeclarationImpl();
                    decl.setEntityName(key);
                    if(en.isExternal()){
                        decl.setXMLResourceIdentifier(((Entity.ExternalEntity)en).entityLocation);
                        decl.setNotationName(((Entity.ExternalEntity)en).notation);
                    } else
                        decl.setEntityReplacementText(((Entity.InternalEntity)en).text);
                    list.add(decl);
                }
            }
            return list;
        }
        return null;
    }
    
    protected List getNotationDecls(){
        if(fEventType == XMLStreamConstants.DTD){
            if(fScanner.fDTDScanner == null) return null;
            DTDGrammar grammar = ((XMLDTDScannerImpl)(fScanner.fDTDScanner)).getGrammar();
            if(grammar == null) return null;
            List notations = grammar.getNotationDecls();
            
            Iterator it = notations.iterator();
            ArrayList list = new ArrayList();
            while(it.hasNext()){
                XMLNotationDecl ni = (XMLNotationDecl)it.next();
                if(ni!= null){
                    list.add(new NotationDeclarationImpl(ni));
                }
            }
            return list;
        }
        return null;
    }
    
    
    
}//XMLReaderImpl
