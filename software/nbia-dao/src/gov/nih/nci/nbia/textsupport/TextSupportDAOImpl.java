/*L
 *  Copyright SAIC, Ellumen and RSNA (CTP)
 *
 *
 *  Distributed under the OSI-approved BSD 3-Clause License.
 *  See http://ncip.github.com/national-biomedical-image-archive/LICENSE.txt for details.
 */

package gov.nih.nci.nbia.textsupport;

import gov.nih.nci.nbia.dao.AbstractDAO;
import gov.nih.nci.nbia.internaldomain.CTImage;
import gov.nih.nci.nbia.internaldomain.GeneralEquipment;
import gov.nih.nci.nbia.internaldomain.GeneralImage;
import gov.nih.nci.nbia.internaldomain.GeneralSeries;
import gov.nih.nci.nbia.internaldomain.MRImage;
import gov.nih.nci.nbia.internaldomain.Patient;
import gov.nih.nci.nbia.internaldomain.Study;
import gov.nih.nci.nbia.internaldomain.TrialDataProvenance;
import gov.nih.nci.nbia.util.SpringApplicationContext;

import java.io.File;
import java.util.*;

import org.apache.log4j.Logger;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;



public class TextSupportDAOImpl extends AbstractDAO
                               implements TextSupportDAO 
{
	static Logger log = Logger.getLogger(TextSupportDAOImpl.class);
    private final static String PATIENT_QUERY="select distinct patient_id from submission_history where submission_timestamp between :low and :high";
    private final static String PATIENT_CATEGORY_QUERY="select distinct patient_id from patient where patient_pk_id in (select patient_pk_id from trial_data_provenance where project=:project)";
    private final static String MAX_TIME_QUERY ="select max(submission_timestamp) from submission_history";
@Transactional(propagation=Propagation.REQUIRED)
public String getMaxTimeStamp()
{
	String returnValue=null;
	
	try {
		List<Object> rs = this.getHibernateTemplate().getSessionFactory().getCurrentSession().createSQLQuery(MAX_TIME_QUERY).list();
		if (rs==null || rs.size()<1) return returnValue; //nothing to do
		returnValue=rs.get(0).toString();
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	
	return returnValue;
}
@Transactional(propagation=Propagation.REQUIRED)
public List<Object> getUpdatedPatients(String high, String low)
{
	List<Object> returnValue = new ArrayList<Object>();
	
	try {
		returnValue= this.getHibernateTemplate().getSessionFactory().getCurrentSession().createSQLQuery(PATIENT_QUERY)
		  .setParameter("low", low)
		  .setParameter("high", high).list();
		if (returnValue.size()==0) {
			   log.error("No new items in submission log");
			   return returnValue; //nothing to do
		   }
	} catch (HibernateException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	
	return returnValue;
}
@Transactional(propagation=Propagation.REQUIRED)
public List<Object> getPatientsForCollection(String collection)
{
	List<Object> returnValue = new ArrayList<Object>();
	
	try {
		returnValue= this.getHibernateTemplate().getSessionFactory().getCurrentSession().createSQLQuery(PATIENT_CATEGORY_QUERY)
    	  .setParameter("project", collection).list();
		if (returnValue.size()==0) {
			   log.error("No new items in submission log");
			   return returnValue; //nothing to do
		   }
	} catch (HibernateException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	
	return returnValue;
}
@Transactional(propagation=Propagation.REQUIRED)
public List<Object> getPatients(String patientId)
{
List<Object> returnValue = new ArrayList<Object>();

try {
	returnValue = getHibernateTemplate().getSessionFactory().getCurrentSession()
    .createQuery("from Patient as patient where patient.patientId =?")
    .setParameter(0, patientId)
    .list();
	if (returnValue.size()==0) return returnValue;
	Patient patient=(Patient)returnValue.get(0);
	// seems in order to deal with lazy loading it is best to iterate through the object graph 
	// and access each method before returning.  Must be an issue with the transaction manager,
	// but this fixes it for now.
	TrialDataProvenance trialDP = patient.getDataProvenance();
	if (trialDP!=null)
	{
	  trialDP.getDpSiteId();
      trialDP.getDpSiteName();
	  trialDP.getProject();
	}
	if (patient.getStudyCollection()!=null)
	{
	  for (Study study : patient.getStudyCollection())
	  {
		study.getId();
		study.getAdmittingDiagnosesCodeSeq();
		study.getAdmittingDiagnosesDesc();
		study.getStudyDate();
		study.getStudyDesc();
		study.getStudyId();
		study.getStudyTime();
		study.getTimePointDesc();
		study.getTimePointId();
		study.getAgeGroup();
		study.getOccupation();
		if (study.getGeneralSeriesCollection()!=null)
		{
			for (GeneralSeries series : study.getGeneralSeriesCollection()){
				series.getId();
				series.getModality();
				series.getLaterality();
				series.getProtocolName();
				series.getSeriesDesc();
				series.getBodyPartExamined();
				series.getTrialProtocolId();
				series.getProtocolName();
	            series.getSite();
	            series.getSeriesDesc();
	            series.getAdmittingDiagnosesDesc();
	            series.getPatientSex();
	            series.getAgeGroup();
	            series.getPatientId();
	            series.getProject();
	            series.getSite();
	            if (series.getGeneralImageCollection()!=null)
	            {
	        		for (GeneralImage image : series.getGeneralImageCollection())
	        		{
	        			image.getId();
	        			image.getImageType();
	        			image.getLossyImageCompression();
	        			image.getImageOrientationPatient();
	                    image.getImagePositionPatient();
	        			image.getContrastBolusAgent();
	        			image.getContrastBolusRoute();
	        			image.getPatientPosition();
	        			image.getImageComments();
	        			image.getAnnotation();
	        			image.getImageLaterality();
	        			image.getPatientId();
	        			image.getProject();
	        			image.getUsFrameNum();
	        			image.getUsColorDataPresent();
	        			image.getUsMultiModality();
	                    if (image.getMrImage()!=null)
	                    {
	                    	MRImage mrImage = image.getMrImage();
	                    	mrImage.getImageTypeValue3();
	                    	mrImage.getScanningSequence();
	                    	mrImage.getSequenceVariant();
	                    	mrImage.getSequenceName();
	                        mrImage.getImagedNucleus();
	                    }
	                    if (image.getCtimage()!=null)
	                    {
	                    	CTImage ctImage = image.getCtimage();
	                    	ctImage.getScanOptions();
	                    	ctImage.getConvolutionKernel();
	                    	ctImage.getAnatomicRegionSeq();
	                    }
	                    if (image.getFilename()!=null)
	                    {
	                    	image.getFilename();
		                 }
	        		}
	            }
	            if (series.getGeneralEquipment()!=null)
	            {
	            	GeneralEquipment equipment = series.getGeneralEquipment();
	            	equipment.getDeviceSerialNumber();
	                equipment.getManufacturer();
	                equipment.getInstitutionName();
	                equipment.getInstitutionName();
	                equipment.getManufacturerModelName();
	                equipment.getSoftwareVersions();
	                equipment.getStationName();
	            }
			    
			}
		}
	  }
	}
	    

} catch (HibernateException e) {
	// TODO Auto-generated catch block
	e.printStackTrace();
}

return returnValue;
}
@Transactional(propagation=Propagation.REQUIRED)
public List<Object> getCollectionDesc(String collection)
{
List<Object> returnValue = new ArrayList<Object>();

try {
	returnValue = getHibernateTemplate().getSessionFactory().getCurrentSession()
	.createSQLQuery("select description descript from collection_descriptions where collection_name = :collection")
    .addScalar("descript",Hibernate.TEXT)
	.setParameter("collection", collection).list();
} catch (HibernateException e) {
	// TODO Auto-generated catch block
	e.printStackTrace();
}

return returnValue;
}
@Transactional(propagation=Propagation.REQUIRED)
public List<Object> getAnnotationFiles(Integer seriesPK)
{
List<Object> returnValue = new ArrayList<Object>();

try {
	returnValue = getHibernateTemplate().getSessionFactory().getCurrentSession()
    .createSQLQuery("SELECT annot.file_Path "+
	"FROM Annotation annot WHERE annot.general_Series_Pk_Id="+seriesPK).list();
} catch (HibernateException e) {
	// TODO Auto-generated catch block
	e.printStackTrace();
}

return returnValue;
}
}