import { defineStore } from 'pinia'
import { ref } from 'vue'
import request from '@/utils/request'

interface AdminInfo {
  id: number
  username: string
  nickname: string
  role: string
}

export const useAdminStore = defineStore('admin', () => {
  const token = ref(localStorage.getItem('admin_token') || '')
  const adminInfo = ref<AdminInfo | null>(
    JSON.parse(localStorage.getItem('admin_info') || 'null')
  )

  async function login(username: string, password: string) {
    const res: any = await request.post('/auth/login', { username, password })
    token.value = res.data.token
    adminInfo.value = res.data.admin
    localStorage.setItem('admin_token', res.data.token)
    localStorage.setItem('admin_info', JSON.stringify(res.data.admin))
    return res
  }

  function logout() {
    token.value = ''
    adminInfo.value = null
    localStorage.removeItem('admin_token')
    localStorage.removeItem('admin_info')
  }

  async function getCurrentAdmin() {
    const res: any = await request.get('/auth/info')
    adminInfo.value = res.data
    localStorage.setItem('admin_info', JSON.stringify(res.data))
    return res
  }

  function isSuper() {
    return adminInfo.value?.role === 'SUPER'
  }

  return {
    token,
    adminInfo,
    login,
    logout,
    getCurrentAdmin,
    isSuper
  }
})
