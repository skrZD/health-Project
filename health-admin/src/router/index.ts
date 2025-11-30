import { createRouter, createWebHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'

const routes: RouteRecordRaw[] = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: { requiresAuth: false }
  },
  {
    path: '/',
    name: 'Layout',
    component: () => import('@/components/Layout.vue'),
    meta: { requiresAuth: true },
    children: [
      {
        path: '',
        redirect: '/dashboard'
      },
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/Dashboard.vue'),
        meta: { title: '仪表盘' }
      },
      {
        path: 'users',
        name: 'Users',
        component: () => import('@/views/Users.vue'),
        meta: { title: '用户管理' }
      },
      {
        path: 'foods',
        name: 'Foods',
        component: () => import('@/views/Foods.vue'),
        meta: { title: '食物管理' }
      },
      {
        path: 'diet-records',
        name: 'DietRecords',
        component: () => import('@/views/DietRecords.vue'),
        meta: { title: '饮食记录' }
      },
      {
        path: 'exercise-records',
        name: 'ExerciseRecords',
        component: () => import('@/views/ExerciseRecords.vue'),
        meta: { title: '运动记录' }
      },
      {
        path: 'weight-records',
        name: 'WeightRecords',
        component: () => import('@/views/WeightRecords.vue'),
        meta: { title: '体重记录' }
      },
      {
        path: 'recipes',
        name: 'Recipes',
        component: () => import('@/views/Recipes.vue'),
        meta: { title: '食谱管理' }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('admin_token')
  
  if (to.meta.requiresAuth && !token) {
    next('/login')
  } else if (to.path === '/login' && token) {
    next('/')
  } else {
    next()
  }
})

export default router

