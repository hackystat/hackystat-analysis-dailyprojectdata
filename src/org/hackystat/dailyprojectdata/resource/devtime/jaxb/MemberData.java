//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.1.5-b01-fcs 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2008.01.02 at 09:46:49 AM HST 
//


package org.hackystat.dailyprojectdata.resource.devtime.jaxb;

import java.math.BigInteger;
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
 *       &lt;attribute ref="{}MemberUri use="required""/>
 *       &lt;attribute ref="{}DevTime use="required""/>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
@XmlRootElement(name = "MemberData")
public class MemberData {

    @XmlAttribute(name = "MemberUri", required = true)
    @XmlSchemaType(name = "anyURI")
    protected String memberUri;
    @XmlAttribute(name = "DevTime", required = true)
    protected BigInteger devTime;

    /**
     * Gets the value of the memberUri property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMemberUri() {
        return memberUri;
    }

    /**
     * Sets the value of the memberUri property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMemberUri(String value) {
        this.memberUri = value;
    }

    /**
     * Gets the value of the devTime property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getDevTime() {
        return devTime;
    }

    /**
     * Sets the value of the devTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setDevTime(BigInteger value) {
        this.devTime = value;
    }

}
