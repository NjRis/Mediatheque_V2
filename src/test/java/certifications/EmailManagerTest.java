package certifications;

import org.junit.jupiter.api.Test;

import javax.mail.Session;
import javax.mail.Transport;
import static org.mockito.Mockito.*;

class EmailManagerTest {

    @Test
    void envoyerAlerte_DoitAppelerTransport() throws Exception {

        Transport transportMock = mock(Transport.class);
        EmailManager emailManager = new EmailManager() {
            protected Transport getTransport(Session session) {
                return transportMock;
            }
        };


        emailManager.envoyerAlerte("Test");

        // marche pas lol
        verify(transportMock).sendMessage(any(), any());
    }
}