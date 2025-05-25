// JUnit import test
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.*;

/**
 * This test class is used to verify that the JUnit library can be imported and used properly.
 */
public class JUnitImportTest {
    
    @BeforeEach
    void setUp() {
        System.out.println("🔧 Preparing for test...");
    }
    
    @AfterEach
    void tearDown() {
        System.out.println("🧹 Cleaning up after test...");
    }
    
    @Test
    @DisplayName("Verify basic JUnit assertions")
    void testBasicAssertions() {
        System.out.println("📋 Running basic assertions test...");
        
        // Test basic assertEquals
        assertEquals(4, 2 + 2, "2 + 2 should be 4");
        
        // Test assertTrue and assertFalse
        assertTrue(true, "true should be true");
        assertFalse(false, "false should be false");
        
        // Test assertNotNull
        String testString = "Hello JUnit";
        assertNotNull(testString, "String should not be null");
        
        System.out.println("✅ Basic assertions test passed!");
    }
    
    @Test
    @DisplayName("Verify string operations")
    void testStringOperations() {
        System.out.println("📝 Running string operations test...");
        
        String expected = "JUnit Test";
        String actual = "JUnit" + " " + "Test";
        
        assertEquals(expected, actual, "String concatenation should be correct");
        assertTrue(actual.contains("JUnit"), "Result should contain 'JUnit'");
        
        System.out.println("✅ String operations test passed!");
    }
    
    @Test
    @DisplayName("Verify array and collection operations")
    void testArrayAndCollections() {
        System.out.println("📊 Running array and collection test...");
        
        int[] numbers = {1, 2, 3, 4, 5};
        assertEquals(5, numbers.length, "Array length should be 5");
        
        // Test array contents
        assertArrayEquals(new int[]{1, 2, 3, 4, 5}, numbers, "Array contents should match");
        
        System.out.println("✅ Array and collection test passed!");
    }
    
    @Test
    @DisplayName("Verify exception handling")
    void testExceptionHandling() {
        System.out.println("⚠️  Running exception handling test...");
        
        // Test expected exception
        assertThrows(ArithmeticException.class, () -> {
            int result = 10 / 0;
        }, "Division by zero should throw ArithmeticException");
        
        System.out.println("✅ Exception handling test passed!");
    }
    
    @Test
    @DisplayName("JUnit library import verification")
    void testJUnitImport() {
        System.out.println("📚 Verifying JUnit library import...");
        
        // If this runs, JUnit is imported successfully
        System.out.println("✅ Successfully imported the following JUnit classes:");
        System.out.println("   - org.junit.jupiter.api.Test");
        System.out.println("   - org.junit.jupiter.api.DisplayName");
        System.out.println("   - org.junit.jupiter.api.BeforeEach");
        System.out.println("   - org.junit.jupiter.api.AfterEach");
        System.out.println("   - org.junit.jupiter.api.Assertions");
        
        // Simple success assertion
        assertTrue(true, "JUnit library imported and running successfully!");
    }
}
