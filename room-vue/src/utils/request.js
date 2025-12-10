import axios from 'axios'
import router from "@/router";
import {Message} from "element-ui";

const request = axios.create({
    baseURL: process.env.VUE_APP_API_BASE_URL || 'http://localhost:8080',
    timeout: 5000
})

// 登录流程控制中，根据本地是否存在token判断用户的登录情况
// 但是即使token存在，也有可能token是过期的，所以在每次的请求头中携带token
// 后台根据携带的token判断用户的登录情况，并返回给我们对应的状态码
// 而后我们可以在响应拦截器中，根据状态码进行一些统一的操作。
// 请求拦截器
request.interceptors.request.use(
    config => {
        const token = localStorage.getItem('token');
        if (token) {
            config.headers.Authorization = `Bearer ${token}`;
        }
        return config;
    },
    error => {
        console.error('Request error:', error);
        return Promise.reject(error);
    }
);

// 响应拦截器
request.interceptors.response.use(response => {
    return response.data;
}, error => {
    if (error.response && error.response.status === 401) {
        localStorage.removeItem('token');
        localStorage.removeItem('user');
        localStorage.removeItem('role');
        import('@/views/login/login.vue').then(() => {
            // 使用路由跳转到登录页
            if (router.currentRoute.path !== '/login') {
                router.push('/login');
            }
        });
    } else if (error.response && error.response.status === 403) {
        console.error('Forbidden: No permission');
    }
    return Promise.reject(error);
});


export default request