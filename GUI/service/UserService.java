package service;

import dto.User;
import exception.AuthException;

/**
 * 用户服务接口，处理用户登录、注册等功能
 */
public interface UserService {
    /**
     * 用户登录
     * @param username 用户名
     * @param password 密码
     * @return 登录成功的用户对象
     * @throws AuthException 登录失败时抛出
     */
    User login(String username, String password) throws AuthException;
    
    /**
     * 用户注册
     * @param username 用户名
     * @param password 密码
     * @param email 电子邮箱
     * @return 注册成功的用户对象
     * @throws AuthException 注册失败时抛出
     */
    User register(String username, String password, String email) throws AuthException;
    
    /**
     * 根据用户ID获取用户信息
     * @param userId 用户ID
     * @return 用户对象
     * @throws AuthException 获取失败时抛出
     */
    User getUserById(String userId) throws AuthException;
    
    /**
     * 删除/注销用户账号
     * @param userId 用户ID
     * @param password 密码，用于验证
     * @return 是否成功删除
     * @throws AuthException 删除失败时抛出
     */
    boolean deleteUser(String userId, String password) throws AuthException;
} 