package proof.demo;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UploadTest {

    Upload underTest;

    @Test
    public void canConnectToFtpClient() {

        underTest = new Upload();
        boolean expected = Upload.canConnect();
        System.out.println("test runs");
        assertEquals(expected, true);
    }
}
