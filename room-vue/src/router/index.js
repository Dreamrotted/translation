import Vue from 'vue'
import VueRouter from 'vue-router'
import Layout from '../views/Layout.vue'
import {Message} from "element-ui";

Vue.use(VueRouter)

const routes = [
    //  ====== 登录  =====
    {
        path: '/login',
        name: 'Login',
        component: () => import('@/views/login/login.vue'),
    },
    {
        path: '/register',
        name: 'Register',
        component: () => import('@/views/login/register.vue'),
    },
    //  ====== 主页  =====
    {
        path: '/',
        name: 'Layout',
        component: Layout,
        children: [  // 子路由
            //  ====== 管理员  =====
            {
                path: 'dashboard',
                name: 'dashboard',
                component: () => import('@/views/admin/dashboard.vue'),
            },
            {
                path: 'studyRoom',
                name: 'studyRoom',
                component: () => import('@/views/admin/studyRoom.vue'),
            },
            {
                path: 'student',
                name: 'student',
                component: () => import('@/views/admin/student.vue'),
            },
            {
                path: 'reservationData',
                name: 'reservationData',
                component: () => import('@/views/admin/reservation.vue'),
            },
            {
                path: 'seat',
                name: 'seat',
                component: () => import('@/views/admin/seat.vue'),
            },
            {
                path: 'violation',
                name: 'violation',
                component: () => import('@/views/admin/violation.vue'),
            },
            {
                path: 'blacklist',
                name: 'blacklist',
                component: () => import('@/views/admin/blacklist.vue'),
            },
            {
                path: 'feedbackAdmin',
                name: 'feedbackAdmin',
                component: () => import('@/views/admin/feedback.vue'),
            },
            //  ====== 学生  =====
            {
                path: 'reservationRoom',
                name: 'reservationRoom',
                component: () => import('@/views/student/studyRoom.vue'),
            },
            {
                path: 'record',
                name: 'record',
                component: () => import('@/views/student/reservation.vue'),
            },
            {
                path: 'info',
                name: 'info',
                component: () => import('@/views/student/studentInfo.vue'),
            },
            {
                path: 'feedback',
                name: 'feedback',
                component: () => import('@/views/student/feedback.vue'),
            },
            {
                path: 'feedbackList',
                name: 'feedbackList',
                component: () => import('@/views/student/feedbackList.vue'),
            },
        ]
    },
    {
        path: "*",
        component: () => import('@/views/404')
    }
]

const router = new VueRouter({
    mode: 'history',
    base: process.env.BASE_URL,
    routes
})

// 路由拦截器
router.beforeEach((to, from, next) => {
    const token = localStorage.getItem('token');
    if ((to.path !== '/login' && to.path !== '/register') && !token) {
        Message.error('登录已过期，请重新登录')
        next('/login');
    } else {
        next();
    }
});

export default router
