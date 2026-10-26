import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/store/user'

const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),
    routes: [
        {
            path: '/',
            name: 'home',
            component: () => import('../views/Home.vue')
        },
        {
            path: '/login',
            name: 'login',
            component: () => import('../views/Login.vue')
        },
        {
            path: '/register',
            name: 'register',
            component: () => import('../views/Register.vue')
        },
        {
            path: '/profile',
            name: 'profile',
            component: () => import('../views/Profile.vue')
        },
        {
            path: '/admin',
            name: 'admin',
            component: () => import('../views/admin/Layout.vue'),
            meta: { requiresAdmin: true },
            children: [
                {
                    path: '',
                    name: 'admin-dashboard',
                    component: () => import('../views/admin/Dashboard.vue')
                },
                {
                    path: 'music',
                    name: 'admin-music',
                    component: () => import('../views/admin/MusicManage.vue')
                },
                {
                    path: 'categories',
                    name: 'admin-categories',
                    component: () => import('../views/admin/CategoryManage.vue')
                },
                {
                    path: 'users',
                    name: 'admin-users',
                    component: () => import('../views/admin/UserManage.vue')
                },
                {
                    path: 'banners',
                    name: 'admin-banners',
                    component: () => import('../views/admin/BannerManage.vue')
                },
                {
                    path: 'notices',
                    name: 'admin-notices',
                    component: () => import('../views/admin/NoticeManage.vue')
                },
                {
                    path: 'sensitive',
                    name: 'admin-sensitive',
                    component: () => import('../views/admin/SensitiveWordManage.vue')
                }
            ]
        }
    ]
})

router.beforeEach((to, from) => {
    const userStore = useUserStore()
    if (to.meta.requiresAdmin && !userStore.isAdmin()) {
        return '/login'
    } else {
        return true
    }
})

export default router
