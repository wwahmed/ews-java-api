/*
 * $Id: XMLEntityStorage.java,v 1.5 2007-07-19 22:33:12 ofung Exp $
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

import java.util.Hashtable;

import com.sun.xml.stream.xerces.impl.msg.XMLMessageFormatter;
import com.sun.xml.stream.xerces.util.URI;
import com.sun.xml.stream.xerces.util.XMLResourceIdentifierImpl;
import com.sun.xml.stream.xerces.xni.parser.XMLComponentManager;
import com.sun.xml.stream.xerces.xni.parser.XMLConfigurationException;

/**
 *
 * @author K.Venugopal SUN Microsystems
 * @author Neeraj Bajaj SUN Microsystems
 * @author Andy Clark, IBM
 *
 */
public class XMLEntityStorage {
    
    /** Property identifier: error reporter. */
    protected static final String ERROR_REPORTER =
    Constants.XERCES_PROPERTY_PREFIX + Constants.ERROR_REPORTER_PROPERTY;
    
    /** Feature identifier: warn on duplicate EntityDef */
    protected static final String WARN_ON_DUPLICATE_ENTITYDEF =
    Constants.XERCES_FEATURE_PREFIX +Constants.WARN_ON_DUPLICATE_ENTITYDEF_FEATURE;
    
    /** warn on duplicate Entity declaration.
     *  http://apache.org/xml/features/warn-on-duplicate-entitydef
     */
    protected boolean fWarnDuplicateEntityDef;
    
    /** Entities. */
    protected Hashtable fEntities = new Hashtable();
    
    protected Entity.ScannedEntity fCurrentEntity ;
    
    private XMLEntityManager fEntityManager;
    /**
     * Error reporter. This property identifier is:
     * http://apache.org/xml/properties/internal/error-reporter
     */
    protected XMLErrorReporter fErrorReporter;
    protected PropertyManager fPropertyManager ;
    
    /** Creates a new instance of XMLEntityStorage */
    public XMLEntityStorage(PropertyManager propertyManager) {
        fPropertyManager = propertyManager ;
    }
    
    /** Creates a new instance of XMLEntityStorage */
    /*public XMLEntityStorage(Entity.ScannedEntity currentEntity) {
        fCurrentEntity = currentEntity ;*/
    public XMLEntityStorage(XMLEntityManager entityManager) {
        fEntityManager = entityManager;
    }
    
    public void reset(PropertyManager propertyManager){
        
        fErrorReporter = (XMLErrorReporter)propertyManager.getProperty(Constants.XERCES_PROPERTY_PREFIX + Constants.ERROR_REPORTER_PROPERTY);
        fEntities.clear();
        fCurrentEntity = null;
        
    }
    
    public void reset(){
        fEntities.clear();
        fCurrentEntity = null;
    }
    /**
     * Resets the component. The component can query the component manager
     * about any features and properties that affect the operation of the
     * component.
     *
     * @param componentManager The component manager.
     *
     * @throws SAXException Thrown by component on initialization error.
     *                      For example, if a feature or property is
     *                      required for the operation of the component, the
     *                      component manager may throw a
     *                      SAXNotRecognizedException or a
     *                      SAXNotSupportedException.
     */
    public void reset(XMLComponentManager componentManager)
    throws XMLConfigurationException {
        
        
        // xerces features
        
        try {
            fWarnDuplicateEntityDef = componentManager.getFeature(WARN_ON_DUPLICATE_ENTITYDEF);
        }
        catch (XMLConfigurationException e) {
            fWarnDuplicateEntityDef = false;
        }
        
        fErrorReporter = (XMLErrorReporter)componentManager.getProperty(ERROR_REPORTER);
        
        fEntities.clear();
        fCurrentEntity = null;
        
    } // reset(XMLComponentManager)
    
    /**
     * Returns the hashtable of declared entities.
     * <p>
     * <strong>REVISIT:</strong>
     * This should be done the "right" way by designing a better way to
     * enumerate the declared entities. For now, this method is needed
     * by the constructor that takes an XMLEntityManager parameter.
     * XXX Making this method public, return all the declared entities.
     * @return Hashtable hastable containing all the declared entities.
     */
    public Hashtable getDeclaredEntities() {
        return fEntities;
    } // getDeclaredEntities():Hashtable
    
    /**
     * Adds an internal entity declaration.
     * <p>
     * <strong>Note:</strong> This method ignores subsequent entity
     * declarations.
     * <p>
     * <strong>Note:</strong> The name should be a unique symbol. The
     * SymbolTable can be used for this purpose.
     *
     * @param name The name of the entity.
     * @param text The text of the entity.
     *
     * @see SymbolTable
     */
    public void addInternalEntity(String name, String text) {
        if (!fEntities.containsKey(name)) {
            //some times we need information, if the current entity is part of external subset..
            fCurrentEntity = fEntityManager.getCurrentEntity();
            Entity entity = new Entity.InternalEntity(name, text, false);
            //(fCurrentEntity == null) ? fasle : fCurrentEntity.isEntityDeclInExternalSubset());
            fEntities.put(name, entity);
        }
        else{
            if(fWarnDuplicateEntityDef){
                fErrorReporter.reportError(XMLMessageFormatter.XML_DOMAIN,
                "MSG_DUPLICATE_ENTITY_DEFINITION",
                new Object[]{ name },
                XMLErrorReporter.SEVERITY_WARNING );
            }
        }
        
    } // addInternalEntity(String,String)
    
    /**
     * Adds an external entity declaration.
     * <p>
     * <strong>Note:</strong> This method ignores subsequent entity
     * declarations.
     * <p>
     * <strong>Note:</strong> The name should be a unique symbol. The
     * SymbolTable can be used for this purpose.
     *
     * @param name         The name of the entity.
     * @param publicId     The public identifier of the entity.
     * @param literalSystemId     The system identifier of the entity.
     * @param baseSystemId The base system identifier of the entity.
     *                     This is the system identifier of the entity
     *                     where <em>the entity being added</em> and
     *                     is used to expand the system identifier when
     *                     the system identifier is a relative URI.
     *                     When null the system identifier of the first
     *                     external entity on the stack is used instead.
     *
     * @see SymbolTable
     */
    public void addExternalEntity(String name,
    String publicId, String literalSystemId,
    String baseSystemId) {
        if (!fEntities.containsKey(name)) {
            if (baseSystemId == null) {
                // search for the first external entity on the stack
                //xxx commenting the 'size' variable..
                /**
                 * int size = fEntityStack.size();
                 * if (size == 0 && fCurrentEntity != null && fCurrentEntity.entityLocation != null) {
                 * baseSystemId = fCurrentEntity.entityLocation.getExpandedSystemId();
                 * }
                 */
                
                //xxx we need to have information about the current entity.
                if (fCurrentEntity != null && fCurrentEntity.entityLocation != null) {
                    baseSystemId = fCurrentEntity.entityLocation.getExpandedSystemId();
                }
                /**
                 * for (int i = size - 1; i >= 0 ; i--) {
                 * ScannedEntity externalEntity =
                 * (ScannedEntity)fEntityStack.elementAt(i);
                 * if (externalEntity.entityLocation != null && externalEntity.entityLocation.getExpandedSystemId() != null) {
                 * baseSystemId = externalEntity.entityLocation.getExpandedSystemId();
                 * break;
                 * }
                 * }
                 */
            }
            
            fCurrentEntity = fEntityManager.getCurrentEntity();
            Entity entity = new Entity.ExternalEntity(name,
            new XMLResourceIdentifierImpl(publicId, literalSystemId,
            baseSystemId, expandSystemId(literalSystemId, baseSystemId)),
            null,true);
            //TODO :: Forced to pass true above remove it.
            //(fCurrentEntity == null) ? fasle : fCurrentEntity.isEntityDeclInExternalSubset());
            //					null, fCurrentEntity.isEntityDeclInExternalSubset());
            fEntities.put(name, entity);
        }
        else{
            if(fWarnDuplicateEntityDef){
                fErrorReporter.reportError(XMLMessageFormatter.XML_DOMAIN,
                "MSG_DUPLICATE_ENTITY_DEFINITION",
                new Object[]{ name },
                XMLErrorReporter.SEVERITY_WARNING );
            }
        }
        
    } // addExternalEntity(String,String,String,String)
    
    /**
     * Checks whether an entity given by name is external.
     *
     * @param entityName The name of the entity to check.
     * @returns True if the entity is external, false otherwise
     *           (including when the entity is not declared).
     */
    public boolean isExternalEntity(String entityName) {
        
        Entity entity = (Entity)fEntities.get(entityName);
        if (entity == null) {
            return false;
        }
        return entity.isExternal();
    }
    
    /**
     * Checks whether the declaration of an entity given by name is
     * // in the external subset.
     *
     * @param entityName The name of the entity to check.
     * @returns True if the entity was declared in the external subset, false otherwise
     *           (including when the entity is not declared).
     */
    public boolean isEntityDeclInExternalSubset(String entityName) {
        
        Entity entity = (Entity)fEntities.get(entityName);
        if (entity == null) {
            return false;
        }
        return entity.isEntityDeclInExternalSubset();
    }
    
    /**
     * Adds an unparsed entity declaration.
     * <p>
     * <strong>Note:</strong> This method ignores subsequent entity
     * declarations.
     * <p>
     * <strong>Note:</strong> The name should be a unique symbol. The
     * SymbolTable can be used for this purpose.
     *
     * @param name     The name of the entity.
     * @param publicId The public identifier of the entity.
     * @param systemId The system identifier of the entity.
     * @param notation The name of the notation.
     *
     * @see SymbolTable
     */
    public void addUnparsedEntity(String name,
    String publicId, String systemId,
    String baseSystemId, String notation) {
        
        fCurrentEntity = fEntityManager.getCurrentEntity();
        if (!fEntities.containsKey(name)) {
            Entity entity = new Entity.ExternalEntity(name, new XMLResourceIdentifierImpl(publicId, systemId, baseSystemId, null), notation,false);
            //			(fCurrentEntity == null) ? fasle : fCurrentEntity.isEntityDeclInExternalSubset());
            //			fCurrentEntity.isEntityDeclInExternalSubset());
            fEntities.put(name, entity);
        }
        else{
            if(fWarnDuplicateEntityDef){
                fErrorReporter.reportError(XMLMessageFormatter.XML_DOMAIN,
                "MSG_DUPLICATE_ENTITY_DEFINITION",
                new Object[]{ name },
                XMLErrorReporter.SEVERITY_WARNING );
            }
        }
    } // addUnparsedEntity(String,String,String,String)
    
    /**
     * Checks whether an entity given by name is unparsed.
     *
     * @param entityName The name of the entity to check.
     * @returns True if the entity is unparsed, false otherwise
     *          (including when the entity is not declared).
     */
    public boolean isUnparsedEntity(String entityName) {
        
        Entity entity = (Entity)fEntities.get(entityName);
        if (entity == null) {
            return false;
        }
        return entity.isUnparsed();
    }
    
    /**
     * Checks whether an entity given by name is declared.
     *
     * @param entityName The name of the entity to check.
     * @returns True if the entity is declared, false otherwise.
     */
    public boolean isDeclaredEntity(String entityName) {
        
        Entity entity = (Entity)fEntities.get(entityName);
        return entity != null;
    }
    /**
     * Expands a system id and returns the system id as a URI, if
     * it can be expanded. A return value of null means that the
     * identifier is already expanded. An exception thrown
     * indicates a failure to expand the id.
     *
     * @param systemId The systemId to be expanded.
     *
     * @return Returns the URI string representing the expanded system
     *         identifier. A null value indicates that the given
     *         system identifier is already expanded.
     *
     */
    public static String expandSystemId(String systemId) {
        return expandSystemId(systemId, null);
    } // expandSystemId(String):String
    
    // current value of the "user.dir" property
    private static String gUserDir;
    // escaped value of the current "user.dir" property
    private static String gEscapedUserDir;
    // which ASCII characters need to be escaped
    private static boolean gNeedEscaping[] = new boolean[128];
    // the first hex character if a character needs to be escaped
    private static char gAfterEscaping1[] = new char[128];
    // the second hex character if a character needs to be escaped
    private static char gAfterEscaping2[] = new char[128];
    private static char[] gHexChs = {'0', '1', '2', '3', '4', '5', '6', '7',
    '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
    // initialize the above 3 arrays
    static {
        for (int i = 0; i <= 0x1f; i++) {
            gNeedEscaping[i] = true;
            gAfterEscaping1[i] = gHexChs[i >> 4];
            gAfterEscaping2[i] = gHexChs[i & 0xf];
        }
        gNeedEscaping[0x7f] = true;
        gAfterEscaping1[0x7f] = '7';
        gAfterEscaping2[0x7f] = 'F';
        char[] escChs = {' ', '<', '>', '#', '%', '"', '{', '}',
        '|', '\\', '^', '~', '[', ']', '`'};
        int len = escChs.length;
        char ch;
        for (int i = 0; i < len; i++) {
            ch = escChs[i];
            gNeedEscaping[ch] = true;
            gAfterEscaping1[ch] = gHexChs[ch >> 4];
            gAfterEscaping2[ch] = gHexChs[ch & 0xf];
        }
    }
    // To escape the "user.dir" system property, by using %HH to represent
    // special ASCII characters: 0x00~0x1F, 0x7F, ' ', '<', '>', '#', '%'
    // and '"'. It's a static method, so needs to be synchronized.
    // this method looks heavy, but since the system property isn't expected
    // to change often, so in most cases, we only need to return the string
    // that was escaped before.
    // According to the URI spec, non-ASCII characters (whose value >= 128)
    // need to be escaped too.
    // REVISIT: don't know how to escape non-ASCII characters, especially
    // which encoding to use. Leave them for now.
    private static synchronized String getUserDir() {
        // get the user.dir property
        String userDir = "";
        try {
            userDir = System.getProperty("user.dir");
        }
        catch (SecurityException se) {
        }
        
        // return empty string if property value is empty string.
        if (userDir.length() == 0)
            return "";
        
        // compute the new escaped value if the new property value doesn't
        // match the previous one
        if (userDir.equals(gUserDir)) {
            return gEscapedUserDir;
        }
        
        // record the new value as the global property value
        gUserDir = userDir;
        
        char separator = java.io.File.separatorChar;
        userDir = userDir.replace(separator, '/');
        
        int len = userDir.length(), ch;
        StringBuffer buffer = new StringBuffer(len*3);
        // change C:/blah to /C:/blah
        if (len >= 2 && userDir.charAt(1) == ':') {
            ch = Character.toUpperCase(userDir.charAt(0));
            if (ch >= 'A' && ch <= 'Z') {
                buffer.append('/');
            }
        }
        
        // for each character in the path
        int i = 0;
        for (; i < len; i++) {
            ch = userDir.charAt(i);
            // if it's not an ASCII character, break here, and use UTF-8 encoding
            if (ch >= 128)
                break;
            if (gNeedEscaping[ch]) {
                buffer.append('%');
                buffer.append(gAfterEscaping1[ch]);
                buffer.append(gAfterEscaping2[ch]);
                // record the fact that it's escaped
            }
            else {
                buffer.append((char)ch);
            }
        }
        
        // we saw some non-ascii character
        if (i < len) {
            // get UTF-8 bytes for the remaining sub-string
            byte[] bytes = null;
            byte b;
            try {
                bytes = userDir.substring(i).getBytes("UTF-8");
            } catch (java.io.UnsupportedEncodingException e) {
                // should never happen
                return userDir;
            }
            len = bytes.length;
            
            // for each byte
            for (i = 0; i < len; i++) {
                b = bytes[i];
                // for non-ascii character: make it positive, then escape
                if (b < 0) {
                    ch = b + 256;
                    buffer.append('%');
                    buffer.append(gHexChs[ch >> 4]);
                    buffer.append(gHexChs[ch & 0xf]);
                }
                else if (gNeedEscaping[b]) {
                    buffer.append('%');
                    buffer.append(gAfterEscaping1[b]);
                    buffer.append(gAfterEscaping2[b]);
                }
                else {
                    buffer.append((char)b);
                }
            }
        }
        
        // change blah/blah to blah/blah/
        if (!userDir.endsWith("/"))
            buffer.append('/');
        
        gEscapedUserDir = buffer.toString();
        
        return gEscapedUserDir;
    }
    
    /**
     * Expands a system id and returns the system id as a URI, if
     * it can be expanded. A return value of null means that the
     * identifier is already expanded. An exception thrown
     * indicates a failure to expand the id.
     *
     * @param systemId The systemId to be expanded.
     *
     * @return Returns the URI string representing the expanded system
     *         identifier. A null value indicates that the given
     *         system identifier is already expanded.
     *
     */
    public static String expandSystemId(String systemId, String baseSystemId) {
        
        // check for bad parameters id
        if (systemId == null || systemId.length() == 0) {
            return systemId;
        }
        // if id already expanded, return
        try {
            URI uri = new URI(systemId);
            if (uri != null) {
                return systemId;
            }
        }
        catch (URI.MalformedURIException e) {
            // continue on...
        }
        // normalize id
        String id = fixURI(systemId);
        
        // normalize base
        URI base = null;
        URI uri = null;
        try {
            if (baseSystemId == null || baseSystemId.length() == 0 ||
            baseSystemId.equals(systemId)) {
                String dir = getUserDir();
                base = new URI("file", "", dir, null, null);
            }
            else {
                try {
                    base = new URI(fixURI(baseSystemId));
                }
                catch (URI.MalformedURIException e) {
                    if (baseSystemId.indexOf(':') != -1) {
                        // for xml schemas we might have baseURI with
                        // a specified drive
                        base = new URI("file", "", fixURI(baseSystemId), null, null);
                    }
                    else {
                        String dir = getUserDir();
                        dir = dir + fixURI(baseSystemId);
                        base = new URI("file", "", dir, null, null);
                    }
                }
            }
            // expand id
            uri = new URI(base, id);
        }
        catch (Exception e) {
            // let it go through
            
        }
        
        if (uri == null) {
            return systemId;
        }
        return uri.toString();
        
    } // expandSystemId(String,String):String
    //
    // Protected static methods
    //
    
    /**
     * Fixes a platform dependent filename to standard URI form.
     *
     * @param str The string to fix.
     *
     * @return Returns the fixed URI string.
     */
    protected static String fixURI(String str) {
        
        // handle platform dependent strings
        str = str.replace(java.io.File.separatorChar, '/');
        
        // Windows fix
        if (str.length() >= 2) {
            char ch1 = str.charAt(1);
            // change "C:blah" to "/C:blah"
            if (ch1 == ':') {
                char ch0 = Character.toUpperCase(str.charAt(0));
                if (ch0 >= 'A' && ch0 <= 'Z') {
                    str = "/" + str;
                }
            }
            // change "//blah" to "file://blah"
            else if (ch1 == '/' && str.charAt(0) == '/') {
                str = "file:" + str;
            }
        }
        
        // done
        return str;
        
    } // fixURI(String):String
    
}
