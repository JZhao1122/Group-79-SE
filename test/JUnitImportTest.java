// JUnit导入测试
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.*;

/**
 * 这个测试类用于验证JUnit库是否可以正常导入和使用
 */
public class JUnitImportTest {
    
    @BeforeEach
    void setUp() {
        System.out.println("🔧 测试准备...");
    }
    
    @AfterEach
    void tearDown() {
        System.out.println("🧹 测试清理...");
    }
    
    @Test
    @DisplayName("验证JUnit基本断言功能")
    void testBasicAssertions() {
        System.out.println("📋 运行基本断言测试...");
        
        // 测试基本的assertEquals
        assertEquals(4, 2 + 2, "2 + 2 应该等于 4");
        
        // 测试assertTrue和assertFalse
        assertTrue(true, "true应该为真");
        assertFalse(false, "false应该为假");
        
        // 测试assertNotNull
        String testString = "Hello JUnit";
        assertNotNull(testString, "字符串不应该为null");
        
        System.out.println("✅ 基本断言测试通过！");
    }
    
    @Test
    @DisplayName("验证字符串操作")
    void testStringOperations() {
        System.out.println("📝 运行字符串测试...");
        
        String expected = "JUnit Test";
        String actual = "JUnit" + " " + "Test";
        
        assertEquals(expected, actual, "字符串拼接应该正确");
        assertTrue(actual.contains("JUnit"), "结果应该包含'JUnit'");
        
        System.out.println("✅ 字符串测试通过！");
    }
    
    @Test
    @DisplayName("验证数组和集合操作")
    void testArrayAndCollections() {
        System.out.println("📊 运行数组和集合测试...");
        
        int[] numbers = {1, 2, 3, 4, 5};
        assertEquals(5, numbers.length, "数组长度应该为5");
        
        // 测试数组内容
        assertArrayEquals(new int[]{1, 2, 3, 4, 5}, numbers, "数组内容应该匹配");
        
        System.out.println("✅ 数组和集合测试通过！");
    }
    
    @Test
    @DisplayName("验证异常处理")
    void testExceptionHandling() {
        System.out.println("⚠️  运行异常处理测试...");
        
        // 测试预期异常
        assertThrows(ArithmeticException.class, () -> {
            int result = 10 / 0;
        }, "除零应该抛出ArithmeticException");
        
        System.out.println("✅ 异常处理测试通过！");
    }
    
    @Test
    @DisplayName("JUnit库导入验证")
    void testJUnitImport() {
        System.out.println("📚 验证JUnit库导入...");
        
        // 如果能运行到这里，说明JUnit导入成功
        System.out.println("✅ 成功导入以下JUnit类：");
        System.out.println("   - org.junit.jupiter.api.Test");
        System.out.println("   - org.junit.jupiter.api.DisplayName");
        System.out.println("   - org.junit.jupiter.api.BeforeEach");
        System.out.println("   - org.junit.jupiter.api.AfterEach");
        System.out.println("   - org.junit.jupiter.api.Assertions");
        
        // 简单的成功断言
        assertTrue(true, "JUnit库导入并运行成功！");
    }
} 