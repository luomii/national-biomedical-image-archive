package gov.nih.nci.ncia.dto;


public class ImageSecurityDTO {

	public ImageSecurityDTO(String SOPInstanceUID,
			                String fileName,
			                String project,
			                String site,
			                String ssg,
			                boolean seriesVisibility,
			                int frameNum){
		this.SOPInstanceUID = SOPInstanceUID;
		this.fileName = fileName;
		this.project = project;
		this.site = site;
		this.ssg = ssg;
		this.seriesVisibility = seriesVisibility;
		this.frameNum = frameNum;
	}


	public String getSOPInstanceUID() {
		return SOPInstanceUID;
	}

	public String getFileName() {
		return fileName;
	}

	public String getProject() {
		return project;
	}

	public String getSite() {
		return site;
	}

	public String getSsg() {
		return ssg;
	}

	public boolean getSeriesVisibility() {
		return seriesVisibility;
	}

	public int getFrameNum() {
			return frameNum;
	}

	private String SOPInstanceUID;
	private String fileName;
	private String project;
	private String site;
	private String ssg;
	private boolean seriesVisibility;
	private int frameNum;

}