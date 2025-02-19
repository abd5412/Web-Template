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