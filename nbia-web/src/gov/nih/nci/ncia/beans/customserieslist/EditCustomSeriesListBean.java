package gov.nih.nci.ncia.beans.customserieslist;

import gov.nih.nci.nbia.dto.CustomSeriesListAttributeDTO;
import gov.nih.nci.nbia.dto.CustomSeriesListDTO;
import gov.nih.nci.nbia.security.AuthorizationManager;
import gov.nih.nci.ncia.beans.BeanManager;
import gov.nih.nci.ncia.beans.security.SecurityBean;
import gov.nih.nci.ncia.customserieslist.CustomSeriesListProcessor;
import gov.nih.nci.ncia.customserieslist.FileParser;
import gov.nih.nci.ncia.util.MessageUtil;

import java.util.ArrayList;
import java.util.EventObject;
import java.util.List;

import javax.faces.component.UIData;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import com.icesoft.faces.component.inputfile.FileInfo;
import com.icesoft.faces.component.inputfile.InputFile;

public class EditCustomSeriesListBean {
	//private static Logger logger = Logger.getLogger(EditCustomSeriesListBean.class);
	private String name = "";
	private String comment = "";
	private String hyperlink = "";
	private String seriesUIDs = "";
	private CustomSeriesListDTO selectedList;

	private CustomSeriesListProcessor processor;
	private FileParser parser;
	private SecurityBean sb;
	private String username;
	private UIData table;
	private String selectedDispItemNum = "10";

	/* hold the list of the custom series for the user */
	private List<CustomSeriesListDTO> customList = new ArrayList<CustomSeriesListDTO>();
	/* hold the series instance uid user uploads from a file */
	private List<String> seriesInstanceUids = new ArrayList<String>();
	/* hold list of attribute for a given custom list */
	private List<CustomSeriesListAttributeDTO> seriesInstanceUidsList = new ArrayList<CustomSeriesListAttributeDTO>();
	private boolean showSelected = false;
	private List<String> noPermissionSeries = new ArrayList<String>();
	private List<String> privateSeries = new ArrayList<String>();

	private FileInfo currentFile;
	private boolean infoMessage = false;
	private String message;
	
	// file upload completed percent (Progress)
	private int fileProgress=-1;
    private int seriesCount=0;
    private int privateSeriesCount=0;
    private int noPermissionSeriesCount=0;

	public EditCustomSeriesListBean() throws Exception{
		sb = BeanManager.getSecurityBean();
		username = sb.getUsername();
		AuthorizationManager am =sb.getAuthorizationManager();
		processor = new CustomSeriesListProcessor(sb.getUsername(), am);
		// retrieve the list this user have created
		System.out.println("contructor... getting custom list by this user: "
				+ username);

		parser = new FileParser();
	}

	/**
	 * action event to upload file
	 * 
	 * @param actionEvent
	 */
	public void uploadActionListener(ActionEvent actionEvent) {
		InputFile inputFile = (InputFile) actionEvent.getSource();
		currentFile = inputFile.getFileInfo();
		if (currentFile.getStatus() == FileInfo.SAVED) {
			/* clear whatever in the list */
			// seriesFromFile.clear();
			privateSeries.clear();
			noPermissionSeries.clear();
			System.out.println("file path: " +currentFile.getFile().getPath() + 
					" \nabsolute path: " + currentFile.getFile().getAbsolutePath() + " contentType: " + currentFile.getContentType()
					);
			System.out.println("currentFile.getFile().getName() " + currentFile.getFile().getName());
			if(parser.isCSVFile(currentFile.getFile())){
				System.out.println("not .cvs file");
				infoMessage = true;
				message = "Please upload an .csv file.";
				fileProgress=-1;
			}else{
				seriesInstanceUids = parser.parse(currentFile.getFile());

				// System.out.println("series list size: " +
				// seriesInstanceUids.size());
				// check for permission to see all the series in the list
				noPermissionSeries = processor.validate(seriesInstanceUids);
				System.out.println("noPermissionSeries size: "
						+ noPermissionSeries.size());
				if (noPermissionSeries.isEmpty()) {
					privateSeries = processor.isAnyPrivate(seriesInstanceUids);
				}
				// need to replace the current list
				replace(seriesInstanceUids);
				
				infoMessage = false;
			}
		} else {
			System.out.println("uploaded file status "
					+ currentFile.getStatus());
			MessageUtil.addErrorMessage(
					"MAINbody:customSeriesEditForm:inputFileMessage",
					"customListUploadFileError");
			infoMessage = true;
			message = "Error uploading the file. Please check the file and load again.";
		}
	}
	/**
     * <p>This method is bound to the inputFile component and is executed
     * multiple times during the file upload process.  Every call allows
     * the user to finds out what percentage of the file has been uploaded.
     * This progress information can then be used with a progressBar component
     * for user feedback on the file upload progress. </p>
     *
     * @param event holds a InputFile object in its source which can be probed
     *              for the file upload percentage complete.
     */
    public void fileUploadProgress(EventObject event) {
        InputFile ifile = (InputFile) event.getSource();
        fileProgress = ifile.getFileInfo().getPercent();
    }


	/**
	 * retrieve list the user had created
	 */
	public String viewCreatedLists() {
		System.out.println("retrieve... .. getting custom list by this user: "
				+ username);
		showSelected = false;
		customList = processor.getCustomListByUser(username);
		return "editCustomSeriesList";
	}

	/**
	 * action event when name is clicked for editing
	 * 
	 * @param actionEvent
	 * @throws Exception
	 */
	public void namedDetailsClicked(ActionEvent actionEvent) throws Exception {
		reset();
		//noPermissionSeries.clear();
		int index = table.getRowIndex();
		System.out.println("index: " + index);
		//selectedList = null;
		selectedList = customList.get(index);
		System.out.println("name: " + selectedList.getName() + " comment: " + selectedList.getComment());
		Integer customSeriesListPkId = selectedList.getId();
		seriesInstanceUidsList.clear();
		seriesInstanceUidsList = processor
				.getCustomseriesListAttributesById(customSeriesListPkId);
		selectedList.setSeriesInstanceUidsList(seriesInstanceUidsList);

		showSelected = true;
	}

	/**
	 * action to submit the selected and edited list name
	 */
	public String edit() {
		showSelected = true;
		//String name = selectedList.getName();
		// if user doesn't have permission to see some series, do not allow
		// update
		if (!noPermissionSeries.isEmpty()) {
//			MessageUtil.addErrorMessage(
//					"MAINbody:customSeriesEditForm:editMessage",
//					"customListUpdateError", new Object[] { name });
			System.out.println("isEmty noPermissionSeries: "
					+ noPermissionSeries.isEmpty());
			infoMessage = true;
			message = "Please make sure you have permission to update all the series. The list (" + selectedList.getName() + ") has not been updated.";
			return "";
		}

		CustomSeriesListDTO editDTO = new CustomSeriesListDTO();
		System.out.println("id: " + selectedList.getId() + " name: " + selectedList.getName() + " comment: " + selectedList.getComment() + " hyperlink: " + selectedList.getHyperlink());
		editDTO.setId(selectedList.getId());
		editDTO.setName(selectedList.getName());
		editDTO.setComment(selectedList.getComment());
		editDTO.setHyperlink(selectedList.getHyperlink());
		Boolean updatedSeries = seriesInstanceUids.size() > 0 ? true: false ;
		editDTO.setSeriesInstanceUIDs(seriesInstanceUids);
		long update = processor.update(selectedList, updatedSeries);
		if (update > 0L) {
			showSelected = false;
			seriesInstanceUids.clear();
			noPermissionSeries.clear();
			privateSeries.clear();
			infoMessage = false;
		} else {
			// display error message
			MessageUtil.addInfoMessage(
					"MAINbody:customSeriesEditForm:infoMessage",
					"customListUpdateError", new Object[] { name });
			infoMessage = true;
			message = "Error updating the list '" + name + "'";
		}
		return "";
	}

	/**
	 * Reset
	 */
	public String reset() {
		message = "";
		showSelected = false;
		seriesInstanceUids.clear();
		noPermissionSeries.clear();
		privateSeries.clear();
		infoMessage = false;
		selectedList = null;
		fileProgress= -1;
		return "";
	}
	public int getFileProgress() {
        return fileProgress;
    }

	public List<String> getNoPermissionSeries() {
		return noPermissionSeries;
	}

	public void setNoPermissionSeries(List<String> noPermissionSeries) {
		this.noPermissionSeries = noPermissionSeries;
	}

	public List<String> getPrivateSeries() {
		return privateSeries;
	}

	public void setPrivateSeries(List<String> privateSeries) {
		this.privateSeries = privateSeries;
	}

	public List<CustomSeriesListDTO> getCustomList() {
		return customList;
	}

	public void setCustomizedList(List<CustomSeriesListDTO> customList) {
		this.customList = customList;
	}

	public boolean isShowSelected() {
		return showSelected;
	}

	public void setShowSelected(boolean showSelected) {
		this.showSelected = showSelected;
	}

	public String getSeriesUIDs() {
		return seriesUIDs;
	}

	public void setSeriesUIDs(String seriesUIDs) {
		this.seriesUIDs = seriesUIDs;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getHyperlink() {
		return hyperlink;
	}

	public void setHyperlink(String hyperlink) {
		this.hyperlink = hyperlink;
	}

	public boolean showSelected() {
		return showSelected;
	}

	public List<String> getSeriesInstanceUids() {
		return seriesInstanceUids;
	}

	public void setSeriesInstanceUids(List<String> seriesInstanceUids) {
		this.seriesInstanceUids = seriesInstanceUids;
	}

	public List<CustomSeriesListAttributeDTO> getSeriesInstanceUidsList() {
		return seriesInstanceUidsList;
	}

	public void setSeriesInstanceUidsList(
			List<CustomSeriesListAttributeDTO> seriesInstanceUidsList) {
		this.seriesInstanceUidsList = seriesInstanceUidsList;
	}

	public CustomSeriesListDTO getSelectedList() {
		return selectedList;
	}

	public void setSelectedList(CustomSeriesListDTO selectedList) {
		this.selectedList = selectedList;
	}

	public UIData getTable() {
		return table;
	}

	public void setTable(UIData table) {
		this.table = table;
	}

	/**
	 * Gets the options for number of displaying items for QC Result.
	 * 
	 * @return array of predefined numbers for displaying search result
	 */
	public SelectItem[] getDispItemNums() {
		SelectItem[] dispItemNums = new SelectItem[4];
		dispItemNums[0] = new SelectItem("10");
		dispItemNums[1] = new SelectItem("25");
		dispItemNums[2] = new SelectItem("50");
		dispItemNums[3] = new SelectItem("100");
		return dispItemNums;
	}

	/**
	 * Method called when the number of display item are changed.
	 * 
	 *@param vce
	 *            event of the change
	 */
	public void numberChangeListener(ValueChangeEvent vce) {
		System.out.println("numberChangeListener called");
	}

	/**
	 * @return the selectedDispItemNum
	 */
	public String getSelectedDispItemNum() {
		return selectedDispItemNum;
	}

	/**
	 * @param selectedDispItemNum
	 *            the selectedDispItemNum to set
	 */
	public void setSelectedDispItemNum(String selectedDispItemNum) {
		this.selectedDispItemNum = selectedDispItemNum;
	}

	public boolean isInfoMessage() {
		return infoMessage;
	}

	public void setInfoMessage(boolean infoMessage) {
		this.infoMessage = infoMessage;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public FileInfo getCurrentFile() {
		return currentFile;
	}

	public int getSeriesCount() {
		if(seriesInstanceUidsList != null){
			seriesCount = seriesInstanceUidsList.size();
		}
		return seriesCount;
	}
	public int getPrivateSeriesCount() {
		if(privateSeries != null){
			privateSeriesCount = privateSeries.size();
		}
		return privateSeriesCount;
	}
	public int getNoPermissionSeriesCount() {
		if(noPermissionSeries != null){
			noPermissionSeriesCount = noPermissionSeries.size();
		}
		return noPermissionSeriesCount;
	}
	private void replace(List<String> seriesInstanceUids) {
		int currentSize = seriesInstanceUidsList.size();
		int newSize = seriesInstanceUids.size();
		System.out.println("currentSize: " + currentSize + " newSize: "
				+ newSize);
		seriesInstanceUidsList.clear();
		for(int i=0; i<newSize; i++){
			CustomSeriesListAttributeDTO dto = new CustomSeriesListAttributeDTO();
			dto.setParentId(selectedList.getId());
			dto.setSeriesInstanceUid(seriesInstanceUids.get(i));
			seriesInstanceUidsList.add(dto);
		}
	}
}
