import { createRouter, createWebHistory } from 'vue-router'
import Layout from '@/layout/Layout.vue'
import { useAuthStore } from '@/stores/auth'

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
    {
        path: '/403',
        name: 'NotAuthorized',
        component: () => import('@/views/NotAuthorized.vue'),
        meta: { title: '无权限', public: true }
    },

    // 业务页面：走 Layout（一个 Layout 承载全部页面）
    {
        path: '/',
        component: Layout,
        redirect: '/home',
        children: [
            // ✅ 公开首页（你原来就有）
            {
                path: 'home',
                name: 'Home',
                component: () => import('@/views/Home.vue'),
                meta: { public: false, title: '首页', roles: ['ADMIN', 'TEACHER'] } // 如果你希望不登录也能看首页，就加 public:true
            },

            // 用户管理
            {
                path: 'users/list',
                name: 'UserList',
                component: () => import('@/views/user/UserList.vue'),
                meta: { title: '用户列表', roles: ['ADMIN'] }
            },
            {
                path: 'users/profile',
                name: 'UserInfo',
                component: () => import('@/views/user/UserInfo.vue'),
                meta: { title: '个人信息', roles: ['ADMIN', 'TEACHER'] }
            },

            // 运动记录
            {
                path: 'records/list',
                name: 'RecordList',
                component: () => import('@/views/record/RecordList.vue'),
                meta: { title: '记录列表', roles: ['ADMIN'] }
            },

            // 赛事管理
            {
                path: 'events/list',
                name: 'EventList',
                component: () => import('@/views/event/EventList.vue'),
                meta: { title: '赛事列表', roles: ['ADMIN', 'TEACHER'] }
            },
            {
                path: 'events/signups',
                name: 'EventSignups',
                component: () => import('@/views/event/SignupList.vue'),
                meta: { title: '报名管理', roles: ['ADMIN','TEACHER'] }
            },
            {
                path: 'events/results',
                name: 'EventResults',
                component: () => import('@/views/event/ResultList.vue'),
                meta: { title: '成绩管理', roles: ['ADMIN', 'TEACHER'] }
            },

            // 健康报告
            {
                path: 'reports/health',
                name: 'HealthReport',
                component: () => import('@/views/report/HealthReport.vue'),
                meta: { title: '健康报告', roles: ['ADMIN'] }
            },
            {
                path: 'events/edit',
                name: 'EventEdit',
                component: () => import('@/views/event/EventForm.vue'),
                meta: { roles: ['ADMIN', 'TEACHER'] }
            },
            {
                path: 'events/results/edit',
                name: 'EventResultEdit',
                component: () => import('@/views/event/SportEventResultForm.vue'),
                meta: {title: '编辑成绩', roles: ['ADMIN', 'TEACHER'] }
            }
        ]
    },

    // 兜底
    {
        path: '/:pathMatch(.*)*',
        redirect: '/login'
    }
]

const base =
    process.env.NODE_ENV === 'production'
        ? '/admin/'
        : '/'

const router = createRouter({
    history: createWebHistory(base),
    routes
})

router.beforeEach(async (to) => {
    if (to.meta?.title) document.title = String(to.meta.title)
    if (to.meta?.public) return true

    const auth = useAuthStore()

    if (!auth.inited) {
        try {
            await auth.fetchMe()
        } catch (e) {
            auth.user = null
        } finally {
            auth.inited = true
        }
    }

    // 未登录
    if (!auth.user || auth.user.roleCode === 'anonymousUser') {
        return { path: '/login', query: { redirect: to.fullPath } }
    }

    // ✅ 角色控制（单角色 roleCode）
    if (Array.isArray(to.meta?.roles) && to.meta.roles.length) {
        if (!to.meta.roles.includes(auth.user.roleCode)) {
            return { path: '/403' }
        }
    }

    return true
})

export default router