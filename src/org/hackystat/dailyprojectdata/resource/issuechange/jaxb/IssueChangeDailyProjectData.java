//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.0.5-b02-fcs 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2009.10.30 at 02:01:18 PM HST 
//


package org.hackystat.dailyprojectdata.resource.issuechange.jaxb;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
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
 *         &lt;element ref="{}IssueChangeData" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="Closed" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="Opened" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="Owner" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="Project" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="Reopened" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="StartTime" use="required" type="{http://www.w3.org/2001/XMLSchema}dateTime" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "issueChangeData"
})
@XmlRootElement(name = "IssueChangeDailyProjectData")
public class IssueChangeDailyProjectData
    implements Serializable
{

    private final static long serialVersionUID = 20091030L;
    @XmlElement(name = "IssueChangeData")
    protected List<IssueChangeData> issueChangeData;
    @XmlAttribute(name = "Closed", required = true)
    protected int closed;
    @XmlAttribute(name = "Opened", required = true)
    protected int opened;
    @XmlAttribute(name = "Owner", required = true)
    protected String owner;
    @XmlAttribute(name = "Project", required = true)
    protected String project;
    @XmlAttribute(name = "Reopened", required = true)
    protected int reopened;
    @XmlAttribute(name = "StartTime", required = true)
    protected XMLGregorianCalendar startTime;

    /**
     * Gets the value of the issueChangeData property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the issueChangeData property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getIssueChangeData().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link IssueChangeData }
     * 
     * 
     */
    public List<IssueChangeData> getIssueChangeData() {
        if (issueChangeData == null) {
            issueChangeData = new ArrayList<IssueChangeData>();
        }
        return this.issueChangeData;
    }

    public boolean isSetIssueChangeData() {
        return ((this.issueChangeData!= null)&&(!this.issueChangeData.isEmpty()));
    }

    public void unsetIssueChangeData() {
        this.issueChangeData = null;
    }

    /**
     * Gets the value of the closed property.
     * 
     */
    public int getClosed() {
        return closed;
    }

    /**
     * Sets the value of the closed property.
     * 
     */
    public void setClosed(int value) {
        this.closed = value;
    }

    public boolean isSetClosed() {
        return true;
    }

    /**
     * Gets the value of the opened property.
     * 
     */
    public int getOpened() {
        return opened;
    }

    /**
     * Sets the value of the opened property.
     * 
     */
    public void setOpened(int value) {
        this.opened = value;
    }

    public boolean isSetOpened() {
        return true;
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
     * Gets the value of the reopened property.
     * 
     */
    public int getReopened() {
        return reopened;
    }

    /**
     * Sets the value of the reopened property.
     * 
     */
    public void setReopened(int value) {
        this.reopened = value;
    }

    public boolean isSetReopened() {
        return true;
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

}
