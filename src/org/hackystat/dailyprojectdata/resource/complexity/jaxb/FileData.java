//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.1.5-b01-fcs 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2008.03.24 at 12:05:54 PM HST 
//


package org.hackystat.dailyprojectdata.resource.complexity.jaxb;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute ref="{}FileUri use="required""/>
 *       &lt;attribute ref="{}ComplexityValues use="required""/>
 *       &lt;attribute ref="{}TotalLines use="required""/>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
@XmlRootElement(name = "FileData")
public class FileData
    implements Serializable
{

    private final static long serialVersionUID = 12343L;
    @XmlAttribute(name = "FileUri", required = true)
    @XmlSchemaType(name = "anyURI")
    protected String fileUri;
    @XmlAttribute(name = "ComplexityValues", required = true)
    protected String complexityValues;
    @XmlAttribute(name = "TotalLines", required = true)
    protected String totalLines;

    /**
     * Gets the value of the fileUri property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFileUri() {
        return fileUri;
    }

    /**
     * Sets the value of the fileUri property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFileUri(String value) {
        this.fileUri = value;
    }

    public boolean isSetFileUri() {
        return (this.fileUri!= null);
    }

    /**
     * Gets the value of the complexityValues property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getComplexityValues() {
        return complexityValues;
    }

    /**
     * Sets the value of the complexityValues property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setComplexityValues(String value) {
        this.complexityValues = value;
    }

    public boolean isSetComplexityValues() {
        return (this.complexityValues!= null);
    }

    /**
     * Gets the value of the totalLines property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTotalLines() {
        return totalLines;
    }

    /**
     * Sets the value of the totalLines property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTotalLines(String value) {
        this.totalLines = value;
    }

    public boolean isSetTotalLines() {
        return (this.totalLines!= null);
    }

}