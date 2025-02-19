import axios from "axios";
import router from "../router";
import { ElMessage } from "element-plus";

// 创建一个axios的实例
const milliaAxios = axios.create({
    baseURL: "http://127.0.0.1:8888",
    timeout: 1000 * 60, // 请求超时时间 毫秒
    withCredentials: false,  //  异步请求携带cookie
    headers: {
        // 设置后端需要的传参类型
        'Content-Type': 'application/json',
        'token': ''
    }
})

// 拦截器 请求拦截
milliaAxios.interceptors.request.use(config => {
    console.log("请求拦截");
    const token = localStorage.getItem('token');

    // 判断是否存在token
    if (token) {
        config.headers.Authorization = `Bearer ${token}`;
    } else {
        router.push('/login')
    }
    return config
}, error => {
    console.log(error);
    return Promise.reject(error)
})

// 拦截器 响应拦截
milliaAxios.interceptors.response.use(response => {
    console.log("响应拦截");
    console.log(response);

    if (response.data.code != 200) {
        ElMessage.error(response.data.msg)
        switch (response.data.code) {
            case 401:
                router.push('/login')
                break
            // 其他错误，直接抛出错误提示
            default:
        }
    }
    return Promise.resolve(response);
}, error => {
    console.log(error)
    if (error.code == 'ERR_NETWORK') {
        ElMessage.error(error.message)
        return Promise.reject(error);
    }
    switch (error.response.status) {
        case 500:
            ElMessage.error('Oops,Server Error.')
            break
        case 404:
            ElMessage.error('Oops, Not Find!')
            break
        case 400:
            // ElMessage.error('Oops, 400!')
            break
        // 其他错误，直接抛出错误提示
        default:
            ElMessage.error(error.message)
    }
    return Promise.reject(error);

});

// 处理put、delete请求


function f_base_request(method, url, params) {
    return new Promise((resolve, reject) => {
        milliaAxios[method](url, method === 'get' ? { params } : params)
            .then(res => {
                resolve(res.data);
            })
            .catch(err => {
                reject(err);
            });
    });
}

//  将Promise 的错误统一捕获
export async function post(url, params) {
    let back = null;
    try {
        back = await f_base_request('post', url, params);
    } catch (e) {
        if (e.response && e.response.data === 'The CSRF token is missing.') {
            console.log('CSRF token miss, try again');
            back = await post(url, params);
        }
        console.log('async post', e);
    }
    return back;
}

export async function get(url, params) {
    let back = null;
    try {
        back = await f_base_request('get', url, params);
    } catch (e) {
        console.log('async get', e);
    }
    return back;
}

export async function put(url, params) {
    let back = null;
    try {
        back = await f_base_request('put', url, params);
    } catch (e) {
        console.log('async put', e);
    }
    return back;
}

export async function del(url, params) {
    let back = null;
    try {
        back = await f_base_request('delete', url, { data: params });
    } catch (e) {
        console.log('async delete', e);
    }
    return back;
}