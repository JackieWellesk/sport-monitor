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
                name: 'HomeView',
                component: () => import('@/views/HomeView.vue'),
                meta: { public: false, title: '首页', roles: ['STUDENT'] } // 如果你希望不登录也能看首页，就加 public:true
            },
            {
                path: 'users/profile',
                name: 'UserInfo',
                component: () => import('@/views/user/UserInfo.vue'),
                meta: { title: '个人信息', roles: ['STUDENT'] }
            },
            {
                path: 'events/detail',
                name: 'EventDetail',
                component: () => import('@/views/events/EventDetail.vue'),
                meta: { title: '赛事详情', roles: ['STUDENT'] }
            },
            {
                path: 'records/list',
                name: 'RecordsList',
                component: () => import('@/views/records/RecordsList.vue'),
                meta: { title: '运动记录管理', roles: ['STUDENT'] }
            },
            {
                path: 'events/signups',
                name: 'EventsSignups',
                component: () => import('@/views/events/EventsSignup.vue'),
                meta: { title: '报名管理', roles: ['STUDENT'] }
            },
            {
                path: 'events/results',
                name: 'EventsResults',
                component: () => import('@/views/events/EventsResults.vue'),
                meta: { title: '我的成绩', roles: ['STUDENT'] }
            },
            {
                path: 'reports/health',
                name: 'HealthView',
                component: () => import('@/views/health/ReportsHealth.vue'),
                meta: { title: '健康报告', roles: ['STUDENT'] }
            },
            {
                path: '/records/edit/:id?',
                name: 'SportRecordEdit',
                component: () => import('@/views/records/SportRecordEdit.vue'),
                meta: { title: '新增/修改运动记录', roles: ['STUDENT'] }
            },
            {
                path: '/health-report',
                name: 'HealthReport',
                component: () => import('@/views/health/HealthReport.vue'),
                meta: { title: '健康报告', roles: ['STUDENT'] }
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
        ? '/front/'
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