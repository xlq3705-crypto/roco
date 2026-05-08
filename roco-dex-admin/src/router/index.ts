import { createRouter, createWebHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'

const routes: RouteRecordRaw[] = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/LoginView.vue'),
    meta: { title: '登录' }
  },
  {
    path: '/',
    component: () => import('@/layouts/AdminLayout.vue'),
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/dashboard/DashboardView.vue'),
        meta: { title: '仪表盘' }
      },
      {
        path: 'pet',
        name: 'Pet',
        component: () => import('@/views/pet/PetView.vue'),
        meta: { title: '宠物管理' }
      },
      {
        path: 'skill',
        name: 'Skill',
        component: () => import('@/views/skill/SkillView.vue'),
        meta: { title: '技能管理' }
      },
      {
        path: 'item',
        name: 'Item',
        component: () => import('@/views/item/ItemView.vue'),
        meta: { title: '道具管理' }
      },
      {
        path: 'equipment',
        name: 'Equipment',
        component: () => import('@/views/equipment/EquipmentView.vue'),
        meta: { title: '装备管理' }
      },
      {
        path: 'attribute',
        name: 'Attribute',
        component: () => import('@/views/attribute/AttributeView.vue'),
        meta: { title: '属性克制' }
      },
      {
        path: 'user',
        name: 'User',
        component: () => import('@/views/user/UserView.vue'),
        meta: { title: '用户管理' }
      },
      {
        path: 'admin',
        name: 'Admin',
        component: () => import('@/views/admin/AdminView.vue'),
        meta: { title: '管理员管理', requiresSuper: true }
      },
      {
        path: 'log/login',
        name: 'LoginLog',
        component: () => import('@/views/log/LoginLogView.vue'),
        meta: { title: '登录日志' }
      },
      {
        path: 'log/operation',
        name: 'OperationLog',
        component: () => import('@/views/log/OperationLogView.vue'),
        meta: { title: '操作日志' }
      },
      {
        path: 'import-export',
        name: 'ImportExport',
        component: () => import('@/views/import-export/ImportExportView.vue'),
        meta: { title: '导入导出' }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, _from, next) => {
  const token = localStorage.getItem('admin_token')
  if (to.path !== '/login' && !token) {
    next('/login')
  } else if (to.path === '/login' && token) {
    next('/dashboard')
  } else {
    document.title = `${to.meta.title || ''} - 洛克王国图鉴管理系统`
    next()
  }
})

export default router
