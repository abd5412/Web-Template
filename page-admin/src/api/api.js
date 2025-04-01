import { get, post } from './config';

/**
 * 测试API
 * @param {Object} param - 请求参数
 * @returns {Promise} - 返回Promise对象
 */
export const apitest = (param) => get('/user/test', param);

/**
 * 登录API
 * @param {Object} param - 请求参数
 * @returns {Promise} - 返回Promise对象
 */
export const login = (param) => {
    console.log('参数：', param);
    return post('/user/login', param);
};

/**
 * 获取用户信息API
 * @param {Object} param - 请求参数
 * @returns {Promise} - 返回Promise对象
 */
export const getUserInfo = (param) => get('/user/getUserInfo', param);

/**
 * 登出API
 * @param {Object} param - 请求参数
 * @returns {Promise} - 返回Promise对象
 */
export const logoutApi = (param) => get('/user/logout', param);

/**
 * 重置密码API
 * @param {Object} param - 请求参数
 * @returns {Promise} - 返回Promise对象
 */
export const resetPwdApi = (param) => post('/user/resetPassword', param);

/**
 * 获取用户列表API
 */
export const userPage = (param) => get('/user/userPage', param);

/**
 * 修改用户信息
 */
export const updateUser = (param) => post('/user/editUser', param);

/**
 * 新增用户
 */
export const addUser = (param) => post('/user/addUser', param);

/**
 * 删除用户
 */
export const delUser = (param) => post('/user/delUser', param);

/**
 * 用户下线
 */
export const offlineUser = (param) => post('/user/offlineUser', param);
