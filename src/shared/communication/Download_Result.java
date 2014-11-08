package shared.communication;

public class Download_Result {
	private byte[] fileBytes;

	public Download_Result(byte[] fileBytes)
	{
		this.fileBytes = fileBytes;
	}

	public byte[] getFileBytes() {
		return fileBytes;
	}

	public void setFileBytes(byte[] fileBytes) {
		this.fileBytes = fileBytes;
	}
}
