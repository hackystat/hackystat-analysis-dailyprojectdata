//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.1.5-b01-fcs 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2008.07.28 at 12:01:56 PM GMT-10:00 
//


package org.hackystat.dailyprojectdata.resource.coverage.jaxb;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{}ConstructData" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute ref="{}Project"/>
 *       &lt;attribute ref="{}Owner"/>
 *       &lt;attribute ref="{}StartTime"/>
 *       &lt;attribute ref="{}Granularity"/>
 *       &lt;attribute ref="{}Tool"/>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "constructData"
})
@XmlRootElement(name = "CoverageDailyProjectData")
public class CoverageDailyProjectData
    implements Serializable
{

    private final static long serialVersionUID = 12343L;
    @XmlElement(name = "ConstructData")
    protected List<ConstructData> constructData;
    @XmlAttribute(name = "Project")
    protected String project;
    @XmlAttribute(name = "Owner")
    protected String owner;
    @XmlAttribute(name = "StartTime")
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar startTime;
    @XmlAttribute(name = "Granularity")
    protected String granularity;
    @XmlAttribute(name = "Tool")
    protected String tool;

    /**
     * Gets the value of the constructData property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the constructData property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getConstructData().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ConstructData }
     * 
     * 
     */
    public List<ConstructData> getConstructData() {
        if (constructData == null) {
            constructData = new ArrayList<ConstructData>();
        }
        return this.constructData;
    }

    public boolean isSetConstructData() {
        return ((this.constructData!= null)&&(!this.constructData.isEmpty()));
    }

    public void unsetConstructData() {
        this.constructData = null;
    }

    /**
     * Gets the value of the project property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getProject() {
        return project;
    }

    /**
     * Sets the value of the project property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setProject(String value) {
        this.project = value;
    }

    public boolean isSetProject() {
        return (this.project!= null);
    }

    /**
     * Gets the value of the owner property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOwner() {
        return owner;
    }

    /**
     * Sets the value of the owner property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOwner(String value) {
        this.owner = value;
    }

    public boolean isSetOwner() {
        return (this.owner!= null);
    }

    /**
     * Gets the value of the startTime property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getStartTime() {
        return startTime;
    }

    /**
     * Sets the value of the startTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setStartTime(XMLGregorianCalendar value) {
        this.startTime = value;
    }

    public boolean isSetStartTime() {
        return (this.startTime!= null);
    }

    /**
     * Gets the value of the granularity property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGranularity() {
        return granularity;
    }

    /**
     * Sets the value of the granularity property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGranularity(String value) {
        this.granularity = value;
    }

    public boolean isSetGranularity() {
        return (this.granularity!= null);
    }

    /**
     * Gets the value of the tool property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTool() {
        return tool;
    }

    /**
     * Sets the value of the tool property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTool(String value) {
        this.tool = value;
    }

    public boolean isSetTool() {
        return (this.tool!= null);
    }

}
