package gov.nih.nci.ncia.deletion;

import gov.nih.nci.nbia.internaldomain.GeneralSeries;
import gov.nih.nci.nbia.internaldomain.Patient;
import gov.nih.nci.nbia.internaldomain.Study;
import gov.nih.nci.ncia.exception.DataAccessException;

public interface DeletionCheckingTestCaseSupport {
	public GeneralSeries getSeries(Integer id) throws DataAccessException;
	public Study getStudy(Integer id) throws DataAccessException;
	public Patient getPatient(Integer id) throws DataAccessException;
}