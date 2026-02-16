import { createRouter, createWebHistory } from 'vue-router'
import Layout from '@/layout/Layout.vue'

const routes = [
    {
        path: '/',
        component: Layout,
        redirect: '/home',
        children: [
            { path: 'home', component: () => import('@/views/Home.vue') }
        ]
    }
]

export default createRouter({
    history: createWebHistory(),
    routes
})
