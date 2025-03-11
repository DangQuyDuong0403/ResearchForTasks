package Duong.CV.previewFile.image;


import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Arrays;

/**
 * Java file bean that holds file data. The file data content is stored in
 * byte[] array and there are few complementary properties like name,
 * content type etc. If the gzipped property is set the data content is encoded.
 */
public class FileData
{
    /** Name of data file including file extension. */
    protected String fileName = null;

    /** The content type with MIME and character encoding (separated by semicolon). */
    protected String contentType = null;

    /** Flag that data are encoded (packed). */
    protected boolean gzipped = false;

    /**
     * File data.
     * @deprecated Use input stream instead.
     */
    protected byte[] data = null;

    /**
     * Data input stream.
     */
    protected InputStream dataInput = null;

    /**
     * Java bean default constructor.
     */
    public FileData()
    {
    }

    /**
     * Copy constructor that initializes all fields .
     * @param fileData name of file
     */
    public FileData(FileData fileData)
    {
        this.fileName = fileData.fileName;
        this.contentType = fileData.contentType;
        this.gzipped = fileData.gzipped;
        this.data = fileData.data;
        this.dataInput = fileData.dataInput;
    }

    /**
     * Creates file data object.
     * @param fileName name of file
     * @param contentType content type
     * @param gzipped true if data content is encoded
     * @param dataInput data content input stream
     */
    public FileData(String fileName,
                    String contentType,
                    boolean gzipped,
                    InputStream dataInput)
    {
        this.fileName = fileName;
        this.contentType = contentType;
        this.gzipped = gzipped;
        this.dataInput = dataInput;
    }

    /**
     * Creates file data object.
     * @param fileName name of file
     * @param contentType content type
     * @param gzipped true if data content is encoded
     * @param data data content
     */
    public FileData(String fileName,
                    String contentType,
                    boolean gzipped,
                    byte[] data)
    {
        this.fileName = fileName;
        this.contentType = contentType;
        this.gzipped = gzipped;
        this.data = data;
    }

    /**
     * Returns data file name with file extension (eg. picture.png).
     * @return name of file
     */
    public String getFileName()
    {
        return fileName;
    }

    /**
     * Sets data file name. The name needs to include file extension to associate
     * it with appropriate application.
     * @param name name of file
     */
    public void setFileName(String name)
    {
        this.fileName = name;
    }

    /**
     * Returns content type definition with MIME and character encoding separated
     * by semicolon.
     * @return content type definition with MIME and character encoding
     */
    public String getContentType()
    {
        return contentType;
    }

    /**
     * Sets content type definition with MIME and character encoding separated
     * by semicolon.
     * @param contentType content type definition with MIME and character encoding
     */
    public void setContentType(String contentType)
    {
        this.contentType = contentType;
    }

    /**
     * Returns <code>true</code> if the content is encoded (zipped).
     * @return <code>true</code> if the content is encoded
     */
    public boolean isGzipped()
    {
        return gzipped;
    }

    /**
     * Sets flag that the content is zipped.
     * @param gzipped flag that the content is zipped
     */
    public void setGzipped(boolean gzipped)
    {
        this.gzipped = gzipped;
    }

    /**
     * Returns data content. It may be encoded (see <code>isGzipped()</code>).
     * @return data content
     */
    public byte[] getData()
    {
        return data;
    }

    /**
     * Sets the file data. If the data are encoded make sure that gzipped
     * property is set to true.
     * @param data file data
     */
    public void setData(byte[] data)
    {
        this.data = data;
    }

    /**
     * Returns data content as an input stream. It may be encoded (see <code>isGzipped()</code>).
     * @return data content
     */
    public InputStream getDataStream()
    {
        if (dataInput == null && data != null)
        {
            return new ByteArrayInputStream(data);
        }
        else
        {
            return dataInput;
        }
    }

    /**
     * Sets the file data input stream. If the data are encoded make sure that gzipped
     * property is set to true.
     * @param dataStream file data
     */
    public void setDataStream(InputStream dataStream)
    {
        this.dataInput = dataStream;
    }

    /**
     * Returns string status of this object useful for debugging.
     * @return string status of this object
     */
    @Override
    public String toString()
    {
        return "Filename: " + fileName +
                "\n  ContentType: " + contentType +
                "\n  Gzipped: " + gzipped +
                "\n  Data length: " + (data != null ? data.length : "unknown");
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == null)
        {
            return false;
        }
        if (getClass() != obj.getClass())
        {
            return false;
        }
        final FileData other = (FileData) obj;
        if ((this.fileName == null) ? (other.fileName != null) : !this.fileName.equals(other.fileName))
        {
            return false;
        }
        if ((this.contentType == null) ? (other.contentType != null) : !this.contentType.equals(other.contentType))
        {
            return false;
        }
        if (this.gzipped != other.gzipped)
        {
            return false;
        }
        // compare data only if they are set and don't compare streams
        if (this.data != null && other.data != null)
        {
            if ( !Arrays.equals(this.data, other.data))
            {
                return false;
            }
        }
        // INFO PO: commented because they may not be set
//    // compare if data or input stream is set or not
//    if ((this.data != null || this.dataInput != null)
//            && (other.data == null && other.dataInput == null))
//    {
//      return false;
//    }
//    if ((this.data == null && this.dataInput == null)
//            && (other.data != null || other.dataInput != null))
//    {
//      return false;
//    }

        return true;
    }

    @Override
    public int hashCode()
    {
        int hash = 7;
        hash = 89 * hash + (this.fileName != null ? this.fileName.hashCode() : 0);
        hash = 89 * hash + (this.contentType != null ? this.contentType.hashCode() : 0);
        hash = 89 * hash + (this.gzipped ? 1 : 0);
        hash = 89 * hash + Arrays.hashCode(this.data);
        return hash;
    }
}

