import component from "element-plus/es/components/tree-select/src/tree-select-option.mjs";
import { createRouter, createWebHistory } from "vue-router"


// 配置路由
const routes = [
    {
        path: "/",
        component: () => import("../view/index.vue"),
    },
    {
        path: "/login",
        name: "login",
        component: () => import("../view/user/login.vue")
    },
    // {
    //     path: '/test',
    //     name: 'test',
    //     component: () => import("../view/page/test.vue")
    // },
    // {
    //     path: '/userManagement',
    //     name: 'userManagement',
    //     component: () => import("../view/user/userManagement.vue")
    // },
    {
        path: '/404',
        name: 'NotFound',
        component: () => import("../components/error/404.vue")
    }
]

// 返回一个router实例，为函数，配置history模式
const router = createRouter({
    history: createWebHistory(),
    routes
});

//设置路由守卫
router.beforeEach((to, from, next) => {
    if (to.name !== 'NotFound' && !routes.some(route => route.path === to.path)) {
        next({ name: 'NotFound' }); // 重定向到404页面
    } else {
        next(); // 继续导航
    }
})

// 导出路由 去main.js注册
export default router