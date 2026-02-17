import { createRouter, createWebHistory } from 'vue-router'
import Layout from '@/layout/Layout.vue'

const routes = [
    // 公共页面：登录/注册（不走 Layout）
    {
        path: '/login',
        name: 'LoginView',
        component: () => import('@/views/Login.vue'),
        meta: { public: true, title: '登录' }
    },
    {
        path: '/register',
        name: 'RegisterView',
        component: () => import('@/views/Register.vue'),
        meta: { public: true, title: '注册' }
    },

    // 业务页面：走 Layout
    {
        path: '/',
        component: Layout,
        redirect: '/home',
        children: [
            {
                path: 'home',
                name: 'Home',
                component: () => import('@/views/Home.vue'),
                meta: { title: '首页' }
            }
            // 你后面还有其它 children 路由，继续往这里加
        ]
    },

    // 兜底
    {
        path: '/:pathMatch(.*)*',
        redirect: '/home'
    }
]

const router = createRouter({
    history: createWebHistory(),
    routes
})

// 你项目里 token 名字如果不是 token/access_token，就改这里
function getToken() {
    return localStorage.getItem('token') || localStorage.getItem('access_token')
}

router.beforeEach((to) => {
    // 设置标题（可选）
    if (to.meta?.title) document.title = String(to.meta.title)

    // 公共页面放行
    if (to.meta?.public) return true

    // 未登录拦截
    const token = getToken()
    if (!token) {
        return {
            path: '/login',
            query: { redirect: to.fullPath }
        }
    }

    return true
})

export default router
