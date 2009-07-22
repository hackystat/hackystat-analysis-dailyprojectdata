//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.0.5-b02-fcs 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2009.07.19 at 11:49:25 PM HST 
//


package org.hackystat.dailyprojectdata.resource.issue.jaxb;

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
 *         &lt;element ref="{}IssueData" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute ref="{}OpenIssues use="required""/>
 *       &lt;attribute ref="{}Owner use="required""/>
 *       &lt;attribute ref="{}Project use="required""/>
 *       &lt;attribute ref="{}StartTime use="required""/>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "issueData"
})
@XmlRootElement(name = "IssueDailyProjectData")
public class IssueDailyProjectData
    implements Serializable
{

    private final static long serialVersionUID = 20090719L;
    @XmlElement(name = "IssueData")
    protected List<IssueData> issueData;
    @XmlAttribute(name = "OpenIssues", required = true)
    protected int openIssues;
    @XmlAttribute(name = "Owner", required = true)
    protected String owner;
    @XmlAttribute(name = "Project", required = true)
    protected String project;
    @XmlAttribute(name = "StartTime", required = true)
    protected XMLGregorianCalendar startTime;

    /**
     * Gets the value of the issueData property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the issueData property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getIssueData().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link IssueData }
     * 
     * 
     */
    public List<IssueData> getIssueData() {
        if (issueData == null) {
            issueData = new ArrayList<IssueData>();
        }
        return this.issueData;
    }

    public boolean isSetIssueData() {
        return ((this.issueData!= null)&&(!this.issueData.isEmpty()));
    }

    public void unsetIssueData() {
        this.issueData = null;
    }

    /**
     * Gets the value of the openIssues property.
     * 
     */
    public int getOpenIssues() {
        return openIssues;
    }

    /**
     * Sets the value of the openIssues property.
     * 
     */
    public void setOpenIssues(int value) {
        this.openIssues = value;
    }

    public boolean isSetOpenIssues() {
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