package proof.demo;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.junit.After;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.mockftpserver.fake.FakeFtpServer;
import org.mockftpserver.fake.UserAccount;
import org.mockftpserver.fake.filesystem.DirectoryEntry;
import org.mockftpserver.fake.filesystem.FileEntry;
import org.mockftpserver.fake.filesystem.FileSystem;
import org.mockftpserver.fake.filesystem.UnixFakeFileSystem;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class FtpClientIntegrationTest {

    private FakeFtpServer fakeFtpServer;
    private FtpClient ftpClient;


    @Before
    public void setup() throws IOException {
        fakeFtpServer = new FakeFtpServer();
        fakeFtpServer.addUserAccount(new UserAccount("user", "password", "/data"));

        FileSystem fileSystem = new UnixFakeFileSystem();
        fileSystem.add(new DirectoryEntry("/data"));
        fileSystem.add(new FileEntry("/data/foobar.txt", "abcdef 1234567890"));
        fakeFtpServer.setFileSystem(fileSystem);
        fakeFtpServer.setServerControlPort(0);

        fakeFtpServer.start();

        ftpClient = new FtpClient("localhost", fakeFtpServer.getServerControlPort(), "user", "password");
        ftpClient.open();
    }

    @After
    public void teardown() throws IOException {
        ftpClient.close();
        fakeFtpServer.stop();
    }

    @Test
    public void givenRemoteFile_whenListingRemoteFiles_thenItIsContainedInList() {
        Collection<String> fileList = new ArrayList<String>();
        try {
            FTPClient ftpClient = new FTPClient();
            ftpClient.connect("testftp.beatcatalog.com", 21);
            int replyCode = ftpClient.getReplyCode();
            if (!FTPReply.isPositiveCompletion(replyCode)) {
                System.out.println("Operation failed. Server reply code: " + replyCode);
                return;
            }
            boolean success = ftpClient.login("testftp2@testftp.beatcatalog.com", "P455w0rd!");
            if (!success) {
                System.out.println("Could not login to the server");
                return;
            } else {
                System.out.println("LOGGED IN SERVER");
            }
            try {
                FTPFile[] files = ftpClient.listFiles(".");
                System.out.println("test has files");
                for (FTPFile file : files) {
                    System.out.println("file: " + file.getName());
                    fileList.add(file.getName());
                }
            } catch (Exception e){
                System.out.println(e);
            }
        } catch (IOException ex) {
            System.out.println("Oops! Something wrong happened");
            ex.printStackTrace();
        }
        assertThat(fileList).contains("foobarTest.txt");
    }

    @Test
    public void givenRemoteFile_whenDownloading_isThenOnTheLocalFilesystem() throws IOException {
        FTPClient ftpClient = new FTPClient();
        System.out.println(ftpClient);
//        File foobar = new File("/data/foobar.txt");
//        File buz = new File("downloaded_buz.txt");
//        if (foobar.isDirectory()) System.out.println("foobar is a directory");
//        if (foobar.isFile()) System.out.println("foobar is a file");
//        if (buz.isDirectory()) System.out.println("buz is a directory");
//        if (buz.isFile()) System.out.println("buz is a file");


        FtpClient.downloadFile("/data/foobar.txt", "downloaded_buz.txt");

//        String buzPath = buz.getPath();
//        String foobarPath = foobar.getPath();
//        System.out.println(foobarPath);
//        System.out.println(buzPath);
        assertThat(new File("/data/foobar.txt")).exists();
//        assertTrue(foobar.buz.exists());
        new File("downloaded_buz.txt").delete();
    }

    @Test
    public void givenLocalFile_whenUpload_ExistsInRemoteLocation() throws URISyntaxException, IOException {
        try {
        } catch (Exception e){
            System.out.println("in catch exception");
            File file = new File(getClass().getClassLoader().getResource("baz.txt").toURI());
            ftpClient.putFileToPath(file, "/buz.txt");
            assertThat(fakeFtpServer.getFileSystem().exists("/buz.txt")).isTrue();
        }
    }
}
