package uan.edu.co.crazy_bakery;

import com.google.firebase.FirebaseApp;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class CrazyBakeryBkApplicationTests {

  @MockBean
  FirebaseApp firebaseApp;

  @Test
  void contextLoads() {
  }

}
